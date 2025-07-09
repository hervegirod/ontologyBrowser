/*
Copyright (c) 2021, 2022, 2023, 2024, 2028 Hervé Girod
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
package org.girod.ontobrowser;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.girod.ontobrowser.gui.errors.ErrorLevel;
import org.girod.ontobrowser.model.OntModelSpecTypes;
import org.mdiutil.swing.JFileSelector;
import org.mdiutil.swing.JMultipleFileSelector;
import org.mdiutil.swing.PropertyEditor;
import org.girod.ontobrowser.parsers.graph.LayoutOptions;

/**
 * This class encapsulates the settings.
 *
 * @version 0.17.2
 */
public class BrowserSettings {
   private static BrowserSettings settings = null;
   private File dir = new File(System.getProperty("user.dir"));
   private MenuFactory factory = null;
   private final PropertyEditor generalSettings = new PropertyEditor();
   private final PropertyEditor diagramsSettings = new PropertyEditor();
   private final PropertyEditor parsingSettings = new PropertyEditor();
   private final PropertyEditor schemasSettings = new PropertyEditor();
   private final PropertyEditor sparqlSettings = new PropertyEditor();
   private final PropertyEditor scriptsSettings = new PropertyEditor();
   private final PropertyEditor styleSettings = new PropertyEditor();
   private final PropertyEditor packageSettings = new PropertyEditor();
   private final PropertyEditor yEdSettings = new PropertyEditor();

   // general
   private JCheckBox showIndirectRelationsCb;
   private JCheckBox autoRefreshCb;
   private JCheckBox multiSelectionCb;
   private JCheckBox showCommentsCb;
   private JCheckBox showOwnElementsInBoldCb;
   private JCheckBox includeForeignDisconnectedElementsCb;
   private JCheckBox includeParentRelationsCb;
   private JCheckBox includeAliasCb;
   private JComboBox logLevelCb;
   // parsing
   private JCheckBox includeIndividualsCb;
   private JCheckBox addThingClassCb;
   private JCheckBox strictModeCb;
   private JComboBox modelSpecCb;
   // diagrams
   private JCheckBox showAliasCb;
   private JCheckBox showRelationsConstraintsCb;
   private JCheckBox showDataPropertiesTypesCb;
   private JCheckBox showPropertiesCb;
   private JCheckBox showIndividualsCb;
   private JCheckBox showInterPackageConnectionsCb;
   private final SpinnerNumberModel maximumRadiusSpinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
   private JSpinner maximumRadiusSpinner;   
   // Packages
   private JCheckBox showPackagesCb;
   private JCheckBox acceptSubPackagesCb;
   private JCheckBox showPackagesAsClosedCb;
   private JCheckBox showPackagesInPackageViewCb;
   // SPARQL
   private JCheckBox prefixInSPARQLCb;
   private JCheckBox prefixGeoSPARQLInSPARQLCb;
   private JCheckBox prefixOwlTimeInSPARQLCb;   
   private JTextField basePrefixTf;
   // scripts
   private JCheckBox endAtFirstExceptionCb;
   // Styles
   private final SpinnerNumberModel padWidthSpinnerModel = new SpinnerNumberModel(15, 0, 100, 1);
   private JSpinner padWidthSpinner;
   private final SpinnerNumberModel padHeightSpinnerModel = new SpinnerNumberModel(11, 0, 100, 1);
   private JSpinner padHeightSpinner;
   private JFileSelector customStylesFs;
   private JComboBox layoutOptionCb;
   // yEd
   private JFileSelector yedExeDirectoryFs;
   private JFileSelector packagesConfigurationFs;
   // schemas locations
   private JFileSelector schemasRepositoryFs;
   private JCheckBox builtinSchemasCb;
   private JMultipleFileSelector schemasLocationsFs;

   private BrowserSettings() {
      super();
   }

   /**
    * Return the unique instance.
    *
    * @return the unique instance
    */
   public static BrowserSettings getInstance() {
      if (settings == null) {
         settings = new BrowserSettings();
      }
      return settings;
   }

