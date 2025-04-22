package app;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

/**
 * A driver that can be used to test GPS capabilities.
 *
 * @author  Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class GPSDriver
{
  /**
   * The entry point of the application.
   *
   * @param args   The command line arguments
   * @throws InterruptedException if something goes wrong
   * @throws InvocationTargetException if something goes wrong 
   */
  public static void main(final String[] args) 
      throws InterruptedException, InvocationTargetException 
  {

    SwingUtilities.invokeAndWait(new GPSApp());
  }
}
