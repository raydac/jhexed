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
 * Interface describes a hexagonal model
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 * @param <E> object type saved in a cell
 */
public interface HexEngineModel <E> extends AttachDetachAware {
  /**
   * Get the Model width in cells
   * @return the model width in cells
   */
  int getColumnNumber();
  /**
   * Get the Model height in cells
   * @return the model height in cells
   */
  int getRowNumber();
  
  /**
   * Get cell value at coordinates.
   * @param col the column index.
   * @param row the row index.
   * @return the value saved in the cell.
   */
  E getValueAt(int col, int row);
  
  /**
   * Get cell value at coordinates.
   *
   * @param pos the cell position.
   * @return the value saved in the cell.
   */
  E getValueAt(HexPosition pos);

  /**
   * Set value into a cess for position
   * @param col the column index of a cell.
   * @param row the row index of a cell.
   * @param value the value to be saved into the cell.
   */
  void setValueAt(int col, int row, E value);
  
  /**
   * Set value into a cess for position
   *
   * @param pos the cell position.
   * @param value the value to be saved into the cell.
   */
  void setValueAt(HexPosition pos, E value);

  /**
   * Check that the cell for the position is valid one.
   * @param col the column index.
   * @param row the row index.
   * @return true if the position is valid for the model
   */
  boolean isPositionValid(int col, int row);
  /**
   * Check that the cell for the position is valid one.
   *
   * @param pos the cell position.
   * @return true if the position is valid for the model
   */
  boolean isPositionValid(HexPosition pos);
}
