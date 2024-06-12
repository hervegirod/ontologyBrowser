/*
Copyright (c) 2023, 2024 Hervé Girod
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
package org.girod.ontobrowser.actions.script;

import java.io.File;
import java.util.Set;
import javax.swing.JFrame;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OntoBrowserGUI;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.script.DefaultScriptImports;
import org.girod.ontobrowser.script.OwlScriptContext;
import org.girod.ontobrowser.script.Script;
import org.mdi.bootstrap.MDIApplication;
import org.scripthelper.debug.DebugSession;
import org.scripthelper.debug.SessionHook;
import org.scripthelper.groovy.GroovyScriptWrapper;
import org.scripthelper.model.ScriptWrapper;
import org.scripthelper.swing.SwingExceptionListener;
import org.scripthelper.swing.debug.SwingDebugScriptWindow;

/**
 * The apply debug Script Action.
 *
 * @version 0.13
 */
public class ApplyDebugScriptAction extends AbstractScriptAction {
   private SwingDebugScriptWindow debugWindow = null;

   /**
    * Create a Script Action.
    *
    * @param appli the Application
    * @param scriptFile the Script File
    * @param graphPanel the graph panel
    */
   public ApplyDebugScriptAction(MDIApplication appli, File scriptFile, GraphPanel graphPanel) {
      super(appli, scriptFile, graphPanel);
   }

   /**
    * Run the Script.
    */
   @Override
   public void run() throws Exception {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      endAtFirstException = conf.endAtFirstException;

      debugWindow = new SwingDebugScriptWindow(20, 20);
      context = new OwlScriptContext((OntoBrowserGUI) app, diagram);
      debugWindow = new SwingDebugScriptWindow(20, 20);
      context.setLogger(debugWindow);
      debugWindow.registerHyperLinkListener(this);
      debugWindow.registerLinkIndexConverter(this);
      GroovyScriptWrapper<Script> wrapper = new GroovyScriptWrapper<Script>() {
      };
      wrapper.setDebugListener(debugWindow);
      wrapper.setScriptContext(context);
      Set<String> decl = DefaultScriptImports.getImports();
      wrapper.addImports(decl);

      wrapper.initializeDebugSession();
      wrapper.installScript(scriptFile);
      exListener = new SwingExceptionListener();
      wrapper.addExceptionListener(exListener);
      this.setDescription("Apply Script " + scriptName, "Apply Script " + scriptName);
      DebugSession<Script> session = wrapper.createDebugSession();
      script = session.getScript();
      debugWindow.setVisible(true);
      debugWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      session.setExecutionMode(ScriptWrapper.MODE_NON_BLOCKING);
      session.setSessionHook(new SessionHook<Script>() {
         public Object start(Script script) {
            startSession();
            return null;
         }
      });
      debugWindow.setVisible(true);
      session.startSession();
      context.end();
   }

   private void startSession() {
      startScript();
      if (!isScriptAborted()) {
         schema.accept(this);
         endScript();
         debugWindow.finishedScript();
         context.end();
      } else {
         context.end();
      }
   }
}
