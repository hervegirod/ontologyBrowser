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
package org.girod.ontobrowser.actions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.girod.jgraphml.GraphMLFactory;
import org.girod.jgraphml.model.Arrows;
import org.girod.jgraphml.model.DiagramDefaults;
import org.girod.jgraphml.model.EdgeLabel;
import org.girod.jgraphml.model.Geometry;
import org.girod.jgraphml.model.GraphMLDiagram;
import org.girod.jgraphml.model.GraphMLEdge;
import org.girod.jgraphml.model.GraphMLGroupNode;
import org.girod.jgraphml.model.GroupStateNode;
import org.girod.jgraphml.model.IGraphMLNode;
import org.girod.jgraphml.model.LabelPlacement;
import org.girod.jgraphml.model.LabelStyle;
import org.girod.jgraphml.model.NodeLabel;
import org.girod.jgraphml.model.ShapeType;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.gui.CustomGraphStyles;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.parsers.graph.NodeSorter;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.girod.ontobrowser.parsers.graph.LayoutOptions;

/**
 * An abstract class which exports a doiagram as a GraphML graoh.
 *
 * @version 0.14
 */
public abstract class AbstractExportGraphAction extends AbstractMDIAction {
   protected static final String DEFAULT_NS = "http://www.w3.org/2001/XMLSchema#";
   protected static final char ACCEPT_CONNECTION = 0;
   protected static final char ACCEPT_CONNECTION_ON_PACKAGES = 1;
   protected static final char REFUSE_CONNECTION = 2;
   /**
    * The file to export.
    */
   protected File file = null;
   /**
    * The diagram.
    */
   protected final OwlDiagram diagram;
   /**
    * The schema.
    */
   protected final OwlSchema schema;
   /**
    * The exported graph.
    */
   protected GraphMLDiagram graph;
   protected Map<ElementKey, GraphMLGroupNode> packagesNodes = new HashMap<>();
   protected Map<ElementKey, IGraphMLNode> elementToNode;
   private final Set<NodesConnection> connections = new HashSet<>();
   protected Set<EdgePair> processedEdges = new HashSet<>();
   protected boolean showRelationsConstraints = false;
   protected boolean showDataPropertiesTypes = false;
   protected DiagramDefaults defaults = null;
   protected final CustomGraphStyles customStyles;
   protected final Set<ElementKey> processedPackages = new HashSet<>();
   private final BrowserConfiguration conf = BrowserConfiguration.getInstance();
   protected final boolean hasLayoutOption;
   protected final boolean hasSuperClassLayout;
   protected final boolean hasChildrenLayout;
   protected final boolean showProperties;
   protected final boolean showIndividuals;
   protected final boolean showInterPackageConnections;

   /**
    * Create the export File Action.
    *
    * @param app the Application
    * @param desc the short description of the action
    * @param longDesc the long description of the action
    * @param diagram the diagram
    * @param file the file to open
    */
   public AbstractExportGraphAction(MDIApplication app, String desc, String longDesc, OwlDiagram diagram, File file) {
      super(app, desc);
      this.file = file;
      this.diagram = diagram;
      this.schema = diagram.getSchema();
      this.customStyles = BrowserConfiguration.getInstance().getCustomGraphStyles();
      this.showProperties = conf.showProperties;
      this.showIndividuals = conf.showIndividuals;
      this.showInterPackageConnections = conf.showInterPackageConnections;
      switch (conf.layoutOptions) {
         case LayoutOptions.ANY_POSITION:
            this.hasLayoutOption = false;
            this.hasSuperClassLayout = false;
            this.hasChildrenLayout = false;
            break;
         case LayoutOptions.SUBCLASS_SOUTH_POSITION:
            this.hasLayoutOption = true;
            this.hasSuperClassLayout = true;
            this.hasChildrenLayout = false;
            break;
         case LayoutOptions.CHILDREN_SOUTH_POSITION:
            this.hasLayoutOption = true;
            this.hasSuperClassLayout = true;
            this.hasChildrenLayout = true;
            break;
         default:
            this.hasLayoutOption = false;
            this.hasSuperClassLayout = false;
            this.hasChildrenLayout = false;
            break;
      }
      this.setDescription(desc, desc);
   }

