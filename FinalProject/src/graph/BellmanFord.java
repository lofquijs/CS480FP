package graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feature.Intersection;
import feature.StreetSegment;

/**
 * Implementation of the BellmanFord Label setting algorithm for CS480 Final Project.
 * 
 * @author mason puckett
 * @version 1.0
 */
public class BellmanFord extends AbstractShortestPathAlgorithm
{

  private CandidateLabelManager labels;

  /**
   * Constructor for a BellmanFord Label Setting algorithm.
   * 
   * @param labels
   *          candidate label manager to use.
   */
  public BellmanFord(CandidateLabelManager labels)
  {
    super();
    this.labels = labels;
  }

  @Override
  public Map<String, StreetSegment> findPath(int origin, int destination, StreetNetwork net)
  {
    Map<String, StreetSegment> result = new HashMap<String, StreetSegment>();
    labels.getLabel(origin).setValue(0.0);

    int currID = origin;
    processCandidate(currID, net);
    do
    {
      List<Label> candidates = labels.getAllCandidates();
      if (candidates == null)
        break;
      else
      {
        for (Label l : candidates)
        {
          processCandidate(l.getID(), net);
        }
      }
    }
    while (true);
    
    currID = destination;
    while(currID != origin) {
      StreetSegment segment = labels.getLabel(currID).getPredecessor();
      if (segment != null)
      {
        result.put(segment.getID(), segment);
        currID = segment.getTail();
      }
      else
      {
        currID = origin;
      }
    }

    return result;
  }

  /**
   * Helper method to update an id within the street network.
   * 
   * @param id
   *          intersection to update
   * @param net
   *          streetnetwork to use
   */
  private void processCandidate(int id, StreetNetwork net)
  {
    Intersection current = net.getIntersection(id);
    for (StreetSegment segment : current.getOutbound())
    {
      labels.adjustHeadValue(segment);
    }
  }

}