   public void setMenuFactory(MenuFactory factory) {
      this.factory = factory;
   }

   /**
    * Return the general Settings.
    *
    * @return the general Settings
    */
   public PropertyEditor getGeneralSettings() {
      return generalSettings;
   }

   /**
    * Return the diagrams Settings.
    *
    * @return the diagrams Settings
    */
   public PropertyEditor getDiagramsSettings() {
      return diagramsSettings;
   }

   /**
    * Return the parsing Settings.
    *
    * @return the parsing Settings
    */
   public PropertyEditor getParsingSettings() {
      return parsingSettings;
   }

   /**
    * Return the schemas Settings.
    *
    * @return the schemas Settings
    */
   public PropertyEditor getSchemasSettings() {
      return schemasSettings;
   }

   /**
    * Return the SPARQL Settings.
    *
    * @return the SPARQL Settings
    */
   public PropertyEditor getSPARQLSettings() {
      return sparqlSettings;
   }

   /**
    * Return the scripts Settings.
    *
    * @return the scripts Settings
    */
   public PropertyEditor getScriptsSettings() {
      return scriptsSettings;
   }

   /**
    * Return the general Settings.
    *
    * @return the general Settings
    */
   public PropertyEditor getStyleSettings() {
      return styleSettings;
   }

   /**
    * Return the package Settings.
    *
    * @return the package Settings
    */
   public PropertyEditor getPackageSettings() {
      return packageSettings;
   }

   /**
    * Return the yEd Settings.
    *
    * @return the yEd Settings
    */
   public PropertyEditor getYedSettings() {
      return yEdSettings;
   }

   /**
    * Reset the settings to the configuration values.
    */
   public void resetSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      if (conf.getDefaultDirectory() != null) {
         this.dir = conf.getDefaultDirectory();
      }
      // General
      showCommentsCb.setSelected(conf.showComments);
      showOwnElementsInBoldCb.setSelected(conf.showOwnElementsInBold);
      showIndirectRelationsCb.setSelected(conf.showIndirectRelations);
      autoRefreshCb.setSelected(conf.autoRefresh);
      multiSelectionCb.setSelected(conf.multiSelection);
      includeParentRelationsCb.setSelected(conf.includeParentRelations);
      includeAliasCb.setSelected(conf.includeAlias);
      logLevelCb.setSelectedItem(getLogItem(conf.logLevel));

      // diagrams
      showRelationsConstraintsCb.setSelected(conf.showRelationsConstraints);
      showDataPropertiesTypesCb.setSelected(conf.showDataPropertiesTypes);
      showAliasCb.setSelected(conf.showAlias);
      showPropertiesCb.setSelected(conf.showProperties);
      showIndividualsCb.setSelected(conf.showIndividuals);
      showInterPackageConnectionsCb.setSelected(conf.showInterPackageConnections);
      maximumRadiusSpinner.setValue(conf.maximumRadius);

      // parsing
      includeIndividualsCb.setSelected(conf.includeIndividuals);
      addThingClassCb.setSelected(conf.addThingClass);
      strictModeCb.setSelected(conf.strictMode);
      modelSpecCb.setSelectedItem(conf.modelSpec);

      // Styles
      padWidthSpinner.setValue(conf.padWidth);
      padHeightSpinner.setValue(conf.padHeight);
      if (conf.getCustomGraphStylesFile() != null) {
         customStylesFs.setSelectedFile(conf.getCustomGraphStylesFile());
      } else {
         customStylesFs.setSelectedFile(null);
         customStylesFs.setCurrentDirectory(dir);
      }
      layoutOptionCb.setSelectedItem(this.getLayoutOption(conf.layoutOptions));

