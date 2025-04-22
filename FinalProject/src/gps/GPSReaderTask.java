package gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * Reads from an input stream and processes NEMA Sentences.
 * 
 * @author Andrew Hansen
 * 
 * This work complies with the JMU Honor Code.
 */
public class GPSReaderTask extends SwingWorker<Void, String> implements GPSSubject 
{
  private BufferedReader in;
  private String[] sentences;
  private List<GPSObserver> observers = new ArrayList<>();
  
  /**
   * Explicit Value Constructor.
   * @param is -> Input Stream
   * @param sentences -> What sentences are being remembered (ex. GPGGA, GSA, etc.)
   */
  public GPSReaderTask(final InputStream is, final String... sentences)
  {
    this.in = new BufferedReader(new InputStreamReader(is));
    this.sentences = sentences;
  }
  
  /**
   * Background thread that continuously reads InputStream until cancelled.
   * @return void
   */
  public Void doInBackground() throws IOException
  {
    while (!super.isCancelled())
      super.publish(in.readLine());
    return null;
  }
  
  /**
   * Notifies all GPSObserver objects if desired sentence is found.
   * @param lines -> The Lines being read.
   */
  public void process(final List<String> lines)
  {
    for (String line: lines)
    {
      for (String sentence: sentences)
      {
        if (line.contains(sentence))
          notifyGPSObservers(line);
      }
    }
  }

  @Override
  public void addGPSObserver(final GPSObserver observer)
  {
    // TODO Auto-generated method stub
    observers.add(observer);
  }

  @Override
  public void notifyGPSObservers(final String sentence)
  {
    // TODO Auto-generated method stub
    for (GPSObserver observer: observers)
      observer.handleGPSData(sentence);
  }

  @Override
  public void removeGPSObserver(final GPSObserver observer)
  {
    // TODO Auto-generated method stub
    observers.remove(observer);
  }
}
