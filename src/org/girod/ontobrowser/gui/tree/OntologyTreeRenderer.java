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
package org.girod.ontobrowser.gui.tree;

import java.awt.Component;
import java.awt.Font;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.girod.ontobrowser.gui.GUITabTypes;

/**
 * A tree cell renderer.
 *
 * @since 0.8
 */
public class OntologyTreeRenderer extends DefaultTreeCellRenderer {
   private static final Icon ONTOLOGY_ICON;
   private static final Icon ONTOLOGY_IMPORT_ICON;
   private static final Icon PREFIX_ICON;

   static {
      ONTOLOGY_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("ontology.png"));
      ONTOLOGY_IMPORT_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("ontologyImport.png"));
      PREFIX_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("namespace.png"));
   }

   public OntologyTreeRenderer() {
      super();
   }

   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      DefaultTreeCellRenderer c = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      Object o = ((DefaultMutableTreeNode) value).getUserObject();
      c.setIcon(getValueIcon(o, expanded));
      setFont(c, o);
      return c;
   }

   private void setFont(DefaultTreeCellRenderer c, Object value) {
      if (value instanceof OwlPrefixRep || value instanceof OwlOntologyRep) {
         c.setFont(c.getFont().deriveFont(Font.PLAIN));
      } else {
         c.setFont(c.getFont().deriveFont(Font.ITALIC));
      }
   }

   private Icon getValueIcon(Object value, boolean expanded) {
      if (value instanceof OwlImportedSchemaRep) {
         return ONTOLOGY_IMPORT_ICON;
      } else if (value instanceof OwlPrefixRep) {
         return PREFIX_ICON;
      } else if (value instanceof OwlOntologyRep) {
         return ONTOLOGY_ICON;
      } else {
         if (value.equals(GUITabTypes.ONTOLOGY_NAMESPACES_NAME)) {
            return PREFIX_ICON;
         } else {
            return ONTOLOGY_IMPORT_ICON;
         }
      }
   }
}
