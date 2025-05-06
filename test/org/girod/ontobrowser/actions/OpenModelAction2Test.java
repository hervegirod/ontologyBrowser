/*
Copyright (c) 2023, 2025 Hervé Girod
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
import java.util.Map;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.OntoBrowserGUI;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.OwlSchemaProperties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;

/**
 * Test opening a model with packages.
 *
 * @version 0.17.1
 */
@RunWith(OrderedRunner.class)
public class OpenModelAction2Test {
   private static OntoBrowserGUI browser;
   private static OwlSchema schema;

   public OpenModelAction2Test() {
   }

   @BeforeClass
   public static void setUpClass() {
      browser = new OntoBrowserGUI(false);
   }

   @AfterClass
   public static void tearDownClass() {
      browser = null;
      schema = null;
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
      System.out.println("OpenModelAction2Test : testOpenModel");
      URL url = this.getClass().getResource("testOwl2.owl");

      File file = new File(url.getFile());
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      conf.showPackages = true;
      OpenModelAction action = new OpenModelAction(browser, null, null, file);
      action.run();

      OwlDiagram diagram = action.getDiagram();
      assertNotNull("Diagram must not be null", diagram);
      schema = diagram.getSchema();
      assertNotNull("OwlSchema must not be null", schema);

      Map<ElementKey, OwlClass> classes = schema.getOwlClasses();
      assertNotNull("classes must not be null", classes);
      assertEquals("Must have 10 classes", 10, classes.size());
      String namespace = "http://www.semanticweb.org/herve/ontologies/2023/4/untitled-ontology-11#";
      ElementKey pack1 = new ElementKey(namespace, "Package1");
      assertTrue("Must have Class Package1", classes.containsKey(pack1));
      ElementKey pack2 = new ElementKey(namespace, "Package2");
      assertTrue("Must have Class Package2", classes.containsKey(pack2));
      ElementKey pack3 = new ElementKey(namespace, "Package3");
      assertTrue("Must have Class Package3", classes.containsKey(pack3));

      ElementKey theClass = new ElementKey(namespace, "Class1");
      assertTrue("Must have Class Class1", classes.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class2");
      assertTrue("Must have Class Class2", classes.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class3");
      assertTrue("Must have Class Class3", classes.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class4");
      assertTrue("Must have Class Class4", classes.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class5");
      assertTrue("Must have Class Class5", classes.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class6");
      assertTrue("Must have Class Class6", classes.containsKey(theClass));
      theClass = new ElementKey(namespace, "Class7");
      assertTrue("Must have Class Class7", classes.containsKey(theClass));

      Map<ElementKey, OwlClass> packages = schema.getPackages();
      assertEquals("Must have 3 packages", 3, packages.size());
      pack1 = new ElementKey(namespace, "Package1");
      assertTrue("Must have Class Package1", packages.containsKey(pack1));
      pack2 = new ElementKey(namespace, "Package2");
      assertTrue("Must have Class Package2", packages.containsKey(pack2));
      pack3 = new ElementKey(namespace, "Package3");
      assertTrue("Must have Class Package3", packages.containsKey(pack3));
   }
   
   /**
    * Check the schema properties.
    */
   @Test
   @Order(order = 2)
   public void testProperties()  {   
      System.out.println("OpenModelAction2Test : testProperties");
      assertNotNull("OwlSchema must not be null", schema);
      
      boolean isEmpty = schema.getProperty(OwlSchemaProperties.IS_EMPTY);
      assertFalse("OwlSchema must not be empty", isEmpty);
      isEmpty = schema.isEmpty();
      assertFalse("OwlSchema must not be empty", isEmpty);
      
      boolean hasPackages = schema.getProperty(OwlSchemaProperties.HAS_PACKAGES);
      assertTrue("OwlSchema must have packages", hasPackages);
      hasPackages = schema.hasPackages();
      assertTrue("OwlSchema must have packages", hasPackages);
      
      boolean hasForeign = schema.getProperty(OwlSchemaProperties.HAS_FOREIGN_ELEMENTS);
      assertFalse("OwlSchema must not have foreign elements", hasForeign);
      hasForeign = schema.hasForeignElements();
      assertFalse("OwlSchema must not have foreign elements", hasForeign);
      
      boolean hasNonForeign = schema.getProperty(OwlSchemaProperties.HAS_NON_FOREIGN_ELEMENTS);
      assertTrue("OwlSchema must have non foreign elements", hasNonForeign);
      hasNonForeign = schema.hasNonForeignElements();
      assertTrue("OwlSchema must have non foreign elements", hasNonForeign);
      
      boolean hasDefaultNamespace = schema.getProperty(OwlSchemaProperties.HAS_DEFAULT_NAMESPACE);
      assertTrue("OwlSchema must have a default namespace", hasDefaultNamespace);
      hasDefaultNamespace = schema.hasDefaultNamespace();
      assertTrue("OwlSchema must have a default namespace", hasDefaultNamespace); 
      
      boolean hasDefaultPrefix = schema.getProperty(OwlSchemaProperties.HAS_DEFAULT_PREFIX);
      assertFalse("OwlSchema must not have a default prefix", hasDefaultPrefix);
      hasDefaultPrefix = schema.hasDefaultPrefix();
      assertFalse("OwlSchema must not have a default namespace", hasDefaultPrefix);         
   }   
}
