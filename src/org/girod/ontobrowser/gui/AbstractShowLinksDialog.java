/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.girod.ontobrowser.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.mdi.bootstrap.swing.MDIDialog;
import org.mdiutil.swing.GenericDialog;

/**
 * Represents a window which will show a list of elements linked to a selecetd element in the trees.
 *
 * @param <E> the NamedOwlElement from which the panel is shown
 * @param <B> the type of element in the list
 * @since 0.5
 */
public abstract class AbstractShowLinksDialog<E extends NamedOwlElement, B> extends GenericDialog implements MDIDialog {
   protected final E element;
   protected final GraphPanel panel;
   protected JList<B> list;
   protected DefaultListModel<B> model = new DefaultListModel<>();

   public AbstractShowLinksDialog(E element, GraphPanel panel, Component parent, String title) {
      super(title + " " + element.toString());
      this.element = element;
      this.panel = panel;
      this.setResizable(true);
   }

   protected abstract void initializeList();

   protected void createList() {
      list = new LinkList<>(model);

      list.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
               rightClickOnList(e.getX(), e.getY());
            }
         }
      });
   }

   protected abstract void rightClickOnList(int x, int y);

   @Override
   protected void createPanel() {
      Container pane = dialog.getContentPane();
      pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
      pane.add(Box.createVerticalStrut(5));
      initializeList();

      // create the list panel
      JPanel listPanel = new JPanel();
      listPanel.setLayout(new BorderLayout());
      listPanel.add(new JScrollPane(list), BorderLayout.CENTER);
      pane.add(listPanel);

      JPanel okpanel = this.createOKPanel();
      pane.add(okpanel);
   }

   private class LinkList<E> extends JList {
      public LinkList(ListModel<E> dataModel) {
         super(dataModel);
      }

      @Override
      public Dimension getPreferredScrollableViewportSize() {
         return new Dimension(400, 200);
      }
   }
}
