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

import java.util.*;

/**
 * A collection of Substreams of a particular stream.
 *
 * @author Andrew de Quincey
 */
public class Substreams {

    /**
     * The ordered list of substreams.
     */
    protected List substreams = Collections.synchronizedList(new ArrayList());

    /**
     * Fast lookup of Substream instance -> index.
     */
    protected Map substreamToIndex = Collections.synchronizedMap(new HashMap());



    /**
     * Count of substreams.
     *
     * @return The number.
     */
    public int size() {
        return substreams.size();
    }

    /**
     * Get the stream at index.
     *
     * @param index The requested stream.
     * @return The stream, or null if not present.
     */
    public Substream get(int index) {
        if (index >= substreams.size()) return null;
        return (Substream) substreams.get(index);
    }

    /**
     * Get all substreams.
     *
     * @return Iterator of all Substream instances in order.
     */
    public Iterator get() {
        return substreams.iterator();
    }

    /**
     * Return the index of the given substream.
     *
     * @param substream The substream to find.
     * @return The index, or -1 if not found.
     */
    public int indexOf(Substream substream) {
        if (substreamToIndex.containsKey(substream)) {
            return ((Integer) substreamToIndex.get(substream)).intValue();
        }

        return -1;
    }

    /**
     * Append the specified substream to the list.
     *
     * @param substream The substream to add.
     * @return The index of the new substream.
     */
    public int add(Substream substream) {
        int insertPos = substreams.size();
        substreams.add(substream);
        substreamToIndex.put(substream, new Integer(insertPos));
        return insertPos;
    }

    /**
     * Move a substream.
     *
     * @param oldIndex Index of substream to move.
     * @param newIndex Destination index of substream.
     */
    public void move(int oldIndex, int newIndex) {

        if (newIndex > oldIndex) newIndex--;

        Substream s = (Substream) substreams.get(oldIndex);
        substreams.remove(oldIndex);
        if (newIndex >= substreams.size()) {
            substreams.add(s);
        } else {
            substreams.add(newIndex, s);
        }

        substreamToIndex.clear();
        for(int i=0; i< substreams.size(); i++) {
            substreamToIndex.put(substreams.get(i), new Integer(i));
        }
    }
}
