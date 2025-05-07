package testing;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import geography.AbstractMapProjection;
import geography.ConicalEqualAreaProjection;
import geography.GeographicShape;
import geography.GeographicShapesReader;
import geography.MapMatcher;
import gui.CartographyDocument;

class TestMapMatcher {

	@Test
	void TestConstructor() throws FileNotFoundException {
		InputStream isgeo = new FileInputStream(new File("rockingham-streets.geo"));
	    AbstractMapProjection proj = new ConicalEqualAreaProjection(-96.0, 37.5, 29.5, 45.5);
	    GeographicShapesReader gsReader = new GeographicShapesReader(isgeo, proj);
	    CartographyDocument<GeographicShape> geographicShapes = gsReader.read();
	    MapMatcher mm = new MapMatcher(geographicShapes);
	    double[] km = {1456.232, 240.0};
	    LinkedList<GeographicShape> shapes = mm.getClosestGeographicShapes(km);
	    assertEquals(shapes.getClass(), LinkedList.class);
	}
	
	@Test
	void TestEdgeCases() throws FileNotFoundException
	{
		InputStream isgeo = new FileInputStream(new File("rockingham-streets.geo"));
	    AbstractMapProjection proj = new ConicalEqualAreaProjection(-96.0, 37.5, 29.5, 45.5);
	    GeographicShapesReader gsReader = new GeographicShapesReader(isgeo, proj);
	    CartographyDocument<GeographicShape> geographicShapes = gsReader.read();
	    MapMatcher mm = new MapMatcher(geographicShapes);
	    
	    // NULLS
	    double[] km1 = {Double.MAX_VALUE, Double.MAX_VALUE};
	    double[] km2 = {1500.0, Double.MIN_VALUE};
	    double[] km3 = {Double.MIN_VALUE, Double.MAX_VALUE};
	    double[] km4 = {1500.0, Double.MAX_VALUE};
	    assertNull(mm.getClosestGeographicShapes(km1));
	    assertNull(mm.getClosestGeographicShapes(km2));
	    assertNull(mm.getClosestGeographicShapes(km3));
	    assertNull(mm.getClosestGeographicShapes(km4));
	    
	    // MAX/MINS
	    double[] maxKM = {1501.9431023788957, 281.56193744751045};
	    double[] minKM = {1438.2523713927162, 214.37637700137842};
	    assertEquals(mm.getClosestGeographicShapes(maxKM).getClass(), LinkedList.class);
	    assertEquals(mm.getClosestGeographicShapes(minKM).getClass(), LinkedList.class);
	}

}
