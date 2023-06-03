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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.girod.jgraphml.model.Arrows;
import org.girod.jgraphml.model.EdgeLabel;
import org.girod.jgraphml.model.GraphMLEdge;
import org.girod.jgraphml.model.GraphMLGroupNode;
import org.girod.jgraphml.model.GraphMLNode;
import org.girod.jgraphml.model.IGraphMLNode;
import org.girod.jgraphml.model.NodeLabel;
import org.girod.jgraphml.model.NodeParent;
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
 * @version 0.4
 */
public class ExportPackageGraphAction extends AbstractExportGraphAction {
   private final OwlClass thePackage;
   private final ElementKey thePackageKey;
   private boolean showPackagesInPackageView = false;
   private final Set<ElementKey> subPackages = new HashSet<>();
   private Map<ElementKey, OwlClass> owlClasses = null;
   private Map<ElementKey, OwlClass> allOwlClasses = null;
   private final Map<ElementKey, IGraphMLNode> elementToNode = new HashMap<>();

   /**
    * Create the export File Action.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param diagram the diagram
    * @param thePackage the package to export
    * @param file the file to open
    */
   public ExportPackageGraphAction(MDIApplication app, String desc, String longDesc, OwlDiagram diagram, OwlClass thePackage, File file) {
      super(app, desc, longDesc, diagram, file);
      this.thePackage = thePackage;
      this.thePackageKey = thePackage.getKey();
   }

   @Override
   protected GraphMLGroupNode getPackageNode(OwlClass theClass, ElementKey key) {
      if (packagesNodes.containsKey(key)) {
         return packagesNodes.get(key);
      } else {
         OwlClass superClass = theClass.getFirstSuperClass();
         if (superClass == null) {
            GraphMLGroupNode node = graph.addGroupNode();
            setGroupNodeStyle(node, theClass.getName());
            packagesNodes.put(key, node);
            return node;
         } else {
            ElementKey superclassKey = superClass.getKey();
            if (superclassKey.equals(thePackageKey)) {
               GraphMLGroupNode node = graph.addGroupNode();
               setGroupNodeStyle(node, theClass.getName());
               packagesNodes.put(key, node);
               return node;
            } else if (packagesNodes.containsKey(superclassKey)) {
               GraphMLGroupNode superclassNode = packagesNodes.get(superclassKey);
               GraphMLGroupNode node = superclassNode.addGroupNode();
               setGroupNodeStyle(node, theClass.getName());
               packagesNodes.put(key, node);
               return node;
            } else {
               GraphMLGroupNode superclassNode = getPackageNode(schema.getOwlClass(superclassKey), superclassKey);
               GraphMLGroupNode node = superclassNode.addGroupNode();
               setGroupNodeStyle(node, theClass.getName());
               packagesNodes.put(key, node);
               return node;
            }
         }
      }
   }

   private void detectSubPackages() {
      if (schema.hasPackages()) {
         Iterator<Entry<ElementKey, OwlClass>> it = schema.getPackages().entrySet().iterator();
         while (it.hasNext()) {
            Entry<ElementKey, OwlClass> entry = it.next();
            ElementKey key = entry.getKey();
            if (!key.equals(thePackageKey)) {
               OwlClass theClass = entry.getValue();
               if (theClass.isInPackage()) {
                  ElementKey parentPackageKey = theClass.getPackage();
                  boolean isInPackage = checkForPackage(parentPackageKey);
                  if (isInPackage) {
                     subPackages.add(key);
                  }
               }
            }
         }
      }
   }

   private boolean checkForPackage(ElementKey key) {
      if (key.equals(thePackageKey)) {
         return true;
      } else {
         OwlClass theClass = schema.getOwlClass(key);
         if (!theClass.isPackage()) {
            return false;
         } else if (!theClass.isInPackage()) {
            return false;
         } else {
            ElementKey packageKey = theClass.getPackage();
            if (packageKey.equals(thePackageKey)) {
               return true;
            } else {
               return checkForPackage(packageKey);
            }
         }
      }
   }

