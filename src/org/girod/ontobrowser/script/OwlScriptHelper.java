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
package org.girod.ontobrowser.script;

import java.util.Map;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.ElementFilter;
import org.girod.ontobrowser.utils.SchemaUtils;
import org.scripthelper.context.ScriptContext;
import org.scripthelper.context.ScriptHelper;

/**
 * The script helper.
 *
 * @since 0.8
 */
public class OwlScriptHelper implements ScriptHelper {
   private final OwlScriptContext context;

   public OwlScriptHelper(OwlScriptContext context) {
      this.context = context;
   }

   @Override
   public ScriptContext getContext() {
      return context;
   }

   /**
    * Return the data properties of a Class.
    *
    * @param theClass the Class
    * @param filter the request filter
    * @return the data properties
    */
   public static Map<ElementKey, OwlProperty> getDataProperties(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getDataProperties(theClass, filter);
   }

   /**
    * Return the properties for which this Class is in their domain.
    *
    * @param theClass the Class
    * @param filter the request properties
    * @return the properties for which this Class is in their domain
    */
   public static Map<ElementKey, OwlProperty> getDomainProperties(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getDomainProperties(theClass, filter);
   }

   /**
    * Return the properties for which this Class is in their range.
    *
    * @param theClass the Class
    * @param filter the request filter
    * @return the properties for which this Class is in their range
    */
   public Map<ElementKey, OwlProperty> getRangeProperties(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getRangeProperties(theClass, filter);
   }

   /**
    * Return the classes dependant from a class.
    *
    * @param theClass the class
    * @param filter the filter
    * @return the dependant classes
    */
   public static Map<ElementKey, OwlClass> getDependentClasses(OwlClass theClass, ElementFilter filter) {
      return SchemaUtils.getDependentClasses(theClass, filter);
   }
}
