void process(NamedElement element) {
   if (element.getElementType().equals(ElementTypes.CLASS)) {
      OwlClass theClass = (OwlClass)element;
      logger.appendObjectLink(theClass.getKey().toString(), theClass);
   }
}