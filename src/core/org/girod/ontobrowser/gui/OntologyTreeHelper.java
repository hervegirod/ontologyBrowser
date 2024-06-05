/*
Copyright (c) 2023 Hervé Girod
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
package org.girod.ontobrowser.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.gui.tree.OwlImportedSchemaRep;
import org.girod.ontobrowser.gui.tree.OwlPrefixRep;
import org.girod.ontobrowser.model.OwlImportedSchema;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.SchemasRepository;

/**
 * This class is used to create the Ontology tab and its associated tree.
 *
 * @since 0.8
 */
class OntologyTreeHelper {
   private OntologyTreeHelper() {
   }

   static void computeOntologyPrefixTree(OwlDiagram diagram, JTree prefixTree, DefaultMutableTreeNode prefixRoot) {
      OwlSchema schema = diagram.getSchema();
      DefaultMutableTreeNode namespacesNode = new DefaultMutableTreeNode(GUITabTypes.ONTOLOGY_NAMESPACES_NAME);
      prefixRoot.add(namespacesNode);
      DefaultMutableTreeNode importsNode = new DefaultMutableTreeNode(GUITabTypes.ONTOLOGY_IMPORTS_NAME);
      prefixRoot.add(importsNode);
      SchemasRepository schemasRepository = SchemasRepository.getInstance();
      Map<String, OwlImportedSchemaRep> importedReps = new HashMap<>();
      Map<String, DefaultMutableTreeNode> importedRepNodes = new HashMap<>();
      Map<String, String> prefixMap = new TreeMap<>(schema.getPrefixToNamespaceMap());
      // now create the nodes
      Iterator<Map.Entry<String, String>> it = prefixMap.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<String, String> entry = it.next();
         String prefix = entry.getKey();
         DefaultMutableTreeNode node;
         if (schema.hasImportedSchema(prefix)) {
            OwlImportedSchemaRep importSchemaRep = new OwlImportedSchemaRep(schema.getImportedSchema(prefix));
            importedReps.put(importSchemaRep.getNamespace(), importSchemaRep);
            node = new DefaultMutableTreeNode(importSchemaRep);
            OwlImportedSchema importedSchema = schema.getImportedSchema(prefix);
            importSchemaRep = new OwlImportedSchemaRep(importedSchema);
            DefaultMutableTreeNode importnode = new DefaultMutableTreeNode(importSchemaRep);
            importedRepNodes.put(importSchemaRep.getNamespace(), importnode);
            importsNode.add(importnode);
         } else {
            OwlPrefixRep prefixRep = new OwlPrefixRep(prefix, entry.getValue());
            node = new DefaultMutableTreeNode(prefixRep);
         }
         namespacesNode.add(node);
      }
      Iterator<OwlImportedSchemaRep> it2 = importedReps.values().iterator();
      while (it2.hasNext()) {
         OwlImportedSchemaRep theImportedRep = it2.next();
         OwlImportedSchema theImportedSchema = theImportedRep.getImportedSchema();
         if (theImportedSchema.getSchemaRep().hasDependencies()) {
            DefaultMutableTreeNode theNode = importedRepNodes.get(theImportedSchema.getNamespace());
            Iterator<SchemasRepository.SchemaRep> it3 = theImportedSchema.getSchemaRep().getDependenciesByNamespace().values().iterator();
            while (it3.hasNext()) {
               SchemasRepository.SchemaRep theRep = it3.next();
               String namespace = theRep.getNamespace();
               OwlImportedSchemaRep theImportedRep2 = null;
               if (schemasRepository.hasSchemaByNamespace(namespace)) {
                  SchemasRepository.SchemaRep theRep2 = schemasRepository.getSchemaByNamespace(namespace);
                  theImportedRep2 = new OwlImportedSchemaRep(theRep2);
               }
               if (theImportedRep2 != null) {
                  DefaultMutableTreeNode node = new DefaultMutableTreeNode(theImportedRep2);
                  theNode.add(node);
               }
            }
         }
      }
      TreePath path = new TreePath(importsNode.getPath());
      prefixTree.expandPath(path);
      path = new TreePath(namespacesNode.getPath());
      prefixTree.expandPath(path);
   }
}
