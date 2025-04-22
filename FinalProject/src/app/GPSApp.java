package app;

import java.awt.*;
import javax.swing.*;

import com.fazecast.jSerialComm.*;
import java.io.*;

import gps.*;

/**
 * An application that can be used to test a GPSReaderTask.
 * 
 * @author Prof. David Bernstein, James Madison University
 * @version 1.0
 */
public class GPSApp implements GPSObserver, Runnable
{
  private JTextArea textArea;
  
  /**
   * Default COnstructor.
   */
  public GPSApp()
  {
    // Nothing to do.
  }

  /**
   * Handle an NMEA sentence.
   * 
   * @param data The NMEA sentecnce
   */
  @Override
  public void handleGPSData(final String data)
  {
    GPGGASentence gpgga = GPGGASentence.parseGPGGA(data);
    if (gpgga != null) textArea.append(String.format("%9.6f, %9.6f",
        gpgga.getLongitude(),gpgga.getLatitude()));
    else textArea.append("Waiting for a fix...");
    textArea.append("\n");
  }
  
  /**
   * The code to run in the event dispatch thread.
   */
  @Override
  public void run()
  {
    JFrame frame = new JFrame("GPS Window");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel contentPane = (JPanel)frame.getContentPane();
    contentPane.setLayout(new BorderLayout());
    textArea = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(textArea);
    contentPane.add(scrollPane, BorderLayout.CENTER);
    frame.setSize(600, 600);

    // Find the right serial port
//    SerialPort[] ports = SerialPort.getCommPorts();
//    String gpsPath = null;
//    for (SerialPort port:ports)
//    {
//      String description = port.getPortDescription();
//      String path = port.getSystemPortPath();
//      if (description.indexOf("GPS") >= 0) gpsPath = path;
//    }
  
    // Setup the serial port
//    SerialPort gps = SerialPort.getCommPort(gpsPath); 
//    gps.openPort();
//    gps.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
//    InputStream is = gps.getInputStream();
    
    GPSSimulator gps = new GPSSimulator("rockingham.gps");
    InputStream is = gps.getInputStream();
    
    // Setup the GPSReaderTask
    GPSReaderTask gpsReader = new GPSReaderTask(is, "GPGGA");
    gpsReader.addGPSObserver(this);
    frame.setVisible(true);
    gpsReader.execute();
  }
}
