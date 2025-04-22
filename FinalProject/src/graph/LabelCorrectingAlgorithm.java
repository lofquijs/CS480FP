package graph;

import java.util.HashMap;
import java.util.Map;
import feature.Intersection;
import feature.StreetSegment;

/**
 * Algorithm used to find the shortest path using 'label correcting'.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public class LabelCorrectingAlgorithm extends AbstractShortestPathAlgorithm
{
  private CandidateLabelManager labels;

  /**
   * Explicit Constructor.
   * @param labels -> The LabelManager to use
   */
  public LabelCorrectingAlgorithm(final CandidateLabelManager labels)
  {
    super();
    this.labels = labels;
  }
  
  @Override
  public Map<String, StreetSegment> findPath(final int origin, final int destination, 
                                             final StreetNetwork net)
  {
    Map<String, StreetSegment> result = new HashMap<String, StreetSegment>();
    labels.getLabel(origin).setValue(0.0);
    
    int currentID = origin;
    do
    {
      Intersection current = net.getIntersection(currentID);
      for (StreetSegment segment: current.getOutbound())
      {
        labels.adjustHeadValue(segment);
      }
      Label candidate = labels.getCandidateLabel();
      if (candidate == null) break;
      else currentID = candidate.getID();
    } while (true);
    
    currentID = destination;
    while (currentID != origin)
    {
      StreetSegment segment = labels.getLabel(currentID).getPredecessor();
      if (segment != null)
      {
        result.put(segment.getID(), segment);
        currentID = segment.getTail();
      }
      else
      {
        currentID = origin;
      }
    }
    return result;
  }

}
