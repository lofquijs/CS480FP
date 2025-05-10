package simulator;

import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

/**
 * The main class for PA5.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class PA6Driver 
{
  /**
   * The entry point for the application.
   * 
   * @param args The command-line arguments (IGNORED)
   * @throws InterruptedException if something goes wrong with Swing
   * @throws InvocationTargetException if something goes wrong with Swing
   */
  public static void main(final String[] args) 
      throws InterruptedException, InvocationTargetException 
  {
    SwingUtilities.invokeAndWait(new PA6App());
  }
}