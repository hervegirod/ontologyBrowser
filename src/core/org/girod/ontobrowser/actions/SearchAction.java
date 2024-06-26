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
package org.girod.ontobrowser.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.girod.ontobrowser.MenuFactory;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.gui.search.SearchOptions;
import org.girod.ontobrowser.gui.search.SearchResultDialog;
import org.girod.ontobrowser.gui.search.UneditableTableModel;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.ElementTypes;
import org.girod.ontobrowser.model.NamedOwlElement;
import org.girod.ontobrowser.model.OwlAnnotation;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlDatatypeProperty;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlObjectProperty;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.mdi.bootstrap.swing.GUIApplication;

/**
 * The Action that search for elements.
 *
 * @version 0.12
 */
public class SearchAction extends AbstractMDIAction {
   private final String category;
   private final List<String> categories;
   private String searchString;
   private final String searchText;
   private final boolean regex;
   private final boolean matchCase;
   private final GraphPanel graphPanel;
   private final OwlSchema schema;
   private final boolean indirectRelations;
   private final UneditableTableModel model;

   public SearchAction(MDIApplication app, GraphPanel graphPanel, boolean indirectRelations, SearchOptions options) {
      super(app, "Search");
      this.setDescription("Search", "Search");
      this.graphPanel = graphPanel;
      this.schema = graphPanel.getSchema();
      this.indirectRelations = indirectRelations;
      this.categories = options.categories;
      this.category = options.category;
      this.searchText = options.searchString;
      this.regex = options.regex;
      this.matchCase = options.matchCase;
      // set the column names and create the table model
      Vector<String> colNames = new Vector<>();
      colNames.add("Name");
      colNames.add("Type");
      model = new UneditableTableModel(colNames, 0);
   }

   private String getName(NamedOwlElement elt) {
      return elt.getDisplayedName();
   }

   public List<String> getCategories() {
      return categories;
   }

   public String getSelectedCategory() {
      return category;
   }

   public DefaultTableModel getModel() {
      return model;
   }

   @Override
   public void run() throws Exception {
      search();
   }

   private Pattern createPattern(String patS) throws PatternSyntaxException {
      Pattern pat;
      if (!matchCase) {
         pat = Pattern.compile(patS, Pattern.CASE_INSENSITIVE);
      } else {
         pat = Pattern.compile(patS);
      }
      return pat;
   }

   private Pattern getPattern() {
      Pattern pat = null;
      searchString = searchText;
      if (regex) {
         try {
            pat = createPattern(searchString);
         } catch (PatternSyntaxException e) {
         }
      } else {
         StringBuilder buf = new StringBuilder();
         for (int i = 0; i < searchString.length(); i++) {
            switch (searchString.charAt(i)) {
               case '*':
                  buf.append(".*");
                  break;
               case '.':
                  buf.append("\\.");
                  break;
               case '(':
                  buf.append("\\(");
                  break;
               case ')':
                  buf.append("\\)");
                  break;
               case '[':
                  buf.append("\\[");
                  break;
               case ']':
                  buf.append("\\]");
                  break;
               default:
                  buf.append(searchString.charAt(i));
                  break;
            }
         }
         searchString = buf.toString();
         try {
            pat = createPattern(searchString);
         } catch (PatternSyntaxException e) {
         }
      }
      return pat;
   }

   private String getCategory(NamedOwlElement elt) {
      if (elt instanceof OwlClass) {
         return ElementTypes.CLASS;
      } else if (elt instanceof OwlIndividual) {
         return ElementTypes.INDIVIDUAL;
      } else if (elt instanceof OwlObjectProperty) {
         return ElementTypes.OBJECTPROPERTY;
      } else if (elt instanceof OwlDatatypeProperty) {
         return ElementTypes.DATAPROPERTY;
      } else if (elt instanceof OwlAnnotation) {
         return ElementTypes.ANNOTATION;
      } else {
         return null;
      }
   }

   private void addAll(List<NamedOwlElement> list) {
      addClasses(list);
      addObjectProperties(list);
      addDatatypesProperties(list);
      addIndividuals(list);
   }

   private void addClasses(List<NamedOwlElement> list) {
      Map<ElementKey, OwlClass> map = schema.getOwlClasses();
      Iterator<OwlClass> it = map.values().iterator();
      while (it.hasNext()) {
         OwlClass owlClass = it.next();
         list.add(owlClass);
         if (indirectRelations) {
            if (owlClass.hasAliasClasses()) {
               Iterator<OwlClass> it2 = owlClass.getAliasClasses().values().iterator();
               while (it2.hasNext()) {
                  OwlClass theClass = it2.next();
                  list.add(theClass);
               }
            }
            if (owlClass.hasFromAliasedClasses()) {
               Iterator<OwlClass> it2 = owlClass.getFromAliasClasses().values().iterator();
               while (it2.hasNext()) {
                  OwlClass theClass = it2.next();
                  list.add(theClass);
               }
            }
         }
      }
   }

   private void addObjectProperties(List<NamedOwlElement> list) {
      Map<ElementKey, OwlObjectProperty> map = schema.getOwlObjectProperties();
      Iterator<OwlObjectProperty> it = map.values().iterator();
      while (it.hasNext()) {
         OwlObjectProperty property = it.next();
         list.add(property);
         if (indirectRelations) {
            addAliasedProperties(list, property);
         }
      }
   }

