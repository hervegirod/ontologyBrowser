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
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.girod.ontobrowser.BrowserConfiguration;
import org.mdiutil.swing.JFileSelector;
import org.mdiutil.swing.PropertyEditor;

/**
 * This class encapsulate the Property edition for the XSDPlugin.
 *
 * @since 0.13
 */
public class XSDSettings extends PropertyEditor {
   private XSDPluginConfiguration xsdconf;
   private static XSDSettings settings = null;
   private JFileSelector fc;

   private XSDSettings() {
      super();
      initialize();
   }

   public static XSDSettings getXSDSettings() {
      if (settings == null) {
         settings = new XSDSettings();
      }

      return settings;
   }

   public void resetSettings() {
      XSDPluginConfiguration conf = XSDPluginConfiguration.getInstance();
      fc.setSelectedFile(conf.getXSDExportConfigurationFile());
   }

   private void initialize() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      XSDPluginConfiguration xsdconf = XSDPluginConfiguration.getInstance();
      File dir = conf.getDefaultDirectory();

      fc = new JFileSelector("XSD Export Configuration");
      fc.setCurrentDirectory(dir);
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.setFileFilter(conf.xmlfilter);
      fc.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File file = ((JFileChooser) e.getSource()).getSelectedFile();
            xsdconf.setXSDExportConfiguration(file);
         }
      });
      this.addProperty(fc, "", "XSD Export Configuration");
      this.setVisible(true);
   }
}
