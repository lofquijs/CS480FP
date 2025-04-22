package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geography.AbstractMapProjection;
import geography.ConicalEqualAreaProjection;

class TestConcialEqualAreaProjection
{

  @Test
  void TestConcialEqualAreaProjectionEquation()
  {
    AbstractMapProjection proj = new ConicalEqualAreaProjection(-96.0, 37.5, 29.5, 45.5);
    double[] origPoint = {-78.959296, 38.487986};
    
    double[] kilometers = proj.forward(origPoint);
    double[] ll = proj.inverse(kilometers);
    
    assertEquals(ll[0], origPoint[0]);
    assertEquals(ll[1], origPoint[1]);
  }

}
