package com.igormaznitsa.jhexed.swing.editor.ui.dialogs.hexeditors;

import com.igormaznitsa.jhexed.values.HexFieldValue;

public interface HexEditor {
  HexFieldValue getHexEditResult();
  HexFieldValue showDialog();
}
