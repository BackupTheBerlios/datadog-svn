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
 * Bookmarks for a stream.
 *
 * @author Andrew de Quincey
 */
public class StreamBookmarks {

    /**
     * Constructor.
     */
    public StreamBookmarks() {
        bookmarks = Collections.synchronizedSortedMap(new TreeMap());
    }

    /**
     * Get all bookmarks.
     *
     * @return Ascendingly ordered Iterator of Long objects, each giving the absolute stream position of a bookmark.
     */
    public Iterator getKeys() {
        return bookmarks.values().iterator();
    }

    /**
     * Get bookmarks between two points.
     *
     * @param start Starting position.
     * @param end Ending position (inclusive).
     * @return Ascendingly ordered Iterator of Long objects, each giving the absolute stream position of a bookmark.
     */
    public Iterator getKeys(long start, long end) {
        return bookmarks.subMap(new Long(start), new Long(end)).keySet().iterator();
    }

    /**
     * Add a bookmark.
     *
     * @param absolutePosition Position of the bookmark.
     * @param bookmark The bookmark instance.
     */
    public void add(long absolutePosition, Object bookmark) {
        bookmarks.put(new Long(absolutePosition), bookmark);
    }

    /**
     * Remove a bookmark by object.
     *
     * @param bookmark The bookmark instance to remove.
     */
    public void remove(Object bookmark) {
        Iterator i = bookmarks.keySet().iterator();
        while(i.hasNext()) {
            Long key = (Long) i.next();

            if (bookmarks.get(key) == bookmark) {
                bookmarks.remove(key);
            }
        }
    }

    /**
     * Remove a bookmark by absolute position.
     *
     * @param absolutePosition The position at which to remove all bookmarks.
     */
    public void remove(long absolutePosition) {
        bookmarks.remove(new Long(absolutePosition));
    }

    /**
     * Move a bookmark existing at curAbsolutePosition to newAbsolutePosition.
     *
     * @param curAbsolutePosition Current position of the bookmark.
     * @param newAbsolutePosition New position of the bookmark.
     * @return True on success (i.e. there was a bookmark at curAbsolutePosition),
     * false on failure (i.e. nothing was changed).
     */
    public boolean move(long curAbsolutePosition, long newAbsolutePosition) {
        // if the bookmark really exists, move it
        Object tmp = bookmarks.get(new Long(curAbsolutePosition));
        if (tmp != null) {
            bookmarks.remove(new Long(curAbsolutePosition));
            bookmarks.put(new Long(newAbsolutePosition), tmp);
            return true;
        }

        // there was no such bookmark anyway.
        return false;
    }

    /**
     * Does a bookmark exist at the given position.
     *
     * @param position Position to check.
     * @return True if there does, false if not.
     */
    public boolean contains(long position) {
        return bookmarks.containsKey(new Long(position));
    }

    /**
     * The bookmarks themselves.
     */
    protected SortedMap bookmarks;
}
