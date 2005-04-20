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
 * Interface implemented by components which are interested in changes to a
 * StreamsViewer.
 *
 * @author Andrew de Quincey
 */
public interface StreamsViewerChangeListener {

    /**
     * Event fired when the zoom factor of the StreamsViewer changes.
     *
     * @param e
     *            The StreamsViewerChangeEvent describing the event.
     */
    public void zoomChanged(StreamsViewerChangeEvent e);

    /**
     * Event fired when the length of the stream viewed by the StreamsViewer
     * changes.
     *
     * @param e
     *            The StreamsViewerChangeEvent describing the event.
     */
    public void lengthChanged(StreamsViewerChangeEvent e);

    /**
     * A bookmark was moved from oldAbsolutePosition to newAbsolutePosition.
     *
     * @param e
     *            The StreamsViewerChangeEvent describing the event.
     */
    public void bookmarkMoved(StreamsViewerChangeEvent e);
}
