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

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.util.Iterator;

/**
 * Class implements a hex value iterator.
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
final class HexIterator implements Iterable<HexFieldValue>, Iterator<HexFieldValue> {
  private final LayerListModel model;
  private int col;
  private int row;
  private int index;
  private boolean nextPresented;
  private HexFieldValue nextValue;

  public HexIterator(final LayerListModel model, final int col, final int row) {
    this.model = model;
    reinitIterator(col, row);
  }

  public void reinitIterator(final int col, final int row) {
    this.col = col;
    this.row = row;
    this.index = this.model.getSize() - 1;
    processNext();
  }

  private void processNext() {
    this.nextPresented = false;
    this.nextValue = null;
    while (this.index >= 0) {
      final HexFieldLayer layer = this.model.getElementAt(this.index--).getLayer();
      if (layer.isLayerVisible()) {
        this.nextValue = layer.getHexValueAtPos(this.col, this.row);
        this.nextPresented = true;
        break;
      }
    }
  }

  @Override
  public Iterator<HexFieldValue> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    return this.nextPresented;
  }

  @Override
  public HexFieldValue next() {
    final HexFieldValue result = this.nextValue;
    processNext();
    return result;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("You can't remove hexes through iterator");
  }
  
}
