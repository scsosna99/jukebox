package com.buddhadata.projects.jukebox.random;

import com.buddhadata.projects.jukebox.random.sdr.SdrRandom;

import java.util.Random;
import java.util.Scanner;
import java.util.function.LongSupplier;

/**
 * Generates random numbers by using bits generated through whatever external long provider is supplied
 */
public class ExternalSeedRandom extends Random {

  /**
   * Provides the next seed value to use, however the producer wants to supply it.
   */
  private final LongSupplier seedProducer;

  /**
   * Constructor
   * @param seedProducer instance of class which can generate a seed to use.
   */
  public ExternalSeedRandom (LongSupplier seedProducer) {
    this.seedProducer = seedProducer;
  }

  /**
   * All methods in java.util.Random call this method to get a series of bits, from which the next int, long,
   * double, whatever is generated.  Based on this presumed source: http://developer.classpath.org/doc/java/util/Random-source.html
   * @param bits  the number of random bits to generate, in the range 1..32
   * @return the next random value
   */
  @Override
  protected int next (int bits) {
    long l = seedProducer.getAsLong() & ((1L << 48) - 1);
    return (int) (l >>> (48 - bits));
  }


  /**
   * Dummy program for testing things out.
   * @param args
   */
  public static void main (String[] args) {

    //  Create the seed producer
    SdrRandom sdr = new SdrRandom ("96.5", "mypipe");
    sdr.start();

    Random rnd = new ExternalSeedRandom (sdr);
    Scanner input = new Scanner(System.in);
    for (int i = 0; i < 10; i++) {
      input.nextLine();
      System.out.println (rnd.nextInt());
    }
    sdr.stop();
  }
}
