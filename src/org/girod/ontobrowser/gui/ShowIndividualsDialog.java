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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlIndividual;

/**
 * This Dialog shows the individuals of a Class.
 *
 * @since 0.5
 */
public class ShowIndividualsDialog extends AbstractShowLinksDialog<OwlClass, OwlIndividual> {

   public ShowIndividualsDialog(OwlClass owlClass, GraphPanel panel, Component parent) {
      super(owlClass, panel, parent, "Individuals of");
   }

   @Override
   protected void initializeList() {
      // individuals of the Class
      SortedMap<ElementKey, OwlIndividual> map = new TreeMap<>(element.getIndividuals());
      Iterator<Entry<ElementKey, OwlIndividual>> it = map.entrySet().iterator();
      while (it.hasNext()) {
         Entry<ElementKey, OwlIndividual> entry = it.next();
         OwlIndividual individual = entry.getValue();
         model.addElement(individual);
      }

      this.createList();
      list.setCellRenderer(new IndividualListCellRenderer());
   }

   @Override
   protected void rightClickOnList(int x, int y) {
      Object o = list.getSelectedValue();
      if (o != null) {
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
      }
   }

   private void gotoIndividual(OwlIndividual selectedIndividual) {
      ElementKey key = selectedIndividual.getKey();
      panel.selectIndividual(key);
   }
}
