package gps;

/**
 * Class with functions to deal with NMEA Sentences.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public abstract class NMEASentence
{
  /**
   * Checks on the value of checksum.
   * @param s -> String being checked.
   * @param originalChecksum -> The original check sum at the end of a NMEA sentence.
   * @return -1 if originalChecksum is not correct, or the originalChecksum if it is.
   */
  public static int addToChecksum(final String s, final int originalChecksum)
  {
    int current = 0, length;
    length = s.length();
    for (int i = 0; i < length; i++)
    {
      char character = s.charAt(i);
      if (character == '$') continue;
      current ^= (int) character;
    }
    current %= 256;
    if (current != originalChecksum) return -1;
    return current;
  }
  
  /**
   * Converts Degrees and Minutes to Decimal.
   * @param latitudeString -> String representation of latitude
   * @return double representation of latitude
   */
  public static double convertLatitude(final String latitudeString)
  {
    String degrees = latitudeString.substring(0, 2);
    String minutes = latitudeString.substring(2, 9);
    String direction = latitudeString.substring(9, 10);
    double lat = Double.parseDouble(degrees) + Double.parseDouble(minutes) / 60;
    if (direction.equals("N")) return lat;
    return -1 * lat;
  }
  
  /**
   * Converts Degrees and Minutes to Decimal.
   * @param longitudeString -> String representation of longitude
   * @return double representation of longitude
   */
  public static double convertLongitude(final String longitudeString)
  {
    String degrees = longitudeString.substring(0, 3);
    String minutes = longitudeString.substring(3, 10);
    String direction = longitudeString.substring(10, 11);
    double lon = Double.parseDouble(degrees) + Double.parseDouble(minutes) / 60;
    if (direction.equals("E")) return lon;
    return -1 * lon;
  }
}
