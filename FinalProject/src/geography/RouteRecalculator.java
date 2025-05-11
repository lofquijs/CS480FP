package geography;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import feature.StreetSegment;
import gps.GPSReaderTask;
import gui.CartographyDocument;

/**
 * Class used for checking if the user is on the correct route.
 * 
 * @author Andrew Hansen
 * 
 *         This work complies with the JMU Honor Code.
 */
public class RouteRecalculator
{

  private CartographyDocument<StreetSegment> model;
  private ActionListener al;
  private GPSReaderTask gps;

  /**
   * Explicit Constructor.
   * 
   * @param model
   *          -> Model used to check which StreetSegments are the path.
   * @param al
   *          -> Linked with GUI, used to send action once off route.
   */
  public RouteRecalculator(final CartographyDocument<StreetSegment> model, final ActionListener al,
      final GPSReaderTask gps)
  {
    this.model = model;
    this.al = al;
    this.gps = gps;
  }


  public boolean isRoute()
  {
    return model.highlighted().hasNext();
  }

  /**
   * Checks if we are on route. If not, it sends an action to recalculate route.
   * 
   * @param shape
   *          -> Current Location
   * @throws InterruptedException 
   */
  public void checkRoute(final GeographicShape shape) throws InterruptedException
  {
    String id = shape.getID();

    Iterator<StreetSegment> iter = model.highlighted();
    while (iter.hasNext())
    {
      StreetSegment segment = iter.next();
      if (segment.getID().equalsIgnoreCase(id))
        return; // Early return if on-route
    }
    
    synchronized(gps) {
      try {
        gps.waiter();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    StreetSegment origin = model.getElement(id);
    ActionEvent ae = new ActionEvent(origin, 88, "Calculate");
    al.actionPerformed(ae);
  }

}
