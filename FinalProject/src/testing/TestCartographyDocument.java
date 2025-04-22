package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

import geography.GeographicShape;
import geography.PiecewiseLinearCurve;
import gui.CartographyDocument;

class TestCartographyDocument
{
  Map<String, GeographicShape> map = new HashMap<String, GeographicShape>();
  PiecewiseLinearCurve plc1 = new PiecewiseLinearCurve("23");
  PiecewiseLinearCurve plc2 = new PiecewiseLinearCurve("33");
  PiecewiseLinearCurve plc3 = new PiecewiseLinearCurve("6");

  @Test
  void TestCartographyDocumentElement()
  {
    map.put(plc1.getID(), plc1);
    map.put(plc2.getID(), plc2);
    map.put(plc3.getID(), plc3);
    
    CartographyDocument<GeographicShape> cd = new CartographyDocument<GeographicShape>(map, null);
    
    assertEquals(plc1, cd.getElement("23"));
    assertEquals(plc2, cd.getElement("33"));
    assertEquals(plc3, cd.getElement("6"));
    
    Iterator<GeographicShape> iter = cd.iterator();
    int i = 0;
    while(iter.hasNext())
    {
      GeographicShape shape = iter.next();
      switch (i)
      {
        case 0: assertEquals(plc2.getID(), shape.getID()); break;
        case 1: assertEquals(plc1.getID(), shape.getID()); break;
        case 2: assertEquals(plc3.getID(), shape.getID()); break;
      }
      i++;
    }
  }
  
  @Test
  void TestCartographyDocumentHighlight()
  { 
    map.put(plc1.getID(), plc1);
    map.put(plc2.getID(), plc2);
    map.put(plc3.getID(), plc3);
    
    CartographyDocument<GeographicShape> cd = new CartographyDocument<GeographicShape>(map, null);
    
    map.remove("23");
    map.remove("33");
    cd.setHighlighted(map);
    
    Iterator<GeographicShape> iter = cd.highlighted();
    GeographicShape shape = iter.next();
    assertEquals(shape.getID(), "6");
    
  }
  
  @Test
  void TestCartographyDocumentBounds()
  {
    double[] point1 = {23.0, 23.0};
    double[] point2 = {33.0, -33.0};
    double[] point3 = {-6.0, -6.0};
    map.put(plc1.getID(), plc1);
    map.put(plc2.getID(), plc2);
    map.put(plc3.getID(), plc3);
    
    Rectangle2D.Double rec = new Rectangle2D.Double();
    rec.add(point1[0], point1[1]);
    rec.add(point2[0], point2[1]);
    rec.add(point3[0], point3[1]);
    
    CartographyDocument<GeographicShape> cd = new CartographyDocument<GeographicShape>(map, rec);
    
    Rectangle2D.Double bounds = cd.getBounds();
    // Minimum x and y (bottom left corner of the rectangle)
    assertEquals(bounds.x, point3[0]);
    assertEquals(bounds.y, point2[1]);
    // Width -> Max(x) + Abs|Min(x)|
    assertEquals(bounds.width, point2[0] + Math.abs(point3[0]));
    // Height -> Max(y) + Abs|Min(y)|
    assertEquals(bounds.height, point1[1] + Math.abs(point2[1]));
    
  }

}
