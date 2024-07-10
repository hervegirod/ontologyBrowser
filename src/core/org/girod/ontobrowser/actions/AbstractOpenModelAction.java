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

import org.girod.ontobrowser.model.OwlRepresentationType;
import com.mxgraph.view.mxGraph;
import java.io.File;
import java.net.URI;
import javax.swing.JOptionPane;
import javax.xml.namespace.QName;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntologyException;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceRequiredException;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.util.FileManager;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.parsers.graph.GraphExtractor;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.gui.errors.SwingErrorLogger;
import org.girod.ontobrowser.model.OntModelSpecTypes;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdi.bootstrap.swing.SwingFileProperties;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.xml.XMLRootDetector;

/**
 * The Action that opens owl/rdf schemas.
 *
 * @version 0.13
 */
public abstract class AbstractOpenModelAction extends AbstractUpdateModelAction {
   protected File file = null;
   protected String name = null;
   private short owlRepresentationType = OwlRepresentationType.TYPE_UNDEFINED;

   /**
    * Constructor.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param file the file to open
    */
   public AbstractOpenModelAction(MDIApplication app, String desc, String longDesc, File file) {
      super(app, desc, longDesc);
      this.file = file;
      this.name = FileUtilities.getFileNameBody(file);
   }

   /**
    * Constructor.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param prop the file properties
    */
   public AbstractOpenModelAction(MDIApplication app, String desc, String longDesc, SwingFileProperties prop) {
      super(app, desc, longDesc, prop);
      this.file = prop.getFile();
      this.name = FileUtilities.getFileNameBody(file);
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
   public AbstractOpenModelAction(MDIApplication app, String desc, String longDesc, SwingFileProperties prop, File file) {
      super(app, desc, longDesc);
      this.file = file;
      this.prop = prop;
      this.name = FileUtilities.getFileNameBody(file);
   }

   /**
    * Return the Owl representation type.
    *
    * @param file the file
    * @return the Owl representation type
    */
   protected short getOwlRepresentationType(File file) {
      String extension = FileUtilities.getFileExtension(file);
      if (extension == null) {
         return OwlRepresentationType.TYPE_UNDEFINED;
      } else {
         switch (extension.toLowerCase()) {
            case "owl":
            case "rdf": {
               XMLRootDetector detector = new XMLRootDetector();
               QName rootName = detector.getQualifiedRootName(file);
               if (rootName.getLocalPart().equals("Ontology")) {
                  return OwlRepresentationType.TYPE_OWL2_UNSUPPORTED;
               } else if (rootName.getLocalPart().equals("RDF") && rootName.getPrefix().equals("rdf")) {
                  return OwlRepresentationType.TYPE_OWL_XML;
               } else {
                  return OwlRepresentationType.TYPE_UNSUPPORTED;
               }
            }
            case "ttl":
               return OwlRepresentationType.TYPE_OWL_TURTLE;
            default:
               return OwlRepresentationType.TYPE_UNDEFINED;
         }
      }
   }

   @Override
   public void run() throws Exception {
      owlRepresentationType = getOwlRepresentationType(file);
      switch (owlRepresentationType) {
         case OwlRepresentationType.TYPE_UNDEFINED:
            JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), "Undefined Owl representation", "Could not parse model", JOptionPane.ERROR_MESSAGE);
            return;
         case OwlRepresentationType.TYPE_OWL2_UNSUPPORTED:
            JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), "Owl2 representation unsupported", "Could not parse model", JOptionPane.ERROR_MESSAGE);
            return;
         case OwlRepresentationType.TYPE_UNSUPPORTED:
            JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), "XML representation unsupported", "Could not parse model", JOptionPane.ERROR_MESSAGE);
            return;
         case OwlRepresentationType.TYPE_OWL_TURTLE:
         case OwlRepresentationType.TYPE_OWL_XML:
            parseImpl(owlRepresentationType);
            break;
      }
   }

   private void parseImpl(short owlType) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      URI uri = file.toURI();
      OntoErrorHandler errorHandler = new OntoErrorHandler((GUIApplication) app);
      ErrorHandlerFactory.setDefaultErrorHandler(errorHandler);
      OntModel model = createModel("OWL_MEM");
      try {
         // see https://web-semantique.developpez.com/tutoriels/jena/io/#LV-A
         switch (owlType) {
            case OwlRepresentationType.TYPE_OWL_XML:
               FileManager.get().readModel(model, uri.toString(), "N-TRIPLES");
               break;
            case OwlRepresentationType.TYPE_OWL_TURTLE:
               FileManager.get().readModel(model, uri.toString(), "TURTLE");
               break;
         }
         // getting a raw model is necessary because if we don't do that, we will use the reasoner when getting the individuals, and if can take a
         // very long time
         // see https://stackoverflow.com/questions/27645110/method-listindividual-takes-more-than-15-mins-with-dbpedia-2014-owl-2mb-siz
         Model _model = model.getRawModel();
         model = new OntModelImpl(OntModelSpecTypes.getOntModelSpec(conf.modelSpec), _model);
         model.setStrictMode(conf.strictMode);

         boolean addThingClass = conf.addThingClass;
         boolean showPackages = conf.showPackages;
         GraphExtractor extractor = new GraphExtractor(file, model, addThingClass, showPackages);
         schema = extractor.getGraph();
         diagram = new OwlDiagram(file.getName());
         diagram.setRepresentationType(owlRepresentationType);
         diagram.setFile(file);
         diagram.setSchema(schema);
         mxGraph graph = createGraph(schema);
         diagram.setGraph(graph);
         diagram.setKeyToCell(cell4Class);

         if (graphPanel == null) {
            graphPanel = new GraphPanel((GUIApplication) app);
         }
         graphPanel.setDiagram(diagram);
         if (extractor.hasErrors()) {
            SwingErrorLogger logger = new SwingErrorLogger();
            logger.showParserExceptions(extractor.getErrors());
         }
         if (! schema.hasNonForeignElements()) {
            ((GUIApplication) app).getMessageArea().append("The Ontology does not contain any non foreign elements", "red");
         }
         if (schema.isEmpty()) {
            String message;
            if (schema.hasForeignElements()) {
               message = "The Ontology only contains foreign elements, and the resulting diagram is empty, is it normal?";
            } else {
               message = "The resulting diagram is empty, is it normal?";
            }
            JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), message, "Warning when parsing model", JOptionPane.WARNING_MESSAGE);
         }
      } catch (OntologyException ex) {
         JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), ex.getMessage(), "Error when getting model graph", JOptionPane.ERROR_MESSAGE);
      } catch (RiotException ex) {
         JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), "RiotException: " + ex.getMessage(), "Error when parsing model", JOptionPane.ERROR_MESSAGE);
      } catch (ResourceRequiredException ex) {
         JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), "ResourceRequiredException: " + ex.getMessage(), "Error when parsing model", JOptionPane.ERROR_MESSAGE);
      }
   }

   private OntModel createModel(String ontologyModel) {
      switch (ontologyModel) {
         case "OWL_MEM_RDFS_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
         case "OWL_DL_MEM_RULE_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
         case "OWL_DL_MEM_TRANS_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
         case "OWL_LITE_MEM":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
         case "OWL_LITE_MEM_RDFS_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RDFS_INF);
         case "OWL_LITE_MEM_RULES_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RULES_INF);
         case "OWL_LITE_MEM_TRANS_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_TRANS_INF);
         case "OWL_MEM_MICRO_RULE_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
         case "OWL_MEM_MINI_RULE_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MINI_RULE_INF);
         case "OWL_MEM_TRANS_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
         case "RDFS_MEM_RDFS_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF);
         case "RDFS_MEM_TRANS_INF":
            return ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_TRANS_INF);
         case "OWL_DL_MEM":
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
         case "PELLET":
         case "OWL_MEM":
         default:
            return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
      }
   }

   @Override
   public String getMessage() {
      switch (owlRepresentationType) {
         case OwlRepresentationType.TYPE_UNDEFINED:
            return "Owl2 Representation undefined";
         case OwlRepresentationType.TYPE_OWL2_UNSUPPORTED:
            return "Owl2 Representation unsupported";
         case OwlRepresentationType.TYPE_UNSUPPORTED:
            return "Representation unsupported";
         case OwlRepresentationType.TYPE_OWL_TURTLE:
         case OwlRepresentationType.TYPE_OWL_XML:
            return this.getLongDescription() + " opened successfully";
         default:
            return "Owl2 Representation undefined";
      }
   }
}
