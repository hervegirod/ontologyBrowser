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
package org.girod.ontobrowser.gui.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.girod.ontobrowser.MenuFactory;
import org.girod.ontobrowser.actions.SearchAction;
import org.girod.ontobrowser.gui.GraphPanel;
import org.mdi.bootstrap.swing.MDIDialog;
import org.mdiutil.swing.GenericDialog;

/**
 * This class is the result of the Search Dialog.
 *
 * @since 0.5
 */
public class SearchResultDialog extends GenericDialog implements MDIDialog {
   private DefaultTableModel model;
   private GraphPanel graphPanel;
   private JTable table;
   private static double width;
   private static int w_short;
   private static int w_long;
   private static int h;
   private int index = -1;
   private MenuFactory fac = null;
   private static JDialog currentDialog = null;

   static {
      JLabel label = new JLabel("0123456789012345678901234501234567890012345");
      width = label.getPreferredSize().getWidth();
      Vector v = new Vector(1);
      for (int i = 0; i < 10; i++) {
         v.add("ABCDE");
      }
      JList list = new JList(v);
      h = list.getPreferredSize().height;

      w_long = label.getPreferredSize().width;
      label = new JLabel("01234567890123456");
      w_short = label.getPreferredSize().width;
   }

   public SearchResultDialog() {
      super();
   }

   public SearchResultDialog(Component parent, MenuFactory fac, DefaultTableModel model, GraphPanel graphPanel, String title) {
      super();
      this.model = model;
      this.fac = fac;
      this.graphPanel = graphPanel;
      this.setResizable(true);
      this.setDialogTitle(title);
      this.setYesNoLabels("New Search", "Cancel");
      this.createDialog(parent, false);
   }

   @Override
   protected JDialog createDialog(Component parent) {
      return createDialog(parent, false);
   }

   /**
    * show the dialog.
    *
    * @return the JFileChooser return value.
    */
   @Override
   public int showDialog() {
      createDialog(getParent());
      if (currentDialog != null) {
         currentDialog.dispose();
         currentDialog = null;
      }
      if (dialog != null) {
         currentDialog = dialog;
         dialog.setVisible(true);
      } else {
         frame.setVisible(true);
      }

      return returnValue;
   }

   @Override
   protected void doYes() {
      super.doYes();
      fac.startSearch(false);
      fac.doSearch();
   }

   @Override
   protected void doCancel() {
      currentDialog = null;
      super.doCancel();
   }

   @Override
   protected void createPanel() {
      // get the container where to put the search panels
      Container pane = dialog.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

      JPanel resultsPanel = new JPanel();
      resultsPanel.setLayout(new BorderLayout());
      TitledBorder listBorder = BorderFactory.createTitledBorder("Elements List");
      listBorder.setTitleColor(Color.GRAY);
      resultsPanel.setBorder(listBorder);
      pane.add(resultsPanel);

      // create the list panel
      JPanel listPanel = new JPanel();
      resultsPanel.add(listPanel, BorderLayout.CENTER);
      listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

      // table
      table = new JTable(model);
      // the following line allows to sort the table by its header
      table.setAutoCreateRowSorter(true);
      table.setShowHorizontalLines(false);
      table.setShowVerticalLines(false);
      // header
      DefaultTableColumnModel tableColumnModel = new DefaultTableColumnModel();
      TableColumn column = new TableColumn();
      column.setModelIndex(0);
      tableColumnModel.addColumn(column);
      column = new TableColumn();
      column.setModelIndex(1);
      tableColumnModel.addColumn(column);
      table.setColumnModel(tableColumnModel);

      // set table preferred size
      table.setPreferredScrollableViewportSize(new Dimension(w_short + w_long, h));
      table.getColumnModel().getColumn(0).setPreferredWidth(w_short);
      table.getColumnModel().getColumn(1).setPreferredWidth(w_short);

      // list selection model
      DefaultListSelectionModel selModel = new DefaultListSelectionModel();
      selModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      table.setSelectionModel(selModel);
      // add the table header
      TableColumnModel cmodel = table.getTableHeader().getColumnModel();
      cmodel.getColumn(0).setHeaderValue("Name");
      cmodel.getColumn(1).setHeaderValue("Type");
      listPanel.add(table.getTableHeader());
      listPanel.add(new JScrollPane(table));

      selModel.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            index = table.getSelectedRow();
            // we will have the -1 value for the header
            if (index != -1) {
               // this is necessary to get the index in the model, else we will have the index in the Sorter view
               index = table.getRowSorter().convertRowIndexToModel(index);
               highlight(index);
            }
         }
      });

      pane.add(Box.createRigidArea(new Dimension(50, 20)));
      pane.add(createYesNoPanel());
      pane.add(Box.createHorizontalGlue());
      pane.add(Box.createVerticalGlue());
   }

   private void highlight(int index) {
      if (index >= 0) {
         SearchAction.Result result = (SearchAction.Result) table.getModel().getValueAt(index, 0);
         graphPanel.highlightElement(result.getElementType(), result.path);
      }
   }

}
