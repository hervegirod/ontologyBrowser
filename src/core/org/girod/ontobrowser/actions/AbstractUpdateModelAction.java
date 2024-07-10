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

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.gui.CustomGraphStyles;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.utils.LabelUtils;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdi.bootstrap.swing.SwingFileProperties;

/**
 * The Action that updates owl/rdf schemas.
 *
 * @version 0.15
 */
public abstract class AbstractUpdateModelAction extends AbstractMDIAction {
   protected OwlSchema schema = null;
   protected OwlDiagram diagram = null;
   protected SwingFileProperties prop = null;   
   protected GraphPanel graphPanel = null;
   protected Map<ElementKey, mxCell> cell4Class = null;
   protected Map<ElementKey, mxCell> cell4Property = null;   
   private static final String FONT_FAMILY = "Dialog";
   private static final int FONT_SIZE = 11;   
   private boolean showAlias = false;
   
   /**
    * Constructor.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    */
   public AbstractUpdateModelAction(MDIApplication app, String desc, String longDesc) {
      super(app, desc);
      this.showAlias = BrowserConfiguration.getInstance().showAlias;
      this.setDescription(desc, longDesc);
   }   
   
   /**
    * Constructor.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param prop the file properties
    */
   public AbstractUpdateModelAction(MDIApplication app, String desc, String longDesc, SwingFileProperties prop) {
      super(app, desc);
      this.prop = prop;
   }      
   
   /**
    * Return the diagram.
    *
    * @return the diagram
    */
   public OwlDiagram getDiagram() {
      return diagram;
   }
   
   /**
    * Return the map from class keys to the cell.
    *
    * @return the map
    */
   public Map<ElementKey, mxCell> getKeyToCell() {
      return cell4Class;
   }
   
