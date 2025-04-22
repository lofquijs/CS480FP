package graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import feature.StreetSegment;
import feature.StreetSegmentObserver;

/**
 * An abstraction for the shortest path algorithm. This is used for both label setting and 
 * correcting.
 * 
 * @author Andrew Hansen
 * 
 * This complies with the JMU Honor Code.
 */
public abstract class AbstractShortestPathAlgorithm implements ShortestPathAlgorithm
{
  private Collection<StreetSegmentObserver> observers;
  
  /**
   * Explicit Constructor.
   */
  public AbstractShortestPathAlgorithm()
  {
    observers = new LinkedList<StreetSegmentObserver>();
  }
  
  @Override
  public abstract Map<String, StreetSegment> findPath(final int origin, 
      final int destination, final StreetNetwork net);

  @Override
  public void addStreetSegmentObserver(final StreetSegmentObserver observer)
  {
    observers.add(observer);
  }
  
  @Override
  public void removeStreetSegmentObserver(final StreetSegmentObserver observer)
  {
    observers.remove(observer);
  }
  
  @Override
  public void notifyStreetSegmentObserver(final StreetSegmentObserver observer)
  {
    // NOT IMPLEMENTED!
  }
  
}
