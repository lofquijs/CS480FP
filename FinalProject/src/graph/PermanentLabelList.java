package graph;

import feature.StreetSegment;

/**
 * Class used to manage labels using a list data structure for the label setting algorithm.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public class PermanentLabelList extends AbstractLabelManager implements PermanentLabelManager
{

  /**
   * Explicit Constructor.
   * @param networkSize -> How big the network is.
   */
  public PermanentLabelList(final int networkSize)
  {
    super(networkSize);
  }
  
  @Override
  public void adjustHeadValue(final StreetSegment segment)
  {
    Label label = labels[segment.getHead()];
    label.adjustValue(segment.getLength() + labels[segment.getTail()].getValue(), segment);
  }


  @Override
  public Label getSmallestLabel()
  {
    double min = Double.MAX_VALUE;
    Label result = null;
    for (Label label : labels)
    {
      double value = label.getValue();
      if (!label.isPermanent() && min > value)
      {
        min = value;
        result = label;
      }
    }
    return result;
  }

  @Override
  public void makePermanent(final int intersectionID)
  {
    labels[intersectionID].makePermanent();
  }

@Override
public void adjustStarValue(StreetSegment segment, StreetSegment destination) {
	// TODO Auto-generated method stub
	
}

}
