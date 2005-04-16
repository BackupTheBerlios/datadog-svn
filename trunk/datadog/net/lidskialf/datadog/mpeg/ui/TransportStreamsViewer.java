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

import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 * @author Andrew de Quincey
 *
 */
public class TransportStreamsViewer extends StreamsViewer {

  /**
   * Constructor.
   */
  public TransportStreamsViewer(TransportStream stream, DefaultListModel rowModel) throws IOException {
    super();
    this.rowModel = rowModel;
    this.stream = stream;
    
    panel.setBackground(Color.white);
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
    
    try {
      long minStreamPosition = stream.round(windowXPositionToStreamPosition(clip.x), TransportStream.ROUND_DOWN);
      long maxStreamPosition = stream.round(minStreamPosition + windowWidthToStreamLength(clip.width), TransportStream.ROUND_INC);
      
      // render each packet
      for(long curPos = minStreamPosition; curPos <= maxStreamPosition; curPos+=Constants.TS_PACKET_LENGTH) {
        // get the packet
        TransportPacket packet = stream.getPacketAt(curPos);
        if (packet == null) continue;
        
        // find/create a row for the PID
        int pid = packet.pid();
        TransportStreamRow row = getRowForPid(pid);
        
        // draw it if it is within the bounds
        if ((row.rowIdx >= minStreamIdx) && (row.rowIdx <= maxStreamIdx)) {
          int x = streamPositionToWindowXPosition(curPos);
          int y = streamIndexToWindowYPosition(row.rowIdx);
          g.setColor(Color.green);
          g.fillRect(x, y, packetWidth, windowRowHeight);
          g.setColor(Color.black);
          g.drawRect(x, y, packetWidth, windowRowHeight);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, 
          "An error during stream rendering (" + e.getMessage() + ")", 
          "Error", 
          JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private TransportStreamRow getRowForPid(int pid) {
    
    // retrieve the old version if present
    Integer pidI = new Integer(pid);
    if (pidToRowDescriptor.containsKey(pidI)) {
      return (TransportStreamRow) pidToRowDescriptor.get(pidI);
    }
    
    // create a new one
    TransportStreamRow newDesc = new TransportStreamRow(pid);
    
    // find where to insert it
    boolean inserted = false;
    for(int i=0; i< rowModel.getSize(); i++) {
      TransportStreamRow curDesc = (TransportStreamRow) rowModel.getElementAt(i);
      
      // found a pid greater than our current one? insert it!
      if (!inserted) {
        if (curDesc.pid > newDesc.pid) {
          newDesc.rowIdx = i;
          rowModel.add(i, newDesc);
          pidToRowDescriptor.put(pidI, newDesc);
          inserted = true;
          i++;
          curDesc.rowIdx = i;
        }
      } else {
        // update all row indexes after the inserted position
        curDesc.rowIdx = i;
      }
    }
    
    // if we didn't insert it anywhere, append it
    if (!inserted) {
      newDesc.rowIdx = rowModel.getSize();
      rowModel.addElement(newDesc);
      pidToRowDescriptor.put(pidI, newDesc);
    }
    
    return newDesc;
  }
  
  
  /**
   * A Row tailored for transport streams.
   * 
   * @author Andrew de Quincey
   */
  private class TransportStreamRow {
    
    /**
     * Constructor.
     * 
     * @param pid PID this row is representing.
     */
    public TransportStreamRow(int pid) {
      String tmp = Integer.toHexString(pid);
      while(tmp.length() < 4) {
        tmp = "0" + tmp; 
      }
      description = "0x" + tmp;
      this.pid = pid;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
      return description;
    }
    
    private String description;
    
    /**
     * The PID of the row.
     */
    public int pid;
    
    public int rowIdx;
  }
  
  /**
   * The stream we are viewing.
   */
  private TransportStream stream;
  
  /**
   * PID -> row descriptor.
   */
  private Map pidToRowDescriptor = Collections.synchronizedMap(new HashMap());
  
  private int packetWidth = (int) streamLengthToWindowWidth(Constants.TS_PACKET_LENGTH);
  
  private DefaultListModel rowModel; 
}
