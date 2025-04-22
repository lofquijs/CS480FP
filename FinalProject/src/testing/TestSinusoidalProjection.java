package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geography.SinusoidalProjection;

class TestSinusoidalProjection
{
  @Test
  void TestSinusoidalProjectionEquation()
  {
    SinusoidalProjection proj = new SinusoidalProjection();
    double[] origPoint = {-78.959296, 38.487986};
    
    double[] kilometers = proj.forward(origPoint);
    double[] ll = proj.inverse(kilometers);
    
    assertEquals(ll[0], origPoint[0]);
    assertEquals(ll[1], origPoint[1]);
  }

}
