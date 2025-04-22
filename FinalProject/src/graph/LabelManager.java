package graph;

import feature.StreetSegment;

/**
 * Interface used to manager labels.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Coode.
 */
public interface LabelManager
{
  /**
   * This method is used to change the head value of a StreetSegment. Doing so, we can save the 
   * predecessor and previous values of labels. This will be helpful for label setting and 
   * correcting. 
   * @param segment -> Contains the head value to be adjusted, and a potential predeccessor
   */
  public abstract void adjustHeadValue(StreetSegment segment);
  
  /**
   * Getter for label.
   * @param intersectionID -> Label ID
   * @return desired label
   */
  public abstract Label getLabel(int intersectionID);
}
