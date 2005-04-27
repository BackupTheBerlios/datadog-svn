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
package net.lidskialf.datadog.mpeg.ui;

import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.io.IOException;

import net.lidskialf.datadog.*;
import net.lidskialf.datadog.mpeg.bitstream.*;
import net.lidskialf.datadog.ui.*;
import net.lidskialf.datadog.ui.actions.*;

/**
 * The StreamExplorer for Transport Streams.
 *
 * @author Andrew de Quincey
 */
public class TransportStreamExplorer implements StreamExplorer {

    /**
     * Spacing between minor ticks in the column header.
     */
    public static final int MINOR_TICK_SPACING = 16;

    /**
     * Spacing between major ticks in the column header.
     */
    public static final int MAJOR_TICK_SPACING = 0x100;


    private Bitstream bitstream;
    private TransportStream transportStream;
    private StreamBookmarks bookmarks;
    private Substreams substreams;

    private JToolBar toolbar;
    private TransportStreamsViewer viewer;
    private StreamsViewerColumnHeader columnHeader;
    private StreamsViewerRowHeader rowHeader;
    private JComponent ui;


    /**
     * Constructor.
     *
     * @param bitstream
     *            The Bitstream to view.
     * @throws IOException if there was a problem parsing <code>bitstream</code>
     */
    public TransportStreamExplorer(Bitstream bitstream) throws IOException {
        this.bitstream = bitstream;
        transportStream = new TransportStream(bitstream);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.StreamParser#GetUI(java.lang.String)
     */
    public JComponent buildUI() {
        if (ui != null)
            return ui;

        try {
            initComponents();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        FormLayout layout = new FormLayout("pref:grow", "pref:grow, pref:grow");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.add(toolbar, cc.xy(1, 1, "fill, top"));
        builder.add(viewer, cc.xy(1, 2, "fill, fill"));
        ui = builder.getPanel();

        return ui;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.StreamExplorer#buildMenuBar()
     */
    public JMenuBar buildMenuBar() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.lidskialf.datadog.StreamExplorer#close()
     */
    public void close() {
        try {
            bitstream.close();
        } catch (IOException e) {
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Transport Stream: " + bitstream.toString();
    }

    /**
     * Initialise components used here.
     *
     * @throws IOException if there was a problem initialising any component
     */
    private void initComponents() throws IOException {
        bookmarks = new StreamBookmarks();
        substreams = new Substreams();

        viewer = new TransportStreamsViewer(transportStream, bookmarks, substreams);
        viewer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        columnHeader = new StreamsViewerColumnHeader(viewer, MINOR_TICK_SPACING, MAJOR_TICK_SPACING);
        viewer.setColumnHeaderView(columnHeader);

        rowHeader = new StreamsViewerRowHeader(viewer);
        viewer.setRowHeaderView(rowHeader);

        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        toolbar.add(new ZoomInAction(viewer));
        toolbar.add(new ZoomOutAction(viewer));
    }
}
