package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import feature.StreetSegment;
import graph.Label;

class TestLabel
{
  StreetSegment ss = new StreetSegment("55734286", "A41", null, 2024, 3342, 000001, 000000, 0.725722);

  @Test
  void TestLabelGettersAndSetters()
  {
    Label label = new Label(321);
    assertEquals(321, label.getID());
    assertEquals(Double.MAX_VALUE, label.getValue());
    assertEquals(null, label.getPredecessor());
    assertFalse(label.isPermanent());
    label.makePermanent();
    assertTrue(label.isPermanent());
    label.setValue(3.15);
    assertEquals(3.15, label.getValue());
  }
  
  @Test
  void TestAdjustValue()
  {
    Label label = new Label();
    assertEquals(Double.MAX_VALUE, label.getValue());
    
    
    label.adjustValue(ss.getLength(), ss);
    
    assertEquals(ss.getLength(), label.getValue());
    assertEquals(ss.getID(), label.getPredecessor().getID());
  }
  
  @Test
  void TestAdjustValueMoreThan()
  {
    Label label = new Label();
    label.setValue(0.2);
    
    label.adjustValue(ss.getLength(), ss);
    
    assertEquals(0.2, label.getValue());
    assertEquals(null, label.getPredecessor());
  }

}
