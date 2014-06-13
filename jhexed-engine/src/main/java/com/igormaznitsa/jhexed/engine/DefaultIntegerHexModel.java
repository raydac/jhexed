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
package com.igormaznitsa.jhexed.engine;

import com.igormaznitsa.jhexed.engine.misc.HexPosition;

/**
 * Default model allows to save integer values.
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class DefaultIntegerHexModel implements HexEngineModel<Integer> {

  /**
   * Inside array to keep values
   */
  private final int[] array;

  /**
   * Number of columns
   */
  private final int columns;

  /**
   * Number of rows
   */
  private final int rows;

  /**
   * The Value returned if requested position is not a valid one.
   */
  private final int outboundIValue;
  /**
   * AN Object representation of the outbound value cached for speed.
   */
  private final Integer outboundValueInt;

  /**
   * The Constructor
   *
   * @param columns the column number in the model, must be greater than zero
   * @param rows the row number in the model, must be greater than zero
   * @param outboundValue the value to be used for illegal positions.
   */
  public DefaultIntegerHexModel(final int columns, final int rows, final int outboundValue) {
    if (columns <= 0) {
      throw new IllegalArgumentException("Column number must be greater than zero [" + columns + ']');
    }
    if (rows <= 0) {
      throw new IllegalArgumentException("Row number must be greater than zero [" + rows + ']');
    }

    this.columns = columns;
    this.rows = rows;
    this.outboundIValue = outboundValue;
    this.outboundValueInt = this.outboundIValue;
    this.array = new int[columns * rows];
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
  public Integer getValueAt(final int col, final int row) {
    if (isPositionValid(col, row)) {
      return this.array[col + row * this.columns];
    }
    else {
      return this.outboundValueInt;
    }
  }

  @Override
  public boolean isPositionValid(final int col, final int row) {
    return col >= 0 && row >= 0 && col < this.columns && row < this.rows;
  }

  @Override
  public boolean isPositionValid(final HexPosition pos) {
    return this.isPositionValid(pos.getColumn(), pos.getRow());
  }

  @Override
  public Integer getValueAt(final HexPosition pos) {
    return this.getValueAt(pos.getColumn(), pos.getRow());
  }

  @Override
  public void setValueAt(final HexPosition pos, final Integer value) {
    this.setValueAt(pos.getColumn(), pos.getRow(), value);
  }

  @Override
  public void setValueAt(final int col, final int row, final Integer value) {
    if (value == null) {
      throw new NullPointerException("Null value");
    }
    if (isPositionValid(col, row)) {
      this.array[col + row * this.columns] = value;
    }
  }

  public void attachedToEngine(final HexEngine<?> layer) {
  }

  public void detachedFromEngine(final HexEngine<?> layer) {
  }
}
