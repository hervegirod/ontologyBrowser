/*
Copyright (c) 2024, 2025 Hervé Girod
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.OwlRepresentationType;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdi.bootstrap.swing.GUIApplication;

/**
 * The Action that save owl/rdf schemas.
 *
 * @version 0.17
 */
public class SaveModelAction extends AbstractMDIAction {
   private final OwlDiagram diagram;
   private final File file;
   private final short representationType;

   /**
    * Constructor.
    *
    * @param app the Application
    * @param diagram the diagram
    * @param file the file to save
    * @param representationType the representation type
    */
   public SaveModelAction(MDIApplication app, OwlDiagram diagram, File file, short representationType) {
      super(app, "Save");
      this.diagram = diagram;
      this.file = file;
      this.representationType = representationType;
   }

   @Override
   public void run() throws Exception {
      try {
         OwlSchema schema = diagram.getSchema();
         OntModel model = schema.getOntModel();
         FileOutputStream outputFile = new FileOutputStream(file);
         if (representationType == OwlRepresentationType.TYPE_OWL_XML) {
            RDFDataMgr.write(outputFile, model, Lang.RDFXML);
         } else if (representationType == OwlRepresentationType.TYPE_OWL_TURTLE) {
            RDFDataMgr.write(outputFile, model, Lang.TTL);
         }
      } catch (FileNotFoundException ex) {
      }
   }

   @Override
   public void endAction() {
      ((GUIApplication) app).getMessageArea().append("Model saved as " + file.getName());
   }
}
