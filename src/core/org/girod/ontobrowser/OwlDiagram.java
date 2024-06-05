/*
Copyright (c) 2021, 2023 Hervé Girod
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
package org.girod.ontobrowser;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.io.File;
import java.util.Map;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlRepresentationType;
import org.girod.ontobrowser.model.OwlSchema;

/**
 * An Owl diagram shown in the browser.
 *
 * @version 0.13
 */
public class OwlDiagram {
   private mxGraph graph = null;
   private mxGraphComponent comp = null;
   private Map<ElementKey, mxCell> keyToCell = null;
   private String name = null;
   private File file;
   private short representationType = OwlRepresentationType.TYPE_OWL_XML;
   private OwlSchema schema = null;

   /**
    * Constructor.
    *
    * @param name the diagram name
    */
   public OwlDiagram(String name) {
      this.name = name;
   }

   /**
    * Return the diagram name.
    *
    * @return the diagram name
    */
   public String getName() {
      return name;
   }

   /**
    * Set the representation type.
    *
    * @param representationType the representation type
    */
   public void setRepresentationType(short representationType) {
      this.representationType = representationType;
   }

   /**
    * Return the representation type.
    *
    * @return the representation type
    */
   public short getRepresentationType() {
      return representationType;
   }

   /**
    * Set the schema associated file.
    *
    * @param file the schema associated file
    */
   public void setFile(File file) {
      this.file = file;
   }

   /**
    * Set the schema.
    *
    * @param schema the schema
    */
   public void setSchema(OwlSchema schema) {
      this.schema = schema;
   }

   /**
    * Return the schema.
    *
    * @return the schema
    */
   public OwlSchema getSchema() {
      return schema;
   }

   /**
    * Return true if there are packages in the model.
    *
    * @return true if there are packages in the model
    */
   public boolean hasPackages() {
      return schema.hasPackages();
   }

   /**
    * Return the File.
    *
    * @return the File
    */
   public File getFile() {
      return file;
   }

   /**
    * Set the diagram associated graph.
    *
    * @param graph the diagram associated graph
    */
   public void setGraph(mxGraph graph) {
      this.graph = graph;
   }

   /**
    * Set the diagram associated graph component.
    *
    * @param comp the diagram associated graph component
    */
   public void setGraphComponent(mxGraphComponent comp) {
      this.comp = comp;
   }

   /**
    * Set the map from class keys to the cell.
    *
    * @param keyToCell the map
    */
   public void setKeyToCell(Map<ElementKey, mxCell> keyToCell) {
      this.keyToCell = keyToCell;
   }

   /**
    * Return the map from class keys to the cell.
    *
    * @return the map
    */
   public Map<ElementKey, mxCell> getKeyToCell() {
      return keyToCell;
   }

   /**
    * Return the cell for a key.
    *
    * @param key the element key
    * @return the cell
    */
   public mxCell getCell(ElementKey key) {
      return keyToCell.get(key);
   }

   /**
    * Return the diagram associated graph.
    *
    * @return the diagram associated graph
    */
   public mxGraph getGraph() {
      return graph;
   }

   /**
    * Return the diagram associated graph component.
    *
    * @return the diagram associated graph component
    */
   public mxGraphComponent getGraphComponent() {
      return comp;
   }
}
