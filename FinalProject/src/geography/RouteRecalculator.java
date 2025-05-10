package geography;

import java.util.Map;
import graph.StreetNetwork;

/**
 * Class used for checking if the user is on the correct route.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */

  //My idea for this class, is it will be called every second like Map Matcher.
  // Specifically, we will call onRoute(). If it returns false, it should recalculate.
  // However, I have no idea where this should be. I would put it into DynamicCartoPanel.
  // But, DynamicCartoPanel is not implemented into the FPApp yet. This is important 
  // because it needs the Map given from a SSSP algorithm. Therefore, this class is
  // alone and useless.
public class RouteRecalculator 
{

  private Map<String, StreetNetwork> currentRoute;

  /**
   * Explicit Constructor.
   * @param currentRoute -> Map that comes from the shortest path algorithms
   */
  public RouteRecalculator(final Map<String, StreetNetwork> currentRoute) 
  {
    this.currentRoute = currentRoute;
  }
  
  /**
   * Whether or not the user is on the correct route.
   * @param shape -> Current Location
   * @return conditional on if the user is on correct route
   */
  public boolean onRoute(final GeographicShape shape)
  {
    String id = shape.getID();
    return (currentRoute.containsKey(id));
  }

}
