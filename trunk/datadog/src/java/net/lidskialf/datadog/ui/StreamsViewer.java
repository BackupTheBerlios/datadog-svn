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

import net.lidskialf.datadog.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * The base StreamsViewer implementation.
 *
 * @author Andrew de Quincey
 */
public abstract class StreamsViewer extends JScrollPane {

    /**
     * The absolute length of the stream.
     */
    protected long absoluteLength = 0;

    /**
     * Absolute position of the selector marker.
     */
    protected long absoluteSelectorPos = -1;

    /**
     * The zoom factor applied to the absolute stream positions.
     */
    protected int curZoomFactor = 0;

    /**
     * The minimum permitted zoom factor (auto-chosen to avoid integer overflows for
     * very long streams).
     */
    protected int minZoomFactor = 0;

    /**
     * The maximum permitted zoom factor.
     */
    protected int maxZoomFactor = 20;

    /**
     * Total width of the panel window in pixels.
     */
    protected int panelWidth = 0;

    /**
     * The height of a row in pixels.
     */
    protected int panelRowHeight = 10;

    /**
     * The stream viewer component instance.
     */
    protected StreamsPanel panel;

    /**
     * List of registered StreamViewerChangeListeners.
     */
    protected java.util.List changeListeners = Collections.synchronizedList(new ArrayList());

    /**
     * Bookmarks known to this stream.
     */
    protected StreamBookmarks bookmarks;

    /**
     * Flag indicating a bookmark is being moved.
     */
    protected StreamBookmark movingBookmark = null;

    /**
     * List of substreams within the stream.
     */
    protected Substreams substreams;



    /**
     * Constructor.
     *
     * @param bookmarks StreamBookmarks instance for this stream, or null if no implementation desired.
     * @param substreams Substreams for this stream.
     * @param maxZoomFactor Max zoom factor for this stream.
     */
    public StreamsViewer(StreamBookmarks bookmarks, Substreams substreams, int maxZoomFactor) {
        super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.bookmarks = bookmarks;
        this.substreams = substreams;
        this.maxZoomFactor = maxZoomFactor;

        panel = new StreamsPanel();
        getViewport().add(panel);
        panel.setBackground(Color.white);
        panel.setBorder(null);

        setBorder(null);
        setViewportBorder(null);
        setPreferredSize(new Dimension(600, 200));
    }

