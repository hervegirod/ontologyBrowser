ScriptLogger logger;

void start() {
  logger = context.getScriptLogger();
}

void process(NamedElement element) {
  if (element.getElementType().equals(ElementTypes.CLASS)) {
     logger.appendObjectLink(element.getKey().toString(), element);
  }
}
