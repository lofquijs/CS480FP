package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import feature.Street;
import feature.StreetSegment;
import feature.StreetsReader;
import geography.AbstractMapProjection;
import geography.ConicalEqualAreaProjection;
import geography.GeographicShape;
import geography.GeographicShapesReader;
import graph.StreetNetwork;
import gui.CartographyDocument;

class TestStreetNetwork
{

  @Test
  void TestCreateStreetNetwork() throws IOException
  {
 // READ .geo (stolen from PA5App)
    InputStream isgeo = new FileInputStream(new File("virginia-streets.geo"));
    AbstractMapProjection proj = new ConicalEqualAreaProjection(-96.0, 37.5, 29.5, 45.5);
    GeographicShapesReader gsReader = new GeographicShapesReader(isgeo, proj);
    CartographyDocument<GeographicShape> geographicShapes = gsReader.read();
    assertEquals("54195970", geographicShapes.getElement("54195970").getID());
    
    // READ .str (stolen from PA5App)
    InputStream iss = new FileInputStream(new File("virginia-streets.str"));
    StreetsReader sReader = new StreetsReader(iss, geographicShapes);
    Map<String, Street> streets = new HashMap<String, Street>();
    CartographyDocument<StreetSegment> document = sReader.read(streets);
    assertEquals("A31", document.getElement("54195970").getCode());
    
    StreetNetwork network = StreetNetwork.createStreetNetwork(streets);
    assertNotNull(network);
    
    // Testing Inbounds
    List<StreetSegment> inbounds = network.getIntersection(0).getInbound();
    Iterator<StreetSegment> iter = inbounds.iterator();
    while (iter.hasNext())
    {
      StreetSegment segment = iter.next();
      assertEquals(0, segment.getHead());
      // USED TO CHECK RESULTS
//      System.out.println(segment.getID());
    }
    
    inbounds = network.getIntersection(005543).getInbound();
    for (StreetSegment segment : inbounds)
    {
      assertEquals(005543, segment.getHead());
    }
    
    // Testing Outbounds
    List<StreetSegment> outbounds = network.getIntersection(783654).getOutbound();
    for (StreetSegment segment: outbounds)
    {
      assertEquals(783654, segment.getTail());
      // USED TO CHECK RESULTS
//      System.out.println(segment.getID());
    }
  }

}
