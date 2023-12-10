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
package org.girod.ontobrowser.actions;

import java.util.ArrayList;
import java.util.List;
import org.girod.ontobrowser.BrowserConfiguration;
import org.girod.ontobrowser.gui.errors.ErrorLevel;
import org.girod.ontobrowser.gui.errors.ParserLog;

/**
 * Represents an action which can emit an information, an error or a warning.
 *
 * @since 0.7
 */
public abstract class AbstractWarningAction {
   private short logLevel = ErrorLevel.NO_LOGGING;
   private short currentLevel = ErrorLevel.NO_LOGGING;
   private final List<ParserLog> errors = new ArrayList<>();

   public AbstractWarningAction() {
      this.logLevel = BrowserConfiguration.getInstance().logLevel;
   }

   public boolean hasErrors() {
      return !errors.isEmpty();
   }

   public short getErrorLevel() {
      return currentLevel;
   }

   public List<ParserLog> getErrors() {
      return errors;
   }

   public void addLog(short level, String key) {
      if (level >= logLevel) {
         ParserLog error = new ParserLog(level, key);
         errors.add(error);
         if (currentLevel < level) {
            currentLevel = level;
         }
      }
   }

   public void addLog(short level, String key, Object... vars) {
      if (level >= logLevel) {
         ParserLog error = new ParserLog(level, key, vars);
         errors.add(error);
         if (currentLevel < level) {
            currentLevel = level;
         }
      }
   }

   public void addError(String key) {
      addLog(ErrorLevel.ERROR, key);
   }

   public void addError(String key, Object... vars) {
      addLog(ErrorLevel.ERROR, key, vars);
   }

   public void addWarning(String key) {
      addLog(ErrorLevel.WARNING, key);
   }

   public void addWarning(String key, Object... vars) {
      addLog(ErrorLevel.WARNING, key, vars);
   }

   public void addInfo(String key) {
      addLog(ErrorLevel.INFO, key);
   }

   public void addInfo(String key, Object... vars) {
      addLog(ErrorLevel.INFO, key, vars);
   }
}
