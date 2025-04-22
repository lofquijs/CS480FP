package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import feature.Street;
import feature.StreetSegment;
import geography.PiecewiseLinearCurve;

class TestStreet
{
  StreetSegment ss = new StreetSegment("55734286", "A41", new PiecewiseLinearCurve("123"), 2024, 3342, 000001, 000000, 0.725722);
  List<StreetSegment> ssList = new LinkedList<StreetSegment>();

  @Test
  void TestCanonicalName()
  {
    String test = Street.createCanonicalName("N", "Lee", "St", null);
    assertEquals("N Lee St", test);
    
    test = Street.createCanonicalName(null, "Quail Roost", "Rd", null);
    assertEquals("Quail Roost Rd", test);
    
    test = Street.createCanonicalName(null, "Domain", "Blvd", "N");
    assertEquals("Domain Blvd N", test);
    
    test = Street.createCanonicalName(null, null, null, null);
    assertEquals(null, test);
  }
  

}
