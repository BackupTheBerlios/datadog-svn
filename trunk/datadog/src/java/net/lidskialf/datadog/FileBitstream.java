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

import java.io.*;

/**
 * A Bitstream which acts directly on a file on disk.
 * 
 * @author Andrew de Quincey
 */
public class FileBitstream implements Bitstream {

    /**
     * Constructor.
     * 
     * @param filename
     *            The filename of the file to wrap.
     */
    public FileBitstream(String filename) throws IOException {
        this.filename = filename;
        dataFile = new RandomAccessFile(filename, "r");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.lidskialf.datadog.Bitstream#readByte()
     */
    public int readByte() throws IOException {
        return dataFile.read();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.lidskialf.datadog.Bitstream#readBlock(byte[], int, int)
     */
    public int readBlock(byte[] dest) throws IOException {
        return dataFile.read(dest);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.lidskialf.datadog.Bitstream#readBlock(byte[], int, int)
     */
    public int readBlock(byte[] dest, int destPos, int length) throws IOException {
        return dataFile.read(dest, destPos, length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.lidskialf.datadog.Bitstream#getLength()
     */
    public long length() throws IOException {
        return dataFile.length();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.lidskialf.datadog.Bitstream#seek(long)
     */
    public void seek(long position) throws IOException {
        dataFile.seek(position);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.lidskialf.datadog.Bitstream#close()
     */
    public void close() throws IOException {
        dataFile.close();
        dataFile = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return filename;
    }

    /**
     * The file backing the bitstream.
     */
    private RandomAccessFile dataFile;

    /**
     * The filename of the file.
     */
    private String filename;
}
