package graph;

import feature.StreetSegment;

/**
 * This class is an abstraction for managing labels. This is used for both label correcting and 
 * setting.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public abstract class AbstractLabelManager implements LabelManager
{
  protected Label[] labels;
  
  /**
   * Explicit Constructor.
   * @param networkSize -> How big the network is, and how many labels need to be created. 
   */
  public AbstractLabelManager(final int networkSize)
  {
    labels = new Label[networkSize];
    for (int i = 0; i < networkSize; i++)
    {
      labels[i] = new Label(i);
    }
  }

  @Override
  public abstract void adjustHeadValue(StreetSegment segment);

  @Override
  public Label getLabel(final int intersectionID)
  {
    // TODO Auto-generated method stub
    if (labels.length >= intersectionID)
      return labels[intersectionID];
    else return null;
  }

}
