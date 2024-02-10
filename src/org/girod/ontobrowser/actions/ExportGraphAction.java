/*
Copyright (c) 2021, 2022, 2023, 2024 Hervé Girod
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.girod.jgraphml.model.Arrows;
import org.girod.jgraphml.model.EdgeLabel;
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
import org.mdi.bootstrap.MDIApplication;

/**
 * The Action that save schemas as yEd diagrams.
 *
 * @version 0.10
 */
public class ExportGraphAction extends AbstractExportGraphAction {
   private boolean showPackages = false;
   private boolean showAlias = false;

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
      super(app, desc, longDesc, diagram, file);
   }

   @Override
   protected GraphMLGroupNode getPackageNode(OwlClass theClass, ElementKey key) {
      if (packagesNodes.containsKey(key)) {
         return packagesNodes.get(key);
      } else {
         OwlClass superClass = theClass.getFirstSuperClass();
         if (superClass == null) {
            GraphMLGroupNode node = graph.addGroupNode();
            elementToNode.put(key, node);
            setGroupNodeStyle(node, theClass.getDisplayedName());
            packagesNodes.put(key, node);
            processedPackages.add(key);
            return node;
         } else {
            ElementKey superclassKey = superClass.getKey();
            if (!processedPackages.contains(superclassKey)) {
               getPackageNode(superClass, superclassKey);
            }
            if (packagesNodes.containsKey(superclassKey)) {
               GraphMLGroupNode superclassNode = packagesNodes.get(superclassKey);
               GraphMLGroupNode node = superclassNode.addGroupNode();
               elementToNode.put(key, node);
               setGroupNodeStyle(node, theClass.getDisplayedName());
               packagesNodes.put(key, node);
               processedPackages.add(key);
               return node;
            } else {
               GraphMLGroupNode node = graph.addGroupNode();
               elementToNode.put(key, node);
               setGroupNodeStyle(node, theClass.getDisplayedName());
               packagesNodes.put(key, node);
               processedPackages.add(key);
               return node;
            }
         }
      }
   }

   private GraphMLGroupNode getGroupParent(ElementKey key, OwlClass owlClass) {
      if (packagesNodes == null || !owlClass.isInUniquePackage()) {
         return null;
      } else {
         ElementKey thePackageKey = owlClass.getPackage();
         return packagesNodes.get(thePackageKey);
      }
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
      Arrows arrows = edge.getArrows();
      arrows.setSource(Arrows.NONE);
      arrows.setTarget(Arrows.NONE);
      return inode;
   }

   private GraphMLNode addDataProperty(OwlClass owlClass, OwlDatatypeProperty dataProperty) {
      GraphMLNode propertyNode;
      if (owlClass.isPackageOrInPackage()) {
         ElementKey packageKey = owlClass.getPackage(false);
         GraphMLGroupNode groupNode = packagesNodes.get(packageKey);
         propertyNode = groupNode.addNode();
      } else {
         propertyNode = graph.addNode();
      }
      propertyNode.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
      propertyNode.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.PROPERTY));
      NodeLabel label = propertyNode.createLabel(true);
      label.setFontSize(11);
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
      return propertyNode;
   }

   private GraphMLNode addLeafNode(ElementKey key, OwlClass owlClass) {
      GraphMLGroupNode groupNode = getGroupParent(key, owlClass);
      GraphMLNode node;
      if (groupNode == null) {
         node = graph.addNode();
      } else {
         node = groupNode.addNode();
      }
      node.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
      node.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.CLASS));
      NodeLabel label = node.createLabel(true);
      label.setFontSize(11);
      label.setLabel(owlClass.getDisplayedName());
      elementToNode.put(key, node);

      // individuals
      if (owlClass.hasIndividuals()) {
         Iterator<OwlIndividual> it2 = owlClass.getIndividuals().values().iterator();
         while (it2.hasNext()) {
            OwlIndividual individual = it2.next();
            addIndividual(owlClass, node, individual);
         }
      }
      return node;
   }

   private void addPackages() {
      if (showPackages) {
         Map<ElementKey, OwlClass> owlPackages = schema.getPackages();
         Iterator<Entry<ElementKey, OwlClass>> it = owlPackages.entrySet().iterator();
         while (it.hasNext()) {
            Entry<ElementKey, OwlClass> entry = it.next();
            ElementKey key = entry.getKey();
            OwlClass owlPackage = entry.getValue();
            getPackageNode(owlPackage, key);
         }
      }
   }

   private void exportAllImpl() {
      elementToNode = new HashMap<>();
      Map<ElementKey, OwlClass> owlClasses = schema.getOwlClasses();
      if (showPackages) {
         addPackages();
      }

      exportClasses(owlClasses);
      exportProperties(owlClasses);
   }

   private void exportProperties(Map<ElementKey, OwlClass> owlClasses) {
      // properties
      Iterator<ElementKey> it = owlClasses.keySet().iterator();
      while (it.hasNext()) {
         ElementKey key = it.next();
         OwlClass theClass = owlClasses.get(key);
         IGraphMLNode theNode = elementToNode.get(key);
         Iterator<OwlProperty> it3 = theClass.getOwlProperties().values().iterator();
         while (it3.hasNext()) {
            OwlProperty property = it3.next();
            if (property instanceof OwlObjectProperty) {
               OwlObjectProperty objectProp = (OwlObjectProperty) property;
               Iterator<ElementKey> it4 = objectProp.getRange().keySet().iterator();
               while (it4.hasNext()) {
                  ElementKey propKey = it4.next();
                  if (owlClasses.containsKey(propKey)) {
                     IGraphMLNode rangeNode = elementToNode.get(propKey);
                     GraphMLEdge edge = graph.addEdge(rangeNode, theNode);
                     EdgeLabel label = edge.createLabel(true);
                     label.setLabel(property.getDisplayedName());
                     Arrows arrows = edge.getArrows();
                     arrows.setSource(Arrows.STANDARD);
                     arrows.setTarget(Arrows.NONE);
                     if (showRelationsConstraints) {
                        addCardinalityRestriction(objectProp, edge);
                     }
                  }
               }
            } else {
               OwlDatatypeProperty dataProperty = (OwlDatatypeProperty) property;
               GraphMLNode propertyNode = addDataProperty(theClass, dataProperty);

               GraphMLEdge edge = graph.addEdge(propertyNode, theNode);
               EdgeLabel elabel = edge.createLabel(true);
               Arrows arrows = edge.getArrows();
               arrows.setSource(Arrows.STANDARD);
               arrows.setTarget(Arrows.NONE);

               if (showAlias && dataProperty.hasEquivalentProperties()) {
                  exportPropertyAlias(dataProperty);
               }
            }
         }
      }
   }

   private void exportPropertyAlias(OwlProperty theProperty) {
      Iterator<ElementKey> it3 = theProperty.getEquivalentProperties().keySet().iterator();
      while (it3.hasNext()) {
         ElementKey keyAlias = it3.next();
         IGraphMLNode source = elementToNode.get(theProperty.getKey());
         IGraphMLNode target = elementToNode.get(keyAlias);
         GraphMLEdge edge = graph.addEdge(source, target);
         Arrows arrows = edge.getArrows();
         arrows.setSource(Arrows.NONE);
         arrows.setTarget(Arrows.NONE);
         LineStyle lineStyle = new LineStyle();
         lineStyle.setLineStyle(LineStyle.DASHED);
         edge.setLineStyle(lineStyle);
      }
   }

   private void exportClasses(Map<ElementKey, OwlClass> owlClasses) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      Iterator<Entry<ElementKey, OwlClass>> it = owlClasses.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlClass> entry = it.next();
         ElementKey key = entry.getKey();
         OwlClass owlClass = entry.getValue();
         if (!showPackages || !packagesNodes.containsKey(key)) {
            addLeafNode(key, owlClass);
         }
      }

      // parent classes
      Iterator<ElementKey> it2 = owlClasses.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         OwlClass theClass = owlClasses.get(key);
         if (!showPackages || !packagesNodes.containsKey(key)) {
            IGraphMLNode theNode = elementToNode.get(key);
            Iterator<ElementKey> it3 = theClass.getSuperClasses().keySet().iterator();
            while (it3.hasNext()) {
               ElementKey parentKey = it3.next();
               if (owlClasses.containsKey(parentKey)) {
                  OwlClass parentClass = owlClasses.get(parentKey);
                  if (!showPackages || !parentClass.isPackage()) {
                     IGraphMLNode source = elementToNode.get(parentKey);
                     GraphMLEdge edge = graph.addEdge(source, theNode);
                     if (conf.superClassesOnTop) {
                        edge.setPortConstraint(PortConstraints.SOURCE, PortConstraints.POSITION_SOUTH);
                        edge.setPortConstraint(PortConstraints.TARGET, PortConstraints.POSITION_NORTH);
                     }
                     Arrows arrows = edge.getArrows();
                     arrows.setSource(Arrows.WHITE_DELTA);
                     arrows.setTarget(Arrows.NONE);
                  }
               }
            }
         }
         if (showAlias && theClass.hasAliasClasses()) {
            exportClassAlias(theClass);
         }
      }
   }

   private void exportClassAlias(OwlClass theClass) {
      Iterator<ElementKey> it3 = theClass.getAliasClasses().keySet().iterator();
      while (it3.hasNext()) {
         ElementKey keyAlias = it3.next();
         IGraphMLNode source = elementToNode.get(theClass.getKey());
         IGraphMLNode target = elementToNode.get(keyAlias);
         GraphMLEdge edge = graph.addEdge(source, target);
         Arrows arrows = edge.getArrows();
         arrows.setSource(Arrows.NONE);
         arrows.setTarget(Arrows.NONE);
         LineStyle lineStyle = new LineStyle();
         lineStyle.setLineStyle(LineStyle.DASHED);
         edge.setLineStyle(lineStyle);
      }
   }

   @Override
   protected void configure() {
      super.configure();
      this.showPackages = diagram.hasPackages();
      this.showAlias = BrowserConfiguration.getInstance().showAlias;
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
