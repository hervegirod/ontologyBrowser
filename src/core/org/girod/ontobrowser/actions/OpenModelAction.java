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
package org.girod.ontobrowser.actions;

import java.io.File;
import java.net.MalformedURLException;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.SwingFileProperties;

/**
 * The Action that opens owl/rdf schemas.
 *
 * @version 0.13
 */
public class OpenModelAction extends AbstractOpenModelAction {

   /**
    * Constructor.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param file the file to open
    */
   public OpenModelAction(MDIApplication app, String desc, String longDesc, File file) {
      super(app, desc, longDesc, file);
   }

   /**
    * Constructor.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param prop the file properties
    * @param file the file to open
    */
   public OpenModelAction(MDIApplication app, String desc, String longDesc, SwingFileProperties prop, File file) {
      super(app, desc, longDesc, prop, file);
   }

   @Override
   public void endAction() {
      if (diagram == null) {
         return;
      }
      if (prop == null) {
         AbstractMDIApplication mdi = (AbstractMDIApplication) app;
         if (mdi.hasTab(name)) {
            int i = 1;
            String postName = null;
            while (true) {
               postName = name + "_" + i;
               if (!mdi.hasTab(postName)) {
                  break;
               }
            }
            name = postName;
         }

         prop = new SwingFileProperties(name, graphPanel, diagram);
         try {
            prop.setURL(file.toURI().toURL());
         } catch (MalformedURLException ex) {
         }
         ((AbstractMDIApplication) app).addTab(graphPanel, prop);
      }
   }
}
