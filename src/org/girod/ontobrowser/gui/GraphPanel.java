/*
 Copyright (c) 2023 Dassault Aviation. All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008 Paris,
 France if you need additional information.
 Alternatively if you have any questions about this project, you can visit
 the project website at the project page on https://sourceforge.net/projects/protoframework/
 */
package org.girod.ontobrowser.gui;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.BorderLayout;
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
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.actions.ExportPackageGraphAction;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdiutil.io.FileUtilities;

/**
 * The panel for one ontology graph.
 *
 * @since 0.4
 */
public class GraphPanel extends JSplitPane {
   private final GUIApplication browser;
   // Classes tree
   private final DefaultMutableTreeNode thingRoot = new DefaultMutableTreeNode("Thing");
   private ElementKey thingKey = null;
   private final DefaultTreeModel treeModel = new DefaultTreeModel(thingRoot);
   private final JTree classTree = new JTree(treeModel);
   // Packages tree
   private DefaultMutableTreeNode thingPackagesRoot = null;
   private DefaultTreeModel packagesModel = null;
   private JTree packagesTree = null;

   private OwlDiagram diagram;
   private OwlSchema schema;
   private mxGraphComponent graphComp = null;
   private final JPanel diagramPanel = new JPanel();
   private ClassRep selectedClass = null;

   public GraphPanel(GUIApplication browser) {
      super(JSplitPane.HORIZONTAL_SPLIT);
      this.browser = browser;
   }

   public OwlDiagram getDiagram() {
      return diagram;
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
      ModelTreeRenderer treeRenderer = new ModelTreeRenderer();
      classTree.setCellRenderer(treeRenderer);

      if (schema.hasPackages()) {
         thingPackagesRoot = new DefaultMutableTreeNode("Packages");
         packagesModel = new DefaultTreeModel(thingPackagesRoot);
         packagesTree = new JTree(packagesModel);
         packagesTree.setCellRenderer(treeRenderer);
      }

      computeTree();
      classTree.expandRow(0);
      addTreeListeners();

      this.setRightComponent(diagramPanel);
      if (schema.hasPackages()) {
         JSplitPane treePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
         treePane.setTopComponent(classTree);
         treePane.setBottomComponent(packagesTree);
         treePane.setDividerLocation(250);
         this.setLeftComponent(treePane);
      } else {
         this.setLeftComponent(classTree);
      }
      this.setDividerLocation(150);
   }

