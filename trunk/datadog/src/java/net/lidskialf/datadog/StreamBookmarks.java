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
     * @return Ascendingly ordered Iterator of bookmarks (by absolute position of bookmark). 
     */
    public Iterator get() {
        return bookmarks.values().iterator();
    }
    
    /**
     * Get bookmarks between two points.
     * 
     * @param start Starting position.
     * @param end Ending position (inclusive).
     * @return Ascendingly ordered Iterator of bookmarks (by absolute position of bookmark). 
     */
    public Iterator get(long start, long end) {
        return bookmarks.subMap(new Long(start), new Long(end)).values().iterator();
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
     * The bookmarks themselves.
     */
    protected SortedMap bookmarks;
}
