/*
Copyright (c) 2021, 2023 Hervé Girod
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
package org.girod.ontobrowser;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.mxPartitionLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import org.girod.ontobrowser.actions.ExportGraphAction;
import org.girod.ontobrowser.actions.OpenModelAction;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdi.app.swing.AbstractMDIMenuFactory;
import org.mdi.bootstrap.swing.SwingFileProperties;
import org.mdi.gui.swing.AbstractSettingsAction;
import org.mdi.gui.swing.DefaultSettingsAction;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.lang.swing.ResourceUILoader;

/**
 * This class creates the Menus for the application.
 *
 * @version 0.4
 */
public class MenuFactory extends AbstractMDIMenuFactory {
   private final JMenu filemenu = new JMenu("File");
   private final JMenu helpmenu = new JMenu("Help");
   private final JMenu toolsmenu = new JMenu("Tools");
   private final JMenu optionsmenu = new JMenu("Options");
   private AbstractAction aboutAction;
   private OntoBrowser browser = null;
   private final BrowserConfiguration bconf;
   private BrowserSettings settings = null;
   private Icon zoomInIcon = null;
   private Icon zoomOutIcon = null;
   private Icon centerIcon = null;

   /**
    * Constructor.
    *
    * @param browser the Framework Browser
    */
   public MenuFactory(OntoBrowser browser) {
      this.bconf = BrowserConfiguration.getInstance();
      this.browser = browser;
   }

   /**
    * load icons
    */
   private void loadResources() {
      ResourceUILoader loader = new ResourceUILoader("org/girod/ontobrowser/resources");
      zoomInIcon = loader.getIcon("zoomIn.png");
      zoomOutIcon = loader.getIcon("zoomOut.png");
      centerIcon = loader.getIcon("center.png");
   }

