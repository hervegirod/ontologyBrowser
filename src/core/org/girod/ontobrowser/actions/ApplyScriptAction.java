/*
Copyright (c) 2023 Hervé Girod
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
package org.girod.ontobrowser.actions;

import java.io.File;
import java.util.Set;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OntoBrowserGUI;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.script.DefaultScriptImports;
import org.girod.ontobrowser.script.OwlScriptContext;
import org.girod.ontobrowser.script.Script;
import org.mdi.bootstrap.MDIApplication;
import org.scripthelper.groovy.GroovyScriptWrapper;
import org.scripthelper.swing.DefaultSwingScriptLogger;
import org.scripthelper.swing.SwingExceptionListener;

/**
 * The apply Script Action.
 *
 * @version 0.13
 */
public class ApplyScriptAction extends AbstractScriptAction {

   /**
    * Create a Script Action.
    *
    * @param appli the Application
    * @param scriptFile the Script File
    * @param graphPanel the graph panel
    */
   public ApplyScriptAction(MDIApplication appli, File scriptFile, GraphPanel graphPanel) {
      super(appli, scriptFile, graphPanel);
   }

   /**
    * Run the Script.
    */
   @Override
   public void run() throws Exception {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      endAtFirstException = conf.endAtFirstException;

      context = new OwlScriptContext((OntoBrowserGUI) app, diagram);
      DefaultSwingScriptLogger logger = new DefaultSwingScriptLogger();
      logger.registerHyperLinkListener(this);
      logger.registerLinkIndexConverter(this);
      context.setLogger(logger);
      GroovyScriptWrapper<Script> wrapper = new GroovyScriptWrapper<Script>() {
      };
      wrapper.setScriptContext(context);
      Set<String> decl = DefaultScriptImports.getImports();
      wrapper.addImports(decl);
      script = wrapper.getScript();

      // we should protect against a wrong script definition
      try {
         wrapper.addImports(decl);
         wrapper.setScriptLogger(logger);
         exListener = new SwingExceptionListener();
         wrapper.addExceptionListener(exListener);
         wrapper.installScript(scriptFile);
         scriptName = wrapper.getFilename();
         this.setDescription("Apply Script " + scriptName, "Apply Script " + scriptName);
         logger.setVisible(true);
      } catch (Exception e) {
         logger.appendError("Aborted: " + e.getMessage());
      }

      if (script != null && !isScriptAborted()) {
         startScript();
         if (!isScriptAborted()) {
            if (script.visitSchema()) {
               schema.accept(this);
            }
            endScript();
            logger.finishedScript();
            context.end();
         } else {
            context.end();
         }
      }
   }
}
