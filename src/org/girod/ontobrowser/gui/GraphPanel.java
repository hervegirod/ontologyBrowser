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
package org.girod.ontobrowser.gui;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.actions.ExportPackageGraphAction;
import org.girod.ontobrowser.gui.tree.ModelTreeRenderer;
import org.girod.ontobrowser.gui.tree.OwlElementRep;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.ElementTypes;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.MDIDialogType;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdiutil.io.FileUtilities;

/**
 * The panel for one ontology graph.
 *
 * @version 0.5
 */
public class GraphPanel extends JSplitPane {
   private final GUIApplication browser;
   // Classes tree
   private final DefaultMutableTreeNode thingRoot = new DefaultMutableTreeNode("Thing");
   private ElementKey thingKey = null;
   private final DefaultTreeModel classTreeModel = new DefaultTreeModel(thingRoot);
   private final JTree classTree = new JTree(classTreeModel);
   // Properties tree
   private final DefaultMutableTreeNode propertiesRoot = new DefaultMutableTreeNode("Properties");
   private final DefaultMutableTreeNode dataPropertiesRoot = new DefaultMutableTreeNode("Data Properties");
   private final DefaultMutableTreeNode objectPropertiesRoot = new DefaultMutableTreeNode("Object Properties");
   private final DefaultTreeModel propertiesTreeModel = new DefaultTreeModel(propertiesRoot);
   private final JTree propertiesTree = new JTree(propertiesTreeModel);
   // Individuals tree
   private final DefaultMutableTreeNode individualsRoot = new DefaultMutableTreeNode("Individuals");
   private final DefaultTreeModel individualsTreeModel = new DefaultTreeModel(individualsRoot);
   private final JTree individualsTree = new JTree(individualsTreeModel);
   // Packages tree
   private DefaultMutableTreeNode thingPackagesRoot = null;
   private DefaultTreeModel packagesModel = null;
   private JTree packagesTree = null;
   private JTabbedPane modelTab;
   private OwlDiagram diagram;
   private OwlSchema schema;
   private mxGraphComponent graphComp = null;
   private final JPanel diagramPanel = new JPanel();
   private OwlElementRep selectedElement = null;
   private final Map<ElementKey, DefaultMutableTreeNode> keyToClassNode = new HashMap<>();
   private final Map<ElementKey, DefaultMutableTreeNode> keyToPropertyNode = new HashMap<>();
   private final Map<ElementKey, DefaultMutableTreeNode> keyToIndividualNode = new HashMap<>();

