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

import java.awt.Graphics;

import net.lidskialf.datadog.ui.StreamsViewer;

/**
 * @author Andrew de Quincey
 *
 */
public class TransportStreamsViewer extends StreamsViewer {

  /**
   * 
   */
  public TransportStreamsViewer() {
    super();
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.lidskialf.datadog.ui.StreamWidget#paintStreamsPanel(java.awt.Graphics, int, int, long, long)
   */
  protected void paintStreamsPanel(Graphics g, int minStreamIdx,
      int maxStreamIdx, long minStreamPosition, long length) {
    // TODO Auto-generated method stub

  }
}
