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
package org.girod.ontobrowser.script;

import org.girod.ontobrowser.OntoBrowserGUI;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedElement;
import org.girod.ontobrowser.model.OwlSchema;
import org.scripthelper.context.DefaultScriptContext;
import org.scripthelper.context.ScriptHelper;

/**
 * A Script Context passed to browser Scripts.
 *
 * @version 0.13
 */
public class OwlScriptContext extends DefaultScriptContext {
   private OntoBrowserGUI app = null;
   private OwlDiagram diagram = null;
   private OwlSchema schema = null;
   private NamedElement currentElt = null;
   private OwlScriptHelper scriptHelper = null;

   /**
    * Create a new ScriptContext.
    * @param app the application
    * @param diagram the diagram
    */
   public OwlScriptContext(OntoBrowserGUI app, OwlDiagram diagram) {
      super();
      this.app = app;
      this.diagram = diagram;
      this.schema = diagram.getSchema();
      this.scriptHelper = new OwlScriptHelper(this);
   }   

   /**
    * Return the script helper.
    *
    * @return the script helper
    */
   @Override
   public ScriptHelper getHelper() {
      return scriptHelper;
   }

   public void echo(ElementKey key) {
      echo(key.toString());
   }

   /**
    * Called automatically by the Scripting framework at the end of the Script.
    */
   public void end() {
   }

   /**
    * Return the application.
    *
    * @return the application
    */
   public OntoBrowserGUI getApplication() {
      return app;
   }

   /**
    * Set the current element used by the Script.
    *
    * @param currentElt the element
    */
   public void setCurrentElement(NamedElement currentElt) {
      this.currentElt = currentElt;
   }
   
   /**
    * Return the current element used by the Script.
    *
    * @return the element
    */   
   public NamedElement gettCurrentElement() {
      return currentElt;
   }
   
   /**
    * Return the schema.
    *
    * @return the schema
    */
   public OwlSchema getSchema() {
      return schema;
   }   

   /**
    * Return the diagram.
    *
    * @return the diagram
    */
   public OwlDiagram getDiagram() {
      return diagram;
   }
}
