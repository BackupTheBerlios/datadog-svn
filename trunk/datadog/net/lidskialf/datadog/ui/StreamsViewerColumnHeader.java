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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 * A generic ColumnHeader for a StreamsViewer.
 * 
 * @author Andrew de Quincey
 */
public class StreamsViewerColumnHeader extends JPanel implements StreamsViewerChangeListener {
  
  /**
   * Constructor.
   * 
   * @param viewer The associated StreamsViewer. 
   */
  public StreamsViewerColumnHeader(StreamsViewer viewer) {
    this.viewer = viewer;
    viewer.addStreamsViewerChangeListener(this);
    
    setPreferredSize(new Dimension(0, 20));
    updateDimensions();
  }
  
  
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // calculate what to draw
    Rectangle clip = g.getClipBounds();
    long minStreamDrawPosition = viewer.panelXPositionToAbsPosition(clip.x); 
    long maxStreamDrawPosition = minStreamDrawPosition + viewer.panelWidthToStreamLength(clip.width);
    
    // round the min position down to the nearest major tick
    minStreamDrawPosition = (minStreamDrawPosition  / viewer.streamMajorTickSpacing) * viewer.streamMajorTickSpacing;
    
    // round the max position up to the nearest major tick
    maxStreamDrawPosition = ((maxStreamDrawPosition + viewer.streamMajorTickSpacing) / viewer.streamMajorTickSpacing) * viewer.streamMajorTickSpacing;
    
    // draw the ticks
    g.setColor(Color.black);
    for(long pos = minStreamDrawPosition; pos <= maxStreamDrawPosition; pos+= viewer.streamMinorTickSpacing) {
      int x = viewer.absPositionToPanelXPosition(pos);
      
      // determine the kind of tick we need to draw
      if ((pos % viewer.streamMajorTickSpacing) == 0) {
        // draw major tick
        g.drawLine(x, 11, x, 20);
        
        // render the position string and draw it somewhere
        String rendered = viewer.renderStreamPosition(pos);
        int width = g.getFontMetrics().stringWidth(rendered);
        x -= (width/2);
        if (x < 0) x = 0;
        if (x > panelWidth) x = panelWidth - width;
        g.drawString(rendered, x, 10);
      } else {
        g.drawLine(x, 15, x, 20);
      }
    }
  }
  
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#lengthChanged(net.lidskialf.datadog.ui.StreamsViewer, long)
   */
  public void lengthChanged(StreamsViewer viewer, long newLength) {
    updateDimensions();
  }
  
  /* (non-Javadoc)
   * @see net.lidskialf.datadog.ui.StreamsViewerChangeListener#zoomChanged(net.lidskialf.datadog.ui.StreamsViewer, int)
   */
  public void zoomChanged(StreamsViewer viewer, int newZoom) {
    updateDimensions();
  }
  
  /**
   * Update the dimensions of the column header.
   */
  protected void updateDimensions() {
    panelWidth = viewer.getTotalPanelWidth();
    Dimension curSize = getPreferredSize();
    curSize.width = panelWidth;
    setPreferredSize(curSize);
  }
  
  /**
   * The StreamsViewer we are associated with.
   */
  protected StreamsViewer viewer;
  
  /**
   * The width of the stream panel window in pixels.
   */
  protected int panelWidth;
}
