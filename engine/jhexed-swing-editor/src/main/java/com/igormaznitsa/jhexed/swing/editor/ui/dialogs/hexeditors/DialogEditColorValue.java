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

import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.AbstractDialog;
import com.igormaznitsa.jhexed.values.HexColorValue;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

public class DialogEditColorValue extends AbstractDialog implements HexEditor, ActionListener {
  private static final long serialVersionUID = -1165115255188795383L;

  private static final class ColorLabel extends JLabel {
    private static final long serialVersionUID = -9006240682239425479L;
    
    int red;
    int green;
    int blue;
    int alpha;
    
    public ColorLabel(final ActionListener listener, final Color color){
      super();
      setColor(color);
      setToolTipText("Click twice to open the color chooser");
      final ColorLabel theInstance = this;
      
      addMouseListener(new MouseInputListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount()>1){
            final Color newColor = JColorChooser.showDialog(null, "Select color", new Color(red,green,blue,alpha));
            if (newColor!=null){
              setColor(newColor);
              if (listener!=null){
                listener.actionPerformed(new ActionEvent(theInstance, 0, "ColorChanged"));
              }
            }
          }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
      });
    }

    public void setColor(final Color color){
      final int r = color == null ? 0xFF : color.getRed();
      final int g = color == null ? 0xFF : color.getGreen();
      final int b = color == null ? 0xFF : color.getBlue();
      final int a = color == null ? 0x7F : color.getAlpha();
      
      setRed(r);
      setGreen(g);
      setBlue(b);
      setAlpha(a);
    }

    public Color getColor(){
      return new Color(this.red, this.green, this.blue, this.alpha);
    }
    
    public int getRed(){
      return this.red;
    }
    
    public void setRed(final int value){
      this.red = Math.max(0,Math.min(255, value));
      repaint();
    }

    public int getGreen(){
      return this.green;
    }
    
    public void setGreen(final int value){
      this.green = Math.max(0, Math.min(255, value));
      repaint();
    }
    
    public int getBlue(){
      return this.blue;
    }
    
    public void setBlue(final int value){
      this.blue = Math.max(0, Math.min(255, value));
      repaint();
    }
    
    public int getAlpha(){
      return this.alpha;
    }
    
    public void setAlpha(final int value){
      this.alpha = Math.max(0, Math.min(255, value));
      repaint();
    }
    
    @Override
    protected void paintComponent(final Graphics g) {
      g.setColor(new Color(red,green,blue,alpha));
      final Rectangle r = g.getClipBounds();
      g.fillRect(r.x, r.y, r.width, r.height);
    }
  }
  
  private final ColorLabel colorLabel;
  
  private final HexColorValue value;
  private HexColorValue result = null;
  
  public DialogEditColorValue(final java.awt.Frame parent, final HexColorValue value) {
    super(parent, true);
    initComponents();
    
    this.colorLabel = new ColorLabel(this,Color.GRAY);
    
    if (value == null){
      setTitle("New color value");
      this.value = new HexColorValue("", "", Color.white, -1);
    }else{
      setTitle("Edit color value '"+value.getName()+'\'');
      this.value = (HexColorValue) value.cloneValue();
    }
    panelColor.add(this.colorLabel, BorderLayout.CENTER);
  
    this.textName.setText(this.value.getName());
    this.textComments.setText(this.value.getComment());
    
    this.colorLabel.setColor(this.value.getColor());
    
    updateSliderFromLabel();
  }
  
  private void updateSliderFromLabel(){
    this.sliderRed.setValue(this.colorLabel.getRed());
    this.sliderGreen.setValue(this.colorLabel.getGreen());
    this.sliderBlue.setValue(this.colorLabel.getBlue());
    this.sliderAlpha.setValue(this.colorLabel.getAlpha());
  }
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonCancel = new javax.swing.JButton();
    buttonSave = new javax.swing.JButton();
    sliderBlue = new javax.swing.JSlider();
    jLabel2 = new javax.swing.JLabel();
    textName = new javax.swing.JTextField();
    jScrollPane1 = new javax.swing.JScrollPane();
    textComments = new javax.swing.JEditorPane();
    panelColor = new javax.swing.JPanel();
    sliderGreen = new javax.swing.JSlider();
    sliderRed = new javax.swing.JSlider();
    sliderAlpha = new javax.swing.JSlider();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Edit a color value");
    setLocationByPlatform(true);

    buttonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/cross.png"))); // NOI18N
    buttonCancel.setText("Cancel");
    buttonCancel.setToolTipText("Reject changes");
    buttonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonCancelActionPerformed(evt);
      }
    });

    buttonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/igormaznitsa/jhexed/swing/editor/icons/tick.png"))); // NOI18N
    buttonSave.setText("Ok");
    buttonSave.setToolTipText("Save changes");
    buttonSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonSaveActionPerformed(evt);
      }
    });

    sliderBlue.setMajorTickSpacing(50);
    sliderBlue.setMaximum(255);
    sliderBlue.setMinorTickSpacing(5);
    sliderBlue.setPaintTicks(true);
    sliderBlue.setBorder(javax.swing.BorderFactory.createTitledBorder("Blue"));
    sliderBlue.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        sliderBlueStateChanged(evt);
      }
    });

    jLabel2.setText("Name:");

    textName.setToolTipText("Name of the hex value");

    jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Commentary"));

    textComments.setToolTipText("Commentaries");
    jScrollPane1.setViewportView(textComments);

    panelColor.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));
    panelColor.setLayout(new java.awt.BorderLayout());

    sliderGreen.setMajorTickSpacing(50);
    sliderGreen.setMaximum(255);
    sliderGreen.setMinorTickSpacing(5);
    sliderGreen.setPaintTicks(true);
    sliderGreen.setBorder(javax.swing.BorderFactory.createTitledBorder("Green"));
    sliderGreen.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        sliderGreenStateChanged(evt);
      }
    });

    sliderRed.setMajorTickSpacing(50);
    sliderRed.setMaximum(255);
    sliderRed.setMinorTickSpacing(5);
    sliderRed.setPaintTicks(true);
    sliderRed.setBorder(javax.swing.BorderFactory.createTitledBorder("Red"));
    sliderRed.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        sliderRedStateChanged(evt);
      }
    });

    sliderAlpha.setMajorTickSpacing(50);
    sliderAlpha.setMaximum(255);
    sliderAlpha.setMinorTickSpacing(5);
    sliderAlpha.setPaintTicks(true);
    sliderAlpha.setSnapToTicks(true);
    sliderAlpha.setBorder(javax.swing.BorderFactory.createTitledBorder("Alpha"));
    sliderAlpha.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        sliderAlphaStateChanged(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textName))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(buttonSave)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel))
          .addGroup(layout.createSequentialGroup()
            .addComponent(panelColor, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(sliderBlue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(sliderRed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(sliderGreen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(sliderAlpha, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addContainerGap())
    );

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonCancel, buttonSave});

    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(textName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addGroup(layout.createSequentialGroup()
            .addComponent(sliderRed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(3, 3, 3)
            .addComponent(sliderBlue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(sliderGreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(sliderAlpha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(panelColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonCancel)
          .addComponent(buttonSave))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void sliderRedStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderRedStateChanged
    this.colorLabel.setRed(this.sliderRed.getValue());
  }//GEN-LAST:event_sliderRedStateChanged

  private void sliderBlueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderBlueStateChanged
    this.colorLabel.setBlue(this.sliderBlue.getValue());
  }//GEN-LAST:event_sliderBlueStateChanged

  private void sliderGreenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderGreenStateChanged
    this.colorLabel.setGreen(this.sliderGreen.getValue());
  }//GEN-LAST:event_sliderGreenStateChanged

  private void sliderAlphaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sliderAlphaStateChanged
    this.colorLabel.setAlpha(this.sliderAlpha.getValue());
  }//GEN-LAST:event_sliderAlphaStateChanged

  private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
    this.value.setName(this.textName.getText());
    this.value.setComment(this.textComments.getText());
    this.value.setColor(this.colorLabel.getColor());
    this.result = this.value;
    dispose();
  }//GEN-LAST:event_buttonSaveActionPerformed

  private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
    dispose();
  }//GEN-LAST:event_buttonCancelActionPerformed

  @Override
  public HexFieldValue getHexEditResult() {
    return this.result;
  }  

  @Override
  public HexFieldValue showDialog() {
    setVisible(true);
    return getHexEditResult();
  }
  
  @Override
  public void actionPerformed(final ActionEvent e) {
    if (e.getSource() == this.colorLabel){
      updateSliderFromLabel();
    }
  }  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonSave;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JPanel panelColor;
  private javax.swing.JSlider sliderAlpha;
  private javax.swing.JSlider sliderBlue;
  private javax.swing.JSlider sliderGreen;
  private javax.swing.JSlider sliderRed;
  private javax.swing.JEditorPane textComments;
  private javax.swing.JTextField textName;
  // End of variables declaration//GEN-END:variables

  @Override
  public void processEscape(final ActionEvent e) {
    buttonCancelActionPerformed(e);
  }
}
