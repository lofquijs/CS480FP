package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HexFormat;

import org.junit.jupiter.api.Test;

import gps.GPGGASentence;

class TestGGA
{
  String example = "$GPGGA,210230,3855.4487,N,09446.0071,W,1,07,1.1,370.5,M,-29.5,M,,*7A";

  @Test
  void TestLatConversion()
  {
    double lat = GPGGASentence.convertLatitude("3855.4487", "N");
    double desiredLat = 38 + (55.4487 / 60);
    assertEquals(desiredLat, lat);
    
    lat = GPGGASentence.convertLatitude("3855.4487", "S");
    desiredLat = (38 + (55.4487 / 60)) * -1;
    assertEquals(desiredLat, lat);
  }
  
  
  @Test
  void TestLongConverstion()
  {
    double lon = GPGGASentence.convertLongitude("09446.0071", "W");
    double desiredLong = -1 * (94 + (46.0071 / 60));
    assertEquals(desiredLong, lon);
    
    lon = GPGGASentence.convertLongitude("09446.0071", "E");
    desiredLong = 94 + (46.0071 / 60);
    assertEquals(desiredLong, lon);
  }
  
  @Test
  void TestChecksum()
  {
    int astriks = example.indexOf('*');
    String sentence = example.substring(0, astriks);
    int checkSum = Integer.parseInt(example.substring(astriks + 1), 16);
    int test = GPGGASentence.addToChecksum(sentence, checkSum);
    assertEquals(test, checkSum);
    
    sentence = "TESTINGTESTINGTESTING";
    checkSum = 30;
    test = GPGGASentence.addToChecksum(sentence, checkSum);
    assertEquals(-1, test);
  }
  
  @Test
  void TestParse()
  {
    GPGGASentence gas = GPGGASentence.parseGPGGA(example);
    assertEquals(38 + (55.4487 / 60), gas.getLatitude());
    assertEquals(-1 * (94 + (46.0071 / 60)), gas.getLongitude());
  }
  
  @Test
  void TestEmptyParse()
  {
    String newExample = "$GPGGA,,,,,,0,00,,,,,,,*66";
    GPGGASentence gas = GPGGASentence.parseGPGGA(newExample);
    assertNull(gas);
  }
  

}
