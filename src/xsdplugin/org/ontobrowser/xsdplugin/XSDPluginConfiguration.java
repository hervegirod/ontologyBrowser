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

import java.io.File;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.girod.ontobrowser.gui.CustomGraphStyles;
import org.girod.ontobrowser.parsers.CustomGraphStylesParser;
import org.mdi.bootstrap.Configuration;
import org.mdiutil.lang.ResourceLoader;
import org.mdiutil.swing.ExtensionFileFilter;
import org.mdiutil.swing.JErrorPane;

/**
 * This class maintains the configburation of the XSD plugin.
 *
 * @since 0.13
 */
public class XSDPluginConfiguration implements Configuration {
   private static XSDPluginConfiguration conf = null;
   private final transient String version;
   private final transient String date;
   private File xsdExportConfigFile = null;
   private XSDExportConfig xsdExportConfig = new XSDExportConfig();;
   private boolean hasXsdExportConfig = false;
   public transient ExtensionFileFilter xsdfilter;
   private final URL xsdExportConfigXSD;   

   private XSDPluginConfiguration() {
      ResourceLoader loader = new ResourceLoader("org/ontobrowser/xsdplugin/resources");

      // load other ressources
      PropertyResourceBundle prb = loader.getPropertyResourceBundle("xsdplugin.properties");
      xsdExportConfigXSD = loader.getURL("xsdExportConfig.xsd");
      version = prb.getString("version");
      date = prb.getString("date");

      loadFilters();
   }

   /**
    * Unique access to the Configuration. The singleton class is created at the first call of this method.
    *
    * @return the DICOConfiguration
    */
   public static XSDPluginConfiguration getInstance() {
      if (conf == null) {
         conf = new XSDPluginConfiguration();
      }
      return conf;
   }

   private void loadFilters() {
      String[] ext1 = { "xsd" };
      xsdfilter = new ExtensionFileFilter(ext1, "XSD Files");
   }

   public String getVersion() {
      return version;
   }

   public String getDate() {
      return date;
   }
   
   /**
    * Return the export configuration schema.
    *
    * @return the export configuration schema
    */
   public URL getXSDExportConfigurationSchema() {
      return xsdExportConfigXSD;
   }   
   
   /**
    * Return the XSD export configuration.
    *
    * @return the XSD export configuration
    */
   public XSDExportConfig getXSDExportConfiguration() {
      return xsdExportConfig;
   }

   /**
    * Return the XSD export configuration file.
    *
    * @return the XSD export configuration file
    */
   public File getXSDExportConfigurationFile() {
      return xsdExportConfigFile;
   }

   /**
    * Return true if there is a XSD export configuration file.
    *
    * @return true if there is a XSD export configuration file
    */
   public boolean hasXSDExportConfiguration() {
      return hasXsdExportConfig;
   }

   public void setXSDExportConfiguration(File xsdExportConfigFile) {
      if (xsdExportConfigFile != null && xsdExportConfigFile.exists()) {
         XSDExportConfigParser parser = new XSDExportConfigParser();
         try {
            parser.parse(xsdExportConfigFile);
            this.xsdExportConfigFile = xsdExportConfigFile;
            this.hasXsdExportConfig = true;
         } catch (Exception e) {
            this.hasXsdExportConfig = false;
            this.xsdExportConfigFile = null;
            JErrorPane pane = new JErrorPane(e, JOptionPane.ERROR_MESSAGE);
            JDialog dialog = pane.createDialog(null, "Exception");
            dialog.setModal(false);
            dialog.setVisible(true);

         }
      } else {
         this.hasXsdExportConfig = false;
         this.xsdExportConfigFile = null;
      }
   }   
   
   @Override
   public void getConfiguration(Preferences pref, File dir) {      
   }   
   
   @Override
   public void putConfiguration(Preferences pref, File dir) {      
   }   
}
