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

import net.lidskialf.datadog.ui.*;
import net.lidskialf.datadog.mpeg.bitstream.*;

import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * @author Andrew de Quincey
 *  
 */
public class TransportStreamsViewer extends StreamsViewer {

    /**
     * Constructor.
     */
    public TransportStreamsViewer(TransportStream stream, DefaultListModel rowModel) throws IOException {
        super();
        this.rowModel = rowModel;
        this.stream = stream;

        // hardcoded "good" value.
        maxZoomFactor = 5;

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
        int minStreamIdx = panelYPositionToStreamIndex(clip.y, SEPARATOR_PARTOF_STREAM_BELOW_IT);
        int maxStreamIdx = panelYPositionToStreamIndex(clip.y + clip.height, SEPARATOR_PARTOF_STREAM_ABOVE_IT);

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
                int pid = packet.pid();
                TransportStreamRow row = getRowForPid(pid);

                // draw it if it is within the bounds
                if ((row.rowIdx >= minStreamIdx) && (row.rowIdx <= maxStreamIdx)) {
                    int x = absolutePositionToPanelXPosition(curPos);
                    int x2 = absolutePositionToPanelXPosition(curPos + Constants.TS_PACKET_LENGTH);
                    int y = streamIndexToPanelYPosition(row.rowIdx);
                    g.setColor(Color.green);
                    g.fillRect(x+1, y+1, x2 - x - 1, panelRowHeight-1);
                    g.setColor(Color.black);
                    g.drawRect(x, y, x2 - x, panelRowHeight);
                }
            }

            // draw the selector
            paintSelector(g, minStreamDrawPosition, maxStreamDrawPosition);
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
        return 0x100;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.lidskialf.datadog.ui.StreamsViewer#unitScrollIncrement(java.awt.Rectangle,
     *      int, int)
     */
    protected int unitScrollIncrement(Rectangle arg0, int arg1, int arg2) {
        return 16;
    }

    /**
     * Find/allocate a row for the specified PID.
     * 
     * @param pid
     *            The PID concerned.
     * @return A TransportStreamRow structure for that PID.
     */
    private TransportStreamRow getRowForPid(int pid) {

        // retrieve the old version if present
        Integer pidI = new Integer(pid);
        if (pidToRowDescriptor.containsKey(pidI)) {
            return (TransportStreamRow) pidToRowDescriptor.get(pidI);
        }

        // create a new one
        TransportStreamRow newDesc = new TransportStreamRow(pid);

        // find where to insert it
        boolean inserted = false;
        for (int i = 0; i < rowModel.getSize(); i++) {
            TransportStreamRow curDesc = (TransportStreamRow) rowModel.getElementAt(i);

            // found a pid greater than our current one? insert it!
            if (!inserted) {
                if (curDesc.pid > newDesc.pid) {
                    newDesc.rowIdx = i;
                    rowModel.add(i, newDesc);
                    pidToRowDescriptor.put(pidI, newDesc);
                    inserted = true;
                    i++;
                    curDesc.rowIdx = i;
                }
            } else {
                // update all row indexes after the inserted position
                curDesc.rowIdx = i;
            }
        }

        // if we didn't insert it anywhere, append it
        if (!inserted) {
            newDesc.rowIdx = rowModel.getSize();
            rowModel.addElement(newDesc);
            pidToRowDescriptor.put(pidI, newDesc);
        }

        return newDesc;
    }

    /**
     * A Row tailored for transport streams.
     * 
     * @author Andrew de Quincey
     */
    private class TransportStreamRow {

        /**
         * Constructor.
         * 
         * @param pid
         *            PID this row is representing.
         */
        public TransportStreamRow(int pid) {
            String tmp = Integer.toHexString(pid);
            while (tmp.length() < 4) {
                tmp = "0" + tmp;
            }
            description = "0x" + tmp;
            this.pid = pid;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return description;
        }

        private String description;

        /**
         * The PID of the row.
         */
        public int pid;

        /**
         * Index of the row containing the pid.
         */
        public int rowIdx;
    }

    /**
     * The stream we are viewing.
     */
    private TransportStream stream;

    /**
     * PID -> row descriptor.
     */
    private Map pidToRowDescriptor = Collections.synchronizedMap(new HashMap());

    /**
     * Model of the rows of this viewer.
     */
    private DefaultListModel rowModel;
}
