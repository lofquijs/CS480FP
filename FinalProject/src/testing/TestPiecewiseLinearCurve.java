package testing;

import static org.junit.jupiter.api.Assertions.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.junit.jupiter.api.Test;

import geography.PiecewiseLinearCurve;

class TestPiecewiseLinearCurve
{
  PiecewiseLinearCurve plc = new PiecewiseLinearCurve("123");
  Rectangle2D rec = new Rectangle2D.Double(0.0, -5.0, 2.0, 2.0);
  Path2D.Double path = new Path2D.Double(rec);
  PiecewiseLinearCurve plcOverload = new PiecewiseLinearCurve("321", path);
  
  @Test
  void TestPiecewiseLinearCurveGetID()
  {
    assertEquals(plc.getID(), "123");
    assertEquals(plcOverload.getID(), "321");
  }
  
  @Test
  void TestPiecewiseLinearCurveAdd()
  {
    double[] point = {0.0, 0.0};
    plc.add(point);
    Path2D.Double shape = (Path2D.Double) plc.getShape();
    Point2D currentPoint = shape.getCurrentPoint();
    assertEquals(currentPoint.getX(), 0.0);
    assertEquals(currentPoint.getY(), 0.0);
    
    double[] point1 = {1.0, 3.0};
    plc.add(point1);
    double[] point2 = {5.0, 5.0};
    plc.add(point2);
    
    // Start Point
    assertFalse(shape.contains(0.0, 0.0));
    // Mid Point
    assertTrue(shape.contains(1.0, 3.0));
    // End Point
    assertFalse(shape.contains(5.0, 5.0));
    
    // Not in Curve:
    assertFalse(shape.contains(2.0, 4.0));
    
    // Inside the Curve (Desmos was a handy tool):
    assertTrue(shape.contains(0.004, 0.012));
  }
  
  @Test
  void TestPiecewiseLinearCurveAppendTrue()
  {
    plc.append(rec, true);
    assertTrue(plc.getShape().contains(rec));
  }
  
// TODO -> I am not completely sure what "append(shape, false)" is suppose to do.
// How is it not connected to the shape?
//  @Test
//  void TestPiecewiseLinearCurveAppendFalse()
//  {
//    double[] point = {1.0, 3.0};
//    plc.add(point);
//    plc.append(rec, false);
//    Path2D.Double plcPath = (Path2D.Double) plc.getShape();
//    assertFalse(plcPath.contains(rec));
//  }
  
  @Test
  void TestPiecewiseLinearCurveGetShape()
  {
    assertEquals(plcOverload.getShape(), path);
  }

}
