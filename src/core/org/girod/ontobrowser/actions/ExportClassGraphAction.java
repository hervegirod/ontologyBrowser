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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.girod.jgraphml.model.Arrows;
import org.girod.jgraphml.model.GraphMLEdge;
import org.girod.jgraphml.model.GraphMLGroupNode;
import org.girod.jgraphml.model.GraphMLNode;
import org.girod.jgraphml.model.IGraphMLNode;
import org.girod.jgraphml.model.LineStyle;
import org.girod.jgraphml.model.NodeLabel;
import org.girod.jgraphml.model.PortConstraints;
import org.girod.jgraphml.model.ShapeType;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.gui.CustomGraphStyles;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.MDIApplication;

/**
 * The Action that save Classes as yEd diagrams.
 *
 * @version 0.15
 */
public class ExportClassGraphAction extends AbstractExportGraphAction {
   private final OwlSchema schema;
   private final List<OwlClass> selectedClasses;
   private final Set<ElementKey> selectedClassesKeys;
   private boolean showAlias = false;
   private int maximumRadius = 1;
   private int effectiveRadius = 0;
   private boolean addedRadius = false;
   private Set<ElementKey> newClasses = new HashSet<>();
   private Map<ElementKey, OwlProperty> owlProperties = null;
   private Map<ElementKey, OwlClass> owlClasses = null;

   /**
    * Create the export File Action.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param diagram the diagram
    * @param theClass the Class
    * @param file the file to open
    */
   public ExportClassGraphAction(MDIApplication app, String desc, String longDesc, OwlDiagram diagram, OwlClass theClass, File file) {
      super(app, desc, longDesc, diagram, file);
      this.schema = diagram.getSchema();
      this.selectedClasses = new ArrayList<>();
      this.selectedClasses.add(theClass);
      this.selectedClassesKeys = new HashSet<>();
      this.selectedClassesKeys.add(theClass.getKey());
   }

