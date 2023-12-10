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

import org.girod.ontobrowser.graph.PackagesExtractor;
import static org.junit.Assert.*;
import java.io.File;
import java.net.URL;
import java.util.Map;
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
public class PackagesExtractorTest {
   private static OntoBrowserGUI browser;
   private static OwlSchema schema;
   private static Map<ElementKey, OwlClass> packages = null;

   public PackagesExtractorTest() {
   }

   @BeforeClass
   public static void setUpClass() {
      browser = new OntoBrowserGUI(false);
   }

   @AfterClass
   public static void tearDownClass() {
      browser = null;
      schema = null;
      packages = null;
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
      System.out.println("PackagesExtractorTest : testOpenModel");
      URL url = this.getClass().getResource("testExtractPackageOwl.owl");

      File file = new File(url.getFile());
      OpenModelAction action = new OpenModelAction(browser, null, null, file);
      action.run();

      OwlDiagram diagram = action.getDiagram();
      assertNotNull("Diagram must not be null", diagram);
      schema = diagram.getSchema();
      assertNotNull("OwlSchema must not be null", schema);
   }

   /**
    * Test of extracting the packages.
    */
   @Test
   @Order(order = 2)
   public void testExtractPackages() throws Exception {
      System.out.println("PackagesExtractorTest : testExtractPackages");
      assertNotNull("OwlSchema must not be null", schema);

      PackagesExtractor extractor = new PackagesExtractor(schema);
      packages = extractor.extractPackages();
      assertNotNull("packages must not be null", packages);
   }

   /**
    * Test of extracting the packages.
    */
   @Test
   @Order(order = 3)
   public void testExtractPackagesCheckContent() throws Exception {
      System.out.println("PackagesExtractorTest : testExtractPackagesCheckContent");
      assertNotNull("packages must not be null", packages);

      String namespace = "http://www.semanticweb.org/herve/ontologies/2023/4/untitled-ontology-11#";
      assertEquals("Must have 2 packages", 2, packages.size());
      ElementKey pack1 = new ElementKey(namespace, "Package1");
      assertTrue("Must have Package1 package", packages.containsKey(pack1));
      OwlClass pack1Class = packages.get(pack1);
      Map<ElementKey, OwlClass> subclasses = pack1Class.getSubClasses();
      assertEquals("Must have 3 subclasses", 3, subclasses.size());
      ElementKey theClass = new ElementKey(namespace, "Class6");
      assertTrue("Must have Class6 class", subclasses.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class1");
      assertTrue("Must have Class1 class", subclasses.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class2");
      assertTrue("Must have Class2 class", subclasses.containsKey(theClass));

      ElementKey pack2 = new ElementKey(namespace, "Package2");
      assertTrue("Must have Package2 package", packages.containsKey(pack2));
      OwlClass pack2Class = packages.get(pack2);
      subclasses = pack2Class.getSubClasses();
      assertEquals("Must have 2 subclasses", 2, subclasses.size());
      theClass = new ElementKey(namespace, "Class4");
      assertTrue("Must have Class4 class", subclasses.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class5");
      assertTrue("Must have Class5 class", subclasses.containsKey(theClass));
   }
}
