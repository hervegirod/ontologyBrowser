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
package org.girod.ontobrowser.actions;

import com.mxgraph.model.mxCell;
import org.girod.jgraphml.model.Arrows;
import org.girod.jgraphml.model.GraphMLEdge;
import org.girod.ontobrowser.model.OwlObjectProperty;

/**
 * This class has some utilities to export diagrams.
 *
 * @since 0.15
 */
public class ExportUtils {
   private ExportUtils() {
   }

   /**
    * Set the arrows on an cell for an object property.
    *
    * @param cell the cell
    * @param property the object property
    */
   public static void setCellStyle(mxCell cell, OwlObjectProperty property) {
      if (property.hasInverseProperty()) {
         cell.setStyle("inverseproperty");
      } else {
         cell.setStyle("property");
      }
   }

   /**
    * Set the arrows on an edge for an object property.
    *
    * @param edge the edge
    * @param property the object property
    * @return the arrows
    */
   public static Arrows setArrows(GraphMLEdge edge, OwlObjectProperty property) {
      Arrows arrows = edge.getArrows();
      if (property.hasInverseProperty()) {
         arrows.setSource(Arrows.STANDARD);
         arrows.setTarget(Arrows.STANDARD);
      } else {
         arrows.setSource(Arrows.STANDARD);
         arrows.setTarget(Arrows.NONE);
      }
      return arrows;
   }

   /**
    * Return the displayed label on an edge for an object property.
    *
    * @param property the object property
    * @return the displayed label
    */
   public static String getDisplayedLabel(OwlObjectProperty property) {
      if (property.hasInverseProperty()) {
         return "\u2190" + property.getInverseProperty().getDisplayedName() + " / " + property.getDisplayedName() + "\u2192";
      } else {
         return property.getDisplayedName();
      }
   }
}
