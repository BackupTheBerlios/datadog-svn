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
 * A StreamParserFactory is responsible for probing and creating StreamParsers.
 *
 * @author Andrew de Quincey
 */
public interface StreamExplorerFactory {

    /**
     * Probe to see if the stream is supported by this StreamParser.
     *
     * @param bitstream
     *            The Bitstream to test.
     * @return True if supported, false if not.
     * @throws IOException
     *             On error.
     */
    public boolean probe(Bitstream bitstream) throws IOException;

    /**
     * Open a stream.
     *
     * @param bitstream
     *            The Bitstream to open.
     * @return StreamParser StreamParser instance representing this stream.
     * @throws IOException
     *             On error.
     */
    public StreamExplorer open(Bitstream bitstream) throws IOException;
}
