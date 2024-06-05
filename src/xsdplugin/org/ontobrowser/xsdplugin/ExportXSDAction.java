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
package org.ontobrowser.xsdplugin;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatype;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdiutil.xml.tree.XMLNode;
import org.mdiutil.xml.tree.XMLNodeUtilities;
import org.mdiutil.xml.tree.XMLRoot;
import org.ontobrowser.xsdplugin.model.ComplexTypeRep;
import org.ontobrowser.xsdplugin.model.SequenceElement;
import org.ontobrowser.xsdplugin.model.SequenceListElement;
import org.ontobrowser.xsdplugin.model.XSDModel;

/**
 * The Action that exprot an Ontology as an XSD.
 *
 * @since 0.13
 */
public class ExportXSDAction extends AbstractMDIAction {
   private File file = null;
   private OwlSchema schema = null;
   private XSDModel model = null;
   private XMLRoot rootNode = null;

   /**
    * Constructor.
    *
    * @param app the Application
    * @param schema the schema
    * @param file the file to open
    */
   public ExportXSDAction(MDIApplication app, OwlSchema schema, File file) {
      super(app, "Export as XSD");
      this.file = file;
      this.schema = schema;
      this.setDescription("Export as XSD", "Export as XSD");
   }

   /**
    * Return the diagram.
    *
    * @return the diagram
    */
   public OwlSchema getSchema() {
      return schema;
   }

   @Override
   public void run() throws Exception {
      model = new XSDModel(schema);
      Iterator<OwlClass> it = schema.getOwlClasses().values().iterator();
      while (it.hasNext()) {
         OwlClass owlClass = it.next();
         if (!owlClass.isForeign()) {
            model.addOwlClass(owlClass);
         }
      }
      model.setUp();

      rootNode = new XMLRoot("xs:schema");
      rootNode.addAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
      rootNode.addAttribute("elementFormDefault", "qualified");
      XMLNode node = new XMLNode("xs:element");
      node.addAttribute("name", model.getRootName());
      rootNode.addChild(node);
      XMLNode sequenceNode = addSequence(node);
      Iterator<ComplexTypeRep> it2 = model.getTopLevelTypes().values().iterator();
      while (it2.hasNext()) {
         ComplexTypeRep rep = it2.next();
         node = new XMLNode("xs:element");
         node.addAttribute("minOccurs", 0);
         node.addAttribute("maxOccurs", 1);
         node.addAttribute("name", rep.getName());
         node.addAttribute("type", rep.getName());
         sequenceNode.addChild(node);
      }
      writeTopLevelTypes(rootNode);
      writeComplexTypes(rootNode);

      XMLNodeUtilities.print(rootNode, 3, file);
   }

   private void writeTopLevelTypes(XMLRoot root) {
      Iterator<ComplexTypeRep> it = model.getTopLevelTypes().values().iterator();
      while (it.hasNext()) {
         ComplexTypeRep rep = it.next();
         writeTopLevelType(rep, root);
      }
   }

   private void writeTopLevelType(ComplexTypeRep rep, XMLNode parentNode) {
      XMLNode node = new XMLNode("xs:complexType");
      node.addAttribute("name", rep.getName());
      addAnnotation(node, rep);
      parentNode.addChild(node);
      if (rep.hasChildrenElements() || rep.hasObjectProperties()) {
         writeSequence(node, rep);
      }
      if (rep.hasDataProperties()) {
         writeProperties(node, rep);
      }
   }

   private void writeComplexTypes(XMLRoot root) {
      Iterator<ComplexTypeRep> it = model.getComplexTypes().values().iterator();
      while (it.hasNext()) {
         ComplexTypeRep rep = it.next();
         if (model.isTopLevelType(rep.getKey()) || rep.isEmpty()) {
            continue;
         }
         XMLNode node = new XMLNode("xs:complexType");
         node.addAttribute("name", rep.getName());
         addAnnotation(node, rep);
         root.addChild(node);
         if (rep.hasChildrenElements() || rep.hasObjectProperties()) {
            writeSequence(node, rep);
         }
         if (rep.hasDataProperties()) {
            writeProperties(node, rep);
         }
      }
   }

   private void writeProperties(XMLNode typeNode, ComplexTypeRep typeRep) {
      Iterator<OwlDatatypeProperty> itd = typeRep.getDataProperties().values().iterator();
      while (itd.hasNext()) {
         OwlDatatypeProperty property = itd.next();
         String name = property.getName();
         String theType = getType(property);
         XMLNode attrNode = new XMLNode("xs:attribute");
         attrNode.addAttribute("name", name);
         attrNode.addAttribute("type", theType);
         if (property.hasMinCardinality() && property.getMinCardinality() > 0) {
            attrNode.addAttribute("use", "required");
         }
         typeNode.addChild(attrNode);
      }
   }

   private String getType(OwlDatatypeProperty property) {
      Map<ElementKey, OwlDatatype> types = property.getTypes();
      if (types.isEmpty() || types.size() > 1) {
         return "xs:string";
      } else {
         OwlDatatype datatype = types.values().iterator().next();
         switch (datatype.getType()) {
            case OwlDatatype.BOOLEAN:
               return "xs:boolean";
            case OwlDatatype.DOUBLE:
            case OwlDatatype.FLOAT:
               return "xs:decimal";
            case OwlDatatype.INT:
               return "xs:int";
            case OwlDatatype.LONG:
               return "xs:long";
            case OwlDatatype.NON_NEGATIVE_INT:
               return "xs:nonNegativeInteger";
            case OwlDatatype.POSITIVE_INT:
               return "xs:positiveInteger";
            case OwlDatatype.SHORT:
               return "xs:short";
            case OwlDatatype.STRING:
               return "xs:string";
            default:
               return "xs:string";
         }
      }
   }

