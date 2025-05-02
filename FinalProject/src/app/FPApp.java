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
import gui.GeocodeDialog;
import gui.StreetSegmentCartographer;

/**
 * The application for the final project of Personal Navigation Systems.
 * 
 * @author Mason Puckett, Andrew Hansen, Jackson Lofquist, Dakota Lawson, Prof. David Bernstein
 * @version 1.0
 */
public class FPApp
    implements ActionListener, Runnable, GPSObserver, StreetSegmentObserver, PropertyChangeListener
{
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

  private static final String TAB = "\t";

  private CartographyPanel<StreetSegment> panel;
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
  public FPApp()
  {
	  textArea = new JTextArea();
    // nope
  }

  @Override
  public void run()
  {
    try
    {
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
      System.out.println("Read the .str file");

      panel = new CartographyPanel<StreetSegment>(document, new StreetSegmentCartographer());
      frame = new JFrame("Map");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(600, 600);

      network = StreetNetwork.createStreetNetwork(streets);

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

      frame.setContentPane(panel);
      frame.setVisible(true);

      Geocoder geocoder = new Geocoder(geographicShapes, document, streets);
      dialog = new GeocodeDialog(frame, geocoder);
      dialog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      dialog.addStreetSegmentObserver(this);
      dialog.setLocation((int) frame.getBounds().getMaxX(), (int) frame.getBounds().getY());

      GPSSimulator gps = new GPSSimulator("rockingham.gps");
      InputStream is = gps.getInputStream();

      // Setup the GPSReaderTask
      GPSReaderTask gpsReader = new GPSReaderTask(is, "GPGGA");
      gpsReader.addGPSObserver(this);
      frame.setVisible(true);
      gpsReader.execute();
    }
    catch (IOException ioe)
    {
      JOptionPane.showMessageDialog(frame, ioe.toString(), "Error", JOptionPane.ERROR_MESSAGE);
    }

  }

  @Override
  public void propertyChange(final PropertyChangeEvent evt)
  {
    if (evt.getPropertyName().equals("progress"))
    {
      System.out.println("progress...");
    }
    else if (evt.getPropertyName().equals("state"))
    {
      if (evt.getNewValue().equals(SwingWorker.StateValue.DONE))
      {
        try
        {
          Map<String, StreetSegment> path = task.get();
          document.setHighlighted(path);
          panel.repaint();
          task = null;
        }
        catch (InterruptedException | ExecutionException e)
        {
          JOptionPane.showMessageDialog(frame, "Interrupted", "Exception",
              JOptionPane.ERROR_MESSAGE);
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void handleStreetSegments(final List<String> segmentIDs)
  {
    HashMap<String, StreetSegment> highlighted = new HashMap<String, StreetSegment>();
    for (String id : segmentIDs)
    {
      highlighted.put(id, document.getElement(id));
    }
    document.setHighlighted(highlighted);
    panel.repaint();

    if (segmentIDs.size() > 0)
    {
      if (mode == SET_ORIGIN)
      {
        dialog.setVisible(true);
        originSegment = highlighted.get(segmentIDs.get(0));
        System.out.println(originSegment + TAB + destinationSegment);
      }
      else if (mode == SET_DESTINATION)
      {
        dialog.setVisible(true);
        destinationSegment = highlighted.get(segmentIDs.get(0));
        System.out.println(originSegment + TAB + destinationSegment);
      }
    }
  }

  @Override
  public void actionPerformed(final ActionEvent evt)
  {
    String ac = evt.getActionCommand();

    if (ac.equals(ORIGIN))
      mode = SET_ORIGIN;
    else if (ac.equals(DESTINATION))
      mode = SET_DESTINATION;
    else if (ac.equals(CALCULATE))
      mode = CALCULATE_PATH;

    if (ac.equals(ORIGIN) || ac.equals(DESTINATION))
    {
      if (!dialog.isVisible())
      {
        dialog.setLocation((int) frame.getBounds().getMaxX(), (int) frame.getBounds().getY());
        dialog.setVisible(true);
      }
    }
    
    if(ac.equals(ASTAR)) {
      pLabels = new PermanentLabelHeap(5, network.size());
      alg = new AStar(pLabels);
    }else if(ac.equals(BELLMANFORD)) {
      cLabels = new CandidateLabelList(CandidateLabelList.NEWEST, network.size());
      alg = new BellmanFord(cLabels);
    }else if (ac.equals(DIJKSTRA)) {
      pLabels = new PermanentLabelHeap(5, network.size());
      alg = new LabelSettingAlgorithm(pLabels);
    }else if (ac.equals(CORRECTING)) {
      cLabels = new CandidateLabelList(CandidateLabelList.NEWEST, network.size());
      alg = new LabelCorrectingAlgorithm(cLabels);
    }

    if (ac.equals(CALCULATE))
    {
      
      // Construct the SwingWorker
      task = new PathFindingWorker(alg, originSegment.getHead(), destinationSegment.getHead(),
          network, document, panel);
      task.addPropertyChangeListener(this);
      task.shouldShowIntermediateResults(false); // TODO SET TO true IF YOU WANT TO SEE INTERMEDIATE
                                                 // RESULTS
      dialog.setVisible(false);

      // Construct the dialog box
      BackgroundTaskDialog<Map<String, StreetSegment>, String> btd = new BackgroundTaskDialog<Map<String, StreetSegment>, String>(
          frame, "Calculating...", task);

      // Execute
      btd.execute();
    }

    if (ac.equals(EXIT))
    {
      dialog.dispose();
      frame.dispose();
      System.exit(0);
    }
  }

  @Override
  public void handleGPSData(String sentence)
  {
    GPGGASentence gpgga = GPGGASentence.parseGPGGA(sentence);
    if (gpgga != null)
      textArea.append(String.format("%9.6f, %9.6f", gpgga.getLongitude(), gpgga.getLatitude()));
    else
      textArea.append("Waiting for a fix...");
    textArea.append("\n");
  }

}
