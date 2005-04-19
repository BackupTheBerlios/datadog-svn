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
 * Abstract representation of a bitstream.
 * 
 * @author Andrew de Quincey
 */
public interface Bitstream {

    /**
     * Read a single byte from the bitstream.
     * 
     * @return The byte
     * @throws IOException
     *             On error.
     */
    public int readByte() throws IOException;

    /**
     * Read a block of data from the bitstream.
     * 
     * @param dest
     *            Destination for the data
     * @param destPos
     *            Position into block to write the data.
     * @param length
     *            Number of bytes to read.
     * @return The number of bytes actually read.
     * @throws IOException
     *             On error.
     */
    public int readBlock(byte[] dest) throws IOException;

    /**
     * Read a block of data from the bitstream.
     * 
     * @param dest
     *            Destination for the data
     * @param destPos
     *            Position into block to write the data.
     * @param length
     *            Number of bytes to read.
     * @return The number of bytes actually read.
     * @throws IOException
     *             On error.
     */
    public int readBlock(byte[] dest, int destPos, int length) throws IOException;

    /**
     * Get the length of the bitstream.
     * 
     * @return The length.
     * @throws IOException
     *             On error.
     */
    public long length() throws IOException;

    /**
     * Seek to a position within the bitstream.
     * 
     * @param position
     *            The position to seek to.
     * @throws IOException
     *             On error.
     */
    public void seek(long position) throws IOException;

    /**
     * Close the Bitstream when it is no longer required.
     * 
     * @throws IOException
     *             On error.
     */
    public void close() throws IOException;
}
