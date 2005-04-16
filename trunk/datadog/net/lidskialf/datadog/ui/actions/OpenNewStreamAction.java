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
package net.lidskialf.datadog.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import net.lidskialf.datadog.*;
import net.lidskialf.datadog.ui.DataDog;

/**
 * Action to open a new stream from disk.
 * 
 * @author Andrew de Quincey
 */
public class OpenNewStreamAction extends AbstractAction {
  
  /**
   * Constructor.
   */
  public OpenNewStreamAction() {
    putValue(Action.NAME, "Open new stream...");
  }

  /* (non-Javadoc)
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) {
    JFileChooser chooser = new JFileChooser();
    int result = chooser.showOpenDialog(DataDog.getApplication().getFrame());
    if (result == JFileChooser.APPROVE_OPTION) {
      String filename = chooser.getSelectedFile().getName();
      String pathname = chooser.getSelectedFile().getAbsolutePath();
      
      // try each parser in turn until we get one which matches. FIXME: this could probably be made a lot smarter
      try {
        Bitstream stream = new FileBitstream(pathname);
        for(int i=0; i< DataDog.streamExplorerFactories.length; i++) {
          if (DataDog.streamExplorerFactories[i].probe(stream)) {
            StreamExplorer explorer = DataDog.streamExplorerFactories[i].open(stream);
            
            DataDog.getApplication().addNewStream(explorer);
            return;
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(DataDog.getApplication().getFrame(), 
            "An error during stream probing (" + t.getMessage() + ")", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      
      // if we get here, nothing matched
      JOptionPane.showMessageDialog(DataDog.getApplication().getFrame(), 
          "Sorry, the stream \"" + filename + "\" is unsupported.", 
          "Unsupported stream", 
          JOptionPane.WARNING_MESSAGE);
    }
  }
}
