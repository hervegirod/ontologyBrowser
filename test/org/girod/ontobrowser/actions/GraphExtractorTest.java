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
import org.girod.ontobrowser.OntoBrowserGUI;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
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
 * @since 0.5
 */
@RunWith(OrderedRunner.class)
public class GraphExtractorTest {
   private static OntoBrowserGUI browser;
   private static OwlSchema schema;
   private static String namespace;
   private static ElementKey class1Key;
   private static ElementKey class2Key;
   private static ElementKey class3Key;
   private static ElementKey class3ParentKey;

   public GraphExtractorTest() {
   }

   @BeforeClass
   public static void setUpClass() {
      browser = new OntoBrowserGUI(false);
      namespace = "http://www.semanticweb.org/herve/ontologies/2023/9/untitled-ontology-11#";
      class1Key = new ElementKey(namespace, "Class1");
      class2Key = new ElementKey(namespace, "Class2");
      class3Key = new ElementKey(namespace, "Class3");
      class3ParentKey = new ElementKey(namespace, "Class3Parent");
   }

   @AfterClass
   public static void tearDownClass() {
      browser = null;
      schema = null;
      namespace = null;
      class1Key = null;
      class2Key = null;
      class3Key = null;
      class3ParentKey = null;
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
      System.out.println("GraphExtractorTest : testOpenModel");
      URL url = this.getClass().getResource("testOwl4.rdf");

      File file = new File(url.getFile());
      OpenModelAction action = new OpenModelAction(browser, null, null, file);
      action.run();

      OwlDiagram diagram = action.getDiagram();
      assertNotNull("Diagram must not be null", diagram);
      schema = diagram.getSchema();
      assertNotNull("OwlSchema must not be null", schema);
   }

   /**
    * Test of checking the content of the graph for the Classes.
    */
   @Test
   @Order(order = 2)
   public void testExtractGraphClasses() throws Exception {
      System.out.println("GraphExtractorTest : testExtractGraphClasses");
      assertNotNull("OwlSchema must not be null", schema);

      assertTrue("Must have Class1", schema.hasOwlClass(class1Key));
      assertTrue("Must have Class2", schema.hasOwlClass(class2Key));
      assertTrue("Must have Class3", schema.hasOwlClass(class3Key));
      assertTrue("Must have Class3Parent", schema.hasOwlClass(class3ParentKey));

      OwlClass class1 = schema.getOwlClass(class1Key);
      assertFalse("Class1 must not have a defined SuperClass", class1.hasDefinedSuperClass());
      OwlClass class2 = schema.getOwlClass(class2Key);
      assertFalse("Class2 must not have a defined SuperClass", class2.hasDefinedSuperClass());
      OwlClass class3 = schema.getOwlClass(class3Key);
      assertTrue("Class3 must have a defined SuperClass", class3.hasDefinedSuperClass());

      OwlClass class3SuperClass = schema.getOwlClass(class3ParentKey);
      assertTrue("Class3Parent must have a SubClass", class3SuperClass.hasSubClasses());
      Map<ElementKey, OwlClass> classMap = class3.getSuperClasses();
      assertTrue("Class3 must have a defined SuperClass", classMap.containsKey(class3ParentKey));
   }

