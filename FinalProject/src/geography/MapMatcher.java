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
 * @author Andrew Hansen
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
	                               // The lower this number is the better we are at finding a match
                                 // The higher the number the faster it runs
                                 // If it takes too long to run it is completely useless
                                 // If it can't find a match it is completely useless
	
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
		System.out.println(MIN_Y);
		System.out.println(MIN_X);
		
		
		// Initialize buckets
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
		
		// Populate buckets
		for (Map.Entry<double[], GeographicShape> entry : allCoords.entrySet())
		{
			double[] coord = entry.getKey();
			double coordX = coord[0];
			double coordY = coord[1];
			
			for (int i = 1; i <= GAP; i++)
			{
				double placementX = (RATIO_X * i) + MIN_X;
				if (coordX <= placementX)
				{
					for (int j = 1; j <= GAP; j++)
					{
						double placementY = (RATIO_Y * j) + MIN_Y;
						if (coordY <= placementY)
						{
							buckets.get(placementX)
							       .get(placementY)
							       .add(entry.getValue());
							break;
						}
					}
					break;
				}
			}
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
		  // This looks like O(n^2), is it possible to make this faster?
		  // I'm pretty sure there is a better way of doing this with algebra?
		  // since we want to satisfy the equation x <= MIN_X + (RATIO_X * i)
		  // The only unknown is i, so to solve for i -> (x - MIN_X) / RATIO_X <= i
		  // Then placementX would just == MIN_X + (RATIO_X * i)
		  // The same could be done for placementY
			for (int i = 1; i <= GAP; i++)
			{
				double placementX = MIN_X + (RATIO_X * i);
				if (x <= placementX)
				{
					for (int j = 1; j <= GAP; j++)
					{
						double placementY = MIN_Y + (RATIO_Y * j);
						if (y <= placementY)
						  // you are only returning one cell here, but we really want to search the surrounding cells too
						  // now that you are saving time above, why don't you return multiple cells to check in an order that makes the closest cells first.
						  // Then in mapMatch you could try and if it fails in the first attempt it can try all the cells neighbors
							return buckets.get(placementX).get(placementY);
					}
				}
			}
		}
		
		return null;
	}
	
  // 2 problems, a) this runs too slow, b) you only ever consider the closest bucket.
	public double[] mapMatch(Queue<double[]> curve)
	{
	  LinkedList<GeographicShape> closestShapes = getClosestGeographicShapes(curve.peek());
	  
	  double min = Double.MAX_VALUE;
	  double[] permFirst = null;
	  double[] permLast = null;
	  
	  for (GeographicShape gshape: closestShapes)
	  {
	    
	    Shape s = gshape.getShape();
	    PathIterator pi = s.getPathIterator(null); // Why isn't this identity?
	    
	    double[] first = new double[2];
	    double[] last = new double[2];
	    pi.currentSegment(first);
	    do
	    {
	      pi.currentSegment(last); // I want to here the explanation on this, is it the case that calling current segment on pi twice gives you different results?
	      pi.next(); // If not this next should be before right? Otherwise it might get strange results from this.
	      
	      double result = 0.0;
	      for (double[] point : curve)
	        result += Vector.distancePointToLine(first, last, point);
	      
	      if (result < min)
	      {
	        min = result;
	        // This is not optimal, you should init a 2d array only the first time
	        permFirst = new double[] {first[0], first[1]};
	        permLast = new double[] {last[0], last[1]};
	      }
	      // Same here you shouldn't be making a new array, thats a waste of computation
	      first = new double[] {last[0], last[1]};
	    } while (!pi.isDone());
	  }
	  
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
	
}