   /**
    * Return the graph.
    *
    * @return the graph
    */
   public GraphMLDiagram getGraph() {
      return graph;
   }

   /**
    * Perform the export.
    */
   public abstract void export();

   /**
    * Return a value indicating if the tentative connection between two nodes is allowed.
    *
    * @param key1 the first class key
    * @param key2 the second class key
    * @return the value
    */
   protected char acceptConnection(ElementKey key1, ElementKey key2) {
      OwlClass class1 = schema.getOwlClass(key1);
      OwlClass class2 = schema.getOwlClass(key2);
      return acceptConnection(class1, class2);
   }

   protected boolean acceptSuperClassConnection(OwlClass class1, OwlClass class2) {
      if (showInterPackageConnections) {
         return true;
      }
      if (class1.isInPackage() || class2.isInPackage()) {
         ElementKey pack1 = class1.getPackage();
         ElementKey pack2 = class2.getPackage();
         if (pack1 != null && pack2 != null && pack1.equals(pack2)) {
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   /**
    * Return a value indicating if the tentative connection between two nodes is allowed.
    *
    * @param class1 the first class
    * @param class2 the second class
    * @return the value
    */
   private char acceptConnection(OwlClass class1, OwlClass class2) {
      if (showInterPackageConnections) {
         return ACCEPT_CONNECTION;
      }
      if (class1.isInPackage() || class2.isInPackage()) {
         ElementKey pack1 = class1.getPackage();
         ElementKey pack2 = class2.getPackage();
         if (pack1 != null && pack2 != null && pack1.equals(pack2)) {
            NodesConnection connection = new NodesConnection(class1, class2);
            boolean accept = !connections.contains(connection);
            if (accept) {
               connections.add(connection);
            }
            return accept ? ACCEPT_CONNECTION : REFUSE_CONNECTION;
         } else if (pack1 != null && pack2 != null) {
            NodesConnection connection = new NodesConnection(pack1, pack2);
            boolean accept = !connections.contains(connection);
            if (accept) {
               connections.add(connection);
            }
            return accept ? ACCEPT_CONNECTION_ON_PACKAGES : REFUSE_CONNECTION;
         } else {
            return REFUSE_CONNECTION;
         }
      } else {
         NodesConnection connection = new NodesConnection(class1, class2);
         boolean accept = !connections.contains(connection);
         if (accept) {
            connections.add(connection);
         }
         return accept ? ACCEPT_CONNECTION : REFUSE_CONNECTION;
      }
   }

   /**
    * Sort the relative position of the nodes.
    */
   protected void sortNodesPosition() {
      if (hasSuperClassLayout) {
         NodeSorter sorter = new NodeSorter(schema, elementToNode);
         sorter.setLayoutOption(conf.layoutOptions);
         if (conf.customGraphStyles != null) {
            if (conf.customGraphStyles.hasLayoutMaximumSteps()) {
               int maximumSteps = conf.customGraphStyles.getLayoutMaximumSteps();
               sorter.setMaximumSteps(maximumSteps);
            }
            if (conf.customGraphStyles.hasLayoutDistance()) {
               float distance = conf.customGraphStyles.getLayoutDistance();
               sorter.setDistance(distance);
            }
         }
         sorter.apply();
      }
   }

   /**
    * Save the diagram.
    *
    * @throws java.io.IOException if the diagram can not be saved
    */
   protected void saveDiagram() throws IOException {
      if (file != null) {
         GraphMLFactory factory = GraphMLFactory.getInstance();
         factory.saveDiagram(graph, file);
      }
   }

   /**
    * Configure the exporter.
    */
   protected void configure() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      this.showRelationsConstraints = conf.showRelationsConstraints;
      this.showDataPropertiesTypes = conf.showDataPropertiesTypes;

      GraphMLFactory factory = GraphMLFactory.getInstance();
      graph = factory.newDiagram();
      defaults = graph.getDefaults();
      defaults.edgeLabelAutoRotate = true;
      defaults.edgeLabelAutoFlip = true;
      defaults.nodeFontSize = 11;
      defaults.edgeFontSize = 11;
      defaults.autosized = true;
      defaults.padWidth = conf.padWidth;
      defaults.padHeight = conf.padHeight;
      defaults.edgeLabelDistance = -4f;
      defaults.edgeLabelPosition = EdgeLabel.ParamModel.POSITION_TAIL;
      defaults.arrowSource = Arrows.NONE;
      defaults.arrowTarget = Arrows.STANDARD;

      packagesNodes = new HashMap<>();
   }

   /**
    * Return the group node for the package associated with a class.
    *
    * @param theClass the class
    * @param key the class key
    * @return the group node
    */
   protected abstract GraphMLGroupNode getPackageNode(OwlClass theClass, ElementKey key);

   protected void setGroupNodeStyle(GraphMLGroupNode node, String name) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      node.setClosedWidth(200);
      node.setClosedHeight(100);
      node.setRealizedStateValue(conf.showPackagesAsClosed);

      GroupStateNode gnode = node.getOpenedStateNode();
      gnode.setType(ShapeType.ROUNDRECTANGLE);
      boolean isShowingInnerGraphDisplay = customStyles.isShowingInnerGraphDisplay();
      gnode.setInnerGraphDisplayEnabled(false);
      gnode.setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.PACKAGE));
      gnode.setInsets(20);
      NodeLabel label = gnode.setLabel(name);
      label.setFontSize(14);
      label.setAlignment(LabelStyle.RIGHT);
      LabelPlacement placementModel = label.createLabelPlacement(LabelPlacement.MODEL_INTERNAL);
      placementModel.setPlacement(LabelPlacement.PLACEMENT_TOP_RIGHT);

