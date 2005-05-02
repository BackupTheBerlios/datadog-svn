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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import javax.swing.*;

import net.lidskialf.datadog.Substream;
import net.lidskialf.datadog.ui.actions.*;

/**
 * Generic Row Header for the StreamViewer.
 *
 * @author Andrew de Quincey
 */
public class StreamsViewerRowHeader extends JPanel implements StreamsViewerChangeListener, MouseMotionListener, MouseListener, ActionInformationSource {

    /**
     * The StreamsViewer instance we are associated with.
     */
    protected StreamsViewer viewer;

    /**
     * Index of the selector when dragging a substream.
     */
    protected int curSelectorIndex = -1;

    /**
     * Index of the selected substream.
     */
    protected int selectedIndex = -1;

    /**
     * The selected substream.
     */
    protected Substream selectedSubstream = null;

    protected JPopupMenu substreamPopupMenu;

    protected ActionGroup substreamPopupMenuActions;



    /**
     * Constructor.
     *
     * @param viewer
     *            The StreamsViewer we are associated with.
     */
    public StreamsViewerRowHeader(StreamsViewer viewer) {
        this.viewer = viewer;

        viewer.addStreamsViewerChangeListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        setPreferredSize(new Dimension(20, 0));

        createPopupMenus();
    }

    public void bookmarkAdded(StreamsViewerChangeEvent e) {
    }

    public void bookmarkChanged(StreamsViewerChangeEvent e) {
    }

    public void bookmarkMoved(StreamsViewerChangeEvent e) {
    }

    public void bookmarkRemoved(StreamsViewerChangeEvent e) {
    }

    public void lengthChanged(StreamsViewerChangeEvent e) {
    }

    public void zoomChanged(StreamsViewerChangeEvent e) {
    }

    public void substreamAdded(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    public void substreamChanged(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    public void substreamRemoved(StreamsViewerChangeEvent e) {
        updateDimensions();
    }

    public void substreamMoved(StreamsViewerChangeEvent e) {
        repaint();
    }

    public void mouseDragged(MouseEvent e) {
        if (curSelectorIndex != -1) {
            int tmpSelectorIndex = viewer.panelYPositionToStreamIndex(e.getY());
            if (tmpSelectorIndex <= viewer.substreamsCount()) {
                repaintSelector();
                curSelectorIndex = tmpSelectorIndex;
                repaintSelector();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        int index = viewer.panelYPositionToStreamIndex(e.getY());
        Substream substream = viewer.getSubstream(index);
        if (substream != null) {
            setToolTipText(substream.getDescription());
        }
    }

    public void mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {
            updateSelectedSubstream(e);
            substreamPopupMenuActions.update();
            substreamPopupMenu.show(this, e.getX(), e.getY());
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            updateSelectedSubstream(e);
            substreamPopupMenuActions.update();
            substreamPopupMenu.show(this, e.getX(), e.getY());
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            updateSelectedSubstream(e);
            curSelectorIndex = selectedIndex;
            repaintSelector();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (curSelectorIndex != -1) {
            viewer.moveSubstream(selectedIndex, curSelectorIndex);

            repaintSelector();
            selectedIndex = -1;
            selectedSubstream = null;
            curSelectorIndex = -1;
        }
    }

    public Object getActionParameter(String name) {
        // return what it asks for
        if (name == "substreamIndex") {
            if (selectedIndex == -1) return null;
            return new Integer(selectedIndex);
        } else if (name == "substream") {
            return selectedSubstream;
        }

        // unknown
        return null;
    }

    public boolean isActionEnabled(String action) {
        if ((action == "EditSubstreamAction") && (selectedIndex != -1)) return true;

        return false;
    }

    protected void updateSelectedSubstream(MouseEvent e) {
        selectedIndex = viewer.panelYPositionToStreamIndex(e.getY());
        if (selectedIndex < viewer.substreamsCount()) {
            selectedSubstream = viewer.getSubstream(selectedIndex);
        } else {
            selectedIndex = -1;
            selectedSubstream = null;
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // dimensions
        int rowHeight = viewer.substreamHeight();
        int rowWidth = getWidth() - 2;

        // work out what to redraw
        Rectangle clip = g.getClipBounds();
        int minStreamIdx = viewer.panelYPositionToStreamIndex(clip.y);
        int maxStreamIdx = viewer.panelYPositionToStreamIndex(clip.y + clip.height + (rowHeight - 1));

        // redraw it!
        int y = minStreamIdx * rowHeight;
        for (int i = minStreamIdx; i <= maxStreamIdx; i++) {
            // get the Substream instance if it exists
            Substream substream = viewer.getSubstream(i);
            if (substream == null) {
                continue;
            }

            // draw the cell
            g.setColor(substream.getColour());
            g.fillRect(1, y+1, rowWidth-1, rowHeight-1);
            g.setColor(Color.black);
            g.drawRect(0, y, rowWidth, rowHeight);

            // draw the text
            g.drawString(substream.getLabel(), 1, y + rowHeight-1);

            // next row
            y += rowHeight;
        }

        // draw the selector if present
        if ((curSelectorIndex != -1) && (minStreamIdx <= curSelectorIndex) && (maxStreamIdx >= curSelectorIndex)) {
            g.setColor(Color.red);
            g.drawLine(0, curSelectorIndex * rowHeight, rowWidth, curSelectorIndex * rowHeight);
        }
    }

    /**
     * Repaint the current selector position.
     */
    protected void repaintSelector() {
        if (curSelectorIndex != -1) {
            repaint(0, curSelectorIndex * viewer.substreamHeight(), getWidth(), (curSelectorIndex * viewer.substreamHeight())+1);
        }
    }

    /**
     * Update the dimensions of the row header when the list of rows changes.
     */
    protected void updateDimensions() {
        Graphics g = getGraphics();
        if (g == null)
            return;

        // work out the minimum width
        int minWidth = 0;
        FontMetrics fontMetrics = g.getFontMetrics();
        Iterator it = viewer.getSubstreams();
        while(it.hasNext()) {
            Substream substream = (Substream) it.next();

            Rectangle2D bounds = fontMetrics.getStringBounds(substream.getLabel(), g);
            if (bounds.getWidth() > minWidth) {
                minWidth = (int) Math.round(bounds.getWidth());
            }
        }

        // set the row height
        viewer.setSubstreamHeight(13); // FIXME: hardcoded just now 'cos fontmetrics return ridiculously large values for font height

        // set the width
        Dimension curSize = getPreferredSize();
        curSize.width = minWidth + 3;
        setPreferredSize(curSize);
        revalidate();
        repaint();
    }

    /**
     * Create the popup menus.
     */
    protected void createPopupMenus() {
        if (substreamPopupMenu != null) return;

        substreamPopupMenu = new JPopupMenu();
        substreamPopupMenuActions = new ActionGroup();

        substreamPopupMenu.add(substreamPopupMenuActions.add(new AddSubstreamAction(viewer, this)));
        substreamPopupMenu.add(substreamPopupMenuActions.add(new EditSubstreamAction(viewer, this)));
        substreamPopupMenu.add(substreamPopupMenuActions.add(new RemoveSubstreamAction(viewer, this)));
    }
}
