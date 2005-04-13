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
package net.lidskialf.datadog.mpeg.ui;


import net.lidskialf.datadog.ui.*;
import net.lidskialf.datadog.mpeg.bitstream.*;

import java.awt.*;
import java.io.*;

/**
 * @author Andrew de Quincey
 *
 */
public class TransportStreamsViewer extends StreamsViewer {

  /**
   * Constructor.
   */
  public TransportStreamsViewer() {
    super();
    panel.setBackground(Color.white);
    
    rows.add(new TransportStreamRowDescriptor(0));
    rows.add(new TransportStreamRowDescriptor(1));
    rows.add(new TransportStreamRowDescriptor(2));
    rows.add(new TransportStreamRowDescriptor(3));
  }
  
  /**
   * Set the TransportStream viewed by this component.
   * 
   * @param stream The stream.
   * @throws IOException On error.
   */
  public void setStream(TransportStream stream) throws IOException {
    this.stream = stream;
    setStreamHDimensions(stream.startPosition(), stream.length());
  }

  /* (non-Javadoc)
   * @see net.lidskialf.datadog.ui.StreamWidget#paintStreamsPanel(java.awt.Graphics, int, int, long, long)
   */
  protected void paintStreamsPanel(Graphics g) {
    
    // calculate which area of the stream we need to redraw
    Rectangle clip = g.getClipBounds();
    int minStreamIdx = windowYPositionToStreamIndex(clip.y, SEPARATOR_PARTOF_STREAM_BELOW_IT);
    int maxStreamIdx = windowYPositionToStreamIndex(clip.y + clip.height, SEPARATOR_PARTOF_STREAM_ABOVE_IT);
    long minStreamPosition = windowXPositionToStreamPosition(clip.x); 
    long length = windowWidthToStreamLength(clip.width);

    // FIXME: do something
  }
  
  /**
   * RowDescriptor tailored for transport streams.
   * 
   * @author Andrew de Quincey
   */
  private class TransportStreamRowDescriptor extends StreamsViewerRowDescriptor {
    
    /**
     * Constructor.
     * 
     * @param pid PID this row is representing.
     */
    public TransportStreamRowDescriptor(int pid) {
      String tmp = Integer.toHexString(pid);
      while(tmp.length() < 4) {
        tmp = "0" + tmp; 
      }
      description = "0x" + tmp;
    }

    /**
     * The PID of the row.
     */
    public int pid;
  }
  
  protected TransportStream stream;
}
