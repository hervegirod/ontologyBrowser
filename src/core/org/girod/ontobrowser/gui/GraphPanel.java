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
package org.girod.ontobrowser.gui;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.actions.AbstractExportGraphAction;
import org.girod.ontobrowser.actions.ExportClassGraphAction;
import org.girod.ontobrowser.actions.ExportImportGraphAction;
import org.girod.ontobrowser.actions.ExportPackageGraphAction;
import org.girod.ontobrowser.actions.OpenClassInYedAction;
import org.girod.ontobrowser.actions.OpenPackageInYedAction;
import org.girod.ontobrowser.gui.tree.ModelTreeRenderer;
import org.girod.ontobrowser.gui.tree.OntologyTreeRenderer;
import org.girod.ontobrowser.gui.tree.OwlElementRep;
import org.girod.ontobrowser.gui.tree.OwlImportedSchemaRep;
import org.girod.ontobrowser.gui.tree.OwlOntologyRep;
import org.girod.ontobrowser.gui.tree.OwlOntologyTreeRep;
import org.girod.ontobrowser.gui.tree.OwlPrefixRep;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.ElementTypes;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlAnnotation;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.MDIDialogType;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdiutil.io.FileUtilities;

/**
 * The panel for one ontology graph.
 *
 * @version 0.13
 */
public class GraphPanel extends JSplitPane implements GUITabTypes {
   private final GUIApplication browser;
   // Classes tree
   private DefaultMutableTreeNode thingRoot = new DefaultMutableTreeNode("Thing");
   private ElementKey thingKey = null;
   private DefaultTreeModel classTreeModel = new DefaultTreeModel(thingRoot);
   private JTree classTree = new JTree(classTreeModel);
   // Properties tree
   private DefaultMutableTreeNode propertiesRoot = new DefaultMutableTreeNode(PROPERTIES_NAME);
   private DefaultMutableTreeNode dataPropertiesRoot = new DefaultMutableTreeNode(DATA_PROPERTIES_NAME);
   private DefaultMutableTreeNode objectPropertiesRoot = new DefaultMutableTreeNode(OBJECT_PROPERTIES_NAME);
   private DefaultTreeModel propertiesTreeModel = new DefaultTreeModel(propertiesRoot);
   private JTree propertiesTree = new JTree(propertiesTreeModel);
   // Annotations tree
   private DefaultMutableTreeNode annotationsRoot = new DefaultMutableTreeNode(ANNOTATIONS_NAME);
   private DefaultTreeModel annotationsTreeModel = new DefaultTreeModel(annotationsRoot);
   private JTree annotationsTree = new JTree(annotationsTreeModel);
   // Datatypes tree
   private DefaultMutableTreeNode datatypesRoot = new DefaultMutableTreeNode(DATATYPES_NAME);
   private DefaultTreeModel datatypesTreeModel = new DefaultTreeModel(datatypesRoot);
   private JTree datatypesTree = new JTree(datatypesTreeModel);
   // Individuals tree
   private DefaultMutableTreeNode individualsRoot = new DefaultMutableTreeNode(INDIVIDUALS_NAME);
   private DefaultTreeModel individualsTreeModel = new DefaultTreeModel(individualsRoot);
   private JTree individualsTree = new JTree(individualsTreeModel);
   // prefix tree
   private DefaultMutableTreeNode prefixRoot;
   private DefaultTreeModel prefixTreeModel;
   private JTree prefixTree;
   // Packages tree
   private DefaultMutableTreeNode thingPackagesRoot = null;
   private DefaultTreeModel packagesModel = null;
   private JTree packagesTree = null;
   private JTabbedPane modelTab;
   private OwlDiagram diagram;
   private OwlSchema schema;
   private mxGraphComponent graphComp = null;
   private final JSplitPane contentpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
   private final JPanel diagramPanel = new JPanel();
   private final JPanel defaultComponentPanel = new JPanel();
   private ComponentPanelFactory panelFactory = null;
   private OwlElementRep selectedElement = null;
   private OwlOntologyTreeRep selectedPrefix = null;
   private boolean autoTabChange = false;
   private boolean allowBold = false;
   private final Map<ElementKey, DefaultMutableTreeNode> keyToClassNode = new HashMap<>();
   private final Map<ElementKey, DefaultMutableTreeNode> keyToPropertyNode = new HashMap<>();
   private final Map<ElementKey, DefaultMutableTreeNode> keyToAnnotationNode = new HashMap<>();
   private final Map<ElementKey, DefaultMutableTreeNode> keyToDatatypeNode = new HashMap<>();
   private final Map<ElementKey, DefaultMutableTreeNode> keyToIndividualNode = new HashMap<>();

