/*
Copyright (c) 2021, 2022, 2023 Hervé Girod
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
the project website at the project page on https://github.com/hervegirod/owlToGraph
 */
package org.girod.ontobrowser;

import java.io.File;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.girod.ontobrowser.gui.CustomGraphStyles;
import org.girod.ontobrowser.model.PackagesConfiguration;
import org.mdi.bootstrap.Configuration;
import org.mdiutil.lang.swing.ResourceUILoader;
import org.mdiutil.prefs.PreferencesHelper;
import org.mdiutil.swing.ExtensionFileFilter;
import org.mdiutil.swing.JErrorPane;

/**
 * The browser configuration.
 *
 * @version 0.5
 */
public class BrowserConfiguration implements Configuration {
   private static BrowserConfiguration conf = null;
   private File defaultDir = null;
   /**
    * The width of the browser window.
    */
   public int sizeX;
   /**
    * The height of the browser window.
    */
   public int sizeY;
   /**
    * The version.
    */
   public final String version;
   /**
    * The date.
    */
   public final String date;
   public boolean includeIndividuals = false;
   public boolean showRelationsConstraints = false;
   public boolean showDataPropertiesTypes = false;
   public boolean addThingClass = true;
   public boolean showIndirectRelations = false;
   // padding and size
   public int padWidth = 15;
   public int padHeight = 10;
   // packages
   public boolean showPackages = false;
   public boolean showPackagesAsClosed = false;
   public boolean showPackagesInPackageView = false;
   private boolean hasPackagesConfiguration = false;
   private File packagesConfigurationFile = null;
   public final PackagesConfiguration packagesConfiguration = new PackagesConfiguration();
   // GUI
   public boolean autoRefresh = false;
   private boolean hasCustomStyles = false;
   private File customGraphStylesFile = null;
   public final CustomGraphStyles customGraphStyles = new CustomGraphStyles();
   // graph styles Schema
   private final URL graphStylesXSD;
   // packages configuration Schema
   private final URL packagesConfigurationXSD;

   /**
    * The owl/rdf file filter.
    */
   public ExtensionFileFilter owlfilter;
   /**
    * The graphml file filter.
    */
   public ExtensionFileFilter graphmlfilter;

