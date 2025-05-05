package gps;

import java.util.StringTokenizer;

/**
 * Class that reads in $GPGGA Sentences.
 * 
 * @author Andrew Hansen, Jackson Lofquist
 * 
 * This work complies with the JMU Honor Code.
 */
public class GPGGASentence extends NMEASentence
{
  private String time;
  private double latitude;
  private double longitude;
  private int fixType;
  private int satellites;
  private double dilution;
  private double altitude;
  private String altitudeUnits;
  private double seaLevel;
  private String geoidUnits;
  
  /**
   * Explicit Value Constructor.
   * @param latitude -> Latitude from the GPGGA
   * @param longitude -> Longitude from the GPGGA
   */
  public GPGGASentence(final double latitude, final double longitude)
  {
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  /**
   * Reads string representation of GPGGA, and makes an GPGGASentence Object.
   * @param s -> GPGGA Sentence
   * @return GPGGASentence Object
   */
  public static GPGGASentence parseGPGGA(final String s)
  {
    int astriks = s.indexOf('*');
    String sentence = s.substring(0, astriks);
    int checkSum = Integer.parseInt(s.substring(astriks + 1), 16);
    if (addToChecksum(sentence, checkSum) == -1)
      return null;
    
    
    StringTokenizer st = new StringTokenizer(s, ",");
    // TODO Handle errors with in different NMENA Sentences
    
    
    st.nextToken(); // $GPGGA
    st.nextToken(); //210230
    
    String dot = ".";
    String stringLat = st.nextToken(); // 3855.4487
    if (!stringLat.contains(dot)) return null;
    String directionLat = st.nextToken();
    double latitude = convertLatitude(stringLat, directionLat);
    if ("S".equalsIgnoreCase(directionLat)) {
      latitude = -latitude;
    }

    String stringLong = st.nextToken();
    if (!stringLong.contains(dot)) return null;
    String directionLong = st.nextToken();
    double longitude = convertLongitude(stringLong + directionLong);
    if ("W".equalsIgnoreCase(directionLong)) {
      longitude = -longitude;
    }
    
    return new GPGGASentence(latitude, longitude);
  }

  /**
   * Getter for Longitude.
   * @return longitude
   */
  public double getLongitude()
  {
    // TODO Auto-generated method stub
    return longitude;
  }

  /**
   * Getter for Latitude.
   * @return latitude
   */
  public double getLatitude()
  {
    // TODO Auto-generated method stub
    return latitude;
  }
}
