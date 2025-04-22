package graph;

import feature.StreetSegment;

/**
 * Object that represents a label.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public class Label
{
  private boolean permanent = false;
  private double value = Double.MAX_VALUE;
  private int id;
  private StreetSegment predecessor;
  
  /**
   * Empty Constructor.
   */
  public Label()
  {
    
  }
  
  /**
   * Explicit constructor.
   * @param id -> Integer identification for Label (Head Node)
   */
  public Label(final int id)
  {
    this.id = id;
    
  }
  
  /**
   * Used to adjust the value of a label, if the possible value is less than the current value held.
   * @param possibleValue -> Possible value that can replace the current value
   * @param possiblePredecessor -> Possible predecessor that can replace the current predecessor
   */
  public void adjustValue(final double possibleValue, final StreetSegment possiblePredecessor)
  {
    if (possibleValue < value)
    {
      value = possibleValue;
      predecessor = possiblePredecessor;
    }
  }
  
  /**
   * Getter for ID.
   * @return integer representation for id
   */
  public int getID()
  {
    return id;
  }
  
  /**
   * Getter for predecessor.
   * @return StreetSegment of last label
   */
  public StreetSegment getPredecessor()
  {
    return predecessor;
  }
  
  /**
   * Getter for value.
   * @return Length of path
   */
  public double getValue()
  {
    return value;
  }
  
  /**
   * Check if label is permanent.
   * @return if label is permanent
   */
  public boolean isPermanent()
  {
    return permanent;
  }
  
  /**
   * Makes label permanent.
   */
  public void makePermanent()
  {
    permanent = true;
  }
  
  /**
   * Setter for value.
   * @param value -> Length of path
   */
  public void setValue(final double value)
  {
    this.value = value;
  }
}
