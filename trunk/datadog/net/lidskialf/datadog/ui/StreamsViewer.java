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
import java.awt.*;

import javax.swing.*;

/**
 * @author Andrew de Quincey
 */
public abstract class StreamsViewer extends JScrollPane {

  /**
   * Spaces between streams should be treated as part of the stream above the separator in the display.
   */
  protected static final int SEPARATOR_PARTOF_STREAM_ABOVE_IT = 0;
  
  /**
   * Spaces between streams should be treated as part of the stream below the separator in the display.
   */
  protected static final int SEPARATOR_PARTOF_STREAM_BELOW_IT = 1;
  
  /**
   * Spaces between streams should be treated as invalid streams.
   */
  protected static final int SEPARATOR_INVALID = 2;

  
  
  
  /**
   * Constructor.
   */
  public StreamsViewer() {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    panel = new StreamsPanel();    
    getViewport().add(panel);
    
    columnHeader = new ColumnHeader();
    setColumnHeaderView(columnHeader);
    
    rowHeader = new RowHeader();
    setRowHeaderView(rowHeader);
    
    rowHeaderColour = new Color(207, 212, 255);
  }

  /**
   * Determine the Y position a stream with index streamIdx will be displayed at.
   * 
   * @param streamIdx The index of the stream.
   * @return The Y position of the top of the stream's rendering position.
   */
  protected int streamIndexToWindowYPosition(int streamIdx) {
    return streamIdx * (windowRowHeight + windowRowSeparation);
  }
  
  /**
   * Determine the streamIdx that a y position corresponds to.
   * 
   * @param yPos The y position.
   * @param separatorDisposition One of the SEPARATOR_* values, for controlling how the 
   * spaces between the streams are treated.  
   * @return The stream index, or -1 for an invalid y position.
   */
  protected int windowYPositionToStreamIndex(int yPos, int separatorDisposition) {
    
    // calculate the index
    int tmpIdx = -1;
    if (separatorDisposition == SEPARATOR_PARTOF_STREAM_BELOW_IT) {
      tmpIdx = (yPos + windowRowSeparation) / (windowRowHeight + windowRowSeparation);
    } else if (separatorDisposition == SEPARATOR_PARTOF_STREAM_ABOVE_IT) {
      if (yPos < 0) return -1;
      tmpIdx = yPos / (windowRowHeight + windowRowSeparation);
    } else if (separatorDisposition == SEPARATOR_INVALID) {
      if (yPos < 0) return -1;
      tmpIdx = yPos / (windowRowHeight + windowRowSeparation);
      if (yPos >= ((tmpIdx * (windowRowHeight + windowRowSeparation)) + windowRowHeight)) return -1;
    }
    
    // watch out for too many streams!
    return tmpIdx;
  }
  
  /**
   * Convert an X position in the window into a real position within the stream.
   * 
   * @param x The X position.
   * @return The stream position.
   */
  protected long windowXPositionToStreamPosition(int x) {
    return streamRealStart + (long) ((x << windowScalingFactor) + 0.5);
  }

  /**
   * Convert a real position within the stream into an X position in the window.
   * 
   * @param x The X position.
   * @return The stream position.
   */
  protected int streamPositionToWindowXPosition(long position) {
    return (int) ((position - streamRealStart) >> windowScalingFactor);
  }
  
  /**
   * Convert an width in the window into a real length of an area within the stream.
   * 
   * @param width The width.
   * @return The length of the area within the stream..
   */
  protected long windowWidthToStreamLength(int width) {
    return (long) ((width << windowScalingFactor) + 0.5);
  }
  
  /**
   * Convert a real length of an area within the stream into a width in the window.
   * 
   * @param width The width.
   * @return The length of the area within the stream..
   */
  protected int streamLengthToWindowWidth(long length) {
    return (int) (length >> windowScalingFactor);
  }
  
