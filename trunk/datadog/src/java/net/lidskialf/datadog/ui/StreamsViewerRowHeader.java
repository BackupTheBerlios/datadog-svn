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
package net.lidskialf.datadog.ui;

import java.util.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import javax.swing.*;

import net.lidskialf.datadog.Substream;

/**
 * Generic Row Header for the StreamViewer.
 *
 * @author Andrew de Quincey
 */
public class StreamsViewerRowHeader extends JPanel implements StreamsViewerChangeListener, MouseMotionListener, MouseListener {

    /**
     * The StreamsViewer instance we are associated with.
     */
    protected StreamsViewer viewer;

    /**
     * Index of the selector when dragging a row.
     */
    protected int selectorIndex = -1;

    /**
     * Index of the row being moved.
     */
    protected int movingRow = -1;



    /**
     * Constructor.
     *
     * @param viewer
     *            The StreamsViewer we are associated with.
     */
    public StreamsViewerRowHeader(StreamsViewer viewer) {
        this.viewer = viewer;

        viewer.addStreamsViewerChangeListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        setPreferredSize(new Dimension(20, 0));
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#print(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // dimensions
        int rowHeight = viewer.substreamHeight();
        int rowWidth = getWidth() - 2;

        // work out what to redraw
        Rectangle clip = g.getClipBounds();
        int minStreamIdx = viewer.panelYPositionToStreamIndex(clip.y);
        int maxStreamIdx = viewer.panelYPositionToStreamIndex(clip.y + clip.height + (rowHeight - 1));

        // redraw it!
        int y = minStreamIdx * rowHeight;
        for (int i = minStreamIdx; i <= maxStreamIdx; i++) {
            // get the Substream instance if it exists
            Substream substream = viewer.getSubstream(i);
            if (substream == null) {
                continue;
            }

            // draw the cell
            g.setColor(substream.getColour());
            g.fillRect(1, y+1, rowWidth-1, rowHeight-1);
            g.setColor(Color.black);
            g.drawRect(0, y, rowWidth, rowHeight);

            // draw the text
            g.drawString(substream.getLabel(), 1, y + rowHeight-1);

            // next row
            y += rowHeight;
        }

        // draw the selector if present
        if ((selectorIndex != -1) && (minStreamIdx <= selectorIndex) && (maxStreamIdx >= selectorIndex)) {
            g.setColor(Color.red);
            g.drawLine(0, selectorIndex * rowHeight, rowWidth, selectorIndex * rowHeight);
        }
    }

    /**
     * Repaint the current selector position.
     */
    protected void repaintSelector() {
        if (selectorIndex != -1) {
            repaint(0, selectorIndex * viewer.substreamHeight(), getWidth(), (selectorIndex * viewer.substreamHeight())+1);
        }
    }

    /**
     * Update the dimensions of the row header when the list of rows changes.
     */
    protected void updateDimensions() {
        Graphics g = getGraphics();
        if (g == null)
            return;

        // work out the minimum width
        int minWidth = 0;
        FontMetrics fontMetrics = g.getFontMetrics();
        Iterator it = viewer.getSubstreams();
        while(it.hasNext()) {
            Substream substream = (Substream) it.next();

            Rectangle2D bounds = fontMetrics.getStringBounds(substream.getLabel(), g);
            if (bounds.getWidth() > minWidth) {
                minWidth = (int) Math.round(bounds.getWidth());
            }
        }

        // set the row height
        viewer.setSubstreamHeight(13); // FIXME: hardcoded just now 'cos fontmetrics return ridiculously large values for font height

        // set the width
        Dimension curSize = getPreferredSize();
        curSize.width = minWidth + 3;
        setPreferredSize(curSize);
        revalidate();
        repaint();
    }


    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#bookmarkAdded(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void bookmarkAdded(StreamsViewerChangeEvent e) {
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#bookmarkChanged(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void bookmarkChanged(StreamsViewerChangeEvent e) {
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#bookmarkMoved(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void bookmarkMoved(StreamsViewerChangeEvent e) {
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#bookmarkRemoved(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void bookmarkRemoved(StreamsViewerChangeEvent e) {
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#lengthChanged(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void lengthChanged(StreamsViewerChangeEvent e) {
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#zoomChanged(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void zoomChanged(StreamsViewerChangeEvent e) {
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#substreamAdded(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void substreamAdded(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#substreamChanged(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void substreamChanged(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#substreamRemoved(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void substreamRemoved(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#substreamMoved(net.lidskialf.datadog.ui.StreamsViewerChangeEvent)
     */
    public void substreamMoved(StreamsViewerChangeEvent e) {
        repaint();
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
        if (selectorIndex != -1) {
            int tmpSelectorIndex = viewer.panelYPositionToStreamIndex(e.getY());
            if (tmpSelectorIndex <= viewer.substreamsCount()) {
                repaintSelector();
                selectorIndex = tmpSelectorIndex;
                repaintSelector();
            }
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            selectorIndex = viewer.panelYPositionToStreamIndex(e.getY());
            if (selectorIndex >= viewer.substreamsCount()) {
                selectorIndex = -1;
                return;
            }

            movingRow = selectorIndex;
            repaintSelector();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        if (selectorIndex != -1) {
            viewer.moveSubstream(movingRow, selectorIndex);

            repaintSelector();
            selectorIndex = -1;
        }
    }
}
