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

import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;

import net.lidskialf.datadog.StreamParser;

/**
 * @author Andrew de Quincey
 */
public class TransportStreamParser implements StreamParser {

	private JPanel mainPanel = null;  //  @jve:decl-index=0:visual-constraint="179,63"
	private TransportStreamsViewer transportStreamsViewer = null;
	private JButton nextButton = null;
	private JButton previousButton = null;
	private JPanel jPanel1 = null;
	private JComboBox seekCombo = null;
  
  
  
  
	/**
	 * This method initializes mainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
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
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.gridy = 2;
			gridBagConstraints9.ipadx = 0;
			gridBagConstraints9.ipady = 0;
			gridBagConstraints9.insets = new java.awt.Insets(0,0,1,0);
			gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			mainPanel.add(getTransportStreamsViewer(), gridBagConstraints8);
			mainPanel.add(getJPanel1(), gridBagConstraints9);
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
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton();
			nextButton.setName("");
			nextButton.setText(">>>");
		}
		return nextButton;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getPreviousButton() {
		if (previousButton == null) {
			previousButton = new JButton();
			previousButton.setName("");
			previousButton.setText("<<<");
		}
		return previousButton;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.ipadx = 0;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.gridx = 2;
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.ipadx = 0;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridx = 1;
			gridBagConstraints13.gridy = 0;
			jPanel1.add(getPreviousButton(), gridBagConstraints11);
			jPanel1.add(getSeekCombo(), gridBagConstraints13);
			jPanel1.add(getNextButton(), gridBagConstraints12);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getSeekCombo() {
		if (seekCombo == null) {
			seekCombo = new JComboBox();
		}
		return seekCombo;
	}

  /**
   * Constructor.
   * 
   * @param filename The filename of the stream to parse.
   */
  public TransportStreamParser(String filename) {
    this.filename = filename;
  }
  
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.StreamParser#GetUI(java.lang.String)
   */
  public JPanel GetUI() {
    return getMainPanel();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "Transport Stream: " + filename;
  }
  
  /**
   * The filename we are parsing.
   */
  private String filename;
}
