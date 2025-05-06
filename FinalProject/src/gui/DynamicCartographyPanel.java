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
import gps.GPGGASentence;
import gps.GPSObserver;

/**
 * Class Used to display and animate a route.
 * 
 * @param <T>
 * 
 * @author Andrew Hansen
 * 
 *         This code complies with the JMU Honor Code.
 */
public class DynamicCartographyPanel<T> extends CartographyPanel<T> implements GPSObserver
{
  private static final long serialVersionUID = 1L;
  private GPGGASentence gpgga;
  private MapProjection proj;
  private MapMatcher mm;
  private Queue<double[]> currentCurve;

  // Used to reduce object creation.
  private double[] ll, km;
  private Point2D.Double pointKM, pointXY;

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
  public DynamicCartographyPanel(final CartographyDocument<T> model,
      final Cartographer<T> cartographer, final MapProjection proj, final MapMatcher mm)
  {
    super(model, cartographer);
    this.proj = proj;

    ll = new double[2];
    pointKM = new Point2D.Double();
    pointXY = new Point2D.Double();

    this.mm = mm;
    this.currentCurve = new LinkedList<>();
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

      km = proj.forward(ll);

      // Curve-to-curve matching! Somehow we must compare the arc from previousPath
      // to GeographicShapes return from getClosestGeographicShapes. How we do this?
      // I dunno. However, I would think you would use the path iterator on GeographicShapes.
      // Using those double[] from path iterator, we can compare them to the double[]
      // in previousPath. How we decide what point to be compared to what. I have no
      // utter idea. If anyone sees this, and has an idea, let me know!
      // Reference:
      // https://w3.cs.jmu.edu/bernstdh/web/common/lectures/summary_map-matching_introduction.php

      // Saves last 3 kms to compare curve
      currentCurve.add(km);
      if (currentCurve.size() > 3)
        currentCurve.remove();

      // km = mm.mapMatch(currentCurve);

      bounds = new Rectangle2D.Double(km[0] - 1.0, km[1] - 1.0, 2.0, 2.0);
      zoomStack.addFirst(bounds);
    }

    // Here I will need to pass the location of the user to super.paint() somehow: Dakota
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
