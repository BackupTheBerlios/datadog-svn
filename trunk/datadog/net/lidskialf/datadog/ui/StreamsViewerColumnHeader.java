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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

/**
 * A column header used with the StreamsViewer.
 * 
 * @author Andrew de Quincey
 */
public class StreamsViewerColumnHeader extends JPanel implements MouseMotionListener, MouseListener {
  
  /**
   * Constructor.
   * 
   * @param viewer The StreamsViewer we will be used with.
   */
  public StreamsViewerColumnHeader(StreamsViewer viewer) {
    this.viewer = viewer;
    setPreferredSize(new Dimension(0, 20));
    addMouseMotionListener(this);
    addMouseListener(this);
  }
  
  /* (non-Javadoc)
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   */
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    // calculate what to draw
    Rectangle clip = g.getClipBounds();
    long minStreamDrawPosition = viewer.windowXPositionToStreamPosition(clip.x); 
    long maxStreamDrawPosition = minStreamDrawPosition + viewer.windowWidthToStreamLength(clip.width);
    
    // round the min position down to the nearest major tick
    minStreamDrawPosition = (minStreamDrawPosition  / viewer.streamMajorTickSpacing) * viewer.streamMajorTickSpacing;
    
    // round the max position up to the nearest major tick
    maxStreamDrawPosition = ((maxStreamDrawPosition + viewer.streamMajorTickSpacing) / viewer.streamMajorTickSpacing) * viewer.streamMajorTickSpacing;
    
    // draw the ticks
    g.setColor(Color.black);
    for(long pos = minStreamDrawPosition; pos <= maxStreamDrawPosition; pos+= viewer.streamMinorTickSpacing) {
      int x = (int) (pos >> viewer.windowScalingFactor);
      
      // determine the kind of tick we need to draw
      if ((pos % viewer.streamMajorTickSpacing) == 0) {
        // draw major tick
        g.drawLine(x, 11, x, 20);
        
        // render the position string and draw it somewhere
        String rendered = viewer.renderStreamPosition(pos);
        int width = g.getFontMetrics().stringWidth(rendered);
        x -= (width/2);
        if (x < 0) x = 0;
        if (x > viewer.windowWidth) x = viewer.windowWidth - width;
        g.drawString(rendered, x, 10);
      } else {
        g.drawLine(x, 15, x, 20);
      }
    }
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
   */
  public void mouseDragged(MouseEvent arg0) {
    // FIXME: do something interesting with bookmarks
  }

  /* (non-Javadoc)
   * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
   */
  public void mouseMoved(MouseEvent e) {
    // FIXME
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
   */
  public void mouseClicked(MouseEvent arg0) {
    // TODO Auto-generated method stub
  }
  
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
   */
  public void mouseEntered(MouseEvent arg0) {
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
   */
  public void mouseExited(MouseEvent arg0) {
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
  public void mousePressed(MouseEvent arg0) {
  }
  
  /* (non-Javadoc)
   * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
   */
  public void mouseReleased(MouseEvent arg0) {
  }
  
  /**
   * The StreamsViewer we're attached to.
   */
  protected StreamsViewer viewer;
}
