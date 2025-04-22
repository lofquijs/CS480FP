package graph;

/**
 * Interfaced used for the CandidateLabelList Class.
 * 
 * @author Andrew Hansen
 * 
 * This code complies with the JMU Honor Code.
 */
public interface CandidateLabelManager extends LabelManager
{
  /**
   * Method used to get an appropriate candidate depending on policy.
   * @return appropriate candidate label
   */
  public abstract Label getCandidateLabel();
}
