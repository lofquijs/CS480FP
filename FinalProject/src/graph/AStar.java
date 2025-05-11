	package graph;

import java.util.HashMap;
import java.util.Map;

import feature.Intersection;
import feature.StreetSegment;

/**
 * Implementation of the A* Algorithm for label setting based off the Euclidean distance.
 * 
 * @author mason puckett
 * @version 1.0
 */
public class AStar extends AbstractShortestPathAlgorithm
{

  private PermanentLabelManager labels;

  public AStar(PermanentLabelManager labels)
  {
    super();
    this.labels = labels;
  }

  @Override
  public Map<String, StreetSegment> findPath(int origin, int destination, StreetNetwork net)
  {
    Map<String, StreetSegment> result = new HashMap<String, StreetSegment>();
    
    labels.getLabel(origin).setValue(0.0);
    labels.getLabel(origin).makePermanent();
    
    int currID = origin;
    do {
      Intersection current = net.getIntersection(currID);
      for(StreetSegment seg : current.getOutbound()) {
        labels.adjustStarValue(seg, net.getIntersection(destination).getInbound().get(0));
      }
      
      
      Label nextLabel = labels.getSmallestLabel();
      currID = nextLabel.getID();
          
      labels.makePermanent(currID);
    }while(currID != destination);
    
    currID = destination;
    while (currID != origin)
    {
      StreetSegment segment = labels.getLabel(currID).getPredecessor();
      if (segment != null)
      {
        result.put(segment.getID(), segment);
        //highlightIDs.add(segment.getID());
        currID = segment.getTail();
      }
      else
      {
        currID = origin;
      }
    }
    
    return result;
  }

}
