package graph;

/**
 * Interface used to manage labels for the label setting algorithm.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public interface PermanentLabelManager extends LabelManager
{
  /**
   * Getter for a label that has the minimum value among all non-permanent Label objects.
   * @return smallest label
   */
  public abstract Label getSmallestLabel();
  
  /**
   * Setter that makes a specific label permanent.
   * @param intersectionID -> Where specific label is found
   */
  public abstract void makePermanent(int intersectionID);
}
