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

import java.util.*;
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
    streamsList.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) {
          showStreamAction.actionPerformed(new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, ""));
        }
      }
    });
    
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
    showStreamAction = new ShowStreamAction(); 
    fileMenu.add(showStreamAction);
    fileMenu.add(new CloseStreamAction());
    quitAction = new QuitDataDogAction();
    fileMenu.add(quitAction);
    menuBar.add(fileMenu);
    
    JMenu helpMenu = new JMenu("Help");
    helpMenu.add(new AboutDataDogAction());
    menuBar.add(helpMenu);
    
    return menuBar;
  }
  
  /**
   * Get the main JFrame for the application.
   * 
   * @return The JFrame.
   */
  public JFrame getFrame() {
    return frame;
  }
  
  /**
   * Add a new stream to the central list.
   * 
   * @param explorer The StreamExplorer instance to add.
   */
  public void addNewStream(StreamExplorer explorer) {
    openedStreams.addElement(explorer);
    
    JFrame explorerFrame = new JFrame(explorer.toString());
    JComponent explorerUi = explorer.buildUI();
    explorerFrame.getContentPane().add(explorerUi);
    
    openedStreamsUis.put(explorer, explorerFrame);
    
    JMenuBar menuBar = explorer.buildMenuBar();
    if (menuBar != null) {
      explorerFrame.setJMenuBar(menuBar);
    }
    
    explorerFrame.pack();
    explorerFrame.setVisible(true);
  }
  
  /**
   * Get the currently selected stream.
   * 
   * @return The StreamExplorer instance, or null if nothing is selected.
   */
  public StreamExplorer getSelectedStream() {
    return (StreamExplorer) streamsList.getSelectedValue();
  }
  
  /**
   * Close a stream.
   * 
   * @param explorer The StreamExplorer to close.
   */
  public void closeStream(StreamExplorer explorer) {
    // destroy the UI.
    JFrame explorerFrame = (JFrame) openedStreamsUis.get(explorer);
    if (explorerFrame != null) {
      explorerFrame.dispose();
      openedStreamsUis.remove(explorer);
    }
    
    explorer.close();
    openedStreams.removeElement(explorer);
  }
  
  /**
   * Set the visibility of a particular stream's explorer.
   * 
   * @param explorer The StreamExplorer in question.
   * @param visible True to set it visible, false to hide it.
   */
  public void setStreamVisibility(StreamExplorer explorer, boolean visible) {
    JFrame explorerFrame = (JFrame) openedStreamsUis.get(explorer);
    if (explorerFrame != null) {
      explorerFrame.setVisible(visible);
    }
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
    
    // create the main app.
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
  private Action quitAction;
  private Action showStreamAction;
  private DefaultListModel openedStreams;
  private Map openedStreamsUis = Collections.synchronizedMap(new HashMap());
  
  /**
   * The application instance.
   */
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
}