   public GraphPanel(GUIApplication browser) {
      super(JSplitPane.HORIZONTAL_SPLIT);
      this.browser = browser;
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
    * Return the Owl diagram.
    *
    * @return the Owl diagram
    */
   public OwlDiagram getDiagram() {
      return diagram;
   }

   /**
    * Return the Owl classes tree.
    *
    * @return the Owl classes tree
    */
   public JTree getClassTree() {
      return classTree;
   }

   /**
    * Return the Owl properties tree.
    *
    * @return the Owl properties tree
    */
   public JTree getPropertiesTree() {
      return propertiesTree;
   }

   /**
    * Return the Owl individuals tree.
    *
    * @return the Owl individuals tree
    */
   public JTree getIndividualsTree() {
      return individualsTree;
   }

   /**
    * Return the packages tree.
    *
    * @return the packages tree
    */
   public JTree getPackagesTree() {
      return packagesTree;
   }

   /**
    * Hightlight an element.
    *
    * @param elementType the element type
    * @param path the element path
    */
   public void highlightElement(String elementType, TreePath path) {
      switch (elementType) {
         case ElementTypes.CLASS:
            classTree.setSelectionPath(path);
            classTree.scrollPathToVisible(path);
            modelTab.setSelectedIndex(0);
            break;
         case ElementTypes.PROPERTY:
         case ElementTypes.DATAPROPERTY:
         case ElementTypes.OBJECTPROPERTY:
            propertiesTree.setSelectionPath(path);
            propertiesTree.scrollPathToVisible(path);
            modelTab.setSelectedIndex(1);
            break;
         case ElementTypes.INDIVIDUAL:
            individualsTree.setSelectionPath(path);
            individualsTree.scrollPathToVisible(path);
            modelTab.setSelectedIndex(2);
            break;
      }
   }

   public DefaultMutableTreeNode getNode(NamedOwlElement elt) {
      ElementKey key = elt.getKey();
      if (elt instanceof OwlClass) {
         return keyToClassNode.get(key);
      } else if (elt instanceof OwlProperty) {
         return keyToPropertyNode.get(key);
      } else if (elt instanceof OwlIndividual) {
         return keyToIndividualNode.get(key);
      } else {
         return null;
      }
   }

   public void setDiagram(OwlDiagram diagram) {
      this.diagram = diagram;
      this.schema = diagram.getSchema();

      mxGraph graph = diagram.getGraph();
      mxStylesheet stylesheet = graph.getStylesheet();
      diagramPanel.setLayout(new BorderLayout());
      stylesheet.getDefaultVertexStyle().put(mxConstants.STYLE_FONTSIZE, 8);
      graphComp = new mxGraphComponent(graph);
      diagram.setGraphComponent(graphComp);
      graphComp.setPanning(true);
      graphComp.zoom(1.5f);
      graph.getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(-300, -300, 300, 300));
      diagramPanel.add(graphComp, BorderLayout.CENTER);

      ModelTreeRenderer treeRenderer = setupTrees();
      if (schema.hasPackages()) {
         thingPackagesRoot = new DefaultMutableTreeNode("Packages");
         packagesModel = new DefaultTreeModel(thingPackagesRoot);
         packagesTree = new JTree(packagesModel);
         packagesTree.setCellRenderer(treeRenderer);
      }

      computeClassTree();
      computePropertiesTree();
      computeIndividualsTree();

      // expands rows for the trees
      expandTrees();

      // add listeners for the trees
      addTreeListeners();

      this.setRightComponent(diagramPanel);
      if (schema.hasPackages()) {
         JSplitPane classTreePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         classTreePane.setTopComponent(new JScrollPane(classTree));
         classTreePane.setBottomComponent(new JScrollPane(packagesTree));
         classTreePane.setDividerLocation(250);
         modelTab = new JTabbedPane();
         modelTab.add("Classes", classTreePane);
         modelTab.add("Properties", new JScrollPane(propertiesTree));
         modelTab.add("Individuals", new JScrollPane(individualsTree));
         this.setLeftComponent(modelTab);
      } else {
         modelTab = new JTabbedPane();
         modelTab.add("Classes", new JScrollPane(classTree));
         modelTab.add("Properties", new JScrollPane(propertiesTree));
         modelTab.add("Individuals", new JScrollPane(individualsTree));
         this.setLeftComponent(modelTab);
      }
      this.setDividerLocation(250);
   }

   private ModelTreeRenderer setupTrees() {
      propertiesRoot.add(objectPropertiesRoot);
      propertiesRoot.add(dataPropertiesRoot);

      ModelTreeRenderer treeRenderer = new ModelTreeRenderer();
      classTree.setCellRenderer(treeRenderer);
      propertiesTree.setCellRenderer(treeRenderer);
      individualsTree.setCellRenderer(treeRenderer);
      return treeRenderer;
   }

   private void expandTrees() {
      classTree.expandRow(0);
      propertiesTree.expandRow(0);
      individualsTree.expandRow(0);
   }

   /**
    * Select a class in the class tree.
    *
    * @param theKey the class key
    */
   public void selectClass(ElementKey theKey) {
      if (keyToClassNode.containsKey(theKey)) {
         DefaultMutableTreeNode node = keyToClassNode.get(theKey);
         TreePath path = new TreePath(node.getPath());
         highlightElement(ElementTypes.CLASS, path);
         Object o = node.getUserObject();
         if (o instanceof OwlElementRep) {
            selectedElement = (OwlElementRep) o;
            selectElementRep(selectedElement);
         } else {
            selectedElement = null;
         }
      }
   }

   /**
    * Select a property in the properties tree.
    *
    * @param theKey the property key
    */
   public void selectProperty(ElementKey theKey) {
      if (keyToPropertyNode.containsKey(theKey)) {
         DefaultMutableTreeNode node = keyToPropertyNode.get(theKey);
         TreePath path = new TreePath(node.getPath());
         highlightElement(ElementTypes.PROPERTY, path);
         Object o = node.getUserObject();
         if (o instanceof OwlElementRep) {
            selectedElement = (OwlElementRep) o;
            selectElementRep(selectedElement);
         } else {
            selectedElement = null;
         }
      }
   }