   private void writeSequence(XMLNode typeNode, ComplexTypeRep typeRep) {
      boolean hasObjectProperties = typeRep.hasObjectProperties();
      boolean hasSequence = typeRep.hasChildrenElements();
      if (hasSequence && !hasObjectProperties) {
         SequenceListElement sequence = typeRep.getChildrenElements();
         XMLNode sequenceNode;
         if (sequence.isSequence()) {
            sequenceNode = new XMLNode("xs:sequence");
         } else {
            sequenceNode = new XMLNode("xs:choice");
         }
         typeNode.addChild(sequenceNode);
         sequenceNode.addAttribute("minOccurs", sequence.getMinimumAsString());
         sequenceNode.addAttribute("maxOccurs", sequence.getMaximumAsString());
         Iterator<SequenceElement> it = sequence.getChildren().values().iterator();
         while (it.hasNext()) {
            SequenceElement element = it.next();
            String name = element.getKey().getName();
            XMLNode node = new XMLNode("xs:element");
            node.addAttribute("name", name);
            node.addAttribute("type", name);
            sequenceNode.addChild(node);
         }
      } else if (!hasSequence && hasObjectProperties) {
         XMLNode sequenceNode = new XMLNode("xs:sequence");
         typeNode.addChild(sequenceNode);
         Iterator<OwlObjectProperty> ito = typeRep.getObjectProperties().values().iterator();
         while (ito.hasNext()) {
            OwlObjectProperty property = ito.next();
            String propertyName = property.getName();
            XMLNode linkNode = new XMLNode("xs:element");
            Map<ElementKey, OwlRestriction> domain = property.getDomain();
            if (domain.size() == 1) {
               OwlRestriction restriction = domain.values().iterator().next();
               String name = restriction.getKey().getName();
               linkNode.addAttribute("name", propertyName);
               linkNode.addAttribute("type", propertyName);
               sequenceNode.addChild(linkNode);
               XMLNode domainNode = new XMLNode("xs:complexType");
               domainNode.addAttribute("name", name);
               rootNode.addChild(domainNode);
            }
         }
      } else if (hasSequence && hasObjectProperties) {
         XMLNode topsequenceNode = new XMLNode("xs:sequence");
         typeNode.addChild(topsequenceNode);

         SequenceListElement sequence = typeRep.getChildrenElements();
         XMLNode sequenceNode;
         if (sequence.isSequence()) {
            sequenceNode = new XMLNode("xs:sequence");
         } else {
            sequenceNode = new XMLNode("xs:choice");
         }
         topsequenceNode.addChild(sequenceNode);
         sequenceNode.addAttribute("minOccurs", sequence.getMinimumAsString());
         sequenceNode.addAttribute("maxOccurs", sequence.getMaximumAsString());
         Iterator<SequenceElement> it = sequence.getChildren().values().iterator();
         while (it.hasNext()) {
            SequenceElement element = it.next();
            String name = element.getKey().getName();
            XMLNode node = new XMLNode("xs:element");
            node.addAttribute("name", name);
            node.addAttribute("type", name);
            sequenceNode.addChild(node);
         }
         sequenceNode = new XMLNode("xs:sequence");
         topsequenceNode.addChild(sequenceNode);
         Iterator<OwlObjectProperty> ito = typeRep.getObjectProperties().values().iterator();
         while (ito.hasNext()) {
            OwlObjectProperty property = ito.next();
            String propertyName = property.getName();
            XMLNode linkNode = new XMLNode("xs:element");
            Map<ElementKey, OwlRestriction> domain = property.getDomain();
            if (domain.size() == 1) {
               OwlRestriction restriction = domain.values().iterator().next();
               linkNode.addAttribute("name", propertyName);
               linkNode.addAttribute("type", propertyName);
               sequenceNode.addChild(linkNode);
               XMLNode domainNode = new XMLNode("xs:complexType");
               domainNode.addAttribute("name", propertyName);
               XMLNode attrNode = new XMLNode("xs:attribute");
               attrNode.addAttribute("name", "nameRef");
               attrNode.addAttribute("type", "xs:string");
               domainNode.addChild(attrNode);
               rootNode.addChild(domainNode);
            }
         }
      }
   }

   private void addAnnotation(XMLNode node, ComplexTypeRep rep) {
      String desc = rep.getAnnotation();
      if (desc != null) {
         XMLNode annNode = new XMLNode("xs:annotation");
         XMLNode docNode = new XMLNode("xs:documentation");
         docNode.setCDATA(desc);
         annNode.addChild(docNode);
         node.addChild(annNode);
      }
   }

   private XMLNode addSequence(XMLNode elementNode) {
      XMLNode complexTypeNode = new XMLNode("xs:complexType");
      elementNode.addChild(complexTypeNode);

      XMLNode sequenceNode = new XMLNode("xs:sequence");
      complexTypeNode.addChild(sequenceNode);

      return sequenceNode;
   }
}
