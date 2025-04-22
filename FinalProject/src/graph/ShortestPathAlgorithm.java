package graph;

import java.util.Map;
import feature.StreetSegment;
import feature.StreetSegmentObserver;
import feature.StreetSegmentSubject;

/**
 * Interface used for executing PathFindingWorker.
 * 
 * @author Andrew Hansen
 * 
 * This code complies with the JMU Honor Code.
 */
public interface ShortestPathAlgorithm extends StreetSegmentSubject
{
  /**
   * Populates a map of StreetSegments using the shortest path from origin to destination.
   * @param origin -> ID of the origin node
   * @param destination -> ID of the destination node
   * @param net -> An object that holds all intersections
   * @return map of streetsegments using the shortest path from origin to destination
   */
  public abstract Map<String, StreetSegment> findPath(int origin, 
      int destination, StreetNetwork net);
  
  /**
   * Adds StreetSegmentObserver.
   * @param observer -> Observer to be added.
   */
  public abstract void addStreetSegmentObserver(StreetSegmentObserver observer);
  
  /**
   * Removes StreetSegmentObserver.
   * @param observer -> Observer to be removed.
   */
  public abstract void removeStreetSegmentObserver(StreetSegmentObserver observer);
  
  /**
   * Notifies StreetSegmentObserver.
   * @param observer -> Observer to be notified.
   */
  public abstract void notifyStreetSegmentObserver(StreetSegmentObserver observer);
}
