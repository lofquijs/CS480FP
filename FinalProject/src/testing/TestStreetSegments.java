package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import feature.StreetSegment;
import geography.PiecewiseLinearCurve;

class TestStreetSegments
{

  @Test
  void TestStreetSegmentConstructor()
  {
    StreetSegment ss = new StreetSegment("55734286", "A41", new PiecewiseLinearCurve("123"), 2024, 3342, 000001, 000000, 0.725722);
    assertEquals("55734286", ss.getID());
    assertEquals("123", ss.getGeographicShape().getID());
  }

}
