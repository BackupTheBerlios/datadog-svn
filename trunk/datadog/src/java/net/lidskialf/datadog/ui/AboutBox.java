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

import javax.swing.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Andrew de Quincey
 *  
 */
public class AboutBox {

    /**
     * Constructor - shows the About Box.
     */
    public AboutBox() {
        dialog = new JDialog();
        dialog.setTitle("About DataDog");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setModal(true);

        dialog.getContentPane().add(buildPanel());
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Initialise components used here.
     */
    private void initComponents() {

        logo = new JLabel("logo");

        aboutText = new JTextArea();
        aboutText.setText("DataDog v" + DataDog.version + "\n" + "(c) 2005 Andrew de Quincey. All rights reserved.\n"
                + "Licensed under the GNU Public License (GPL).\n" + "See http://www.gnu.org for details.");
        aboutText.setEditable(false);

        okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    /**
     * Build the panel/layout components.
     * 
     * @return The completed JComponent
     */
    private JComponent buildPanel() {
        initComponents();

        FormLayout layout = new FormLayout("pref, 4dlu, pref:grow, pref", "pref:grow, 4dlu, pref");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints cc = new CellConstraints();

        builder.add(logo, cc.xy(1, 1, "left, top"));
        builder.add(aboutText, cc.xyw(3, 1, 2, "fill, fill"));
        builder.add(okButton, cc.xy(4, 3, "right, bottom"));

        return builder.getPanel();
    }

    private JDialog dialog;

    private JLabel logo;

    private JTextArea aboutText;

    private JButton okButton;
}
