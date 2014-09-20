package com.igormaznitsa.jhexed.swing.editor.ui.extensions;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.swing.editor.model.LayerListModel;
import com.igormaznitsa.jhexed.swing.editor.ui.MainForm;
import javax.swing.JOptionPane;

public class GroovyPluginBase {

  private final LayerListModel layerListModel;
  private final MainForm frame;
  
  public GroovyPluginBase(final MainForm frame, final LayerListModel listModel){
    this.frame = frame;
    this.layerListModel = listModel;
  }

  public int selectValue(final String text, final HexFieldLayer layer){
    final SelectValueDialog dialog = new SelectValueDialog(this.frame, text, layer);
    dialog.setVisible(true);
    return dialog.getSelectedIndex();
  }

  public void addUndo(final HexFieldLayer layer){
    if (layer!=null){
      this.frame.addedUndoStep(new HexFieldLayer[]{layer});
    }
  }
  
  public void addUndo(final HexFieldLayer [] layers){
    if (layers!=null){
      this.frame.addedUndoStep(layers);
    }
  }
  
  public HexFieldLayer selectLayer(final String title){
    final SelectLayerDialog dialog = new SelectLayerDialog(this.frame, title, this.layerListModel, 1);
    dialog.setVisible(true);
    final HexFieldLayer [] result = dialog.getResult();
    return result == null ? null : result[0];
  }
  
  public HexFieldLayer [] selectLayers(final String title, final int number){
    final SelectLayerDialog dialog = new SelectLayerDialog(this.frame, title, this.layerListModel, Math.max(1,number));
    dialog.setVisible(true);
    final HexFieldLayer [] result = dialog.getResult();
    return result;
  }
  
  public void warn(final String text){
    JOptionPane.showMessageDialog(this.frame, text, "Warning", JOptionPane.WARNING_MESSAGE);
  }
  
}
