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
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import org.girod.ontobrowser.actions.ApplyDebugScriptAction;
import org.girod.ontobrowser.actions.ApplyScriptAction;
import org.girod.ontobrowser.actions.ExecuteSPARQLAction;
import org.girod.ontobrowser.actions.ExportGraphAction;
import org.girod.ontobrowser.actions.OpenInYedAction;
import org.girod.ontobrowser.actions.OpenModelAction;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.gui.search.SearchDialog;
import org.girod.ontobrowser.gui.search.SearchOptions;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdi.app.swing.AbstractMDIMenuFactory;
import org.mdi.app.swing.DefaultMDIDialogBuilder;
import org.mdi.app.swing.DefaultMDIDialogBuilder.DialogListener;
import org.mdi.bootstrap.MDIDialogType;
import org.mdi.bootstrap.swing.GUIApplication;
import org.mdi.bootstrap.swing.MDIDialogBuilder;
import org.mdi.bootstrap.swing.SwingFileProperties;
import org.mdi.gui.swing.AbstractSettingsAction;
import org.mdi.gui.swing.DefaultSettingsAction;
import org.mdiutil.io.FileUtilities;
import org.mdiutil.lang.swing.ResourceUILoader;
import org.mdiutil.swing.GenericDialog;

/**
 * This class creates the Menus for the application.
 *
 * @version 0.8
 */
public class MenuFactory extends AbstractMDIMenuFactory {
   private final JMenu filemenu = new JMenu("File");
   private final JMenu helpmenu = new JMenu("Help");
   private final JMenu toolsmenu = new JMenu("Tools");
   private final JMenu viewmenu = new JMenu("View");
   private final JMenu optionsmenu = new JMenu("Options");
   private AbstractAction aboutAction;
   private AbstractAction openInYedAction = null;
   private final SearchDialog searchDialog = new SearchDialog();
   private final SearchDialogListener searchDialogListener = new SearchDialogListener();
   private boolean startSearch = false;
   private final BrowserConfiguration bconf;
   private BrowserSettings settings = null;
   private Icon zoomInIcon = null;
   private Icon zoomOutIcon = null;
   private Icon centerIcon = null;
   private Icon searchIcon = null;

   /**
    * Constructor.
    *
    * @param browser the Framework Browser
    */
   public MenuFactory(OntoBrowserGUI browser) {
      this.bconf = BrowserConfiguration.getInstance();
      this.appli = browser;
      searchDialog.addDialogListener(searchDialogListener);
   }

   /**
    * load icons
    */
   private void loadResources() {
      ResourceUILoader loader = new ResourceUILoader("org/girod/ontobrowser/resources");
      zoomInIcon = loader.getIcon("zoomIn.png");
      zoomOutIcon = loader.getIcon("zoomOut.png");
      centerIcon = loader.getIcon("center.png");
      searchIcon = loader.getIcon("search.gif");
   }

