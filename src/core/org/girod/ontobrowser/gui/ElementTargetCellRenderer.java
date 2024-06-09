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
package org.girod.ontobrowser.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Used to wrap the text inside a table cell.
 *
 * @since 0.13
 */
public class ElementTargetCellRenderer extends DefaultTableCellRenderer implements MouseListener, MouseMotionListener {
   private int row = -1;
   private int col = -1;
   private boolean isRollover = false;

   public ElementTargetCellRenderer() {
   }

   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
      setText("<html><u><font color='blue'>" + value.toString() + "</font></u></html>");
      return this;
   }

   private static boolean isURLColumn(JTable table, int column) {
      return column >= 0 && table.getColumnClass(column).equals(URL.class);
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      JTable table = (JTable) e.getSource();
      Point pt = e.getPoint();
      int prev_row = row;
      int prev_col = col;
      boolean prev_ro = isRollover;
      row = table.rowAtPoint(pt);
      col = table.columnAtPoint(pt);
      isRollover = isURLColumn(table, col); // && pointInsidePrefSize(table, pt);
      if ((row == prev_row && col == prev_col && Boolean.valueOf(isRollover).equals(prev_ro))
              || (!isRollover && !prev_ro)) {
         return;
      }
      Rectangle repaintRect;
      if (isRollover) {
         Rectangle r = table.getCellRect(row, col, false);
         repaintRect = prev_ro ? r.union(table.getCellRect(prev_row, prev_col, false)) : r;
      } else { //if(prev_ro) {
         repaintRect = table.getCellRect(prev_row, prev_col, false);
      }
      table.repaint(repaintRect);
   }

   @Override
   public void mouseExited(MouseEvent e) {
      JTable table = (JTable) e.getSource();
      if (isURLColumn(table, col)) {
         table.repaint(table.getCellRect(row, col, false));
         row = -1;
         col = -1;
         isRollover = false;
      }
   }

   @Override
   public void mouseClicked(MouseEvent e) {
      JTable table = (JTable) e.getSource();
      Point pt = e.getPoint();
      int ccol = table.columnAtPoint(pt);
      if (isURLColumn(table, ccol)) {
         int crow = table.rowAtPoint(pt);
         URL url = (URL) table.getValueAt(crow, ccol);
      }
   }

   @Override
   public void mousePressed(MouseEvent e) {
   }

   @Override
   public void mouseReleased(MouseEvent e) {
   }

   @Override
   public void mouseEntered(MouseEvent e) {
   }

   @Override
   public void mouseDragged(MouseEvent e) {
   }
}
