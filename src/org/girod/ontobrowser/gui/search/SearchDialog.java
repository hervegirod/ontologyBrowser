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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import org.girod.ontobrowser.model.ElementTypes;
import org.mdi.bootstrap.swing.MDIDialog;
import org.mdiutil.swing.GenericDialog;

/**
 * This class is a Search Dialog.
 *
 * @version 0.6
 */
public class SearchDialog extends GenericDialog implements MDIDialog {
   private JPanel listPanel = new JPanel();
   private JList<String> list;
   private DefaultListModel<String> model;
   private JTextField tf;
   private JCheckBox regexCheck;
   private JCheckBox matchcaseCheck;
   private static boolean _regexCheck = false;
   private static boolean _matchcaseCheck = true;
   private final Vector<String> categories = new Vector<>();

   public SearchDialog() {
      super("Search");
      categories.add(ElementTypes.ALL);
      categories.add(ElementTypes.CLASS);
      categories.add(ElementTypes.PROPERTY);
      categories.add(ElementTypes.DATAPROPERTY);
      categories.add(ElementTypes.OBJECTPROPERTY);
      categories.add(ElementTypes.ANNOTATION);
      categories.add(ElementTypes.INDIVIDUAL);
   }

   public List<String> getAvailableCategories() {
      return categories;
   }

   @Override
   protected void createPanel() {
      // get the container where to put the search panels
      Container pane = dialog.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

      // create the list panel
      model = new DefaultListModel<>();
      for (int i = 0; i < categories.size(); i++) {
         model.addElement(categories.get(i));
      }
      listPanel = new JPanel();
      TitledBorder listBorder = BorderFactory.createTitledBorder("Elements List");
      listBorder.setTitleColor(Color.GRAY);
      listPanel.setLayout(new BorderLayout());
      listPanel.setBorder(listBorder);
      list = new JList<>(model);
      listPanel.add(list, BorderLayout.CENTER);
      list.setSelectedValue("All", false);

      // create the regex panel
      JPanel regexPanel = new JPanel();
      regexCheck = new JCheckBox();
      regexCheck.setSelected(_regexCheck);
      regexCheck.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            _regexCheck = regexCheck.isSelected();
         }
      });

      JLabel matchcaseLabel = new JLabel("Match Case");
      matchcaseCheck = new JCheckBox();
      matchcaseCheck.setSelected(_matchcaseCheck);
      matchcaseCheck.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            _matchcaseCheck = matchcaseCheck.isSelected();
         }
      });

      JLabel regexLabel = new JLabel("Regular Expressions");
      regexPanel.setLayout(new BoxLayout(regexPanel, BoxLayout.X_AXIS));
      regexPanel.add(Box.createRigidArea(new Dimension(5, 5)));
      regexPanel.add(regexLabel);
      regexPanel.add(regexCheck);
      regexPanel.add(Box.createRigidArea(new Dimension(5, 5)));
      regexPanel.add(matchcaseLabel);
      regexPanel.add(matchcaseCheck);

      // create the search panel
      JPanel searchPanel = new JPanel();
      tf = new JTextField(15);

      searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
      searchPanel.add(Box.createRigidArea(new Dimension(5, 5)));
      searchPanel.add(tf);
      searchPanel.add(Box.createRigidArea(new Dimension(5, 5)));

      TitledBorder searchBorder = BorderFactory.createTitledBorder("Search");
      searchBorder.setTitleColor(Color.GRAY);
      searchPanel.setBorder(searchBorder);
      Dimension dim = list.getPreferredSize();
      dim.width = searchPanel.getPreferredSize().width;
      list.setPreferredSize(dim);

      pane.add(listPanel);
      pane.add(Box.createRigidArea(new Dimension(5, 5)));
      pane.add(regexPanel);
      pane.add(Box.createRigidArea(new Dimension(5, 5)));
      pane.add(searchPanel);
      pane.add(createYesNoPanel());
      pane.add(Box.createHorizontalGlue());
   }

   /**
    * Return the search category name, or null if no category or "All" was selected.
    *
    * @return the search category name, or null if no category or "All" was selected
    */
   public String getSearchCategory() {
      String selection = list.getSelectedValue();
      if ((selection != null) && (selection.equals(ElementTypes.ALL))) {
         selection = null;
      }
      return selection;
   }

   /**
    * Return the search string.
    *
    * @return the search string
    */
   public String getSearchString() {
      if (!(regexCheck.isSelected())) {
         if (tf.getText().length() == 0) {
            return "*";
         } else {
            return tf.getText();
         }
      } else {
         return tf.getText();
      }
   }

   /**
    * Return true if the search must be performed with longNames.
    *
    * @return true if the search must be performed with longNames
    */
   public boolean isRegexSearch() {
      return regexCheck.isSelected();
   }

   /**
    * Return true if the search must be performed with matchCase.
    *
    * @return true if the search must be performed with matchCase
    */
   public boolean matchCase() {
      return matchcaseCheck.isSelected();
   }
}
