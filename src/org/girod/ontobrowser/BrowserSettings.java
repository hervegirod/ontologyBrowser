/*
Copyright (c) 2021, 2022, 2023 Hervé Girod
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
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.mdiutil.swing.JFileSelector;
import org.mdiutil.swing.JMultipleFileSelector;
import org.mdiutil.swing.PropertyEditor;

/**
 * This class encapsulates the settings.
 *
 * @version 0.6
 */
public class BrowserSettings {
   private static BrowserSettings settings = null;
   private File dir = new File(System.getProperty("user.dir"));
   private MenuFactory factory = null;
   private final PropertyEditor generalSettings = new PropertyEditor();
   private final PropertyEditor styleSettings = new PropertyEditor();
   private final PropertyEditor packageSettings = new PropertyEditor();
   private final PropertyEditor yEdSettings = new PropertyEditor();
   private JCheckBox includeIndividualsCb;
   private JCheckBox showRelationsConstraintsCb;
   private JCheckBox showDataPropertiesTypesCb;
   private JCheckBox addThingClassCb;
   private JCheckBox showIndirectRelationsCb;
   private JCheckBox showPackagesCb;
   private JCheckBox showPackagesAsClosedCb;
   private JCheckBox showPackagesInPackageViewCb;
   private final SpinnerNumberModel padWidthSpinnerModel = new SpinnerNumberModel(15, 0, 100, 1);
   private JSpinner padWidthSpinner;
   private final SpinnerNumberModel padHeightSpinnerModel = new SpinnerNumberModel(11, 0, 100, 1);
   private JSpinner padHeightSpinner;
   private JCheckBox autoRefreshCb;
   private JCheckBox showCommentsCb;
   private JFileSelector customStylesFs;
   private JFileSelector yedExeDirectoryFs;
   private JFileSelector packagesConfigurationFs;
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
      includeIndividualsCb.setSelected(conf.includeIndividuals);
      showRelationsConstraintsCb.setSelected(conf.showRelationsConstraints);
      showDataPropertiesTypesCb.setSelected(conf.showDataPropertiesTypes);
      showCommentsCb.setSelected(conf.showComments);
      showPackagesCb.setSelected(conf.showPackages);
      showPackagesAsClosedCb.setSelected(conf.showPackagesAsClosed);
      showPackagesInPackageViewCb.setSelected(conf.showPackagesInPackageView);
      addThingClassCb.setSelected(conf.addThingClass);
      showIndirectRelationsCb.setSelected(conf.showIndirectRelations);
      padWidthSpinner.setValue(conf.padWidth);
      padHeightSpinner.setValue(conf.padHeight);
      autoRefreshCb.setSelected(conf.autoRefresh);
      if (conf.getCustomGraphStylesFile() != null) {
         customStylesFs.setSelectedFile(conf.getCustomGraphStylesFile());
      } else {
         customStylesFs.setSelectedFile(null);
         customStylesFs.setCurrentDirectory(dir);
      }
      if (conf.getPackagesToForgetFile() != null) {
         packagesConfigurationFs.setSelectedFile(conf.getPackagesToForgetFile());
      } else {
         packagesConfigurationFs.setSelectedFile(null);
         packagesConfigurationFs.setCurrentDirectory(dir);
      }
      yedExeDirectoryFs.setSelectedFile(conf.getYedExeDirectory());
      if (conf.getAlternateLocations() != null) {
         schemasLocationsFs.setSelectedFiles(conf.getAlternateLocations());
      } else {
         schemasLocationsFs.setSelectedFiles(null);
         schemasLocationsFs.setCurrentDirectory(dir);
      }
   }

   /**
    * Initialize the settings GUI.
    */
   public void initialize() {
      initializeGeneralSettings();
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
   }

   /**
    * Initialize the general Settings.
    */
   private void initializeGeneralSettings() {
      BrowserConfiguration conf = BrowserConfiguration.getInstance();
      File _dir = conf.getDefaultDirectory();

      autoRefreshCb = new JCheckBox("", conf.autoRefresh);
      autoRefreshCb.setBackground(Color.WHITE);
      autoRefreshCb.addActionListener((ActionEvent e) -> {
         conf.autoRefresh = autoRefreshCb.isSelected();
      });

      showCommentsCb = new JCheckBox("", conf.showComments);
      showCommentsCb.setBackground(Color.WHITE);
      showCommentsCb.addActionListener((ActionEvent e) -> {
         conf.showComments = showCommentsCb.isSelected();
      });

      includeIndividualsCb = new JCheckBox("", conf.includeIndividuals);
      includeIndividualsCb.setBackground(Color.WHITE);
      includeIndividualsCb.addActionListener((ActionEvent e) -> {
         conf.includeIndividuals = includeIndividualsCb.isSelected();
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

      addThingClassCb = new JCheckBox("", conf.addThingClass);
      addThingClassCb.setBackground(Color.WHITE);
      addThingClassCb.addActionListener((ActionEvent e) -> {
         conf.addThingClass = addThingClassCb.isSelected();
      });

      showIndirectRelationsCb = new JCheckBox("", conf.showIndirectRelations);
      showIndirectRelationsCb.setBackground(Color.WHITE);
      showIndirectRelationsCb.addActionListener((ActionEvent e) -> {
         conf.showIndirectRelations = showIndirectRelationsCb.isSelected();
      });

      schemasLocationsFs = new JMultipleFileSelector("Schemas Alternate Locations");
      schemasLocationsFs.setHasOptionalFiles(true);
      schemasLocationsFs.setCurrentDirectory(_dir);
      schemasLocationsFs.setFileSelectionMode(JFileChooser.FILES_ONLY);
      schemasLocationsFs.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            File[] files = ((JMultipleFileSelector) e.getSource()).getSelectedFiles();
            if (files != null) {
               conf.setAlternateLocations(files);
               dir = files[0].getParentFile();
            } else {
               conf.setAlternateLocations(null);
            }
         }
      });
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
      generalSettings.addProperty(includeIndividualsCb, "", "Include Individuals");
      generalSettings.addProperty(showRelationsConstraintsCb, "", "Show Relations Constraints");
      generalSettings.addProperty(showDataPropertiesTypesCb, "", "Show DataProperties Types");
      generalSettings.addProperty(showIndirectRelationsCb, "", "Show Indirect Relations");
      generalSettings.addProperty(showCommentsCb, "", "Show Commented Elements");
      generalSettings.addProperty(addThingClassCb, "", "Add Thing Class");
      generalSettings.addProperty(schemasLocationsFs, "", "Schemas Alternate Locations");
      generalSettings.setVisible(true);

      styleSettings.addProperty(padWidthSpinner, "", "Width Padding");
      styleSettings.addProperty(padHeightSpinner, "", "Height Padding");
      styleSettings.addProperty(customStylesFs, "", "Custom Styles");
      styleSettings.setVisible(true);

      packageSettings.addProperty(showPackagesCb, "", "Show Packages");
      packageSettings.addProperty(showPackagesAsClosedCb, "", "Show Packages as Closed");
      packageSettings.addProperty(showPackagesInPackageViewCb, "", "Show Packages in Package View");
      packageSettings.addProperty(packagesConfigurationFs, "", "Packages Configuration");
      packageSettings.setVisible(true);

      yEdSettings.addProperty(yedExeDirectoryFs, "", "yEd Exe Directory");
      yEdSettings.setVisible(true);
   }
}