   private void addTreeListeners() {
      classTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            Object o = path.getLastPathComponent();
            o = ((DefaultMutableTreeNode) o).getUserObject();
            if (o instanceof ClassRep) {
               selectedClass = (ClassRep) o;
               selectClassRep(selectedClass);
            } else {
               selectedClass = null;
            }
         }
      });

      classTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (selectedClass != null && e.getButton() == MouseEvent.BUTTON3) {
               clickOnTree(e.getX(), e.getY());
            }
         }
      });
      if (packagesTree != null) {
         packagesTree.expandRow(0);

         packagesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
               TreePath path = e.getPath();
               Object o = path.getLastPathComponent();
               o = ((DefaultMutableTreeNode) o).getUserObject();
               if (o instanceof ClassRep) {
                  selectedClass = (ClassRep) o;
                  selectClassRep(selectedClass);
               } else {
                  selectedClass = null;
               }
            }
         });

         packagesTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
               if (selectedClass != null && e.getButton() == MouseEvent.BUTTON3) {
                  clickOnPackageTree(e.getX(), e.getY());
               }
            }
         });
      }
   }

   private void clickOnPackageTree(int x, int y) {
      if (selectedClass.isPackage()) {
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

   private void clickOnTree(int x, int y) {
      if (selectedClass.isPackage()) {
         JPopupMenu menu = new JPopupMenu();
         JMenuItem item = new JMenuItem("Export Package");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportModel(true);
            }
         });
         menu.add(item);
         menu.show(classTree, x, y);
      } else {
         JPopupMenu menu = new JPopupMenu();
         JMenuItem item = new JMenuItem("Export Class");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               exportModel(false);
            }
         });
         menu.add(item);
         menu.show(classTree, x, y);
      }
   }

   private void exportModel(boolean isPackage) {
      BrowserConfiguration bconf = BrowserConfiguration.getInstance();
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Export Package as graphml");
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      chooser.setCurrentDirectory(bconf.getDefaultDirectory());
      chooser.setFileFilter(conf.graphmlfilter);
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      if (chooser.showOpenDialog(browser.getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         file = FileUtilities.getCompatibleFile(file, "graphml");
         ExportPackageGraphAction action;
         if (isPackage) {
            action = new ExportPackageGraphAction(browser, "Export Package graph", "Export Package graph", diagram, selectedClass.getOwlClass(), file);
         } else {
            action = new ExportPackageGraphAction(browser, "Export Class graph", "Export Class graph", diagram, selectedClass.getOwlClass(), file);
         }
         browser.executeAction(action);
         bconf.setDefaultDirectory(file.getParentFile());
      }
   }

   private void selectClassRep(ClassRep rep) {
      ElementKey key = rep.getOwlClass().getKey();
      mxCell cell = diagram.getCell(key);
      if (cell != null) {
         graphComp.scrollCellToVisible(cell, true);
      }
   }

   private void addToPackagesTree(OwlClass theClass) {
      if (theClass.isPackage() && packagesTree != null) {
         DefaultMutableTreeNode node = createClassNode(theClass);
         thingPackagesRoot.add(node);
      }
   }

   private void computeTree() {
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
            DefaultMutableTreeNode node = createClassNode(theClass);
            list.add(node);
            thingRoot.add(node);
         } else {
            Iterator<ElementKey> it2 = superClasses.keySet().iterator();
            while (it2.hasNext()) {
               ElementKey parentKey = it2.next();
               if (parentKey.equals(thingKey)) {
                  DefaultMutableTreeNode node = createClassNode(theClass);
                  list.add(node);
                  thingRoot.add(node);
               } else {
                  OwlClass parentClass = superClasses.get(parentKey);
                  List<DefaultMutableTreeNode> nodes;
                  if (nodesMap.containsKey(parentKey)) {
                     nodes = nodesMap.get(parentKey);
                  } else {
                     nodes = computeTree(parentClass, nodesMap);
                  }
                  Iterator<DefaultMutableTreeNode> it3 = nodes.iterator();
                  while (it3.hasNext()) {
                     DefaultMutableTreeNode theNode = it3.next();
                     DefaultMutableTreeNode node = createClassNode(theClass);
                     list.add(node);
                     theNode.add(node);
                  }
               }
            }
         }
         nodesMap.put(key, list);
      }
   }

   private List<DefaultMutableTreeNode> computeTree(OwlClass theClass, Map<ElementKey, List<DefaultMutableTreeNode>> nodesMap) {
      List<DefaultMutableTreeNode> nodes = new ArrayList<>();
      ElementKey key = theClass.getKey();
      if (nodesMap.containsKey(key)) {
         return nodesMap.get(key);
      }
      addToPackagesTree(theClass);
      Map<ElementKey, OwlClass> superClasses = theClass.getSuperClasses();
      if (superClasses.isEmpty()) {
         DefaultMutableTreeNode node = createClassNode(theClass);
         nodes.add(node);
         thingRoot.add(node);
      } else {
         Iterator<ElementKey> it2 = superClasses.keySet().iterator();
         while (it2.hasNext()) {
            ElementKey parentKey = it2.next();
            if (parentKey.equals(thingKey)) {
               DefaultMutableTreeNode node = createClassNode(theClass);
               nodes.add(node);
               thingRoot.add(node);
            } else {
               OwlClass parentClass = superClasses.get(parentKey);
               List<DefaultMutableTreeNode> _nodes = computeTree(parentClass, nodesMap);
               Iterator<DefaultMutableTreeNode> it3 = _nodes.iterator();
               while (it3.hasNext()) {
                  DefaultMutableTreeNode theNode = it3.next();
                  DefaultMutableTreeNode node = createClassNode(theClass);
                  nodes.add(node);
                  theNode.add(node);
               }
            }
         }
      }
      nodesMap.put(key, nodes);
      return nodes;
   }

   private DefaultMutableTreeNode createClassNode(OwlClass theClass) {
      return new DefaultMutableTreeNode(createClassRep(theClass));
   }

   private ClassRep createClassRep(OwlClass theClass) {
      return new ClassRep(theClass, schema);
   }
}