   private BrowserConfiguration() {
      // load ressources
      ResourceUILoader loader = new ResourceUILoader("org/girod/ontobrowser/resources");
      graphStylesXSD = loader.getURL("customGraphStyles.xsd");
      packagesConfigurationXSD = loader.getURL("packagesConfiguration.xsd");

      PropertyResourceBundle prb = loader.getPropertyResourceBundle("browser.properties");

      // load size
      sizeX = Integer.parseInt(prb.getString("sizeX"));
      sizeY = Integer.parseInt(prb.getString("sizeY"));
      version = prb.getString("version");
      date = prb.getString("date");

      defaultDir = new File(System.getProperty("user.dir"));
      String[] ext1 = { "owl", "rdf", "ttl" };
      owlfilter = new ExtensionFileFilter(ext1, "OWL/RDF Files");

      String[] ext2 = { "graphml" };
      graphmlfilter = new ExtensionFileFilter(ext2, "graphml Files");
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static BrowserConfiguration getInstance() {
      if (conf == null) {
         conf = new BrowserConfiguration();
      }
      return conf;
   }

   /**
    * Set the default directory.
    *
    * @param dir the default directory
    */
   public void setDefaultDirectory(File dir) {
      defaultDir = dir;
   }

   /**
    * Return the default directory.
    *
    * @return the default directory
    */
   public File getDefaultDirectory() {
      return defaultDir;
   }

   /**
    * Return the graph styles Schema.
    *
    * @return the graph styles Schema
    */
   public URL getGraphStylesSchema() {
      return graphStylesXSD;
   }

   /**
    * Return the custom graph styles.
    *
    * @return the custom graph styles
    */
   public CustomGraphStyles getCustomGraphStyles() {
      return customGraphStyles;
   }

   /**
    * Return the custom graph styles file.
    *
    * @return the custom graph styles file
    */
   public File getCustomGraphStylesFile() {
      return customGraphStylesFile;
   }

   /**
    * Return true if there are custom graph styles.
    *
    * @return true if there are custom graph styles
    */
   public boolean hasCustomStyles() {
      return hasCustomStyles;
   }

   public void setHasCustomStyles(boolean hasCustomStyles) {
      this.hasCustomStyles = hasCustomStyles;
      if (hasCustomStyles && customGraphStylesFile != null && customGraphStylesFile.exists()) {
         setCustomStylesConfiguration(customGraphStylesFile);
      }
   }

   public void setCustomStylesConfiguration(File customGraphStylesFile) {
      if (hasCustomStyles) {
         this.customGraphStylesFile = customGraphStylesFile;
         CustomGraphStylesParser parser = new CustomGraphStylesParser();
         try {
            parser.parse(customGraphStylesFile);
         } catch (Exception e) {
            JErrorPane pane = new JErrorPane(e, JOptionPane.ERROR_MESSAGE);
            JDialog dialog = pane.createDialog(null, "Exception");
            dialog.setModal(false);
            dialog.setVisible(true);
         }
      }
   }

   /**
    * Return the Schema for the packages configuration.
    *
    * @return the Schema for the packages configuration
    */
   public URL getPackagesConfigurationSchema() {
      return packagesConfigurationXSD;
   }

   /**
    * Return the packages configuration.
    *
    * @return the packages configuration
    */
   public PackagesConfiguration getPackagesConfiguration() {
      return packagesConfiguration;
   }

   /**
    * Return true if there is a packagesConfiguration.
    *
    * @return true if there is a packagesConfiguration
    */
   public boolean hasPackagesConfiguration() {
      return hasPackagesConfiguration;
   }

   /**
    * Return the packages configuration file.
    *
    * @return the packages configuration file
    */
   public File getPackagesToForgetFile() {
      return packagesConfigurationFile;
   }

   public void setHasPackagesConfiguration(boolean hasPackagesConfiguration) {
      this.hasPackagesConfiguration = hasPackagesConfiguration;
      if (hasPackagesConfiguration && packagesConfigurationFile != null && packagesConfigurationFile.exists()) {
         setPackagesConfiguration(packagesConfigurationFile);
      }
   }

   public void setPackagesConfiguration(File packagesConfiguration) {
      if (hasPackagesConfiguration) {
         this.packagesConfigurationFile = packagesConfiguration;
         PackagesConfigurationParser parser = new PackagesConfigurationParser();
         try {
            parser.parse(packagesConfiguration);
         } catch (Exception e) {
            JErrorPane pane = new JErrorPane(e, JOptionPane.ERROR_MESSAGE);
            JDialog dialog = pane.createDialog(null, "Exception");
            dialog.setModal(false);
            dialog.setVisible(true);
         }
      }
   }

   @Override
   public void putConfiguration(Preferences p, File dir) {
      // general
      PreferencesHelper.putFile(p, "defaultDir", defaultDir);
      p.putBoolean("includeIndividuals", includeIndividuals);
      p.putBoolean("showRelationsConstraints", showRelationsConstraints);
      p.putBoolean("showDataPropertiesTypes", showDataPropertiesTypes);
      p.putBoolean("addThingClass", addThingClass);
      p.putBoolean("showIndirectRelations", showIndirectRelations);

      // styles
      p.putInt("padWidth", padWidth);
      p.putInt("padHeight", padHeight);
      p.putBoolean("autoRefresh", autoRefresh);
      p.putBoolean("hasCustomStyles", hasCustomStyles);
      PreferencesHelper.putFileRelativeTo(p, "customGraphStyles", customGraphStylesFile, dir);

      // packages
      p.putBoolean("showPackages", showPackages);
      p.putBoolean("showPackagesAsClosed", showPackagesAsClosed);
      p.putBoolean("showPackagesInPackageView", showPackagesInPackageView);
      p.putBoolean("hasPackagesConfiguration", hasPackagesConfiguration);
      PreferencesHelper.putFileRelativeTo(p, "packagesConfiguration", packagesConfigurationFile, dir);
   }

   @Override
   public void getConfiguration(Preferences p, File dir) {
      // general
      defaultDir = PreferencesHelper.getFile(p, "defaultDir", defaultDir);
      includeIndividuals = p.getBoolean("includeIndividuals", includeIndividuals);
      showRelationsConstraints = p.getBoolean("showRelationsConstraints", showRelationsConstraints);
      showDataPropertiesTypes = p.getBoolean("showDataPropertiesTypes", showDataPropertiesTypes);
      addThingClass = p.getBoolean("addThingClass", addThingClass);
      showIndirectRelations = p.getBoolean("showIndirectRelations", showIndirectRelations);

      // styles
      padWidth = p.getInt("padWidth", padWidth);
      padHeight = p.getInt("padHeight", padHeight);
      customGraphStylesFile = PreferencesHelper.getFileRelativeTo(p, "customGraphStyles", customGraphStylesFile, dir);
      hasCustomStyles = p.getBoolean("hasCustomStyles", hasCustomStyles);
      autoRefresh = p.getBoolean("autoRefresh", autoRefresh);
      if (customGraphStylesFile != null && customGraphStylesFile.exists()) {
         setCustomStylesConfiguration(customGraphStylesFile);
      } else {
         customGraphStyles.reset();
      }

      // packages
      showPackages = p.getBoolean("showPackages", showPackages);
      showPackagesAsClosed = p.getBoolean("showPackagesAsClosed", showPackagesAsClosed);
      showPackagesInPackageView = p.getBoolean("showPackagesInPackageView", showPackagesInPackageView);
      hasPackagesConfiguration = p.getBoolean("hasPackagesConfiguration", hasPackagesConfiguration);
      packagesConfigurationFile = PreferencesHelper.getFileRelativeTo(p, "packagesConfiguration", packagesConfigurationFile, dir);
      if (packagesConfigurationFile != null && packagesConfigurationFile.exists()) {
         setPackagesConfiguration(packagesConfigurationFile);
      } else {
         packagesConfiguration.reset();
      }
   }

}
