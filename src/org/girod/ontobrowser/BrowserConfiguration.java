/*
Copyright (c) 2021, 2022 Hervé Girod
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
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;
import org.mdi.bootstrap.Configuration;
import org.mdiutil.lang.swing.ResourceUILoader;
import org.mdiutil.prefs.PreferencesHelper;
import org.mdiutil.swing.ExtensionFileFilter;

/**
 * The browser configuration.
 *
 * @version 0.2
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
   // padding and size
   public int padWidth = 15;
   public int padHeight = 10;
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
      PropertyResourceBundle prb = loader.getPropertyResourceBundle("browser.properties");

      // load size
      sizeX = Integer.parseInt(prb.getString("sizeX"));
      sizeY = Integer.parseInt(prb.getString("sizeY"));
      version = prb.getString("version");
      date = prb.getString("date");

      defaultDir = new File(System.getProperty("user.dir"));
      String[] ext1 = { "owl", "rdf" };
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

   @Override
   public void putConfiguration(Preferences p, File file) {
      PreferencesHelper.putFile(p, "defaultDir", defaultDir);
      p.putInt("padWidth", padWidth);
      p.putInt("padHeight", padHeight);
      p.putBoolean("includeIndividuals", includeIndividuals);
      p.putBoolean("showRelationsConstraints", showRelationsConstraints);
      p.putBoolean("showDataPropertiesTypes", showDataPropertiesTypes);
   }

   @Override
   public void getConfiguration(Preferences p, File file) {
      defaultDir = PreferencesHelper.getFile(p, "defaultDir", defaultDir);
      padWidth = p.getInt("padWidth", padWidth);
      padHeight = p.getInt("padHeight", padHeight);
      includeIndividuals = p.getBoolean("includeIndividuals", includeIndividuals);
      showRelationsConstraints = p.getBoolean("showRelationsConstraints", showRelationsConstraints);
      showDataPropertiesTypes = p.getBoolean("showDataPropertiesTypes", showDataPropertiesTypes);
   }

}
