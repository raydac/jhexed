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
package com.igormaznitsa.jhexed.hexmap;

import com.igormaznitsa.jhexed.engine.*;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.renders.Utils;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.awt.geom.Path2D;
import java.io.*;
import java.util.*;
import org.apache.commons.io.IOUtils;

public class HexFieldLayer implements HexEngineModel<Byte> {

  public static final int MAX_UNDO_DEPTH = 10;
  
  private int columns;
  private int rows;

  private boolean visible;
  private String name;
  private String comments;

  private byte[] array;
  private final Byte defaultValue = (byte) 0;

  private final List<HexFieldValue> values = new ArrayList<HexFieldValue>();

  private final List<CopyOfLayerState> listUndo = new ArrayList<CopyOfLayerState>();
  private final List<CopyOfLayerState> listRedo = new ArrayList<CopyOfLayerState>();

  private static class CopyOfLayerState {
    private final String name;
    private final String comment;
    private final boolean visible;
    private final int columns;
    private final int rows;
    private final byte [] array;
    private final HexFieldValue [] values;
    
    public CopyOfLayerState(final HexFieldLayer fld){
      this.name = fld.name;
      this.comment = fld.comments;
      this.array = fld.array.clone();
      this.columns = fld.columns;
      this.rows = fld.rows;
      this.visible = fld.visible;
      
      this.values = new HexFieldValue[fld.values.size()];
      for(int i=0; i<fld.values.size();i++){
        this.values [i] = fld.values.get(i).cloneValue();
      }
    }
    
    public void restoreLayer(final HexFieldLayer fld){
      fld.name = this.name;
      fld.comments = this.comment;
      fld.visible = this.visible;
      fld.columns = this.columns;
      fld.rows = this.rows;
      fld.array = this.array.clone();
      
      fld.values.clear();
      for(final HexFieldValue v : this.values){
        fld.values.add(v.cloneValue());
      }
    }
  }
  
  
  public HexFieldLayer(final String name, final String comments, final int width, final int height) {
    if (name == null) {
      throw new NullPointerException("Name is null");
    }
    this.visible = true;
    this.comments = comments == null ? "" : comments;
    this.columns = width;
    this.rows = height;
    this.array = new byte[this.columns * this.rows];
    this.name = name;
    this.values.add(HexFieldValue.NULL);
  }

  public HexFieldLayer(final InputStream in) throws IOException {
    final DataInputStream din = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
    this.name = din.readUTF();
    this.comments = din.readUTF();

    final int valuesNum = din.readShort() & 0xFFFF;
    for (int i = 0; i < valuesNum; i++) {
      final HexFieldValue value = HexFieldValue.readValue(in);
      value.setIndex(i);
      this.values.add(value);
    }

    this.columns = din.readInt();
    this.rows = din.readInt();
    this.visible = din.readBoolean();
  
    final byte [] packedLayerData = new byte [din.readInt()];
    IOUtils.readFully(din, packedLayerData);
    this.array = Utils.unpackArray(packedLayerData);

    if (this.array.length!=(this.columns*this.rows)) throw new IOException("Wrong field size");
  }

  private HexFieldLayer(final HexFieldLayer layer) {
    this.visible = layer.visible;
    this.columns = layer.columns;
    this.rows = layer.rows;
    this.name = layer.name;
    this.comments = layer.comments;
    this.array = layer.array.clone();

    for (final HexFieldValue h : layer.values) {
      this.values.add(h.cloneValue());
    }
  }

  public HexFieldLayer cloneLayer() {
    return new HexFieldLayer(this);
  }

  public byte[] getArray() {
    return this.array;
  }  
  
  public void loadFromAnotherInstance(final HexFieldLayer layer) {
    this.visible = layer.visible;
    this.columns = layer.columns;
    this.rows = layer.rows;
    this.name = layer.name;
    this.comments = layer.comments;
    this.array = layer.array.clone();

    this.values.clear();
    this.values.addAll(layer.values);
  }

  public void write(final OutputStream out) throws IOException {
    final DataOutputStream dout = out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);
    dout.writeUTF(this.name);
    dout.writeUTF(this.comments);

    dout.writeShort(this.values.size());
    for (int i = 0; i < this.values.size(); i++) {
      this.values.get(i).write(dout);
    }

    dout.writeInt(this.columns);
    dout.writeInt(this.rows);
    dout.writeBoolean(this.visible);
  
