/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jhexed.swing.editor.ui;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.hexmap.HexMapPanelListener;
import com.igormaznitsa.jhexed.hexmap.HexMapPanel;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.layers.LayerRecordPanel;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.FrameTools;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.FrameToolOptions;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.layers.FrameLayers;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.EditLayerDialog;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.DialogDocumentOptions;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.DialogAbout;
import com.igormaznitsa.jhexed.engine.misc.*;
import com.igormaznitsa.jhexed.swing.editor.Log;
import com.igormaznitsa.jhexed.swing.editor.filecontainer.FileContainer;
import com.igormaznitsa.jhexed.swing.editor.filecontainer.FileContainerSection;
import com.igormaznitsa.jhexed.swing.editor.model.*;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import com.igormaznitsa.jhexed.swing.editor.ui.exporters.ImageExporter;
import com.igormaznitsa.jhexed.swing.editor.ui.exporters.XmlExporter;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.FrameUtils;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class MainForm extends javax.swing.JFrame implements MouseListener, MouseMotionListener, MouseWheelListener, HexMapPanelListener, InsideApplicationBus.AppBusListener {

  private static final long serialVersionUID = 3235266727080222251L;

  private final Desktop hexMapPanelDesktop;
  private final HexMapPanel hexMapPanel;

  private final FrameLayers frameLayers;
  private final FrameTools frameTools;
  private final FrameToolOptions frameToolOptions;

  private final DocumentCellComments cellComments = new DocumentCellComments();
  
  private final LayerListModel layers;

  private ToolType selectedToolType;
  private HexFieldLayer selectedLayer;

  private File destinationFile;
  private File lastExportedFile;
  private String documentComments = "";

  private boolean dragging = false;

  private static final Preferences REGISTRY = Preferences.userRoot().node(MainForm.class.getName());

  public MainForm(final String fileToOpen) {
    initComponents();
    hexMapPanelDesktop = new Desktop();
    layers = new LayerListModel(256, 128);

    try {
      this.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResource("com/igormaznitsa/jhexed/swing/editor/icons/logo.png")));
    }
    catch (Exception ex) {
      Log.error("Can't load app icon", ex);
    }

    hexMapPanel = new HexMapPanel(this.layers);
    hexMapPanel.addHexMapPanelListener(this);

    hexMapPanel.addMouseListener(this);
    hexMapPanel.addMouseMotionListener(this);
    hexMapPanel.addMouseWheelListener(this);

    this.panelMainArea.add(hexMapPanelDesktop, BorderLayout.CENTER);

    this.frameLayers = new FrameLayers(layers, hexMapPanel);
    this.frameTools = new FrameTools();
    this.frameToolOptions = new FrameToolOptions();

    hexMapPanelDesktop.addFrame(this.frameLayers);
    hexMapPanelDesktop.addFrame(this.frameTools);
    hexMapPanelDesktop.addFrame(this.frameToolOptions);

    this.frameLayers.setVisible(true);
    this.frameTools.setVisible(true);
    this.frameToolOptions.setVisible(true);

    hexMapPanelDesktop.setContentPane(hexMapPanel);

    InsideApplicationBus.getInstance().addAppBusListener(this);

    loadSettings();

    resetState();

    Log.info("The MainForm created");

    if (fileToOpen != null) {
      Log.info("Started with parameter: " + fileToOpen);
      final File file = new File(fileToOpen);
      loadFromFile(file);
    }

  }

  private Rectangle loadPosition(final String prefix, final Rectangle dflt) {
    final int x = REGISTRY.getInt(prefix + ".X", dflt.x);
    final int y = REGISTRY.getInt(prefix + ".Y", dflt.y);
    final int width = REGISTRY.getInt(prefix + ".Width", dflt.width);
    final int height = REGISTRY.getInt(prefix + ".Height", dflt.height);

    return new Rectangle(x, y, width, height);
  }

  private void loadSettings() {
    this.setBounds(loadPosition("mainForm", new Rectangle(10, 10, 800, 600)));
    this.frameLayers.setBounds(loadPosition("frameLayers", new Rectangle(10, 10, this.frameLayers.getPreferredSize().width, frameLayers.getPreferredSize().height)));
    this.frameTools.setBounds(loadPosition("frameTools", new Rectangle(10, 10 + this.frameLayers.getPreferredSize().height + 20, this.frameTools.getPreferredSize().width, frameTools.getPreferredSize().height)));
    this.frameToolOptions.setBounds(loadPosition("frameToolOptions", new Rectangle(10 + this.frameLayers.getPreferredSize().width + 20, 10, this.frameToolOptions.getPreferredSize().width, this.frameToolOptions.getPreferredSize().height)));

    this.frameLayers.setVisible(REGISTRY.getBoolean("frameLayers.visible", true));
    this.frameTools.setVisible(REGISTRY.getBoolean("frameTools.visible", true));
    this.frameToolOptions.setVisible(REGISTRY.getBoolean("frameToolOptions.visible", true));

    this.menuViewBackImage.setSelected(REGISTRY.getBoolean("showBackImage", true));
    this.menuShowHexBorders.setSelected(REGISTRY.getBoolean("showHexBorders", true));
  }

  private void writePosition(final String prefix, final Rectangle rect) {
    REGISTRY.putInt(prefix + ".X", rect.x);
    REGISTRY.putInt(prefix + ".Y", rect.y);
    REGISTRY.putInt(prefix + ".Width", rect.width);
    REGISTRY.putInt(prefix + ".Height", rect.height);
  }

  private void saveSettings() {
    writePosition("mainForm", this.getBounds());
    writePosition("frameLayers", this.frameLayers.getBounds());
    writePosition("frameTools", this.frameTools.getBounds());
    writePosition("frameToolOptions", this.frameToolOptions.getBounds());

    REGISTRY.putBoolean("frameLayers.visible", this.frameLayers.isVisible());
    REGISTRY.putBoolean("frameTools.visible", this.frameTools.isVisible());
    REGISTRY.putBoolean("frameToolOptions.visible", this.frameToolOptions.isVisible());

    REGISTRY.putBoolean("showBackImage", this.menuViewBackImage.isSelected());
    REGISTRY.putBoolean("showHexBorders", this.menuShowHexBorders.isSelected());
  }

  protected JInternalFrame createFrame() {
    JInternalFrame frame = new JInternalFrame("Some frame");
    frame.setBounds(30, 30, 300, 300);
    frame.setTitle("Some frame");
    frame.setVisible(true);
    return frame;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    panelMainArea = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    labelCellUnderMouse = new javax.swing.JLabel();
    labelZoomStatus = new javax.swing.JLabel();
    jMenuBar1 = new javax.swing.JMenuBar();
    menuFile = new javax.swing.JMenu();
    menuFileNew = new javax.swing.JMenuItem();
    menuItemFileOpen = new javax.swing.JMenuItem();
    menuFileSave = new javax.swing.JMenuItem();
    menuFileSaveAs = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JPopupMenu.Separator();
    menuFileExportAs = new javax.swing.JMenu();
    menuFileExportAsImage = new javax.swing.JMenuItem();
    menuFileExportAsXML = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JPopupMenu.Separator();
    menuFileDocumentOptions = new javax.swing.JMenuItem();
    jSeparator4 = new javax.swing.JPopupMenu.Separator();
    menuFileExit = new javax.swing.JMenuItem();
    jMenu2 = new javax.swing.JMenu();
    menuEditUndo = new javax.swing.JMenuItem();
    menuEditRedo = new javax.swing.JMenuItem();
    jMenu1 = new javax.swing.JMenu();
    menuViewZoomIn = new javax.swing.JMenuItem();
    menuViewZoomOut = new javax.swing.JMenuItem();
    menuViewZoomReset = new javax.swing.JMenuItem();
    jSeparator2 = new javax.swing.JPopupMenu.Separator();
    menuViewBackImage = new javax.swing.JCheckBoxMenuItem();
    menuShowHexBorders = new javax.swing.JCheckBoxMenuItem();
    menuWindow = new javax.swing.JMenu();
    menuWindowLayers = new javax.swing.JCheckBoxMenuItem();
    menuWindowTools = new javax.swing.JCheckBoxMenuItem();
    menuWindowOptions = new javax.swing.JCheckBoxMenuItem();
    menuHelp = new javax.swing.JMenu();
    menuHelpAbout = new javax.swing.JMenuItem();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("JHexed Map editor");
    setLocationByPlatform(true);
    setMinimumSize(new java.awt.Dimension(300, 300));
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });

    panelMainArea.setLayout(new java.awt.BorderLayout());

    jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    labelCellUnderMouse.setText("   ");

    labelZoomStatus.setText("   ");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(labelCellUnderMouse)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 705, Short.MAX_VALUE)
        .addComponent(labelZoomStatus)
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addGap(0, 0, Short.MAX_VALUE)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelCellUnderMouse)
          .addComponent(labelZoomStatus)))
    );

    panelMainArea.add(jPanel1, java.awt.BorderLayout.PAGE_END);

    getContentPane().add(panelMainArea, java.awt.BorderLayout.CENTER);

    menuFile.setText("File");
    menuFile.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileActionPerformed(evt);
      }
    });

    menuFileNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
    menuFileNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/new.png"))); // NOI18N
    menuFileNew.setText("New");
    menuFileNew.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileNewActionPerformed(evt);
      }
    });
    menuFile.add(menuFileNew);

    menuItemFileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
    menuItemFileOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/folder-open-image.png"))); // NOI18N
    menuItemFileOpen.setText("Open");
    menuItemFileOpen.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuItemFileOpenActionPerformed(evt);
      }
    });
    menuFile.add(menuItemFileOpen);

    menuFileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    menuFileSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/drive-download.png"))); // NOI18N
    menuFileSave.setText("Save");
    menuFileSave.setEnabled(false);
    menuFileSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileSaveActionPerformed(evt);
      }
    });
    menuFile.add(menuFileSave);

    menuFileSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    menuFileSaveAs.setText("Save As...");
    menuFileSaveAs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileSaveAsActionPerformed(evt);
      }
    });
    menuFile.add(menuFileSaveAs);
    menuFile.add(jSeparator1);

    menuFileExportAs.setText("Export as...");

    menuFileExportAsImage.setText("PNG Image");
    menuFileExportAsImage.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileExportAsImageActionPerformed(evt);
      }
    });
    menuFileExportAs.add(menuFileExportAsImage);

    menuFileExportAsXML.setText("Xml file");
    menuFileExportAsXML.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileExportAsXMLActionPerformed(evt);
      }
    });
    menuFileExportAs.add(menuFileExportAsXML);

    menuFile.add(menuFileExportAs);
    menuFile.add(jSeparator3);

    menuFileDocumentOptions.setText("Document options");
    menuFileDocumentOptions.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileDocumentOptionsActionPerformed(evt);
      }
    });
    menuFile.add(menuFileDocumentOptions);
    menuFile.add(jSeparator4);

    menuFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
    menuFileExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/door-open-out.png"))); // NOI18N
    menuFileExit.setText("Exit");
    menuFileExit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuFileExitActionPerformed(evt);
      }
    });
    menuFile.add(menuFileExit);

    jMenuBar1.add(menuFile);

    jMenu2.setText("Edit");

    menuEditUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
    menuEditUndo.setText("Undo");
    menuEditUndo.setEnabled(false);
    menuEditUndo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuEditUndoActionPerformed(evt);
      }
    });
    jMenu2.add(menuEditUndo);

    menuEditRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
    menuEditRedo.setText("Redo");
    menuEditRedo.setEnabled(false);
    menuEditRedo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuEditRedoActionPerformed(evt);
      }
    });
    jMenu2.add(menuEditRedo);

    jMenuBar1.add(jMenu2);

    jMenu1.setText("View");

    menuViewZoomIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
    menuViewZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/magnifier-zoom-in.png"))); // NOI18N
    menuViewZoomIn.setText("Zoom In");
    menuViewZoomIn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuViewZoomInActionPerformed(evt);
      }
    });
    jMenu1.add(menuViewZoomIn);

    menuViewZoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    menuViewZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/magnifier-zoom-out.png"))); // NOI18N
    menuViewZoomOut.setText("Zoom Out");
    menuViewZoomOut.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuViewZoomOutActionPerformed(evt);
      }
    });
    jMenu1.add(menuViewZoomOut);

    menuViewZoomReset.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
    menuViewZoomReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/magnifier-zoom-actual.png"))); // NOI18N
    menuViewZoomReset.setText("Zoom Reset");
    menuViewZoomReset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuViewZoomResetActionPerformed(evt);
      }
    });
    jMenu1.add(menuViewZoomReset);
    jMenu1.add(jSeparator2);

    menuViewBackImage.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
    menuViewBackImage.setSelected(true);
    menuViewBackImage.setText("Show back image");
    menuViewBackImage.setToolTipText("Show/Hide the background image");
    menuViewBackImage.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        menuViewBackImageStateChanged(evt);
      }
    });
    jMenu1.add(menuViewBackImage);

    menuShowHexBorders.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
    menuShowHexBorders.setSelected(true);
    menuShowHexBorders.setText("Show hex borders");
    menuShowHexBorders.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        menuShowHexBordersStateChanged(evt);
      }
    });
    jMenu1.add(menuShowHexBorders);

    jMenuBar1.add(jMenu1);

    menuWindow.setText("Window");

    menuWindowLayers.setSelected(true);
    menuWindowLayers.setText("Layers");
    menuWindowLayers.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuWindowLayersActionPerformed(evt);
      }
    });
    menuWindow.add(menuWindowLayers);

    menuWindowTools.setSelected(true);
    menuWindowTools.setText("Tools");
    menuWindowTools.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuWindowToolsActionPerformed(evt);
      }
    });
    menuWindow.add(menuWindowTools);

    menuWindowOptions.setSelected(true);
    menuWindowOptions.setText("Options");
    menuWindowOptions.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuWindowOptionsActionPerformed(evt);
      }
    });
    menuWindow.add(menuWindowOptions);

    jMenuBar1.add(menuWindow);

    menuHelp.setText("Help");

    menuHelpAbout.setText("About");
    menuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuHelpAboutActionPerformed(evt);
      }
    });
    menuHelp.add(menuHelpAbout);

    jMenuBar1.add(menuHelp);

    setJMenuBar(jMenuBar1);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void menuFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileActionPerformed

  }//GEN-LAST:event_menuFileActionPerformed

  private void menuViewZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewZoomInActionPerformed
    final float z = this.hexMapPanel.getZoom();
    if (z < 5.0f) {
      this.hexMapPanel.setZoom(Math.min(5.0f, z + 0.5f));
    }
  }//GEN-LAST:event_menuViewZoomInActionPerformed

  private void menuViewZoomResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewZoomResetActionPerformed
    this.hexMapPanel.setZoom(1.0f);
  }//GEN-LAST:event_menuViewZoomResetActionPerformed

  private void menuViewZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewZoomOutActionPerformed
    final float z = this.hexMapPanel.getZoom();
    if (z > 0.3f) {
      this.hexMapPanel.setZoom(Math.max(0.3f, z - 0.5f));
    }
  }//GEN-LAST:event_menuViewZoomOutActionPerformed

  private void menuFileExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileExitActionPerformed
    dispose();
  }//GEN-LAST:event_menuFileExitActionPerformed

  private void menuWindowLayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuWindowLayersActionPerformed
    this.frameLayers.setVisible(this.menuWindowLayers.isSelected());
    if (this.frameLayers.isVisible()) {
      FrameUtils.ensureVisibilityAtParent(this.frameLayers);
    }
  }//GEN-LAST:event_menuWindowLayersActionPerformed

  private void menuWindowToolsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuWindowToolsActionPerformed
    this.frameTools.setVisible(this.menuWindowTools.isSelected());
    if (this.frameTools.isVisible()) {
      FrameUtils.ensureVisibilityAtParent(this.frameTools);
    }
  }//GEN-LAST:event_menuWindowToolsActionPerformed

  private void menuWindowOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuWindowOptionsActionPerformed
    this.frameToolOptions.setVisible(this.menuWindowOptions.isSelected());
    if (this.frameToolOptions.isVisible()) {
      FrameUtils.ensureVisibilityAtParent(this.frameToolOptions);
    }
  }//GEN-LAST:event_menuWindowOptionsActionPerformed

  private void loadFromFile(final File file) {
    if (file.isFile()) {

      try {
        final FileContainer container = new FileContainer(file);
        loadState(container);
        updateTheSourceFile(file);
      }
      catch (Exception ex) {
        Log.error("Can't load map from file [" + file + ']', ex);
        JOptionPane.showMessageDialog(this, "Can't load file, may be wrong format", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else {
      Log.warn("Can't find file " + file);
      JOptionPane.showMessageDialog(this, "Can't find file " + file, "Can't find file", JOptionPane.WARNING_MESSAGE);
    }
  }

  private void menuItemFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemFileOpenActionPerformed
    final JFileChooser dlg = new JFileChooser(this.destinationFile);
    dlg.setFileFilter(Utils.JHX_FILE_FILTER);
    if (dlg.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      final File file = dlg.getSelectedFile();
      loadFromFile(file);
    }
  }//GEN-LAST:event_menuItemFileOpenActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    saveSettings();

    this.frameLayers.dispose();
    this.frameToolOptions.dispose();
    this.frameTools.dispose();
    dispose();

    Log.info("The MainForm disposed");
  }//GEN-LAST:event_formWindowClosing

  private void menuFileSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileSaveActionPerformed
    if (this.destinationFile != null) {
      saveStateToFile(this.destinationFile);
    }
  }//GEN-LAST:event_menuFileSaveActionPerformed

  private void menuFileSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileSaveAsActionPerformed
    final JFileChooser fileDlg = new JFileChooser(this.destinationFile);
    fileDlg.setFileFilter(Utils.JHX_FILE_FILTER);

    if (fileDlg.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = fileDlg.getSelectedFile();
      if (!file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jhx")) {
        file = new File(file.getParentFile(), file.getName() + ".jhx");
      }
      if (saveStateToFile(file)) {
        updateTheSourceFile(file);
      }
    }
  }//GEN-LAST:event_menuFileSaveAsActionPerformed

  private boolean saveStateToFile(final File file) {
    try {
      final FileContainer fcontainer = new FileContainer();
      saveState(fcontainer);

      FileOutputStream out = null;
      try {
        out = new FileOutputStream(file);
        fcontainer.write(out);
      }
      finally {
        IOUtils.closeQuietly(out);
      }
      return true;
    }
    catch (IOException ex) {
      Log.error("Can't save map as file [" + file + ']', ex);
      JOptionPane.showMessageDialog(this, "Can't save file for inside error", "IO Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  private void updateTheSourceFile(final File file) {
    if (file == null) {
      this.setTitle("JHexed Map Editor");
    }
    else {
      this.setTitle(file.getAbsolutePath());
    }
    this.destinationFile = file;
    this.menuFileSave.setEnabled(file != null);
  }

  private void menuFileNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileNewActionPerformed
    if (JOptionPane.showConfirmDialog(this, "Do you really want to start new map?", "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
      resetState();
    }
  }//GEN-LAST:event_menuFileNewActionPerformed

  private void menuViewBackImageStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_menuViewBackImageStateChanged
    this.hexMapPanel.setShowBackImage(this.menuViewBackImage.isSelected());
  }//GEN-LAST:event_menuViewBackImageStateChanged

  private void menuEditUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditUndoActionPerformed
    if (this.selectedLayer.undo()) {
      this.selectedLayer.updatePrerasterizedIcons(this.hexMapPanel.getHexShape());
      this.hexMapPanel.repaint();
    }
    updateRedoUndoForCurrentLayer();
  }//GEN-LAST:event_menuEditUndoActionPerformed

  private void menuEditRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditRedoActionPerformed
    if (this.selectedLayer.redo()) {
      this.selectedLayer.updatePrerasterizedIcons(this.hexMapPanel.getHexShape());
      this.hexMapPanel.repaint();
    }
    updateRedoUndoForCurrentLayer();
  }//GEN-LAST:event_menuEditRedoActionPerformed

  private DocumentOptions getDocumentOptions() {
    return new DocumentOptions(
            this.hexMapPanel.getImage(),
            this.layers.getColumnNumber(),
            this.layers.getRowNumber(),
            this.hexMapPanel.getHexOrientation(),
            this.hexMapPanel.getHexRenderer().getLineWidth(),
            this.hexMapPanel.getHexRenderer().getCommonBorderColor(),
            this.documentComments
    );
  }

  private void setDocumentOptions(final DocumentOptions value) {
    this.hexMapPanel.changeHexOrientation(value.getHexOrientation());
    this.layers.resize(value.getColumns(), value.getRows());
    this.hexMapPanel.getHexRenderer().setLineWidth(value.getLineWidth());
    this.hexMapPanel.getHexRenderer().setCommonBorderColor(value.getColor());
    this.documentComments = value.getCommentary();

    this.hexMapPanel.setImage(value.getImage());

    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.HEX_SHAPE, this.hexMapPanel.getHexShape());
  }

  private void menuFileDocumentOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileDocumentOptionsActionPerformed
    final DialogDocumentOptions opts = new DialogDocumentOptions(this, getDocumentOptions());
    opts.setVisible(true);
    final DocumentOptions result = opts.getResult();
    if (result != null) {
      setDocumentOptions(result);
    }
  }//GEN-LAST:event_menuFileDocumentOptionsActionPerformed

  private void menuShowHexBordersStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_menuShowHexBordersStateChanged
    this.hexMapPanel.getHexRenderer().setShowBorders(this.menuShowHexBorders.isSelected());
    this.hexMapPanel.repaint();
  }//GEN-LAST:event_menuShowHexBordersStateChanged

  private void menuHelpAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuHelpAboutActionPerformed
    new DialogAbout(this).setVisible(true);
  }//GEN-LAST:event_menuHelpAboutActionPerformed

  private void menuFileExportAsImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileExportAsImageActionPerformed
    SelectLayersExportData toExport = prepareExportData();

    final DialogSelectLayersForExport dlg = new DialogSelectLayersForExport(this, true, true, toExport);
    dlg.setTitle("Select data for export as Image");
    dlg.setVisible(true);
    toExport = dlg.getResult();
    if (toExport != null) {
      final JFileChooser fileChooser = new JFileChooser(this.lastExportedFile);
      fileChooser.setFileFilter(Utils.PNG_FILE_FILTER);
      if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        this.lastExportedFile = fileChooser.getSelectedFile();

        final ImageExporter exporter = new ImageExporter(getDocumentOptions(), toExport, this.cellComments);
        try {
          exporter.export(this.lastExportedFile);
        }
        catch (IOException ex) {
          Log.error("Can't export Image", ex);
          JOptionPane.showMessageDialog(null, "Can't export as Image for error", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }//GEN-LAST:event_menuFileExportAsImageActionPerformed

  private void menuFileExportAsXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFileExportAsXMLActionPerformed
    SelectLayersExportData toExport = prepareExportData();

    final DialogSelectLayersForExport dlg = new DialogSelectLayersForExport(this, true, true, toExport);
    dlg.setTitle("Select data for XML export");
    dlg.setVisible(true);
    toExport = dlg.getResult();
    if (toExport != null) {
      final JFileChooser fileChooser = new JFileChooser(this.lastExportedFile);
      fileChooser.setFileFilter(Utils.XML_FILE_FILTER);
      if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        this.lastExportedFile = fileChooser.getSelectedFile();

        final XmlExporter exporter = new XmlExporter(getDocumentOptions(), toExport, this.cellComments);
        try {
          exporter.export(this.lastExportedFile);
        }
        catch (IOException ex) {
          Log.error("Can't export XML", ex);
          JOptionPane.showMessageDialog(null, "Can't export as XML for error", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }//GEN-LAST:event_menuFileExportAsXMLActionPerformed

  private SelectLayersExportData prepareExportData() {
    final SelectLayersExportData result = new SelectLayersExportData();

    result.setBackgroundImageExport(this.menuViewBackImage.isSelected());
    result.setCellCommentariesExport(true);
    
    for (int i = 0; i < this.layers.getSize(); i++) {
      final HexFieldLayer field = this.layers.getElementAt(i).getLayer();
      result.addLayer(field.isLayerVisible(), field);
    }

    return result;
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JMenu jMenu1;
  private javax.swing.JMenu jMenu2;
  private javax.swing.JMenuBar jMenuBar1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPopupMenu.Separator jSeparator1;
  private javax.swing.JPopupMenu.Separator jSeparator2;
  private javax.swing.JPopupMenu.Separator jSeparator3;
  private javax.swing.JPopupMenu.Separator jSeparator4;
  private javax.swing.JLabel labelCellUnderMouse;
  private javax.swing.JLabel labelZoomStatus;
  private javax.swing.JMenuItem menuEditRedo;
  private javax.swing.JMenuItem menuEditUndo;
  private javax.swing.JMenu menuFile;
  private javax.swing.JMenuItem menuFileDocumentOptions;
  private javax.swing.JMenuItem menuFileExit;
  private javax.swing.JMenu menuFileExportAs;
  private javax.swing.JMenuItem menuFileExportAsImage;
  private javax.swing.JMenuItem menuFileExportAsXML;
  private javax.swing.JMenuItem menuFileNew;
  private javax.swing.JMenuItem menuFileSave;
  private javax.swing.JMenuItem menuFileSaveAs;
  private javax.swing.JMenu menuHelp;
  private javax.swing.JMenuItem menuHelpAbout;
  private javax.swing.JMenuItem menuItemFileOpen;
  private javax.swing.JCheckBoxMenuItem menuShowHexBorders;
  private javax.swing.JCheckBoxMenuItem menuViewBackImage;
  private javax.swing.JMenuItem menuViewZoomIn;
  private javax.swing.JMenuItem menuViewZoomOut;
  private javax.swing.JMenuItem menuViewZoomReset;
  private javax.swing.JMenu menuWindow;
  private javax.swing.JCheckBoxMenuItem menuWindowLayers;
  private javax.swing.JCheckBoxMenuItem menuWindowOptions;
  private javax.swing.JCheckBoxMenuItem menuWindowTools;
  private javax.swing.JPanel panelMainArea;
  // End of variables declaration//GEN-END:variables

  private void updateActivehexCoord(final HexPosition position) {
    final String comment = this.cellComments.getForHex(position);
    this.labelCellUnderMouse.setText("Hex " + position.getColumn() + ',' + position.getRow()+' '+(comment == null? "" : comment));
    if (this.hexMapPanel.isValidPosition(position)) {
      final StringBuilder buffer = new StringBuilder();

      if (comment !=null){
        buffer.append("<h4>").append(StringEscapeUtils.escapeHtml4(comment)).append("</h4>");
      }
      
      for (int i = 0; i < this.layers.getSize(); i++) {
        final HexFieldLayer field = this.layers.getElementAt(i).getLayer();
        if (field.isLayerVisible()) {
          final String layerName = field.getLayerName().isEmpty() ? "Untitled" : field.getLayerName();
          final HexFieldValue layerValue = field.getHexValueAtPos(position.getColumn(), position.getRow());
          if (layerValue != null) {
            if (buffer.length() > 0) {
              buffer.append("<br>");
            }
            final String valueName = layerValue.getName().isEmpty() ? layerValue.getComment() : layerValue.getName();
            buffer.append("<b>").append(StringEscapeUtils.escapeHtml4(layerName)).append(":</b>").append(StringEscapeUtils.escapeHtml4(valueName));
          }
        }
      }
      if (buffer.length() > 0) {
        this.hexMapPanel.setToolTipText("<html>" + buffer + "</html>");
      }
      else {
        this.hexMapPanel.setToolTipText(null);
      }
    }
    else {
      this.hexMapPanel.setToolTipText(null);
    }
  }

  private void useCurrentToolAtPosition(final HexPosition position) {
    if (this.selectedToolType != null && this.selectedLayer != null && this.hexMapPanel.isValidPosition(position)) {
      updateActivehexCoord(position);
      this.selectedToolType.processTool(this.hexMapPanel.getHexEngine(), this.selectedLayer, position);
      this.hexMapPanel.repaint();
    }
  }

  @Override
  public void mouseClicked(final MouseEvent e) {
    switch (e.getButton()) {
      case MouseEvent.BUTTON1: {
        final HexPosition hexNumber = this.hexMapPanel.getHexPosition(e.getPoint());
        useCurrentToolAtPosition(hexNumber);
      }
      break;
      case MouseEvent.BUTTON3:{
        if (e.getClickCount()>1){
          final HexPosition hexNumber = this.hexMapPanel.getHexPosition(e.getPoint());
          if (hexNumber!=null && this.hexMapPanel.isValidPosition(hexNumber)){
            // open dialog for cell comment
            final CellCommentDialog commentDialog = new CellCommentDialog(this, "Commentaries for "+hexNumber.getColumn()+","+hexNumber.getRow()+" cell", this.cellComments.getForHex(hexNumber));
            commentDialog.setVisible(true);
            final String result = commentDialog.getResult();
            if (result!=null){
              this.cellComments.setForHex(hexNumber, result);
            }
          }
        }
      }
    }
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    switch (e.getButton()) {
      case MouseEvent.BUTTON1: {
        if (selectedToolType != null && this.selectedLayer != null) {
          this.selectedLayer.addUndo();
          updateRedoUndoForCurrentLayer();
          final HexPosition hexNumber = this.hexMapPanel.getHexPosition(e.getPoint());
          useCurrentToolAtPosition(hexNumber);
        }
      }
      break;
      case MouseEvent.BUTTON3: {
        this.dragging = true;
        this.hexMapPanelDesktop.initDrag(e.getPoint());
      }
      break;
    }
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    switch (e.getButton()) {
      case MouseEvent.BUTTON3: {
        this.dragging = false;
        this.hexMapPanelDesktop.endDrag();
      }
      break;
    }
  }

  @Override
  public void mouseEntered(final MouseEvent e) {
  }

  @Override
  public void mouseExited(final MouseEvent e) {
    this.labelCellUnderMouse.setText("   ");
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      final HexPosition hexNumber = this.hexMapPanel.getHexPosition(e.getPoint());
      useCurrentToolAtPosition(hexNumber);
    }
    else {
      if (this.dragging) {
        this.hexMapPanelDesktop.processDrag(e.getPoint());
      }
    }
  }

  @Override
  public void mouseMoved(final MouseEvent e) {
    final HexPosition hexNumber = this.hexMapPanel.getHexPosition(e.getPoint());
    updateActivehexCoord(hexNumber);
    ToolTipManager.sharedInstance().mouseMoved(e);
  }

  @Override
  public void onZoomChanged(final HexMapPanel source, final float scale) {
    final int zoomPercent = Math.round(100 * scale);
    this.labelZoomStatus.setText("Zoom: " + zoomPercent + '%');
  }

  @Override
  public void onAppBusEvent(final Object source, final InsideApplicationBus bus, final InsideApplicationBus.AppBusEvent event, final Object... objects) {
    switch (event) {
      case A_FRAME_CHANGED_ITS_STATUS: {
        if (objects[0] == this.frameLayers) {
          this.menuWindowLayers.setSelected(this.frameLayers.isVisible());
        }
        else if (objects[0] == this.frameToolOptions) {
          this.menuWindowOptions.setSelected(this.frameToolOptions.isVisible());
        }
        else if (objects[0] == this.frameTools) {
          this.menuWindowTools.setSelected(this.frameTools.isVisible());
        }
      }
      break;
      case HEX_FIELD_NEEDS_REPAINT: {
        this.hexMapPanel.repaint();
      }
      break;
      case SELECTED_TOOL_CHANGED: {
        this.selectedToolType = (ToolType) objects[0];
      }
      break;
      case SELECTED_LAYER_CHANGED: {
        this.selectedLayer = (HexFieldLayer) objects[0];
        this.layers.resetaAllRedoUndo();
        updateRedoUndoForCurrentLayer();
      }
      break;
      case LAYER_NEEDS_EDITION: {
        final LayerRecordPanel panel = (LayerRecordPanel) objects[0];
        final EditLayerDialog dlg = new EditLayerDialog(this, this.layers, panel.getLayer(), this.hexMapPanel.getHexShape());
        dlg.setVisible(true);
        final HexFieldLayer result = dlg.getResult();
        if (result != null) {
          result.updatePrerasterizedIcons(this.hexMapPanel.getHexShape());
          panel.updateLayer(result);
          InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.SELECTED_LAYER_CHANGED, result);
          this.hexMapPanel.repaint();
        }
      }
      break;
      case REQUEST_EVENT: {
        if (objects[0] == InsideApplicationBus.AppBusEvent.HEX_SHAPE) {
          InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.HEX_SHAPE, this.hexMapPanel.getHexShape());
        }
      }
      break;
    }
  }

  public void saveState(final FileContainer container) throws IOException {
    container.addSection(new FileContainerSection("docsettings", getDocumentOptions().toByteArray()));
    container.addSection(new FileContainerSection("layers", this.layers.toByteArray()));
    container.addSection(new FileContainerSection("cellcomments", this.cellComments.toByteArray()));
  }

  public void loadState(final FileContainer container) throws IOException {
    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.SELECTED_LAYER_CHANGED, (Object) null);

    final FileContainerSection docsettings = container.findSectionForName("docsettings");
    final FileContainerSection layers = container.findSectionForName("layers");
    final FileContainerSection cellComments = container.findSectionForName("cellcomments");

    if (layers == null) {
      throw new IOException("Can't find 'layers' section");
    }
    if (docsettings == null) {
      throw new IOException("Can't find 'docsettings' section");
    }

    this.cellComments.clear();
    if (cellComments!=null){
      this.cellComments.fromByteArray(cellComments.getData());
    }
    
    DocumentOptions opts = new DocumentOptions(docsettings.getData());
    this.layers.fromByteArray(layers.getData());
    setDocumentOptions(opts);

    this.hexMapPanel.revalidate();
    this.hexMapPanel.repaint();
  }

  private void resetState() {
    this.documentComments = null;
    this.layers.init();
    this.hexMapPanel.init();
    updateTheSourceFile(null);
    updateRedoUndoForCurrentLayer();
    this.hexMapPanel.setZoom(1.0f);
    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.HEX_SHAPE, this.hexMapPanel.getHexShape());
    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.SELECTED_LAYER_CHANGED, (Object) null);
  }

  private void updateRedoUndoForCurrentLayer() {
    if (this.selectedLayer == null) {
      this.menuEditRedo.setEnabled(false);
      this.menuEditUndo.setEnabled(false);
    }
    else {
      this.menuEditRedo.setEnabled(this.selectedLayer.hasRedo());
      this.menuEditUndo.setEnabled(this.selectedLayer.hasUndo());
    }
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    if ((e.getModifiers() & MouseEvent.CTRL_MASK) != 0) {
      final int rotation = e.getWheelRotation();
      if (rotation < 0) {
        menuViewZoomInActionPerformed(null);
      }
      else {
        menuViewZoomOutActionPerformed(null);
      }
    }
  }
}