   /**
    * Select an individual in the individuals tree.
    *
    * @param theKey the individual key
    */
   public void selectIndividual(ElementKey theKey) {
      if (keyToIndividualNode.containsKey(theKey)) {
         DefaultMutableTreeNode node = keyToIndividualNode.get(theKey);
         TreePath path = new TreePath(node.getPath());
         highlightElement(ElementTypes.INDIVIDUAL, path);
         Object o = node.getUserObject();
         if (o instanceof OwlElementRep) {
            selectedElement = (OwlElementRep) o;
            selectElementRep(selectedElement);
         } else {
            selectedElement = null;
         }

      }
   }

   private void addTreeListeners() {
      addClassTreeListeners();
      if (packagesTree != null) {
         addPackagesTreeListeners();
      }
      addPropertiesTreeListeners();
      addIndividualsTreeListeners();
   }

   private void addClassTreeListeners() {
      classTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof OwlElementRep) {
               selectedElement = (OwlElementRep) o;
               selectElementRep(selectedElement);
            } else {
               selectedElement = null;
            }
         }
      });

      classTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedElement != null && e.getButton() == MouseEvent.BUTTON3) {
               clickOnClassTree(e.getX(), e.getY());
            }
         }
      });
   }

   private void addPackagesTreeListeners() {
      packagesTree.expandRow(0);

      packagesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof OwlElementRep) {
               selectedElement = (OwlElementRep) o;
               selectElementRep(selectedElement);
            } else {
               selectedElement = null;
            }
         }
      });

      packagesTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedElement != null && e.getButton() == MouseEvent.BUTTON3) {
               clickOnPackageTree(e.getX(), e.getY());
            }
         }
      });
   }

   private void addPropertiesTreeListeners() {
      propertiesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof OwlElementRep) {
               selectedElement = (OwlElementRep) o;
               selectElementRep(selectedElement);
            } else {
               selectedElement = null;
            }
         }
      });

      propertiesTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedElement != null && e.getButton() == MouseEvent.BUTTON3) {
               clickOnPropertiesTree(e.getX(), e.getY());
            }
         }
      });
   }

   private void addIndividualsTreeListeners() {
      individualsTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof OwlElementRep) {
               selectedElement = (OwlElementRep) o;
               selectElementRep(selectedElement);
            } else {
               selectedElement = null;
            }
         }
      });

      individualsTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedElement != null && e.getButton() == MouseEvent.BUTTON3) {
               clickOnIndividualsTree(e.getX(), e.getY());
            }
         }
      });
   }

   private void clickOnPackageTree(int x, int y) {
      if (selectedElement.isPackage()) {
         JPopupMenu menu = new JPopupMenu();
         JMenuItem item = new JMenuItem("Export Package");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportModel(true);
            }
         });
         menu.add(item);
         menu.show(packagesTree, x, y);
      }
   }

   private void clickOnPropertiesTree(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Show Domain Classes");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showDomainClasses(x, y);
         }
      });
      menu.add(item);
      NamedOwlElement owlElement = selectedElement.getOwlElement();
      if (owlElement instanceof OwlObjectProperty) {
         item = new JMenuItem("Show Range Classes");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               showRangeClasses(x, y);
            }
         });
         menu.add(item);
      }
      item = new JMenuItem("Copy to Clipboard");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            copyToClipboard();
         }
      });
      menu.add(item);
      menu.show(propertiesTree, x, y);
   }

   private void clickOnIndividualsTree(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Show Classes");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showClasses(x, y);
         }
      });
      menu.add(item);
      item = new JMenuItem("Copy to Clipboard");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            copyToClipboard();
         }
      });
      menu.add(item);
      menu.show(individualsTree, x, y);
   }

   private void clickOnClassTree(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Show Properties");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showProperties(x, y);
         }
      });
      menu.add(item);
      item = new JMenuItem("Show Individuals");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showIndividuals(x, y);
         }
      });
      menu.add(item);
      item = new JMenuItem("Copy to Clipboard");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            copyToClipboard();
         }
      });
      menu.add(item);
      menu.addSeparator();
      if (selectedElement.isPackage()) {
         item = new JMenuItem("Export Package");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportModel(true);
            }
         });
         menu.add(item);
      } else {
         item = new JMenuItem("Export Class");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportModel(false);
            }
         });
         menu.add(item);
      }
      menu.show(classTree, x, y);
   }

   private void copyToClipboard() {
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      NamedOwlElement theElement = selectedElement.getOwlElement();
      StringSelection stringSelection = new StringSelection(theElement.toString());
      clpbrd.setContents(stringSelection, null);
   }

   private void showClasses(int x, int y) {
      OwlIndividual individual = (OwlIndividual) selectedElement.getOwlElement();
      ShowClassesDialog dialog = new ShowClassesDialog(individual, this, browser.getApplicationWindow());
      dialog.setDialogLocation(new Point(x, y));
      browser.showDialog(dialog, MDIDialogType.UNLIMITED);
   }

   private void showDomainClasses(int x, int y) {
      OwlProperty property = (OwlProperty) selectedElement.getOwlElement();
      ShowDomainClassesDialog dialog = new ShowDomainClassesDialog(property, this, browser.getApplicationWindow());
      dialog.setDialogLocation(new Point(x, y));
      browser.showDialog(dialog, MDIDialogType.UNLIMITED);
   }

   private void showRangeClasses(int x, int y) {
      OwlObjectProperty property = (OwlObjectProperty) selectedElement.getOwlElement();
      ShowRangeClassesDialog dialog = new ShowRangeClassesDialog(property, this, browser.getApplicationWindow());
      dialog.setDialogLocation(new Point(x, y));
      browser.showDialog(dialog, MDIDialogType.UNLIMITED);
   }

   private void showIndividuals(int x, int y) {
      OwlClass theClass = (OwlClass) selectedElement.getOwlElement();
      ShowIndividualsDialog dialog = new ShowIndividualsDialog(theClass, this, browser.getApplicationWindow());
      dialog.setDialogLocation(new Point(x, y));
      browser.showDialog(dialog, MDIDialogType.UNLIMITED);
   }

   private void showProperties(int x, int y) {
      OwlClass theClass = (OwlClass) selectedElement.getOwlElement();
      ShowPropertiesDialog dialog = new ShowPropertiesDialog(theClass, this, browser.getApplicationWindow());
      dialog.setDialogLocation(new Point(x, y));
      browser.showDialog(dialog, MDIDialogType.UNLIMITED);
   }

   private void exportModel(boolean isPackage) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Export Package as graphml");
      chooser.setCurrentDirectory(conf.getDefaultDirectory());
      chooser.setFileFilter(conf.graphmlfilter);
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      if (chooser.showOpenDialog(browser.getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         file = FileUtilities.getCompatibleFile(file, "graphml");
         OwlClass theClass = (OwlClass) selectedElement.getOwlElement();
         ExportPackageGraphAction action;
         if (isPackage) {
            action = new ExportPackageGraphAction(browser, "Export Package graph", "Export Package graph", diagram, theClass, file);
         } else {
            action = new ExportPackageGraphAction(browser, "Export Class graph", "Export Class graph", diagram, theClass, file);
         }
         browser.executeAction(action);
         conf.setDefaultDirectory(file.getParentFile());
      }
   }

   private void selectElementRep(OwlElementRep rep) {
      ElementKey key = rep.getOwlElement().getKey();
      mxCell cell = diagram.getCell(key);
      if (cell != null) {
         graphComp.scrollCellToVisible(cell, true);
      }
   }

   private void addToPackagesTree(OwlClass theClass) {
      if (theClass.isPackage() && packagesTree != null) {
         DefaultMutableTreeNode node = createClassNode(theClass, true);
         thingPackagesRoot.add(node);
      }
   }

   private void computeIndividualsTree() {
      schema = diagram.getSchema();
      Iterator<OwlIndividual> it = schema.getIndividuals().values().iterator();
      while (it.hasNext()) {
         OwlIndividual individual = it.next();
         DefaultMutableTreeNode node = createIndividualNode(individual);
         individualsRoot.add(node);
      }
   }

   private void computePropertiesTree() {
      schema = diagram.getSchema();
      Iterator<OwlProperty> it = schema.getOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         DefaultMutableTreeNode node = createPropertyNode(property);
         if (property.isObjectProperty()) {
            objectPropertiesRoot.add(node);
         } else {
            dataPropertiesRoot.add(node);
         }
      }
      TreePath path = new TreePath(objectPropertiesRoot.getPath());
      propertiesTree.expandPath(path);
      path = new TreePath(dataPropertiesRoot.getPath());
      propertiesTree.expandPath(path);
   }

   private void computeClassTree() {
      Map<ElementKey, List<DefaultMutableTreeNode>> nodesMap = new HashMap<>();
      schema = diagram.getSchema();
      thingKey = schema.getThingClass().getKey();
      Iterator<OwlClass> it = schema.getOwlClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         ElementKey key = theClass.getKey();
         if (key.equals(thingKey) || nodesMap.containsKey(key)) {
            continue;
         }
         addToPackagesTree(theClass);
         List<DefaultMutableTreeNode> list = new ArrayList<>();
         nodesMap.put(key, list);

         Map<ElementKey, OwlClass> superClasses = theClass.getSuperClasses();
         if (superClasses.isEmpty()) {
            DefaultMutableTreeNode node = createClassNode(theClass, false);
            list.add(node);
            thingRoot.add(node);
         } else {
            Iterator<ElementKey> it2 = superClasses.keySet().iterator();
            while (it2.hasNext()) {
               ElementKey parentKey = it2.next();
               if (parentKey.equals(thingKey)) {
                  DefaultMutableTreeNode node = createClassNode(theClass, false);
                  list.add(node);
                  thingRoot.add(node);
               } else {
                  OwlClass parentClass = superClasses.get(parentKey);
                  List<DefaultMutableTreeNode> nodes;
                  if (nodesMap.containsKey(parentKey)) {
                     nodes = nodesMap.get(parentKey);
                  } else {
                     nodes = computeClassTree(parentClass, nodesMap);
                  }
                  Iterator<DefaultMutableTreeNode> it3 = nodes.iterator();
                  while (it3.hasNext()) {
                     DefaultMutableTreeNode theNode = it3.next();
                     DefaultMutableTreeNode node = createClassNode(theClass, false);
                     list.add(node);
                     theNode.add(node);
                  }
               }
            }
         }
         nodesMap.put(key, list);
      }
   }

   private List<DefaultMutableTreeNode> computeClassTree(OwlClass theClass, Map<ElementKey, List<DefaultMutableTreeNode>> nodesMap) {
      List<DefaultMutableTreeNode> nodes = new ArrayList<>();
      ElementKey key = theClass.getKey();
      if (nodesMap.containsKey(key)) {
         return nodesMap.get(key);
      }
      addToPackagesTree(theClass);
      Map<ElementKey, OwlClass> superClasses = theClass.getSuperClasses();
      if (superClasses.isEmpty()) {
         DefaultMutableTreeNode node = createClassNode(theClass, false);
         nodes.add(node);
         thingRoot.add(node);
      } else {
         Iterator<ElementKey> it2 = superClasses.keySet().iterator();
         while (it2.hasNext()) {
            ElementKey parentKey = it2.next();
            if (parentKey.equals(thingKey)) {
               DefaultMutableTreeNode node = createClassNode(theClass, false);
               nodes.add(node);
               thingRoot.add(node);
            } else {
               OwlClass parentClass = superClasses.get(parentKey);
               List<DefaultMutableTreeNode> _nodes = computeClassTree(parentClass, nodesMap);
               Iterator<DefaultMutableTreeNode> it3 = _nodes.iterator();
               while (it3.hasNext()) {
                  DefaultMutableTreeNode theNode = it3.next();
                  DefaultMutableTreeNode node = createClassNode(theClass, false);
                  nodes.add(node);
                  theNode.add(node);
               }
            }
         }
      }
      nodesMap.put(key, nodes);
      return nodes;
   }

   private DefaultMutableTreeNode createIndividualNode(OwlIndividual individual) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(createElementRep(individual));
      ElementKey theKey = individual.getKey();
      if (!keyToIndividualNode.containsKey(theKey)) {
         keyToIndividualNode.put(theKey, node);
      }
      return node;
   }

   private DefaultMutableTreeNode createPropertyNode(OwlProperty property) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(createElementRep(property));
      ElementKey theKey = property.getKey();
      if (!keyToPropertyNode.containsKey(theKey)) {
         keyToPropertyNode.put(theKey, node);
      }
      return node;
   }

   private DefaultMutableTreeNode createClassNode(OwlClass theClass, boolean packageTree) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(createElementRep(theClass));
      ElementKey theKey = theClass.getKey();
      if (!packageTree) {
         if (!keyToClassNode.containsKey(theKey)) {
            keyToClassNode.put(theKey, node);
         }
      }
      return node;
   }

   private OwlElementRep createElementRep(NamedOwlElement theElement) {
      return new OwlElementRep(theElement, schema);
   }
}
