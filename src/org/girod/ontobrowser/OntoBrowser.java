/*
Copyright (c) 2021, 2023 Hervé Girod
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
package org.girod.ontobrowser;

import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mdi.app.LauncherConf;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdiutil.prefs.NetworkPreferencesFactory;

/**
 * The main class of the application.
 *
 * @version 0.4
 */
public class OntoBrowser extends AbstractMDIApplication {
   private Preferences pref = null;

   public OntoBrowser() {
      this(true);
   }

   public OntoBrowser(boolean startGUI) {
      super("Ontology Browser");
      configureDerby();
      configureLog4J();
      this.hasClosableTab(true);
      conf = BrowserConfiguration.getInstance();

      if (startGUI) {
         initPreferencesFile();
         super.initConfiguration(pref);
         this.setSize(((BrowserConfiguration) conf).sizeX, ((BrowserConfiguration) conf).sizeY);

         mfactory = new MenuFactory(this);
         super.preparePanels(8, true, true, mfactory);
         this.message.manageClipBoard(true);
      }
   }

   private void configureDerby() {
      // this is to make sure that Apache Derby do not create a derby.log file.
      // see http://davidvancouvering.blogspot.com/2007/10/quiet-time-and-how-to-suppress-derbylog.html
      // or https://stackoverflow.com/questions/1004327/getting-rid-of-derby-log
      System.setProperty("derby.stream.error.field", "org.girod.ontobrowser.DerbyUtil.DEV_NULL");
   }

   private void configureLog4J() {
      BasicConfigurator.configure();

      List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
      loggers.add(LogManager.getRootLogger());
      for (Logger logger : loggers) {
         logger.setLevel(Level.OFF);
      }
   }

   /*
    * Main entry point of the Application.
    */
   public static void main(String[] args) {
      OntoBrowser browser = new OntoBrowser();
      browser.setVisible(true);
   }

   private void initPreferencesFile() {
      NetworkPreferencesFactory prefFactory;
      try {
         prefFactory = getPreferencesFactory();
         pref = prefFactory.userRoot();
      } catch (BackingStoreException e) {
         e.printStackTrace();
      }
   }

   private NetworkPreferencesFactory getPreferencesFactory() throws BackingStoreException {
      LauncherConf lconf = LauncherConf.getInstance();
      NetworkPreferencesFactory prefFactory = NetworkPreferencesFactory.getFactory();
      if (prefFactory == null) {
         prefFactory = NetworkPreferencesFactory.newFactory(lconf.getUserHome(), null, null, "ontoBrowser");
      }
      return prefFactory;
   }
}
