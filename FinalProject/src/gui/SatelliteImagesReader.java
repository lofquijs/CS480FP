package gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import geography.MapProjection;

/**
 * Class that reads in all satellite images and associated data in the given folders.
 * 
 * @author Dakota Lawson
 * @version 1.0.0
 */
public class SatelliteImagesReader
{
  private List<SatelliteImage> imgs;
  private MapProjection proj;
  private Path data, images;

  /**
   * Explicit value constructor.
   * 
   * @param images
   *          the path to the image dir
   * @param data
   *          the path to the data dir
   * @param proj
   *          the map projection
   */
  public SatelliteImagesReader(final String images, final String data, final MapProjection proj)
  {
    this.imgs = new ArrayList<>();
    this.data = Paths.get(data);
    this.images = Paths.get(images);
    this.proj = proj;
  }

  /**
   * Reads in a directory of satellite images into SatelliteImage objects.
   */
  public void read()
  {
    try
    {
      // Create iterators for image files
      Iterator<String> imageFilePathsIterator = Files.list(images).filter(Files::isRegularFile)
          .map(Path::toString) // convert each Path to String
          .sorted().iterator(); // Convert the stream into an iterator

      // Create iterators for data files
      Iterator<String> dataFilePathsIterator = Files.list(data).filter(Files::isRegularFile)
          .map(Path::toString) // convert each Path to String
          .sorted().iterator(); // Convert the stream into an iterator

      // Iterate through both iterators simultaneously
      while (imageFilePathsIterator.hasNext() && dataFilePathsIterator.hasNext())
      {
        String imagePath = imageFilePathsIterator.next();
        String dataPath = dataFilePathsIterator.next();
        // System.out.println(dataPath);
        SatelliteImage temp = new SatelliteImage(imagePath, dataPath, proj);
        temp.read();
        imgs.add(temp);
      }
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public SatelliteImage findSatelliteImage(double[] km)
  {
    for (SatelliteImage img : imgs)
    {
      if (img.getBounds().contains(km[0], km[1])) {
        // Print the name of the chosen image
        System.out.println("Chosen Image: " + img.getImageName());
        return img;
      }
    }
    return null;
  }
}
