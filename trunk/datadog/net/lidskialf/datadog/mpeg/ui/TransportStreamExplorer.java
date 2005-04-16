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

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.io.IOException;

import net.lidskialf.datadog.*;
import net.lidskialf.datadog.mpeg.bitstream.*;

/**
 * @author Andrew de Quincey
 */
public class TransportStreamExplorer implements StreamExplorer {

	private JPanel mainPanel = null;  //  @jve:decl-index=0:visual-constraint="179,63"
	private TransportStreamsViewer transportStreamsViewer = null;
	/**
	 * This method initializes mainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			mainPanel.setLayout(new GridBagLayout());
      mainPanel.setSize(639, 176);
      mainPanel.setPreferredSize(new Dimension(639, 176));
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 1.0;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints8.ipadx = 580;
			gridBagConstraints8.ipady = 106;
			mainPanel.add(getTransportStreamsViewer(), gridBagConstraints8);
		}
		return mainPanel;
	}
	/**
	 * This method initializes transportStreamsViewer	
	 * 	
	 * @return net.lidskialf.datadog.mpeg.ui.TransportStreamsViewer	
	 */    
	private TransportStreamsViewer getTransportStreamsViewer() {
		if (transportStreamsViewer == null) {
			transportStreamsViewer = new TransportStreamsViewer();
			transportStreamsViewer.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		}
		return transportStreamsViewer;
	}
  /**
   * Constructor.
   * 
   * @param bitstream The Bitstream to view.
   */
  public TransportStreamExplorer(Bitstream bitstream) throws IOException {
    this.bitstream = bitstream;
    transportStream = new TransportStream(bitstream);
    getTransportStreamsViewer().setStream(transportStream);
  }
 
  
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.StreamParser#GetUI(java.lang.String)
   */
  public JComponent buildUI() {
    return getMainPanel();
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
    } catch (IOException e) {
    }
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "Transport Stream: " + bitstream.toString();
  }
  
  /**
   * The stream we are viewing.
   */
  private Bitstream bitstream;
  
  /**
   * The transport stream we are accessing.
   */
  private TransportStream transportStream;
}
