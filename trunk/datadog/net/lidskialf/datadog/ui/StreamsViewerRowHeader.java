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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * Generic Row Header for the StreamViewer.
 * 
 * @author Andrew de Quincey
 */
public class StreamsViewerRowHeader extends JPanel implements ListDataListener {
  
  /**
   * Constructor.
   * 
   * @param viewer The StreamsViewer we are associated with.
   * @param rowModel The model of the rows.
   */
  public StreamsViewerRowHeader(StreamsViewer viewer, ListModel rowModel) {
    this.viewer = viewer;
    this.rowModel = rowModel;

    // FIXME: remove this when a full implementation is done
    rowHeaderColour = new Color(207, 212, 255);

    setPreferredSize(new Dimension(20, 0));
    rowModel.addListDataListener(this);
  }
  
  /* (non-Javadoc)
   * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
   */
  public void contentsChanged(ListDataEvent arg0) {
    updateRowHeaderDimensions();
  }
  
  /* (non-Javadoc)
   * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
   */
  public void intervalAdded(ListDataEvent arg0) {
    updateRowHeaderDimensions();
  }
  
  /* (non-Javadoc)
   * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
   */
  public void intervalRemoved(ListDataEvent arg0) {
    updateRowHeaderDimensions();
  }
  
  /* (non-Javadoc)
   * @see javax.swing.JComponent#print(java.awt.Graphics)
   */
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // work out what to redraw
    Rectangle clip = g.getClipBounds();
    int minStreamIdx = viewer.windowYPositionToStreamIndex(clip.y, StreamsViewer.SEPARATOR_PARTOF_STREAM_BELOW_IT);
    int maxStreamIdx = viewer.windowYPositionToStreamIndex(clip.y + clip.height + (viewer.panelRowSeparation + viewer.panelRowHeight-1), StreamsViewer.SEPARATOR_PARTOF_STREAM_ABOVE_IT);
    if (maxStreamIdx >= rowModel.getSize()) maxStreamIdx = rowModel.getSize()-1;
    
    // redraw it!
    int y = minStreamIdx * (viewer.panelRowSeparation + viewer.panelRowHeight);
    for(int i=minStreamIdx; i <= maxStreamIdx; i++) {
      g.setColor(rowHeaderColour);
      g.fillRect(0, y, rowHeaderWidth, viewer.panelRowHeight);
      
      g.setColor(Color.black);
      g.drawString(rowModel.getElementAt(i).toString(), 1, y + viewer.panelRowHeight);
      y += viewer.panelRowSeparation + viewer.panelRowHeight;
    }
  }
  
  
  /**
   * Update the dimensions of the row header when the list of rows changes.
   */
  private void updateRowHeaderDimensions() {
    rowHeaderWidth = -1;
    if (getGraphics() == null) return;
    
    // work out the minimum width
    int minWidth = 0;
    FontMetrics fontMetrics = getGraphics().getFontMetrics();
    for(int i=0; i < rowModel.getSize(); i++) {
      String str = rowModel.getElementAt(i).toString();
      int curWidth = fontMetrics.stringWidth(str) + 2;
      if (curWidth > minWidth) {
        minWidth = curWidth;
      }
    }
    rowHeaderWidth = minWidth;
    
    // set the width
    Dimension curSize = getPreferredSize();
    curSize.width = rowHeaderWidth;
    setPreferredSize(curSize);
    revalidate();
    repaint();
  }
  
  protected int rowHeaderWidth = -1; 
  protected ListModel rowModel;
  protected Color rowHeaderColour;
  protected StreamsViewer viewer;
}