   private void addPackages() {
      detectSubPackages();

      Iterator<Entry<ElementKey, OwlClass>> it = owlClasses.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlClass> entry = it.next();
         ElementKey key = entry.getKey();
         OwlClass owlClass = entry.getValue();
         if (schema.isPackage(key)) {
            getPackageNode(owlClass, key);
         }
      }
   }

   private GraphMLGroupNode getGroupParent(ElementKey key, OwlClass owlClass) {
      if (owlClass.isInPackage()) {
         ElementKey _thePackageKey = owlClass.getPackage();
         return packagesNodes.get(_thePackageKey);
      } else {
         return null;
      }
   }

   private GraphMLNode addIndividualInPackage(OwlClass owlClass, IGraphMLNode node, OwlIndividual individual) {
      GraphMLNode inode;
      if (owlClass.isInPackage()) {
         ElementKey packageKey = owlClass.getPackage();
         GraphMLGroupNode groupNode = packagesNodes.get(packageKey);
         if (groupNode != null) {
            inode = groupNode.addNode();
         } else {
            inode = graph.addNode();
         }
      } else {
         inode = null;
      }
      if (inode == null) {
         return null;
      }
      inode.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
      inode.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.INDIVIDUAL));
      NodeLabel ilabel = inode.createLabel(true);
      ilabel.setFontSize(11);
      ilabel.setLabel(individual.getName());
      GraphMLEdge edge = graph.addEdge(node, inode);
      Arrows arrows = edge.getArrows();
      arrows.setSource(Arrows.NONE);
      arrows.setTarget(Arrows.NONE);
      return inode;
   }

   private GraphMLNode addDataPropertyInPackage(IGraphMLNode node, OwlDatatypeProperty dataProperty) {
      NodeParent parent = node.getParent();
      GraphMLNode propertyNode = parent.addNode();
      propertyNode.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
      propertyNode.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.PROPERTY));
      NodeLabel label = propertyNode.createLabel(true);
      label.setFontSize(11);
      if (showDataPropertiesTypes) {
         String typeS = getType(dataProperty);
         if (typeS != null) {
            label.setLabel(dataProperty.getName() + "\n" + typeS);
            propertyNode.setHeight(propertyNode.getHeight() * 1.5f);
         } else {
            label.setLabel(dataProperty.getName());
         }
      } else {
         label.setLabel(dataProperty.getName());
      }
      return propertyNode;
   }

   private IGraphMLNode addLeafNodeImpl(ElementKey key, OwlClass owlClass, boolean isPackage) {
      IGraphMLNode node;
      if (isPackage) {
         node = this.packagesNodes.get(key);
      } else {
         GraphMLNode gnode;
         GraphMLGroupNode groupNode = getGroupParent(key, owlClass);
         if (groupNode == null) {
            gnode = graph.addNode();
         } else {
            gnode = groupNode.addNode();
         }
         node = gnode;
         gnode.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
         gnode.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.CLASS));
         NodeLabel label = gnode.createLabel(true);
         label.setFontSize(11);
         label.setLabel(owlClass.getName());
         elementToNode.put(key, node);
      }

      if (owlClass.hasSubClasses()) {
         Iterator<OwlClass> it = owlClass.getSubClasses().values().iterator();
         while (it.hasNext()) {
            OwlClass parentClass = it.next();
            ElementKey parentKey = parentClass.getKey();
            if (!parentKey.equals(thePackageKey)) {
               IGraphMLNode source = addLeafNode(parentKey, parentClass);
               if (!owlClass.isPackage()) {
                  GraphMLEdge edge = graph.addEdge(source, node);
                  Arrows arrows = edge.getArrows();
                  arrows.setSource(Arrows.NONE);
                  arrows.setTarget(Arrows.WHITE_DELTA);
               }
            }
         }
      }

      // individuals
      if (owlClass.hasIndividuals()) {
         Iterator<OwlIndividual> it2 = owlClass.getIndividuals().values().iterator();
         while (it2.hasNext()) {
            OwlIndividual individual = it2.next();
            addIndividualInPackage(owlClass, node, individual);
         }
      }
      return node;
   }

   private IGraphMLNode addLeafNode(ElementKey key, OwlClass owlClass) {
      if (elementToNode.containsKey(key)) {
         return elementToNode.get(key);
      } else if (packagesNodes.containsKey(key)) {
         if (elementToNode.containsKey(key)) {
            return elementToNode.put(key, packagesNodes.get(key));
         } else {
            IGraphMLNode node = addLeafNodeImpl(key, owlClass, true);
            return node;
         }
      } else {
         IGraphMLNode node = addLeafNodeImpl(key, owlClass, false);
         return node;
      }
   }

   private void addProperties() {
      Iterator<Entry<ElementKey, OwlClass>> it2 = allOwlClasses.entrySet().iterator();
      while (it2.hasNext()) {
         Entry<ElementKey, OwlClass> entry = it2.next();
         OwlClass theClass = entry.getValue();
         IGraphMLNode theNode = elementToNode.get(entry.getKey());
         Iterator<OwlProperty> it3 = theClass.getOwlProperties().values().iterator();
         while (it3.hasNext()) {
            OwlProperty property = it3.next();
            if (property instanceof OwlObjectProperty) {
               OwlObjectProperty objectProp = (OwlObjectProperty) property;
               Iterator<ElementKey> it4 = objectProp.getRange().keySet().iterator();
               while (it4.hasNext()) {
                  ElementKey propKey = it4.next();
                  if (elementToNode.containsKey(propKey)) {
                     IGraphMLNode rangeNode = elementToNode.get(propKey);
                     GraphMLEdge edge = graph.addEdge(rangeNode, theNode);
                     EdgeLabel label = edge.createLabel(true);
                     label.setLabel(property.getName());
                     Arrows arrows = edge.getArrows();
                     arrows.setSource(Arrows.STANDARD);
                     arrows.setTarget(Arrows.NONE);
                     if (showRelationsConstraints) {
                        if (property.hasCardinalityRestriction()) {
                           addCardinalityRestriction(property, edge);
                        } else {
                           addDefaultCardinalityRestriction(edge);
                        }
                     }
                  } else {
                     OwlClass targetClass = schema.getOwlClass(propKey);
                     if (targetClass != null) {
                        GraphMLNode node;
                        if (targetClass.isInPackage()) {
                           ElementKey packageKey = targetClass.getPackage();
                           if (showPackagesInPackageView) {
                              GraphMLGroupNode packageNode;
                              if (packagesNodes.containsKey(packageKey)) {
                                 packageNode = packagesNodes.get(packageKey);
                              } else {
                                 OwlClass packageClass = schema.getOwlClass(packageKey);
                                 packageNode = graph.addGroupNode();
                                 setGroupNodeStyle(packageNode, packageClass.getName());
                                 packagesNodes.put(entry.getKey(), packageNode);
                                 packageNode.setRealizedStateValue(false);
                              }
                              node = packageNode.addNode();
                              node.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
                              node.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.CLASS));
                              NodeLabel label = node.createLabel(true);
                              label.setFontSize(11);
                              label.setLabel(targetClass.getName());
                           } else {
                              node = graph.addNode();
                              node.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
                              node.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.EXTERNAL_PACKAGE));
                              NodeLabel label = node.createLabel(true);
                              label.setFontSize(11);
                              label.setLabel(targetClass.getName() + "\nfrom " + packageKey.getName());
                              node.setHeight(node.getHeight() * 1.5f);
                           }
                           elementToNode.put(entry.getKey(), node);
                        } else {
                           node = graph.addNode();
                           node.getShapeNode().setType(ShapeType.ROUNDRECTANGLE);
                           node.getShapeNode().setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.EXTERNAL_PACKAGE));
                           NodeLabel label = node.createLabel(true);
                           label.setFontSize(11);
                           label.setLabel(targetClass.getName());
                           elementToNode.put(entry.getKey(), node);
                        }

                        GraphMLEdge edge = graph.addEdge(node, theNode);
                        EdgeLabel elabel = edge.createLabel(true);
                        elabel.setLabel(property.getName());
                        Arrows arrows = edge.getArrows();
                        arrows.setSource(Arrows.STANDARD);
                        arrows.setTarget(Arrows.NONE);
                        if (showRelationsConstraints) {
                           if (property.hasCardinalityRestriction()) {
                              addCardinalityRestriction(property, edge);
                           } else {
                              addDefaultCardinalityRestriction(edge);
                           }
                        }
                     }
                  }
               }
            } else {
               OwlDatatypeProperty dataProperty = (OwlDatatypeProperty) property;
               GraphMLNode propertyNode = addDataPropertyInPackage(theNode, dataProperty);
               GraphMLEdge edge = graph.addEdge(propertyNode, theNode);
               EdgeLabel elabel = edge.createLabel(true);
               elabel.setLabel(property.getName());
               Arrows arrows = edge.getArrows();
               arrows.setSource(Arrows.STANDARD);
               arrows.setTarget(Arrows.NONE);
            }
         }
      }
   }

   private void getAllOWlClassses() {
      allOwlClasses = new HashMap<>();
      Iterator<Entry<ElementKey, OwlClass>> it = owlClasses.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlClass> entry = it.next();
         OwlClass theClass = entry.getValue();
         allOwlClasses.put(entry.getKey(), theClass);
         getAllOWlClassses(theClass);
      }
   }

   private void getAllOWlClassses(OwlClass theClass) {
      Map<ElementKey, OwlClass> subClasses = theClass.getSubClasses();
      Iterator<Entry<ElementKey, OwlClass>> it = subClasses.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlClass> entry = it.next();
         OwlClass theClass2 = entry.getValue();
         allOwlClasses.put(entry.getKey(), theClass2);
         getAllOWlClassses(theClass2);
      }
   }

   private void exportPackageImpl() {
      owlClasses = thePackage.getSubClasses();
      getAllOWlClassses();
      addPackages();

      Iterator<Entry<ElementKey, OwlClass>> it = owlClasses.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlClass> entry = it.next();
         ElementKey key = entry.getKey();
         OwlClass owlClass = entry.getValue();
         addLeafNode(key, owlClass);
      }

      // properties
      addProperties();
   }
   
   @Override
   protected void configure() {
      super.configure();
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      this.showPackagesInPackageView = conf.showPackagesInPackageView;   
   }   

   @Override
   public void run() throws Exception {
      configure();

      exportPackageImpl();

      saveDiagram();
   }
}
