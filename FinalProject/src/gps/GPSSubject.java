package gps;

/**
 * Interface used for the GPS Reader.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public interface GPSSubject
{
  /**
   * Adds GPSObserver into a list.
   * @param observer
   */
  public void addGPSObserver(GPSObserver observer);
  
  /**
   * Notify all GPS Observers.
   * @param sentence
   */
  public void notifyGPSObservers(String sentence);
  
  /**
   * Remove a certain GPSObserver from a list.
   * @param observer
   */
  public void removeGPSObserver(GPSObserver observer);
}
