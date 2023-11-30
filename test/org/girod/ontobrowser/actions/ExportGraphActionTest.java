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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.net.URL;
import java.util.Map;
import org.girod.jgraphml.model.GraphMLDiagram;
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
public class ExportGraphActionTest {
   private static OntoBrowserGUI browser;
   private static OwlSchema schema;
   private static OwlDiagram diagram;

   public ExportGraphActionTest() {
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
      System.out.println("ExportGraphActionTest : testOpenModel");
      URL url = this.getClass().getResource("testOwl2.owl");

      File file = new File(url.getFile());
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      conf.addThingClass = false;
      OpenModelAction action = new OpenModelAction(browser, null, null, file);
      action.run();

      diagram = action.getDiagram();
      assertNotNull("Diagram must not be null", diagram);
      schema = diagram.getSchema();
      assertNotNull("OwlSchema must not be null", schema);

      Map<ElementKey, OwlClass> classes = schema.getOwlClasses();
      assertNotNull("classes must not be null", classes);
      assertEquals("Must have 10 classes", 10, classes.size());

      assertFalse("Must have no packages", schema.hasPackages());
   }

   /**
    * Test of exporting the graph.
    */
   @Test
   @Order(order = 2)
   public void testExportGraph() throws Exception {
      System.out.println("ExportGraphActionTest : testExportGraph");

      assertNotNull("diagram must not be null", diagram);
      ExportGraphAction action = new ExportGraphAction(browser, null, null, diagram, null);
      action.run();

      GraphMLDiagram graph = action.getGraph();
      assertNotNull("graph must not be null", graph);

      Map<String, IGraphMLNode> nodes = GraphMLUtils.getChildrenFromLabel(graph);
      assertEquals("Must have 11 root level nodes", 11, nodes.size());
      assertTrue("Must have Package1 root level node", nodes.containsKey("Package1"));
      assertTrue("Must have Package2 root level node", nodes.containsKey("Package2"));
      assertTrue("Must have Package3 root level node", nodes.containsKey("Package3"));
      assertTrue("Must have Class1 root level node", nodes.containsKey("Class1"));
      assertTrue("Must have Class2 root level node", nodes.containsKey("Class2"));
      assertTrue("Must have Class3 root level node", nodes.containsKey("Class3"));
      assertTrue("Must have Class4 root level node", nodes.containsKey("Class4"));
      assertTrue("Must have Class5 root level node", nodes.containsKey("Class5"));
      assertTrue("Must have Class6 root level node", nodes.containsKey("Class6"));
      assertTrue("Must have Class7 root level node", nodes.containsKey("Class7"));
      assertTrue("Must have Name root level node", nodes.containsKey("Name"));
   }
}
