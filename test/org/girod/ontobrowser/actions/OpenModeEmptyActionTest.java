/*
Copyright (c) 2025 Hervé Girod
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

import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.net.URL;
import org.girod.ontobrowser.OntoBrowserGUI;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.OwlSchemaProperties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mdiutil.junit.Order;
import org.mdiutil.junit.OrderedRunner;

/**
 *
 * @since 0.17.1
 */
@RunWith(OrderedRunner.class)
public class OpenModeEmptyActionTest {
   private static OntoBrowserGUI browser;
   private static OwlSchema schema;

   public OpenModeEmptyActionTest() {
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
    * Test of run method, of class OpenModelAction.
    */
   @Test
   @Order(order = 1)
   public void testRun() throws Exception {
      System.out.println("OpenModeEmptyActionTest : testRun");
      URL url = this.getClass().getResource("emptyOwl.rdf");
      File file = new File(url.getFile());
      OpenModelAction action = new OpenModelAction(browser, null, null, file);
      action.run();

      OwlDiagram diagram = action.getDiagram();
      assertNotNull("Diagram must not be null", diagram);
      schema = diagram.getSchema();
      assertNotNull("OwlSchema must not be null", schema);  
   }
   
   /**
    * Check the schema properties.
    */
   @Test
   @Order(order = 2)
   public void testProperties()  {   
      System.out.println("OpenModeEmptyActionTest : testProperties");
      assertNotNull("OwlSchema must not be null", schema);
      
      boolean isEmpty = schema.getProperty(OwlSchemaProperties.IS_EMPTY);
      assertTrue("OwlSchema must be empty", isEmpty);
      isEmpty = schema.isEmpty();
      assertTrue("OwlSchema must be empty", isEmpty);      
   }
}
