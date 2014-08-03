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
package com.igormaznitsa.jhexed.engine.misc;

import com.igormaznitsa.jhexed.engine.HexEngineModel;

/**
 * Class describes a position in a hexagonal field, it is just a pair column,row
 * 
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public final class HexPosition {
  /**
   * Packed column and row.
   */
  private final long packedColAndRow;
  /**
   * Saved hash code of the object.
   */
  private final int hashCode;
  
  /**
   * The Constructor.
   * @param column the column
   * @param row the row
   */
  public HexPosition(final int column, final int row){
    this.packedColAndRow = (((long)column << 32)|((long)row & 0xFFFFFFFFL));
    this.hashCode = column^(row<<16);
  }
  
  /**
   * Get the Column index.
   * @return the column index
   */
  public int getColumn(){
    return (int)(this.packedColAndRow >>> 32);
  }
  
  /**
   * Get the Row index.
   * @return the row index
   */
  public int getRow(){
    return (int)this.packedColAndRow;
  }
  
  /**
   * Check that the position valid for a model.
   * @param model a model to be checked.
   * @return true if the position is valid, false otherwise
   */
  public boolean isAtModel(final HexEngineModel<?> model){
    final int col = getColumn();
    final int row = getRow();
    return col>=0 && col<model.getColumnNumber() && row>=0 && row<model.getRowNumber();
  }
  
  @Override
  public int hashCode(){
    return this.hashCode;
  }
  
  @Override
  public boolean equals(final Object object){
    if (object == null) return false;
    if (object == this) return true;
    if (object.getClass() == HexPosition.class){
      final HexPosition that = (HexPosition)object;
      return that.packedColAndRow == this.packedColAndRow;
    }
    return false;
  }
  
  /**
   * Calculate distance to another position.
   * @param pos an another position
   * @return distance in cells to another position
   */
  public int calcDistance(final HexPosition pos){
    return this.calcDistance(pos.getColumn(), pos.getRow());
  }
  
  /**
   * Calculate distance to another position.
   *
   * @param col the column of another position
   * @param row the row of another position
   * @return distance in cells to another position
   */
  public int calcDistance(final int col, final int row){
    return calcDistance(getColumn(), getRow(), col, row);
  }
  
  /**
   * Calculate distance between A and B
   * @param col1 the column of A position
   * @param row1 the row of A position
   * @param col2 the column of B position
   * @param row2 the row of B position
   * @return distance in cells
   */
  public static int calcDistance(final int col1, final int row1, final int col2, final int row2){
    final float dx = (float)(col2-col1);
    final float dy = (float)(row2-row1);
    return (int)Math.round(Math.sqrt(dx*dx+dy*dy));
  }
  
  /**
   * Calculate distance between positions
   * @param pos1 the first position
   * @param pos2 the second position
   * @return distance in cells
   */
  public static int calcDistance(final HexPosition pos1, final HexPosition pos2){
    return calcDistance(pos1.getColumn(), pos1.getRow(), pos2.getColumn(), pos2.getRow());
  }
  
  @Override
  public String toString(){
    return "HexPosition ("+this.getColumn()+','+this.getRow()+')';
  }
}
