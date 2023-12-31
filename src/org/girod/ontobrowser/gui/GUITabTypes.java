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
package org.girod.ontobrowser.gui;

/**
 * The types of the tabs in the GUI.
 *
 * @version 0.8
 */
public interface GUITabTypes {
   public static String CLASSES_NAME = "Classes";
   public static String PROPERTIES_NAME = "Properties";
   public static String OBJECT_PROPERTIES_NAME = "Object Properties";
   public static String DATA_PROPERTIES_NAME = "Data Properties";
   public static String INDIVIDUALS_NAME = "Individuals";
   public static String ANNOTATIONS_NAME = "Annotations";
   public static String DATATYPES_NAME = "Datatypes";
   public static String ONTOLOGY_NAME = "Ontology";
   public static String ONTOLOGY_NAMESPACES_NAME = "Namespaces";
   public static String ONTOLOGY_IMPORTS_NAME = "Imports";
   /**
    * The index of the Ontology tab.
    */
   public static int TAB_ONTOLOGY_INDEX = 0;
   /**
    * The index of the Classes tab.
    */
   public static int TAB_CLASS_INDEX = 1;
   /**
    * The index of the Properties tab.
    */
   public static int TAB_PROPERTY_INDEX = 2;
   /**
    * The index of the Individuals tab.
    */
   public static int TAB_INDIVIDUAL_INDEX = 3;
   /**
    * The index of the Annotations tab.
    */
   public static int TAB_ANNOTATION_INDEX = 4;
   /**
    * The index of the Datatypes tab.
    */
   public static int TAB_DATATYPE_INDEX = 5;
}
