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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * A generic ColumnHeader for a StreamsViewer.
 *
 * @author Andrew de Quincey
 */
public class StreamsViewerColumnHeader extends JPanel implements StreamsViewerChangeListener, MouseListener, MouseMotionListener {

    /**
     * Constructor.
     *
     * @param viewer                   the associated StreamsViewer
     * @param nominalMinorTickSpacing  spacing between minor ticks
     * @param nominalMajorTickSpacing  spacing between major ticks
     */
    public StreamsViewerColumnHeader(StreamsViewer viewer, int nominalMinorTickSpacing, int nominalMajorTickSpacing) {
        this.viewer = viewer;
        this.nominalMinorTickSpacing = nominalMinorTickSpacing;
        this.nominalMajorTickSpacing = nominalMajorTickSpacing;
        viewer.addStreamsViewerChangeListener(this);

        addMouseListener(this);
        addMouseMotionListener(this);

        setPreferredSize(new Dimension(0, 20));
        updateDimensions();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#lengthChanged(net.lidskialf.datadog.ui.StreamsViewer,
     *      long)
     */
    public void lengthChanged(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#zoomChanged(net.lidskialf.datadog.ui.StreamsViewer,
     *      int)
     */
    public void zoomChanged(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#bookmarkMoved(long, long)
     */
    public void bookmarkMoved(StreamsViewerChangeEvent e) {
        int oldX = viewer.absolutePositionToPanelXPosition(e.oldBookmarkPosition);
        int newX = viewer.absolutePositionToPanelXPosition(e.bookmarkPosition);
        repaint(oldX - bookmarkRadius, 0, bookmarkRadius<<1, getHeight());
        repaint(newX - bookmarkRadius, 0, bookmarkRadius<<1, getHeight());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(MouseEvent arg0) {
        if (selectedBookmark != -1) {
            long newPosition = viewer.panelXPositionToAbsolutePosition(arg0.getX());
            viewer.moveBookmark(selectedBookmark, newPosition);
            selectedBookmark = newPosition;
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
        long minAbsolutePosition = viewer.panelXPositionToAbsolutePosition(arg0.getX() - bookmarkRadius);
        long maxAbsolutePosition = viewer.panelXPositionToAbsolutePosition(arg0.getX() + bookmarkRadius);

        // try all possibilities
        Iterator it = viewer.getBookmarkKeys(minAbsolutePosition, maxAbsolutePosition);
        while(it.hasNext()) {
            long curBookmark = ((Long) it.next()).longValue();

            // this this bookmark within range of the click?
            if ((minAbsolutePosition <= curBookmark) && (maxAbsolutePosition >= curBookmark)) {
                selectedBookmark = curBookmark;
                return;
            }
        }

        // no bookmark is selected
        selectedBookmark = -1;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
        selectedBookmark = -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent arg0) {
        long newSelectorPos = viewer.panelXPositionToAbsolutePosition(arg0.getX());

        if ((selectedBookmark == -1) && (newSelectorPos != absoluteSelectorPos)) {
            long oldSelectorPos = absoluteSelectorPos;
            absoluteSelectorPos = newSelectorPos;

            if (oldSelectorPos != -1)
                repaint(viewer.absolutePositionToPanelXPosition(oldSelectorPos), 0, 1, getHeight());
            repaint(viewer.absolutePositionToPanelXPosition(absoluteSelectorPos), 0, 1, getHeight());

            setToolTipText(renderStreamPosition(newSelectorPos));

            viewer.updateSelector(absoluteSelectorPos);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // calculate what to draw
        Rectangle clip = g.getClipBounds();
        long minStreamDrawPosition = viewer.panelXPositionToAbsolutePosition(clip.x);
        long maxStreamDrawPosition = minStreamDrawPosition + viewer.panelWidthToAbsoluteLength(clip.width);

        // round the min position down to the nearest major tick
        minStreamDrawPosition = (minStreamDrawPosition / curMajorTickSpacing) * curMajorTickSpacing;

        // round the max position up to the nearest major tick
        maxStreamDrawPosition = ((maxStreamDrawPosition + curMajorTickSpacing) / curMajorTickSpacing) * curMajorTickSpacing;

        // draw the ticks
        g.setColor(Color.black);
        for (long pos = minStreamDrawPosition; pos <= maxStreamDrawPosition; pos += curMinorTickSpacing) {
            int x = viewer.absolutePositionToPanelXPosition(pos);

            // determine the kind of tick we need to draw
            if ((pos % curMajorTickSpacing) == 0) {
                // draw major tick
                g.drawLine(x, 11, x, 20);

                // render the position string and draw it somewhere
                String rendered = renderStreamPosition(pos);
                int width = g.getFontMetrics().stringWidth(rendered);
                x -= (width / 2);
                if (x < 0)
                    x = 0;
                if (x > panelWidth)
                    x = panelWidth - width;
                g.drawString(rendered, x, 10);
            } else {
                g.drawLine(x, 15, x, 20);
            }
        }

        // draw the selector.
        if ((minStreamDrawPosition <= absoluteSelectorPos) && (maxStreamDrawPosition >= absoluteSelectorPos)) {
            int xpos = viewer.absolutePositionToPanelXPosition(absoluteSelectorPos);
            g.setColor(Color.blue);
            g.drawLine(xpos, 0, xpos, getHeight());
        }

        // paint the bookmarks if they're supported
        if (viewer.bookmarksSupported()) {
            Iterator it = viewer.getBookmarkKeys(minStreamDrawPosition, maxStreamDrawPosition);
            while(it.hasNext()) {
                Long curBookmark = (Long) it.next();
                int xpos = viewer.absolutePositionToPanelXPosition(curBookmark.longValue());
                g.setColor(Color.orange);
                g.fillOval(xpos-bookmarkRadius, 10, bookmarkRadius<<1, bookmarkRadius<<1);
            }
        }
    }

    /**
     * Update the dimensions of the column header.
     */
    protected void updateDimensions() {
        panelWidth = viewer.getTotalPanelWidth();
        Dimension curSize = getPreferredSize();
        curSize.width = panelWidth;
        setPreferredSize(curSize);

        curMinorTickSpacing = nominalMinorTickSpacing << viewer.getCurZoomFactor();
        curMajorTickSpacing = nominalMajorTickSpacing << viewer.getCurZoomFactor();

        repaint();
    }

    /**
     * Render a position within the stream for display to the user.
     *
     * @param position
     *            The position to render.
     * @return The string to display.
     */
    protected String renderStreamPosition(long position) {
        return "0x" + Long.toHexString(position);
    }

    /**
     * The StreamsViewer we are associated with.
     */
    protected StreamsViewer viewer;

    /**
     * The width of the stream panel window in pixels.
     */
    protected int panelWidth;

    /**
     * Nominal (at 1:1 zoom) spacing between minor ticks.
     */
    protected long nominalMinorTickSpacing;

    /**
     * Nominal (at 1:1 zoom) spacing between major ticks.
     */
    protected long nominalMajorTickSpacing;

    /**
     * Minor tick spacing at current zoom level.
     */
    protected long curMinorTickSpacing;

    /**
     * Major tick spacing at current zoom level.
     */
    protected long curMajorTickSpacing;

    /**
     * Current absolute position of the selector marker.
     */
    protected long absoluteSelectorPos = -1;

    /**
     * Radius in pixels of a bookmark.
     */
    protected int bookmarkRadius = 5;

    /**
     * The currently selected bookmark or -1 if none.
     */
    protected long selectedBookmark = -1;
}
