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
package net.lidskialf.datadog.mpeg;

import java.io.*;

import net.lidskialf.datadog.StreamExplorer;
import net.lidskialf.datadog.StreamExplorerFactory;
import net.lidskialf.datadog.mpeg.bitstream.TransportStream;
import net.lidskialf.datadog.mpeg.ui.TransportStreamExplorer;

/**
 * The StreamParserFactory for Transport Streams.
 * 
 * @author Andrew de Quincey
 */
public class TransportStreamExplorerFactory implements StreamExplorerFactory {

  /* (non-Javadoc)
   * @see net.lidskialf.datadog.StreamParser#Probe(java.lang.String)
   */
  public boolean Probe(String filename) throws IOException {
    return TransportStream.Probe(filename);
  }

  /* (non-Javadoc)
   * @see net.lidskialf.datadog.StreamParser#OpenUI(java.lang.String)
   */
  public StreamExplorer Parse(String filename) throws IOException {
    return new TransportStreamExplorer(filename);
  }
}
