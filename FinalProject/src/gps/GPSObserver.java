package gps;

/**
 * Interface used for the GUI.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public interface GPSObserver
{
  /**
   * Parses NMEA sentences it is passed, and store them.
   * @param sentence -> NMEA Sentences
   */
  public void handleGPSData(String sentence);
}
