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

import java.util.*;


/**
 * A group of actions related in some way (e.g. all on the same menu).
 *
 * @author Andrew de Quincey
 */
public class ActionGroup {

    /**
     * The list of actions
     */
    private Vector actions = new Vector();

    /**
     * Add an action to the group.
     *
     * @param a Action to add.
     * @return The same value passed as a - to allow better code when building menus etc.
     */
    public GroupableAction add(GroupableAction a) {
        actions.add(a);
        return a;
    }

    /**
     * Updates all the actions in the group when something changes.
     */
    public void update() {
        for(int i=0; i< actions.size(); i++) {
            GroupableAction a = (GroupableAction) actions.elementAt(i);
            a.update();
        }
    }
}
