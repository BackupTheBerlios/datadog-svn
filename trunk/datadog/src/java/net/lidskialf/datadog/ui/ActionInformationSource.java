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
 * Interface implemented by UI classes which use Actions and need to control them.
 *
 * @author Andrew de Quincey
 */
public interface ActionInformationSource {

    /**
     * Determine if a given action is enabled just now.
     *
     * @param action The name of the action concerned (generally the name of the class implementing the action).
     * @return True if it is enabled, false if not.
     */
    public boolean isActionEnabled(String action);

    /**
     * Used by Actions to get parameters they require.
     *
     * @param name Name of the parameter.
     * @return The value, or null if unknown.
     */
    public Object getActionParameter(String name);
}
