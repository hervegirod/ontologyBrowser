/*
Copyright (c) 2021, 2023 Hervé Girod
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
package org.girod.ontobrowser.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import org.girod.ontobrowser.BrowserConfiguration;

/**
 * Utilities methods to handle labels in the output graph.
 *
 * @version 0.4
 */
public class LabelUtils {
   private static final AffineTransform TRANSFORM = new AffineTransform();
   private static final FontRenderContext FRC = new FontRenderContext(TRANSFORM, true, true);

   private LabelUtils() {
   }

   private static Dimension getMinimumSize(String label, int fontSize, String fontFamily) {
      Font font = new Font(fontFamily, Font.PLAIN, fontSize);
      int textwidth = (int) (font.getStringBounds(label, FRC).getWidth());
      int textheight = (int) (font.getStringBounds(label, FRC).getHeight());
      return new Dimension(textwidth, textheight);
   }

   /**
    * Return the dimension of a label.
    *
    * @param label the label
    * @param fontSize the font size
    * @param fontFamily the font family
    * @return the dimension
    */
   public static Dimension getDimension(String label, int fontSize, String fontFamily) {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      if (label != null && !label.isEmpty()) {
         Dimension size = getMinimumSize(label, fontSize, fontFamily);
         int width = size.width + conf.padWidth;
         int height = size.height + conf.padHeight;
         return new Dimension(width, height);
      } else {
         return new Dimension(conf.padWidth, conf.padHeight);
      }
   }
}
