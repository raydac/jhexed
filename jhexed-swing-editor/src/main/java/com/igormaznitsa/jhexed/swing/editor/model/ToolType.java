package com.igormaznitsa.jhexed.swing.editor.model;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.swing.editor.Log;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import com.igormaznitsa.jhexed.swing.editor.ui.Utils;
import com.igormaznitsa.jhexed.swing.editor.ui.tooloptions.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public enum ToolType {

  PENCIL("Pencil-icon.png", "Draw selected value", PencilOptions.class),
  FILL("Paint-icon.png", "Fill an area by a selected value", FillOptions.class),
  ERASER("Eraser-icon.png", "Erase the selected value", EraserOptions.class);

  private final ImageIcon icon;
  private volatile JPanel options;
  private final String description;
  private final Class<? extends JPanel> optionsClass;

  private ToolType(final String iconName, final String description, final Class<? extends JPanel> optionsClass) {
    this.icon = loadImage(iconName);
    this.description = description;
    this.optionsClass = optionsClass;
  }

  public synchronized JPanel getOptions() {
    if (this.options == null) {
      try {
        this.options = optionsClass.getConstructor().newInstance();
      }
      catch (Throwable ex) {
        Log.error("Can't make options ["+optionsClass+']', ex);
        return null;
      }
    }
    return this.options;
  }

  public String getDescription() {
    return this.description;
  }

  public ImageIcon getIcon() {
    return this.icon;
  }

  private static void fill(final HexEngine<?> engine, final HexFieldLayer field, final int col, final int row, final int fillIndex, final int borderIndex) {
    final List<Integer> stack = new ArrayList<Integer>(16384);
    
    stack.add(HexEngine.packColumnRow(col, row));
    
    while(!stack.isEmpty()){
      final int stackTop = stack.remove(stack.size()-1);
      final int curColumn = HexEngine.extractColumn(stackTop);
      final int curRow = HexEngine.extractRow(stackTop);

      field.setValueAtPos(curColumn, curRow, fillIndex);
      
      if (curColumn < 0 || curColumn >= field.getColumnNumber() || curRow < 0 || curRow >= field.getRowNumber()) {
        continue;
      }
      
      final int[] packed = engine.getPackedNeighbourPositions(null, curColumn, curRow, 1);

      for (int i = 0; i < packed.length; i++) {
        final int ic = HexEngine.extractColumn(packed[i]);
        final int ir = HexEngine.extractRow(packed[i]);

        final int iv = field.getValueAtPos(ic, ir);
        if (iv == fillIndex || iv == borderIndex) {
          continue;
        }
        field.setValueAtPos(ic, ir, fillIndex);
        stack.add(HexEngine.packColumnRow(ic, ir));
      }
    }
  }

  public void processTool(final HexEngine<?> engine, final HexFieldLayer field, final HexPosition position) {
    switch (this) {
      case ERASER: {
        final EraserOptions opt = (EraserOptions) this.getOptions();
        final HexFieldValue value = opt.getHexValue();
        if (value != null) {
          final int index = value.getIndex();
          final int width = opt.getPencilWidth();
          if (field.getValueAtPos(position.getColumn(), position.getRow()) == index) {
            field.setValueAtPos(position.getColumn(), position.getRow(), 0);
          }
          if (width > 1) {
            final int[] buffer = new int[width * 6];

            for (int i = 1; i < width; i++) {
              final int size = i * 6;
              engine.getPackedNeighbourPositions(buffer, position.getColumn(), position.getRow(), i);
              for (int s = 0; s < size; s++) {
                final int packed = buffer[s];
                final int col = HexEngine.extractColumn(packed);
                final int row = HexEngine.extractRow(packed);
                if (col >= 0 && row >= 0 && field.getValueAtPos(col, row) == index) {
                  field.setValueAtPos(col, row, 0);
                }
              }
            }
          }
        }
      }
      break;
      case FILL: {
        final FillOptions opt = (FillOptions) this.getOptions();
        final HexFieldValue fillValue = opt.getFillValue();
        final HexFieldValue borderValue = opt.getBorderValue();

        if (fillValue == null) {
          JOptionPane.showMessageDialog(null, "You have to select the fill value", "Fill value", JOptionPane.WARNING_MESSAGE);
          return;
        }

        if (borderValue == null) {
          JOptionPane.showMessageDialog(null, "You have to select the border value", "Border value", JOptionPane.WARNING_MESSAGE);
          return;
        }

        fill(engine, field, position.getColumn(), position.getRow(), fillValue.getIndex(), borderValue.getIndex());
      }
      break;
      case PENCIL: {
        final PencilOptions opt = (PencilOptions) this.getOptions();
        final HexFieldValue value = opt.getHexValue();
        if (value != null) {
          final int width = opt.getPencilWidth();
          field.setValueAt(position, (byte) value.getIndex());
          if (width > 1) {
            final int[] buffer = new int[width * 6];

            for (int i = 1; i < width; i++) {
              final int size = i * 6;
              engine.getPackedNeighbourPositions(buffer, position.getColumn(), position.getRow(), i);
              for (int s = 0; s < size; s++) {
                final int packed = buffer[s];
                final int col = HexEngine.extractColumn(packed);
                final int row = HexEngine.extractRow(packed);
                if (col >= 0 && row >= 0) {
                  field.setValueAt(col, row, (byte) value.getIndex());
                }
              }
            }
          }
        }
      }
      break;
      default:
        throw new Error("Unsupported tool [" + this + ']');
    }
  }

  private static ImageIcon loadImage(final String name) {
    try {
      return new ImageIcon(Utils.loadIcon(name));
    }
    catch (Exception ex) {
      Log.error("Can't load image ["+name+']', ex);
      return new ImageIcon(new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB));
    }
  }
}