   public GraphPanel(GUIApplication browser) {
      super(JSplitPane.HORIZONTAL_SPLIT);
      this.browser = browser;
      registerToolTips();
   }

   /**
    * Reset the content of the panel.
    */
   public void reset() {
      keyToClassNode.clear();
      keyToPropertyNode.clear();
      keyToAnnotationNode.clear();
      keyToDatatypeNode.clear();
      keyToIndividualNode.clear();

      // Classes tree
      thingRoot = new DefaultMutableTreeNode("Thing");
      classTreeModel = new DefaultTreeModel(thingRoot);
      if (classTree == null) {
         classTree = new JTree(classTreeModel);
      } else {
         classTree.setModel(classTreeModel);
      }
      // Properties tree
      propertiesRoot = new DefaultMutableTreeNode(PROPERTIES_NAME);
      dataPropertiesRoot = new DefaultMutableTreeNode(DATA_PROPERTIES_NAME);
      objectPropertiesRoot = new DefaultMutableTreeNode(OBJECT_PROPERTIES_NAME);
      propertiesTreeModel = new DefaultTreeModel(propertiesRoot);
      if (propertiesTree == null) {
         propertiesTree = new JTree(propertiesTreeModel);
      } else {
         propertiesTree.setModel(propertiesTreeModel);
         propertiesTree.updateUI();
      }
      // Datatypes tree
      datatypesRoot = new DefaultMutableTreeNode(DATATYPES_NAME);
      datatypesTreeModel = new DefaultTreeModel(datatypesRoot);
      if (datatypesTree == null) {
         datatypesTree = new JTree(datatypesTreeModel);
      } else {
         datatypesTree.setModel(datatypesTreeModel);
         datatypesTree.updateUI();
      }
      // annotations tree
      annotationsRoot = new DefaultMutableTreeNode(ANNOTATIONS_NAME);
      annotationsTreeModel = new DefaultTreeModel(annotationsRoot);
      if (annotationsTree == null) {
         annotationsTree = new JTree(annotationsTreeModel);
      } else {
         annotationsTree.setModel(annotationsTreeModel);
         annotationsTree.updateUI();
      }
      // individual tree
      individualsRoot = new DefaultMutableTreeNode(INDIVIDUALS_NAME);
      individualsTreeModel = new DefaultTreeModel(individualsRoot);
      if (individualsTree == null) {
         individualsTree = new JTree(individualsTreeModel);
      } else {
         individualsTree.setModel(individualsTreeModel);
         individualsTree.updateUI();
      }
      individualsTree.setModel(individualsTreeModel);
      registerToolTips();
   }

   public int getSelectedTab() {
      return modelTab.getSelectedIndex();
   }

   public void setSelectedTab(int index) {
      modelTab.setSelectedIndex(index);
   }

