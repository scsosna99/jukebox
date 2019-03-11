package com.buddhadata.projects.jukebox.random.sdr;


import org.springframework.web.bind.annotation.PathVariable;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

/**
 * Random number generator that uses the data received from the software-defined radio - specifically, rtl_fm - to generate
 * new bits used when generating the random number.  Everything else in the default Random class for generating the random numbers
 * remains the same.
 */
public class SdrRandom
  implements LongSupplier {

  /**
   * boolean for determining whether the thread reading the SDR data should keep working.
   */
  private final AtomicBoolean reading = new AtomicBoolean (false);

  /**
   * The circular buffer into which the data is stored.
   */
  private final byte[] circular;

  /**
   * the size of the buffer, used for reference when reading.
   */
  private final int bufferSize;

  /**
   * current reading position in the buffer to use for generating a random number
   */
  private Integer readPos = 0;

  /**
   * The frequency in which to tune the RTL software-defined radio
   */
  private final String frequency;

  /**
   * The pipe from which the random data is read from, i.e., the input from the software-defined radio stream.
   */
  private final String pipeName;

  /**
   * if not otherwise specified, the size of the buffer into which data is read
   */
  private static int DEFAULT_BUFFER_SIZE = 20480;

  /**
   * Constructor
   * @param frequency frequency in which to tune the RTL software-defined radio
   * @param pipeName pipe from which the random data is read from, i.e., the input from the software-defined radio stream.
   * @param bufferSize how big of a circular buffer to create/use
   */
  public SdrRandom (String frequency,
                    String pipeName,
                    int bufferSize) {

    super();

    this.frequency = frequency;
    this.pipeName = pipeName;
    this.bufferSize = bufferSize;
    this.circular = new byte [bufferSize];
  }

  /**
   * Constructor
   * @param frequency frequency in which to tune the RTL software-defined radio
   * @param pipeName pipe from which the random data is read from, i.e., the input from the software-defined radio stream.
   */
  public SdrRandom (String frequency,
                    String pipeName) {
    this (frequency, pipeName, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Returns a long value based on the bytes in the circular buffer.
   * @return
   */
  public long getAsLong() {

    //  Get the read position and increment immediately, gives us some level of multithreaded-ness.  Even though
    //  the bytes are going to constantly be rewritten as new data is received from the SDR stream, we'll just march
    //  through the buffer reading gradually based on how many random numbers are generated.
    int currentRead;
    synchronized (readPos) {
      currentRead = readPos;
      readPos += 8;
      if (readPos >= bufferSize) {
        readPos = 0;
      }
    }

    // Construct a long by shifting the existing answer 8 bits to the left and or'ing the current byte.
    ByteBuffer buff = ByteBuffer.allocate(Long.BYTES);
    buff.put(circular, currentRead, 8);
    buff.flip();
    long toReturn =  buff.getLong();


    //  Return the newly-built long.
    return toReturn;
  }

  /**
   * Start the thread for reading data from the pipe.
   */
  public void start () {
    reading.set(true);
    new Thread(() -> readData()).start();
  }

  /**
   * Stop the thread for reading data from the pipe.
   */
  public void stop() {
    reading.set(false);
  }

  /**
   * Read the SDR data from the pipe named and stuff it into the buffer.
   */
  private void readData () {

    System.out.println ("Starting thread for reading data");

    try (FileInputStream fis = new FileInputStream (pipeName)) {

      //  Need an input data in which to read the data.
      byte[] input = new byte[2048];
      int writePos = 0;

      //  Keep reading data until the flag is turned off/disabled/whatever.
      while (reading.get()) {

        //  Read data
        int read = fis.read(input);
        int remaining = circular.length - writePos;

        //  If we don't exceed the end of the array based on where we are and how much we read, single array copy suffies.
        if (read < remaining) {
          System.arraycopy(input, 0, circular, writePos, read);
          writePos += read;
        } else {
          //  we're going past the end of the circular buffer so need to fill in the end and then wrap around to the beginning.
          System.arraycopy (input, 0, circular, writePos, remaining);
          System.arraycopy (input, remaining, circular, 0, writePos = read - remaining);
        }
      }

    } catch (Exception e) {
      System.out.println ("Exception reading SDR data: " + e);
    }

    System.out.println ("Stopping thread for reading data.");
  }
}