      gnode = node.getClosedStateNode();
      gnode.setInnerGraphDisplayEnabled(isShowingInnerGraphDisplay);
      Geometry geom = new Geometry();
      geom.setWidth(150);
      geom.setHeight(80);
      gnode.setGeometry(geom);
      gnode.setType(ShapeType.ROUNDRECTANGLE);
      gnode.setFillColor(customStyles.getBackgroundColor(CustomGraphStyles.PACKAGE));
      if (isShowingInnerGraphDisplay) {
         gnode.setInsets(20);
      } else {
         gnode.setInsets(150);
      }
      label = gnode.setLabel(name);
      label.setFontSize(18);
      placementModel = label.createLabelPlacement(LabelPlacement.MODEL_INTERNAL);
      placementModel.setPlacement(LabelPlacement.PLACEMENT_CENTER);
      label.setAlignment(LabelStyle.CENTER);
   }

   /**
    * Add a cardinality restriction.
    *
    * @param property the property
    * @param edge the edge
    */
   protected void addCardinalityRestriction(OwlProperty property, GraphMLEdge edge) {
      StringBuilder buf = new StringBuilder();
      int minCardinality = property.computeMinCardinality();
      int maxCardinality = property.computeMaxCardinality();
      buf.append(Integer.toString(minCardinality));
      if (minCardinality != maxCardinality) {
         buf.append("...");
         if (maxCardinality == -1) {
            buf.append("n");
         } else {
            buf.append(Integer.toString(maxCardinality));
         }
      }
      EdgeLabel label = edge.createAdditionalLabel(buf.toString(), 0.02f);
      label.setAutoFlip(false);
      label.setAutoRotate(false);
   }

   /**
    * Return the type of a dataProperty as a string
    *
    * @param dataProperty the dataProperty
    * @return the type
    */
   protected String getType(OwlDatatypeProperty dataProperty) {
      Map<ElementKey, OwlDatatype> types = dataProperty.getTypes();
      if (types != null && types.size() == 1) {
         OwlDatatype dataType = types.values().iterator().next();
         String ns = dataType.getNamespace();
         if (ns != null && ns.equals(DEFAULT_NS)) {
            String name = "xs: " + dataType.getDisplayedName();
            return name;
         }
         return null;
      } else {
         return null;
      }
   }

   /**
    * Add a label on an object property
    *
    * @param edge the edge
    * @param property the object property
    */
   protected void addLabelOnEdge(GraphMLEdge edge, OwlObjectProperty property) {
      EdgeLabel label = edge.createLabel(true);
      EdgeLabel.ParamModel model = label.getParameterModel();
      model.setAutoFlip(true);
      model.setAutoRotate(true);
      label.setLabel(property.getDisplayedName());
   }

   @Override
   public String getMessage() {
      return this.getLongDescription() + " exported successfully";
   }
}
