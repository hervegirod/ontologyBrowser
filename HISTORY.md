# History
## 0.2
 - Show the cardinality of the relations

## 0.3
 - Fix the manifest
 - Fix some cases where the parsing would lead to an exception

## 0.4
 - Use MDIUtilities 1.2.51
 - Use MDIFramework 1.3.11
 - Use jGraphml 1.2.4
 - Use docJGenerator 1.6.4.9
 - Fix some cases where the parsing would lead to an exception
 - Add an option to allow not to add the Thing class in the diagram
 - Add the notion of packages
 - Add a tree showing the packages and the classes on the left of the UI
 - Center the diagram when selecting a class

## 0.5
 - Use MDIUtilities 1.2.59
 - Use docJGenerator 1.6.5
 - Use Netbeans 12.5 for the development
 - Reorganize the libraries to put jena libraries in a specific folder
 - Add trees showing the properties and individuals on the left of the UI
 - Show the linked elements in a popup panel when right-clicking on the element in a tree
 - Show equivalent classes and equivalent properties
 - Add a search function
 - Allow parsing Turtle files
 - Handle correctly union classes for domains and range when parsing
 - Protect the parser against unsupported Owl2 files
 - Better integrate the app on Mac OS X

# 0.6
 - Use MDIUtilities 1.2.60
 - Use docJGenerator 1.6.5.8
 - Use MDIFramework 1.4.2
 - Add tooltips showing the description and comments of elements in the ontology
 - Show the classes associated with the selected Class in the dependencies window
 - Parse the default annotations for elements in the model and show documented elements in the tree
 - Parse and show all annotations in the model

# 0.7
 - Add a log level for the browser
 - Show correctly the individuals which are parts of equivalent classes
 - Fix some cases where the dependencies window would not show the dependencies on the correct element
 - Support seeAlso and isDefinedBy annotations on elements in the Ontology
 - Allow to specify alternate locations for imported URIs
 - Use labels to name the elements when they are present
 - Add a configuration option to show aliased classes in the diagram
 - Add a Datatypes tree
 - Allow to execute SPARQL queries
 - Add a standards folder in the distribution

# 0.8
 - Use scriptHelper 1.6.2
 - Add scripting debugging functionality
 - Fix the log level which was impossible to set in the settings
 - Fix some cases where parsing some ontologies could result in a Cannot convert node xxx to OntClass: it does not have rdf:type owl:Class or equivalent
 - Add an option allowing to include SuperClasses in the dependencies of a Class
 - Add a scripting framework
 - Show annotations on annotations
 - Add a tab representing the content of the Ontology (the prefixes and the imports)
 - Support parent-children for properties
 - Show the SuperClasses and the SubClasses in the Classes dependencies
 - Show the SuperProperties and the SubProperties in the Properties dependencies

# 0.9
 - Use jGraphml 1.2.6
 - Fix the package export in yEd not always putting some range classes correctly
 - Fix the Class dependencies for domain and range object properties in the dependencies dialog
 - Show the Functional and InverseFunctional characteristics of Properties
 - Fix the package dependencies export for classes which belong to several packages not showing any from package for these classes
 - Show equivalent properties in the export

# 0.10
 - Use docJGenerator 1.6.6.1
 - Use jGraphml 1.2.8
 - Use MDIUtilities 1.2.63
 - Take into account functional properties when computing the cardinalities of properties
 - Allow multi-selection on elements for the export

# 0.11
 - Fix some cases where there were several times the same Object property link shown the the yEd export diagram
 - Fix some cases where the default prefix of a schema was not correctly detected
 - Add a configuration property to show elements using the default namespace / prefix in bold
 - Add a configuration property to avoid to show foreign elements (using namespaces different from the default namespace) if they have no association
   with elements in the default namespace
 - Allow to configure the layout of the diagram when exporting
 - Fix the default directory which was sometimes incorrectly set
 - Fix the namespaces which could be incorrectly set on the overall Ontology

# 0.12
 - Fix some cases where a Search would lead to an exception
 - Fix some cases where a Dependency dialog on a Class would lead to an exception
 
# 0.13b1
 - Use MDIFramework 1.4.3.1
 - Use jGraphml 1.2.9
 - Use jEditor 1.2.18
 - Allow to save an Owl model on the disk
 - Update the scripting framework to be able to edit the Owl model
 - Update the scripting framework to be able to parse XML files
 - Add the datatypes to the DatatypeProperties dependencies window
 - Fix the base type of a datatype always being set to custom
 - Fix elements which were incorrectly seen as foreign 
 - Support the http://purl.org/dc/elements/1.1/ descriptions
 - Allows to refresh the content tab if the ontology has been modified externally
 - Add an option to not allow to have several levels of packages
 - Add an option to not add data properties in graphml exports
 - Add an option to not export links between two packages in graphml exports
 - Fix the graph extractor only seing one value for each property on an Individual 
 - Support the decimal built-in datatype
 - Show property values for individuals

# 0.13
 - Use jEditor 1.2.19
 - Fix some cases where a NPE could be thrown when saving as Yed with packages
 - Improve the presentation of SPARQL execution errors

# 0.14
 - Use jEditor 1.2.22
 - Fix some further cases where a NPE could be thrown when saving as Yed with packages
 - Fix the object properties names not presented in the yEd export
 - Allow to configure the radius of the classes included in the exported graph when exporting only for selected classes

# 0.15
 - Use MDIUtilities 1.3.3
 - Use jEditor 1.2.23
 - Don't superpose the object properties with their inverse properties on both the internal graph and the yEd export
 - Fix foreign individuals not removed if foreign elements should not be added
 - Fix foreign properties not completed removed if foreign elements should not be added
 - Add a warning message when opening an Ontology if there are only foreign elements or the Ontology is empty
 - Add warnings when using the scripts when Classes, Individuals, or Properties do not exist
 - Fix a lot of exceptions which could happen in scripts when providing null keys to the script helper

# 0.16
 - Fix a NPE which could happen when reloading an Ontology
 - Fix saving the ontology not writing correctly the namespaces of elements in some cases
 - Add a message when a model has been saved
 - Simplify the methods of the Script helper to avoid some cases where putting a null element would lead to an exception because the script would not know which method to call

# 0.17
 - Use docJGenerator 1.6.10
 - Use ScriptHelper 1.6.10.1
 - Use MDIFramework 1.4.7
 - Use MDIUtilities 1.3.15
 - Use jEditor 1.2.30
 - Add a method to detect if a Class has another Class as a direct or indirect superclass
 - Add a method to detect if an individual has a Class as a direct or indirect parent
 - Protect the creation of elements in the Ontology against badly formed URIs for the element name

# 0.17.1
 - Add properties on the OwlSchema
 - Don't consider that the schema is not empty if there is only the Thing class after the parsing
 - Don't consider that there are foreign elements because of the Thing class
 - Fix the schema considered having only foreign elements if their existence is not checked
 - Avoid to throw an exception if there are warnings when parsing the schema an no message area in the browser application