   /**
    * Test of checking the content of the graph for the properties.
    */
   @Test
   @Order(order = 3)
   public void testExtractGraphProperties() throws Exception {
      System.out.println("GraphExtractorTest : testExtractGraphProperties");
      assertNotNull("OwlSchema must not be null", schema);

      // object properties
      ElementKey class1And3ToClass2Key = new ElementKey(namespace, "FromClass1And3ToClass2");
      ElementKey class1ToClass2Key = new ElementKey(namespace, "FromClass1ToClass2");
      ElementKey class2ToClass1Key = new ElementKey(namespace, "FromClass2ToClass1");

      assertTrue("Must have FromClass1ToClass2", schema.hasOwlProperty(class1ToClass2Key));
      assertTrue("Must have FromClass2ToClass1", schema.hasOwlProperty(class2ToClass1Key));
      assertTrue("Must have FromClass1And3ToClass2", schema.hasOwlProperty(class1And3ToClass2Key));

      OwlProperty class1ToClass2Prop = schema.getOwlProperty(class1ToClass2Key);
      assertTrue("FromClass1ToClass2 must be an Object property", class1ToClass2Prop instanceof OwlObjectProperty);
      assertTrue("FromClass1ToClass2 must be an Object property", class1ToClass2Prop.isObjectProperty());
      assertFalse("FromClass1ToClass2 must be an Object property", class1ToClass2Prop.isDatatypeProperty());

      OwlProperty class2ToClass1Prop = schema.getOwlProperty(class2ToClass1Key);
      assertTrue("FromClass2ToClass1 must be an Object property", class2ToClass1Prop instanceof OwlObjectProperty);
      assertTrue("FromClass2ToClass1 must be an Object property", class2ToClass1Prop.isObjectProperty());
      assertFalse("FromClass2ToClass1 must be an Object property", class2ToClass1Prop.isDatatypeProperty());

      OwlProperty class1And3ToClass2Prop = schema.getOwlProperty(class1And3ToClass2Key);
      assertTrue("FromClass1And3ToClass2 must be an Object property", class1And3ToClass2Prop instanceof OwlObjectProperty);
      assertTrue("FromClass1And3ToClass2 must be an Object property", class1And3ToClass2Prop.isObjectProperty());
      assertFalse("FromClass1And3ToClass2 must be an Object property", class1And3ToClass2Prop.isDatatypeProperty());

      OwlObjectProperty class1And3ToClass2PropObject = (OwlObjectProperty) class1And3ToClass2Prop;
      assertFalse("FromClass1And3ToClass2 must not have an inverse property", class1And3ToClass2PropObject.hasInverseProperty());

      OwlObjectProperty class1ToClass2PropObject = (OwlObjectProperty) class1ToClass2Prop;
      OwlObjectProperty class2ToClass1PropObject = (OwlObjectProperty) class2ToClass1Prop;
      assertTrue("FromClass1ToClass2 must have an inverse property", class1ToClass2PropObject.hasInverseProperty());
      OwlObjectProperty inverseProp = class1ToClass2PropObject.getInverseProperty();
      assertTrue("FromClass1ToClass2 must have an inverse property", inverseProp == class2ToClass1PropObject);

      Map<ElementKey, OwlRestriction> domain = class1ToClass2PropObject.getDomain();
      assertEquals("FromClass1ToClass2 domain must have 1 Class", 1, domain.size());
      Map<ElementKey, OwlRestriction> range = class1ToClass2PropObject.getRange();
      assertEquals("FromClass1ToClass2 range must have 1 Class", 1, range.size());

      domain = class2ToClass1Prop.getDomain();
      assertEquals("FromClass2ToClass1 domain must have 1 Class", 1, domain.size());
      range = class1ToClass2PropObject.getRange();
      assertEquals("FromClass2ToClass1 range must have 1 Class", 1, range.size());

      domain = class1And3ToClass2PropObject.getDomain();
      assertEquals("FromClass1And3ToClass2 domain must have 2 Classes", 2, domain.size());

      // datatypes properties
      ElementKey intPropFromClass1Key = new ElementKey(namespace, "IntPropertyFromClass1");
      ElementKey intPropFromClass1And3Key = new ElementKey(namespace, "intPropertyFromClass1AndClass3");
      ElementKey intPropetyFromNothingKey = new ElementKey(namespace, "intPropetyFromNothing");
      assertTrue("Must have IntPropertyFromClass1", schema.hasOwlProperty(intPropFromClass1Key));
      assertTrue("Must have intPropertyFromClass1AndClass3", schema.hasOwlProperty(intPropFromClass1And3Key));
      assertTrue("Must have intPropetyFromNothing", schema.hasOwlProperty(intPropetyFromNothingKey));

      OwlProperty intPropFromClass1Prop = schema.getOwlProperty(intPropFromClass1Key);
      assertTrue("IntPropertyFromClass1 must be a datatype property", intPropFromClass1Prop instanceof OwlDatatypeProperty);
      assertFalse("IntPropertyFromClass1 must be a datatype property", intPropFromClass1Prop.isObjectProperty());
      assertTrue("IntPropertyFromClass1 must be a datatype property", intPropFromClass1Prop.isDatatypeProperty());

      domain = intPropFromClass1Prop.getDomain();
      assertEquals("IntPropertyFromClass1 domain must have 1 Class", 1, domain.size());

      OwlProperty intPropFromClass1And3Prop = schema.getOwlProperty(intPropFromClass1And3Key);
      assertTrue("intPropertyFromClass1AndClass3 must be a datatype property", intPropFromClass1And3Prop instanceof OwlDatatypeProperty);
      assertFalse("intPropertyFromClass1AndClass3 must be a datatype property", intPropFromClass1And3Prop.isObjectProperty());
      assertTrue("FromClass1ToClass2 must be a datatype property", intPropFromClass1And3Prop.isDatatypeProperty());

      OwlProperty intPropetyFromNothingProp = schema.getOwlProperty(intPropetyFromNothingKey);
      assertTrue("intPropetyFromNothing must be a datatype property", intPropetyFromNothingProp instanceof OwlDatatypeProperty);
      assertFalse("intPropetyFromNothing must be a datatype property", intPropetyFromNothingProp.isObjectProperty());
      assertTrue("intPropetyFromNothing must be a datatype property", intPropetyFromNothingProp.isDatatypeProperty());
   }
}
