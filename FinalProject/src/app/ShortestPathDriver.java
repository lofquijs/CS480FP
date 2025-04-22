package app;

import geography.*;
import graph.*;
import gui.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import feature.*;

/**
 * A driver for testing shortest path algorithms that does not make
 * use of a SwingWorker.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class ShortestPathDriver 
{

  private static CartographyDocument<StreetSegment> document;
  private static CartographyPanel<StreetSegment> panel;
  
  /**
   * The entry point of the application.
   * 
   * @param args The command-line arguments (which are ignored)
   * @throws IOException If something goes wrong
   */
	public static void main(String[] args) throws IOException
	{ 
	  // TODO SELECT A .geo FILE
    InputStream isgeo = new FileInputStream(new File("rockingham-streets.geo"));
//    InputStream isgeo = new FileInputStream(new File("virginia-streets.geo"));
		AbstractMapProjection proj = new ConicalEqualAreaProjection(-96.0, 37.5, 29.5, 45.5);
		GeographicShapesReader gsReader = new GeographicShapesReader(isgeo, proj);
		CartographyDocument<GeographicShape> geographicShapes = gsReader.read();
		
    // TODO SELECT A .str FILE
    InputStream iss = new FileInputStream(new File("rockingham-streets.str"));
//    InputStream iss = new FileInputStream(new File("virginia-streets.str"));
		StreetsReader sReader = new StreetsReader(iss, geographicShapes);
		Map<String, Street> streets = new HashMap<String, Street>();
		document = sReader.read(streets);

    panel = new CartographyPanel<StreetSegment>(document, new StreetSegmentCartographer());

    StreetNetwork network = StreetNetwork.createStreetNetwork(streets);
    ShortestPathAlgorithm alg;
    
    // TODO CONSTUCT THE ALGORITHM   --   Use a Label Setting Algorithm
//    PermanentLabelManager labels = new PermanentLabelList(network.size());
//    PermanentLabelManager labels = new PermanentLabelBuckets(network.size());
//    PermanentLabelManager labels = new PermanentLabelHeap(5, network.size());
//    alg = new LabelSettingAlgorithm(labels);
    
    // TODO CONSTRUCT THE ALGORITHM   --   Use a LabelCorrecting Algorithm
    CandidateLabelManager labels = new CandidateLabelList(CandidateLabelList.NEWEST, network.size()); 
    alg = new LabelCorrectingAlgorithm(labels);
    
    // TODO CHOOSE AN ORIGIN AND DESTINATION
    Map<String, StreetSegment> path = alg.findPath(251, 300, network); // For rockingham-va
//    Map<String, StreetSegment> path = alg.findPath(45387, 20000, network); // For virginia
    
    // Show the path
    document.setHighlighted(path);
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(600,600);
		f.setContentPane(panel);
		f.setVisible(true);
	}
	
	
}
