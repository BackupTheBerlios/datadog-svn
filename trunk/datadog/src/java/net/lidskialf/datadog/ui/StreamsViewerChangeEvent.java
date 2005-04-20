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
     * The old bookmark absolute position on a CHANGED_MOVEBOOKMARK event.
     */
    public long oldBookmarkPosition;

    /**
     * The bookmark absolute position for a bookmark related event.
     */
    public long bookmarkPosition;



    /**
     * Constructor.
     *
     * @param changeType Type of change - one of the CHANGED_* values.
     */
    public StreamsViewerChangeEvent(StreamsViewer viewer, int changeType) {
        this.viewer = viewer;
        this.changeType = changeType;
    }


    /**
     * Create an object indicating the zoom factor was changed.
     *
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
     * @param zoomFactor The new zoom factor.
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
     * @param zoomFactor The new zoom factor.
     * @return Appropriately initialised StreamsViewerChangeEvent instance.
     */
    public static StreamsViewerChangeEvent bookmarkMoved(StreamsViewer viewer, long oldPosition, long newPosition) {
        StreamsViewerChangeEvent tmp = new StreamsViewerChangeEvent(viewer, CHANGE_MOVEBOOKMARK);
        tmp.oldBookmarkPosition = oldPosition;
        tmp.bookmarkPosition = newPosition;
        return tmp;
    }
}
