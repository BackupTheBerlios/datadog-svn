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
package net.lidskialf.datadog.mpeg.bitstream;

import java.io.IOException;
import java.io.RandomAccessFile;



/**
 * This implements MPEG2 transport streams.
 * 
 * @author Andrew de Quincey
 */
public class TransportStream {
  
  /**
   * Round packet position down to the previous packet. 
   */
  public static final int ROUND_PREVIOUS = 0;
  
  /**
   * Round packet position up to the next packet.
   */
  public static final int ROUND_NEXT = 1;
  
  
  
  /**
   * Construct a parser for an mpeg2 transport stream.
   *  
   * @param filename The filename of the stream.
   */
  public TransportStream(String filename) throws IOException {
    // sanity check
    if (!probe(filename)) {
      throw new RuntimeException("Invalid file: " + filename);
    }
    
    // open it
    dataFile = new RandomAccessFile(filename, "r");
  }
  
  /**
   * Retrieve the nearest transport packet to the given position.
   * 
   * @param position The position concerned.
   * @param rounding One of ROUND_*. If a packet does not start exactly at the given position, 
   * this controls how to find the one returned.
   * @return The packet, or null if unavailable (i.e. end of stream).
   * @throws IOException On IO error.
   */
  public TransportPacket getPacketAt(long position, int rounding) throws IOException {    
    // round to the correct place
    long startPosition = 0;
    if (rounding == ROUND_PREVIOUS) {
      startPosition = (position / Constants.TS_PACKET_LENGTH) * Constants.TS_PACKET_LENGTH;
    } else if (rounding == ROUND_NEXT) {
      startPosition = ((position + Constants.TS_PACKET_LENGTH) / Constants.TS_PACKET_LENGTH) * Constants.TS_PACKET_LENGTH;      
    }
    
    // check position is valid
    if ((startPosition < 0) || ((startPosition + Constants.TS_PACKET_LENGTH) > dataFile.length())) return null;
    
    // ok, read it!
    dataFile.seek(startPosition);
    byte[] data = new byte[Constants.TS_PACKET_LENGTH];
    dataFile.read(data);
    if (data[0] != Constants.TS_SYNC_BYTE) return null;
    TransportPacket packet = new TransportPacket(data, startPosition);
    
    // done
    return packet;
  }
  
  
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.Stream#Probe()
   */
  public static boolean probe(String filename) throws IOException {
    RandomAccessFile tmpFile = new RandomAccessFile(filename, "r");
    
    // must be at least one TS packet long
    if (tmpFile.length() < Constants.TS_PACKET_LENGTH) {
      tmpFile.close();
      return false;
    }
    
    // must be divisible by TS_PACKET_LENGTH
    if ((tmpFile.length() % Constants.TS_PACKET_LENGTH) != 0) {
      return false;
    }
    
    // check sync byte of first packet
    tmpFile.seek(0);
    if (tmpFile.read() != Constants.TS_SYNC_BYTE) {
      tmpFile.close();
      return false;
    }
    
    // check sync byte of second packet 
    if (tmpFile.length() >= (Constants.TS_PACKET_LENGTH*2)) {
      tmpFile.seek(Constants.TS_PACKET_LENGTH);
      if (tmpFile.read() != Constants.TS_SYNC_BYTE) {
        tmpFile.close();
        return false;
      }
    }
    
    // check sync byte of 20th packet
    if (tmpFile.length() >= (Constants.TS_PACKET_LENGTH*21)) {
      tmpFile.seek(Constants.TS_PACKET_LENGTH*20);
      if (tmpFile.read() != Constants.TS_SYNC_BYTE) {
        tmpFile.close();
        return false;
      }
    }
    
    // check sync byte of last packet
    tmpFile.seek(tmpFile.length() - Constants.TS_PACKET_LENGTH);
    if (tmpFile.read() != Constants.TS_SYNC_BYTE) {
      tmpFile.close();
      return false;
    }
    
    // success!
    tmpFile.close();
    return true;
  }

  /**
   * The file containing our stream.
   */
  private RandomAccessFile dataFile;
}
