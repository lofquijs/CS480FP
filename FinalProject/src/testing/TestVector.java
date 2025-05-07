package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import math.Vector;

class TestVector
{

  @Test
  void TestDot()
  {
    double[] firstPoint = {2.0, 3.0};
    double[] secondPoint = {4.0, 5.0};
    
    assertEquals(Vector.dot(firstPoint, secondPoint), 23.0);
  }
  
  @Test
  void TestMinus()
  {
    double[] firstPoint = {1.5, 0.5};
    double[] secondPoint = {0.5, 1.0};
    
    double[] resultPoint = Vector.minus(firstPoint, secondPoint);
    assertEquals(resultPoint[0], 1.0);
    assertEquals(resultPoint[1], -0.5);
  }
  
  @Test
  void TestNorm()
  {
    double[] firstPoint = {-1.0, 3.0};
    assertEquals(Vector.norm(firstPoint), Math.sqrt(10.0));
  }
  
  @Test
  void TestNormalize()
  {
    double[] firstPoint = {4.0, -9.0};
    firstPoint = Vector.normalize(firstPoint);
    double[] expectedPoint = {4.0 / Math.sqrt(97.0), -9.0 / Math.sqrt(97.0)};
    assertEquals(firstPoint[0], expectedPoint[0]);
    assertEquals(firstPoint[1], expectedPoint[1]);
  }
  
  @Test
  void TestPerp()
  {
    double[] firstPoint = {3.0, 1.0};
    firstPoint = Vector.perp(firstPoint);
    assertEquals(firstPoint[0], -1.0);
    assertEquals(firstPoint[1], 3.0);
  }
  
  @Test
  void TestPlus()
  {
    double[] firstPoint = {1.5, -0.5};
    double[] secondPoint = {0.5, -1.0};
    
    double[] resultPoint = Vector.plus(firstPoint, secondPoint);
    assertEquals(resultPoint[0], 2.0);
    assertEquals(resultPoint[1], -1.5);
  }
  
  @Test
  void TestTimes()
  {
    double[] firstPoint = {1.0, 2.0};
    double[] secondPoint = {2.0, -4.0};

    double[] resultPoint = Vector.times(2, firstPoint);
    assertEquals(resultPoint[0], 2.0);
    assertEquals(resultPoint[1], 4.0);
    
    resultPoint = Vector.times(secondPoint, 0.5);
    assertEquals(resultPoint[0], 1.0);
    assertEquals(resultPoint[1], -2.0);
  }
  
  @Test
  void TestLineToLine()
  {
    double[] firstPoint = {1.0, 1.0};
    double[] secondPoint = {1.0, 2.0};
    assertEquals(Vector.distancePointToPoint(firstPoint, secondPoint), 1.0);
    
    secondPoint[0] = 2.0;
    assertEquals(Vector.distancePointToPoint(firstPoint, secondPoint), Math.sqrt(2.0));
  }
  
  @Test
  void TestPointToLine()
  {
    double[] p1 = {1.0, 1.0};
    double[] p2 = {3.0, 3.0};
    double[] point = {0.0, 0.0};
    assertEquals(Vector.distancePointToLine(p1, p2, point), Math.sqrt(2.0));
    
    point[0] = 3.0;
    point[1] = 1.0;
    assertEquals(Vector.distancePointToLine(p1, p2, point), Math.sqrt(2.0), 0.0001);
    
    point[0] = 4.0;
    point[1] = 4.0;
    assertEquals(Vector.distancePointToLine(p1, p2, point), Math.sqrt(2.0), 0.0001);
  }
  
  @Test
  void TestPointToLineHard()
  {
    double[] p1 = {0, 5};
    double[] p2 = {5, 0};
    double[] point = {0, 0};
    
    assertEquals(Vector.distancePointToLine(p1, p2, point), Math.sqrt(2.0) * 2.5, 0.0001);
    
    point[0] = 3;
    point[1] = 2;
    assertEquals(Vector.distancePointToLine(p1, p2, point), 0.0);
    
    point[0] = 5;
    point[1] = -5;
    assertEquals(Vector.distancePointToLine(p1, p2, point), 5);
    
    point[0] = 0;
    assertEquals(Vector.distancePointToLine(p1, p2, point), Math.sqrt(2) * 5);
    
    point[0] = -1;
    point[1] = -1;
    assertEquals(Vector.distancePointToLine(p1, p2, point), Math.sqrt(2) * 3.5);
    
    point[0] = 0;
    point[1] = 5;
    assertEquals(Vector.distancePointToLine(p1, p2, point), 0);
  }

}
