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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;

/**
 * @author Andrew de Quincey
 *
 */
public class AboutBox {

  
	private JPanel jContentPane = null;
	private JDialog aboutDialog = null;  //  @jve:decl-index=0:visual-constraint="42,12"
	private JLabel logo = null;
	private JPanel jPanel = null;
	private JTextArea jTextArea = null;
	private JPanel jPanel1 = null;
	private JButton ok = null;
	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			logo = new JLabel();
			jContentPane.setLayout(new BorderLayout());
			logo.setText("Logo");
			jContentPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(2,2,2,2));
			jContentPane.setBackground(java.awt.Color.white);
			jContentPane.add(logo, java.awt.BorderLayout.WEST);
			jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */    
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog();
			aboutDialog.setContentPane(getJContentPane());
			aboutDialog.setSize(507, 220);
			aboutDialog.setTitle("About DataDog");
			aboutDialog.setName("aboutFrame");
			aboutDialog.setModal(true);
			aboutDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		}
		return aboutDialog;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			jPanel.add(getJTextArea(), gridBagConstraints2);
			jPanel.add(getJPanel1(), gridBagConstraints3);
		}
		return jPanel;
	}
	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
			jTextArea.setText("DataDog v" + DataDog.version + "\n" +
                        "(c) 2005 Andrew de Quincey. All rights reserved.\n" +
                        "Licensed under the GNU Public License (GPL).\n" +
                        "See http://www.gnu.org for details.");
		}
		return jTextArea;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(2,0,2,2));
			jPanel1.setBackground(java.awt.Color.white);
			jPanel1.add(getOk(), java.awt.BorderLayout.EAST);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getOk() {
		if (ok == null) {
			ok = new JButton();
			ok.setText("OK");
			ok.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
          aboutDialog.dispose();
				}
			});
		}
		return ok;
	}
  
  public AboutBox() {
    getAboutDialog().setVisible(true);
  }
}
