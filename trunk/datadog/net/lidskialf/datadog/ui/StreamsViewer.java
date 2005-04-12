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
import javax.swing.*;

/**
 * @author Andrew de Quincey
 */
public abstract class StreamsViewer extends JScrollPane {

  /**
   * Constructor.
   */
  public StreamsViewer() {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    panel = new StreamsPanel();    
    getViewport().add(panel);
  }

  /**
   * Determine the Y position a stream with index streamIdx will be displayed at.
   * 
   * @param streamIdx The index of the stream.
   * @return The Y position of the top of the stream's rendering position.
   */
  protected int StreamIndexToYPosition(int streamIdx) {
    return yBorder + (streamIdx * (streamPacketHeight + streamSeparationHeight));
  }
  
  /**
   * Determine the streamIdx that a y position corresponds to.
   * 
   * @param yPos The y position.
   * @param separatorDisposition One of the SEPARATOR_* values, for controlling how the 
   * spaces between the streams are treated.  
   * @return The stream index, or -1 for an invalid y position.
   */
  protected int StreamYPositionToStreamIndex(int yPos, int separatorDisposition) {
    
    // calculate the index
    yPos -= yBorder;
    int tmpIdx = -1;
    if (separatorDisposition == SEPARATOR_PARTOF_STREAM_BELOW_IT) {
      tmpIdx = (yPos + streamSeparationHeight) / (streamPacketHeight + streamSeparationHeight);
    } else if (separatorDisposition == SEPARATOR_PARTOF_STREAM_ABOVE_IT) {
      if (yPos < 0) return -1;
      tmpIdx = yPos / (streamPacketHeight + streamSeparationHeight);
    } else if (separatorDisposition == SEPARATOR_INVALID) {
      if (yPos < 0) return -1;
      tmpIdx = yPos / (streamPacketHeight + streamSeparationHeight);
      if (yPos >= ((tmpIdx * (streamPacketHeight + streamSeparationHeight)) + streamPacketHeight)) return -1;
    }
    
    // watch out for too many streams!
    if (tmpIdx >= streamCount) return -1;
    return tmpIdx;
  }

  /**
   * The scrollable display for streams.
   * 
   * @author Andrew de Quincey
   */
  protected class StreamsPanel extends JComponent implements Scrollable {

    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      // calculate which area of the stream we need to redraw
      Rectangle clip = g.getClipBounds();
      int minStreamIdx = StreamYPositionToStreamIndex(clip.y, SEPARATOR_PARTOF_STREAM_BELOW_IT);
      int maxStreamIdx = StreamYPositionToStreamIndex(clip.y + clip.height, SEPARATOR_PARTOF_STREAM_ABOVE_IT);
      long minStreamPosition = streamsStartPosition + (long) ((clip.x << streamsScalingFactor) + 0.5);
      long length = (long) ((clip.width << streamsScalingFactor) + 0.5);
      
      paintStreamsPanel(g, minStreamIdx, maxStreamIdx, minStreamPosition, length);
    }

    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
      return new Dimension(500, 200);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
      // TODO Auto-generated method stub
      return 5;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
     */
    public boolean getScrollableTracksViewportHeight() {
      // TODO Auto-generated method stub
      return true;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth() {
      // TODO Auto-generated method stub
      return true;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
      // TODO Auto-generated method stub
      return 5;
    }
  }
  
  /**
   * Repaint a bit of the streams panel
   * 
   * @param g The Graphics context.
   * @param minStreamIdx The minimum stream index to repaint.
   * @param maxStreamIdx The maximum stream index to repaint.
   * @param minStreamPosition The minimum position in the streams. 
   * @param length The length of the area to be repainted.
   */
  protected abstract void paintStreamsPanel(Graphics g, int minStreamIdx, int maxStreamIdx, long minStreamPosition, long length);
   
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
   * The number of streams being displayed.
   */
  protected int streamCount = 0;
    
  /**
   * The height of a packet in a stream in pixels.
   */
  protected int streamPacketHeight = 10;
  
  /**
   * The separation between two streams in pixels.
   */
  protected int streamSeparationHeight = 5;
  
  /**
   * The Y border at the top of the window - FIXME - maybe this should be replaced with just a normal border?
   */
  protected int yBorder = 5;
  
  /**
   * The minimum position across all the streams (inclusively).
   */
  protected long streamsStartPosition = 0;
  
  /**
   * The maximum position across all the streams (exclusively).
   */
  protected long streamsEndPosition = 0;

  /**
   * The maxmimum real (unscaled) length across all the streams. 
   */
  protected long streamsRealLength = 0;
  
  /**
   * The scaling factor applied to the dimensions of the view panel (e.g. real stream position = (xpos << streamScalingFactor))
   */
  protected long streamsScalingFactor = 1;

  /**
   * The stream viewer component instance.
   */
  protected StreamsPanel panel; 
}