  /**
   * Set the horizontal dimensions of the stream. Also calculates an appropriate windowScalingFactor to avoid integer overflow issues.
   * 
   * @param streamsRealStart The real start position of the stream (so it can be nonzero).
   * @param streamsRealLength The real length of the stream.
   */
  protected void setStreamHDimensions(long streamRealStart, long streamRealLength) {
    // update the values
    this.streamRealStart = streamRealStart;
    this.streamRealLength = streamRealLength;
    this.streamRealEnd = streamRealStart + streamRealLength;
    
    // now choose a scaling factor to avoid integer overflow
    windowScalingFactor = 0;
    while(streamRealStart > Integer.MAX_VALUE) {
      windowScalingFactor++;
      streamRealStart>>=1;
    }
    
    // update the width of the window
    windowWidth = (int) (streamRealLength >> windowScalingFactor);
    Dimension curSize = panel.getPreferredSize();
    curSize.width = windowWidth;
    panel.setPreferredSize(curSize);
    panel.revalidate();
    
    // update the width of the column header if present
    if (columnHeader != null) {
      curSize = columnHeader.getPreferredSize();
      curSize.width = (int) (streamRealLength >> windowScalingFactor);
      columnHeader.setPreferredSize(curSize);
    }
  }
  
  /**
   * Render a position within the stream for display to the user.
   * 
   * @param position The position to render.
   * @return The string to display.
   */
  protected String renderStreamPosition(long position) {
    return "0x" + Long.toHexString(position);
  }
  
  /**
   * Update the dimensions of the row header when the list of rows changes.
   */
  protected void updateRowHeaderDimensions() {
    rowHeaderWidth = -1;
    if (rowHeader.getGraphics() == null) return;
    
    // work out the minimum width
    int minWidth = 0;
    FontMetrics fontMetrics = rowHeader.getGraphics().getFontMetrics();
    for(int i=0; i < rows.size(); i++) {
      String str = rows.get(i).toString();
      int curWidth = fontMetrics.stringWidth(str) + 2;
      if (curWidth > minWidth) {
        minWidth = curWidth;
      }
    }
    rowHeaderWidth = minWidth;
    
    // set the width
    Dimension curSize = rowHeader.getPreferredSize();
    curSize.width = rowHeaderWidth;
    rowHeader.setPreferredSize(curSize);
    rowHeader.revalidate();
  }
  
  /**
   * Paint the column header component.
   * 
   * @param g Graphics to paint into
   */
  protected void paintColumnHeader(Graphics g) {
    super.paintComponent(g);
    
    // calculate what to draw
    Rectangle clip = g.getClipBounds();
    long minStreamDrawPosition = windowXPositionToStreamPosition(clip.x); 
    long maxStreamDrawPosition = minStreamDrawPosition + windowWidthToStreamLength(clip.width);
    
    // round the min position down to the nearest major tick
    minStreamDrawPosition = (minStreamDrawPosition  / streamMajorTickSpacing) * streamMajorTickSpacing;
    
    // round the max position up to the nearest major tick
    maxStreamDrawPosition = ((maxStreamDrawPosition + streamMajorTickSpacing) / streamMajorTickSpacing) * streamMajorTickSpacing;
    
    // draw the ticks
    g.setColor(Color.black);
    for(long pos = minStreamDrawPosition; pos <= maxStreamDrawPosition; pos+= streamMinorTickSpacing) {
      int x = (int) (pos >> windowScalingFactor);
      
      // determine the kind of tick we need to draw
      if ((pos % streamMajorTickSpacing) == 0) {
        // draw major tick
        g.drawLine(x, 11, x, 20);
        
        // render the position string and draw it somewhere
        String rendered = renderStreamPosition(pos);
        int width = g.getFontMetrics().stringWidth(rendered);
        x -= (width/2);
        if (x < 0) x = 0;
        if (x > windowWidth) x = windowWidth - width;
        g.drawString(rendered, x, 10);
      } else {
        g.drawLine(x, 15, x, 20);
      }
    }
  }

