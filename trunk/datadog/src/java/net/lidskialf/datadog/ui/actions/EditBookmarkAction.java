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
package net.lidskialf.datadog.ui.actions;

import java.awt.event.*;
import javax.swing.*;
import net.lidskialf.datadog.*;
import net.lidskialf.datadog.ui.*;

/**
 * Action for editing a bookmark.
 *
 * ActionInformationSource.isEnabled() should support the "EditBookmarkAction" action.
 *
 * ActionInformationSource.getParameter() must implement the following:
 *   "bookmarkPosition" - return a Long giving the position of the current bookmark (or null if none).
 *   "bookmark" - return a StreamBookmark for the current bookmark (or null if none).
 *
 * @author Andrew de Quincey
 */
public class EditBookmarkAction extends GroupableAction {

    private StreamsViewer viewer;
    private ActionInformationSource infoSource;

    /**
     * Constructor.
     *
     * @param viewer The StreamsViewer concerned.
     * @param infoSource The ActionInformationSource as described in the class information.
     */
    public EditBookmarkAction(StreamsViewer viewer, ActionInformationSource infoSource) {
        this.viewer = viewer;
        this.infoSource = infoSource;

        putValue(Action.NAME, "Edit bookmark...");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Long position = (Long) infoSource.getActionParameter("bookmarkPosition");
        StreamBookmark bookmark = (StreamBookmark) infoSource.getActionParameter("bookmark");

        if ((position != null) && (bookmark != null)) {
            new BookmarkEditor(viewer, position.longValue(), bookmark);
        }
    }

    /* (non-Javadoc)
     * @see net.lidskialf.datadog.ui.GroupableAction#update()
     */
    public void update() {
        setEnabled(infoSource.isActionEnabled("EditBookmarkAction"));
    }
}
