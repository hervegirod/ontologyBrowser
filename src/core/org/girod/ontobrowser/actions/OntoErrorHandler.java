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

import org.apache.jena.riot.system.ErrorHandler;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdi.gui.swing.SwingMessageArea;

/**
 * This class show errors encountered curing the Owl/ORDF model parsing.
 *
 * @since 0.4
 */
public class OntoErrorHandler implements ErrorHandler {
   private final GUIApplication appli;
   private final SwingMessageArea messageArea;

   public OntoErrorHandler(GUIApplication appli) {
      this.appli = appli;
      this.messageArea = appli.getMessageArea();
   }

   @Override
   public void warning(String message, long line, long col) {
      messageArea.append("Warning in line " + line + ": " + message, "red");
   }

   @Override
   public void error(String message, long line, long col) {
      messageArea.append("Error in line " + line + ": " + message, "red");
   }

   @Override
   public void fatal(String message, long line, long col) {
      messageArea.append("Fatal error  in line " + line + ": " + message, "red");
   }

}
