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
package com.igormaznitsa.jhexed.swing.editor.ui.tooloptions;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.swing.editor.model.*;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

public class EraserOptions extends javax.swing.JPanel implements InsideApplicationBus.AppBusListener, LayerValueIconList.LayerIconListListener {
  private static final long serialVersionUID = 2906524676479899740L;
  
  private HexFieldValue selectedValue;
  
  public EraserOptions() {
    initComponents();
    InsideApplicationBus.getInstance().addAppBusListener(this);
    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.REQUEST_EVENT, InsideApplicationBus.AppBusEvent.HEX_SHAPE);
  
    this.layerIconList.addLayerIconListListener(this);
  }

  public HexFieldValue getHexValue(){
    return this.selectedValue;
  }
  
  public int getPencilWidth(){
    return this.sliderWidth.getValue();
  }
  
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    sliderWidth = new javax.swing.JSlider();
    layerIconList = new com.igormaznitsa.jhexed.swing.editor.ui.tooloptions.LayerValueIconList();

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Radius of the eraser"));

    sliderWidth.setMajorTickSpacing(1);
    sliderWidth.setMaximum(10);
    sliderWidth.setMinimum(1);
    sliderWidth.setMinorTickSpacing(1);
    sliderWidth.setPaintLabels(true);
    sliderWidth.setPaintTicks(true);
    sliderWidth.setSnapToTicks(true);
    sliderWidth.setToolTipText("Radius of the tool");
    sliderWidth.setValue(1);

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(sliderWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(sliderWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    sliderWidth.getAccessibleContext().setAccessibleDescription("Width of the pencil");

    layerIconList.setBorder(javax.swing.BorderFactory.createTitledBorder("Allowable layer values"));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(layerIconList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(layerIconList, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
        .addContainerGap())
    );
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private com.igormaznitsa.jhexed.swing.editor.ui.tooloptions.LayerValueIconList layerIconList;
  private javax.swing.JSlider sliderWidth;
  // End of variables declaration//GEN-END:variables


  @Override
  public void onAppBusEvent(final Object source, final InsideApplicationBus bus, final InsideApplicationBus.AppBusEvent event, final Object... objects) {
    switch(event){
      case SELECTED_LAYER_CHANGED:{
        final HexFieldLayer layer = (HexFieldLayer) objects[0];
        layerIconList.setLayerField(layer);
        this.selectedValue = null;
        this.layerIconList.clearSelection();
      }break;
      case HEX_SHAPE:{
        this.layerIconList.setIconShape((Path2D)objects[0]);
      }break;
    }
  }

  @Override
  public void onLeftClick(final HexFieldValue value, final ImageIcon icon) {
    this.selectedValue = value;
  }

  @Override
  public void onRightClick(final HexFieldValue value, final ImageIcon icon) {
    this.selectedValue = value;
  }

  
}
