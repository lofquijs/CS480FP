package gui;

import java.awt.Graphics2D;
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
  private BufferedImage cashedImg;
  private Rectangle2D bounds;
  private File data, image;
  private MapProjection proj;

  public SatelliteImage(final String imagePath, final String dataPath, final MapProjection proj)
  {
    this.image = new File(imagePath);
    this.data = new File(dataPath);
    this.proj = proj;
    this.cashedImg = null;
  }

  /**
   * Reads in a satellite image and data into this SateliteImage object.
   */
  public void read()
  {
    double[] topLeftLL = new double[2];
    double[] bottomRightLL = new double[2];

    try (BufferedReader reader = new BufferedReader(new FileReader(data.getPath())))
    {
      String line1 = reader.readLine();
      String line2 = reader.readLine();

//      System.out.println("Line 1: " + line1);
//      System.out.println("Line 2: " + line2);

      String[] parts1 = line1.trim().split("\\s+");
      String[] parts2 = line2.trim().split("\\s+");

      topLeftLL[0] = Double.parseDouble(parts2[1]); // Longitude
      topLeftLL[1] = Double.parseDouble(parts2[0]); // Latitude

      bottomRightLL[0] = Double.parseDouble(parts1[1]); // Longitude
      bottomRightLL[1] = Double.parseDouble(parts1[0]); // Latitude

//      System.out.printf("Parsed Top Left LL: %.6f, %.6f%n", topLeftLL[0], topLeftLL[1]);
//      System.out.printf("Parsed Bottom Right LL: %.6f, %.6f%n", bottomRightLL[0], bottomRightLL[1]);

      // Store the returned values in new arrays
      double[] topLeftXY = proj.forward(topLeftLL).clone(); // Clone to store a copy
      double[] bottomRightXY = proj.forward(bottomRightLL).clone(); // Clone to store a copy

//      System.out.printf("Top Left LL: %.6f, %.6f -> XY: %.6f, %.6f%n", topLeftLL[0], topLeftLL[1],
//          topLeftXY[0], topLeftXY[1]);
//      System.out.printf("Bottom Right LL: %.6f, %.6f -> XY: %.6f, %.6f%n", bottomRightLL[0],
//          bottomRightLL[1], bottomRightXY[0], bottomRightXY[1]);

      double minX = topLeftXY[0]; // X-coordinate of the top-left corner
      double minY = bottomRightXY[1]; // Y-coordinate of the bottom-right corner
      double width = Math.abs(bottomRightXY[0] - topLeftXY[0]);
      double height = Math.abs(topLeftXY[1] - bottomRightXY[1]);

      this.bounds = new Rectangle2D.Double(minX, minY, width, height);
      System.out.println("Calculated Bounds: " + bounds + " | Image: " + image.getName());
    }
    catch (NumberFormatException | IOException e)
    {
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
    if (cashedImg != null) {
      return cashedImg;
    }
    
    BufferedImage img = null;

    try
    {
      img = ImageIO.read(image);
      if (img == null)
      {
        System.err.println(
            "Error: Unable to load image. Unsupported or invalid format: " + image.getPath());
      }
      else
      {
        // Always convert to a standard type (TYPE_INT_ARGB)
        BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(),
            BufferedImage.TYPE_INT_ARGB // Use a standard type
        );
        Graphics2D g2d = convertedImg.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        img = convertedImg;
        cashedImg = img;
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
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

  /**
   * Getter for the name of the image file.
   * 
   * @return the name of the image file
   */
  public String getImageName()
  {
    return image.getName();
  }
}
