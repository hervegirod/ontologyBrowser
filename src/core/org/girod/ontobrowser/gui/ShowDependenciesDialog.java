/*
Copyright (c) 2023, 2024 Hervé Girod
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.swing.MDIDialog;
import org.mdiutil.swing.GenericDialog;

/**
 * This Dialog shows the dependencies of an element.
 *
 * @version 0.13
 */
public class ShowDependenciesDialog extends GenericDialog implements MDIDialog {
   private NamedOwlElement element;
   private final GraphPanel panel;
   private final OwlSchema schema;
   private JList<Object> list;
   private final boolean autoRefresh;
   private final DependenciesHelper helper;
   private final DefaultListModel<Object> model = new DefaultListModel<>();

   public ShowDependenciesDialog(NamedOwlElement element, GraphPanel panel, Component parent, boolean autoRefresh) {
      super("Dependencies of " + element.toString());
      this.element = element;
      this.panel = panel;
      this.schema = panel.getSchema();
      this.helper = new DependenciesHelper(schema, model);
      this.autoRefresh = autoRefresh;
      this.setResizable(true);
   }

   /**
    * Refresh the content of the dialog window.
    *
    * @param element the element
    */
   public void refresh(NamedOwlElement element) {
      this.element = element;
      model.clear();
      initializeList(true);
   }

   private void createList() {
      list = new DependenciesList(model);

      list.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
               rightClickOnList(e.getX(), e.getY());
            }
         }
      });
   }

   @Override
   protected void createPanel() {
      Container pane = dialog.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
      pane.add(Box.createVerticalStrut(5));
      initializeList(false);

      // create the list panel
      JPanel listPanel = new JPanel();
      listPanel.setLayout(new BorderLayout());
      listPanel.add(new JScrollPane(list), BorderLayout.CENTER);
      pane.add(listPanel);

      JPanel actionspanel = this.createActionsPanel();
      pane.add(actionspanel);
   }

   /**
    * Create the Yes Panel, which is part of the Yes / No panel.
    *
    * @return the Yes Panel
    */
   private JPanel createActionsPanel() {
      JPanel actionsPanel = new JPanel();
      actionsPanel.setLayout(new FlowLayout());
      if (!autoRefresh) {
         AbstractAction refreshAction = new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent ae) {
               refresh(element);
            }
         };
         actionsPanel.add(new JButton(refreshAction));
      }
      yesAction = new AbstractAction(okLabel) {
         @Override
         public void actionPerformed(ActionEvent ae) {
            doYes();
         }
      };
      actionsPanel.add(new JButton(yesAction));

      return actionsPanel;
   }

   private void initializeList(boolean refreshed) {
      helper.createDependenciesList(element);
      if (!refreshed) {
         this.createList();
         list.setCellRenderer(new DependenciesListCellRenderer(schema));
      }
   }

   private void rightClickOnList(int x, int y) {
      Object o = list.getSelectedValue();
      if (o instanceof OwlClass) {
         OwlClass selectedClass = (OwlClass) o;
         JPopupMenu menu = new JPopupMenu();
         JMenuItem item = new JMenuItem("Goto Class");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               gotoClass(selectedClass);
            }
         });
         menu.add(item);
         menu.show(list, x, y);
      } else if (o instanceof OwlIndividual) {
         OwlIndividual selectedIndividual = (OwlIndividual) o;
         JPopupMenu menu = new JPopupMenu();
         JMenuItem item = new JMenuItem("Goto Individual");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               gotoIndividual(selectedIndividual);
            }
         });
         menu.add(item);
         menu.show(list, x, y);
      } else if (o instanceof OwlProperty) {
         OwlProperty selectedProperty = (OwlProperty) o;
         JPopupMenu menu = new JPopupMenu();
         JMenuItem item = new JMenuItem("Goto Property");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               gotoProperty(selectedProperty);
            }
         });
         menu.add(item);
         menu.show(list, x, y);
      } else if (o instanceof PropertyBridge) {
         PropertyBridge bridge = (PropertyBridge) o;
         OwlProperty selectedProperty = bridge.getOwlProperty();
         JPopupMenu menu = new JPopupMenu();
         JMenuItem item = new JMenuItem("Goto Property");
         item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               gotoProperty(selectedProperty);
            }
         });
         menu.add(item);
         menu.show(list, x, y);
      }
   }

   private void gotoClass(OwlClass selectedClass) {
      ElementKey key = selectedClass.getKey();
      panel.selectClass(key);
      this.element = selectedClass;
      if (autoRefresh) {
         this.refresh(selectedClass);
      }
   }

   private void gotoIndividual(OwlIndividual selectedIndividual) {
      ElementKey key = selectedIndividual.getKey();
      panel.selectIndividual(key);
      this.element = selectedIndividual;
      if (autoRefresh) {
         this.refresh(selectedIndividual);
      }
   }

   private void gotoProperty(OwlProperty selectedProperty) {
      ElementKey key = selectedProperty.getKey();
      panel.selectProperty(key);
      this.element = selectedProperty;
      if (autoRefresh) {
         this.refresh(selectedProperty);
      }
   }

   private class DependenciesList<E> extends JList {
      public DependenciesList(ListModel<Object> dataModel) {
         super(dataModel);
      }

      @Override
      public Dimension getPreferredScrollableViewportSize() {
         return new Dimension(500, 200);
      }
   }
}
