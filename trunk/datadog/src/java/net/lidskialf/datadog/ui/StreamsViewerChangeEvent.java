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

/**
 * Class communicating information about a change to a StreamsViewer.
 *
 * @author Andrew de Quincey
 */
public class StreamsViewerChangeEvent {

    /**
     * Zoom factor has changed.
     */
    public static final int CHANGE_ZOOM = 0;

    /**
     * Absolute length has changed.
     */
    public static final int CHANGE_LENGTH = 1;

    /**
     * A bookmark was moved.
     */
    public static final int CHANGE_MOVEBOOKMARK = 2;

    /**
     * A bookmark was changed (but not moved).
     */
    public static final int CHANGE_CHANGEBOOKMARK = 3;

    /**
     * A bookmark was removed.
     */
    public static final int CHANGE_REMOVEBOOKMARK = 4;

    /**
     * A bookmark was added.
     */
    public static final int CHANGE_ADDBOOKMARK = 5;

    /**
     * A substream was added.
     */
    public static final int CHANGE_ADDSUBSTREAM = 6;

    /**
     * A substream was changed.
     */
    public static final int CHANGE_CHANGESUBSTREAM = 7;

    /**
     * A substream was removed.
     */
    public static final int CHANGE_REMOVESUBSTREAM = 8;

    /**
     * A substream was moved.
     */
    public static final int CHANGE_MOVESUBSTREAM = 9;


    /**
     * The StreamsViewer which this event concerns.
     */
    public StreamsViewer viewer;

    /**
     * Type of change - one of the CHANGED_* values.
     */
    public int changeType;

    /**
     * The new zoom factor on a CHANGED_ZOOM event.
     */
    public int zoomFactor;

    /**
     * The length of the stream on a CHANGED_LENGTH event.
     */
    public long length;

    /**
     * The old bookmark absolute position on a CHANGE_MOVEBOOKMARK event.
     */
    public long oldBookmarkPosition;

    /**
     * The bookmark absolute position for a bookmark related event.
     */
    public long bookmarkPosition;

    /**
     * The old substream index for a CHANGE_MOVESUBSTREAM event.
     */
    public int oldSubstreamIndex;

    /**
     * The substream index for a substream related event.
     */
    public int substreamIndex;


    /**
     * Constructor.
     *
     * @param viewer The associated StreamsViewer.
     * @param changeType Type of change - one of the CHANGED_* values.
     */
    public StreamsViewerChangeEvent(StreamsViewer viewer, int changeType) {
        this.viewer = viewer;
        this.changeType = changeType;
    }


    /**
     * Create an object indicating the zoom factor was changed.
     *
     * @param viewer The associated StreamsViewer.
     * @param zoomFactor The new zoom factor.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent zoomChanged(StreamsViewer viewer, int zoomFactor) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_ZOOM);
        tmp.zoomFactor = zoomFactor;
        return tmp;
    }

    /**
     * Create an object indicating the stream length changed.
     *
     * @param viewer The associated StreamsViewer.
     * @param length The new length.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent lengthChanged(StreamsViewer viewer, long length) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_LENGTH);
        tmp.length = length;
        return tmp;
    }

    /**
     * Create an object indicating a bookmark was moved.
     *
     * @param viewer The associated StreamsViewer.
     * @param oldPosition The old position of the bookmark.
     * @param newPosition The new position of the bookmark.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent bookmarkMoved(StreamsViewer viewer, long oldPosition, long newPosition) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_MOVEBOOKMARK);
        tmp.oldBookmarkPosition = oldPosition;
        tmp.bookmarkPosition = newPosition;
        return tmp;
    }

    /**
     * Create an object indicating a bookmark was changed.
     *
     * @param viewer The associated StreamsViewer.
     * @param position Position of the bookmark that changed.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent bookmarkChanged(StreamsViewer viewer, long position) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_CHANGEBOOKMARK);
        tmp.bookmarkPosition = position;
        return tmp;
    }

    /**
     * Create an object indicating a bookmark was removed.
     *
     * @param viewer The associated StreamsViewer.
     * @param position Position of the bookmark that was removed.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent bookmarkRemoved(StreamsViewer viewer, long position) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_REMOVEBOOKMARK);
        tmp.bookmarkPosition = position;
        return tmp;
    }

    /**
     * Create an object indicating a bookmark was added.
     *
     * @param viewer The associated StreamsViewer.
     * @param position Position of the new bookmark.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent bookmarkAdded(StreamsViewer viewer, long position) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_ADDBOOKMARK);
        tmp.bookmarkPosition = position;
        return tmp;
    }

    /**
     * Create an object indicating a substream was added.
     *
     * @param viewer The associated StreamsViewer.
     * @param substream Index of the new substream.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent substreamAdded(StreamsViewer viewer, int substream) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_ADDSUBSTREAM);
        tmp.substreamIndex = substream;
        return tmp;
    }

    /**
     * Create an object indicating a substream was changed.
     *
     * @param viewer The associated StreamsViewer.
     * @param substream Index of the modified substream.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent substreamChanged(StreamsViewer viewer, int substream) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_CHANGESUBSTREAM);
        tmp.substreamIndex = substream;
        return tmp;
    }

    /**
     * Create an object indicating a substream was removed.
     *
     * @param viewer The associated StreamsViewer.
     * @param substream Index of the removed substream.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent substreamRemoved(StreamsViewer viewer, int substream) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_REMOVESUBSTREAM);
        tmp.substreamIndex = substream;
        return tmp;
    }

    /**
     * Create an object indicating a substream was moved.
     *
     * @param viewer The associated StreamsViewer.
     * @param oldIndex Old substream index.
     * @param newIndex New substream index.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent substreamMoved(StreamsViewer viewer, int oldIndex, int newIndex) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_MOVESUBSTREAM);
        tmp.oldSubstreamIndex = oldIndex;
        tmp.substreamIndex = newIndex;
        return tmp;
    }
}
