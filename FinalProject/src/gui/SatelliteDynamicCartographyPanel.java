package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Queue;
import geography.GeographicShape;
import geography.MapMatcher;
import geography.MapProjection;
import geography.RouteRecalculator;
import gps.GPGGASentence;
import gps.GPSObserver;

/**
 * Class Used to display and animate a route.
 * 
 * @param <T>
 * 
 * @author Andrew Hansen and Dakota Lawson
 * 
 *         This code complies with the JMU Honor Code.
 */
public class SatelliteDynamicCartographyPanel<T> extends SatelliteCartographyPanel<T>
    implements GPSObserver
{
  private static final long serialVersionUID = 1L;
  private GPGGASentence gpgga;
  private MapProjection proj;
  private MapMatcher mm;
  private RouteRecalculator rr;
  private Queue<double[]> currentPath;

  // Used to reduce object creation.
  private double[] ll, km;
  private Point2D.Double pointKM, pointXY;
  private int counter = 0;

  /**
   * Explicit Value Constructor.
   * 
   * @param model
   *          -> Used to keep track of map assets.
   * @param cartographer
   *          -> Used to draw the maps.
   * @param proj
   *          -> Map Projection used on other Methods.
   */
  public SatelliteDynamicCartographyPanel(final CartographyDocument<T> model,
      final Cartographer<T> cartographer, final MapProjection proj, final MapMatcher mm,
      final RouteRecalculator rr)
  {
    super(model, cartographer, proj);
    this.proj = proj;

    ll = new double[2];
    pointKM = new Point2D.Double();
    pointXY = new Point2D.Double();

    this.mm = mm;
    this.rr = rr;
    this.currentPath = new LinkedList<>();
  }

  @Override
  public void handleGPSData(final String data)
  {
    gpgga = GPGGASentence.parseGPGGA(data);
    if (gpgga != null)
    {
      repaint();
    }
  }

  /**
   * Render this component.
   * 
   * @param g
   *          The rendering engine to use
   */
  @Override
  public void paint(final Graphics g)
  {
    Rectangle2D.Double bounds = null;
    LinkedList<GeographicShape> nearestShapes = null;

    if (gpgga != null)
    {
      ll[0] = gpgga.getLongitude();
      ll[1] = gpgga.getLatitude();

      // System.out.printf("GPS Location: %.6f, %.6f%n", ll[0], ll[1]);

      km = proj.forward(ll);

      // From what Dr. Bernstein has said, we must split up the points
      // Ex1:
      /*
       * | |---• | | CORRECT! |---• | | |---• |
       */

      // Ex2:
      /*
       * | |---• |---• |---• WRONG! |---• |---• |---• |---• |
       */

      // I'm trying to do this by using a counter to add points late
      if (counter % 20 == 0)
      {
        // System.out.println(counter);
        currentPath.add(km);
      }
      if (currentPath.size() > 5)
        currentPath.remove();

      double[] p = mm.mapMatch(currentPath);
      if (p != null)
        km = p;
      else
        System.out.println("Map Match failed");

      // Route Recalculation
      if ((rr != null) && rr.isRoute())
      {
        GeographicShape currentLocation = mm.getCurrentLocation();
        try
        {
          rr.checkRoute(currentLocation);
        }
        catch (InterruptedException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      bounds = new Rectangle2D.Double(km[0] - 1.0, km[1] - 1.0, 2.0, 2.0);
      zoomStack.addFirst(bounds);
      counter++;
    }

    // Here I will need to pass the location of the user to super.paint() somehow: Dakota
    setGPSLocation(km);
    super.paint(g);

    if (gpgga != null)
    {
      Graphics2D g2 = (Graphics2D) g;
      Rectangle screenBounds = g2.getClipBounds();
      AffineTransform at = displayTransform.getTransform(screenBounds, bounds);
      pointKM.setLocation(km[0], km[1]);
      at.transform(pointKM, pointXY);
      Ellipse2D.Double point = new Ellipse2D.Double(pointXY.x - 4.0, pointXY.y - 4.00, 8.0, 8.0);
      g2.setPaint(Color.RED);
      g2.fill(point);
      g2.draw(point);

    }

  }
}
