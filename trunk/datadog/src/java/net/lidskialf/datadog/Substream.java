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

import java.awt.Color;

/**
 * Class representing a Substream.
 *
 * @author Andrew de Quincey
 */
public class Substream {

    /**
     * The colour of the substream.
     */
    protected Color colour;

    /**
     * A longer textual description of the substream.
     */
    protected String description;

    /**
     * A brief label to display for this substream.
     */
    protected String label;

    /**
     * Is this stream removeable by the user?
     */
    protected boolean removeable;



    /**
     * Constructor.
     *
     * @param label A brief label to display for this substream.
     * @param colour Colour used for this substream.
     * @param description A textual description of the substream.
     * @param removable Is this stream removeable by the user?
     */
    public Substream(String label, Color colour, String description, boolean removable) {
        this.label = label;
        this.colour = colour;
        this.description = description;
        this.removeable = removable;
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

    /**
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return Returns the removeable.
     */
    public boolean isRemoveable() {
        return removeable;
    }

    /**
     * @param removeable The removeable to set.
     */
    public void setRemoveable(boolean removeable) {
        this.removeable = removeable;
    }
}
