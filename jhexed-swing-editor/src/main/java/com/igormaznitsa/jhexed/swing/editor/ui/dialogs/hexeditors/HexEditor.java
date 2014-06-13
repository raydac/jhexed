package com.igormaznitsa.jhexed.swing.editor.ui.dialogs.hexeditors;

import com.igormaznitsa.jhexed.swing.editor.model.values.HexValue;

public interface HexEditor {
  HexValue getHexEditResult();
  HexValue showDialog();
}