   private void addAliasedProperties(List<NamedOwlElement> list, OwlProperty property) {
      if (property.hasAliasProperties()) {
         Iterator<OwlProperty> it2 = property.getAliasProperties().values().iterator();
         while (it2.hasNext()) {
            OwlProperty theProperty = it2.next();
            list.add(theProperty);
         }
      }
      if (property.hasFromAliasedProperties()) {
         Iterator<OwlProperty> it2 = property.getFromAliasProperties().values().iterator();
         while (it2.hasNext()) {
            OwlProperty theProperty = it2.next();
            list.add(theProperty);
         }
      }
   }

   private void addDatatypesProperties(List<NamedOwlElement> list) {
      Map<ElementKey, OwlDatatypeProperty> map = schema.getOwlDatatypeProperties();
      Iterator<OwlDatatypeProperty> it = map.values().iterator();
      while (it.hasNext()) {
         OwlDatatypeProperty property = it.next();
         list.add(property);
         if (indirectRelations) {
            addAliasedProperties(list, property);
         }
      }
   }

   private void addAnnotations(List<NamedOwlElement> list) {
      Map<ElementKey, OwlAnnotation> map = schema.getElementAnnotations();
      Iterator<OwlAnnotation> it = map.values().iterator();
      while (it.hasNext()) {
         OwlAnnotation annotation = it.next();
         list.add(annotation);
      }
   }

   private void addIndividuals(List<NamedOwlElement> list) {
      Map<ElementKey, OwlIndividual> map = schema.getIndividuals();
      Iterator<OwlIndividual> it = map.values().iterator();
      while (it.hasNext()) {
         OwlIndividual individual = it.next();
         list.add(individual);
      }
   }

   /**
    * Return the list of {@link inter.model.Element}s that fulfill the conditions.
    *
    * @param pat the Pattern used to look for the Element name (short of long names)
    * @param cat the category name, as of the {@link #getElementsCategories} method
    * @return the list
    */
   private List<NamedOwlElement> getList(Pattern pat, String cat) {
      // first get the List of all Elements of the defined category
      List<NamedOwlElement> list = new ArrayList<>();
      if (cat == null || cat.equals(ElementTypes.ALL)) {
         addAll(list);
      } else if (cat.equals(ElementTypes.CLASS)) {
         addClasses(list);
      } else if (cat.equals(ElementTypes.PROPERTY)) {
         addObjectProperties(list);
         addDatatypesProperties(list);
      } else if (cat.equals(ElementTypes.OBJECTPROPERTY)) {
         addObjectProperties(list);
      } else if (cat.equals(ElementTypes.DATAPROPERTY)) {
         addDatatypesProperties(list);
      } else if (cat.equals(ElementTypes.ANNOTATION)) {
         addAnnotations(list);
      } else if (cat.equals(ElementTypes.INDIVIDUAL)) {
         addIndividuals(list);
      }
      // then get only those who match the pattern
      List<NamedOwlElement> outputList = new ArrayList<>();
      Iterator<NamedOwlElement> it = list.iterator();
      while (it.hasNext()) {
         NamedOwlElement elt = it.next();
         String name = getName(elt);
         if (name != null) {
            Matcher matcher = pat.matcher(getName(elt));
            if (matcher.matches()) {
               outputList.add(elt);
            }
         }
      }
      return outputList;
   }

   private void search() {
      Pattern pat = getPattern();
      if (pat != null) {
         List<NamedOwlElement> list = getList(pat, category);
         Iterator<NamedOwlElement> it = list.iterator();
         while (it.hasNext()) {
            NamedOwlElement elt = it.next();
            DefaultMutableTreeNode node = graphPanel.getNode(elt);
            if (node != null) {
               Result result = new Result(elt.getKey(), elt.getElementType(), new TreePath(node.getPath()));
               Vector v = new Vector(2);
               v.add(result);
               v.add(getCategory(elt));
               model.addRow(v);
            }
         }
      }
   }

   private String getSearchTitle() {
      String title = "Search Results";
      int maxLength = title.length() + 10;
      if (!regex) {
         title += " for " + searchText;
      }
      if (title.length() > maxLength) {
         title = title.substring(0, maxLength) + "...";
      }
      return title;
   }

   @Override
   public void endAction() {
      if (model != null) {
         MenuFactory fac = (MenuFactory) ((GUIApplication) app).getMenuFactory();
         String title = getSearchTitle();
         SearchResultDialog dialog = new SearchResultDialog(((GUIApplication) app).getApplicationWindow(), fac, model, graphPanel, title);
         dialog.showDialog();
      } else {
         JOptionPane.showMessageDialog(((GUIApplication) app).getApplicationWindow(), "Bad Search String", "Error", JOptionPane.ERROR_MESSAGE);
      }
   }

   @Override
   public String getMessage() {
      return null;
   }

   /**
    * A Search Result action.
    *
    * @since 0.5
    */
   public static class Result {
      public ElementKey key;
      public String elementType;
      public TreePath path;

      public Result(ElementKey key, String elementType, TreePath path) {
         this.key = key;
         this.elementType = elementType;
         this.path = path;
      }

      public ElementKey getKey() {
         return key;
      }

      public String getElementType() {
         return elementType;
      }

      @Override
      public String toString() {
         return key.getName();
      }
   }
}
