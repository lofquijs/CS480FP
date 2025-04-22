package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import feature.Intersection;
import feature.Street;
import feature.StreetSegment;

/**
 * An object used to hold all intersections.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public class StreetNetwork
{
  private List<Intersection> intersections;
  
  /**
   * Explicit Constructor.
   */
  public StreetNetwork()
  {
    this.intersections = new ArrayList<Intersection>();
  }
  
  /**
   * Add Intersection into the street network.
   * @param index -> Where intersection will be added
   * @param intersection -> Which intersection will be added
   */
  public void addIntersection(final int index, final Intersection intersection)
  {
    intersections.add(index, intersection);
  }
  
  /**
   * Getter for a specific intersection.
   * @param index -> Where intersection is located
   * @return Desired intersection
   */
  public Intersection getIntersection(final int index)
  {
    return intersections.get(index);
  }
  
  /**
   * Getter for size of StreetNetwork.
   * @return Size of this Street Network
   */
  public int size()
  {
    return intersections.size();
  }
  
  /**
   * Static method used to create a street network.
   * @param streets -> Map of Streets that is iterated upon to create a street network
   * @return StreetNetwork based off of given map of Streets
   */
  public static StreetNetwork createStreetNetwork(final Map<String, Street> streets)
  {
    StreetNetwork result = new StreetNetwork();
    for (Map.Entry<String, Street> entry: streets.entrySet())
    {
      Street street = entry.getValue();
      Iterator<StreetSegment> iter = street.getSegments();
      while (iter.hasNext())
      {
        StreetSegment segment = iter.next();
        int head = segment.getHead();
        int tail = segment.getTail();
        
        // Add Inbound
        if (result.size() <= head)
        {
          for (int i = result.size(); i <= head; i++)
          {
            result.addIntersection(i, new Intersection());
          }
        }
        
        Intersection intersection = result.getIntersection(head);
        intersection.addInbound(segment);

        
        // Add Outbound
        if (result.size() <= tail)
        {
          for (int i = result.size(); i <= tail; i++)
          {
            result.addIntersection(i, new Intersection());
          }
        }
        
        intersection = result.getIntersection(tail);
        intersection.addOutbound(segment);
        
      }
    }
    return result;
  }
}
