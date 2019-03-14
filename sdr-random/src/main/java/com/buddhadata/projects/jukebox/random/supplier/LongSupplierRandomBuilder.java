package com.buddhadata.projects.jukebox.random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.LongSupplier;

@Component
public class LongSupplierRandomBuilder {

    /**
     * Defines the class used for generating Longs used when getting bits for the random numbmer generator.
     */
    @Value("${jukebox.random.supplier}")
    private String supplierClassName;

    public Random create() {

        Random toReturn;
        if (supplierClassName != null) {

            try {
                //  Attempt to use reflection to get the constructor of the class and create an instance.
                Class supplier = Class.forName(supplierClassName);
                toReturn = new LongSupplierRandom((LongSupplier) supplier.getConstructor().newInstance());
            } catch (Throwable t) {
                System.out.println ("Exception creating byte supplier: " + t);
                toReturn = new Random(System.currentTimeMillis());
            }

        } else {
            //  No class name supplied, so just use plain-old Java random-number generator.
            toReturn = new Random(System.currentTimeMillis());
        }


        return toReturn;
    }
}
