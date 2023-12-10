/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.girod.ontobrowser.gui;

/**
 * The types of the tabs in the GUI.
 *
 * @since 0.7
 */
public interface GUITabTypes {
   public static String CLASSES_NAME = "Classes";
   public static String PROPERTIES_NAME = "Properties";
   public static String OBJECT_PROPERTIES_NAME = "Object Properties";
   public static String DATA_PROPERTIES_NAME = "Data Properties";
   public static String INDIVIDUALS_NAME = "Individuals";
   public static String ANNOTATIONS_NAME = "Annotations";
   public static String DATATYPES_NAME = "Datatypes";
   /**
    * The index of the Classes tab.
    */
   public static int TAB_CLASS_INDEX = 0;
   /**
    * The index of the Properties tab.
    */
   public static int TAB_PROPERTY_INDEX = 1;
   /**
    * The index of the Individuals tab.
    */
   public static int TAB_INDIVIDUAL_INDEX = 2;
   /**
    * The index of the Annotations tab.
    */
   public static int TAB_ANNOTATION_INDEX = 3;
   /**
    * The index of the Datatypes tab.
    */
   public static int TAB_DATATYPE_INDEX = 4;
}
