package geography;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import gui.CartographyDocument;
import math.Vector;

/**
 * Class used for map matching.
 * 
 * @author Andrew Hansen, Dakota Lawson
 * 
 * 
 * This work complies with the JMU Honor Code.
 */
public class MapMatcher 
{
 // These values are used to create the buckets. I hate global variables 
 // as much as the next guy, but it is what it is.
  private static double MAX_X = Double.MIN_VALUE;
  private static double MAX_Y = Double.MIN_VALUE; 
	private static double MIN_X = Double.MAX_VALUE;
	private static double MIN_Y = Double.MAX_VALUE;
	private static double RATIO_X;
	private static double RATIO_Y;
	private static int GAP = 700;  // How big the grid is (i.e. 10 x 10)
	                               // We can experiment with this value.
	                               // The lower this number is the better we are at finding a 
                                 // match the higher the number the faster it runs
                                 // If it takes too long to run it is completely useless
                                 // If it can't find a match it is completely useless
	
	 // After running mapMatch() this value will be populated
  private GeographicShape currentLocation;
	
	// This is the buckets itself. It is quite confusing, but all you need to know is that
	// it is a HashMap inside of another HashMap. You use the RATIO values to access it.
	// This comes from instruction of Dr. Bernstein. This is basically a 2D Array (Grid).
	// Is there a better way of doing this? Probably. Is it done? Hopefully.
	private HashMap<Double, HashMap<Double, 
	    LinkedList<GeographicShape>>> buckets = new HashMap<>();
	
	
	/**
	 * Creates the MapMatcher object and populates the buckets.
	 * @param document -> Document that contains all geographic shapes.
	 */
	public MapMatcher(final CartographyDocument<GeographicShape> document)
	{
	  // Used to compare km values with future values and have the 
	  // ability to access the geographic shape.
		HashMap<double[], GeographicShape> allCoords = new HashMap<>();
		
		// Very similar to Geocode class implementation by Dr. Bernstein
		Iterator<GeographicShape> iter = document.iterator();
		AffineTransform identity = new AffineTransform();
		
		// Iterating through the paths to find the points. This is also making a Map from 
		// points to their original geographic shapes. This is helpful for populating the
		// buckets. This loop also finds the minimum points (where the grid starts or the
		// top left of the grid) and maximum points (similar to minimum points).
		while (iter.hasNext())
		{
			GeographicShape gshape = iter.next();
			Shape s = gshape.getShape();
			PathIterator pi = s.getPathIterator(identity);
			while(!pi.isDone())
			{
				double[] coords = new double[2];
				pi.currentSegment(coords);
				allCoords.put(coords, gshape);
				if (MAX_X < coords[0]) MAX_X = coords[0];
				if (MAX_Y < coords[1]) MAX_Y = coords[1];
				if (MIN_X > coords[0]) MIN_X = coords[0];
				if (MIN_Y > coords[1]) MIN_Y = coords[1];
				pi.next();
			}
		}
		
		RATIO_X = (MAX_X - MIN_X) / GAP;
		RATIO_Y = (MAX_Y - MIN_Y) / GAP;
		
		initalizeBuckets();
		populateBuckets(allCoords);
	}
	
	// Helper method used to return placement doubles for accessing the bucket
	private double[] getPlacements(final double x, final double y)
	{
	  double i, j, placementX, placementY;
	  
	  if (x == MIN_X)
      placementX = MIN_X + RATIO_X;
    else
    {
      i = (x - MIN_X) / RATIO_X;
      placementX = MIN_X + (RATIO_X * Math.ceil(i));
    }
    
    if (y == MIN_Y)
      placementY = MIN_Y + RATIO_Y;
    else
    {
      j = (y - MIN_Y) / RATIO_Y;
      placementY = MIN_Y + (RATIO_Y * Math.ceil(j));
    }
    
    return new double[] {placementX, placementY};
	}
	