   private void createStyles(mxGraph graph) {
      CustomGraphStyles customStyles = BrowserConfiguration.getInstance().getCustomGraphStyles();;
      mxStylesheet stylesheet = graph.getStylesheet();
      Map<String, Object> defaultStyles = stylesheet.getDefaultEdgeStyle();

      Map<String, Object> styles = new HashMap<>(defaultStyles);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_BLOCK);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FILL_OPACITY, 100);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      stylesheet.putCellStyle("inherit", styles);

      styles = new HashMap<>(defaultStyles);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_DIAMOND);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FILL_OPACITY, 100);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      stylesheet.putCellStyle("parent", styles);

      styles = new HashMap<>(defaultStyles);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FONTSIZE, 11);
      styles.put(mxConstants.STYLE_FILL_OPACITY, 0);
      styles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      styles.put(mxConstants.STYLE_ROUNDED, true);
      styles.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_SIDETOSIDE);
      stylesheet.putCellStyle("property", styles);
      
      styles = new HashMap<>(defaultStyles);
      styles.put(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_OPEN);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FONTSIZE, 11);
      styles.put(mxConstants.STYLE_FILL_OPACITY, 0);
      styles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      styles.put(mxConstants.STYLE_ROUNDED, true);
      styles.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_SIDETOSIDE);
      stylesheet.putCellStyle("inverseproperty", styles);      

      styles = new HashMap<>(defaultStyles);
      styles.put(mxConstants.STYLE_STARTARROW, mxConstants.NONE);
      styles.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
      styles.put(mxConstants.STYLE_ENDFILL, "white");
      styles.put(mxConstants.STYLE_FONTSIZE, 11);
      styles.put(mxConstants.STYLE_DASHED, true);
      styles.put(mxConstants.STYLE_FILL_OPACITY, 0);
      styles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
      styles.put(mxConstants.STYLE_ENDSIZE, 10);
      styles.put(mxConstants.STYLE_ROUNDED, true);
      styles.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_SIDETOSIDE);
      stylesheet.putCellStyle("alias", styles);

      styles = stylesheet.getDefaultVertexStyle();
      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_FILLCOLOR, customStyles.getBackgroundColorAsString(CustomGraphStyles.CLASS));
      styles.put(mxConstants.STYLE_FONTSIZE, FONT_SIZE);
      stylesheet.putCellStyle("class", styles);

      styles = stylesheet.getDefaultVertexStyle();
      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_FILLCOLOR, customStyles.getBackgroundColorAsString(CustomGraphStyles.INDIVIDUAL));
      styles.put(mxConstants.STYLE_FONTSIZE, FONT_SIZE);
      stylesheet.putCellStyle("individual", styles);

      styles = stylesheet.getDefaultVertexStyle();
      styles = new HashMap<>(styles);
      styles.put(mxConstants.STYLE_FILLCOLOR, customStyles.getBackgroundColorAsString(CustomGraphStyles.PROPERTY));
      styles.put(mxConstants.STYLE_FONTSIZE, 11);
      stylesheet.putCellStyle("dataProperty", styles);
   }   
   
   /**
    * Creates the graph for a schema.
    * @param schema the schema 
    * @return the graph
    */
   protected mxGraph createGraph(OwlSchema schema) {
      mxGraph graph = new mxGraph();
      graph.setDisconnectOnMove(false);
      graph.setAutoOrigin(true);
      createStyles(graph);

      Object parent = graph.getDefaultParent();
      graph.getModel().beginUpdate();
      Map<ElementKey, OwlClass> owlClasses = schema.getOwlClasses();
      Map<ElementKey, OwlDatatypeProperty> owlDatatypeProperties = schema.getOwlDatatypeProperties();
      cell4Class = new HashMap<>();
      cell4Property = new HashMap<>();
      Map<ElementKey, mxCell> cell4Dataproperty = new HashMap<>();
      List<mxCell> allCells = new ArrayList<>();

      Iterator<OwlClass> it = owlClasses.values().iterator();
      while (it.hasNext()) {
         OwlClass owlClass = it.next();
         Dimension d = LabelUtils.getDimension(owlClass.getDisplayedName(), FONT_SIZE, FONT_FAMILY);
         mxCell classCell = (mxCell) graph.insertVertex(parent, null, owlClass.getDisplayedName(), 0, 100, d.width, d.height);
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
               d = LabelUtils.getDimension(individual.getDisplayedName(), FONT_SIZE, FONT_FAMILY);
               mxCell individualCell = (mxCell) graph.insertVertex(parent, null, individual.getDisplayedName(), 0, 100, d.width, d.height);
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
         Iterator<ElementKey> it3 = theClass.getSuperClasses().keySet().iterator();
         while (it3.hasNext()) {
            ElementKey parentKey = it3.next();
            if (owlClasses.containsKey(parentKey)) {
               mxCell parentCell = cell4Class.get(parentKey);
               mxCell edge = (mxCell) graph.insertEdge(parent, null, "", theCell, parentCell);
               edge.setStyle("parent");
            }
         }

      }
      // alias classes
      it2 = owlClasses.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         OwlClass theClass = owlClasses.get(key);
         if (showAlias && theClass.hasAliasClasses()) {
            Iterator<ElementKey> it4 = theClass.getAliasClasses().keySet().iterator();
            while (it4.hasNext()) {
               ElementKey keyAlias = it4.next();
               mxCell theCell = cell4Class.get(key);
               mxCell theAliasCell = cell4Class.get(keyAlias);
               mxCell edge = (mxCell) graph.insertEdge(parent, null, "", theCell, theAliasCell);
               edge.setStyle("alias");
            }
         }
      }

      // datatype properties
      Iterator<OwlDatatypeProperty> it3 = owlDatatypeProperties.values().iterator();
      while (it3.hasNext()) {
         OwlDatatypeProperty datatypeProperty = it3.next();
         Dimension d = LabelUtils.getDimension(datatypeProperty.getDisplayedName(), FONT_SIZE, FONT_FAMILY);
         mxCell propertyCell = (mxCell) graph.insertVertex(parent, null, datatypeProperty.getDisplayedName(), 0, 100, d.width, d.height);
         allCells.add(propertyCell);
         propertyCell.setStyle("dataProperty");
         ElementKey key = datatypeProperty.getKey();
         cell4Dataproperty.put(key, propertyCell);
         cell4Property.put(key, propertyCell);
      }

      Map<EdgeKey, EdgeValue> edges = new HashMap<>();
      Set<ElementKey> inversePropertiesToSkip = new HashSet<>();
      // object properties
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
               if (objectProp.hasInverseProperty()) {
                  ElementKey thePropertyKey = objectProp.getKey();
                  if (inversePropertiesToSkip.contains(thePropertyKey)) {
                     continue;
                  } else {
                     inversePropertiesToSkip.add(objectProp.getInverseProperty().getKey());
                  }
               }
               
               Iterator<ElementKey> it5 = objectProp.getRange().keySet().iterator();
               while (it5.hasNext()) {
                  ElementKey propKey = it5.next();
                  if (owlClasses.containsKey(propKey)) {
                     mxCell rangeCell = cell4Class.get(propKey);
                     String edgelabel = ExportUtils.getDisplayedLabel(objectProp);
                     mxCell edge = (mxCell) graph.insertEdge(parent, null, edgelabel, theCell, rangeCell);
                     ExportUtils.setCellStyle(edge, objectProp);
                  }
               }
            } else {
               OwlDatatypeProperty datatypeProp = (OwlDatatypeProperty) property;
               ElementKey propKey = datatypeProp.getKey();
               if (cell4Dataproperty.containsKey(propKey)) {
                  mxCell rangeCell = cell4Dataproperty.get(propKey);
                  mxCell edge = (mxCell) graph.insertEdge(parent, null, datatypeProp.getDisplayedName(), theCell, rangeCell);
                  edge.setStyle("property");
               }
            }
         }
      }

      // alias properties
      it2 = owlDatatypeProperties.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         OwlDatatypeProperty theProperty = owlDatatypeProperties.get(key);
         if (showAlias && theProperty.hasAliasProperties()) {
            Iterator<ElementKey> it4 = theProperty.getAliasProperties().keySet().iterator();
            while (it4.hasNext()) {
               ElementKey keyAlias = it4.next();
               mxCell theCell = cell4Property.get(key);
               mxCell theAliasCell = cell4Property.get(keyAlias);
               mxCell edge = (mxCell) graph.insertEdge(parent, null, "", theCell, theAliasCell);
               edge.setStyle("alias");
            }
         }
      }

      // parent properties
      it2 = owlDatatypeProperties.keySet().iterator();
      while (it2.hasNext()) {
         ElementKey key = it2.next();
         mxCell theCell = cell4Property.get(key);
         OwlDatatypeProperty property = owlDatatypeProperties.get(key);
         Iterator<ElementKey> it4 = property.getSuperProperties().keySet().iterator();
         while (it4.hasNext()) {
            ElementKey parentKey = it4.next();
            if (owlDatatypeProperties.containsKey(parentKey)) {
               mxCell parentCell = cell4Property.get(parentKey);
               mxCell edge = (mxCell) graph.insertEdge(parent, null, "", theCell, parentCell);
               edge.setStyle("parent");
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
         double x;
         double y;
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
