/*
 Copyright (c) 2023 Dassault Aviation. All rights reserved.
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

 Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008 Paris,
 France if you need additional information.
 Alternatively if you have any questions about this project, you can visit
 the project website at the project page on https://sourceforge.net/projects/protoframework/
 */
package org.girod.ontobrowser.gui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A tree cell renderer.
 *
 * @since 0.4
 */
public class ModelTreeRenderer extends DefaultTreeCellRenderer {
   private static final Icon PACKAGE_ICON;
   private static final Icon CLASS_ICON;

   static {
      PACKAGE_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("package.gif"));
      CLASS_ICON = new ImageIcon(ModelTreeRenderer.class.getResource("class.png"));
   }

   public ModelTreeRenderer() {
      super();
   }

   @Override
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      DefaultTreeCellRenderer c = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      Object o = ((DefaultMutableTreeNode) value).getUserObject();
      c.setIcon(getValueIcon(o, expanded));
      return c;
   }

   private Icon getValueIcon(Object value, boolean expanded) {
      if (value instanceof ClassRep) {
         ClassRep rep = (ClassRep) value;
         if (rep.isPackage()) {
            return PACKAGE_ICON;
         } else {
            return CLASS_ICON;
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