   /**
    * Create the export File Action.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param diagram the diagram
    * @param theClasses the Classes list
    * @param file the file to open
    */
   public ExportClassGraphAction(MDIApplication app, String desc, String longDesc, OwlDiagram diagram, List<OwlClass> theClasses, File file) {
      super(app, desc, longDesc, diagram, file);
      this.schema = diagram.getSchema();
      this.selectedClasses = theClasses;
      this.selectedClassesKeys = new HashSet<>();
      Iterator<OwlClass> it = theClasses.iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         this.selectedClassesKeys.add(theClass.getKey());
      }
   }

   @Override
   protected GraphMLGroupNode getPackageNode(OwlClass theClass, ElementKey key) {
      return null;
   }

   private GraphMLNode addIndividual(OwlClass owlClass, GraphMLNode node, OwlIndividual individual) {
      GraphMLNode inode;
      if (owlClass.isPackageOrInPackage()) {
         ElementKey packageKey = owlClass.getPackage(false);
         GraphMLGroupNode groupNode = packagesNodes.get(packageKey);
         inode = groupNode.addNode();
      } else {
         inode = graph.addNode();
      }
      inode.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
      inode.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.INDIVIDUAL));
      NodeLabel ilabel = inode.createLabel(true);
      ilabel.setFontSize(11);
      ilabel.setLabel(individual.getDisplayedName());
      GraphMLEdge edge = graph.addEdge(node, inode);
      if (hasChildrenLayout) {
         edge.setPortConstraint(PortConstraints.TARGET, PortConstraints.POSITION_SOUTH);
         edge.setPortConstraint(PortConstraints.SOURCE, PortConstraints.POSITION_NORTH);
      }
      Arrows arrows = edge.getArrows();
      arrows.setSource(Arrows.NONE);
      arrows.setTarget(Arrows.NONE);
      return inode;
   }

   private GraphMLNode addRootNode(OwlClass theClass) {
      GraphMLNode node;
      ElementKey theKey = theClass.getKey();
      if (elementToNode.containsKey(theKey)) {
         node = (GraphMLNode) elementToNode.get(theKey);
      } else {
         node = graph.addNode();
         node.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
         node.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.CLASS));
         LineStyle style = new LineStyle();
         style.setWidth(3);
         style.setColor(Color.BLUE);
         node.getShapeNode().setBorderStyle(style);
         NodeLabel label = node.createLabel(true);
         label.setTextColor(Color.BLUE);
         label.setFontStyle(NodeLabel.STYLE_BOLD);
         label.setFontSize(11);
         label.setLabel(theClass.getDisplayedName());
         elementToNode.put(theClass.getKey(), node);
      }

      // individuals
      if (showIndividuals && theClass.hasIndividuals()) {
         Iterator<OwlIndividual> it2 = theClass.getIndividuals().values().iterator();
         while (it2.hasNext()) {
            OwlIndividual individual = it2.next();
            addIndividual(theClass, node, individual);
         }
      }
      return node;
   }

   private void exportAllImpl() {
      elementToNode = new HashMap<>();
      owlClasses = schema.getOwlClasses();
      owlProperties = schema.getOwlProperties();

      exportClasses();
      sortNodesPosition();
   }

   private void exportClassProperties(OwlClass theClass, GraphMLNode theRootNode) {
      Map<ElementKey, OwlProperty> processedProperties = new HashMap<>();
      Iterator<OwlProperty> it3 = theClass.getOwlProperties().values().iterator();
      while (it3.hasNext()) {
         OwlProperty property = it3.next();
         if (property instanceof OwlObjectProperty) {
            OwlObjectProperty objectProp = (OwlObjectProperty) property;
            Iterator<ElementKey> it4 = objectProp.getRange().keySet().iterator();
            while (it4.hasNext()) {
               ElementKey propKey = it4.next();
               if (owlClasses.containsKey(propKey)) {
                  EdgePair pair = new EdgePair(property, theClass.getKey(), propKey);
                  if (!processedEdges.contains(pair)) {
                     processedEdges.add(pair);
                     OwlClass rangeClass = owlClasses.get(propKey);
                     IGraphMLNode rangeNode = createClassNode(rangeClass);
                     GraphMLEdge edge = graph.addEdge(rangeNode, theRootNode);
                     addLabelOnEdge(edge, objectProp);
                     if (showRelationsConstraints) {
                        addCardinalityRestriction(property, edge);
                     }
                  }
               }
            }
         } else if (showProperties) {
            OwlDatatypeProperty dataProperty = (OwlDatatypeProperty) property;
            EdgePair pair = new EdgePair(property, theClass.getKey());
            if (!processedEdges.contains(pair)) {
               processedEdges.add(pair);
               IGraphMLNode propertyNode = createDataPropertyNode(dataProperty);
               processedProperties.put(dataProperty.getKey(), dataProperty);
               GraphMLEdge edge = graph.addEdge(propertyNode, theRootNode);
               if (hasChildrenLayout) {
                  edge.setPortConstraint(PortConstraints.TARGET, PortConstraints.POSITION_SOUTH);
                  edge.setPortConstraint(PortConstraints.SOURCE, PortConstraints.POSITION_NORTH);
               }
               Arrows arrows = edge.getArrows();
               arrows.setSource(Arrows.STANDARD);
               arrows.setTarget(Arrows.NONE);
            }
         }
      }
      Set<ElementKey> inversePropertiesToSkip = new HashSet<>();
      Iterator<OwlObjectProperty> it5 = theClass.getRangeOwlProperties().values().iterator();
      while (it5.hasNext()) {
         OwlObjectProperty property = it5.next();

         if (property.hasInverseProperty()) {
            ElementKey thePropertyKey = property.getKey();
            if (inversePropertiesToSkip.contains(thePropertyKey)) {
               continue;
            } else {
               inversePropertiesToSkip.add(property.getInverseProperty().getKey());
            }
         }

         Iterator<ElementKey> it4 = property.getDomain().keySet().iterator();
         while (it4.hasNext()) {
            ElementKey propKey = it4.next();
            if (owlClasses.containsKey(propKey)) {
               EdgePair pair = new EdgePair(property, propKey, theClass.getKey());
               if (!processedEdges.contains(pair)) {
                  processedEdges.add(pair);
                  OwlClass domainClass = owlClasses.get(propKey);
                  IGraphMLNode domainNode = createClassNode(domainClass);
                  GraphMLEdge edge = graph.addEdge(domainNode, theRootNode);
                  addLabelOnEdge(edge, property);
                  Arrows arrows = edge.getArrows();
                  if (property.hasInverseProperty()) {
                     arrows.setSource(Arrows.STANDARD);
                     arrows.setTarget(Arrows.STANDARD);
                  } else {
                     arrows.setSource(Arrows.STANDARD);
                     arrows.setTarget(Arrows.NONE);
                  }
                  if (showRelationsConstraints) {
                     addCardinalityRestriction(property, edge);
                  }
               }
            }
         }
      }
      if (showAlias) {
         Iterator<OwlProperty> it2 = processedProperties.values().iterator();
         while (it2.hasNext()) {
            OwlProperty property = it2.next();
            if (property instanceof OwlDatatypeProperty && property.hasAliasElements()) {
               exportPropertyAlias((OwlDatatypeProperty) property);
            }
         }
      }
   }

   private GraphMLNode createDataPropertyNode(OwlDatatypeProperty dataProperty) {
      GraphMLNode propertyNode;
      if (elementToNode.containsKey(dataProperty.getKey())) {
         propertyNode = (GraphMLNode) elementToNode.get(dataProperty.getKey());
      } else {
         propertyNode = graph.addNode();
         elementToNode.put(dataProperty.getKey(), propertyNode);
         propertyNode.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
         propertyNode.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.PROPERTY));
         NodeLabel label = propertyNode.createLabel(true);
         if (showDataPropertiesTypes) {
            String typeS = getType(dataProperty);
            if (typeS != null) {
               label.setLabel(dataProperty.getDisplayedName() + "\n" + typeS);
               propertyNode.setHeight(propertyNode.getHeight() * 1.5f);
            } else {
               label.setLabel(dataProperty.getDisplayedName());
            }
         } else {
            label.setLabel(dataProperty.getDisplayedName());
         }
      }

      return propertyNode;
   }

   private void exportPropertyAlias(OwlDatatypeProperty theProperty) {
      Iterator<ElementKey> it3 = theProperty.getAliasElements().keySet().iterator();
      while (it3.hasNext()) {
         ElementKey key = it3.next();
         OwlDatatypeProperty aliasProperty = (OwlDatatypeProperty) owlProperties.get(key);
         IGraphMLNode source = createDataPropertyNode(theProperty);
         IGraphMLNode target = createDataPropertyNode(aliasProperty);
         GraphMLEdge edge = graph.addEdge(source, target);
         Arrows arrows = edge.getArrows();
         arrows.setSource(Arrows.NONE);
         arrows.setTarget(Arrows.NONE);
         LineStyle lineStyle = new LineStyle();
         lineStyle.setLineStyle(LineStyle.DASHED);
         edge.setLineStyle(lineStyle);
      }
   }

   private void exportClasses() {
      Iterator<OwlClass> it = selectedClasses.iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         exportClass(theClass);
      }
      boolean executeNewSession = effectiveRadius < maximumRadius && !newClasses.isEmpty();
      while (executeNewSession) {
         addedRadius = false;
         Set<ElementKey> currentNewClasses = new HashSet<>(newClasses);
         newClasses.clear();
         Iterator<ElementKey> it2 = currentNewClasses.iterator();
         while (it2.hasNext()) {
            ElementKey key = it2.next();
            OwlClass theClass = schema.getOwlClass(key);
            exportClass(theClass);
         }
         newClasses.addAll(currentNewClasses);
         executeNewSession = effectiveRadius < maximumRadius && !newClasses.isEmpty();
      }
   }

   private void exportClass(OwlClass theClass) {
      GraphMLNode theRootNode = addRootNode(theClass);

      // parent classes
      Iterator<ElementKey> it3 = theClass.getSuperClasses().keySet().iterator();
      while (it3.hasNext()) {
         ElementKey parentKey = it3.next();
         if (owlClasses.containsKey(parentKey)) {
            OwlClass parentClass = owlClasses.get(parentKey);
            IGraphMLNode theNode = createClassNode(parentClass);
            GraphMLEdge edge = graph.addEdge(theRootNode, theNode);
            if (hasLayoutOption) {
               edge.setPortConstraint(PortConstraints.TARGET, PortConstraints.POSITION_SOUTH);
               edge.setPortConstraint(PortConstraints.SOURCE, PortConstraints.POSITION_NORTH);
            }
            Arrows arrows = edge.getArrows();
            arrows.setSource(Arrows.NONE);
            arrows.setTarget(Arrows.WHITE_DELTA);
         }
      }
      // sub classes
      it3 = theClass.getSubClasses().keySet().iterator();
      while (it3.hasNext()) {
         ElementKey parentKey = it3.next();
         if (owlClasses.containsKey(parentKey)) {
            OwlClass parentClass = owlClasses.get(parentKey);
            IGraphMLNode theNode = createClassNode(parentClass);
            GraphMLEdge edge = graph.addEdge(theRootNode, theNode);
            if (hasLayoutOption) {
               edge.setPortConstraint(PortConstraints.SOURCE, PortConstraints.POSITION_SOUTH);
               edge.setPortConstraint(PortConstraints.TARGET, PortConstraints.POSITION_NORTH);
            }
            Arrows arrows = edge.getArrows();
            arrows.setSource(Arrows.WHITE_DELTA);
            arrows.setTarget(Arrows.NONE);
         }
      }
      if (showAlias && theClass.hasAliasClasses()) {
         exportClassAlias(theClass, theRootNode);
      }
      this.exportClassProperties(theClass, theRootNode);
   }

   private GraphMLNode createClassNode(OwlClass owlClass) {
      ElementKey key = owlClass.getKey();
      GraphMLNode theNode;
      if (elementToNode.containsKey(key)) {
         theNode = (GraphMLNode) elementToNode.get(key);
      } else if (effectiveRadius <= maximumRadius) {
         GraphMLNode theNode1 = graph.addNode();
         newClasses.add(owlClass.getKey());
         theNode = theNode1;
         elementToNode.put(key, theNode);
         theNode1.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
         theNode1.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.CLASS));
         NodeLabel label = theNode1.createLabel(true);
         label.setFontSize(11);
         label.setLabel(owlClass.getDisplayedName());
         if (!addedRadius) {
            addedRadius = true;
            effectiveRadius++;
         }
      } else {
         theNode = null;
      }
      return theNode;
   }

   private void exportClassAlias(OwlClass theClass, GraphMLNode theRootNode) {
      Iterator<ElementKey> it3 = theClass.getAliasClasses().keySet().iterator();
      while (it3.hasNext()) {
         ElementKey keyAlias = it3.next();
         if (owlClasses.containsKey(keyAlias)) {
            OwlClass aliasClass = owlClasses.get(keyAlias);
            IGraphMLNode theNode = createClassNode(aliasClass);
            GraphMLEdge edge = graph.addEdge(theRootNode, theNode);
            Arrows arrows = edge.getArrows();
            arrows.setSource(Arrows.NONE);
            arrows.setTarget(Arrows.NONE);
            LineStyle lineStyle = new LineStyle();
            lineStyle.setLineStyle(LineStyle.DASHED);
            edge.setLineStyle(lineStyle);
         }
      }
   }

   @Override
   protected void configure() {
      super.configure();
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      this.showAlias = conf.showAlias;
      this.maximumRadius = conf.maximumRadius;
   }

   /**
    * Perform the export.
    */
   @Override
   public void export() {
      elementToNode = new HashMap<>();
      exportAllImpl();
   }

   @Override
   public void run() throws Exception {
      configure();

      export();
      saveDiagram();
   }
}
