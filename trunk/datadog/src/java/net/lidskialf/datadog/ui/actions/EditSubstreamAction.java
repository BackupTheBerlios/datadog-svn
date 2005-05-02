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

import java.awt.event.ActionEvent;

import javax.swing.Action;

import net.lidskialf.datadog.Substream;
import net.lidskialf.datadog.ui.ActionInformationSource;
import net.lidskialf.datadog.ui.GroupableAction;
import net.lidskialf.datadog.ui.StreamsViewer;
import net.lidskialf.datadog.ui.SubstreamEditor;

/**
 * Action for editing a substream.
 *
 * ActionInformationSource.isEnabled() should support the "EditSubstreamAction" action.
 *
 * ActionInformationSource.getParameter() must implement the following:
 *   "substreamIndex" - return an Integer giving the position of the current substream (or null if none).
 *   "substream" - return a Substream for the current bookmark (or null if none).
 *
 * @author Andrew de Quincey
 */
public class EditSubstreamAction extends GroupableAction {

    private StreamsViewer viewer;
    private ActionInformationSource infoSource;

    /**
     * Constructor.
     *
     * @param viewer The StreamsViewer concerned.
     * @param infoSource The ActionInformationSource as described in the class information.
     */
    public EditSubstreamAction(StreamsViewer viewer, ActionInformationSource infoSource) {
        this.viewer = viewer;
        this.infoSource = infoSource;

        putValue(Action.NAME, "Edit substream...");
    }

    public void update() {
        setEnabled(infoSource.isActionEnabled("EditSubstreamAction"));
    }

    public void actionPerformed(ActionEvent e) {
        Integer index = (Integer) infoSource.getActionParameter("substreamIndex");
        Substream substream = (Substream) infoSource.getActionParameter("substream");

        if ((index != null) && (substream!= null)) {
            new SubstreamEditor(viewer, index.intValue(), substream);
        }
    }
}