   private void registerToolTips() {
      ToolTipManager toolTipmanager = ToolTipManager.sharedInstance();
      toolTipmanager.registerComponent(classTree);
      toolTipmanager.registerComponent(propertiesTree);
      toolTipmanager.registerComponent(individualsTree);
      toolTipmanager.registerComponent(datatypesTree);
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
    * Return the Ontology prefixes tree.
    *
    * @return the Ontology prefixes tree
    */
   public JTree getOntologyPrefixTree() {
      return prefixTree;
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
    * Return the Owl annotations tree.
    *
    * @return the Owl annotations tree
    */
   public JTree getAnnotationsTree() {
      return annotationsTree;
   }

   /**
    * Return the Owl datatypes tree.
    *
    * @return the Owl datatypes tree
    */
   public JTree getDatatypesTree() {
      return datatypesTree;
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
            autoSelectTab(TAB_CLASS_INDEX);
            break;
         case ElementTypes.PROPERTY:
         case ElementTypes.DATAPROPERTY:
         case ElementTypes.OBJECTPROPERTY:
            propertiesTree.setSelectionPath(path);
            propertiesTree.scrollPathToVisible(path);
            autoSelectTab(TAB_PROPERTY_INDEX);
            break;
         case ElementTypes.ANNOTATION:
            annotationsTree.setSelectionPath(path);
            annotationsTree.scrollPathToVisible(path);
            autoSelectTab(TAB_ANNOTATION_INDEX);
         case ElementTypes.DATATYPE:
            datatypesTree.setSelectionPath(path);
            datatypesTree.scrollPathToVisible(path);
            autoSelectTab(TAB_DATATYPE_INDEX);
         case ElementTypes.INDIVIDUAL:
            individualsTree.setSelectionPath(path);
            individualsTree.scrollPathToVisible(path);
            autoSelectTab(TAB_INDIVIDUAL_INDEX);
            break;
      }
   }

   private void autoSelectTab(int index) {
      autoTabChange = true;
      modelTab.setSelectedIndex(index);
      autoTabChange = false;
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
      this.allowBold = BrowserConfiguration.getInstance().showOwnElementsInBold;
      prefixRoot = new DefaultMutableTreeNode(new OwlOntologyRep(schema));
      prefixTreeModel = new DefaultTreeModel(prefixRoot);
      prefixTree = new JTree(prefixTreeModel);
      panelFactory = new ComponentPanelFactory(this, schema);

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
         if (packagesTree == null) {
            packagesTree = new JTree(packagesModel);
            packagesTree.setCellRenderer(treeRenderer);
         } else {
            packagesTree.setModel(packagesModel);
            packagesTree.updateUI();
         }

         ToolTipManager toolTipmanager = ToolTipManager.sharedInstance();
         toolTipmanager.registerComponent(packagesTree);
      }

      computeOntologyPrefixTree();
      computeClassTree();
      computePropertiesTree();
      computeAnnotationsTree();
      computeDatatypesTree();
      computeIndividualsTree();

      // expands rows for the trees
      expandTrees();

      // add listeners for the trees
      addTreeListeners();

      this.setRightComponent(contentpanel);
      contentpanel.setTopComponent(diagramPanel);
      contentpanel.setBottomComponent(defaultComponentPanel);
      contentpanel.setDividerLocation(400);
      if (schema.hasPackages()) {
         JSplitPane classTreePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         classTreePane.setTopComponent(new JScrollPane(classTree));
         classTreePane.setBottomComponent(new JScrollPane(packagesTree));
         classTreePane.setDividerLocation(250);
         modelTab = new JTabbedPane();
         modelTab.add(ONTOLOGY_NAME, new JScrollPane(prefixTree));
         modelTab.add(CLASSES_NAME, classTreePane);
         modelTab.add(PROPERTIES_NAME, new JScrollPane(propertiesTree));
         addIndividualsTab(schema.isIncludingIndividuals());
         modelTab.add(ANNOTATIONS_NAME, new JScrollPane(annotationsTree));
         modelTab.add(DATATYPES_NAME, new JScrollPane(datatypesTree));
         this.setLeftComponent(modelTab);
      } else {
         modelTab = new JTabbedPane();
         modelTab.add(ONTOLOGY_NAME, new JScrollPane(prefixTree));
         modelTab.add(CLASSES_NAME, new JScrollPane(classTree));
         modelTab.add(PROPERTIES_NAME, new JScrollPane(propertiesTree));
         addIndividualsTab(schema.isIncludingIndividuals());
         modelTab.add(ANNOTATIONS_NAME, new JScrollPane(annotationsTree));
         modelTab.add(DATATYPES_NAME, new JScrollPane(datatypesTree));
         this.setLeftComponent(modelTab);
      }
      modelTab.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e) {
            if (!autoTabChange) {
               listenTabChange();
            }
         }
      });
      this.setDividerLocation(350);
   }

   private void addIndividualsTab(boolean includeIndividuals) {
      if (includeIndividuals) {
         modelTab.add(INDIVIDUALS_NAME, new JScrollPane(individualsTree));
      } else {
         JPanel emptyPanel = new JPanel();
         emptyPanel.setBackground(Color.WHITE);
         emptyPanel.setLayout(new BorderLayout());
         JLabel emptyLabel = new JLabel("Individuals Not Included");
         emptyLabel.setHorizontalAlignment(JLabel.CENTER);
         emptyPanel.add(emptyLabel, BorderLayout.CENTER);
         modelTab.add(INDIVIDUALS_NAME, emptyPanel);
      }
   }

   private ModelTreeRenderer setupTrees() {
      propertiesRoot.add(objectPropertiesRoot);
      propertiesRoot.add(dataPropertiesRoot);

      ModelTreeRenderer treeRenderer = new ModelTreeRenderer();
      classTree.setCellRenderer(treeRenderer);
      propertiesTree.setCellRenderer(treeRenderer);
      annotationsTree.setCellRenderer(treeRenderer);
      datatypesTree.setCellRenderer(treeRenderer);
      individualsTree.setCellRenderer(treeRenderer);
      OntologyTreeRenderer prefixTreeRenderer = new OntologyTreeRenderer();
      prefixTree.setCellRenderer(prefixTreeRenderer);
      return treeRenderer;
   }

   private void expandTrees() {
      prefixTree.expandRow(0);
      classTree.expandRow(0);
      propertiesTree.expandRow(0);
      annotationsTree.expandRow(0);
      individualsTree.expandRow(0);
      datatypesTree.expandRow(0);
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
            updateComponentPanel(null);
            selectedElement = null;
         }
         selectedPrefix = null;
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
            updateComponentPanel(null);
            selectedElement = null;
         }
         selectedPrefix = null;
      }
   }

   /**
    * Select an annotation in the annotations tree.
    *
    * @param theKey the annotation key
    */
   public void selectAnnotation(ElementKey theKey) {
      if (keyToAnnotationNode.containsKey(theKey)) {
         DefaultMutableTreeNode node = keyToAnnotationNode.get(theKey);
         TreePath path = new TreePath(node.getPath());
         highlightElement(ElementTypes.ANNOTATION, path);
         Object o = node.getUserObject();
         if (o instanceof OwlElementRep) {
            selectedElement = (OwlElementRep) o;
            selectElementRep(selectedElement);
         } else {
            updateComponentPanel(null);
            selectedElement = null;
         }
         selectedPrefix = null;
      }
   }

   /**
    * Select a datatype in the datatypes tree.
    *
    * @param theKey the datatype key
    */
   public void selectDatatype(ElementKey theKey) {
      if (keyToDatatypeNode.containsKey(theKey)) {
         DefaultMutableTreeNode node = keyToDatatypeNode.get(theKey);
         TreePath path = new TreePath(node.getPath());
         highlightElement(ElementTypes.DATATYPE, path);
         Object o = node.getUserObject();
         if (o instanceof OwlElementRep) {
            selectedElement = (OwlElementRep) o;
            selectElementRep(selectedElement);
         } else {
            updateComponentPanel(null);
            selectedElement = null;
         }
         selectedPrefix = null;
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
            updateComponentPanel(null);
            selectedElement = null;
         }
         selectedPrefix = null;
      }
   }

   private void addTreeListeners() {
      addPrefixTreeListeners();
      addClassTreeListeners();
      if (packagesTree != null) {
         addPackagesTreeListeners();
      }
      addPropertiesTreeListeners();
      addIndividualsTreeListeners();
      addDatatypesTreeListeners();
      addAnnotationsTreeListeners();
   }

   private void listenTabChange() {
      int tabIndex = modelTab.getSelectedIndex();
      switch (tabIndex) {
         case TAB_CLASS_INDEX:
            TreePath path = classTree.getSelectionModel().getSelectionPath();
            setSelectedElementFromPath(path);
            break;
         case TAB_PROPERTY_INDEX:
            path = propertiesTree.getSelectionModel().getSelectionPath();
            setSelectedElementFromPath(path);
            break;
         case TAB_ANNOTATION_INDEX:
            path = annotationsTree.getSelectionModel().getSelectionPath();
            setSelectedElementFromPath(path);
            break;
         case TAB_DATATYPE_INDEX:
            path = datatypesTree.getSelectionModel().getSelectionPath();
            setSelectedElementFromPath(path);
            break;
         case TAB_INDIVIDUAL_INDEX:
            path = individualsTree.getSelectionModel().getSelectionPath();
            setSelectedElementFromPath(path);
            break;
      }
   }

   private void setSelectedElementFromPath(TreePath path) {
      if (path != null) {
         Object o = path.getLastPathComponent();
         o = ((DefaultMutableTreeNode) o).getUserObject();
         if (o instanceof OwlElementRep) {
            selectedElement = (OwlElementRep) o;
         }
      }
   }

   private void addPrefixTreeListeners() {
      prefixTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof OwlOntologyRep) {
               updateOntologyPanel((OwlOntologyRep) o);
               selectedPrefix = (OwlOntologyRep) o;
            } else if (o instanceof OwlPrefixRep) {
               updateComponentPanel(null);
               selectedPrefix = (OwlPrefixRep) o;
            } else if (o instanceof OwlImportedSchemaRep) {
               updateOntologyImport((OwlImportedSchemaRep) o);
               selectedPrefix = (OwlOntologyTreeRep) o;
            } else {
               updateComponentPanel(null);
               selectedPrefix = null;
            }
            selectedElement = null;
         }
      });

      prefixTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
               if (prefixTree.getSelectionModel().getLeadSelectionRow() == 0) {
                  clickOnPrefixTreeRoot(e.getX(), e.getY());
               } else if (selectedPrefix != null) {
                  clickOnPrefixTree(e.getX(), e.getY());
               }
            }
         }
      }
      );
   }

   private List<OwlElementRep> getSelectedElements(TreePath[] paths) {
      List<OwlElementRep> selectedElements = new ArrayList<>();
      for (int i = 0; i < paths.length; i++) {
         TreePath path = paths[i];
         Object o = path.getLastPathComponent();
         o = ((DefaultMutableTreeNode) o).getUserObject();
         if (o instanceof OwlElementRep) {
            OwlElementRep elementRep = (OwlElementRep) o;
            selectedElements.add(elementRep);
         }
      }
      return selectedElements;
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
               updateComponentPanel(null);
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
               updateComponentPanel(null);
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
               updateComponentPanel(null);
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

   private void addDatatypesTreeListeners() {
      datatypesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof OwlElementRep) {
               selectedElement = (OwlElementRep) o;
               updateComponentPanel(null);
            } else {
               updateComponentPanel(null);
               selectedElement = null;
            }
         }
      });

      datatypesTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedElement != null && e.getButton() == MouseEvent.BUTTON3) {
               clickOnDatatypesTree(e.getX(), e.getY());
            }
         }
      });
   }

   private void addAnnotationsTreeListeners() {
      annotationsTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof OwlElementRep) {
               selectedElement = (OwlElementRep) o;
               updateComponentPanel(selectedElement);
            } else {
               updateComponentPanel(null);
               selectedElement = null;
            }
         }
      });

      annotationsTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedElement != null && e.getButton() == MouseEvent.BUTTON3) {
               clickOnAnnotationsTree(e.getX(), e.getY());
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
               updateComponentPanel(null);
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

   private void clickOnPrefixTreeRoot(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Show Ontology Imports graph in yEd");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            exportImportGraphInYed();
         }
      });
      menu.add(item);
      menu.show(prefixTree, x, y);
   }

   private void clickOnPrefixTree(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Copy to Clipboard");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            copyToClipboard();
         }
      });
      menu.add(item);
      menu.show(prefixTree, x, y);
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
      JMenuItem item = new JMenuItem("Show Dependencies");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showDependencies(x, y);
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
      menu.show(propertiesTree, x, y);
   }

   private void clickOnDatatypesTree(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Copy to Clipboard");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            copyToClipboard();
         }
      });
      menu.add(item);
      menu.show(datatypesTree, x, y);
   }

   private void clickOnAnnotationsTree(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Show Dependencies");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showDependencies(x, y);
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
      menu.show(annotationsTree, x, y);
   }

   private void clickOnIndividualsTree(int x, int y) {
      JPopupMenu menu = new JPopupMenu();
      JMenuItem item = new JMenuItem("Show Dependencies");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showDependencies(x, y);
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
      JMenuItem item = new JMenuItem("Show Dependencies");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            showDependencies(x, y);
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
         item = new JMenuItem("Show Package in yEd");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportModelInYed(true);
            }
         });
         menu.add(item);
         BrowserConfiguration conf = BrowserConfiguration.getInstance();
         item.setEnabled(conf.hasYedExeDirectory() && conf.getYedExeDirectory() != null);
      } else {
         item = new JMenuItem("Export Class");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportModel(false);
            }
         });
         menu.add(item);
         menu.add(item);
         item = new JMenuItem("Show Class in yEd");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportClassInYed();
            }
         });
         menu.add(item);
         BrowserConfiguration conf = BrowserConfiguration.getInstance();
         item.setEnabled(conf.hasYedExeDirectory() && conf.getYedExeDirectory() != null);
      }
      menu.show(classTree, x, y);
   }

   private void updateOntologyPanel(OwlOntologyRep element) {
      int location = contentpanel.getDividerLocation();
      JComponent panel = panelFactory.getComponentPanel(element);
      contentpanel.setBottomComponent(panel);
      contentpanel.setDividerLocation(location);
   }

   private void updateOntologyImport(OwlImportedSchemaRep element) {
      int location = contentpanel.getDividerLocation();
      JComponent panel = panelFactory.getComponentPanel(element);
      contentpanel.setBottomComponent(panel);
      contentpanel.setDividerLocation(location);
   }

   private void updateComponentPanel(OwlElementRep selectedElement) {
      int location = contentpanel.getDividerLocation();
      JComponent panel = panelFactory.getComponentPanel(selectedElement);
      contentpanel.setBottomComponent(panel);
      contentpanel.setDividerLocation(location);
   }

   private void copyToClipboard() {
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      if (selectedElement != null) {
         NamedOwlElement theElement = selectedElement.getOwlElement();
         StringSelection stringSelection = new StringSelection(theElement.toString());
         clpbrd.setContents(stringSelection, null);
      } else {
         String namespace = selectedPrefix.getNamespace();
         StringSelection stringSelection = new StringSelection(namespace);
         clpbrd.setContents(stringSelection, null);
      }
   }

   private void showDependencies(int x, int y) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      NamedOwlElement element = (NamedOwlElement) selectedElement.getOwlElement();
      ShowDependenciesDialog dialog = new ShowDependenciesDialog(element, this, browser.getApplicationWindow(), conf.autoRefresh);
      browser.showDialog(dialog, MDIDialogType.UNLIMITED);
   }

   private List<OwlClass> getSelectedClasses(TreePath[] paths) {
      List<OwlElementRep> selectedElements = getSelectedElements(paths);
      List<OwlClass> theClasses = new ArrayList<>();
      Iterator<OwlElementRep> it = selectedElements.iterator();
      while (it.hasNext()) {
         OwlElementRep rep = it.next();
         NamedOwlElement element = rep.getOwlElement();
         if (element instanceof OwlClass) {
            theClasses.add((OwlClass) element);
         }
      }
      return theClasses;
   }

   private void exportClassInYed() {
      TreePath[] paths = classTree.getSelectionModel().getSelectionPaths();
      if (paths == null || paths.length < 2) {
         OwlClass theClass = (OwlClass) selectedElement.getOwlElement();
         try {
            File tempFile = File.createTempFile("yEd", ".graphml");
            ExportClassGraphAction action = new OpenClassInYedAction(browser, "Show Class graph", "Show Class graph", diagram, theClass, tempFile);
            browser.executeAction(action);
         } catch (IOException ex) {
         }
      } else {
         List<OwlClass> theClasses = getSelectedClasses(paths);
         try {
            File tempFile = File.createTempFile("yEd", ".graphml");
            ExportClassGraphAction action = new OpenClassInYedAction(browser, "Show Class graph", "Show Class graph", diagram, theClasses, tempFile);
            browser.executeAction(action);
         } catch (IOException ex) {
         }
      }
   }

   private void exportModelInYed(boolean isPackage) {
      ExportPackageGraphAction action;
      OwlClass theClass = (OwlClass) selectedElement.getOwlElement();
      try {
         File tempFile = File.createTempFile("yEd", ".graphml");
         if (isPackage) {
            action = new OpenPackageInYedAction(browser, "Show Package graph", "Show Package graph", diagram, theClass, tempFile);
         } else {
            action = new OpenPackageInYedAction(browser, "Show Class graph", "Show Class graph", diagram, theClass, tempFile);
         }
         browser.executeAction(action);
      } catch (IOException ex) {
      }
   }

   private void exportImportGraphInYed() {
      OwlOntologyRep ontologyRep = (OwlOntologyRep) selectedPrefix;
      try {
         File tempFile = File.createTempFile("yEd", ".graphml");
         ExportImportGraphAction action = new ExportImportGraphAction(browser, "Show Import graph", "Show Import graph", ontologyRep.schema, tempFile);
         browser.executeAction(action);
      } catch (IOException ex) {
      }
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
         AbstractExportGraphAction action;
         if (isPackage) {
            action = new ExportPackageGraphAction(browser, "Export Package graph", "Export Package graph", diagram, theClass, file);
         } else {
            TreePath[] paths = classTree.getSelectionModel().getSelectionPaths();
            if (paths == null || paths.length < 2) {
               action = new ExportClassGraphAction(browser, "Export Class graph", "Export Class graph", diagram, theClass, file);
            } else {
               List<OwlClass> theClasses = getSelectedClasses(paths);
               action = new ExportClassGraphAction(browser, "Export Class graph", "Export Class graph", diagram, theClasses, file);
            }
         }
         browser.executeAction(action);
         conf.setDefaultDirectory(file.getParentFile());
      }
   }

   public void selectElement(NamedOwlElement element) {
      ElementKey key = element.getKey();
      if (keyToClassNode.containsKey(key)) {
         this.selectClass(key);
      } else if (keyToPropertyNode.containsKey(key)) {
         this.selectProperty(key);
      } else if (keyToIndividualNode.containsKey(key)) {
         this.selectIndividual(key);
      } else if (keyToAnnotationNode.containsKey(key)) {
         this.selectAnnotation(key);
      }
   }

   private void selectElementRep(OwlElementRep rep) {
      ElementKey key = rep.getOwlElement().getKey();
      updateComponentPanel(rep);
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
      SortedMap<ElementKey, OwlIndividual> sortedMap = new TreeMap<>();
      // first created a sorted map for the individuals keys
      Iterator<OwlIndividual> it = schema.getIndividuals().values().iterator();
      while (it.hasNext()) {
         OwlIndividual individual = it.next();
         sortedMap.put(individual.getKey(), individual);
      }
      // now create the nodes
      it = sortedMap.values().iterator();
      while (it.hasNext()) {
         OwlIndividual individual = it.next();
         DefaultMutableTreeNode node = createIndividualNode(individual);
         individualsRoot.add(node);
      }
   }

   private void computeAnnotationsTree() {
      schema = diagram.getSchema();
      SortedMap<ElementKey, OwlAnnotation> sortedMap = new TreeMap<>();
      // first created a sorted map for the annotations keys
      Iterator<OwlAnnotation> it = schema.getElementAnnotations().values().iterator();
      while (it.hasNext()) {
         OwlAnnotation annotation = it.next();
         sortedMap.put(annotation.getKey(), annotation);
      }
      // now create the nodes
      it = sortedMap.values().iterator();
      while (it.hasNext()) {
         OwlAnnotation annotation = it.next();
         DefaultMutableTreeNode node = createAnnotationNode(annotation);
         annotationsRoot.add(node);
      }
   }

   private void computeDatatypesTree() {
      schema = diagram.getSchema();
      SortedMap<ElementKey, OwlDatatype> sortedMap = new TreeMap<>();
      // first created a sorted map for the datatypes keys
      Iterator<OwlDatatype> it = schema.getDatatypes().values().iterator();
      while (it.hasNext()) {
         OwlDatatype datatype = it.next();
         sortedMap.put(datatype.getKey(), datatype);
      }
      // now create the nodes
      it = sortedMap.values().iterator();
      while (it.hasNext()) {
         OwlDatatype datatype = it.next();
         DefaultMutableTreeNode node = createDatatypeNode(datatype);
         datatypesRoot.add(node);
      }
   }

   private void computePropertiesTree() {
      Map<ElementKey, List<DefaultMutableTreeNode>> nodesMap = new HashMap<>();
      schema = diagram.getSchema();
      SortedMap<ElementKey, OwlProperty> sortedMap = new TreeMap<>();
      // first created a sorted map for the properties keys
      Iterator<OwlProperty> it = schema.getOwlProperties().values().iterator();
      while (it.hasNext()) {
         OwlProperty property = it.next();
         sortedMap.put(property.getKey(), property);
      }
      // now create the nodes
      it = sortedMap.values().iterator();
      while (it.hasNext()) {
         OwlProperty theProperty = it.next();
         ElementKey key = theProperty.getKey();
         if (!nodesMap.containsKey(key)) {
            List<DefaultMutableTreeNode> list = new ArrayList<>();
            nodesMap.put(key, list);
            Map<ElementKey, OwlProperty> superProperties = theProperty.getSuperProperties();
            if (superProperties.isEmpty()) {
               DefaultMutableTreeNode node = createPropertyNode(theProperty);
               if (theProperty.isObjectProperty()) {
                  objectPropertiesRoot.add(node);
               } else {
                  dataPropertiesRoot.add(node);
               }
               list.add(node);
            } else {
               Iterator<ElementKey> it2 = superProperties.keySet().iterator();
               while (it2.hasNext()) {
                  ElementKey parentKey = it2.next();
                  OwlProperty parentProperty = superProperties.get(parentKey);
                  List<DefaultMutableTreeNode> nodes;
                  if (nodesMap.containsKey(parentKey)) {
                     nodes = nodesMap.get(parentKey);
                  } else {
                     nodes = computePropertyTree(parentProperty, nodesMap);
                  }
                  Iterator<DefaultMutableTreeNode> it3 = nodes.iterator();
                  while (it3.hasNext()) {
                     DefaultMutableTreeNode theNode = it3.next();
                     DefaultMutableTreeNode node = createPropertyNode(theProperty);
                     theNode.add(node);
                     list.add(node);
                  }
               }
            }
         }
      }
      TreePath path = new TreePath(objectPropertiesRoot.getPath());
      propertiesTree.expandPath(path);
      path = new TreePath(dataPropertiesRoot.getPath());
      propertiesTree.expandPath(path);
   }

   private List<DefaultMutableTreeNode> computePropertyTree(OwlProperty theProperty, Map<ElementKey, List<DefaultMutableTreeNode>> nodesMap) {
      List<DefaultMutableTreeNode> nodes = new ArrayList<>();
      ElementKey key = theProperty.getKey();
      if (nodesMap.containsKey(key)) {
         return nodesMap.get(key);
      }
      Map<ElementKey, OwlProperty> superProperties = theProperty.getSuperProperties();
      if (superProperties.isEmpty()) {
         DefaultMutableTreeNode node = createPropertyNode(theProperty);
         nodes.add(node);
         if (theProperty.isObjectProperty()) {
            objectPropertiesRoot.add(node);
         } else {
            dataPropertiesRoot.add(node);
         }
      } else {
         Iterator<ElementKey> it2 = superProperties.keySet().iterator();
         while (it2.hasNext()) {
            ElementKey parentKey = it2.next();
            OwlProperty parentProperty = superProperties.get(parentKey);
            List<DefaultMutableTreeNode> _nodes = computePropertyTree(parentProperty, nodesMap);
            Iterator<DefaultMutableTreeNode> it3 = _nodes.iterator();
            while (it3.hasNext()) {
               DefaultMutableTreeNode theNode = it3.next();
               DefaultMutableTreeNode node = createPropertyNode(theProperty);
               nodes.add(node);
               theNode.add(node);
            }
         }
      }
      nodesMap.put(key, nodes);
      return nodes;
   }

   private void computeOntologyPrefixTree() {
      OntologyTreeHelper.computeOntologyPrefixTree(diagram, prefixTree, prefixRoot);
   }

   /**
    * Update the tree selection model.
    */
   public void updateTreeSelectionMode() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      if (conf.multiSelection) {
         classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      } else {
         classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      }
   }

   private void computeClassTree() {
      updateTreeSelectionMode();

      Map<ElementKey, List<DefaultMutableTreeNode>> nodesMap = new HashMap<>();
      SortedMap<ElementKey, OwlClass> sortedMap = new TreeMap<>();
      schema = diagram.getSchema();
      thingKey = schema.getThingClass().getKey();
      // first created a sorted map for the classes keys
      Iterator<OwlClass> it = schema.getOwlClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass theClass = it.next();
         ElementKey key = theClass.getKey();
         if (key.equals(thingKey) || nodesMap.containsKey(key)) {
            continue;
         }
         sortedMap.put(key, theClass);
      }

      // now create the nodes
      it = sortedMap.values().iterator();
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
         if (!nodesMap.containsKey(key)) {
            nodesMap.put(key, list);
            addToPackagesTree(theClass);
         }
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

   private DefaultMutableTreeNode createAnnotationNode(OwlAnnotation annotation) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(createElementRep(annotation));
      ElementKey theKey = annotation.getKey();
      if (!keyToAnnotationNode.containsKey(theKey)) {
         keyToAnnotationNode.put(theKey, node);
      }
      return node;
   }

   private DefaultMutableTreeNode createDatatypeNode(OwlDatatype datatype) {
      DefaultMutableTreeNode node = new DefaultMutableTreeNode(createElementRep(datatype));
      ElementKey theKey = datatype.getKey();
      if (!keyToDatatypeNode.containsKey(theKey)) {
         keyToDatatypeNode.put(theKey, node);
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
      return new OwlElementRep(theElement, schema, allowBold);
   }
}
