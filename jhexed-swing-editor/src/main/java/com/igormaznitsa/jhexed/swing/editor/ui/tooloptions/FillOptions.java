package com.igormaznitsa.jhexed.swing.editor.ui.tooloptions;

import com.igormaznitsa.jhexed.swing.editor.model.*;
import com.igormaznitsa.jhexed.swing.editor.model.values.HexValue;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

public class FillOptions extends javax.swing.JPanel implements AppBus.AppBusListener, LayerValueIconList.LayerIconListListener {

  private static final long serialVersionUID = 2906524676479899740L;

  private HexValue fillValue;
  private HexValue borderValue;
  
  public FillOptions() {
    initComponents();
    AppBus.getInstance().addAppBusListener(this);
    AppBus.getInstance().fireEvent(this, AppBus.AppBusEvent.REQUEST_EVENT, AppBus.AppBusEvent.HEX_SHAPE);
  
    this.layerIconList.addLayerIconListListener(this);
  }

  public HexValue getFillValue(){
    return fillValue;
  }
  
  public HexValue getBorderValue(){
    return this.borderValue;
  }
  
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jScrollPane1 = new javax.swing.JScrollPane();
    layerIconList = new com.igormaznitsa.jhexed.swing.editor.ui.tooloptions.LayerValueIconList();
    jPanel1 = new javax.swing.JPanel();
    labelFillValue = new javax.swing.JLabel();
    jPanel2 = new javax.swing.JPanel();
    labelBorderValue = new javax.swing.JLabel();

    jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Allowable layer values"));
    jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setViewportView(layerIconList);

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fill value"));
    jPanel1.setLayout(new java.awt.BorderLayout());

    labelFillValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jPanel1.add(labelFillValue, java.awt.BorderLayout.CENTER);

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Border value"));
    jPanel2.setLayout(new java.awt.BorderLayout());

    labelBorderValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jPanel2.add(labelBorderValue, java.awt.BorderLayout.CENTER);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
          .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        .addGap(13, 13, 13))
    );

    layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel1, jPanel2});

  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JLabel labelBorderValue;
  private javax.swing.JLabel labelFillValue;
  private com.igormaznitsa.jhexed.swing.editor.ui.tooloptions.LayerValueIconList layerIconList;
  // End of variables declaration//GEN-END:variables


  private void resetValue(){
    this.borderValue = null;
    this.fillValue = null;
    this.labelBorderValue.setIcon(null);
    this.labelFillValue.setIcon(null);
  }
  
  @Override
  public void onAppBusEvent(final Object source, final AppBus bus, final AppBus.AppBusEvent event, final Object... objects) {
    switch(event){
      case SELECTED_LAYER_CHANGED:{
        final LayerDataField layer = (LayerDataField) objects[0];
        layerIconList.setLayerField(layer);

        resetValue();
        
      }break;
      case HEX_SHAPE:{
        layerIconList.setIconShape((Path2D)objects[0]);
      }break;
    }
  }
  
  @Override
  public void onLeftClick(final HexValue h, final ImageIcon icon) {
    this.fillValue = h;
    this.labelFillValue.setIcon(icon);
  }

  @Override
  public void onRightClick(final HexValue h, final ImageIcon icon) {
    this.borderValue = h;
    this.labelBorderValue.setIcon(icon);
  }
}
