package graph;

import java.util.ArrayList;
import java.util.List;
import feature.StreetSegment;

/**
 * Class used to manage candidate label using a list.
 * 
 * @author Andrew Hansen
 * 
 *         This work complies with the JMU Honor Code.
 */
public class CandidateLabelList extends AbstractLabelManager implements CandidateLabelManager
{
  public static final String NEWEST = "N";
  public static final String OLDEST = "O";
  private List<Integer> candidates;
  private String policy;

  /**
   * Explicit Constructor.
   * 
   * @param policy
   *          -> Whether this object selects the newest or oldest candidate
   * @param networkSize
   *          -> The size of the network
   */
  public CandidateLabelList(final String policy, final int networkSize)
  {
    super(networkSize);
    this.policy = policy;
    candidates = new ArrayList<Integer>(networkSize);
  }

  @Override
  public void adjustHeadValue(final StreetSegment segment)
  {
    // TODO Auto-generated method stub
    Label label = labels[segment.getHead()];
    double temp = label.getValue();
    label.adjustValue(segment.getLength() + labels[segment.getTail()].getValue(), segment);
    if (temp != label.getValue())
      candidates.add(label.getID());
  }

  @Override
  public Label getLabel(final int intersectionID)
  {
    // TODO Auto-generated method stub
    return labels[intersectionID];
  }

  @Override
  public Label getCandidateLabel()
  {
    if (candidates.isEmpty())
      return null;

    int id;
    if (policy.equals(NEWEST))
      id = candidates.removeLast();
    else
      id = candidates.removeFirst();
    return getLabel(id);
  }

  @Override
  public List<Label> getAllCandidates()
  {
    if (candidates.isEmpty())
      return null;
    
    List<Label> result = new ArrayList<Label>();
    for (Integer i : candidates)
    {
      result.add(getLabel(i));
    }
    candidates.removeAll(candidates);
    
    return result;
  }

}