   /**
    * construct the application internal menus.
    */
   @Override
   protected void initMenus() {
      loadResources();

      AbstractAction openAction = new AbstractAction("Open Model") {
         public void actionPerformed(ActionEvent ae) {
            openModel();
         }
      };

      AbstractAction exportAction = new AbstractAction("Export as graphml") {
         public void actionPerformed(ActionEvent ae) {
            exportModel();
         }
      };

      AbstractAction zoomInAction = new AbstractAction("Zoom In") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            zoomIn();
         }
      };
      JButton zoomInButton = new JButton(zoomInAction);
      zoomInButton.setText("");
      zoomInButton.setIcon(zoomInIcon);
      zoomInButton.setToolTipText("Zoom In");
      AbstractAction zoomOutAction = new AbstractAction("Zoom out") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            zoomOut();
         }
      };
      JButton zoomOutButton = new JButton(zoomOutAction);
      zoomOutButton.setText("");
      zoomOutButton.setIcon(zoomOutIcon);
      zoomOutButton.setToolTipText("Zoom Out");

      AbstractAction centerAction = new AbstractAction("Center Graph") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            centerGraph();
         }
      };
      JButton centerButton = new JButton(centerAction);
      centerButton.setText("");
      centerButton.setIcon(centerIcon);
      centerButton.setToolTipText("Center Graph");

      JToolBar tbar = new JToolBar("File");
      getToolBarPanel().add(tbar);
      tbar.add(zoomInButton);
      tbar.add(zoomOutButton);
      tbar.add(centerButton);

      JMenu layoutMenu = new JMenu("Layout");
      createLayoutMenu(layoutMenu);
      toolsmenu.add(layoutMenu);

      JMenuItem openItem = new JMenuItem(openAction);
      JMenuItem exportItem = new JMenuItem(exportAction);
      JMenuItem exitItem = new JMenuItem(this.getDefaultExitAction("Exit"));

      settings = BrowserSettings.getInstance();
      settings.initialize();
      ((BrowserSettings) settings).setMenuFactory(this);
      settingsAction = new DefaultSettingsAction(appli, "Settings");

      registerMenus();

      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "OwlToGraph version " + conf.version + "\n"
               + "building date: " + conf.date, "About", JOptionPane.INFORMATION_MESSAGE);
         }
      };

      settingsAction.getSettingsComponent().setPreferredSize(new Dimension(700, 500));
      settingsAction.initialize();
      settingsAction.addNode(null, "General", settings.getGeneralSettings(), null);
      settingsAction.addNode(null, "Style", settings.getStyleSettings(), null);
      settingsAction.addNode(null, "Packages", settings.getPackageSettings(), null);
      optionsmenu.add(new JMenuItem((AbstractSettingsAction) settingsAction));

      // create main menus
      filemenu.add(openItem);
      filemenu.add(exportItem);
      filemenu.add(exitItem);

      // create help menu
      JMenuItem aboutItem = new JMenuItem(aboutAction);
      helpmenu.add(aboutItem);

      // create Menu bar
      Mbar.add(filemenu);
      Mbar.add(optionsmenu);
      Mbar.add(toolsmenu);
      Mbar.add(helpmenu);
   }

   private void centerGraph() {
      OwlDiagram elt = getElement();
      if (elt != null) {
         mxGraphComponent comp = elt.getGraphComponent();
         comp.setCenterPage(true);
         comp.scrollToCenter(true);
      }
   }

   private OwlDiagram getElement() {
      SwingFileProperties prop = ((AbstractMDIApplication) appli).getSelectedProperties();
      if (prop != null) {
         Object o = prop.getObject();
         if (o != null && o instanceof OwlDiagram) {
            return (OwlDiagram) o;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private mxGraph getGraph() {
      OwlDiagram elt = getElement();
      if (elt != null) {
         mxGraph graph = elt.getGraph();
         return graph;
      } else {
         return null;
      }
   }

   private void createLayoutMenu(JMenu layoutMenu) {
      addLayout(layoutMenu, "Organic Layout");
      addLayout(layoutMenu, "Hierarchical Layout");
      addLayout(layoutMenu, "Partition Layout");
      addLayout(layoutMenu, "CompactTree Layout");
      addLayout(layoutMenu, "ParallelEdge Layout");
   }

   private void addLayout(JMenu layoutMenu, String layoutName) {
      JMenuItem item = new JMenuItem(layoutName);
      layoutMenu.add(item);
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            OwlDiagram elt = getElement();
            if (elt != null) {
               mxGraph graph = elt.getGraph();
               graph.setDisconnectOnMove(true);
               graph.getModel().beginUpdate();
               mxIGraphLayout layout = createLayout(layoutName, graph);
               Object parent = graph.getDefaultParent();
               layout.execute(parent);
               graph.getModel().endUpdate();
               graph.setDisconnectOnMove(false);
            }
         }
      });
   }

   private mxIGraphLayout createLayout(String layoutName, mxGraph graph) {
      switch (layoutName) {
         case "Organic Layout":
            return new mxOrganicLayout(graph);
         case "Hierarchical Layout":
            return new mxHierarchicalLayout(graph);
         case "Partition Layout":
            return new mxPartitionLayout(graph);
         case "CompactTree Layout":
            return new mxCompactTreeLayout(graph);
         case "ParallelEdge Layout":
            return new mxParallelEdgeLayout(graph);
         default:
            return null;
      }
   }

   private void zoomIn() {
      OwlDiagram elt = getElement();
      if (elt != null) {
         mxGraphComponent comp = elt.getGraphComponent();
         double zoom = comp.getZoomFactor();
         zoom = zoom * 1.5d;
         comp.zoom(zoom);
      }
   }

   private void zoomOut() {
      OwlDiagram elt = getElement();
      if (elt != null) {
         mxGraphComponent comp = elt.getGraphComponent();
         double zoom = comp.getZoomFactor();
         zoom = zoom / 1.5d;
         comp.zoom(zoom);
      }
   }

   private void openModel() {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Open OWL/RDF");
      chooser.setCurrentDirectory(bconf.getDefaultDirectory());
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      chooser.setFileFilter(conf.owlfilter);
      chooser.setMultiSelectionEnabled(true);
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      if (chooser.showOpenDialog(appli.getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
         File file = chooser.getSelectedFile();
         if (file != null) {
            String longDesc = "Open Model";
            OpenModelAction action = new OpenModelAction(appli, "Open Model", longDesc, file);
            appli.executeAction(action);
            bconf.setDefaultDirectory(file.getParentFile());
         }
      }
   }

   private void exportModel() {
      exportModel(null);
   }

   public void exportModel(OwlDiagram diagram) {
      OwlDiagram elt = getElement();
      if (elt != null) {
         JFileChooser chooser = new JFileChooser();
         chooser.setDialogTitle("Export Model as graphml");
         BrowserConfiguration conf = BrowserConfiguration.getInstance();
         chooser.setCurrentDirectory(bconf.getDefaultDirectory());
         chooser.setFileFilter(conf.graphmlfilter);
         chooser.setDialogType(JFileChooser.SAVE_DIALOG);
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         if (chooser.showOpenDialog(appli.getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            file = FileUtilities.getCompatibleFile(file, "graphml");
            ExportGraphAction action = new ExportGraphAction(appli, "Export Graph", "Export Graph", elt, file);
            appli.executeAction(action);
            bconf.setDefaultDirectory(file.getParentFile());
         }
      }
   }

}
