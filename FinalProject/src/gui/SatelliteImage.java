package gui;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import geography.MapProjection;

/**
 * Class that represents a SatelliteImage.
 * 
 * @author Dakota Lawson
 * @version 1.0.0
 */
public class SatelliteImage
{
  private Rectangle2D bounds;
  private File image;

  public SatelliteImage(final String imagePath, final String dataPath, final MapProjection proj)
  {
    // TODO: I should move all of this to a read function
    this.image = new File(imagePath);

    double[] topLeftLL = new double[2];
    double[] bottomRightLL = new double[2];

    try (BufferedReader reader = new BufferedReader(new FileReader(dataPath)))
    {
      String line1 = reader.readLine();
      String line2 = reader.readLine();

      String[] parts1 = line1.trim().split("\\s+");
      topLeftLL[0] = Double.parseDouble(parts1[0]);
      topLeftLL[1] = Double.parseDouble(parts1[1]);

      String[] parts2 = line2.trim().split("\\s+");
      bottomRightLL[0] = Double.parseDouble(parts2[0]);
      bottomRightLL[1] = Double.parseDouble(parts2[1]);

      double[] topLeftXY = proj.forward(topLeftLL);
      double[] bottomRightXY = proj.forward(bottomRightLL);

      // Get min and max for x and y in case coordinates are not in strict order
      double minX = Math.min(topLeftXY[0], bottomRightXY[0]);
      double maxX = Math.max(topLeftXY[0], bottomRightXY[0]);
      double minY = Math.min(topLeftXY[1], bottomRightXY[1]);
      double maxY = Math.max(topLeftXY[1], bottomRightXY[1]);

      double width = maxX - minX;
      double height = maxY - minY;

      this.bounds = new Rectangle2D.Double(minX, minY, width, height);
    }
    catch (NumberFormatException | IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Loads this satellite image into a BufferedImage to be drawn.
   * 
   * @return the loaded image
   */
  public BufferedImage loadImage()
  {
    BufferedImage img;

    try
    {
      img = ImageIO.read(image);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      img = null;
    }

    return img;
  }

  /**
   * Getter for the bounds of this satellite image.
   * 
   * @return a Rectangle2D that stores the bounds of this satellite image
   */
  public Rectangle2D getBounds()
  {
    return bounds;
  }
}