    final byte [] packed = Utils.packByteArray(this.array);
    dout.writeInt(packed.length);
    dout.write(packed);
    dout.flush();
  }

  public String getComments() {
    return this.comments;
  }

  public void setComments(final String text) {
    this.comments = text == null ? "" : text;
  }

  @Override
  public int getColumnNumber() {
    return this.columns;
  }

  @Override
  public int getRowNumber() {
    return this.rows;
  }

  @Override
  public Byte getValueAt(final int col, final int row) {
    if (this.isPositionValid(col, row)) {
      return array[col + row * this.columns];
    }
    else {
      return defaultValue;
    }
  }

  @Override
  public Byte getValueAt(final HexPosition pos) {
    return this.getValueAt(pos.getColumn(), pos.getRow());
  }

  @Override
  public void setValueAt(final int col, final int row, final Byte value) {
    if (this.isPositionValid(col, row)) {
      this.array[col + row * this.columns] = value.byteValue();
    }
  }

  @Override
  public void setValueAt(final HexPosition pos, final Byte value) {
    this.setValueAtPos(pos.getColumn(), pos.getRow(), value);
  }

  public void setValueAtPos(final HexPosition pos, final byte value) {
    this.setValueAtPos(pos.getColumn(), pos.getRow(), value);
  }

  public void setValueAtPos(final int col, final int row, final int i) {
    if (i > 255) {
      throw new IllegalArgumentException("Too big value, must be 0...255");
    }
    if (isPositionValid(col, row)) {
      this.array[col + row * this.columns] = (byte) i;
    }
  }

  @Override
  public boolean isPositionValid(int col, int row) {
    return col >= 0 && row >= 0 && col < this.columns && row < this.rows;
  }

  @Override
  public boolean isPositionValid(HexPosition pos) {
    return this.isPositionValid(pos.getColumn(), pos.getRow());
  }

  @Override
  public void attachedToEngine(HexEngine<?> engine) {
  }

  @Override
  public void detachedFromEngine(HexEngine<?> engine) {
  }

  public int getValueAtPos(final int column, final int row) {
    if (column < 0 || row < 0 || column >= this.columns || row >= this.rows) {
      return 0;
    }
    else {
      return this.array[column + (row * this.columns)] & 0xFF;
    }
  }

  public HexFieldValue getHexValueAtPos(final int column, final int row) {
    final int value = this.getValueAtPos(column, row);
    return value == 0 ? null : this.values.get(value);
  }

  public String getLayerName() {
    return this.name;
  }

  public void setLayerName(final String name) {
    if (name == null) {
      throw new NullPointerException("Name is null");
    }
    this.name = name;
  }

  public boolean isLayerVisible() {
    return this.visible;
  }

  public void setVisible(final boolean flag) {
    this.visible = flag;
  }

  public void resize(final int newColumns, final int newRows) {
    final byte[] newArray = new byte[newColumns * newRows];

    Arrays.fill(newArray, defaultValue);

    final int columnsToCopy = Math.min(this.columns, newColumns);
    final int rowsToCopy = Math.min(this.rows, newRows);

    for (int i = 0; i < rowsToCopy; i++) {
      final int newLineStart = i * newColumns;
      final int oldLineStart = i * this.columns;
      System.arraycopy(this.array, oldLineStart, newArray, newLineStart, columnsToCopy);
    }

    this.array = newArray;
    this.columns = newColumns;
    this.rows = newRows;
  }

  public HexFieldValue getHexValueForIndex(final int index) {
    if (index < 0 || index >= this.values.size()) {
      return null;
    }
    return this.values.get(index);
  }

  public int getHexValuesNumber() {
    return this.values.size();
  }

  public void addValue(final HexFieldValue value){
    value.setIndex(this.getHexValuesNumber());
    this.values.add(value);
  }
  
  public void replaceValues(final List<HexFieldValue> values) {
    this.values.clear();
    this.values.addAll(values);
  }

  public void updatePrerasterizedIcons(final Path2D hexShape) {
    for (final HexFieldValue h : this.values) {
      h.prerasterizeIcon(hexShape);
    }
  }
  
  public boolean hasUndo(){
    return !this.listUndo.isEmpty();
  }
  
  public boolean hasRedo(){
    return !this.listRedo.isEmpty();
  }
  
  /**
   * Add undo copy into inside list.
   * @return true if the inside list too big and the first item has been removed, false otherwise
   */
  public boolean addUndo(){
    this.listRedo.clear();
    this.listUndo.add(new CopyOfLayerState(this));
    boolean result = false;
    while(this.listUndo.size()>MAX_UNDO_DEPTH){
      this.listUndo.remove(0);
      result = true;
    }
    return result;
  }
  
  public void resetRedoUndo(){
    this.listRedo.clear();
    this.listUndo.clear();
  } 
  
  public boolean undo(){
    if (!this.listUndo.isEmpty()){
      this.listRedo.add(new CopyOfLayerState(this));
      final CopyOfLayerState undoState = this.listUndo.remove(this.listUndo.size()-1);
      undoState.restoreLayer(this);
      return true;
    }
    return false;
  }
  
  public boolean redo(){
    if (!this.listRedo.isEmpty()){
      this.listUndo.add(new CopyOfLayerState(this));
      final CopyOfLayerState redoState = this.listRedo.remove(this.listRedo.size() - 1);
      redoState.restoreLayer(this);
      return true;
    }
    return false;
  }
  
  public void updateIndexes(final List<Integer> removedIndexes, final List<Integer> insertedIndexes, final int numberOfActualElements) {

    for (final Integer i : removedIndexes) {
      if (insertedIndexes.contains(i)) {
        continue;
      }
      for (int arrayIndex = 0; arrayIndex < this.array.length; arrayIndex++) {
        final int current = this.array[arrayIndex] & 0xFF;
        if (current == i) {
          this.array[arrayIndex] = 0;
        }
        else if (current > i) {
          this.array[arrayIndex] = (byte) (current - 1);
        }
      }
    }

    for (final Integer i : insertedIndexes) {
      if (removedIndexes.contains(i)) {
        continue;
      }
      for (int arrayIndex = 0; arrayIndex < this.array.length; arrayIndex++) {
        final int current = this.array[arrayIndex] & 0xFF;
        if (current == i) {
          this.array[arrayIndex] = 0;
        }
        else if (current >= i) {
          this.array[arrayIndex] = (byte) (current + 1);
        }
      }
    }

    for (int arrayIndex = 0; arrayIndex < this.array.length; arrayIndex++) {
      final int current = this.array[arrayIndex] & 0xFF;
      if (current >= numberOfActualElements) {
        this.array[arrayIndex] = 0;
      }
    }
  }
}
