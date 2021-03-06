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

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.impl.OntModelImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.MenuFactory;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.app.swing.AbstractMDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.SwingFileProperties;
import org.mdiutil.io.FileUtilities;

/**
 * The Action that opens owl/rdf schemas.
 *
 * @since 0.1
 */
public class OpenModelAction extends AbstractMDIAction {
   private File file = null;
   private String name = null;
   private MenuFactory factory = null;
   private OwlSchema schema = null;
   private OwlDiagram diagram = null;
   private SwingFileProperties prop = null;
   private JPanel panel = null;
   private JSplitPane split = null;
   private static final String FONT_FAMILY = "Dialog";
   private static final int FONT_SIZE = 11;

   /**
    * Constructor.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param file the file to open
    */
   public OpenModelAction(MDIApplication app, String desc, String longDesc, File file) {
      super(app, "Open");
      this.file = file;
      this.factory = (MenuFactory) ((AbstractMDIApplication) app).getMenuFactory();
      this.name = FileUtilities.getFileNameBody(file);
      this.setDescription(desc, longDesc);
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
   public OpenModelAction(MDIApplication app, String desc, String longDesc, SwingFileProperties prop, File file) {
      super(app, "Open");
      this.factory = (MenuFactory) ((AbstractMDIApplication) app).getMenuFactory();
      this.file = file;
      this.prop = prop;
      this.name = FileUtilities.getFileNameBody(file);
      this.setDescription(desc, longDesc);
   }

   /**
    * Return the diagram.
    *
    * @return the diagram
    */
   public OwlDiagram getDiagram() {
      return diagram;
   }

   @Override
   public void run() throws Exception {
      URI uri = file.toURI();
      OntModel model = createModel("OWL_MEM");
      FileManager.get().readModel(model, uri.toString());
      // getting a raw model is necessary because if we don't do that, we will use the reasoner when getting the individuals, and if can take a
      // very long time
      // see https://stackoverflow.com/questions/27645110/method-listindividual-takes-more-than-15-mins-with-dbpedia-2014-owl-2mb-siz
      Model _model = model.getRawModel();
      model = new OntModelImpl(OntModelSpec.OWL_MEM, _model);

      GraphExtractor extractor = new GraphExtractor(model);
      schema = extractor.getGraph();
      diagram = new OwlDiagram(file.getName());
      diagram.setFile(file);
      diagram.setSchema(schema);
      mxGraph graph = createGraph(schema);
      diagram.setGraph(graph);

      mxStylesheet stylesheet = graph.getStylesheet();

      panel = new JPanel();
      panel.setLayout(new BorderLayout());
      stylesheet.getDefaultVertexStyle().put(mxConstants.STYLE_FONTSIZE, 8);
      mxGraphComponent graphComp = new mxGraphComponent(graph);
      diagram.setGraphComponent(graphComp);
      graphComp.setPanning(true);
      graphComp.zoom(1.5f);
      graph.getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(-300, -300, 300, 300));
      panel.add(graphComp, BorderLayout.CENTER);
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

   private void createStyles(mxGraph graph) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      mxStylesheet stylesheet = graph.getStylesheet();
      Map<String, Object> styles = stylesheet.getDefaultEdgeStyle();

      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_BLOCK);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FILL_OPACITY, 100);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      stylesheet.putCellStyle("inherit", styles);

      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_DIAMOND);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FILL_OPACITY, 100);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      stylesheet.putCellStyle("parent", styles);

      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FONTSIZE, 11);
      styles.put(mxConstants.STYLE_FILL_OPACITY, 0);
      styles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      styles.put(mxConstants.STYLE_ROUNDED, true);
      styles.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_SIDETOSIDE);
      stylesheet.putCellStyle("property", styles);

      styles = stylesheet.getDefaultVertexStyle();
      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_FILLCOLOR, "#D3D3D3");
      styles.put(mxConstants.STYLE_FONTSIZE, FONT_SIZE);
      stylesheet.putCellStyle("class", styles);

      styles = stylesheet.getDefaultVertexStyle();
      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_FILLCOLOR, "#FF66FF");
      styles.put(mxConstants.STYLE_FONTSIZE, FONT_SIZE);
      stylesheet.putCellStyle("individual", styles);

      styles = stylesheet.getDefaultVertexStyle();
      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_FILLCOLOR, "#32CD32");
      styles.put(mxConstants.STYLE_FONTSIZE, 11);
      stylesheet.putCellStyle("dataProperty", styles);
   }

   private mxGraph createGraph(OwlSchema schema) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      mxGraph graph = new mxGraph();
      graph.setDisconnectOnMove(false);
      graph.setAutoOrigin(true);
      createStyles(graph);

      Object parent = graph.getDefaultParent();
      graph.getModel().beginUpdate();
      Map<ElementKey, OwlClass> owlClasses = schema.getOwlClasses();
      Map<ElementKey, OwlDatatypeProperty> owlDatatypeProperties = schema.getOwlDatatypeProperties();
      Map<ElementKey, mxCell> cell4Class = new HashMap<>();
      Map<ElementKey, mxCell> cell4Dataproperty = new HashMap<>();
      List<mxCell> allCells = new ArrayList<>();

      Iterator<OwlClass> it = owlClasses.values().iterator();
      while (it.hasNext()) {
         OwlClass owlClass = it.next();
         Dimension d = LabelUtils.getDimension(owlClass.getName(), FONT_SIZE, FONT_FAMILY);
         mxCell classCell = (mxCell) graph.insertVertex(parent, null, owlClass.getName(), 0, 100, d.width, d.height);
         classCell.setStyle("class");
         allCells.add(classCell);
         ElementKey key = owlClass.getKey();
         owlClasses.put(key, owlClass);
         cell4Class.put(key, classCell);

         // individuals
         if (owlClass.hasIndividuals()) {
            Iterator<OwlIndividual> it2 = owlClass.getIndividuals().values().iterator();
            while (it2.hasNext()) {
               OwlIndividual individual = it2.next();
               d = LabelUtils.getDimension(individual.getName(), FONT_SIZE, FONT_FAMILY);
               mxCell individualCell = (mxCell) graph.insertVertex(parent, null, individual.getName(), 0, 100, d.width, d.height);
               individualCell.setStyle("individual");
               mxCell edge = (mxCell) graph.insertEdge(parent, null, "", classCell, individualCell);
               edge.setStyle("property");
            }
         }
      }

      // parent classes
      Iterator<ElementKey> it2 = owlClasses.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         OwlClass theClass = owlClasses.get(key);
         mxCell theCell = cell4Class.get(key);
         Iterator<ElementKey> it3 = theClass.getSuperClasses().iterator();
         while (it3.hasNext()) {
            ElementKey parentKey = it3.next();
            if (owlClasses.containsKey(parentKey)) {
               mxCell parentCell = cell4Class.get(parentKey);
               mxCell edge = (mxCell) graph.insertEdge(parent, null, "", theCell, parentCell);
               edge.setStyle("parent");
            }
         }
      }

      // datatype properties
      Iterator<OwlDatatypeProperty> it3 = owlDatatypeProperties.values().iterator();
      while (it3.hasNext()) {
         OwlDatatypeProperty datatypeProperty = it3.next();
         Dimension d = LabelUtils.getDimension(datatypeProperty.getName(), FONT_SIZE, FONT_FAMILY);
         mxCell propertyCell = (mxCell) graph.insertVertex(parent, null, datatypeProperty.getName(), 0, 100, d.width, d.height);
         allCells.add(propertyCell);
         propertyCell.setStyle("dataProperty");
         ElementKey key = datatypeProperty.getKey();
         cell4Dataproperty.put(key, propertyCell);
      }

      Map<EdgeKey, EdgeValue> edges = new HashMap<>();
      // properties
      it2 = owlClasses.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         OwlClass theClass = owlClasses.get(key);
         mxCell theCell = cell4Class.get(key);
         Iterator<OwlProperty> it4 = theClass.getOwlProperties().values().iterator();
         while (it4.hasNext()) {
            OwlProperty property = it4.next();
            if (property instanceof OwlObjectProperty) {
               OwlObjectProperty objectProp = (OwlObjectProperty) property;
               Iterator<ElementKey> it5 = objectProp.getRange().keySet().iterator();
               while (it5.hasNext()) {
                  ElementKey propKey = it5.next();
                  if (owlClasses.containsKey(propKey)) {
                     mxCell rangeCell = cell4Class.get(propKey);
                     mxCell edge = (mxCell) graph.insertEdge(parent, null, objectProp.getName(), theCell, rangeCell);
                     edge.setStyle("property");
                  }
               }
            } else {
               OwlDatatypeProperty datatypeProp = (OwlDatatypeProperty) property;
               ElementKey propKey = datatypeProp.getKey();
               if (cell4Dataproperty.containsKey(propKey)) {
                  mxCell rangeCell = cell4Dataproperty.get(propKey);
                  mxCell edge = (mxCell) graph.insertEdge(parent, null, datatypeProp.getName(), theCell, rangeCell);
                  edge.setStyle("property");
               }

            }
         }
      }

      mxOrganicLayout layout = new mxOrganicLayout(graph);
      layout.setMinMoveRadius(100);
      layout.execute(parent);
      updateEdges(graph, edges);
      updateGraphBounds(graph, allCells);

      graph.getModel().endUpdate();
      return graph;
   }

   private void updateGraphBounds(mxGraph graph, List<mxCell> allCells) {
      double minx = 0;
      double miny = 0;
      double maxx = 100;
      double maxy = 100;
      boolean isFirst = true;
      Iterator<mxCell> it = allCells.iterator();
      while (it.hasNext()) {
         mxCell cell = it.next();
         mxGeometry geometry = cell.getGeometry();
         if (isFirst) {
            minx = geometry.getX();
            miny = geometry.getY();
            maxx = minx + geometry.getWidth();
            maxy = miny + geometry.getHeight();
            isFirst = false;
         } else {
            if (geometry.getX() < minx) {
               minx = geometry.getX();
            }
            if (geometry.getY() < miny) {
               miny = geometry.getY();
            }
            if (geometry.getX() + geometry.getWidth() > maxx) {
               maxx = geometry.getX() + geometry.getWidth();
            }
            if (geometry.getY() + geometry.getHeight() > maxy) {
               maxy = geometry.getY() + geometry.getHeight();
            }
         }
      }

      mxRectangle rect = new mxRectangle(0, 0, maxx - minx, maxy - miny);
      graph.getView().setGraphBounds(rect);
      it = allCells.iterator();
      while (it.hasNext()) {
         mxCell cell = it.next();
         mxGeometry geometry = cell.getGeometry();
         geometry.setX(geometry.getX() - minx);
         geometry.setY(geometry.getY() - miny);
      }

   }

   private void updateEdges(mxGraph graph, Map<EdgeKey, EdgeValue> edges) {
      Iterator<EdgeValue> it = edges.values().iterator();
      while (it.hasNext()) {
         EdgeValue eValue = it.next();
         mxCell edge = eValue.edge;
         mxPoint origP = eValue.origBox.addEdge();
         mxPoint destP = eValue.destBox.addEdge();
         mxGeometry geom = edge.getGeometry();
         List<mxPoint> points = geom.getPoints();
         if (points == null) {
            points = new ArrayList<>();
         }
         points.add(origP);
         points.add(destP);

         geom.setPoints(points);
         graph.getModel().setGeometry(edge, geom);
      }
   }

   private CellEdges getCellEdges(Map<EdgeKey, CellEdges> moduleEdges, EdgeKey key, mxCell origCell, mxCell destCell) {
      CellEdges edges;
      if (moduleEdges.containsKey(key)) {
         edges = moduleEdges.get(key);
      } else {
         edges = new CellEdges(origCell, destCell);
         moduleEdges.put(key, edges);
      }
      return edges;
   }

   @Override
   public void endAction() {
      if (diagram == null) {
         return;
      }
      if (prop == null) {
         AbstractMDIApplication mdi = (AbstractMDIApplication) app;
         if (mdi.hasTab(name)) {
            int i = 1;
            String postName = null;
            while (true) {
               postName = name + "_" + i;
               if (!mdi.hasTab(postName)) {
                  break;
               }
            }
            name = postName;
         }

         prop = new SwingFileProperties(name, panel, diagram);
         ((AbstractMDIApplication) app).addTab(panel, prop);
      }
   }

   @Override
   public String getMessage() {
      return this.getLongDescription() + " opened successfully";

   }

   private static class CellEdges {
      private final mxCell origCell;
      private final mxCell destCell;
      private Rectangle origRec = null;
      private Rectangle destRec = null;
      private boolean onX = false;
      private final List<mxPoint> points = new ArrayList<>();
      private static final double DELTA = 5d;

      private CellEdges(mxCell origCell, mxCell destCell) {
         this.origCell = origCell;
         this.destCell = destCell;
      }

      private void updateCellEdges() {
         if (origRec == null) {
            this.origRec = origCell.getGeometry().getRectangle();
            this.destRec = destCell.getGeometry().getRectangle();
         }
      }

      private mxPoint addEdge() {
         updateCellEdges();
         double x = 0;
         double y = 0;
         if (points.isEmpty()) {
            double deltaX = destRec.getX() - origRec.getX();
            double deltaY = destRec.getY() - origRec.getY();
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
               onX = true;
               if (deltaX > 0) {
                  y = origRec.getY() + origRec.getHeight() / 2f;
                  x = origRec.getX() + origRec.getWidth();
               } else {
                  y = origRec.getY() + origRec.getHeight() / 2f;
                  x = origRec.getX();
               }
            } else {
               onX = false;
               if (deltaY > 0) {
                  y = origRec.getY() + origRec.getHeight();
                  x = origRec.getX() + origRec.getWidth() / 2f;
               } else {
                  y = origRec.getY();
                  x = origRec.getX() + origRec.getWidth() / 2f;
               }
            }
            mxPoint p = new mxPoint(x, y);
            points.add(p);
            return p;
         } else {
            mxPoint previous = points.get(points.size() - 1);
            mxPoint p;
            if (onX) {
               p = new mxPoint(previous.getX() + DELTA, previous.getY());
            } else {
               p = new mxPoint(previous.getX(), previous.getY() + DELTA);
            }
            points.add(p);
            return p;
         }
      }
   }

   private static class EdgeValue {
      private mxCell edge;
      private CellEdges origBox;
      private CellEdges destBox;

      private EdgeValue(mxCell edge, CellEdges origP, CellEdges destP) {
         this.edge = edge;
         this.origBox = origP;
         this.destBox = destP;
      }
   }

   private static class EdgeKey {
      private final ElementKey orig;
      private final ElementKey dest;

      private EdgeKey(ElementKey orig, ElementKey dest) {
         this.orig = orig;
         this.dest = dest;
      }

      @Override
      public int hashCode() {
         int hash = 7;
         hash = 79 * hash + Objects.hashCode(this.orig);
         hash = 79 * hash + Objects.hashCode(this.dest);
         return hash;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         final EdgeKey other = (EdgeKey) obj;
         if (!Objects.equals(this.orig, other.orig)) {
            return false;
         }
         if (!Objects.equals(this.dest, other.dest)) {
            return false;
         }
         return true;
      }
   }
}
