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
package net.lidskialf.datadog;

import javax.swing.*;

/**
 * Interface implemented by a stream parser.
 *
 * @author Andrew de Quincey
 */
public interface StreamExplorer {

    /**
     * Gets the UI for viewing the stream (only one UI instance per
     * StreamParser).
     * 
     * @return the UI for viewing the stream
     */
    public JComponent buildUI();

    /**
     * Gets the menubar for the stream viewer.
     *
     * @return The JMenuBar instance, or null if no menubar is wanted.
     */
    public JMenuBar buildMenuBar();

    /**
     * The stream is no longer required - free all resources etc.
     */
    public void close();
}
