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

/**
 * The Class describes an immutable object contains information about a width-height pair. 
 * 
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public final class WidthHeightPair {
  /**
   * Inside storage of width
   */
  private final int width;
  /**
   * Inside storage of height
   */
  private final int height;
  
  /**
   * The Constructor
   * @param width the width
   * @param height the height
   */
  public WidthHeightPair(final int width, final int height){
    this.width = width;
    this.height = height;
  }
  
  /**
   * Get the Width
   * @return the width
   */
  public int getWidth(){
    return this.width;
  }

  /**
   * Get the height
   * @return the height
   */
  public int getHeight(){
    return this.height;
  }
  
  @Override
  public boolean equals(final Object obj){
    if (obj == null) return false;
    if (this == obj) return true;
    if (obj instanceof WidthHeightPair){
      final WidthHeightPair that = (WidthHeightPair)obj;
      return this.width == that.width && this.height == that.height;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.width ^ this.height;
  }
  
  @Override
  public String toString(){
    return "WidthHeightPair ("+getWidth()+','+getHeight()+')';
  }
}
