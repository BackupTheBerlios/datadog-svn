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
import java.util.*;
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


  protected static final int CHANGED_ZOOM = 0;
  protected static final int CHANGED_LENGTH = 1;
  
  
  
  
  
  /**
   * Constructor.
   */
  public StreamsViewer() {
    super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    panel = new StreamsPanel();    
    getViewport().add(panel);
    
    setPreferredSize(new Dimension(600, 200));
  }
  
  /**
   * Add a StreamsViewerChangeListener to receive events from this StreamsViewer.
   * 
   * @param listener The listener to add.
   */
  public void addStreamsViewerChangeListener(StreamsViewerChangeListener listener) {
    changeListeners.add(listener);
  }
  
  /**
   * Remove a StreamsViewerChangeListener so it no longer receives events from this StreamsViewer.
   * 
   * @param listener The listener to remove.
   */
  public void removeStreamsViewerChangeListener(StreamsViewerChangeListener listener) {
    changeListeners.remove(listener);
  }

  /**
   * Determine the Y position a stream with index streamIdx will be displayed at.
   * 
   * @param streamIdx The index of the stream.
   * @return The Y position of the top of the stream's rendering position.
   */
  public int streamIndexToWindowYPosition(int streamIdx) {
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
  public int windowYPositionToStreamIndex(int yPos, int separatorDisposition) {
    
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
  public long windowXPositionToAbsPosition(int x) {
    return (long) ((x << curZoomFactor) + 0.5);
  }

  /**
   * Convert a real position within the stream into an X position in the window.
   * 
   * @param x The X position.
   * @return The stream position.
   */
  public int absPositionToWindowXPosition(long position) {
    return (int) ((position) >> curZoomFactor);
  }
  
  /**
   * Convert an width in the window into a real length of an area within the stream.
   * 
   * @param width The width.
   * @return The length of the area within the stream..
   */
  public long windowWidthToStreamLength(int width) {
    return (long) ((width << curZoomFactor) + 0.5);
  }
  
  /**
   * Convert a real length of an area within the stream into a width in the window.
   * 
   * @param width The width.
   * @return The length of the area within the stream..
   */
  public int streamLengthToWindowWidth(long length) {
    return (int) (length >> curZoomFactor);
  }
  
  /**
   * Tell the StreamsViewer to zoom in one step.
   */
  public void zoomIn() {
    if (curZoomFactor == minZoomFactor) return; 
      
    curZoomFactor--;
    fireChangeListeners(CHANGED_ZOOM);
    panel.repaint();
  }
  
  /**
   * Tell the StreamsViewer to zoom out one step.
   */
  public void zoomOut() {
    if (curZoomFactor == maxZoomFactor) return;
    
    curZoomFactor++;
    fireChangeListeners(CHANGED_ZOOM);
    panel.repaint();
  }

  /**
   * Render a position within the stream for display to the user.
   * 
   * @param position The position to render.
   * @return The string to display.
   */
  public String renderStreamPosition(long position) {
    return "0x" + Long.toHexString(position);
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  /**
   * Fires all change listener events for the given changeType.
   * 
   * @param changeType The type of change that occured (One of CHANGED_*).
   */
  protected void fireChangeListeners(int changeType) {
    Iterator it = changeListeners.iterator();
    while(it.hasNext()) {
      StreamsViewerChangeListener curListener = (StreamsViewerChangeListener) it.next();
      
      switch(changeType) {
      case CHANGED_ZOOM:
        curListener.zoomChanged(this, curZoomFactor);
        break;
        
      case CHANGED_LENGTH:
        curListener.lengthChanged(this, absoluteLength);
        break;
      }
    }
  }
  
  /**
   * Set the horizontal dimensions of the stream. Also calculates appropriate minZoomFactor/maxZoomFactors to avoid integer overflow issues.
   *  
   * @param streamsRealLength The absolute length of the stream.
   */
  protected void setStreamHDimensions(long absoluteLength) {
    // update the values
    this.absoluteLength = absoluteLength;
    
    // FIXME: need to choose appropriate min and max zoom values here!!!!!
    
    // now choose a minimum zoom factor to avoid integer overflow
    minZoomFactor = 0;
    while(absoluteLength > Integer.MAX_VALUE) {
      minZoomFactor++;
      absoluteLength >>= 1;
    }
    curZoomFactor = minZoomFactor;
    
    // update the width of the panel
    windowWidth = (int) (absoluteLength >> curZoomFactor);
    Dimension curSize = panel.getPreferredSize();
    curSize.width = windowWidth;
    panel.setPreferredSize(curSize);
    panel.revalidate();
    
    fireChangeListeners(CHANGED_LENGTH);
  }
  
  /**
   * Repaint a bit of the streams panel
   * 
   * @param g The Graphics context.
   */
  protected abstract void paintStreamsPanel(Graphics g);

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
      return ((int) (streamMinorTickSpacing >> curZoomFactor)) * 10;
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
      return (int) (streamMinorTickSpacing >> curZoomFactor);
    }
  }
  
  

  /**
   * The absolute length of the stream. 
   */
  protected long absoluteLength = 0;
  
  /**
   * The zoom factor applied to the absolute stream positions.
   */
  protected int curZoomFactor = 0;
  
  /**
   * The minimum permitted zoom factor (chosen to avoid integer overflows for very long streams).
   */
  protected int minZoomFactor = 0;

  /**
   * The maximum permitted zoom factor (chosen to avoid performance degradation for large streams).
   */
  protected int maxZoomFactor = 10;
  
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
  
  private java.util.List changeListeners = Collections.synchronizedList(new ArrayList());
}
