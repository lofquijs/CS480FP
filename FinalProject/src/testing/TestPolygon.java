package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.junit.jupiter.api.Test;

import geography.Polygon;

class TestPolygon
{
  Polygon polygon = new Polygon("053103");

  @Test
  void TestPolygonGetID()
  {
    assertEquals(polygon.getID(), "053103");
  }
  
  @Test
  void TestPolygonOverload()
  {
    Rectangle2D.Double rec = new Rectangle2D.Double(0.0, -5.0, 2.0, 2.0);
    Path2D.Double path = new Path2D.Double(rec);
    Polygon poly = new Polygon("040703", path);
    
    assertEquals(poly.getID(), "040703");
    assertTrue(poly.getShape().contains(rec));
  }

}
