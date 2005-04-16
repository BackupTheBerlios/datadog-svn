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

import java.awt.event.*;
import javax.swing.*;

import net.lidskialf.datadog.*;
import net.lidskialf.datadog.ui.actions.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.*;
import com.jgoodies.looks.plastic.theme.*;

/**
 * @author Andrew de Quincey
 *
 */
public class DataDog {

  public DataDog() {
    frame = new JFrame();
    frame.setTitle("DataDog");
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
        quitAction.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, ""));
      }
    });
    
    frame.setJMenuBar(buildMenuBar());
    frame.getContentPane().add(buildPanel());
    frame.pack();
    frame.setVisible(true);
  }
  
  /**
   * Initialise components used here.
   */
  private void initComponents() {
    openedStreams = new DefaultListModel();
    
    streamsList = new JList();
    streamsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    streamsList.setModel(openedStreams);
    
    streamsListScrollPane = new JScrollPane();
    streamsListScrollPane.setViewportView(streamsList);
    streamsListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
  }
  
  /**
   * Build the panel/layout components. 
   * 
   * @return The completed JComponent
   */
  private JComponent buildPanel() {
    initComponents();
    
    FormLayout layout = new FormLayout("pref:grow",
                                       "pref:grow");
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();
    
    builder.add(streamsListScrollPane, cc.xy(1, 1, "fill, fill"));
    
    return builder.getPanel();
  }
  
  /**
   * Build the menu bar.
   * 
   * @return The menu bar
   */
  private JMenuBar buildMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(new OpenNewStreamAction());
    fileMenu.add(new ShowStreamAction());
    fileMenu.add(new CloseStreamAction());
    quitAction = new QuitDataDogAction();
    fileMenu.add(quitAction);
    menuBar.add(fileMenu);
    
    JMenu helpMenu = new JMenu("Help");
    helpMenu.add(new AboutDataDogAction());
    menuBar.add(helpMenu);
    
    return menuBar;
  }
  
  public JFrame getFrame() {
    return frame;
  }
  
  public void AddNewOpenedStream(StreamExplorer explorer) {
    openedStreams.addElement(explorer);
  }
  
  
  
  
  
  
  
  
  
  /**
   * The entry point!
   * 
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    
    // set the look and feel for the application
    PlasticLookAndFeel.setMyCurrentTheme(new ExperienceBlue());
    try {
      UIManager.setLookAndFeel(new PlasticLookAndFeel());
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    application = new DataDog();
  }
  
  
  /**
   * Get the instance of the DataDog application.
   * 
   * @return The application instance.
   */
  public static DataDog getApplication() {
    return application;
  }
  
  private JFrame frame;
  private JList streamsList;
  private JScrollPane streamsListScrollPane; 
  private DefaultListModel openedStreams;
  private Action quitAction;
  
  
  private static DataDog application;
  
  /**
   * The version of the application. 
   */
  public static final String version = "0.01";
  
  /**
   * The list of known stream explorer factories.
   */
  public static final StreamExplorerFactory[] streamExplorerFactories = 
    new StreamExplorerFactory[] { new net.lidskialf.datadog.mpeg.TransportStreamExplorerFactory() };
  
  
  
  
  
  
  /*
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
  
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}
  
	private JList getStreamsList() {
		if (streamsList == null) {
		  streamsList = new JList();
			streamsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			streamsList.setModel(getOpenedStreams());
			streamsList.setVisibleRowCount(8);
			streamsList.addMouseListener(new java.awt.event.MouseAdapter() { 
				public void mouseClicked(java.awt.event.MouseEvent e) {
          if (e.getClickCount() == 2) {
            StreamExplorer explorer = (StreamExplorer) streamsList.getSelectedValue();            
            if (explorer != null) {
              Container topLevel = explorer.getUI().getParent().getParent().getParent().getParent(); 
              topLevel.setVisible(true);
            }
          }
				}
			});
		}
		return streamsList;
	}
  
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
            try {
              Bitstream stream = new FileBitstream(pathname);
              for(int i=0; i< streamExplorerFactories.length; i++) {
                  if (streamExplorerFactories[i].probe(stream)) {
                    StreamExplorer explorer = streamExplorerFactories[i].open(stream);
                    
                    JFrame explorerFrame = new JFrame(explorer.toString());
                    JPanel explorerPanel = explorer.getUI();
                    explorerFrame.getContentPane().add(explorerPanel);
                    explorerFrame.pack();
                    explorerFrame.setVisible(true);
                    getOpenedStreams().addElement(explorer);
                    return;
                  }
              }
            } catch (Throwable t) {
              t.printStackTrace();
              JOptionPane.showMessageDialog(getMainFrame(), 
                  "An error during stream probing (" + t.getMessage() + ")", 
                  "Error", 
                  JOptionPane.ERROR_MESSAGE);
              return;
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
  
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAbout());
		}
		return helpMenu;
	}
  
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
  
	private JMenuItem getShowSelectedStream() {
		if (showSelectedStream == null) {
			showSelectedStream = new JMenuItem();
			showSelectedStream.setText("Show Selected Stream");
			showSelectedStream.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
          StreamExplorer parser = (StreamExplorer) getStreamsList().getSelectedValue();            
          if (parser != null) {
            Container topLevel = parser.getUI().getParent().getParent().getParent().getParent(); 
            topLevel.setVisible(true);
          }
				}
			});
		}
		return showSelectedStream;
	}
  
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
            JFrame topLevel = (JFrame) parser.getUI().getParent().getParent().getParent().getParent(); 
            topLevel.dispose();
          }
				}
			});
		}
		return closeSelectedStream;
	}
  
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getStreamsList());
			jScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return jScrollPane;
	}
  
	private DefaultListModel getOpenedStreams() {
		if (openedStreams == null) {
			openedStreams = new DefaultListModel();
		}
		return openedStreams;
	}
  */
  
  
  
  

}
