package com.igormaznitsa.jhexed.swing.editor.ui.frames;

import com.igormaznitsa.jhexed.swing.editor.model.ToolType;
import javax.swing.JToggleButton;

public class ToolButton extends JToggleButton {
  private static final long serialVersionUID = -4462439797288345867L;
  private final ToolType type;
  
  public ToolButton(final ToolType type){
    super();
    this.type = type;
  }
  
  public ToolType getType(){
    return this.type;
  }
}
