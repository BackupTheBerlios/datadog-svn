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

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.lidskialf.datadog.StreamExplorer;
import net.lidskialf.datadog.ui.DataDog;

/**
 * Action to show (unhide) a stream.
 *
 * @author Andrew de Quincey
 */
public class ShowStreamAction extends AbstractAction {

    private StreamExplorer explorer;

    /**
     * Constructor for an Action which listens to the currently selected item on
     * the streams list on the main datadog window.
     */
    public ShowStreamAction() {
        this.explorer = null;

        putValue(Action.NAME, "Show selected stream");
    }

    /**
     * Constructor for an Action to close a specific stream.
     * @param explorer the stream explorer to show
     */
    public ShowStreamAction(StreamExplorer explorer) {
        this.explorer = explorer;

        putValue(Action.NAME, "Show stream");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        StreamExplorer tmpexplorer = explorer;
        if (tmpexplorer == null) {
            tmpexplorer = DataDog.getApplication().getSelectedStream();
        }
        if (tmpexplorer != null) {
            DataDog.getApplication().setStreamVisibility(tmpexplorer, true);
        }
    }
}
