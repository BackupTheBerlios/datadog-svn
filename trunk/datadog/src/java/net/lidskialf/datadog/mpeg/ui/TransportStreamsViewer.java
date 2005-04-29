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
package net.lidskialf.datadog.mpeg.ui;

import net.lidskialf.datadog.*;
import net.lidskialf.datadog.ui.*;
import net.lidskialf.datadog.mpeg.bitstream.*;

import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * StreamsViewer implementation customised for Transport Streams.
 *
 * @author Andrew de Quincey
 */
public class TransportStreamsViewer extends StreamsViewer {

    /**
     * Maximum zoom factor.
     */
    public static final int MAX_ZOOM_FACTOR = 10;

    /**
     * Block scroll increment for JScrollPane
     */
    public static final int BLOCK_SCROLL_INCREMENT = 0x100;

    /**
     * Unit scroll increment for JScrollPane
     */
    public static final int UNIT_SCROLL_INCREMENT = 16;



    /**
     * The stream we are viewing.
     */
    private TransportStream stream;

    /**
     * PID -> Substream instance.
     */
    private Map pidToSubstream = Collections.synchronizedMap(new HashMap());



    /**
     * Constructor.
     *
     * @param stream   The transport stream.
     * @param bookmarks Bookmarks for that stream.
     * @param substreams Substreams for that stream.
     * @throws IOException if there was a problem determining the length of
     *      other property of <code>stream</code>
     */
    public TransportStreamsViewer(TransportStream stream, StreamBookmarks bookmarks, Substreams substreams) throws IOException {
        super(bookmarks, substreams, MAX_ZOOM_FACTOR);
        this.stream = stream;

        setAbsoluteLength(stream.length());
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.ui.StreamWidget#paintStreamsPanel(java.awt.Graphics,
     *      int, int, long, long)
     */
    protected void paintStreamsPanel(Graphics g) {

        // calculate which area of the stream we need to redraw
        Rectangle clip = g.getClipBounds();
        int minStreamIdx = panelYPositionToStreamIndex(clip.y);
        int maxStreamIdx = panelYPositionToStreamIndex(clip.y + clip.height);

        try {
            long minStreamDrawPosition = panelXPositionToAbsolutePosition(clip.x);
            long maxStreamDrawPosition = minStreamDrawPosition + panelWidthToAbsoluteLength(clip.width);
            minStreamDrawPosition = stream.round(minStreamDrawPosition, TransportStream.ROUND_DOWN);
            maxStreamDrawPosition = stream.round(maxStreamDrawPosition, TransportStream.ROUND_UP);

            // render each packet
            for (long curPos = minStreamDrawPosition; curPos <= maxStreamDrawPosition; curPos += Constants.TS_PACKET_LENGTH) {
                // get the packet
                TransportPacket packet = stream.getPacketAt(curPos);
                if (packet == null)
                    continue;

                // find/create a row for the PID
                TransportSubstream substream = getSubstreamForPid(packet.pid());
                int index = substreams.indexOf(substream);

                // draw it if it is within the bounds
                if ((index >= minStreamIdx) && (index <= maxStreamIdx)) {
                    int x = absolutePositionToPanelXPosition(curPos);
                    int x2 = absolutePositionToPanelXPosition(curPos + Constants.TS_PACKET_LENGTH);
                    int y = streamIndexToPanelYPosition(index);
                    g.setColor(substream.getColour());
                    g.fillRect(x+1, y+1, x2 - x - 1, panelRowHeight-1);
                    g.setColor(Color.black);
                    g.drawRect(x, y, x2 - x, panelRowHeight);
                }
            }

            // draw the generic bits of the streams panel
            super.paintStreamsPanel(g, minStreamDrawPosition, maxStreamDrawPosition);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error during stream rendering (" + e.getMessage() + ")", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.ui.StreamsViewer#blockScrollIncrement(java.awt.Rectangle,
     *      int, int)
     */
    protected int blockScrollIncrement(Rectangle arg0, int arg1, int arg2) {
        return BLOCK_SCROLL_INCREMENT;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.ui.StreamsViewer#unitScrollIncrement(java.awt.Rectangle,
     *      int, int)
     */
    protected int unitScrollIncrement(Rectangle arg0, int arg1, int arg2) {
        return UNIT_SCROLL_INCREMENT;
    }

    /**
     * Find/allocate a substream for the specified PID.
     *
     * @param pid
     *            The PID concerned.
     * @return The TransportSubstream structure for that PID.
     */
    private TransportSubstream getSubstreamForPid(int pid) {

        // retrieve the current version if present
        Integer pidI = new Integer(pid);
        if (pidToSubstream.containsKey(pidI)) {
            return (TransportSubstream) pidToSubstream.get(pidI);
        }

        // create a new one and append it
        TransportSubstream newSubstream = new TransportSubstream(pid);
        addSubstream(newSubstream);
        pidToSubstream.put(pidI, newSubstream);
        return newSubstream;
    }


    /**
     * Substream tailored for transport streams.
     *
     * @author Andrew de Quincey
     */
    public class TransportSubstream extends Substream {

        /**
         * The PID of the substream.
         */
        protected int pid;

        /**
         * Constructor.
         *
         * @param pid The PID of the substream.
         */
        public TransportSubstream(int pid) {
            super("", Color.green, "", false);

            String tmp = Integer.toHexString(pid);
            while (tmp.length() < 4) {
                tmp = "0" + tmp;
            }

            setLabel("0x" + tmp);
            setDescription("0x" + tmp);
        }
    }
}
