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
package com.igormaznitsa.jhexed.swing.editor.ui.dialogs.hexeditors;

import com.igormaznitsa.jhexed.renders.svg.SVGImage;
import com.igormaznitsa.jhexed.swing.editor.Log;
import com.igormaznitsa.jhexed.values.HexSVGImageValue;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import com.igormaznitsa.jhexed.swing.editor.ui.Utils;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class DialogEditSVGImageValue extends javax.swing.JDialog implements HexEditor {

  private static final int PREVIEW_SIZE = 80;

  private static File lastOpenedFile = null;
  private static final long serialVersionUID = 4558151613564722143L;

  private final HexSVGImageValue value;
  private HexSVGImageValue result;
  private final java.awt.Frame parent;

  public DialogEditSVGImageValue(java.awt.Frame parent, final HexSVGImageValue value) {
    super(parent, true);
    this.parent = parent;
    initComponents();

    if (value == null) {
      setTitle("New SVG value");
      this.value = new HexSVGImageValue("", "", null, -1);
      this.buttonOk.setEnabled(false);
    }
    else {
      setTitle("Edit SVG value '" + value.getName() + '\'');
      this.value = (HexSVGImageValue) value.cloneValue();
    }
    load();

    if (value.getImage() == null) {
      buttonOk.setEnabled(false);
      buttonSaveAs.setEnabled(false);
    }

    this.setLocationRelativeTo(parent);
  }

  private void load() {
    this.textName.setText(this.value.getName());
    this.textComments.setText(this.value.getComment());
    final SVGImage img = this.value.getImage();
    if (img == null) {
      this.buttonSaveAs.setEnabled(false);
      this.panelPreview.removeAll();
    }
    else {
      try {
        final JLabel label = new JLabel(new ImageIcon(img.rasterize(PREVIEW_SIZE, PREVIEW_SIZE, BufferedImage.TYPE_INT_ARGB)));
        this.panelPreview.removeAll();
        this.panelPreview.add(label, BorderLayout.CENTER);
        this.panelPreview.revalidate();
        this.panelPreview.repaint();
      }
      catch (IOException ex) {
        Log.error("Can't rasterize image", ex);
      }
    }
  }

  private void save() {
    this.value.setName(this.textName.getText());
    this.value.setComment(this.textComments.getText());
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    textName = new javax.swing.JTextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    textComments = new javax.swing.JTextArea();
    buttonCancel = new javax.swing.JButton();
    buttonOk = new javax.swing.JButton();
    panelPreview = new javax.swing.JPanel();
    buttonLoad = new javax.swing.JButton();
    buttonSaveAs = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setLocationByPlatform(true);

    jLabel1.setText("Name:");

    jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Commentary"));

    textComments.setColumns(20);
    textComments.setRows(5);
    jScrollPane1.setViewportView(textComments);

    buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/cross.png"))); // NOI18N
    buttonCancel.setText("Cancel");
    buttonCancel.setToolTipText("Reject changes");
    buttonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonCancelActionPerformed(evt);
      }
    });

    buttonOk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/tick.png"))); // NOI18N
    buttonOk.setText("Ok");
    buttonOk.setToolTipText("Save the changes");
    buttonOk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonOkActionPerformed(evt);
      }
    });

    panelPreview.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));
    panelPreview.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        panelPreviewMouseClicked(evt);
      }
    });
    panelPreview.setLayout(new java.awt.BorderLayout());

    buttonLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/drive-upload.png"))); // NOI18N
    buttonLoad.setText("Load from file");
    buttonLoad.setToolTipText("Load the image from a file");
    buttonLoad.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonLoadActionPerformed(evt);
      }
    });

    buttonSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/drive-download.png"))); // NOI18N
    buttonSaveAs.setText("Save as file");
    buttonSaveAs.setToolTipText("Save the image as a file");
    buttonSaveAs.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonSaveAsActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(panelPreview, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(buttonOk)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel))
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonLoad)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonSaveAs)
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonCancel, buttonOk});

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonLoad, buttonSaveAs});

    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(panelPreview, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonLoad)
          .addComponent(buttonSaveAs))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel)
          .addComponent(buttonOk))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void buttonLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoadActionPerformed
    final JFileChooser openDialog = new JFileChooser(lastOpenedFile);
    openDialog.setDialogTitle("Select SVG file");
    openDialog.setAcceptAllFileFilterUsed(true);
    openDialog.setFileFilter(Utils.SVG_FILE_FILTER);

    if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      lastOpenedFile = openDialog.getSelectedFile();
      try {
        final SVGImage img = new SVGImage(lastOpenedFile);

        this.value.setImage(img);

        this.panelPreview.removeAll();
        this.panelPreview.add(new JLabel(new ImageIcon(img.rasterize(PREVIEW_SIZE, PREVIEW_SIZE, BufferedImage.TYPE_INT_ARGB))), BorderLayout.CENTER);
        this.panelPreview.revalidate();
        this.panelPreview.repaint();

        this.buttonOk.setEnabled(true);
        this.buttonSaveAs.setEnabled(true);
      }
      catch (IOException ex) {
        Log.error("Can't rasterize image [" + lastOpenedFile + ']', ex);
        JOptionPane.showMessageDialog(this, "Can't load the file, may be it is not a SVG file", "Can't load the file", JOptionPane.ERROR_MESSAGE);
      }
    }
  }//GEN-LAST:event_buttonLoadActionPerformed

  private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
    save();
    this.result = this.value;
    dispose();
  }//GEN-LAST:event_buttonOkActionPerformed

  private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
    dispose();
  }//GEN-LAST:event_buttonCancelActionPerformed

  private void buttonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveAsActionPerformed
    final JFileChooser dlg = new JFileChooser();
    dlg.addChoosableFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".svg");
      }

      @Override
      public String getDescription() {
        return "SVG files (*.svg)";
      }
    });

    if (dlg.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File file = dlg.getSelectedFile();

      if (FilenameUtils.getExtension(file.getName()).isEmpty()) {
        file = new File(file.getParentFile(), file.getName() + ".svg");
      }

      if (file.exists() && JOptionPane.showConfirmDialog(this.parent, "Overwrite file '" + file.getAbsolutePath() + "\'?", "Overwriting", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
        return;
      }
      try {
        FileUtils.writeByteArrayToFile(file, this.value.getImage().getImageData());
      }
      catch (IOException ex) {
        Log.error("Can't write image [" + file + ']', ex);
        JOptionPane.showMessageDialog(this, "Can't save the file for error!", "IO Error", JOptionPane.ERROR_MESSAGE);
      }
    }


  }//GEN-LAST:event_buttonSaveAsActionPerformed

  private void panelPreviewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelPreviewMouseClicked
    if (evt.getClickCount() > 1) {
      this.buttonLoadActionPerformed(null);
    }
  }//GEN-LAST:event_panelPreviewMouseClicked


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonLoad;
  private javax.swing.JButton buttonOk;
  private javax.swing.JButton buttonSaveAs;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPanel panelPreview;
  private javax.swing.JTextArea textComments;
  private javax.swing.JTextField textName;
  // End of variables declaration//GEN-END:variables

  @Override
  public HexFieldValue getHexEditResult() {
    return this.result;
  }

  @Override
  public HexFieldValue showDialog() {
    this.setVisible(true);
    return getHexEditResult();
  }

}
