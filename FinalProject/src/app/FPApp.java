package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;

import com.fazecast.jSerialComm.SerialPort;

import dataprocessing.Geocoder;
import feature.Street;
import feature.StreetSegment;
import feature.StreetSegmentObserver;
import feature.StreetsReader;
import geography.AbstractMapProjection;
import geography.ConicalEqualAreaProjection;
import geography.GeographicShape;
import geography.GeographicShapesReader;
import gps.GPGGASentence;
import gps.GPSObserver;
import gps.GPSReaderTask;
import gps.GPSSimulator;
import graph.*;
import graph.PathFindingWorker;
import graph.ShortestPathAlgorithm;
import graph.StreetNetwork;
import gui.BackgroundTaskDialog;
import gui.CartographyDocument;
import gui.CartographyPanel;
import gui.DynamicCartographyPanel;
import gui.GeocodeDialog;
import gui.StreetSegmentCartographer;
import geography.MapMatcher;
import geography.RouteRecalculator;

/**
 * The application for the final project of Personal Navigation Systems.
 * 
 * @author Mason Puckett, Andrew Hansen, Jackson Lofquist, Dakota Lawson, Prof.
 *         David Bernstein
 * @version 1.0
 */
public class FPApp implements ActionListener, Runnable, StreetSegmentObserver, PropertyChangeListener {
	private static final int SET_DESTINATION = 0;
	private static final int SET_ORIGIN = 1;
	private static final int CALCULATE_PATH = 2;

	private static final String CALCULATE = "Calculate";
	private static final String EXIT = "Exit";
	private static final String DESTINATION = "Destination";
	private static final String ORIGIN = "Origin";
	private static final String ASTAR = "A*";
	private static final String BELLMANFORD = "BellmanFord";
	private static final String DIJKSTRA = "Dijkstra";
	private static final String CORRECTING = "Correcting";
	private static final String HEAP = "Heap";
	private static final String LIST = "List";
	private static final String NEWEST = "Newest";
	private static final String OLDEST = "Oldest";

	private static final String TAB = "\t";

	private CartographyPanel<StreetSegment> panel;
	private DynamicCartographyPanel<StreetSegment> dynamicPanel;
	private CartographyDocument<StreetSegment> document;
	private GeocodeDialog dialog;
	private int mode;
	private JFrame frame;
	private ShortestPathAlgorithm alg;
	private PermanentLabelManager pLabels;
	private CandidateLabelManager cLabels;
	private PathFindingWorker task;
	private StreetSegment originSegment, destinationSegment;
	private StreetNetwork network;
	private JTextArea textArea;

	/**
	 * Default constructor.
	 */
	public FPApp() {
		textArea = new JTextArea();
		// nope
	}

