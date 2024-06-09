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

import java.io.File;
import org.girod.ontobrowser.OwlDiagram;
import org.girod.ontobrowser.gui.GraphPanel;
import org.girod.ontobrowser.model.ElementKey;
import org.girod.ontobrowser.model.ElementVisitor;
import org.girod.ontobrowser.model.NamedElement;
import org.girod.ontobrowser.model.OwlClass;
import org.girod.ontobrowser.model.OwlIndividual;
import org.girod.ontobrowser.model.OwlProperty;
import org.girod.ontobrowser.model.OwlSchema;
import org.girod.ontobrowser.model.PropertyValue;
import org.girod.ontobrowser.script.OwlScriptContext;
import org.girod.ontobrowser.script.Script;
import org.mdi.bootstrap.MDIApplication;
import org.mdi.bootstrap.swing.AbstractMDIAction;
import org.scripthelper.context.LinkIndexConverter;
import org.scripthelper.context.ScriptLoggerHyperLinkListener;
import org.scripthelper.exceptions.ScriptExceptionListener;
import org.scripthelper.swing.SwingExceptionListener;

/**
 * An absract script avtion, used for running and debugging scripts.
 *
 * @since 0.13
 */
public abstract class AbstractScriptAction extends AbstractMDIAction implements ElementVisitor, ScriptLoggerHyperLinkListener, LinkIndexConverter {
   protected File scriptFile = null;
   protected Script script = null;
   protected String scriptName = null;
   protected GraphPanel graphPanel = null;
   protected OwlDiagram diagram = null;
   protected OwlSchema schema = null;
   protected OwlScriptContext context = null;
   protected ScriptExceptionListener exListener = null;
   protected boolean endAtFirstException = false;
   protected boolean isScriptAborted = false;

   /**
    * Create a Script Action.
    *
    * @param appli the Application
    * @param scriptFile the Script File
    * @param graphPanel the graph panel
    */
   public AbstractScriptAction(MDIApplication appli, File scriptFile, GraphPanel graphPanel) {
      super(appli, "Apply Script");
      this.setDescription("Apply Script", "Apply Script");
      this.scriptFile = scriptFile;
      this.graphPanel = graphPanel;
      this.diagram = graphPanel.getDiagram();
      this.schema = graphPanel.getSchema();
   }

   /**
    * Encapsulation of the {@link org.girod.ontobrowser.script.Script#start(org.girod.ontobrowser.model.OwlSchema)} code to process exceptions which can be thrown by the Script.
    *
    * @return true if the Script has not been aborted
    */
   protected boolean startScript() {
      if (!isScriptAborted()) {
         script.start(schema);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Encapsulation of the {@link org.girod.ontobrowser.script.Script#end()} code to process exceptions which can be thrown by the Script.
    *
    * @return true
    */
   protected boolean endScript() {
      script.end();
      return true;
   }

   protected boolean isScriptAborted() {
      if (!endAtFirstException) {
         return false;
      } else if (isScriptAborted) {
         return true;
      } else {
         SwingExceptionListener swingListener = (SwingExceptionListener) exListener;
         isScriptAborted = swingListener.getScriptState() != SwingExceptionListener.STATE_OK;
         return isScriptAborted;
      }
   }

   /**
    * Encapsulate the Script execution on one Element in order to catch and show the possible thrown Exception.
    *
    * @param element the element
    * @return true if the next element should be processsed (true except for certain kinds of errors when executingfthe Script)
    */
   protected boolean processScript(NamedElement element) {
      script.process(element);
      return true;
   }

   /**
    * Visit an Element. Return true by default.
    *
    * @param element the element
    * @return true if the children of the elements should also be visited, and the script has not been aborted
    */
   @Override
   public boolean visit(NamedElement element) {
      if (!isScriptAborted()) {
         ((OwlScriptContext) context).setCurrentElement(element);
         boolean result = processScript(element);
         return result;
      } else {
         return false;
      }
   }

   @Override
   public String getLinkFromObject(Object o) {
      if (o instanceof NamedElement) {
         NamedElement element = (NamedElement) o;
         if (element instanceof OwlClass) {
            ElementKey theKey = element.getKey();
            if (theKey.isThing()) {
               return null;
            } else {
               return "CLASS$" + element.getKey().toString();
            }
         } else if (element instanceof OwlProperty) {
            return "PROPERTY$" + element.getKey().toString();
         } else if (element instanceof OwlIndividual) {
            return "INDIVIDUAL$" + element.getKey().toString();
         } else if (element instanceof PropertyValue) {
            PropertyValue value = (PropertyValue) element;
            ElementKey propKey = value.getProperty().getKey();
            return "PROPERTYVALUE$" + element.getKey().toString() + "$" + propKey.toString();
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public Object getObjectFromLink(String linkDesc) {
      int index = linkDesc.indexOf('$');
      if (index != -1) {
         String objectType = linkDesc.substring(0, index);
         String linkValue = linkDesc.substring(index + 1);
         switch (objectType) {
            case "CLASS":
               ElementKey theKey = ElementKey.createFromURI(linkValue);
               return schema.getOwlClass(theKey);
            case "PROPERTY":
               theKey = ElementKey.createFromURI(linkValue);
               return schema.getOwlProperty(theKey);
            case "INDIVIDUAL":
               theKey = ElementKey.createFromURI(linkValue);
               return schema.getIndividual(theKey);
            case "PROPERTYVALUE":
               index = linkValue.indexOf('$');
               if (index != -1) {
                  String elementLink = linkValue.substring(0, index);
                  String propertyLink = linkValue.substring(index + 1);
                  theKey = ElementKey.createFromURI(elementLink);
                  ElementKey propertyKey = ElementKey.createFromURI(propertyLink);
                  OwlIndividual theIndividual = schema.getIndividual(theKey);
                  if (theIndividual != null) {
                     return theIndividual.getPropertyValues(propertyKey).get(0);
                  } else {
                     return null;
                  }
               } else {
                  return null;
               }
            default:
               return null;
         }
      } else {
         return null;
      }
   }

   /**
    * Visit an hyperlink.
    *
    * @param o the hyperlink associated Object
    */
   @Override
   public void visitHyperLink(Object o) {
      if (o instanceof OwlClass) {
         OwlClass owlClass = (OwlClass) o;
         graphPanel.selectClass(owlClass.getKey());
      } else if (o instanceof OwlProperty) {
         OwlProperty owlProperty = (OwlProperty) o;
         graphPanel.selectProperty(owlProperty.getKey());
      } else if (o instanceof OwlIndividual) {
         OwlIndividual owlIndividual = (OwlIndividual) o;
         graphPanel.selectIndividual(owlIndividual.getKey());
      } else if (o instanceof PropertyValue) {
         PropertyValue theValue = (PropertyValue) o;
         graphPanel.selectIndividual(theValue.getSource().getKey());
      }
   }
}
