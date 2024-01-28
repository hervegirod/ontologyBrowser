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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
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
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.restriction.OwlRestriction;
import org.girod.ontobrowser.model.ElementFilter;
import org.girod.ontobrowser.utils.SchemaUtils;
import org.mdi.bootstrap.swing.MDIDialog;
import org.mdiutil.swing.GenericDialog;

/**
 * This Dialog shows the dependencies of an element.
 *
 * @version 0.8
 */
public class ShowDependenciesDialog extends GenericDialog implements MDIDialog {
   private NamedOwlElement element;
   private final GraphPanel panel;
   private final OwlSchema schema;
   private JList<Object> list;
   private final boolean autoRefresh;
   private final DefaultListModel<Object> model = new DefaultListModel<>();

   public ShowDependenciesDialog(NamedOwlElement element, GraphPanel panel, Component parent, boolean autoRefresh) {
      super("Dependencies of " + element.toString());
      this.element = element;
      this.panel = panel;
      this.schema = panel.getSchema();
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
      boolean includeParentRelations = BrowserConfiguration.getInstance().includeParentRelations;
      boolean includeAlias = BrowserConfiguration.getInstance().includeAlias;
      ElementFilter properties = new ElementFilter();
      properties.includeParentRelations = includeParentRelations;
      properties.includeAlias = includeAlias;
      if (element instanceof OwlIndividual) {
         // Classes of the Individual
         model.addElement("Classes");
         OwlIndividual individual = (OwlIndividual) element;
         SortedMap<ElementKey, OwlClass> map = new TreeMap<>(individual.getParentClasses());
         Iterator<OwlClass> it = map.values().iterator();
         while (it.hasNext()) {
            OwlClass theClass = it.next();
            model.addElement(theClass);
         }
         if (individual.isInEquivalentExpressions()) {
            model.addElement("Used in Equivalent Expressions");
            map = new TreeMap<>(individual.getInEquivalentExpressions());
            it = map.values().iterator();
            while (it.hasNext()) {
               OwlClass theClass = it.next();
               model.addElement(theClass);
            }
         }
      } else if (element instanceof OwlClass) {
         OwlClass theClass = (OwlClass) element;
         // data properties of the Class
         model.addElement("Data Properties");
         SortedMap<ElementKey, OwlProperty> mapp = new TreeMap<>(SchemaUtils.getDataProperties(theClass, properties));
         Iterator<OwlProperty> itp = mapp.values().iterator();
         while (itp.hasNext()) {
            OwlProperty property = itp.next();
            if (property instanceof OwlDatatypeProperty) {
               model.addElement(new PropertyBridge(property, true));
            }
         }
         // domain properties of the Class
         model.addElement("Object Properties Domain");
         mapp = new TreeMap<>(SchemaUtils.getDomainProperties(theClass, properties));
         itp = mapp.values().iterator();
         while (itp.hasNext()) {
            OwlProperty property = itp.next();
            if (property instanceof OwlObjectProperty) {
               model.addElement(new PropertyBridge(property, false));
            }
         }
         // range properties of the Class
         model.addElement("Object Properties Range");
         mapp = new TreeMap<>(SchemaUtils.getRangeProperties(theClass, properties));
         itp = mapp.values().iterator();
         while (itp.hasNext()) {
            OwlProperty property = itp.next();
            if (property instanceof OwlObjectProperty) {
               model.addElement(new PropertyBridge(property, true));
            }
         }
         if (theClass.hasSuperClasses()) {
            // super Classes
            model.addElement("Super-Classes");
            SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getSuperClasses());
            Iterator<OwlClass> itc = mapc.values().iterator();
            while (itc.hasNext()) {
               OwlClass parentClass = itc.next();
               model.addElement(parentClass);
            }
         }
         if (theClass.hasSubClasses()) {
            // sub Classes
            model.addElement("Sub-Classes");
            SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getSubClasses());
            Iterator<OwlClass> itc = mapc.values().iterator();
            while (itc.hasNext()) {
               OwlClass childClass = itc.next();
               model.addElement(childClass);
            }
         }         