   /**
    * Construct the application internal menus.
    */
   @Override
   protected void initMenus() {
      loadResources();
      BrowserConfiguration conf = BrowserConfiguration.getInstance();

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

      openInYedAction = new AbstractAction("Open in yEd") {
         public void actionPerformed(ActionEvent ae) {
            openInYed();
         }
      };

      AbstractAction searchAction = new AbstractAction("Search", searchIcon) {
         @Override
         public void actionPerformed(ActionEvent e) {
            if (!startSearch) {
               doSearch();
            }
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

      JButton searchButton = new JButton(searchAction);
      searchButton.setText("");
      searchButton.setToolTipText("Search");

      JToolBar tbar = new JToolBar("File");
      getToolBarPanel().add(tbar);
      tbar.add(searchButton);
      tbar.add(zoomInButton);
      tbar.add(zoomOutButton);
      tbar.add(centerButton);

      JMenu layoutMenu = new JMenu("Layout");
      createLayoutMenu(layoutMenu);
      viewmenu.add(layoutMenu);

      AbstractAction sparqlAction = new AbstractAction("Execute SPARQL") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            executeSPARQL();
         }
      };
      toolsmenu.add(sparqlAction);

      AbstractAction scriptAction = new AbstractAction("Execute Script") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            executeScript();
         }
      };
      AbstractAction debugScriptAction = new AbstractAction("Debug Script") {
         @Override
         public void actionPerformed(ActionEvent ae) {
            debugScript();
         }
      };
      toolsmenu.addSeparator();
      toolsmenu.add(scriptAction);
      toolsmenu.add(debugScriptAction);

      JMenuItem openItem = new JMenuItem(openAction);
      JMenuItem exportItem = new JMenuItem(exportAction);
      JMenuItem openInYedItem = new JMenuItem(openInYedAction);

      JMenuItem exitItem = new JMenuItem(this.getDefaultExitAction("Exit"));

      settings = BrowserSettings.getInstance();
      settings.initialize();
      ((BrowserSettings) settings).setMenuFactory(this);
      settingsAction = new DefaultSettingsAction(appli, "Settings");

      registerMenus();

      aboutAction = new AbstractAction("About") {
         public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "OwlToGraph version " + conf.version + "\n"
               + "building date: " + conf.date, "About", JOptionPane.INFORMATION_MESSAGE);
         }
      };

      settingsAction.getSettingsComponent().setPreferredSize(new Dimension(700, 500));
      settingsAction.initialize();
      settingsAction.addNode(null, "General", settings.getGeneralSettings(), null);
      settingsAction.addNode(null, "Schemas", settings.getSchemasSettings(), null);
      settingsAction.addNode(null, "SPARQL", settings.getSPARQLSettings(), null);
      settingsAction.addNode(null, "Scripts", settings.getScriptsSettings(), null);
      settingsAction.addNode(null, "Style", settings.getStyleSettings(), null);
      settingsAction.addNode(null, "Packages", settings.getPackageSettings(), null);
      settingsAction.addNode(null, "yEd", settings.getYedSettings(), null);
      optionsmenu.add(new JMenuItem((AbstractSettingsAction) settingsAction));

      // create main menus
      filemenu.add(openItem);
      filemenu.add(exportItem);
      filemenu.add(openInYedItem);
      filemenu.add(exitItem);

      // create help menu
      JMenuItem aboutItem = new JMenuItem(aboutAction);
      helpmenu.add(aboutItem);

      // create Menu bar
      Mbar.add(filemenu);
      Mbar.add(optionsmenu);
      Mbar.add(toolsmenu);
      Mbar.add(viewmenu);
      Mbar.add(helpmenu);
   }

   @Override
   public void updateMenus() {
      openInYedAction.setEnabled(bconf.hasYedExeDirectory() && bconf.getYedExeDirectory() != null);
   }

   private void executeScript() {
      GraphPanel graphPanel = getGraphPanel();
      if (graphPanel != null) {
         JFileChooser chooser = new JFileChooser();
         chooser.setDialogTitle("Select Groovy Script");
         chooser.setDialogType(JFileChooser.OPEN_DIALOG);
         chooser.setFileFilter(bconf.scriptfilter);
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         chooser.setCurrentDirectory(bconf.getDefaultDirectory());
         if (chooser.showOpenDialog(((GUIApplication) appli).getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
            File scriptFile = chooser.getSelectedFile();
            ApplyScriptAction scriptAction = new ApplyScriptAction(appli, scriptFile, graphPanel);
            appli.executeAction(scriptAction);
         }
      }
   }

   private void debugScript() {
      GraphPanel graphPanel = getGraphPanel();
      if (graphPanel != null) {
         JFileChooser chooser = new JFileChooser();
         chooser.setDialogTitle("Select Groovy Script");
         chooser.setDialogType(JFileChooser.OPEN_DIALOG);
         chooser.setFileFilter(bconf.scriptfilter);
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         chooser.setCurrentDirectory(bconf.getDefaultDirectory());
         if (chooser.showOpenDialog(((GUIApplication) appli).getApplicationWindow()) == JFileChooser.APPROVE_OPTION) {
            File scriptFile = chooser.getSelectedFile();
            ApplyDebugScriptAction scriptAction = new ApplyDebugScriptAction(appli, scriptFile, graphPanel);
            appli.executeAction(scriptAction);
         }
      }
   }

   private void executeSPARQL() {
      OwlDiagram elt = getElement();
      if (elt != null) {
         OwlSchema schema = elt.getSchema();
         DefaultMDIDialogBuilder builder = new DefaultMDIDialogBuilder("SPARQL Window");
         JTextArea area = new JTextArea(40, 30);
         builder.setResizable(true);
         builder.addVerticalDialogPart(area);
         builder.setDialogType(MDIDialogBuilder.YES_CANCEL_DIALOG);
         builder.setYesLabel("Apply");
         builder.setListener(new DialogListener() {
            @Override
            public void apply() {
               applySPARQL(schema, area.getText());
            }
         });
         appli.showDialog(builder, MDIDialogType.UNIQUE_INSTANCE);
      }
   }

   private void applySPARQL(OwlSchema schema, String sparql) {
      ExecuteSPARQLAction sparqlAction = new ExecuteSPARQLAction(appli, schema, sparql);
      appli.executeAction(sparqlAction);
   }

   private void centerGraph() {
      OwlDiagram elt = getElement();
      if (elt != null) {
         mxGraphComponent comp = elt.getGraphComponent();
         comp.setCenterPage(true);
         comp.scrollToCenter(true);
      }
   }

   /**
    * Return the current graph panel.
    *
    * @return the current graph panel
    */
   public GraphPanel getGraphPanel() {
      SwingFileProperties prop = ((AbstractMDIApplication) appli).getSelectedProperties();
      if (prop != null) {
         JComponent comp = prop.getComponent();
         if (comp instanceof GraphPanel) {
            return (GraphPanel) comp;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Return the current Owl model.
    *
    * @return the current Owl model
    */
   public OwlDiagram getElement() {
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

   /**
    * Return the current graph.
    *
    * @return the current graph
    */
   public mxGraph getCurrentGraph() {
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

   /**
    * Start a Search.
    *
    * @param startSearch true to start the Search
    */
   public void startSearch(boolean startSearch) {
      this.startSearch = startSearch;
   }

   /**
    * Perform a Search.
    */
   public void doSearch() {
      GraphPanel graphpanel = (GraphPanel) ((AbstractMDIApplication) appli).getSelectedComponent();
      if (graphpanel != null) {
         appli.showDialog(searchDialog, MDIDialogType.UNLIMITED);
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

   private void openInYed() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      OwlDiagram elt = getElement();
      if (elt != null && conf.hasYedExeDirectory() && conf.getYedExeDirectory() != null) {
         try {
            File tempFile = File.createTempFile("yEd", ".graphml");
            OpenInYedAction action = new OpenInYedAction(appli, "Open Graph in yEd", "Open Graph in yEd", elt, tempFile);
            appli.executeAction(action);
         } catch (IOException ex) {
         }
      }
   }

   /**
    * Export an Owl model.
    *
    * @param diagram the Owl model
    */
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

   private class SearchDialogListener extends GenericDialog.DialogAdapter {
      @Override
      public void apply(GenericDialog gd) {
         SearchOptions options = new SearchOptions();
         options.categories = searchDialog.getAvailableCategories();
         options.category = searchDialog.getSearchCategory();
         options.matchCase = searchDialog.matchCase();
         options.regex = searchDialog.isRegexSearch();
         options.searchString = searchDialog.getSearchString();
         ((OntoBrowserGUI) appli).search(options);
      }
   }
}
