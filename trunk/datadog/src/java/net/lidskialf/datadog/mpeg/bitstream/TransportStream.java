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
import net.lidskialf.datadog.*;

/**
 * This implements MPEG2 transport streams.
 *
 * @author Andrew de Quincey
 */
public class TransportStream {

    /**
     * Round packet position down to the previous packet.
     */
    public static final int ROUND_DOWN = 0;

    /**
     * Round packet position up to the next packet.
     */
    public static final int ROUND_UP = 1;

    /**
     * Position should be that of the next packet start (always rounds up).
     */
    public static final int ROUND_INC = 2;

    /**
     * The bitstream containing our stream.
     */
    private Bitstream bitstream;




    /**
     * Construct a parser for an mpeg2 transport stream.
     *
     * @param bitstream
     *            The Bitstream to access.
     * @throws IOException if there was problem probing <code>bitstream</code>
     */
    public TransportStream(Bitstream bitstream) throws IOException {
        // sanity check
        if (!probe(bitstream)) {
            throw new RuntimeException("Invalid bitstream: " + bitstream.toString());
        }

        this.bitstream = bitstream;
    }

    /**
     * Retrieve the nearest transport packet to the given position.
     *
     * @param position
     *            The position concerned.
     * @return The packet, or null if unavailable (i.e. end of stream).
     * @throws IOException
     *             On IO error.
     */
    public TransportPacket getPacketAt(long position) throws IOException {
        // check position is valid
        if ((position < 0) || ((position + Constants.TS_PACKET_LENGTH) > bitstream.length()))
            return null;

        // ok, read it!
        bitstream.seek(position);
        byte[] data = new byte[Constants.TS_PACKET_LENGTH];
        bitstream.readBlock(data);
        if (data[0] != Constants.TS_SYNC_BYTE)
            return null;
        TransportPacket packet = new TransportPacket(data, position);

        // done
        return packet;
    }

    /**
     * Round a given position within the stream to a transport packet.
     *
     * @param position
     *            The original position.
     * @param roundOp
     *            One of ROUND_* - controls how the rounding is done.
     * @return The rounded position.
     * @throws IOException
     *             On error.
     */
    public long round(long position, int roundOp) throws IOException {
        long newPos;
        switch (roundOp) {
        case ROUND_DOWN:
            newPos = (position / Constants.TS_PACKET_LENGTH) * Constants.TS_PACKET_LENGTH;
            break;

        case ROUND_UP:
            newPos = ((position + (Constants.TS_PACKET_LENGTH - 1)) / Constants.TS_PACKET_LENGTH) * Constants.TS_PACKET_LENGTH;
            break;

        case ROUND_INC:
            newPos = ((position + Constants.TS_PACKET_LENGTH) / Constants.TS_PACKET_LENGTH) * Constants.TS_PACKET_LENGTH;
            break;

        default:
            return 0;
        }

        if (newPos < 0)
            return 0;
        if (newPos > bitstream.length())
            return bitstream.length() - Constants.TS_PACKET_LENGTH;

        return newPos;
    }

    /**
     * Retrieve the length of the stream.
     *
     * @return The length.
     * @throws IOException
     *             On error.
     */
    public long length() throws IOException {
        return bitstream.length();
    }

    /**
     * Probe a Bitstream to see if we support it.
     *
     * @param bitstream
     *            The bitstream to probe.
     * @return True if we support it, false if not.
     * @throws IOException
     *             On error.
     */
    public static boolean probe(Bitstream bitstream) throws IOException {
        // must be at least one TS packet long
        if (bitstream.length() < Constants.TS_PACKET_LENGTH) {
            return false;
        }

        // must be divisible by TS_PACKET_LENGTH
        if ((bitstream.length() % Constants.TS_PACKET_LENGTH) != 0) {
            return false;
        }

        // check sync byte of first packet
        bitstream.seek(0);
        if (bitstream.readByte() != Constants.TS_SYNC_BYTE) {
            return false;
        }

        // check sync byte of second packet
        if (bitstream.length() >= (Constants.TS_PACKET_LENGTH * 2)) {
            bitstream.seek(Constants.TS_PACKET_LENGTH);
            if (bitstream.readByte() != Constants.TS_SYNC_BYTE) {
                return false;
            }
        }

        // check sync byte of 20th packet
        if (bitstream.length() >= (Constants.TS_PACKET_LENGTH * 21)) {
            bitstream.seek(Constants.TS_PACKET_LENGTH * 20);
            if (bitstream.readByte() != Constants.TS_SYNC_BYTE) {
                return false;
            }
        }

        // check sync byte of last packet
        bitstream.seek(bitstream.length() - Constants.TS_PACKET_LENGTH);
        if (bitstream.readByte() != Constants.TS_SYNC_BYTE) {
            return false;
        }

        // success!
        return true;
    }
}
