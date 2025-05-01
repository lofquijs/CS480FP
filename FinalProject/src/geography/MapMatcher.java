package geography;

import java.util.ArrayList;
import java.util.LinkedList;

// TODO: I know I didn't really do anything, but I am still thinking of how to implement this.
// 		 My general idea is to have this class be called in the GeographicShapesReader method
// 		 read(). 
// 		 Ex: read(MapMatcher mm);
//		 However, I'm having second thoughts on this. How am I going to distinguish which shape
//       goes in what bucket? There isn't a label associated with these things like on the exam.
//		 Using the lat and long would be stupid because they are floats. Perhaps I try using
//		 PathIterator, and then figure something out? Maybe use the Geocoder when doing this work.
//       I don't know. It's 10:30, and I was editing my Korean movie. It's time to start drinking
//		 this HARD Arizona Iced Tea!
public class MapMatcher {
	private ArrayList<LinkedList<GeographicShape>> bucket;
	
	public MapMatcher() {
		
	}
}
