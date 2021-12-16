/*
Copyright (c) 2021, Hervé Girod
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

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.girod.jgraphml.GraphMLFactory;
import org.girod.jgraphml.model.Arrows;
import org.girod.jgraphml.model.DiagramDefaults;
import org.girod.jgraphml.model.EdgeLabel;
import org.girod.jgraphml.model.GraphMLDiagram;
import org.girod.jgraphml.model.GraphMLEdge;
import org.girod.jgraphml.model.GraphMLNode;
import org.girod.jgraphml.model.NodeLabel;
import org.girod.jgraphml.model.ShapeType;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdi.bootstrap.MDIApplication;

/**
 * The Action that save schemas as yEd diagrams.
 *
 * @since 0.1
 */
public class ExportGraphAction extends AbstractMDIAction {
   private File file = null;
   private OwlDiagram diagram = null;

   /**
    * Create the export File Action.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param diagram the diagram
    * @param file the file to open
    */
   public ExportGraphAction(MDIApplication app, String desc, String longDesc, OwlDiagram diagram, File file) {
      super(app, "Export Graph");
      this.file = file;
      this.diagram = diagram;
      this.setDescription(desc, longDesc);
   }

   @Override
   public void run() throws Exception {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      GraphMLFactory factory = GraphMLFactory.getInstance();
      GraphMLDiagram _diagram = factory.newDiagram();
      DiagramDefaults defaults = _diagram.getDefaults();
      defaults.edgeLabelAutoRotate = true;
      defaults.edgeLabelAutoFlip = true;
      defaults.nodeFontSize = 11;
      defaults.edgeFontSize = 11;
      defaults.autosized = true;
      defaults.padWidth = conf.padWidth;
      defaults.padHeight = conf.padHeight;
      defaults.edgeLabelDistance = -4f;
      defaults.edgeLabelPosition = EdgeLabel.ParamModel.POSITION_TAIL;
      defaults.arrowSource = Arrows.NONE;
      defaults.arrowTarget = Arrows.STANDARD;

      Map<ElementKey, GraphMLNode> elementToNode = new HashMap<>();
      OwlSchema schema = diagram.getSchema();

      Map<ElementKey, OwlClass> owlClasses = schema.getOwlClasses();
      Iterator<ElementKey> it = owlClasses.keySet().iterator();
      while (it.hasNext()) {
         ElementKey key = it.next();
         OwlClass owlClass = owlClasses.get(key);
         GraphMLNode node = _diagram.addNode();
         node.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
         node.getShapeNode().setFillColor(Color.LIGHT_GRAY);
         NodeLabel label = node.createLabel(true);
         label.setFontSize(11);
         label.setLabel(owlClass.getName());
         elementToNode.put(key, node);
      }

      // parent classes
      Iterator<ElementKey> it2 = owlClasses.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         OwlClass theClass = owlClasses.get(key);
         GraphMLNode theNode = elementToNode.get(key);
         Iterator<ElementKey> it3 = theClass.getSuperClasses().iterator();
         while (it3.hasNext()) {
            ElementKey parentKey = it3.next();
            if (owlClasses.containsKey(parentKey)) {
               GraphMLNode source = elementToNode.get(parentKey);
               GraphMLEdge edge = _diagram.addEdge(source, theNode);
               Arrows arrows = edge.getArrows();
               arrows.setSource(Arrows.WHITE_DELTA);
               arrows.setTarget(Arrows.NONE);
            }
         }
      }

      // properties
      it2 = owlClasses.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         OwlClass theClass = owlClasses.get(key);
         GraphMLNode theNode = elementToNode.get(key);
         Iterator<OwlProperty> it3 = theClass.getOwlProperties().values().iterator();
         while (it3.hasNext()) {
            OwlProperty property = it3.next();
            if (property instanceof OwlObjectProperty) {
               OwlObjectProperty objectProp = (OwlObjectProperty) property;
               Iterator<ElementKey> it4 = objectProp.getRange().keySet().iterator();
               while (it4.hasNext()) {
                  ElementKey propKey = it4.next();
                  if (owlClasses.containsKey(propKey)) {
                     GraphMLNode rangeNode = elementToNode.get(propKey);
                     GraphMLEdge edge = _diagram.addEdge(rangeNode, theNode);
                     EdgeLabel label = edge.createLabel(true);
                     label.setLabel(property.getName());
                     Arrows arrows = edge.getArrows();
                     arrows.setSource(Arrows.STANDARD);
                     arrows.setTarget(Arrows.NONE);
                  }
               }
            } else {
               OwlDatatypeProperty dataProperty = (OwlDatatypeProperty) property;
               GraphMLNode propertyNode = _diagram.addNode();
               propertyNode.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
               propertyNode.getShapeNode().setFillColor(Color.CYAN);
               NodeLabel label = propertyNode.createLabel(true);
               label.setFontSize(11);
               label.setLabel(dataProperty.getName());

               GraphMLEdge edge = _diagram.addEdge(propertyNode, theNode);
               EdgeLabel elabel = edge.createLabel(true);
               elabel.setLabel(property.getName());
               Arrows arrows = edge.getArrows();
               arrows.setSource(Arrows.STANDARD);
               arrows.setTarget(Arrows.NONE);
            }
         }
      }
      factory.saveDiagram(_diagram, file);
   }

   @Override
   public String getMessage() {
      return this.getLongDescription() + " exported successfully";
   }
}