package graph;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Arrays;
import feature.StreetSegment;
import geography.PiecewiseLinearCurve;

/**
 * PermanentLabelHeap manages labels for intersections in a street network using a heap structure.
 * 
 * @author Jackson Lofquist
 * 
 *         This code complies with the JMU Honor Code.
 */
public class PermanentLabelHeap extends AbstractLabelManager implements PermanentLabelManager
{
  private int d;
  private int[] heap;
  private int[] positions;
  private int heapSize;

  /**
   * Constructs a PermanentLabelHeap with the specified branching factor and network size.
   *
   * @param d
   *          the branching factor of the heap (e.g., 2 for a binary heap)
   * @param networkSize
   *          the total number of intersections in the network
   */
  public PermanentLabelHeap(final int d, final int networkSize)
  {
    super(networkSize);
    this.d = d;
    this.heap = new int[networkSize];
    this.positions = new int[networkSize];
    Arrays.fill(positions, -1);
    this.heapSize = 0;
  }

  /**
   * Adjusts the head label of the provided street segment.
   * 
   * @param segment
   *          the street segment used for updating labels
   */
  @Override
  public void adjustHeadValue(final StreetSegment segment)
  {
    int tailId = segment.getTail();
    int headId = segment.getHead();

    Label tailLabel = getLabel(tailId);
    Label headLabel = getLabel(headId);

    double newValue = tailLabel.getValue() + segment.getLength();

    if (newValue < headLabel.getValue())
    {
      headLabel.adjustValue(newValue, segment);
      if (positions[headId] == -1)
      {
        insert(headId);
      }
      else
      {
        decreaseKey(positions[headId]);
      }
    }
  }

  /**
   * Adjust the head label of the provided street segment based on euclidean distance.
   * 
   * @param segment
   *          the street segment for updating
   */
  public void adjustStarValue(final StreetSegment segment, final StreetSegment destination)
  {
    int headId = segment.getHead();

    Label headLabel = getLabel(headId);
    
    Path2D.Double plc = (Path2D.Double) segment.getGeographicShape().getShape();
    Path2D.Double dest = (Path2D.Double) destination.getGeographicShape().getShape();
    double newValue = getLabel(segment.getTail()).getValue()
        + plc.getCurrentPoint().distance(dest.getCurrentPoint());
    
    if (newValue < headLabel.getValue())
    {
      headLabel.adjustValue(newValue, segment);
      if (positions[headId] == -1)
      {
        insert(headId);
      }
      else
      {
        decreaseKey(positions[headId]);
      }
    }
  }

  /**
   * Returns the non-permanent Label with the smallest key.
   *
   * @return the Label with the smallest value in the heap, or null if the heap is empty
   */
  @Override
  public Label getSmallestLabel()
  {
    if (heapSize == 0)
    {
      return null;
    }
    int minId = heap[0];
    return getLabel(minId);
  }

  /**
   * Marks the label corresponding to the given intersection id as permanent.
   *
   * @param intersectionID
   *          the id of the intersection to mark as permanent
   */
  @Override
  public void makePermanent(final int intersectionID)
  {
    Label label = getLabel(intersectionID);
    label.makePermanent();
    int pos = positions[intersectionID];
    if (pos != -1)
    {
      removeAt(pos);
    }
  }

  /**
   * Retrieves the Label for the given intersection id.
   *
   * @param intersectionId
   *          the identifier for the intersection
   * @return the corresponding Label
   */
  @Override
  public Label getLabel(final int intersectionId)
  {
    if (intersectionId < 0 || intersectionId >= labels.length)
    {
      throw new IllegalArgumentException("Invalid intersection id: " + intersectionId);
    }
    if (labels[intersectionId] == null)
    {
      labels[intersectionId] = new Label(intersectionId);
      insert(intersectionId);
    }
    return labels[intersectionId];
  }

  /**
   * Inserts an intersection id into the heap.
   *
   * @param intersectionId
   *          the id to insert into the heap
   */
  private void insert(final int intersectionId)
  {
    // Insert at the end of the heap array.
    heap[heapSize] = intersectionId;
    positions[intersectionId] = heapSize;
    heapSize++;
    // Restore heap order by bubbling up.
    decreaseKey(heapSize - 1);
  }

  /**
   * Performs the decrease-key operation (bubble up) on the element at index i.
   *
   * @param i
   *          the index within the heap array to adjust
   */
  private void decreaseKey(final int i)
  {
    int current = i;
    while (current > 0)
    {
      int parent = (current - 1) / d;
      int currentId = heap[current];
      int parentId = heap[parent];
      if (getLabel(currentId).getValue() < getLabel(parentId).getValue())
      {
        swap(current, parent);
        current = parent;
      }
      else
      {
        break;
      }
    }
  }

  /**
   * Restores the heap order by bubbling down from index i.
   *
   * @param i
   *          the starting index for heapifying down.
   */
  private void heapify(final int i)
  {
    int smallest = i;
    for (int j = 1; j <= d; j++)
    {
      int child = d * i + j;
      if (child < heapSize)
      {
        if (getLabel(heap[child]).getValue() < getLabel(heap[smallest]).getValue())
        {
          smallest = child;
        }
      }
    }
    if (smallest != i)
    {
      swap(i, smallest);
      heapify(smallest);
    }
  }

  /**
   * Removes the element at the specified position in the heap.
   *
   * @param pos
   *          the index in the heap array to remove.
   */
  private void removeAt(final int pos)
  {
    if (pos < 0 || pos >= heapSize)
    {
      return;
    }
    int removedId = heap[pos];
    swap(pos, heapSize - 1);
    heapSize--;
    positions[removedId] = -1;
    heapify(pos);
    decreaseKey(pos);
  }

  /**
   * Swaps two elements in the heap array and updates their positions.
   *
   * @param i
   *          the first index
   * @param j
   *          the second index
   */
  private void swap(final int i, final int j)
  {
    int temp = heap[i];
    heap[i] = heap[j];
    heap[j] = temp;
    positions[heap[i]] = i;
    positions[heap[j]] = j;
  }
}
