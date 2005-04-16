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
import net.lidskialf.datadog.ui.StreamsViewerRowHeader;
import net.lidskialf.datadog.ui.StreamsViewerColumnHeader;

/**
 * The StreamExplorer for Transport Streams.
 * 
 * @author Andrew de Quincey
 */
public class TransportStreamExplorer implements StreamExplorer {

  /**
   * Constructor.
   * 
   * @param bitstream The Bitstream to view.
   */
  public TransportStreamExplorer(Bitstream bitstream) throws IOException {
    this.bitstream = bitstream;
    transportStream = new TransportStream(bitstream);
  }
   
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.StreamParser#GetUI(java.lang.String)
   */
  public JComponent buildUI() {
    if (ui != null) return ui;
    
    try {
      initComponents();
    } catch (IOException e) {
      // FIXME: do something with this
    }
    
    FormLayout layout = new FormLayout("pref:grow",
                                       "pref:grow");
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();

    builder.add(viewer, cc.xy(1, 1, "fill, fill"));
    ui = builder.getPanel();
    
    return ui;
  }
  
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.StreamExplorer#buildMenuBar()
   */
  public JMenuBar buildMenuBar() {
    return null;
  }
  
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.StreamExplorer#close()
   */
  public void close() {
    try {
      bitstream.close();
    } catch (IOException e) {}
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "Transport Stream: " + bitstream.toString();
  }
  
  
  /**
   * Initialise components used here.
   */
  private void initComponents() throws IOException {
    rowModel = new DefaultListModel();
    
    viewer = new TransportStreamsViewer(transportStream, rowModel);
    viewer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    columnHeader = new StreamsViewerColumnHeader(viewer);
    viewer.setColumnHeaderView(columnHeader);

    rowHeader = new StreamsViewerRowHeader(viewer, rowModel);
    viewer.setRowHeaderView(rowHeader);
  }

  
  
  private Bitstream bitstream;
  private TransportStream transportStream;
  private DefaultListModel rowModel;
  
  private TransportStreamsViewer viewer;
  private StreamsViewerColumnHeader columnHeader;
  private StreamsViewerRowHeader rowHeader;
  private JComponent ui;
}
