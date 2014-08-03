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
package com.igormaznitsa.jhexed.swing.editor.model;

import com.igormaznitsa.jhexed.engine.*;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.hexmap.LayerableHexValueSource;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.layers.LayerRecordPanel;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.io.*;
import java.util.*;
import javax.swing.ListModel;
import javax.swing.event.*;

public class LayerListModel implements ListModel<LayerRecordPanel>, HexEngineModel<LayerListModel>, LayerableHexValueSource {
  private final List<LayerRecordPanel> layers = new ArrayList<LayerRecordPanel>();
  private final List<ListDataListener> listeners = new ArrayList<ListDataListener>();

  private int cols;
  private int rows;
  
  private final int initCols;
  private final int initRows;
  
  
  public LayerListModel(final int cols, final int rows) {
    this.initCols = cols;
    this.initRows = rows;
    init();
  }
  
  @Override
  public int getSize() {
    return this.layers.size();
  }

  @Override
  public LayerRecordPanel getElementAt(int index) {
    return this.layers.get(index);
  }

  public HexFieldLayer makeNewLayerField(final String name, final String comments){
    return new HexFieldLayer(name, comments, this.cols, this.rows);
  }
  
  public void addLayer(final HexFieldLayer f){
    final LayerRecordPanel newPanel = new LayerRecordPanel(this,f);
    this.layers.add(0,newPanel);
    fireListenerEvent(ListDataEvent.INTERVAL_ADDED, this.layers.size()-1, this.layers.size() - 1);
  }

  public void removeLayer(final HexFieldLayer f) {
    int index = -1;
    for(int i=0;i<this.layers.size();i++){
      if (this.layers.get(i).getLayer() == f){
        index = i;
        break;
      }
    }
    
    if (index>=0){
      this.layers.remove(index);
      fireListenerEvent(ListDataEvent.INTERVAL_REMOVED, index,index);
    }
  }  

  private void fireListenerEvent(final int type, final int index0, final int index1){
    final ListDataEvent evt = new ListDataEvent(this, type, index0, index1);
    for(final ListDataListener l : this.listeners){
      l.contentsChanged(evt);
    }
  }

  public boolean up(final LayerRecordPanel panel){
    final int index = this.layers.indexOf(panel);
    if (index<=0) return false;
    final LayerRecordPanel second = this.layers.get(index-1);
    this.layers.set(index, second);
    this.layers.set(index-1, panel);
    fireListenerEvent(ListDataEvent.CONTENTS_CHANGED, index-1, index);
    return true;
  }
  

  public boolean down(final LayerRecordPanel panel){
    final int index = this.layers.indexOf(panel);
    if (index>=this.layers.size()-1) return false;
    final LayerRecordPanel second = this.layers.get(index+1);
    this.layers.set(index, second);
    this.layers.set(index+1, panel);
    fireListenerEvent(ListDataEvent.CONTENTS_CHANGED, index, index+1);
    return true;
  }
  
  @Override
  public void addListDataListener(final ListDataListener l) {
    this.listeners.add(l);
  }

  @Override
  public void removeListDataListener(ListDataListener l) {
    this.listeners.remove(l);
  }

  public void changedItem(final LayerRecordPanel val) {
    final int index = this.layers.indexOf(val);
    fireListenerEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
  }

  @Override
  public int getColumnNumber() {
    return this.cols;
  }

  @Override
  public int getRowNumber() {
    return this.rows;
  }

  @Override
  public LayerListModel getValueAt(final int col, final int row) {
    return this;
  }

  @Override
  public LayerListModel getValueAt(final HexPosition pos) {
    return this;
  }

  @Override
  public void setValueAt(final int col, final int row, final LayerListModel value) {
  }

  @Override
  public void setValueAt(final HexPosition pos, final LayerListModel value) {
 
  }

  @Override
  public boolean isPositionValid(final int col, final int row) {
    return col>=0 && row>=0 && col<this.cols && row<this.rows;
  }

  @Override
  public boolean isPositionValid(final HexPosition pos) {
    return this.isPositionValid(pos.getColumn(), pos.getRow());
  }

  @Override
  public void attachedToEngine(final HexEngine<?> engine) {
  }

  @Override
  public void detachedFromEngine(final HexEngine<?> engine) {
  }
  
  public void resetaAllRedoUndo(){
    for(final LayerRecordPanel p : this.layers){
      p.getLayer().resetRedoUndo();
    }
  }

  public boolean resize(final int cols, final int rows) {
    if (this.cols!=cols || this.rows!=rows){
      this.cols = cols;
      this.rows = rows;
      for(final LayerRecordPanel f : this.layers){
        f.getLayer().resize(cols, rows);
      }
      return true;
    }else{
      return false;
    }
  }

  public byte[] toByteArray() throws IOException {
    final ByteArrayOutputStream result = new ByteArrayOutputStream(256*1024);
    
    final DataOutputStream dout = new DataOutputStream(result);
    
    dout.writeInt(this.cols);
    dout.writeInt(this.rows);
    
    dout.writeShort(this.layers.size());
    for(final LayerRecordPanel p : this.layers){
      p.getLayer().write(dout);
    }
    
    return result.toByteArray();
  }

  public void fromByteArray(final byte [] array) throws IOException {
    final DataInputStream din = new DataInputStream(new ByteArrayInputStream(array));
    this.cols = din.readInt();
    this.rows = din.readInt();
  
    final int numberOfLayers = din.readUnsignedShort();
    
    final List<HexFieldLayer> newLayers = new ArrayList<HexFieldLayer>();
    
    for(int i=0;i<numberOfLayers;i++){
      newLayers.add(new HexFieldLayer(din));
    }
    
    final int num = this.layers.size();
    this.layers.clear();
    fireListenerEvent(ListDataEvent.INTERVAL_REMOVED, 0, num);
    
    for(final HexFieldLayer f : newLayers){
      this.layers.add(new LayerRecordPanel(this, f));
    }
    fireListenerEvent(ListDataEvent.INTERVAL_ADDED, 0, newLayers.size());
  }

  public void init() {
    this.cols = this.initCols;
    this.rows = this.initRows;
    final int max = this.layers.size();
    this.layers.clear();
    this.fireListenerEvent(ListDataEvent.INTERVAL_REMOVED, 0, max-1);
  }

  @Override
  public Iterable<HexFieldValue> getHexStackAtPosition(final int col, final int row) {
    return new HexIterator(this,col,row);
  }

}
