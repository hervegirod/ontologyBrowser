/*
 * Dassault Aviation confidential
 * Copyright (c) 2017, 2015 Dassault Aviation. All rights reserved.
 *
 * This file is part of the FICOLab project.
 *
 * NOTICE: All information contained herein is, and remains the property of Dassault Aviation.
 * The intellectual and technical concepts contained herein are proprietary to Dassault Aviation,
 * and are protected by trade secret or copyright law.

 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * It can not be copied and/or distributed, in source or binary form, without the express permission of
 * Dassault Aviation.
 */
package org.girod.ontobrowser.script;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.girod.ontobrowser.BrowserConfiguration;
import org.mdiutil.swing.GenericDialog;
import org.mdiutil.swing.JFileSelector;

/**
 * A Dialog allowing to enter an XML File.
 *
 * @since 0.13
 */
public class ScriptXMLFileDialog extends GenericDialog {
   private String desc = null;
   private JFileSelector fs = null;
   /**
    * The extension for the Script File dialog.
    */
   public static final String[] EXTENSIONS = { "xml" };
   private OwlScriptContext context = null;

   /**
    * Constructor.
    *
    * @param parent the Component parent
    * @param propDesc property description
    * @param context the ScriptContext
    */
   public ScriptXMLFileDialog(Component parent, String propDesc, OwlScriptContext context) {
      this.desc = propDesc;
      this.setDialogTitle(desc);
      this.context = context;
      this.createDialog(parent, true);
   }
   
   public boolean hasSelectedFile() {
      File file = fs.getSelectedFile();
      return  file != null;
   }

   /**
    * Return the selected File.
    *
    * @return the selected File
    */
   public File getSelectedFile() {
      File file = fs.getSelectedFile();
      return file;
   }

   @Override
   protected void createPanel() {
      Container pane = dialog.getContentPane();
      pane.setLayout(new BorderLayout());

      // file panel
      JPanel filePanel = new JPanel();
      filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
      filePanel.add(new JLabel("File"));
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      fs = new JFileSelector();
      fs.setCurrentDirectory(context.getScriptFile().getParentFile());
      fs.setDialogType(JFileChooser.OPEN_DIALOG);
      fs.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fs.addChoosableFileFilter(conf.xmlfilter);

      fs.setCurrentDirectory(conf.getDefaultDirectory());
      filePanel.add(Box.createHorizontalStrut(5));
      filePanel.add(fs);

      JPanel yesnopanel = createYesNoPanel();
      pane.add(filePanel, BorderLayout.CENTER);
      pane.add(yesnopanel, BorderLayout.SOUTH);

      dialog.pack();
   }

}
