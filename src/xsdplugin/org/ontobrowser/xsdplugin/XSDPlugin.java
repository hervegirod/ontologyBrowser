/*
Copyright (c) 2024 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/ontologyBrowser
 */
package org.ontobrowser.xsdplugin;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdi.plugins.AbstractMDIPlugin;
import org.mdi.plugins.PluginElementTypes;
import org.mdiutil.io.FileUtilities;

/**
 *
 * @since 0.13
 */
public class XSDPlugin extends AbstractMDIPlugin {
   public static String PLUGIN_NAME = "XSDPlugin";
   private AbstractAction exportXSDAction;
   private XSDSettings settings;

   public XSDPlugin() {
   }

   @Override
   public String getPluginName() {
      return PLUGIN_NAME;
   }
   
   @Override
   public void resetSettings() {
      settings.resetSettings();
   }

   @Override
   public void register(MDIApplication appli) {
      this.appli = appli;
      settings = XSDSettings.getXSDSettings();
      exportXSDAction = new AbstractAction("Export as XSD") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            exportAsXSD();
         }
      };
   }

   private void exportAsXSD() {
      if (((AbstractMDIApplication) appli).hasSelectedTab()) {
         OwlDiagram diagram = (OwlDiagram) ((AbstractMDIApplication) appli).getSelectedProperties().getObject();
         BrowserConfiguration conf = BrowserConfiguration.getInstance();
         XSDPluginConfiguration xsdconf = XSDPluginConfiguration.getInstance();
         JFileChooser chooser = new JFileChooser("Export as XSD");
         chooser.setCurrentDirectory(conf.getDefaultDirectory());
         chooser.addChoosableFileFilter(xsdconf.xsdfilter);
         chooser.setDialogType(JFileChooser.SAVE_DIALOG);
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         if (chooser.showSaveDialog(((GUIApplication) appli).getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            file = FileUtilities.getCompatibleFile(file, "xsd");
            ExportXSDAction action = new ExportXSDAction(appli, diagram.getSchema(), file);
            appli.executeAction(action);
         }
      }
   }

   /**
    * Return the static menus for this plugin.
    */
   @Override
   public Object getStaticMenuElements(String menu) {
      switch (menu) {
         case "Tools":
            return exportXSDAction;
         case PluginElementTypes.SETTINGS:
            return settings;
         default:
            return null;
      }
   }
    
}