      // Packages
      showPackagesCb.setSelected(conf.showPackages);
      acceptSubPackagesCb.setSelected(conf.acceptSubPackages);
      showPackagesAsClosedCb.setSelected(conf.showPackagesAsClosed);
      showPackagesInPackageViewCb.setSelected(conf.showPackagesInPackageView);
      if (conf.getPackagesToForgetFile() != null) {
         packagesConfigurationFs.setSelectedFile(conf.getPackagesToForgetFile());
      } else {
         packagesConfigurationFs.setSelectedFile(null);
         packagesConfigurationFs.setCurrentDirectory(dir);
      }

      // yEd
      yedExeDirectoryFs.setSelectedFile(conf.getYedExeDirectory());

      // Schemas
      if (conf.getSchemasRepositoryFile() != null) {
         schemasRepositoryFs.setSelectedFile(conf.getSchemasRepositoryFile());
      } else {
         schemasRepositoryFs.setSelectedFiles(null);
         schemasRepositoryFs.setCurrentDirectory(dir);
      }
      builtinSchemasCb.setSelected(conf.useBuiltinSchemas);
      if (conf.getAlternateLocations() != null) {
         schemasLocationsFs.setSelectedFiles(conf.getAlternateLocations());
      } else {
         schemasLocationsFs.setSelectedFiles(null);
         schemasLocationsFs.setCurrentDirectory(dir);
      }

      // SPARQL
      prefixInSPARQLCb.setSelected(conf.addPrefixInSPARQL);
      prefixGeoSPARQLInSPARQLCb.setSelected(conf.addGeoSPARQLPrefixInSPARQL);      
      prefixOwlTimeInSPARQLCb.setSelected(conf.addOwlTimePrefixInSPARQL);
      basePrefixTf.setText(conf.basePrefix);

