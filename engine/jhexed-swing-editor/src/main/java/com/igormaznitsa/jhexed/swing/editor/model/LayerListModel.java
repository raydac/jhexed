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
import com.igormaznitsa.jhexed.extapp.hexes.HexLayer;
import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.hexmap.LayerableHexValueSource;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.layers.LayerRecordPanel;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.ListModel;
import javax.swing.event.*;

public class LayerListModel implements ListModel<LayerRecordPanel>, HexEngineModel<LayerListModel>, LayerableHexValueSource {

  private final List<LayerRecordPanel> layers = new ArrayList<LayerRecordPanel>();
  private final List<ListDataListener> listeners = new ArrayList<ListDataListener>();

  private int cols;
  private int rows;

  private final int initCols;
  private final int initRows;

  private List<HexLayer> hexLayers;

  private final ReentrantLock locker = new ReentrantLock();

  public LayerListModel(final int cols, final int rows) {
    this.initCols = cols;
    this.initRows = rows;
    init();
  }

  @Override
  public int getSize() {
    locker.lock();
    try {
      return this.layers.size();
    }
    finally {
      locker.unlock();
    }
  }

  @Override
  public LayerRecordPanel getElementAt(int index) {
    locker.lock();
    try {
      return this.layers.get(index);
    }
    finally {
      locker.unlock();
    }
  }

  public HexFieldLayer makeNewLayerField(final String name, final String comments) {
    locker.lock();
    try {
      return new HexFieldLayer(name, comments, this.cols, this.rows);
    }
    finally {
      locker.unlock();
    }
  }

  public LayerRecordPanel addLayer(final HexFieldLayer f) {
    int from = -1;
    int to = -1;
    LayerRecordPanel result = null;
    locker.lock();
    try {
      result = new LayerRecordPanel(this, f);
      this.layers.add(0, result);
      from = this.layers.size() - 1;
      to = this.layers.size() - 1;
    }
    finally {
      locker.unlock();
    }
    fireListenerEvent(ListDataEvent.INTERVAL_ADDED, from, to);
    return result;
  }

  public void removeLayer(final HexFieldLayer f) {
    int index = -1;
    locker.lock();
    try {
      for (int i = 0; i < this.layers.size(); i++) {
        if (this.layers.get(i).getHexField() == f) {
          index = i;
          break;
        }
      }
      if (index >= 0) {
        this.layers.remove(index);
      }
    }
    finally {
      locker.unlock();
    }
    if (index >= 0) {
      fireListenerEvent(ListDataEvent.INTERVAL_REMOVED, index, index);
    }
  }

  private void fireListenerEvent(final int type, final int index0, final int index1) {
    final ListDataEvent evt = new ListDataEvent(this, type, index0, index1);
    for (final ListDataListener l : this.listeners) {
      l.contentsChanged(evt);
    }
  }

  public boolean up(final LayerRecordPanel panel) {
    boolean result = false;
    int index = -1;
    locker.lock();
    try {
      index = this.layers.indexOf(panel);
      if (index > 0) {
        final LayerRecordPanel second = this.layers.get(index - 1);
        this.layers.set(index, second);
        this.layers.set(index - 1, panel);
        result = true;
      }
    }
    finally {
      locker.unlock();
    }
    if (result) {
      fireListenerEvent(ListDataEvent.CONTENTS_CHANGED, index - 1, index);
    }
    return result;
  }

  public boolean down(final LayerRecordPanel panel) {
    boolean result = false;
    int index = -1;
    locker.lock();
    try {
      index = this.layers.indexOf(panel);
      if (index < this.layers.size() - 1) {
        final LayerRecordPanel second = this.layers.get(index + 1);
        this.layers.set(index, second);
        this.layers.set(index + 1, panel);
        result = true;
      }
    }
    finally {
      locker.unlock();
    }
    if (result) {
      fireListenerEvent(ListDataEvent.CONTENTS_CHANGED, index, index + 1);
    }
    return result;
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
    int index = -1;
    locker.lock();
    try {
      index = this.layers.indexOf(val);
    }
    finally {
      locker.unlock();
    }
    if (index >= 0) {
      fireListenerEvent(ListDataEvent.CONTENTS_CHANGED, index, index);
    }
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
    return col >= 0 && row >= 0 && col < this.cols && row < this.rows;
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

  public boolean resize(final int cols, final int rows) {
    locker.lock();
    try {
      if (this.cols != cols || this.rows != rows) {
        this.cols = cols;
        this.rows = rows;
        for (final LayerRecordPanel f : this.layers) {
          f.getHexField().resize(cols, rows);
        }
        return true;
      }
      else {
        return false;
      }
    }
    finally {
      locker.unlock();
    }
  }

  public byte[] toByteArray() throws IOException {
    final ByteArrayOutputStream result = new ByteArrayOutputStream(256 * 1024);
    final DataOutputStream dout = new DataOutputStream(result);

    locker.lock();
    try {

      dout.writeInt(this.cols);
      dout.writeInt(this.rows);

      dout.writeShort(this.layers.size());
      for (final LayerRecordPanel p : this.layers) {
        p.getHexField().write(dout);
      }
    }
    finally {
      locker.unlock();
    }
    return result.toByteArray();

  }

  public void fromByteArray(final byte[] array) throws IOException {
    final DataInputStream din = new DataInputStream(new ByteArrayInputStream(array));
    final List<HexFieldLayer> newLayers = new ArrayList<HexFieldLayer>();

    locker.lock();
    try {
      this.cols = din.readInt();
      this.rows = din.readInt();

      final int numberOfLayers = din.readUnsignedShort();

      for (int i = 0; i < numberOfLayers; i++) {
        newLayers.add(new HexFieldLayer(din));
      }

      final int num = this.layers.size();
      this.layers.clear();
      fireListenerEvent(ListDataEvent.INTERVAL_REMOVED, 0, num);

      for (final HexFieldLayer f : newLayers) {
        this.layers.add(new LayerRecordPanel(this, f));
      }
    }
    finally {
      locker.unlock();
    }
    fireListenerEvent(ListDataEvent.INTERVAL_ADDED, 0, newLayers.size());
  }

  public void init() {
    int max;
    locker.lock();
    try {
      this.cols = this.initCols;
      this.rows = this.initRows;
      max = this.layers.size();
      this.layers.clear();
    }
    finally {
      locker.unlock();
    }
    this.fireListenerEvent(ListDataEvent.INTERVAL_REMOVED, 0, max - 1);
  }

  @Override
  public Iterable<HexFieldValue> getHexStackAtPosition(final int col, final int row) {
    return new HexIterator(this, col, row);
  }

}
