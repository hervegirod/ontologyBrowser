Map<ElementKey, Boolean> classes = new HashMap<>();

void process(NamedElement element) {
   HyperlinkElement hyperLinkElt = new HyperlinkElement(element.getPrefixedDisplayedName(), element);
   if (element.getElementType().equals(ElementTypes.CLASS)) {
      OwlClass theClass = (OwlClass)element;
      ElementKey key = theClass.getKey();
      if (key.isThing()) {
            return;
      }
      if (!checkClassProperties(theClass, null)) {
         logger.appendObjectLinks("Class %1 has no properties", hyperLinkElt);
      }
   } else if (element.getElementType().equals(ElementTypes.PROPERTY)) {
      OwlProperty theProperty = (OwlProperty)element;
      if (theProperty.isDatatypeProperty()) {
         if (!hasDomain(theProperty)) {
            logger.appendObjectLinks("DataProperty %1 has no Domain", hyperLinkElt);
         }
      } else {
         OwlObjectProperty theObjectProperty = (OwlObjectProperty)element;
         if (!hasDomain(theProperty)) {
            logger.appendObjectLinks("ObjectProperty %1 has no Domain", hyperLinkElt);
         }
         if (!hasRange(theProperty)) {
            logger.appendObjectLinks("ObjectProperty %1 has no Range", hyperLinkElt);
         }
      }
   }
}

boolean hasDomain(OwlProperty theProperty) {
   return hasDomain(theProperty, true);
}

boolean hasDomain(OwlProperty theProperty, boolean lookForInverse) {
   if (!theProperty.hasDomain()) {
      if (lookForInverse && theProperty instanceof OwlObjectProperty) {
         OwlObjectProperty objectProperty = (OwlObjectProperty)theProperty;
         if (objectProperty.hasInverseProperty()) {
            objectProperty = objectProperty.getInverseProperty();
            return hasDomain(objectProperty, false);
         }
      }
      return false;
   } else {
      Map<ElementKey, OwlRestriction> map = theProperty.getDomain();
      Iterator<OwlRestriction> it = map.values().iterator();
      while (it.hasNext()) {
         OwlRestriction restriction = it.next();
         OwlClass theClass = restriction.getOwlClass();
         if (theClass != null && !theClass.isThing()) {
            return true;
         }
      }
      if (lookForInverse && theProperty instanceof OwlObjectProperty) {
         OwlObjectProperty objectProperty = (OwlObjectProperty)theProperty;
         if (objectProperty.hasInverseProperty()) {
            objectProperty = objectProperty.getInverseProperty();
            return hasRange(objectProperty, false);
         }
      }
      return false;
   }
}

boolean hasRange(OwlObjectProperty theProperty) {
   return hasRange(theProperty, true);
}

boolean hasRange(OwlObjectProperty theProperty, boolean lookForInverse) {
   if (!theProperty.hasRange()) {
      if (lookForInverse) {
         OwlObjectProperty objectProperty = (OwlObjectProperty)theProperty;
         if (objectProperty.hasInverseProperty()) {
            objectProperty = objectProperty.getInverseProperty();
            return hasDomain(objectProperty, false);
         }
      }
      return false;
   } else {
      Map<ElementKey, OwlRestriction> map = theProperty.getRange();
      Iterator<OwlRestriction> it = map.values().iterator();
      while (it.hasNext()) {
         OwlRestriction restriction = it.next();
         OwlClass theClass = restriction.getOwlClass();
         if (theClass != null && !theClass.isThing()) {
            return true;
         }
      }
      if (lookForInverse) {
         if (theProperty.hasInverseProperty()) {
            theProperty = theProperty.getInverseProperty();
            return hasDomain(theProperty, false);
         }
      }
      return false;
   }
}

boolean checkClassProperties(OwlClass theClass, ElementKey excluded) {
   if (theClass.isPackage()) {
      return true;
   }
   ElementKey key = theClass.getKey();
   if (excluded!= null && key.equals(excluded)) {
     return false;
   }
   if (classes.containsKey(key)) {
      return classes.get(key);
   } else if (theClass.hasOwlProperties()) {
      classes.put(key, true);
      return true;
   } else {
      Map<ElementKey, OwlClass> superclasses = theClass.getSuperClasses();
      Iterator<OwlClass> it = superclasses.values().iterator();
      while (it.hasNext()) {
        OwlClass parentClass = it.next();
        if (! parentClass.isThing()) {
          boolean hasProperties = checkClassProperties(parentClass, null);
          if (hasProperties) {
            return true;
          }
        }
      }
      Map<ElementKey, OwlClass> aliasClasses = theClass.getFromAliasClasses();
      it = aliasClasses.values().iterator();
      while (it.hasNext()) {
        OwlClass aliasClass = it.next();
        boolean hasProperties = checkClassProperties(parentClass, key);
        if (hasProperties) {
          return true;
        }
      }
      return false;
   }
}