         // equivalent classes
         if (theClass.hasAliasClasses()) {
            model.addElement("Alias Classes");
            SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getAliasClasses());
            Iterator<OwlClass> iti = mapc.values().iterator();
            while (iti.hasNext()) {
               OwlClass alias = iti.next();
               model.addElement(alias);
            }
         }
         if (theClass.hasFromAliasedClasses()) {
            model.addElement("Aliased Classes");
            SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getFromAliasClasses());
            Iterator<OwlClass> iti = mapc.values().iterator();
            while (iti.hasNext()) {
               OwlClass aliased = iti.next();
               model.addElement(aliased);
            }
         }
         if (theClass.hasEquivalentExpressions()) {
            model.addElement("Use in Equivalent Expressions");
            SortedMap<ElementKey, NamedOwlElement> mapc = new TreeMap<>(theClass.getElementsInEquivalentExpressions());
            Iterator<NamedOwlElement> itc = mapc.values().iterator();
            while (itc.hasNext()) {
               NamedOwlElement theElement = itc.next();
               model.addElement(theElement);
            }
         }
         if (theClass.isInEquivalentExpressions()) {
            model.addElement("Used in Equivalent Expressions");
            SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theClass.getInEquivalentExpressions());
            Iterator<OwlClass> it = mapc.values().iterator();
            while (it.hasNext()) {
               OwlClass eqClass = it.next();
               model.addElement(eqClass);
            }
         }
         Map<ElementKey, OwlClass> mapd = schema.getDependentClasses(theClass);
         if (!mapd.isEmpty()) {
            model.addElement("Associated Classes");
            SortedMap<ElementKey, OwlClass> mapd2 = new TreeMap<>(mapd);
            Iterator<OwlClass> iti = mapd2.values().iterator();
            while (iti.hasNext()) {
               OwlClass class2 = iti.next();
               model.addElement(class2);
            }
         }

         // individuals of the Class
         model.addElement("Individuals");
         SortedMap<ElementKey, OwlIndividual> mapi = new TreeMap<>(theClass.getIndividuals());
         Iterator<OwlIndividual> iti = mapi.values().iterator();
         while (iti.hasNext()) {
            OwlIndividual individual = iti.next();
            model.addElement(individual);
         }
      } else if (element instanceof OwlProperty) {
         OwlProperty theProperty = (OwlProperty) element;
         
         if (theProperty.hasSuperProperties()) {
            // super properties
            model.addElement("Super-Properties");
            SortedMap<ElementKey, OwlProperty> mapp = new TreeMap<>(theProperty.getSuperProperties());
            Iterator<OwlProperty> itc = mapp.values().iterator();
            while (itc.hasNext()) {
               OwlProperty parentProperty = itc.next();
               model.addElement(parentProperty);
            }
         }
         if (theProperty.hasSubProperties()) {
            // sub properties
            model.addElement("Sub-Properties");
            SortedMap<ElementKey, OwlProperty> mapp = new TreeMap<>(theProperty.getSubProperties());
            Iterator<OwlProperty> itc = mapp.values().iterator();
            while (itc.hasNext()) {
               OwlProperty childProperty = itc.next();
               model.addElement(childProperty);
            }
         }           

         // equivalent properties
         if (theProperty.hasEquivalentProperties()) {
            model.addElement("Alias Properties");
            SortedMap<ElementKey, OwlProperty> mapc = new TreeMap<>(theProperty.getEquivalentProperties());
            Iterator<OwlProperty> iti = mapc.values().iterator();
            while (iti.hasNext()) {
               OwlProperty alias = iti.next();
               model.addElement(alias);
            }
         }
         if (theProperty.hasFromAliasedProperties()) {
            model.addElement("Aliased Properties");
            SortedMap<ElementKey, OwlProperty> mapc = new TreeMap<>(theProperty.getFromAliasProperties());
            Iterator<OwlProperty> iti = mapc.values().iterator();
            while (iti.hasNext()) {
               OwlProperty aliased = iti.next();
               model.addElement(aliased);
            }
         }
         // Classes of the domain
         model.addElement("Domain Classes");
         SortedMap<ElementKey, OwlRestriction> map = new TreeMap<>(theProperty.getDomain());
         Iterator<OwlRestriction> it = map.values().iterator();
         while (it.hasNext()) {
            OwlRestriction restriction = it.next();
            model.addElement(restriction.getOwlClass());
         }
         if (theProperty instanceof OwlObjectProperty) {
            OwlObjectProperty theObjectProperty = (OwlObjectProperty) element;
            // Classes of the range
            model.addElement("Range Classes");
            map = new TreeMap<>(theObjectProperty.getRange());
            it = map.values().iterator();
            while (it.hasNext()) {
               OwlRestriction restriction = it.next();
               model.addElement(restriction.getOwlClass());
            }

            // Inverse property
            OwlObjectProperty theInverseProperty = theObjectProperty.getInverseProperty();
            if (theInverseProperty != null) {
               model.addElement("Inverse Property");
               model.addElement(theInverseProperty);
            }
            if (theProperty.isInEquivalentExpressions()) {
               model.addElement("Used in Equivalent Expressions");
               SortedMap<ElementKey, OwlClass> mapc = new TreeMap<>(theProperty.getInEquivalentExpressions());
               Iterator<OwlClass> it2 = mapc.values().iterator();
               while (it2.hasNext()) {
                  OwlClass eqClass = it2.next();
                  model.addElement(eqClass);
               }
            }
         }
      }

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
