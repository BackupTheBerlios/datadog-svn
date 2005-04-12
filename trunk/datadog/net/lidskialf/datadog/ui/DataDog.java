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

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.lidskialf.datadog.StreamExplorerFactory;
import net.lidskialf.datadog.StreamExplorer;


import java.awt.GridLayout;
import java.awt.Container;
import javax.swing.JScrollPane;

/**
 * @author Andrew de Quincey
 *
 */
public class DataDog {

	private JPanel jContentPane = null;
	private JFrame mainFrame = null;  //  @jve:decl-index=0:visual-constraint="141,28"
	private JMenuBar jJMenuBar = null;
	private JList streamsList = null;
	private JMenu fileMenu = null;
	private JMenuItem openNewStream = null;
	private JMenuItem quit = null;
	private JMenu helpMenu = null;
	private JMenuItem about = null;
	private JMenuItem showSelectedStream = null;
	private JMenuItem closeSelectedStream = null;  
  private JScrollPane jScrollPane = null;
  private DefaultListModel openedStreams = null;   //  @jve:decl-index=0:

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridLayout gridLayout3 = new GridLayout();
			jContentPane = new JPanel();
			jContentPane.setLayout(gridLayout3);
			gridLayout3.setRows(1);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}
  
	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */    
	private JFrame getMainFrame() {
		if (mainFrame == null) {
			mainFrame = new JFrame();
			mainFrame.setContentPane(getJContentPane());
			mainFrame.setTitle("DataDog v" + version);
			mainFrame.setSize(480, 186);
			mainFrame.setJMenuBar(getJJMenuBar());
			mainFrame.setVisible(true);
			mainFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
			mainFrame.addWindowListener(new java.awt.event.WindowAdapter() { 
				public void windowClosing(java.awt.event.WindowEvent e) {
          System.exit(0);
				}
			});
		}
		return mainFrame;
	}
  
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */    
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}
  
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getStreamsList() {
		if (streamsList == null) {
		  streamsList = new JList();
			streamsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			streamsList.setModel(getOpenedStreams());
			streamsList.setVisibleRowCount(8);
			streamsList.addMouseListener(new java.awt.event.MouseAdapter() { 
				public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() == 2) {
            StreamExplorer parser = (StreamExplorer) streamsList.getSelectedValue();            
            if (parser != null) {
              Container topLevel = parser.GetUI().getParent().getParent().getParent().getParent(); 
              topLevel.setVisible(true);
            }
          }
				}
			});
		}
		return streamsList;
	}
  
	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getOpenNewStream());
			fileMenu.add(getShowSelectedStream());
			fileMenu.add(getCloseSelectedStream());
			fileMenu.add(getQuit());
		}
		return fileMenu;
	}
  
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getOpenNewStream() {
		if (openNewStream == null) {
			openNewStream = new JMenuItem();
			openNewStream.setText("Open New Stream...");
			openNewStream.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
          JFileChooser chooser = new JFileChooser();
          int result = chooser.showOpenDialog(getMainFrame());
          if (result == JFileChooser.APPROVE_OPTION) {
            String filename = chooser.getSelectedFile().getName();
            String pathname = chooser.getSelectedFile().getAbsolutePath();
            
            // try each parser in turn until we get one which matches. FIXME: this could probably be made a lot smarter
            for(int i=0; i< streamParserFactories.length; i++) {
              try {
                if (streamParserFactories[i].Probe(pathname)) {
                  StreamExplorer parser = streamParserFactories[i].Parse(pathname);
                  
                  JFrame parserFrame = new JFrame();
                  JPanel parserPanel = parser.GetUI();
                  parserFrame.getContentPane().add(parserPanel);
                  parserFrame.pack();
                  parserFrame.setVisible(true);
                  getOpenedStreams().addElement(parser);
                  return;
                }
              } catch (Throwable t) {
                t.printStackTrace();
                JOptionPane.showMessageDialog(getMainFrame(), 
                    "An error during stream probing (" + t.getMessage() + ")", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
              }
            }
            
            // if we get here, nothing matched
            JOptionPane.showMessageDialog(getMainFrame(), 
                "Sorry, the stream \"" + filename + "\" is unsupported.", 
                "Unsupported stream", 
                JOptionPane.WARNING_MESSAGE);            
          }
				}
			});
		}
		return openNewStream;
	}
  
	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getQuit() {
		if (quit == null) {
			quit = new JMenuItem();
			quit.setText("Quit");
			quit.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
          System.exit(0);
				}
			});
		}
		return quit;
	}
  
	/**
	 * This method initializes jMenu1	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAbout());
		}
		return helpMenu;
	}
  
	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getAbout() {
		if (about == null) {
			about = new JMenuItem();
			about.setText("About");
			about.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
          AboutBox about = new AboutBox();
				}
			});
		}
		return about;
	}
  
	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getShowSelectedStream() {
		if (showSelectedStream == null) {
			showSelectedStream = new JMenuItem();
			showSelectedStream.setText("Show Selected Stream");
			showSelectedStream.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
          StreamExplorer parser = (StreamExplorer) getStreamsList().getSelectedValue();            
          if (parser != null) {
            Container topLevel = parser.GetUI().getParent().getParent().getParent().getParent(); 
            topLevel.setVisible(true);
          }
				}
			});
		}
		return showSelectedStream;
	}
  
	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getCloseSelectedStream() {
    int x = 1;
    
		if (closeSelectedStream == null) {
			closeSelectedStream = new JMenuItem();
			closeSelectedStream.setText("Close selected stream");
			closeSelectedStream.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
          StreamExplorer parser = (StreamExplorer) getStreamsList().getSelectedValue();            
          if (parser != null) {
            getOpenedStreams().removeElement(parser);
            JFrame topLevel = (JFrame) parser.GetUI().getParent().getParent().getParent().getParent(); 
            topLevel.dispose();
          }
				}
			});
		}
		return closeSelectedStream;
	}
  
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getStreamsList());
			jScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPane;
	}
  
	/**
	 * This method initializes defaultListModel	
	 * 	
	 * @return javax.swing.DefaultListModel	
	 */    
	private DefaultListModel getOpenedStreams() {
		if (openedStreams == null) {
			openedStreams = new DefaultListModel();
		}
		return openedStreams;
	}
  
  /**
   * The entry point!
   * 
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    application = new DataDog();
    application.getMainFrame().setVisible(true);
  }
  
  /**
   * The singleton application instance.
   */
  private static DataDog application;
  
  /**
   * The version of the application. 
   */
  public static final String version = "0.01";
  
  /**
   * The list of known stream analyser factories.
   */
  private static final StreamExplorerFactory[] streamParserFactories = 
    new StreamExplorerFactory[] { new net.lidskialf.datadog.mpeg.TransportStreamExplorerFactory() };
}
