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
import java.io.IOException;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.GUIApplication;

/**
 * The Action that save schemas as yEd diagrams an open them in yEd.
 *
 * @since 0.5
 */
public class OpenInYedAction extends ExportGraphAction {

   /**
    * Create the export File Action.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param diagram the diagram
    * @param file the file to open
    */
   public OpenInYedAction(MDIApplication app, String desc, String longDesc, OwlDiagram diagram, File file) {
      super(app, desc, longDesc, diagram, file);
   }

   @Override
   public void run() throws Exception {
      super.run();
      File yEdDirectory = BrowserConfiguration.getInstance().getYedExeDirectory();
      File yEdFile = new File(yEdDirectory, "yEd.exe");
      String[] cmdArray = {yEdFile.getAbsolutePath(), file.getCanonicalPath()};

      try {
         ProcessBuilder pb = new ProcessBuilder(cmdArray);
         pb.directory(yEdDirectory);
         pb.start();
      } catch (IOException e) {
         ((GUIApplication) app).getMessageArea().append("Impossible to launch yed exexutable", "red");
      }
   }
}
