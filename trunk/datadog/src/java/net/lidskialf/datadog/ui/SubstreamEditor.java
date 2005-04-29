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

/**
 * A generic UI for editing substreams.
 *
 * @author Andrew de Quincey
 */
public class SubstreamEditor {

    private boolean editing = true;
    private StreamsViewer viewer;
    private int index;
    private Substream substream;

    private JDialog dialog;
    private JTextField labelField;
    private JTextField descriptionField;
    private JButton colourButton;
    private JButton okButton;
    private JButton cancelButton;
    private JButton applyButton;


    /**
     * Constructor for creating a new substream.
     *
     * @param viewer The StreamsViewer which is having the bookmark added.
     * @param index Index of the substream.
     */
    public SubstreamEditor(StreamsViewer viewer, int index) {
        this(viewer, index, null);
    }

    /**
     * Constructor for editing an existing substream.
     *
     * @param viewer The StreamsViewer whose substream is to be edited.
     * @param index Index of the substream.
     * @param substream The substream to be edited.
     */
    public SubstreamEditor(StreamsViewer viewer, int index, Substream substream) {
        this.viewer = viewer;
        this.index = index;
        this.substream = substream;

        if (this.substream == null) {
            editing = false;
            this.substream = new Substream("New substream", Color.orange, "New substream", true);
        }

        dialog = new JDialog();
        if (editing) {
            dialog.setTitle("Edit substream");
        } else {
            dialog.setTitle("Add substream");
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

        labelField = new JTextField(substream.getLabel());

        descriptionField = new JTextField(substream.getDescription());

        colourButton = new JButton("...");
        colourButton.setBackground(substream.getColour());
        colourButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Color newColour = JColorChooser.showDialog(null, "Select colour for this substream", colourButton.getBackground());
                if (newColour != null) {
                    colourButton.setBackground(newColour);
                }
            }
        });

        okButton = new JButton("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                updateSubstreamFromForm();
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
                updateSubstreamFromForm();
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

        builder.addLabel("Label", cc.xy(1, 1));
        builder.add(labelField, cc.xy(3, 1, "fill, top"));
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
     * Update the substream with the values the user set in the form.
     */
    protected void updateSubstreamFromForm() {
        substream.setLabel(labelField.getText());
        substream.setColour(colourButton.getBackground());
        substream.setDescription(descriptionField.getText());

        if (editing) {
            viewer.substreamModified(index);
        } else {
            viewer.addSubstream(index, substream);
        }
    }
}
