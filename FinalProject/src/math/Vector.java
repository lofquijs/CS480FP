package math;

/**
 * A utility class that can be used to perform operations on 2-D vectors.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @version 1
 */
public class Vector
{
  /**
   * Default Cpnstructor.
   */
  private Vector()
  {
  }

  /**
   * Calculate the dot (inner) product of two vectors.
   * 
   * @param v One vector
   * @param w The other vector
   * @return The dot product
   */
  public static double dot(final double[] v, final double[] w)
  {
    return v[0]*w[0] + v[1]*w[1];
  }
  
  /**
   * Calculate v - w.
   * 
   * @param v One vector
   * @param w The other vector
   * @return v-w
   */
  public static double[] minus(final double[] v, final double[] w)
  {
    double[] result = new double[2];
    result[0] = v[0] - w[0];
    result[1] = v[1] - w[1];

    return result;
  }
  
  /**
   * Calculate the norm of a vector.
   * 
   * @param v The vector
   * @return The norm
   */
  public static double norm(final double[] v)
  {
    return Math.sqrt(v[0]*v[0] + v[1]*v[1]);
  }
  
  /**
   * Normalize a vector.
   * 
   * @param v The vector to normalize
   * @return The normalized vector (i.e., 1/norm(v) * v)
   */
  public static double[] normalize(final double[] v)
  {
    double denom = norm(v);
    return times(1.0/denom, v);
  }
  
  /**
   * Find a vector that is perpendicular to a vector.
   * 
   * @param v The vector
   * @return The vector (-v[1], v[0])
   */
  public static double[] perp(final double[] v)
  {
    return new double[] {-v[1], v[0]};
  }

  /**
   * Calculate v + w.
   * 
   * @param v One vector
   * @param w Another vector
   * @return v + w
   */
  public static double[] plus(final double[] v, final double[] w)
  {
    double[] result = new double[2];
    result[0] = v[0] + w[0];
    result[1] = v[1] + w[1];

    return result;
  }
  
  /**
   * Multiple a scalar and a vector.
   * 
   * @param s The scalar
   * @param v The vector
   * @return The result of the multiplication
   */
  public static double[] times(final double s, final double[] v)
  {
    return new double[] {s*v[0], s*v[1]};
  }
  
  /**
   * Multiple a vector and a scalar.
   * 
   * @param v The vector
   * @param s The scalar
   * @return The result of the multiplication
   */
  public static double[] times(final double[] v, final double s)
  {
    return times(s, v);
  }
  
  /**
   * Finding distance from Point to Line.
   * @param a -> First Line Coordinates
   * @param b -> Second Line Coordinates
   * @param point -> Point Coordinates
   * @return distance from Point to Line
   */
  public static double distancePointToLine(final double[] a, final double[] b, final double[] point)
  {
    // REFERENCE:
    // https://w3.cs.jmu.edu/bernstdh/web/common/lectures/summary_analytic-geometry-2d_computation.php
    double d1, d2, d3;
    boolean perpendicular;
    
    // These points are not along the line!
    // This may be a problem...
    d1 = distancePointToPoint(point, a);
    d2 = distancePointToPoint(point, b);
    d3 = distancePointToPoint(a, b);
    perpendicular = Math.max(d1, d2) < d3;
    
    if (perpendicular)
    {
      // |(a2 − b2)c1 + (b1 − a1)c2 +(a1b2 − b1a2)|
      double numerator = ((a[1] - b[1]) * point[0]) 
                       + ((b[0] - a[0]) * point[1]) 
                       + ((a[0] * b[1]) - (b[0] * a[1]));
      numerator = Math.abs(numerator);
      
      // ||a - b||
      return numerator / d3;  
    }
    
    return Math.min(d1, d2);
  }
  
  /**
   * Finding distance from Point to Point.
   * @param x -> Point 1
   * @param y -> Point 2
   * @return distance from Point to Point
   */
  public static double distancePointToPoint(final double[] x, final double[] y)
  {
    return Math.sqrt(Math.pow((x[0] - y[0]), 2) + Math.pow((x[1] - y[1]), 2));
  }
  
}
