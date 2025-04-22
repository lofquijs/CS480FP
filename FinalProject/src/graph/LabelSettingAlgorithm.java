package graph;

import feature.*;
import java.util.*;

/**
 * A label-setting algorithm for calculating shortest paths.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class LabelSettingAlgorithm extends AbstractShortestPathAlgorithm 
{
  private PermanentLabelManager labels;

  /**
   * Explicit Value Constructor.
   * 
   * @param labels The LabelManager to use
   */
  public LabelSettingAlgorithm(final PermanentLabelManager labels)
  {
    super();
    this.labels = labels;
  }

  /**
   * Find the shortest path from the given origin Intersection to the given 
   * destination Intersection on the given StreetNetwork.
   * 
   * @param origin
   * @param destination
   * @param net
   * @return The path
   */
  @Override
  public Map<String, StreetSegment> findPath(final int origin, final int destination, 
      final StreetNetwork net)
  {
    Map<String, StreetSegment> result = new HashMap<String, StreetSegment>();
//    List highlightIDs = new ArrayList();
    labels.getLabel(origin).setValue(0.0);
    labels.getLabel(origin).makePermanent();

    int currentID = origin;

    do
    {
      // Adjust the labels of the reachable intersections
      Intersection current = net.getIntersection(currentID);
      for (StreetSegment segment: current.getOutbound())
      {
        labels.adjustHeadValue(segment);
      }

      // Find the intersection to make permanent
      Label smallestLabel = labels.getSmallestLabel();
      currentID = smallestLabel.getID();

      // Notify observers about intermediate results
//      StreetSegment inbound = smallestLabel.getPredecessor();
//      if (inbound != null) highlightIDs.add(inbound.getID());
//      notifyStreetSegmentObservers(highlightIDs);

      labels.makePermanent(currentID);

    } while (currentID != destination);


    currentID = destination;
    while (currentID != origin)
    {
      StreetSegment segment = labels.getLabel(currentID).getPredecessor();
      if (segment != null)
      {
        result.put(segment.getID(), segment);
        //highlightIDs.add(segment.getID());
        currentID = segment.getTail();
      }
      else
      {
        currentID = origin;
      }
    }

    // Display just the path
    //notifyStreetSegmentObservers(highlightIDs);

    return result;
  }
} 
