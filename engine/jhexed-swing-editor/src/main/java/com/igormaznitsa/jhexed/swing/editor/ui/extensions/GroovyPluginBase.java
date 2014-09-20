package com.igormaznitsa.jhexed.swing.editor.ui.extensions;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.swing.editor.model.LayerListModel;
import com.igormaznitsa.jhexed.swing.editor.ui.MainForm;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.layers.LayerRecordPanel;
import com.igormaznitsa.jhexed.values.HexColorValue;
import java.awt.Color;
import javax.swing.JOptionPane;

public class GroovyPluginBase {

  private final LayerListModel layerListModel;
  private final MainForm frame;

  public GroovyPluginBase(final MainForm frame, final LayerListModel listModel) {
    this.frame = frame;
    this.layerListModel = listModel;
  }

  public int selectValueDialog(final String text, final HexFieldLayer layer) {
    final SelectValueDialog dialog = new SelectValueDialog(this.frame, text, layer);
    dialog.setVisible(true);
    return dialog.getSelectedIndex();
  }

  public void addUndo(final HexFieldLayer layer) {
    if (layer != null) {
      this.frame.addedUndoStep(new HexFieldLayer[]{layer});
    }
  }

  public void addUndo(final HexFieldLayer[] layers) {
    if (layers != null) {
      this.frame.addedUndoStep(layers);
    }
  }

  public HexColorValue addColorValue(final HexFieldLayer layer, final String name, final String comment, final Color color){
    if (color==null) throw new NullPointerException("Color is null");
    final HexColorValue value = new HexColorValue(name == null ? "" : name, comment == null ? "" : comment, color, -1);
    layer.addValue(value);
    layer.updatePrerasterizedIcons(this.frame.getHexShape());
    return value;
  }
  
  public int getLayersNumber(){
    return this.layerListModel.getSize();
  }
  
  public void deleteLayer(final HexFieldLayer layer){
    this.layerListModel.removeLayer(layer);
  }
  
  public HexFieldLayer newLayer(final String name, final String comments){
    final HexFieldLayer result = this.layerListModel.makeNewLayerField(name, comments);
    this.layerListModel.addLayer(result);
    return result;
  }
  
  public HexFieldLayer layerForIndex(final int index){
    return this.layerListModel.getElementAt(index).getLayer();
  }
  
  public HexFieldLayer layerForName(final String layerName) {
    for (int i = 0; i < this.layerListModel.getSize(); i++) {
      final LayerRecordPanel panel = this.layerListModel.getElementAt(i);
      if (layerName.equalsIgnoreCase(panel.getLayer().getLayerName())) {
        return panel.getLayer();
      }
    }
    return null;
  }

  public void setHex(final HexFieldLayer layer, final int x, final int y, final int value) {
    if (value < 0) {
      return;
    }
    if (value < layer.getHexValuesNumber()) {
      layer.setValueAt(x, y, (byte) value);
    }
    else {
      error("Attempt to set value " + value + " but max value is " + (layer.getHexValuesNumber() - 1));
    }
  }

  public int getHex(final HexFieldLayer layer, final int x, final int y) {
    final int value = layer.getValueAt(x, y);
    return value < 0 ? -1 : value & 0xFF;
  }

  public HexFieldLayer selectLayerDialog(final String title) {
    final SelectLayerDialog dialog = new SelectLayerDialog(this.frame, title, this.layerListModel, 1);
    dialog.setVisible(true);
    final HexFieldLayer[] result = dialog.getResult();
    return result == null ? null : result[0];
  }

  public HexFieldLayer[] selectLayersDialog(final String title, final int number) {
    final SelectLayerDialog dialog = new SelectLayerDialog(this.frame, title, this.layerListModel, Math.max(1, number));
    dialog.setVisible(true);
    final HexFieldLayer[] result = dialog.getResult();
    return result;
  }

  public void warn(final String text) {
    JOptionPane.showMessageDialog(this.frame, text, "Warning", JOptionPane.WARNING_MESSAGE);
  }

  public void info(final String text) {
    JOptionPane.showMessageDialog(this.frame, text, "info", JOptionPane.INFORMATION_MESSAGE);
  }

  public void error(final String text) {
    JOptionPane.showMessageDialog(this.frame, text, "Error", JOptionPane.ERROR_MESSAGE);
  }

}
