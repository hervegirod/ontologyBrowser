/*
Copyright (c) 2023, Hervé Girod
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.girod.jgraphml.model.GraphMLDiagram;
import org.girod.jgraphml.model.GraphMLEdge;
import org.girod.jgraphml.model.GraphMLGroupNode;
import org.girod.jgraphml.model.GraphMLNode;
import org.girod.jgraphml.model.IGraphMLNode;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.GraphMLUtils;
import org.girod.ontobrowser.OntoBrowserGUI;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlSchema;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;

/**
 *
 * @version 0.5
 */
@RunWith(OrderedRunner.class)
public class ExportGraphActionPackages2Test {
   private static OntoBrowserGUI browser;
   private static OwlSchema schema;
   private static OwlDiagram diagram;
   private static OwlClass package1 = null;
   private static OwlClass package2 = null;

   public ExportGraphActionPackages2Test() {
   }

   @BeforeClass
   public static void setUpClass() {
      browser = new OntoBrowserGUI(false);
   }

   @AfterClass
   public static void tearDownClass() {
      browser = null;
      schema = null;
      diagram = null;
      package1 = null;
      package2 = null;
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }

   /**
    * Test of opening the model.
    */
   @Test
   @Order(order = 1)
   public void testOpenModel() throws Exception {
      System.out.println("ExportGraphActionPackages2Test : testOpenModel");
      URL url = this.getClass().getResource("testOwl3.owl");

      File file = new File(url.getFile());
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      conf.showPackages = true;
      OpenModelAction action = new OpenModelAction(browser, null, null, file);
      action.run();

      diagram = action.getDiagram();
      assertNotNull("Diagram must not be null", diagram);
      schema = diagram.getSchema();
      assertNotNull("OwlSchema must not be null", schema);

      Map<ElementKey, OwlClass> classes = schema.getOwlClasses();
      assertNotNull("classes must not be null", classes);
      assertEquals("Must have 10 classes", 10, classes.size());

      Map<ElementKey, OwlClass> packages = schema.getPackages();
      assertEquals("Must have 3 packages", 3, packages.size());

      String namespace = "http://www.semanticweb.org/herve/ontologies/2023/4/untitled-ontology-11#";
      ElementKey key = new ElementKey(namespace, "Package1");
      package1 = packages.get(key);
      key = new ElementKey(namespace, "Package2");
      package2 = packages.get(key);
   }

   /**
    * Test of exporting the graph.
    */
   @Test
   @Order(order = 2)
   public void testExportGraph() throws Exception {
      System.out.println("ExportGraphActionPackages2Test : testExportGraph");

      assertNotNull("diagram must not be null", diagram);
      ExportGraphAction action = new ExportGraphAction(browser, null, null, diagram, null);
      action.run();

      GraphMLDiagram graph = action.getGraph();
      assertNotNull("graph must not be null", graph);

      Map<String, IGraphMLNode> nodes = GraphMLUtils.getChildrenFromLabel(graph);
      assertEquals("Must have 2 root level packages", 2, nodes.size());
      assertTrue("Must have Package1 root level package", nodes.containsKey("Package1"));

      IGraphMLNode theNode = nodes.get("Package1");
      assertTrue("Package1 must be a group node", theNode instanceof GraphMLGroupNode);
      GraphMLGroupNode group = (GraphMLGroupNode) theNode;
      Map<String, IGraphMLNode> nodes1 = GraphMLUtils.getChildrenFromLabel(group);
      assertEquals("Must have 5 nodes", 5, nodes1.size());

      theNode = nodes.get("Package2");
      assertTrue("Package2 must be a group node", theNode instanceof GraphMLGroupNode);
      group = (GraphMLGroupNode) theNode;
      nodes1 = GraphMLUtils.getChildrenFromLabel(group);
      assertEquals("Must have 3 nodes", 3, nodes1.size());
   }

   /**
    * Test of exporting the graph for one package.
    */
   @Test
   @Order(order = 3)
   public void testExportGraphForPackage() throws Exception {
      System.out.println("ExportGraphActionPackages2Test : testExportGraphForPackage");

      assertNotNull("diagram must not be null", diagram);
      ExportPackageGraphAction action = new ExportPackageGraphAction(browser, null, null, diagram, package1, null);
      action.run();

      GraphMLDiagram graph = action.getGraph();
      assertNotNull("graph must not be null", graph);

      Map<String, IGraphMLNode> nodes = GraphMLUtils.getChildrenFromLabel(graph);
      assertEquals("Must have 6 root level nodes", 6, nodes.size());
      assertTrue("Must have Class6 root level node", nodes.containsKey("Class6"));
      assertTrue("Must have Class2 root level node", nodes.containsKey("Class2"));
      assertTrue("Must have Class3 root level node", nodes.containsKey("Class3"));
      assertTrue("Must have Class1 root level node", nodes.containsKey("Class1"));
      assertTrue("Must have Name root level node", nodes.containsKey("Name"));
      assertTrue("Must have Class4 root level node", nodes.containsKey("Class4\nfrom Package2"));
   }

   /**
    * Test of exporting the graph for one package.
    */
   @Test
   @Order(order = 4)
   public void testExportGraphForPackage2() throws Exception {
      System.out.println("ExportGraphActionPackages2Test : testExportGraphForPackage2");

      assertNotNull("diagram must not be null", diagram);
      ExportPackageGraphAction action = new ExportPackageGraphAction(browser, null, null, diagram, package2, null);
      action.run();

      GraphMLDiagram graph = action.getGraph();
      assertNotNull("graph must not be null", graph);

      Map<String, IGraphMLNode> nodes = GraphMLUtils.getChildrenFromLabel(graph);
      assertEquals("Must have 5 root level nodes", 5, nodes.size());
      assertTrue("Must have Class5 root level node", nodes.containsKey("Class5"));
      assertTrue("Must have Class4 root level node", nodes.containsKey("Class4"));
      assertTrue("Must have Package3 root level node", nodes.containsKey("Package3"));
      assertTrue("Must have Class2 root level node", nodes.containsKey("Class2\nfrom Package1"));
      assertTrue("Must have Class6 root level node", nodes.containsKey("Class6\nfrom Package1"));

      IGraphMLNode theNode = nodes.get("Package3");
      assertTrue("Package3 must be a group node", theNode instanceof GraphMLGroupNode);
      GraphMLGroupNode group = (GraphMLGroupNode) theNode;
      Map<String, IGraphMLNode> nodes1 = GraphMLUtils.getChildrenFromLabel(group);
      assertEquals("Must have 1 node", 1, nodes1.size());
      theNode = nodes1.get("Class7");
      assertTrue("Class7 must be a node", theNode instanceof GraphMLNode);
      Map<String, List<GraphMLEdge>> edges = theNode.getFromEdges();
      assertEquals("Must have 1 edge", 1, edges.size());
   }
}
