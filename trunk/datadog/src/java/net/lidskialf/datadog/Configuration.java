/*
 * Copyright (C) 2005 Andrew de Quincey <adq_dvb@lidskialf.net>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package net.lidskialf.datadog;

import java.util.*;
import java.io.*;
import java.nio.channels.*;

/**
 * Class providing accessors to the DataDog property file (.datadog.properties in user's homedir).
 *
 * @author Andrew de Quincey
 */
public class Configuration {

    private static Properties properties = null;

    private static long lastModified = 0;

    private static FileLock fileLock = null;


    /**
     * Load the configuration. Must be called before any other methods.
     *
     * @return False if it could not be loaded - this is a fatal error!
     */
    public static boolean init() {
        getProperties();
        return (properties != null);
    }

    /**
     * Convenience method to get a single property.
     *
     * @param propertyName Name of property.
     * @return Value of property - will return "" if missing.
     */
    public static String getProperty(String propertyName) {
        String tmp = getProperties().getProperty(propertyName);
        if (tmp == null) tmp = "";
        return tmp;
    }

    /**
     * Convenience method to set a single property (also saves the properties file). This will lock the backing properties file during the update.
     *
     * @param propertyName Name of property.
     * @param propertyValue Value to set.
     *
     * @throws IOException On IO error.
     */
    public static synchronized void setProperty(String propertyName, String propertyValue) throws IOException {
        lock();
        File propFile = new File(propertiesFilename());
        getProperties().setProperty(propertyName, propertyValue);
        getProperties().store(new FileOutputStream(propFile), "DataDog properties");
        lastModified = propFile.lastModified();
        unlock();
    }

    /**
     * Exclusively locks the properties file. This will lock the file itself - so concurrent DataDog processes are fine.
     * This is exposed so functions requiring a global lock between all running DataDog instances can use it.
     *
     * @throws IOException On IO error.
     */
    public static synchronized void lock() throws IOException {
        RandomAccessFile openedFile = new RandomAccessFile(propertiesFilename(), "r");
        fileLock = openedFile.getChannel().lock();
    }

    /**
     * Unlocks the properties file. Should always be paired with a preceding successful call to lock().
     * This is exposed so functions requiring a global lock between all running DataDog instances can use it.
     *
     * @throws IOException On IO Error.
     */
    public static synchronized void unlock() throws IOException {
        if (fileLock != null) fileLock.release();
        fileLock = null;
    }

    /**
     * Get the property set.
     *
     * @return The Property set (or null if it could not be loaded.
     */
    private static Properties getProperties() {
        // if the properties are already loaded, check the modification stamp to see if it differs,
        if (properties != null) {
            File propFile = new File(propertiesFilename());
            if (propFile.lastModified() == lastModified) return properties;
        }
        properties = new Properties();

        // try and read an existing file
        File propertiesFile = new File(propertiesFilename());
        if (propertiesFile.exists()) {
            try {
                RandomAccessFile openedFile = new RandomAccessFile(propertiesFile, "r");
                FileLock lock = openedFile.getChannel().lock(0, 1, true);
                properties.load(new FileInputStream(propertiesFilename()));
                lastModified = propertiesFile.lastModified();
                lock.release();
                return properties;
            } catch (IOException e) {
            }
        }

        // OK: attempt to create a new (empty) file
        try {
            RandomAccessFile openedFile = new RandomAccessFile(propertiesFile, "w");
            FileLock lock = openedFile.getChannel().lock();
            properties.load(new FileInputStream(propertiesFilename()));
            openedFile.getChannel().force(true);
            lastModified = propertiesFile.lastModified();
            lock.release();
        } catch (IOException e) {
            e.printStackTrace();
            properties = null;
        }

        return properties;
    }

    /**
     * Convenience method to get the path of the properties file.
     *
     * @return The absolute path to the properties file.
     */
    private static String propertiesFilename() {
        return System.getProperty("user.home") + File.separator + ".datadog.properties";
    }
}