      // scripts
      endAtFirstExceptionCb.setSelected(conf.endAtFirstException);
   }

   /**
    * Initialize the settings GUI.
    */
   public void initialize() {
      initializeGeneralSettings();
      initializeDiagramSettings();
      initializeParsingSettings();
      initializeSchemasSettings();
      initializeSPARQLSettings();
      initializeScriptsSettings();
      initializeYedSettings();
      initializeStyleSettings();
      initializePackageSettings();

      configureSettings();
   }

   /**
    * Initialize the yed Settings.
    */
   private void initializeYedSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();

      yedExeDirectoryFs = new JFileSelector("yEd Exe Directory");
      yedExeDirectoryFs.setHasOptionalFiles(true);
      yedExeDirectoryFs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      yedExeDirectoryFs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File file = ((JFileChooser) e.getSource()).getSelectedFile();
            if (file != null) {
               conf.setYedExeDirectory(file);
            } else {
               conf.setYedExeDirectory(null);
            }
         }
      });
   }

   /**
    * Initialize the style Settings.
    */
   private void initializeStyleSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      File _dir = conf.getDefaultDirectory();

      customStylesFs = new JFileSelector("Graph Style");
      customStylesFs.setHasOptionalFiles(true);
      customStylesFs.setCurrentDirectory(_dir);
      customStylesFs.setFileSelectionMode(JFileChooser.FILES_ONLY);
      customStylesFs.setFileFilter(conf.xmlfilter);
      customStylesFs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File file = ((JFileChooser) e.getSource()).getSelectedFile();
            if (file != null) {
               conf.setCustomStylesConfiguration(file);
               dir = file.getParentFile();
            } else {
               conf.setCustomStylesConfiguration(null);
            }
         }
      });

      padWidthSpinner = new JSpinner(padWidthSpinnerModel);
      padWidthSpinner.setEditor(new JSpinner.NumberEditor(padWidthSpinner, "###"));
      padWidthSpinner.setMaximumSize(padWidthSpinner.getPreferredSize());
      padWidthSpinner.addChangeListener((ChangeEvent e) -> {
         try {
            int value = ((Integer) ((JSpinner) e.getSource()).getValue());
            if (value < 0) {
               value = 0;
            } else if (value > 100) {
               value = 100;
            }
            conf.padWidth = value;
         } catch (ArithmeticException ex) {
         }
      });

      padHeightSpinner = new JSpinner(padHeightSpinnerModel);
      padHeightSpinner.setEditor(new JSpinner.NumberEditor(padHeightSpinner, "###"));
      padHeightSpinner.setMaximumSize(padHeightSpinner.getPreferredSize());
      padHeightSpinner.addChangeListener((ChangeEvent e) -> {
         try {
            int value = ((Integer) ((JSpinner) e.getSource()).getValue());
            if (value < 0) {
               value = 0;
            } else if (value > 100) {
               value = 100;
            }
            conf.padHeight = value;
         } catch (ArithmeticException ex) {
         }
      });
      String[] positionType = {"No Constraint", "Connector on South Side", "Sub-Classes on South Relative Position", "Children on South Relative Position"};
      layoutOptionCb = new JComboBox<>(positionType);
      layoutOptionCb.setSelectedItem(getLayoutOption(conf.layoutOptions));

      layoutOptionCb.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            conf.layoutOptions = getLayoutOption(layoutOptionCb.getSelectedItem().toString());
         }
      });
   }

   /**
    * Initialize the scripts Settings.
    */
   private void initializeScriptsSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      endAtFirstExceptionCb = new JCheckBox("", conf.endAtFirstException);
      endAtFirstExceptionCb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            conf.endAtFirstException = endAtFirstExceptionCb.isSelected();
         }
      });
   }

   /**
    * Initialize the schemas Settings.
    */
   private void initializeSPARQLSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      prefixInSPARQLCb = new JCheckBox("", conf.addPrefixInSPARQL);
      prefixInSPARQLCb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            conf.addPrefixInSPARQL = prefixInSPARQLCb.isSelected();
         }
      });
      
      prefixGeoSPARQLInSPARQLCb = new JCheckBox("", conf.addGeoSPARQLPrefixInSPARQL);
      prefixGeoSPARQLInSPARQLCb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            conf.addGeoSPARQLPrefixInSPARQL = prefixGeoSPARQLInSPARQLCb.isSelected();
         }
      }); 
      
      prefixOwlTimeInSPARQLCb = new JCheckBox("", conf.addOwlTimePrefixInSPARQL);
      prefixOwlTimeInSPARQLCb.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            conf.addOwlTimePrefixInSPARQL = prefixOwlTimeInSPARQLCb.isSelected();
         }
      });          

      basePrefixTf = new JTextField(conf.basePrefix);
      basePrefixTf.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            conf.basePrefix = basePrefixTf.getText();
         }
      });
   }

   /**
    * Initialize the schemas Settings.
    */
   private void initializeSchemasSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      File _dir = conf.getDefaultDirectory();

      schemasRepositoryFs = new JFileSelector("Schemas Repository");
      schemasRepositoryFs.setHasOptionalFiles(true);
      schemasRepositoryFs.setCurrentDirectory(_dir);
      schemasRepositoryFs.setFileSelectionMode(JFileChooser.FILES_ONLY);
      schemasRepositoryFs.setFileFilter(conf.xmlfilter);
      schemasRepositoryFs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File file = ((JFileChooser) e.getSource()).getSelectedFile();
            if (file != null) {
               conf.setSchemasRepository(file);
               dir = file.getParentFile();
               conf.setDefaultDirectory(dir);
            } else {
               conf.setSchemasRepository(file);
            }
         }
      });

      builtinSchemasCb = new JCheckBox("", conf.useBuiltinSchemas);
      builtinSchemasCb.setBackground(Color.WHITE);
      builtinSchemasCb.addActionListener((ActionEvent e) -> {
         conf.useBuiltinSchemas = builtinSchemasCb.isSelected();
         conf.setAlternateLocations();
      });

      schemasLocationsFs = new JMultipleFileSelector("Schemas Alternate Locations");
      schemasLocationsFs.setHasOptionalFiles(true);
      schemasLocationsFs.setCurrentDirectory(_dir);
      schemasLocationsFs.setFileSelectionMode(JFileChooser.FILES_ONLY);
      schemasLocationsFs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File[] files = ((JMultipleFileSelector) e.getSource()).getSelectedFiles();
            if (files != null && files.length != 0) {
               conf.setAlternateLocations(files);
               dir = files[0].getParentFile();
               conf.setDefaultDirectory(dir);
            } else {
               conf.setAlternateLocations(null);
            }
         }
      });
   }

   /**
    * Initialize the diagram Settings.
    */
   private void initializeDiagramSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();

      showAliasCb = new JCheckBox("", conf.showAlias);
      showAliasCb.setBackground(Color.WHITE);
      showAliasCb.addActionListener((ActionEvent e) -> {
         conf.showAlias = showAliasCb.isSelected();
      });

      showRelationsConstraintsCb = new JCheckBox("", conf.showRelationsConstraints);
      showRelationsConstraintsCb.setBackground(Color.WHITE);
      showRelationsConstraintsCb.addActionListener((ActionEvent e) -> {
         conf.showRelationsConstraints = showRelationsConstraintsCb.isSelected();
      });

      showDataPropertiesTypesCb = new JCheckBox("", conf.showDataPropertiesTypes);
      showDataPropertiesTypesCb.setBackground(Color.WHITE);
      showDataPropertiesTypesCb.addActionListener((ActionEvent e) -> {
         conf.showDataPropertiesTypes = showDataPropertiesTypesCb.isSelected();
      });

      showPropertiesCb = new JCheckBox("", conf.showProperties);
      showPropertiesCb.setBackground(Color.WHITE);
      showPropertiesCb.addActionListener((ActionEvent e) -> {
         conf.showProperties = showPropertiesCb.isSelected();
      });

      showIndividualsCb = new JCheckBox("", conf.showIndividuals);
      showIndividualsCb.setBackground(Color.WHITE);
      showIndividualsCb.addActionListener((ActionEvent e) -> {
         conf.showIndividuals = showIndividualsCb.isSelected();
      });

      showInterPackageConnectionsCb = new JCheckBox("", conf.showInterPackageConnections);
      showInterPackageConnectionsCb.setBackground(Color.WHITE);
      showInterPackageConnectionsCb.addActionListener((ActionEvent e) -> {
         conf.showInterPackageConnections = showInterPackageConnectionsCb.isSelected();
      });
      
      maximumRadiusSpinner = new JSpinner(maximumRadiusSpinnerModel);
      maximumRadiusSpinner.setEditor(new JSpinner.NumberEditor(maximumRadiusSpinner, "##"));
      maximumRadiusSpinner.setMaximumSize(maximumRadiusSpinner.getPreferredSize());
      maximumRadiusSpinner.addChangeListener((ChangeEvent e) -> {
         try {
            int value = ((Integer) ((JSpinner) e.getSource()).getValue());
            if (value < 1) {
               value = 1;
            } else if (value > 10) {
               value = 10;
            }
            conf.maximumRadius = value;
         } catch (ArithmeticException ex) {
         }
      });      
   }

   /**
    * Initialize the parsing Settings.
    */
   private void initializeParsingSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();

      includeIndividualsCb = new JCheckBox("", conf.includeIndividuals);
      includeIndividualsCb.setBackground(Color.WHITE);
      includeIndividualsCb.addActionListener((ActionEvent e) -> {
         conf.includeIndividuals = includeIndividualsCb.isSelected();
      });

      addThingClassCb = new JCheckBox("", conf.addThingClass);
      addThingClassCb.setBackground(Color.WHITE);
      addThingClassCb.addActionListener((ActionEvent e) -> {
         conf.addThingClass = addThingClassCb.isSelected();
      });

      strictModeCb = new JCheckBox("", conf.strictMode);
      strictModeCb.setBackground(Color.WHITE);
      strictModeCb.addActionListener((ActionEvent e) -> {
         conf.strictMode = strictModeCb.isSelected();
      });

      // model spec
      String[] modelSpecType = {OntModelSpecTypes.OWL_DL_MEM, OntModelSpecTypes.OWL_DL_MEM_RDFS_INF, OntModelSpecTypes.OWL_DL_MEM_RULE_INF, OntModelSpecTypes.OWL_DL_MEM_TRANS_INF,
         OntModelSpecTypes.OWL_LITE_MEM, OntModelSpecTypes.OWL_LITE_MEM_RDFS_INF, OntModelSpecTypes.OWL_LITE_MEM_RULES_INF, OntModelSpecTypes.OWL_LITE_MEM_TRANS_INF,
         OntModelSpecTypes.OWL_MEM, OntModelSpecTypes.OWL_MEM_MICRO_RULE_INF, OntModelSpecTypes.OWL_MEM_MINI_RULE_INF, OntModelSpecTypes.OWL_MEM_RDFS_INF,
         OntModelSpecTypes.OWL_MEM_TRANS_INF, OntModelSpecTypes.RDFS_MEM_RDFS_INF, OntModelSpecTypes.RDFS_MEM_TRANS_INF};
      modelSpecCb = new JComboBox<>(modelSpecType);
      modelSpecCb.setSelectedItem(conf.modelSpec);

      modelSpecCb.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            conf.modelSpec = modelSpecCb.getSelectedItem().toString();
         }
      });
   }

   /**
    * Initialize the general Settings.
    */
   private void initializeGeneralSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();

      autoRefreshCb = new JCheckBox("", conf.autoRefresh);
      autoRefreshCb.setBackground(Color.WHITE);
      autoRefreshCb.addActionListener((ActionEvent e) -> {
         conf.autoRefresh = autoRefreshCb.isSelected();
      });

      multiSelectionCb = new JCheckBox("", conf.multiSelection);
      multiSelectionCb.setBackground(Color.WHITE);
      multiSelectionCb.addActionListener((ActionEvent e) -> {
         conf.multiSelection = multiSelectionCb.isSelected();
         factory.updateTreeSelectionMode();
      });

      showCommentsCb = new JCheckBox("", conf.showComments);
      showCommentsCb.setBackground(Color.WHITE);
      showCommentsCb.addActionListener((ActionEvent e) -> {
         conf.showComments = showCommentsCb.isSelected();
      });

      showOwnElementsInBoldCb = new JCheckBox("", conf.showOwnElementsInBold);
      showOwnElementsInBoldCb.setBackground(Color.WHITE);
      showOwnElementsInBoldCb.addActionListener((ActionEvent e) -> {
         conf.showOwnElementsInBold = showOwnElementsInBoldCb.isSelected();
      });

      includeForeignDisconnectedElementsCb = new JCheckBox("", conf.includeForeignDisconnectedElements);
      includeForeignDisconnectedElementsCb.setBackground(Color.WHITE);
      includeForeignDisconnectedElementsCb.addActionListener((ActionEvent e) -> {
         conf.includeForeignDisconnectedElements = includeForeignDisconnectedElementsCb.isSelected();
      });

      showIndirectRelationsCb = new JCheckBox("", conf.showIndirectRelations);
      showIndirectRelationsCb.setBackground(Color.WHITE);
      showIndirectRelationsCb.addActionListener((ActionEvent e) -> {
         conf.showIndirectRelations = showIndirectRelationsCb.isSelected();
      });

      includeParentRelationsCb = new JCheckBox("", conf.includeParentRelations);
      includeParentRelationsCb.setBackground(Color.WHITE);
      includeParentRelationsCb.addActionListener((ActionEvent e) -> {
         conf.includeParentRelations = includeParentRelationsCb.isSelected();
      });

      includeAliasCb = new JCheckBox("", conf.includeAlias);
      includeAliasCb.setBackground(Color.WHITE);
      includeAliasCb.addActionListener((ActionEvent e) -> {
         conf.includeAlias = includeAliasCb.isSelected();
      });

      // log level
      String[] logLevelType = {"No Logging", "Log All", "Log Warnings", "Log Errors"};
      logLevelCb = new JComboBox<>(logLevelType);
      logLevelCb.setSelectedItem(getLogItem(conf.logLevel));

      logLevelCb.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            conf.logLevel = getLogLevel(logLevelCb.getSelectedItem().toString());
         }
      });
   }

   private short getLogLevel(String logItem) {
      switch (logItem) {
         case "No Logging":
            return ErrorLevel.NO_LOGGING;
         case "Log All":
            return ErrorLevel.INFO;
         case "Log Warnings":
            return ErrorLevel.WARNING;
         case "Log Errors":
            return ErrorLevel.ERROR;
         default:
            return ErrorLevel.NO_LOGGING;
      }
   }

   private String getLogItem(short logLevel) {
      switch (logLevel) {
         case ErrorLevel.NO_LOGGING:
            return "No Logging";
         case ErrorLevel.INFO:
            return "Log All";
         case ErrorLevel.WARNING:
            return "Log Warnings";
         case ErrorLevel.ERROR:
            return "Log Errors";
         default:
            return "No Logging";
      }
   }

   private short getLayoutOption(String option) {
      switch (option) {
         case "No Constraint":
            return LayoutOptions.ANY_POSITION;
         case "Connector on South Side":
            return LayoutOptions.PORT_SOUTH_SIDE;
         case "Sub-Classes on South Relative Position":
            return LayoutOptions.SUBCLASS_SOUTH_POSITION;
         case "Children on South Relative Position":
            return LayoutOptions.CHILDREN_SOUTH_POSITION;
         default:
            return LayoutOptions.ANY_POSITION;
      }
   }

   private String getLayoutOption(short option) {
      switch (option) {
         case LayoutOptions.ANY_POSITION:
            return "No Constraint";
         case LayoutOptions.PORT_SOUTH_SIDE:
            return "Connector on South Side";
         case LayoutOptions.SUBCLASS_SOUTH_POSITION:
            return "Sub-Classes on South Relative Position";
         case LayoutOptions.CHILDREN_SOUTH_POSITION:
            return "Children on South Relative Position";
         default:
            return "No Constraint";
      }
   }

   /**
    * Initialize the package Settings.
    */
   private void initializePackageSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      File _dir = conf.getDefaultDirectory();

      showPackagesCb = new JCheckBox("", conf.showPackages);
      showPackagesCb.setBackground(Color.WHITE);
      showPackagesCb.addActionListener((ActionEvent e) -> {
         conf.showPackages = showPackagesCb.isSelected();
      });
      
      acceptSubPackagesCb = new JCheckBox("", conf.acceptSubPackages);
      acceptSubPackagesCb.setBackground(Color.WHITE);
      acceptSubPackagesCb.addActionListener((ActionEvent e) -> {
         conf.acceptSubPackages = acceptSubPackagesCb.isSelected();
      });

      showPackagesInPackageViewCb = new JCheckBox("", conf.showPackagesInPackageView);
      showPackagesInPackageViewCb.setBackground(Color.WHITE);
      showPackagesInPackageViewCb.addActionListener((ActionEvent e) -> {
         conf.showPackagesInPackageView = showPackagesInPackageViewCb.isSelected();
      });

      showPackagesAsClosedCb = new JCheckBox("", conf.showPackagesAsClosed);
      showPackagesAsClosedCb.setBackground(Color.WHITE);
      showPackagesAsClosedCb.addActionListener((ActionEvent e) -> {
         conf.showPackagesAsClosed = showPackagesAsClosedCb.isSelected();
      });

      packagesConfigurationFs = new JFileSelector("Packages Configuration");
      packagesConfigurationFs.setHasOptionalFiles(true);
      packagesConfigurationFs.setCurrentDirectory(_dir);
      packagesConfigurationFs.setFileSelectionMode(JFileChooser.FILES_ONLY);
      packagesConfigurationFs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File file = ((JFileChooser) e.getSource()).getSelectedFile();
            if (file != null) {
               conf.setPackagesConfiguration(file);
               dir = file.getParentFile();
               conf.setDefaultDirectory(dir);
            } else {
               conf.setPackagesConfiguration(null);
            }
         }
      });
   }

   /**
    * Configure the Settings.
    */
   private void configureSettings() {
      resetSettings();

      generalSettings.addProperty(autoRefreshCb, "", "Auto Refresh");
      generalSettings.addProperty(multiSelectionCb, "", "Multi Selection");
      generalSettings.addProperty(showIndirectRelationsCb, "", "Show Indirect Relations in Dependencies");
      generalSettings.addProperty(showCommentsCb, "", "Show Commented Elements");
      generalSettings.addProperty(showOwnElementsInBoldCb, "", "Show Own Elements in Bold");
      generalSettings.addProperty(includeParentRelationsCb, "", "Include Parent Relations in Dependencies");
      generalSettings.addProperty(includeAliasCb, "", "Include Alias Relations in Dependencies");
      generalSettings.addProperty(logLevelCb, "", "Log Level");
      generalSettings.setVisible(true);

      diagramsSettings.addProperty(showRelationsConstraintsCb, "", "Show Relations Constraints");
      diagramsSettings.addProperty(showDataPropertiesTypesCb, "", "Show DataProperties Types");
      diagramsSettings.addProperty(showPropertiesCb, "", "Show Properties");
      diagramsSettings.addProperty(showIndividualsCb, "", "Show Individuals");
      diagramsSettings.addProperty(showInterPackageConnectionsCb, "", "Show Inter-package Connections");
      diagramsSettings.addProperty(maximumRadiusSpinner, "", "Maximum Class Radius");
      diagramsSettings.addProperty(showAliasCb, "", "Show Alias");
      diagramsSettings.setVisible(true);

      parsingSettings.addProperty(includeIndividualsCb, "", "Include Individuals");
      parsingSettings.addProperty(includeForeignDisconnectedElementsCb, "", "Include Foreign Disconnected Elements");
      parsingSettings.addProperty(addThingClassCb, "", "Add Thing Class");
      parsingSettings.addProperty(strictModeCb, "", "Strict Mode");
      parsingSettings.addProperty(modelSpecCb, "", "Model Specification");
      parsingSettings.setVisible(true);

      schemasSettings.addProperty(schemasRepositoryFs, "", "Schemas Repository");
      schemasSettings.addProperty(builtinSchemasCb, "", "Use Built-in Schemas");
      schemasSettings.addProperty(schemasLocationsFs, "", "Schemas Alternate Locations");
      schemasSettings.setVisible(true);

      sparqlSettings.addProperty(prefixInSPARQLCb, "", "Add Prefix in SPARQL Requests");
      sparqlSettings.addProperty(prefixGeoSPARQLInSPARQLCb, "", "Add GeoSPARQL PREFIX in SPARQL Requests");
      sparqlSettings.addProperty(prefixOwlTimeInSPARQLCb, "", "Add OwlTime PREFIX in SPARQL Requests");
      sparqlSettings.addProperty(basePrefixTf, "", "Default Base Prefix");
      sparqlSettings.setVisible(true);

      scriptsSettings.addProperty(endAtFirstExceptionCb, "", "End Script at First Exception");
      scriptsSettings.setVisible(true);

      styleSettings.addProperty(padWidthSpinner, "", "Width Padding");
      styleSettings.addProperty(padHeightSpinner, "", "Height Padding");
      styleSettings.addProperty(customStylesFs, "", "Custom Styles");
      styleSettings.addProperty(layoutOptionCb, "", "Layout Option");
      styleSettings.setVisible(true);

      packageSettings.addProperty(showPackagesCb, "", "Show Packages");
      packageSettings.addProperty(showPackagesAsClosedCb, "", "Show Packages as Closed");
      packageSettings.addProperty(showPackagesInPackageViewCb, "", "Show Packages in Package View");
      packageSettings.addProperty(acceptSubPackagesCb, "", "Accept sub-Packages");      
      packageSettings.addProperty(packagesConfigurationFs, "", "Packages Configuration");
      packageSettings.setVisible(true);

      yEdSettings.addProperty(yedExeDirectoryFs, "", "yEd Exe Directory");
      yEdSettings.setVisible(true);
   }
}
