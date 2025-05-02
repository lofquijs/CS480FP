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
      final Cartographer<T> cartographer, final MapProjection proj)
  {
    super(model, cartographer);
    this.proj = proj;

    ll = new double[2];
    pointKM = new Point2D.Double();
    pointXY = new Point2D.Double();
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

    if (gpgga != null)
    {
      ll[0] = gpgga.getLongitude();
      ll[1] = gpgga.getLatitude();

      km = proj.forward(ll);
      bounds = new Rectangle2D.Double(km[0] - 1.0, km[1] - 1.0, 2.0, 2.0);
      zoomStack.addFirst(bounds);
    }

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
