
import org.scripthelper.context.ScriptContext;
import org.scripthelper.context.HyperlinkElement;
import org.girod.ontobrowser.script.OwlScriptContext;
import org.scripthelper.context.DefaultScriptLogger;
import org.girod.ontobrowser.script.OwlScriptHelper;
import java.util.*;
import org.girod.ontobrowser.model.*;
import org.girod.ontobrowser.model.restriction.*;

class GroovyClass implements org.girod.ontobrowser.script.Script, org.scripthelper.context.ContextListener {
   OwlScriptContext context;
   OwlScriptHelper helper;
   DefaultScriptLogger logger;

   public void init(ScriptContext ctx) {
      context = (OwlScriptContext) ctx;
      helper = (OwlScriptHelper) ctx.getHelper();
      logger = (DefaultScriptLogger) ctx.getScriptLogger();
   }
   Map<ElementKey, Boolean> classes = new HashMap<>();

   void process(NamedElement element) {
      context.__step__(3, element, this.context, this.helper, this.logger);
      HyperlinkElement hyperLinkElt = new HyperlinkElement(element.getPrefixedDisplayedName(), element);
      context.__step__(4, element, hyperLinkElt, this.context, this.helper, this.logger);
      if (element.getElementType().equals(ElementTypes.CLASS)) {
         context.__step__(5, element, hyperLinkElt, this.context, this.helper, this.logger);
         OwlClass theClass = (OwlClass) element;
         context.__step__(6, element, hyperLinkElt, this.context, this.helper, this.logger);
         ElementKey key = theClass.getKey();
         context.__step__(7, element, hyperLinkElt, this.context, this.helper, this.logger);
         if (key.isThing()) {
            context.__step__(8, element, hyperLinkElt, this.context, this.helper, this.logger);
            return;
         }
         context.__step__(10, element, hyperLinkElt, this.context, this.helper, this.logger);
         if (!checkClassProperties(theClass, null)) {
            context.__step__(11, element, hyperLinkElt, this.context, this.helper, this.logger);
            logger.appendObjectLinks("Class %1 has no properties", hyperLinkElt);
         }
         context.__step__(13, element, hyperLinkElt, this.context, this.helper, this.logger);
      } else if (element.getElementType().equals(ElementTypes.PROPERTY)) {
         context.__step__(14, element, hyperLinkElt, this.context, this.helper, this.logger);
         OwlProperty theProperty = (OwlProperty) element;
         context.__step__(15, element, hyperLinkElt, this.context, this.helper, this.logger);
         if (theProperty.isDatatypeProperty()) {
            context.__step__(16, element, hyperLinkElt, this.context, this.helper, this.logger);
            if (!hasDomain(theProperty)) {
               context.__step__(17, element, hyperLinkElt, this.context, this.helper, this.logger);
               logger.appendObjectLinks("DataProperty %1 has no Domain", hyperLinkElt);
            }
            context.__step__(19, element, hyperLinkElt, this.context, this.helper, this.logger);
         } else {
            context.__step__(1, this.context, this.helper, this.logger);
            context.__step__(20, element, hyperLinkElt, this.context, this.helper, this.logger);
            context.__step__(2, this.context, this.helper, this.logger);
            OwlObjectProperty theObjectProperty = (OwlObjectProperty) element;
            context.__step__(21, element, hyperLinkElt, this.context, this.helper, this.logger);
            if (!hasDomain(theProperty)) {
               context.__step__(22, element, hyperLinkElt, this.context, this.helper, this.logger);
               logger.appendObjectLinks("ObjectProperty %1 has no Domain", hyperLinkElt);
            }
            context.__step__(24, element, hyperLinkElt, this.context, this.helper, this.logger);
            context.__step__(9, element, hyperLinkElt, this.context, this.helper, this.logger);
            if (!hasRange(theProperty)) {
               context.__step__(25, element, hyperLinkElt, this.context, this.helper, this.logger);
               logger.appendObjectLinks("ObjectProperty %1 has no Range", hyperLinkElt);
               context.__step__(12, element, hyperLinkElt, this.context, this.helper, this.logger);
            }
         }
      }
   }

