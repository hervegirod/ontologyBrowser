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
import java.util.StringTokenizer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlAnnotation;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;

/**
 * A tree cell renderer.
 *
 * @version 0.6
 */
public class ModelTreeRenderer extends DefaultTreeCellRenderer {
   private static final Icon PACKAGE_ICON;
   private static final Icon CLASS_ICON;
   private static final Icon DATAPROPERTY_ICON;
   private static final Icon OBJECTPROPERTY_ICON;
   private static final Icon INDIVIDUAL_ICON;
   private static final Icon UNDEF_ICON;
   private static final Icon PACKAGE_COMMENTS_ICON;
   private static final Icon CLASS_COMMENTS_ICON;
   private static final Icon DATAPROPERTY_COMMENTS_ICON;
   private static final Icon OBJECTPROPERTY_COMMENTS_ICON;
   private static final Icon INDIVIDUAL_COMMENTS_ICON;
   private static final Icon ANNOTATION_ICON;
   private boolean showCommentedElements = false;

   static {
      PACKAGE_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("package.gif"));
      CLASS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("class.gif"));
      DATAPROPERTY_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("propertydata.png"));
      OBJECTPROPERTY_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("propertyobject.png"));
      INDIVIDUAL_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("individual.png"));
      PACKAGE_COMMENTS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("packagedoc.png"));
      CLASS_COMMENTS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("classdoc.png"));
      DATAPROPERTY_COMMENTS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("propertydatadoc.png"));
      OBJECTPROPERTY_COMMENTS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("propertyobjectdoc.png"));
      INDIVIDUAL_COMMENTS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("individualdoc.png"));
      ANNOTATION_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("annotation.png"));
      UNDEF_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("undef.png"));
   }

   public ModelTreeRenderer() {
      super();
      this.showCommentedElements = BrowserConfiguration.getInstance().showComments;
   }

   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      DefaultTreeCellRenderer c = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      Object o = ((DefaultMutableTreeNode) value).getUserObject();
      c.setIcon(getValueIcon(o, expanded));
      setToolTipText(c, o);
      setFont(c, o);
      return c;
   }

   private void setToolTipText(DefaultTreeCellRenderer c, Object value) {
      if (value instanceof OwlElementRep) {
         OwlElementRep rep = (OwlElementRep) value;
         String comment = rep.getOwlElement().getDescriptionOrComments();
         if (comment != null) {
            c.setToolTipText(getHTMLComment(comment));
         } else {
            c.setToolTipText(null);
         }
      } else {
         c.setToolTipText(null);
      }
   }

   private String getHTMLComment(String comment) {
      StringTokenizer tok = new StringTokenizer(comment, "\n");
      if (tok.countTokens() == 1) {
         return comment;
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append("<html>");
         while (tok.hasMoreTokens()) {
            String tk = tok.nextToken();
            buf.append(tk);
            if (tok.hasMoreTokens()) {
               buf.append("\n<br>");
            }
         }
         buf.append("</html>");
         return buf.toString();
      }
   }

   private void setFont(DefaultTreeCellRenderer c, Object value) {
      if (value instanceof OwlElementRep) {
         c.setFont(c.getFont().deriveFont(Font.PLAIN));
      } else {
         c.setFont(c.getFont().deriveFont(Font.ITALIC));
      }
   }

   private Icon getValueIcon(Object value, boolean expanded) {
      if (value instanceof OwlElementRep) {
         OwlElementRep rep = (OwlElementRep) value;
         if (rep.isPackage()) {
            NamedOwlElement elt = rep.getOwlElement();
            boolean showComments = showCommentedElements && elt.hasDescriptionOrComments();
               if (showComments) {
                  return PACKAGE_COMMENTS_ICON;
               } else {
                  return PACKAGE_ICON;
               }                
         } else {
            NamedOwlElement elt = rep.getOwlElement();
            boolean showComments = showCommentedElements && elt.hasDescriptionOrComments();
            if (elt instanceof OwlClass) {
               if (showComments) {
                  return CLASS_COMMENTS_ICON;
               } else {
                  return CLASS_ICON;
               }
            } else if (elt instanceof OwlIndividual) {
               if (showComments) {
                  return INDIVIDUAL_COMMENTS_ICON;
               } else {
                  return INDIVIDUAL_ICON;
               }               
            } else if (elt instanceof OwlDatatypeProperty) {
               if (showComments) {
                  return DATAPROPERTY_COMMENTS_ICON;
               } else {
                  return DATAPROPERTY_ICON;
               }                 
            } else if (elt instanceof OwlObjectProperty) {
               if (showComments) {
                  return  OBJECTPROPERTY_COMMENTS_ICON;
               } else {
                  return OBJECTPROPERTY_ICON;
               }    
            } else if (elt instanceof OwlAnnotation) {
                  return ANNOTATION_ICON;                   
            } else {
               return UNDEF_ICON;
            }
         }
      } else {
         if (expanded) {
            return this.getDefaultOpenIcon();
         } else {
            return this.getDefaultClosedIcon();
         }
      }
   }
}
