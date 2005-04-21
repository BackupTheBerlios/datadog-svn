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

import net.lidskialf.datadog.*;

import java.awt.*;
import javax.swing.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class BookmarkEditor {

    private boolean editing = true;

    private StreamsViewer viewer;

    private long position;

    private StreamBookmark bookmark;

    private JDialog dialog;

    private JSpinner positionField;

    private JTextField descriptionField;

    private JButton colourButton;

    private JButton okButton;

    private JButton cancelButton;

    private JButton applyButton;


    /**
     * Constructor for creating a new bookmark.
     *
     * @param viewer The StreamsViewer which is having the bookmark added.
     * @param position Initial position of the new bookmark.
     */
    public BookmarkEditor(StreamsViewer viewer, long position) {
        this(viewer, position, null);
    }

    /**
     * Constructor for editing an existing bookmark.
     *
     * @param viewer The StreamsViewer whose bookmark is to be edited.
     * @param position Position of the bookmark.
     * @param bookmark The bookmark to be edited.
     */
    public BookmarkEditor(StreamsViewer viewer, long position, StreamBookmark bookmark) {
        this.viewer = viewer;
        this.position = position;
        this.bookmark = bookmark;

        if (this.bookmark == null) {
            editing = false;
            this.bookmark = new StreamBookmark("New bookmark", Color.orange);
        }

        dialog = new JDialog();
        if (editing) {
            dialog.setTitle("Edit bookmark");
        } else {
            dialog.setTitle("Add bookmark");
        }
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setModal(true);

        dialog.getContentPane().add(buildPanel());
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Initialise components used here.
     */
    protected void initComponents() {

        // FIXME: the max value of the spinner should be retrieved from the StreamViewer!

        SpinnerModel model = new SpinnerNumberModel(new Long(position), new Long(0), new Long(Long.MAX_VALUE), new Long(1));
        positionField = new JSpinner(model);

        descriptionField = new JTextField(bookmark.getDescription());

        colourButton = new JButton("...");
        colourButton.setBackground(bookmark.getColour());
        colourButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Color newColour = JColorChooser.showDialog(null, "Select colour for this bookmark", colourButton.getBackground());
                if (newColour != null) {
                    colourButton.setBackground(newColour);
                }
            }
        });

        okButton = new JButton("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                updateBookmarkFromForm();
                dialog.dispose();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dialog.dispose();
            }
        });

        applyButton = new JButton("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                updateBookmarkFromForm();
            }
        });
    }

    /**
     * Build the panel/layout components.
     *
     * @return The completed JComponent
     */
    protected JComponent buildPanel() {
        initComponents();

        FormLayout layout = new FormLayout("pref, 4dlu, pref:grow", "pref, pref, pref, 10dlu:grow, pref");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();

        builder.addLabel("Position", cc.xy(1, 1));
        builder.add(positionField, cc.xy(3, 1, "fill, top"));
        builder.addLabel("Description", cc.xy(1, 2));
        builder.add(descriptionField, cc.xy(3, 2, "fill, top"));
        builder.addLabel("Colour", cc.xy(1, 3));
        builder.add(colourButton, cc.xy(3, 3, "left, top"));

        ButtonBarBuilder buttonBuilder = new ButtonBarBuilder();
        buttonBuilder.addGriddedButtons(new JButton[] { okButton, cancelButton, applyButton } );

        builder.add(buttonBuilder.getPanel(), cc.xyw(1, 5, 3, "center, top"));

        return builder.getPanel();
    }

    /**
     * Update the bookmark with the value the user set in the form.
     */
    protected void updateBookmarkFromForm() {
        bookmark.setColour(colourButton.getBackground());
        bookmark.setDescription(descriptionField.getText());

        Long newPosition = (Long) positionField.getValue();
        if (newPosition.longValue() != position) {
            viewer.moveBookmark(position, newPosition.longValue());
        }

        if (editing) {
            viewer.bookmarkModified(newPosition.longValue());
        } else {
            viewer.addBookmark(newPosition.longValue(), bookmark);
        }
    }
}
