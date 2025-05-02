package app;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * Driver for the final project.
 * 
 * @author Mason Puckett, Jackson Lofquist, Dakota Lawson, Andrew Hansen
 * @version 1.0
 */
public class FPDriver
{

  /**
   * The entry point of the FP App.
   * 
   * @param args command line arguments.
   * @throws InterruptedException if something goes wrong
   * @throws InvocationTargetException if something goes wrong
   */
  public static void main(final String[] args)
      throws InterruptedException, InvocationTargetException
  {
    SwingUtilities.invokeAndWait(new FPApp());
  }
}