    /**
     * Add a StreamsViewerChangeListener to receive events from this
     * StreamsViewer.
     *
     * @param listener
     *            The listener to add.
     */
    public void addStreamsViewerChangeListener(StreamsViewerChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * Remove a StreamsViewerChangeListener so it no longer receives events from
     * this StreamsViewer.
     *
     * @param listener
     *            The listener to remove.
     */
    public void removeStreamsViewerChangeListener(StreamsViewerChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * Determine the Y position a stream with index streamIdx will be displayed
     * at.
     *
     * @param streamIdx
     *            The index of the stream.
     * @return The Y position of the top of the stream's rendering position.
     */
    public int streamIndexToPanelYPosition(int streamIdx) {
        return streamIdx * panelRowHeight;
    }

    /**
     * Determine the streamIdx that a y position corresponds to.
     *
     * @param yPos
     *            The y position.
     * @return The stream index, or -1 for an invalid y position.
     */
    public int panelYPositionToStreamIndex(int yPos) {
        return yPos / panelRowHeight;
    }

    /**
     * Convert an X position in the window into a real position within the
     * stream.
     *
     * @param x
     *            The X position.
     * @return The stream position.
     */
    public long panelXPositionToAbsolutePosition(int x) {
        return x << curZoomFactor;
    }

    /**
     * Convert a real position within the stream into an X position in the
     * window.
     *
     * @param position
     *            The X position.
     * @return The stream position.
     */
    public int absolutePositionToPanelXPosition(long position) {
        return (int) ((position) >> curZoomFactor);
    }

    /**
     * Convert an width in the window into a real length of an area within the
     * stream.
     *
     * @param width
     *            The width.
     * @return The length of the area within the stream..
     */
    public long panelWidthToAbsoluteLength(int width) {
        return width << curZoomFactor;
    }

    /**
     * Convert a real length of an area within the stream into a width in the
     * window.
     *
     * @param width
     *            The width.
     * @return The length of the area within the stream..
     */
    public int absoluteLengthToPanelWidth(long width) {
        return (int) (width >> curZoomFactor);
    }

    /**
     * The total width of the entire StreamsViewer panel.
     *
     * @return The width in pixels.
     */
    public int getTotalPanelWidth() {
        return panelWidth;
    }

    /**
     * Accessor for the current zoom factor.
     *
     * @return The current zoom factor.
     */
    public int getCurZoomFactor() {
        return curZoomFactor;
    }


    /**
     * Accessor for the total stream length.
     *
     * @return The length.
     */
    public long getStreamLength() {
        return absoluteLength;
    }

    /**
     * Tell the StreamsViewer to zoom in one step.
     */
    public void zoomIn() {
        if (curZoomFactor == minZoomFactor)
            return;

        Rectangle viewRect = getViewport().getViewRect();
        long midPosition = panelXPositionToAbsolutePosition(viewRect.x + (viewRect.width/2));

        curZoomFactor--;
        updateDimensions();
        centreView(midPosition);
        panel.repaint();
        fireChangeListeners(StreamsViewerChangeEvent.zoomChanged(this, curZoomFactor));
    }

    /**
     * Tell the StreamsViewer to zoom out one step.
     */
    public void zoomOut() {
        if (curZoomFactor == maxZoomFactor)
            return;

        Rectangle viewRect = getViewport().getViewRect();
        long midPosition = panelXPositionToAbsolutePosition(viewRect.x + (viewRect.width/2));

        curZoomFactor++;
        updateDimensions();
        centreView(midPosition);
        panel.repaint();
        fireChangeListeners(StreamsViewerChangeEvent.zoomChanged(this, curZoomFactor));
    }

    /**
     * Update the position of the selector.
     *
     * @param newSelectorPos
     *            The absolute new position of the selector.
     */
    public void setSelectorPosition(long newSelectorPos) {
        if (newSelectorPos != absoluteSelectorPos) {
            long oldSelectorPos = absoluteSelectorPos;
            absoluteSelectorPos = newSelectorPos;

            if (oldSelectorPos != -1)
                panel.repaint(absolutePositionToPanelXPosition(oldSelectorPos), 0, 1, getHeight());
            panel.repaint(absolutePositionToPanelXPosition(absoluteSelectorPos), 0, 1, getHeight());
        }
    }

    /**
     * Centre the view about an absolute stream position.
     *
     * @param position The new position.
     */
    public void centreView(long position) {
        Dimension d = getViewport().getExtentSize();
        Point pos = getViewport().getViewPosition();

        pos.x = absolutePositionToPanelXPosition(position - panelWidthToAbsoluteLength(d.width/2));
        getViewport().setViewPosition(pos);
    }

    /**
     * Does this viewer support bookmarks?
     *
     * @return True if it does, false if not.
     */
    public boolean bookmarksSupported() {
        return bookmarks != null;
    }

    /**
     * Get the keys of bookmarks between two absolute stream positions.
     *
     * @param minAbsolutePos Minimum position.
     * @param maxAbsolutePos Maximum position (inclusively).
     * @return Iterator of Long objects, each giving the absolute stream position of a bookmark.
     */
    public Iterator getBookmarkKeys(long minAbsolutePos, long maxAbsolutePos) {
        return bookmarks.getKeys(minAbsolutePos, maxAbsolutePos);
    }

    /**
     * Move the bookmark at curPosition to newPosition.
     *
     * @param curPosition The current position of the bookmark.
     * @param newPosition The new position of the bookmark.
     */
    public void moveBookmark(long curPosition, long newPosition) {
        // don't bother if the bookmark was left where it started
        if (curPosition == newPosition) return;

        // if we're going to clobber one, ask the user first!
        if (bookmarks.contains(newPosition)) {
            if (JOptionPane.showConfirmDialog(null,
                                              "Moving this bookmark here will remove the existing bookmark at this position. Are you sure you wish to do this?",
                                              "Move bookmark?",
                                              JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                return;
            }
        }

        // only redraw if the move is successful.
        if (bookmarks.move(curPosition, newPosition)) {
            int oldX = absolutePositionToPanelXPosition(curPosition);
            int newX = absolutePositionToPanelXPosition(newPosition);

            panel.repaint(oldX, 0, 1, getHeight());
            panel.repaint(newX, 0, 1, getHeight());

            fireChangeListeners(StreamsViewerChangeEvent.bookmarkMoved(this, curPosition, newPosition));
        }
    }

    /**
     * Get the bookmark at a given position.
     *
     * @param position The position.
     * @return The StreamBookmark, or null if no bookmark was present.
     */
    public StreamBookmark getBookmark(long position) {
        return bookmarks.get(position);
    }

    /**
     * Remove the bookmark at a given position.
     *
     * @param position The position.
     */
    public void removeBookmark(long position) {
        int x = absolutePositionToPanelXPosition(position);

        bookmarks.remove(position);

        panel.repaint(x, 0, 1, getHeight());

        fireChangeListeners(StreamsViewerChangeEvent.bookmarkRemoved(this, position));
    }

    /**
     * Add the bookmark at a given position.
     *
     * @param position The position.
     * @param bookmark The bookmark.
     */
    public void addBookmark(long position, StreamBookmark bookmark) {
        int x = absolutePositionToPanelXPosition(position);

        bookmarks.add(position, bookmark);

        panel.repaint(x, 0, 1, getHeight());

        fireChangeListeners(StreamsViewerChangeEvent.bookmarkAdded(this, position));
    }

    /**
     * Set the flag to indicate a bookmark is being moved.
     *
     * @param bookmark The StreamBookmark if moving in progress, or null when move is finished.
     */
    public void setMovingBookmark(StreamBookmark bookmark) {
        movingBookmark = bookmark;
    }

    /**
     * Informs the viewer that the contents of a bookmark have changed.
     *
     * @param position Position of the bookmark which has changed.
     */
    public void bookmarkModified(long position) {
        int x = absolutePositionToPanelXPosition(position);

        panel.repaint(x, 0, 1, getHeight());

        fireChangeListeners(StreamsViewerChangeEvent.bookmarkChanged(this, position));
    }

    /**
     * Returns the number of substreams known to the viewer.
     *
     * @return Number of rows.
     */
    public int substreamsCount() {
        return substreams.size();
    }

    /**
     * Get details of a specific row in the streams viewer.
     *
     * @param index Index of the requested substream.
     * @return The row, or null if it does not exist.
     */
    public Substream getSubstream(int index) {
        return substreams.get(index);
    }

    /**
     * Get an iterator of all substreams.
     *
     * @return The iterator of all substreams in order.
     */
    public Iterator getSubstreams() {
        return substreams.get();
    }

    /**
     * Append a substream to the list.
     *
     * @param substream The substream to add.
     */
    public void addSubstream(Substream substream) {
        int index = substreams.add(substream);
        updateDimensions();
        panel.repaint();
        fireChangeListeners(StreamsViewerChangeEvent.substreamAdded(this, index));
    }

    /**
     * Insert a substream into the list.
     *
     * @param index Index to insert at.
     * @param substream The substream to add.
     */
    public void addSubstream(int index, Substream substream) {
        substreams.add(index, substream);
        updateDimensions();
        panel.repaint();
        fireChangeListeners(StreamsViewerChangeEvent.substreamAdded(this, index));
    }

    /**
     * Vertical space allocated to a substream.
     *
     * @return The vertical space in pixels.
     */
    public int substreamHeight() {
        return panelRowHeight;
    }

    /**
     * Set the height of a substream.
     *
     * @param height The height in pixels.
     */
    public void setSubstreamHeight(int height) {
        panelRowHeight = height;
        panel.repaint();
    }

    /**
     * Move a substream from one position to another.
     *
     * @param start Starting index of substream.
     * @param dest Destination index of substream.
     */
    public void moveSubstream(int start, int dest) {
        substreams.move(start, dest);
        panel.repaint();
        fireChangeListeners(StreamsViewerChangeEvent.substreamMoved(this, start, dest));
    }

    /**
     * Inform the viewer that a substream has changed.
     *
     * @param index
     */
    public void substreamModified(int index) {
        panel.repaint();
        fireChangeListeners(StreamsViewerChangeEvent.substreamChanged(this, index));
    }






    /**
     * Fires all change listener events for the given changeType.
     *
     * @param e The StreamsViewerChangeEvent describing the event.
     */
    protected void fireChangeListeners(StreamsViewerChangeEvent e) {
        Iterator it = changeListeners.iterator();
        while (it.hasNext()) {
            StreamsViewerChangeListener curListener = (StreamsViewerChangeListener) it.next();

            switch (e.changeType) {
            case StreamsViewerChangeEvent.CHANGE_ZOOM:
                curListener.zoomChanged(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_LENGTH:
                curListener.lengthChanged(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_MOVEBOOKMARK:
                curListener.bookmarkMoved(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_CHANGEBOOKMARK:
                curListener.bookmarkChanged(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_REMOVEBOOKMARK:
                curListener.bookmarkRemoved(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_ADDBOOKMARK:
                curListener.bookmarkAdded(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_ADDSUBSTREAM:
                curListener.substreamAdded(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_CHANGESUBSTREAM:
                curListener.substreamChanged(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_REMOVESUBSTREAM:
                curListener.substreamRemoved(e);
                break;

            case StreamsViewerChangeEvent.CHANGE_MOVESUBSTREAM:
                curListener.substreamMoved(e);
                break;
            }
        }
    }

    /**
     * Set the absolute length of the stream.
     *
     * @param absoluteLength
     *            The absolute length of the stream.
     */
    protected void setAbsoluteLength(long absoluteLength) {
        // update the values
        this.absoluteLength = absoluteLength;

        // now choose a minimum zoom factor to avoid integer overflow
        minZoomFactor = 0;
        while (absoluteLength > Integer.MAX_VALUE) {
            minZoomFactor++;
            absoluteLength >>= 1;
        }
        curZoomFactor = minZoomFactor;

        // update everything else
        updateDimensions();
        fireChangeListeners(StreamsViewerChangeEvent.lengthChanged(this, absoluteLength));
    }

    /**
     * Update the dimensions of the streams window (for example when zoom factor
     * changes).
     */
    protected void updateDimensions() {
        // update the width of the panel
        panelWidth = (int) (absoluteLength >> curZoomFactor);
        Dimension curSize = panel.getPreferredSize();
        curSize.width = panelWidth;
        panel.setPreferredSize(curSize);
        panel.revalidate();
    }

    /**
     * Calculate the current scroll unit increment.
     *
     * @param visibleRect the visible rectangle
     * @param orientation the orientation
     * @param direction   the direction
     * @return the increment
     */
    protected abstract int unitScrollIncrement(Rectangle visibleRect, int orientation, int direction);

    /**
     * Calculate the current scroll block increment.
     *
     * @param visibleRect the visible rectangle
     * @param orientation the orientation
     * @param direction   the direction
     * @return the increment
     */
    protected abstract int blockScrollIncrement(Rectangle visibleRect, int orientation, int direction);

    /**
     * Repaint a bit of the streams panel
     *
     * @param g
     *            The Graphics context.
     */
    protected abstract void paintStreamsPanel(Graphics g);

    /**
     * Paint the generic bits of the StreamsPanel onto a graphics context, as long as it lies within
     * the specified minimum and maximum values. Should be called after any stream-specific painting.
     *
     * @param g                       the Graphics context to paint onto.
     * @param minAbsoluteStreamPosition the minimum absolute stream position.
     * @param maxAbsoluteStreamPosition the maximum absolute stream position (inclusively).
     */
    protected void paintStreamsPanel(Graphics g, long minAbsoluteStreamPosition, long maxAbsoluteStreamPosition) {
        // paint the selector
        if ((absoluteSelectorPos >= minAbsoluteStreamPosition) && (absoluteSelectorPos <= maxAbsoluteStreamPosition)) {
            int xpos = absolutePositionToPanelXPosition(absoluteSelectorPos);
            if (movingBookmark == null) {
                g.setColor(Color.blue);
            } else {
                g.setColor(movingBookmark.getColour());
            }
            g.drawLine(xpos, 0, xpos, getHeight());
        }

        // paint the bookmarks if they're supported
        if (bookmarks != null) {
            Iterator it = bookmarks.getKeys(minAbsoluteStreamPosition, maxAbsoluteStreamPosition);
            while(it.hasNext()) {
                Long position = (Long) it.next();
                StreamBookmark curBookmark = bookmarks.get(position.longValue());
                int xpos = absolutePositionToPanelXPosition(position.longValue());
                g.setColor(curBookmark.getColour());
                g.drawLine(xpos, 0, xpos, getHeight());
            }
        }
    }

    /**
     * The scrollable display for streams.
     *
     * @author Andrew de Quincey
     */
    protected class StreamsPanel extends JPanel implements Scrollable {

        /* (non-Javadoc)
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintStreamsPanel(g);
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
         */
        public Dimension getPreferredScrollableViewportSize() {
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
         *      int, int)
         */
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return blockScrollIncrement(visibleRect, orientation, direction);
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
         */
        public boolean getScrollableTracksViewportHeight() {
            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
         */
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
         *      int, int)
         */
        public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
            return unitScrollIncrement(arg0, arg1, arg2);
        }
    }
}
