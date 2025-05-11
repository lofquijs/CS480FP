package graph;

import feature.StreetSegment;

/**
 * Interface used to manage labels for the label setting algorithm.
 * 
 * @author Andrew Hansen
 * 
 *         This work complies with the JMU Honor Code.
 */
public interface PermanentLabelManager extends LabelManager {
	/**
	 * Getter for a label that has the minimum value among all non-permanent Label
	 * objects.
	 * 
	 * @return smallest label
	 */
	public abstract Label getSmallestLabel();

	/**
	 * Adjust the head label of the provided street segment based on euclidean
	 * distance.
	 * 
	 * @param segment     the street segment for updating
	 * @param destination the destination street segment
	 */
	public abstract void adjustStarValue(final StreetSegment segment, final StreetSegment destination);

	/**
	 * Setter that makes a specific label permanent.
	 * 
	 * @param intersectionID -> Where specific label is found
	 */
	public abstract void makePermanent(int intersectionID);
}