   boolean hasDomain(OwlProperty theProperty) {
      context.__step__(18, element, hyperLinkElt, this.context, this.helper, this.logger);
      context.__step__(32, theProperty, this.context, this.helper, this.logger);
      return hasDomain(theProperty, true);
   }

   boolean hasDomain(OwlProperty theProperty, boolean lookForInverse) {
      context.__step__(23, element, hyperLinkElt, this.context, this.helper, this.logger);
      context.__step__(36, lookForInverse, theProperty, this.context, this.helper, this.logger);
      if (!theProperty.hasDomain()) {
         context.__step__(37, lookForInverse, theProperty, this.context, this.helper, this.logger);
         context.__step__(26, element, hyperLinkElt, this.context, this.helper, this.logger);
         if (lookForInverse && theProperty instanceof OwlObjectProperty) {
            context.__step__(27, element, hyperLinkElt, this.context, this.helper, this.logger);
            context.__step__(38, lookForInverse, theProperty, this.context, this.helper, this.logger);
            context.__step__(28, element, hyperLinkElt, this.context, this.helper, this.logger);
            OwlObjectProperty objectProperty = (OwlObjectProperty) theProperty;
            context.__step__(29, element, hyperLinkElt, this.context, this.helper, this.logger);
            context.__step__(39, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
            context.__step__(30, element, hyperLinkElt, this.context, this.helper, this.logger);
            if (objectProperty.hasInverseProperty()) {
               context.__step__(31, element, hyperLinkElt, this.context, this.helper, this.logger);
               context.__step__(40, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
               objectProperty = objectProperty.getInverseProperty();
               context.__step__(33, theProperty, this.context, this.helper, this.logger);
               context.__step__(41, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
               context.__step__(34, theProperty, this.context, this.helper, this.logger);
               return hasDomain(objectProperty, false);
               context.__step__(35, theProperty, this.context, this.helper, this.logger);
            }
         }
         return false;
         context.__step__(45, lookForInverse, theProperty, this.context, this.helper, this.logger);
      } else {
         context.__step__(46, lookForInverse, theProperty, this.context, this.helper, this.logger);
         Map<ElementKey, OwlRestriction> map = theProperty.getDomain();
         context.__step__(42, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
         context.__step__(47, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         context.__step__(43, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
         Iterator<OwlRestriction> it = map.values().iterator();
         context.__step__(44, lookForInverse, theProperty, this.context, this.helper, this.logger);
         context.__step__(48, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         while (it.hasNext()) {
            context.__step__(49, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
            OwlRestriction restriction = it.next();
            context.__step__(50, it, lookForInverse, map, restriction, theProperty, this.context, this.helper, this.logger);
            OwlClass theClass = restriction.getOwlClass();
            context.__step__(51, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
            if (theClass != null && !theClass.isThing()) {
               context.__step__(52, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
               return true;
            }
         }
         context.__step__(55, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         if (lookForInverse && theProperty instanceof OwlObjectProperty) {
            context.__step__(56, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
            OwlObjectProperty objectProperty = (OwlObjectProperty) theProperty;
            context.__step__(57, it, lookForInverse, map, objectProperty, theProperty, this.context, this.helper, this.logger);
            if (objectProperty.hasInverseProperty()) {
               context.__step__(58, it, lookForInverse, map, objectProperty, theProperty, this.context, this.helper, this.logger);
               objectProperty = objectProperty.getInverseProperty();
               context.__step__(59, it, lookForInverse, map, objectProperty, theProperty, this.context, this.helper, this.logger);
               return hasRange(objectProperty, false);
            }
         }
         context.__step__(62, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         return false;
      }
   }

   boolean hasRange(OwlObjectProperty theProperty) {
      context.__step__(53, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
      context.__step__(67, theProperty, this.context, this.helper, this.logger);
      context.__step__(54, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
      return hasRange(theProperty, true);
   }

   boolean hasRange(OwlObjectProperty theProperty, boolean lookForInverse) {
      context.__step__(71, lookForInverse, theProperty, this.context, this.helper, this.logger);
      if (!theProperty.hasRange()) {
         context.__step__(60, it, lookForInverse, map, objectProperty, theProperty, this.context, this.helper, this.logger);
         context.__step__(72, lookForInverse, theProperty, this.context, this.helper, this.logger);
         context.__step__(61, it, lookForInverse, map, objectProperty, theProperty, this.context, this.helper, this.logger);
         if (lookForInverse) {
            context.__step__(73, lookForInverse, theProperty, this.context, this.helper, this.logger);
            context.__step__(63, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
            OwlObjectProperty objectProperty = (OwlObjectProperty) theProperty;
            context.__step__(64, lookForInverse, theProperty, this.context, this.helper, this.logger);
            context.__step__(74, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
            context.__step__(65, lookForInverse, theProperty, this.context, this.helper, this.logger);
            if (objectProperty.hasInverseProperty()) {
               context.__step__(66, lookForInverse, theProperty, this.context, this.helper, this.logger);
               context.__step__(75, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
               objectProperty = objectProperty.getInverseProperty();
               context.__step__(68, theProperty, this.context, this.helper, this.logger);
               context.__step__(76, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
               context.__step__(69, theProperty, this.context, this.helper, this.logger);
               return hasDomain(objectProperty, false);
               context.__step__(70, theProperty, this.context, this.helper, this.logger);
            }
         }
         return false;
         context.__step__(80, lookForInverse, theProperty, this.context, this.helper, this.logger);
      } else {
         context.__step__(81, lookForInverse, theProperty, this.context, this.helper, this.logger);
         Map<ElementKey, OwlRestriction> map = theProperty.getRange();
         context.__step__(77, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
         context.__step__(82, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         context.__step__(78, lookForInverse, objectProperty, theProperty, this.context, this.helper, this.logger);
         Iterator<OwlRestriction> it = map.values().iterator();
         context.__step__(79, lookForInverse, theProperty, this.context, this.helper, this.logger);
         context.__step__(83, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         while (it.hasNext()) {
            context.__step__(84, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
            OwlRestriction restriction = it.next();
            context.__step__(85, it, lookForInverse, map, restriction, theProperty, this.context, this.helper, this.logger);
            OwlClass theClass = restriction.getOwlClass();
            context.__step__(86, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
            if (theClass != null && !theClass.isThing()) {
               context.__step__(87, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
               return true;
            }
         }
         context.__step__(90, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         if (lookForInverse) {
            context.__step__(91, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
            if (theProperty.hasInverseProperty()) {
               context.__step__(88, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
               context.__step__(92, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
               context.__step__(89, it, lookForInverse, map, restriction, theClass, theProperty, this.context, this.helper, this.logger);
               theProperty = theProperty.getInverseProperty();
               context.__step__(93, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
               return hasDomain(theProperty, false);
            }
         }
         context.__step__(94, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         context.__step__(96, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         context.__step__(95, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
         return false;
      }
      context.__step__(97, it, lookForInverse, map, theProperty, this.context, this.helper, this.logger);
   }

   context.__step__ (

   98,lookForInverse,theProperty,this.context,this.helper,this.logger);

   context.__step__ (

   99,lookForInverse,theProperty,this.context,this.helper,this.logger);
   boolean checkClassProperties(OwlClass theClass, ElementKey excluded) {
      context.__step__(100, lookForInverse, theProperty, this.context, this.helper, this.logger);
      context.__step__(101, excluded, theClass, this.context, this.helper, this.logger);
      if (theClass.isPackage()) {
         context.__step__(102, excluded, theClass, this.context, this.helper, this.logger);
         return true;
      }
      context.__step__(104, excluded, theClass, this.context, this.helper, this.logger);
      context.__step__(103, excluded, theClass, this.context, this.helper, this.logger);
      ElementKey key = theClass.getKey();
      context.__step__(105, excluded, key, theClass, this.context, this.helper, this.logger);
      if (excluded != null && key.equals(excluded)) {
         context.__step__(106, excluded, key, theClass, this.context, this.helper, this.logger);
         return false;
      }
      context.__step__(108, excluded, key, theClass, this.context, this.helper, this.logger);
      if (classes.containsKey(key)) {
         context.__step__(109, excluded, key, theClass, this.context, this.helper, this.logger);
         return classes.get(key);
         context.__step__(110, excluded, key, theClass, this.context, this.helper, this.logger);
      } else if (theClass.hasOwlProperties()) {
         context.__step__(111, excluded, key, theClass, this.context, this.helper, this.logger);
         classes.put(key, true);
         return true;
         context.__step__(113, excluded, key, theClass, this.context, this.helper, this.logger);
      } else {
         context.__step__(107, excluded, key, theClass, this.context, this.helper, this.logger);
         context.__step__(114, excluded, key, theClass, this.context, this.helper, this.logger);
         Map<ElementKey, OwlClass> superclasses = theClass.getSuperClasses();
         context.__step__(115, excluded, key, superclasses, theClass, this.context, this.helper, this.logger);
         Iterator<OwlClass> it = superclasses.values().iterator();
         context.__step__(116, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
         context.__step__(112, excluded, key, theClass, this.context, this.helper, this.logger);
         while (it.hasNext()) {
            context.__step__(117, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
            OwlClass parentClass = it.next();
            context.__step__(118, excluded, it, key, parentClass, superclasses, theClass, this.context, this.helper, this.logger);
            if (!parentClass.isThing()) {
               context.__step__(119, excluded, it, key, parentClass, superclasses, theClass, this.context, this.helper, this.logger);
               boolean hasProperties = checkClassProperties(parentClass, null);
               context.__step__(120, excluded, hasProperties, it, key, parentClass, superclasses, theClass, this.context, this.helper, this.logger);
               if (hasProperties) {
                  context.__step__(121, excluded, hasProperties, it, key, parentClass, superclasses, theClass, this.context, this.helper, this.logger);
                  return true;
               }
            }
         }
         context.__step__(125, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
         Map<ElementKey, OwlClass> aliasClasses = theClass.getFromAliasClasses();
         context.__step__(126, aliasClasses, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
         context.__step__(122, excluded, hasProperties, it, key, parentClass, superclasses, theClass, this.context, this.helper, this.logger);
         it = aliasClasses.values().iterator();
         context.__step__(123, excluded, hasProperties, it, key, parentClass, superclasses, theClass, this.context, this.helper, this.logger);
         context.__step__(127, aliasClasses, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
         context.__step__(124, excluded, it, key, parentClass, superclasses, theClass, this.context, this.helper, this.logger);
         while (it.hasNext()) {
            context.__step__(128, aliasClasses, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
            OwlClass aliasClass = it.next();
            context.__step__(129, aliasClass, aliasClasses, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
            boolean hasProperties = checkClassProperties(parentClass, key);
            context.__step__(130, aliasClass, aliasClasses, excluded, hasProperties, it, key, superclasses, theClass, this.context, this.helper, this.logger);
            if (hasProperties) {
               context.__step__(131, aliasClass, aliasClasses, excluded, hasProperties, it, key, superclasses, theClass, this.context, this.helper, this.logger);
               return true;
            }
         }
         context.__step__(134, aliasClasses, excluded, it, key, superclasses, theClass, this.context, this.helper, this.logger);
         return false;
      }
   }
}
