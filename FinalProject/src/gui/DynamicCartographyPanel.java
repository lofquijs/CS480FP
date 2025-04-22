package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import geography.MapProjection;
import gps.GPGGASentence;
import gps.GPSObserver;

/**
 * Class Used to display and animate a route.
 * @param <T>
 * 
 * @author Andrew Hansen
 * 
 * This code complies with the JMU Honor Code.
 */
public class DynamicCartographyPanel<T> extends CartographyPanel<T> implements GPSObserver
{
  private static final long serialVersionUID = 1L;
  private GPGGASentence gpgga;
  private MapProjection proj;

  /**
   * Explicit Value Constructor.
   * @param model -> Used to keep track of map assets.
   * @param cartographer -> Used to draw the maps.
   * @param proj -> Map Projection used on other Methods.
   */
  public DynamicCartographyPanel(final CartographyDocument<T> model, 
       final Cartographer<T> cartographer, final MapProjection proj)
  {
    super(model, cartographer);
    this.proj = proj;
  }
  
  @Override
  public void handleGPSData(final String data)
  {
    gpgga = GPGGASentence.parseGPGGA(data);
    repaint();
  }
  
  /**
   * Paints current location depending on GPGGA data.
   * @param g -> Graphics
   */
  public void paint(final Graphics g)
  {
    super.paint(g);
    
    if (gpgga == null) return;
    double[] kilo = {gpgga.getLongitude(), gpgga.getLatitude()};
    kilo = proj.forward(kilo);
    Rectangle2D.Double rec = new Rectangle2D.Double(kilo[0] - 1, kilo[1] - 1, 2, 2);
    zoomStack.add(0, rec);
    
    
    super.paint(g);
    
    AffineTransform at = this.displayTransform.getLastTransform();
    Shape s = at.createTransformedShape(rec);
    double centerX = s.getBounds2D().getCenterX();
    double centerY = s.getBounds2D().getCenterY();
    
    Graphics2D g2 = (Graphics2D) g;
    Ellipse2D.Double circle = new Ellipse2D.Double(centerX - 4, centerY - 4, 8, 8);
    g2.setColor(Color.RED);
    g2.fill(circle);
  }
  
}