  /**
   * Paint the row header component.
   * 
   * @param g Graphics to paint into
   */
  protected void paintRowHeader(Graphics g) {
    super.paintComponent(g);
    
    // work out what to redraw
    Rectangle clip = g.getClipBounds();
    int minStreamIdx = windowYPositionToStreamIndex(clip.y, SEPARATOR_PARTOF_STREAM_BELOW_IT);
    int maxStreamIdx = windowYPositionToStreamIndex(clip.y + clip.height + (windowRowSeparation + windowRowHeight-1), SEPARATOR_PARTOF_STREAM_ABOVE_IT);
    if (maxStreamIdx >= rows.size()) maxStreamIdx = rows.size()-1;
    
    // redraw it!
    int y = minStreamIdx * (windowRowSeparation + windowRowHeight);
    for(int i=minStreamIdx; i <= maxStreamIdx; i++) {
      g.setColor(rowHeaderColour);
      g.fillRect(0, y, rowHeaderWidth, windowRowHeight);
      
      g.setColor(Color.black);
      g.drawString(rows.get(i).toString(), 1, y + windowRowHeight);
      y += windowRowSeparation + windowRowHeight;
    }
  }

  /**
   * Repaint a bit of the streams panel
   * 
   * @param g The Graphics context.
   */
  protected abstract void paintStreamsPanel(Graphics g);
 
  
  
  /**
   * The column header
   * 
   * @author Andrew de Quincey
   */
  protected class ColumnHeader extends JPanel {
    
    public ColumnHeader() {
      setPreferredSize(new Dimension(0, 20));
    }
    
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      paintColumnHeader(g);
    }
  }
 
  /**
   * The row header
   * 
   * @author Andrew de Quincey
   */
  protected class RowHeader extends JPanel {
    
    public RowHeader() {
      setPreferredSize(new Dimension(20, 0));
    }
    
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      updateRowHeaderDimensions();
      paintRowHeader(g);
    }
  }
  
  
  /**
   * The scrollable display for streams.
   * 
   * @author Andrew de Quincey
   */
  protected class StreamsPanel extends JPanel implements Scrollable {
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      paintStreamsPanel(g);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
      return null;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
      return ((int) (streamMinorTickSpacing >> windowScalingFactor)) * 10;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
     */
    public boolean getScrollableTracksViewportHeight() {
      return true;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth() {
      return false;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
      return (int) (streamMinorTickSpacing >> windowScalingFactor);
    }
  }

  /**
   * The real start position of the stream.
   */
  protected long streamRealStart = 0;
  
  /**
   * The real length of the stream. 
   */
  protected long streamRealLength = 0;
  
  /**
   * The real end of the stream.
   */
  protected long streamRealEnd = 0;
  
  /**
   * The scaling factor applied to the real stream coordinates so they don't cause integer overflow in the view panel.
   */
  protected int windowScalingFactor = 0;

  /**
   * Width of the panel window in pixels.
   */
  protected int windowWidth = 0;
  
  /**
   * The height of a row in pixels.
   */
  protected int windowRowHeight = 10;
  
  /**
   * The separation between two rows in pixels.
   */
  protected int windowRowSeparation = 1;
  
  /**
   * Spacing between minor ticks in the column header for the viewer. This is in real stream units.
   */
  protected long streamMinorTickSpacing = 16;
  
  /**
   * Spacing between minor ticks in the column header for the viewer. This is in real stream units.
   */
  protected long streamMajorTickSpacing = 0x100;
  
  /**
   * The stream viewer component instance.
   */
  protected StreamsPanel panel; 
  
  /**
   * The column header component instance.
   */
  protected ColumnHeader columnHeader = null;
  
  /**
   * The row header component instance.
   */
  protected RowHeader rowHeader = null;
  
  /**
   * Width of the row header window.
   */
  protected int rowHeaderWidth = -1; 
  
  /**
   * The rows displayed by the viewer.
   */
  protected java.util.List rows = Collections.synchronizedList(new ArrayList());
  
  protected Color rowHeaderColour;
}
