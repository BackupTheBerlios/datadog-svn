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

import java.awt.*;


/**
 * A bookmark marking a significant place in the stream.
 *
 * @author Andrew de Quincey
 */
public class StreamBookmark {

    /**
     * The colour.
     */
    protected Color colour;

    /**
     * The description.
     */
    protected String description;


    /**
     * Constructor.
     *
     * @param description The initial description.
     * @param colour The initial colour.
     */
    public StreamBookmark(String description, Color colour) {
        this.description = description;
        this.colour = colour;
    }

    /**
     * @return Returns the colour.
     */
    public Color getColour() {
        return colour;
    }

    /**
     * @param colour The colour to set.
     */
    public void setColour(Color colour) {
        this.colour = colour;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getDescription();
    }
}