	@Override
	public void run() {
		try {
			// CHOOSE A .geo FILE
			InputStream isgeo = new FileInputStream(new File("rockingham-streets.geo"));
			// InputStream isgeo = new FileInputStream(new File("virginia-streets.geo"));
			AbstractMapProjection proj = new ConicalEqualAreaProjection(-96.0, 37.5, 29.5, 45.5);
			GeographicShapesReader gsReader = new GeographicShapesReader(isgeo, proj);
			CartographyDocument<GeographicShape> geographicShapes = gsReader.read();
			System.out.println("Read the .geo file");

			// CHOOSE A .str FILE
			InputStream iss = new FileInputStream(new File("rockingham-streets.str"));
			// InputStream iss = new FileInputStream(new File("virginia-streets.str"));
			StreetsReader sReader = new StreetsReader(iss, geographicShapes);
			Map<String, Street> streets = new HashMap<String, Street>();
			document = sReader.read(streets);
			network = StreetNetwork.createStreetNetwork(streets);
			System.out.println("Read the .str file");
			

			panel = new CartographyPanel<StreetSegment>(document, new StreetSegmentCartographer());
			MapMatcher mm = new MapMatcher(geographicShapes);
			RouteRecalculator routeRecalculator = new RouteRecalculator(document, this);
			
			dynamicPanel = new DynamicCartographyPanel<StreetSegment>(document,
			new StreetSegmentCartographer(), proj, mm, routeRecalculator);
			
			frame = new JFrame("Map");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(600, 600);


			JMenuBar menuBar = new JMenuBar();
			frame.setJMenuBar(menuBar);
			JMenuItem item;
			JMenu menu;

			menu = new JMenu("File");
			menuBar.add(menu);

			item = new JMenuItem(EXIT);
			item.addActionListener(this);
			menu.add(item);

			menu = new JMenu("Geocode");
			menuBar.add(menu);

			item = new JMenuItem(ORIGIN);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(DESTINATION);
			item.addActionListener(this);
			menu.add(item);

			menu = new JMenu("Path");
			menuBar.add(menu);

			item = new JMenuItem(CALCULATE);
			item.addActionListener(this);
			menu.add(item);

			menu = new JMenu("Algorithm");
			menuBar.add(menu);

			item = new JMenuItem(ASTAR);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(BELLMANFORD);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(DIJKSTRA);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(CORRECTING);
			item.addActionListener(this);
			menu.add(item);

			menu = new JMenu("Permanent Structure");
			menuBar.add(menu);

			item = new JMenuItem(HEAP);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(LIST);
			item.addActionListener(this);
			menu.add(item);

			menu = new JMenu("Candidate Policy");
			menuBar.add(menu);

			item = new JMenuItem(NEWEST);
			item.addActionListener(this);
			menu.add(item);
			item = new JMenuItem(OLDEST);
			item.addActionListener(this);
			menu.add(item);

			frame.setContentPane(dynamicPanel);
			frame.setVisible(true);

			Geocoder geocoder = new Geocoder(geographicShapes, document, streets);
			dialog = new GeocodeDialog(frame, geocoder);
			dialog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			dialog.addStreetSegmentObserver(this);
			dialog.setLocation((int) frame.getBounds().getMaxX(), (int) frame.getBounds().getY());

//      GPSSimulator gps = new GPSSimulator("rockingham.gps");
//      InputStream is = gps.getInputStream();

			SerialPort[] ports = SerialPort.getCommPorts();
	     String gpsPath = null;
	     for (SerialPort port:ports)
	     {
	       String description = port.getPortDescription();
	       String path = port.getSystemPortPath();
	       if (description.indexOf("GPS") >= 0) gpsPath = path;
	     }
	   
	     // Setup the serial port
	     SerialPort gps = SerialPort.getCommPort(gpsPath); 
	     gps.openPort();
	     gps.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
	     InputStream is = gps.getInputStream();
			
			// Setup the GPSReaderTask
      GPSReaderTask gpsReader = new GPSReaderTask(is, "GPGGA");
      gpsReader.addGPSObserver(dynamicPanel);
			frame.setVisible(true);
      gpsReader.execute();

			frame.setVisible(true);
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(frame, ioe.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("progress")) {
			System.out.println("progress...");
		} else if (evt.getPropertyName().equals("state")) {
			if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
				try {
					Map<String, StreetSegment> path = task.get();
					document.setHighlighted(path);
					panel.repaint();
					task = null;
				} catch (InterruptedException | ExecutionException e) {
					JOptionPane.showMessageDialog(frame, "Interrupted", "Exception", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void handleStreetSegments(final List<String> segmentIDs) {
		HashMap<String, StreetSegment> highlighted = new HashMap<String, StreetSegment>();
		for (String id : segmentIDs) {
			highlighted.put(id, document.getElement(id));
		}
		document.setHighlighted(highlighted);
		panel.repaint();

		if (segmentIDs.size() > 0) {
			if (mode == SET_ORIGIN) {
				dialog.setVisible(true);
				originSegment = highlighted.get(segmentIDs.get(0));
				System.out.println(originSegment + TAB + destinationSegment);
			} else if (mode == SET_DESTINATION) {
				dialog.setVisible(true);
				destinationSegment = highlighted.get(segmentIDs.get(0));
				System.out.println(originSegment + TAB + destinationSegment);
			}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		String ac = evt.getActionCommand();

		if (ac.equals(ORIGIN))
			mode = SET_ORIGIN;
		else if (ac.equals(DESTINATION))
			mode = SET_DESTINATION;
		else if (ac.equals(CALCULATE))
			mode = CALCULATE_PATH;

		if (ac.equals(ORIGIN) || ac.equals(DESTINATION)) {
			if (!dialog.isVisible()) {
				dialog.setLocation((int) frame.getBounds().getMaxX(), (int) frame.getBounds().getY());
				dialog.setVisible(true);
			}
		}

		if (ac.equals(HEAP)) {
			pLabels = new PermanentLabelHeap(5, network.size());
			System.out.println("Data Structure set to heap");
		} else if (ac.equals(LIST)) {
			pLabels = new PermanentLabelList(network.size());
			System.out.println("Data Structure set to list");
		}

		if (ac.equals(NEWEST)) {
			cLabels = new CandidateLabelList(CandidateLabelList.NEWEST, network.size());
			System.out.println("Policy set to newest");
		} else if (ac.equals(OLDEST)) {
			cLabels = new CandidateLabelList(CandidateLabelList.OLDEST, network.size());
			System.out.println("Policy set to oldest");
		}

		if (ac.equals(ASTAR)) {
			alg = new AStar(pLabels);
			System.out.println("AStar is the algorithm");
		} else if (ac.equals(BELLMANFORD)) {
			alg = new BellmanFord(cLabels);
			System.out.println("BellmanFord is the algorithm");
		} else if (ac.equals(DIJKSTRA)) {
			alg = new LabelSettingAlgorithm(pLabels);
			System.out.println("Dijkstra is the algorithm");
		} else if (ac.equals(CORRECTING)) {
			alg = new LabelCorrectingAlgorithm(cLabels);
			System.out.println("Label Correcting is the algorithm");
		}

		if (ac.equals(CALCULATE)) {
		  
		  String message = "Calculating...";
		  
		  // Recalculation Flag. Why is its ID 88? Because.
		  if (evt.getID() == 88)
		  { 
		    originSegment = (StreetSegment) evt.getSource();
		    message = "Recalculating!";
		  }

			// Construct the SwingWorker
			task = new PathFindingWorker(alg, originSegment.getHead(), destinationSegment.getHead(), network, document,
					panel);
			task.addPropertyChangeListener(this);
			task.shouldShowIntermediateResults(false); // TODO SET TO true IF YOU WANT TO SEE INTERMEDIATE
														// RESULTS
			dialog.setVisible(false);

			// Construct the dialog box
			BackgroundTaskDialog<Map<String, StreetSegment>, String> btd = new BackgroundTaskDialog<Map<String, StreetSegment>, String>(
					frame, message, task);

			// Execute
			btd.execute();
		}
		

		if (ac.equals(EXIT)) {
			dialog.dispose();
			frame.dispose();
			System.exit(0);
		}
	}

}