	// Helper method to initialize the buckets with empty Maps/Lists
	private void initalizeBuckets()
	{
	  for (int i = 1; i <= GAP; i++)
    {
      double x = MIN_X + (i * RATIO_X);
      buckets.put(x, new HashMap<>());
      for (int j = 1; j <= GAP; j++)
      {
        double y = MIN_Y + (j * RATIO_Y);
        buckets.get(x).put(y, new LinkedList<>());
      }
    }
	}
	
	// Helper method to populate the buckets with the correct geographic shapes based on 
	// their coordinates.
	private void populateBuckets(final Map<double[], GeographicShape> allCoords)
	{
	  for (Map.Entry<double[], GeographicShape> entry : allCoords.entrySet())
	  {
	    double[] coord = entry.getKey();
	    double x = coord[0];
	    double y = coord[1];
	    
	    double[] placement;
	    placement = getPlacements(x, y);
      buckets.get(placement[0]).get(placement[1]).add(entry.getValue());
	  }
	}
	
	/**
	 * Finds the closest Geographic Shapes to the current location.
	 * @param km -> Current location in kilometers
	 * @return Geographic Shapes nearest desired km (null if param is not in grid)
	 */
	public LinkedList<GeographicShape> getClosestGeographicShapes(final double[] km)
	{
		double x = km[0];
		double y = km[1];
		
		if (y <= MAX_Y && y >= MIN_Y && x <= MAX_X && x >= MIN_X)
		{
      
		  double[] placement;
      placement = getPlacements(x, y);
      
      return buckets.get(placement[0]).get(placement[1]);
		}
		
		return null;
	}
	
	/**
	 * Finds the correct placement of a point relative to the buckets.
	 * @param curve -> A queue of points that creates a curve to be compared to other shapes
	 * @return the correct coordinates, relative to the buckets, in kilometers
	 */
	public double[] mapMatch(final Queue<double[]> curve)
	{
	  LinkedList<GeographicShape> closestShapes = getClosestGeographicShapes(curve.peek());
	  
	  AffineTransform identity = new AffineTransform();
	  double min = Double.MAX_VALUE;
	  double[] permFirst = null;
	  double[] permLast = null;
	  
	  for (GeographicShape gshape: closestShapes)
	  {
	    
	    Shape s = gshape.getShape();
	    PathIterator pi = s.getPathIterator(identity);
	    
	    double[] first = new double[2];
	    double[] last = new double[2];
	    pi.currentSegment(first);
	    do
	    {
	      pi.currentSegment(last);
	      pi.next();
	      
	      double result = 0.0;
	      for (double[] point : curve)
	        result += Vector.distancePointToLine(first, last, point);
	      
	      if (result < min)
	      {
	        min = result;
	        // This is not optimal, you should init a 2d array only the first time
	        permFirst = new double[] {first[0], first[1]};
	        permLast = new double[] {last[0], last[1]};
	        currentLocation = gshape;
	      }
	      // Same here you shouldn't be making a new array, thats a waste of computation
	      first = new double[] {last[0], last[1]};
	    } while (!pi.isDone());
	  }
	  
	 // REFERENCE:
   // https://w3.cs.jmu.edu/bernstdh/web/common/lectures/slides_analytic-geometry-2d_computation.php
	  
	  /**
	   *             
	   *             |     / 
	   *             |    0a
	   *             |   /
	   *             |  0p <---- b
	   *             | /
	   *             |/
	   * ------------0---------------
	   *             | 
	   *             | 
	   *             |
	   *             |
	   *             |
	   *             |
	   */
	  
	  // lambda = (a * b) / (a * a)
	  // p = lambda * a
	  if (permFirst != null && permLast != null)
	  {
	    double lambda = Vector.dot(permFirst, curve.peek()) / Vector.dot(permFirst, permFirst);
	    double[] p = Vector.times(lambda, permFirst);
	    return p;
	  }
	  
	  return null;
	}
	
	/**
	 * Getter for Current Location (relative to buckets).
	 * @return current location
	 */
	public GeographicShape getCurrentLocation()
	{
	  return currentLocation;
	}
	
}
