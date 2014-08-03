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
 * A Rectangle which can be used for inside engine operations.
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public final class HexRect2D {

  /**
   * The Left edge X.
   */
  private final float left;
  /**
   * The Top edge Y.
   */
  private final float top;
  /**
   * The Rectangle width.
   */
  private final float width;
  /**
   * The Rectangle height.
   */
  private final float height;

  /**
   * The bitmask that indicates that a point lies to the left of this.
   */
  public static final int POS_LEFT = 1;
  /**
   * The bitmask that indicates that a point lies above this.
   */
  public static final int POS_TOP = 2;
  /**
   * The bitmask that indicates that a point lies to the right of this.
   */
  public static final int POS_RIGHT = 4;
  /**
   * The bitmask that indicates that a point lies below this.
   */
  public static final int POS_BOTTOM = 8;

  /**
   * The Constructor.
   *
   * @param x the left-top X
   * @param y the left-top Y
   * @param width the width
   * @param height the height
   */
  public HexRect2D(final float x, final float y, final float width, final float height) {
    this.left = x;
    this.top = y;
    this.width = width;
    this.height = height;
  }

  /**
   * Get the left X as integer.
   *
   * @return the left X as integer.
   */
  public int getLeftAsInt() {
    return Math.round(this.left);
  }

  /**
   * Get the left X.
   *
   * @return the left X as float
   */
  public float getLeft() {
    return this.left;
  }

  /**
   * Get the top Y.
   *
   * @return the top Y as float
   */
  public float getTop() {
    return this.top;
  }

  /**
   * Get the top Y as integer.
   *
   * @return the top Y as integer.
   */
  public int getTopAsInt() {
    return Math.round(this.top);
  }

  /**
   * Get the right X as float
   *
   * @return the right X as float
   */
  public float getRight() {
    return this.left + this.width;
  }

  /**
   * Get the right X as integer
   *
   * @return the right X as integer
   */
  public int getRightAsInt() {
    return Math.round(getRight());
  }

  /**
   * Get the bottom Y as float
   *
   * @return the bottom Y as float
   */
  public float getBottom() {
    return this.top + this.height;
  }

  /**
   * Get the bottom Y as integer
   *
   * @return the bottom Y as integer
   */
  public int getBottomAsInt() {
    return Math.round(getBottom());
  }

  /**
   * Get the width as float
   *
   * @return the rectangle width as float value
   */
  public float getWidth() {
    return this.width;
  }

  /**
   * Get the width as integer
   *
   * @return the rectangle width as integer
   */
  public int getWidthAsInt() {
    return Math.round(this.width);
  }

  /**
   * Get the rectangle height as float.
   *
   * @return the rectangle height as float
   */
  public float getHeight() {
    return this.height;
  }

  /**
   * Get the rectangle height as float
   *
   * @return the rectangle height as float
   */
  public int getHeightAsInt() {
    return Math.round(this.height);
  }

  /**
   * Get the center X as float
   *
   * @return the center point X
   */
  public float getCenterX() {
    return this.left + this.width / 2;
  }

  /**
   * Get the center Y as float
   *
   * @return the center point Y
   */
  public float getCenterY() {
    return this.top + this.height / 2;
  }

  /**
   * Get coordinates of the center point.
   *
   * @return the center point coordinates
   */
  public HexPoint2D getCenter() {
    return new HexPoint2D(getCenterX(), getCenterY());
  }

  /**
   * Check that a point is placed inside the rectangle
   *
   * @param p a point to be checked
   * @return true if the point placed inside the rectangle, false otherwise
   */
  public boolean isPointInside(final HexPoint2D p) {
    final float px = p.getX();
    final float py = p.getY();
    return px >= this.left && px < this.left + this.width && py >= this.top && py < this.top + this.height;
  }

  /**
   * Check relative position for the rectangle
   *
   * @param x the x coordinate of checking point
   * @param y the y coordinate of checking point
   * @return detected position flags (as combination) for the point relative to
   * the rectangle
   * @see #POS_BOTTOM
   * @see #POS_LEFT
   * @see #POS_RIGHT
   * @see #POS_TOP
   */
  public int checkRelativePosition(final float x, final float y) {
    int out = 0;
    if (this.width <= 0) {
      out |= POS_LEFT | POS_RIGHT;
    }
    else if (x < this.left) {
      out |= POS_LEFT;
    }
    else if (x > getRight()) {
      out |= POS_RIGHT;
    }
    
    if (this.height <= 0) {
      out |= POS_TOP | POS_BOTTOM;
    }
    else if (y < this.top) {
      out |= POS_TOP;
    }
    else if (y > getBottom()) {
      out |= POS_BOTTOM;
    }
    return out;
  }

  /**
   * Check that a line X0,Y0-X1,Y1 intersects the rectangle
   *
   * @param x0 the X0 coordinate of the line
   * @param y0 the Y0 coordinate of the line
   * @param x1 the X1 coordinate of the line
   * @param y1 the Y1 coordinate of the line
   * @return true if the line intersects the rectangle, false otherwise
   */
  public boolean isLineIntersects(float x0, float y0, float x1, float y1) {
    final int posOfSecPoint;
    if ((posOfSecPoint = checkRelativePosition(x1, y1)) == 0) {
      return true;
    }
    while (true) {
      final int posOfFrstPoint = checkRelativePosition(x0, y0);
      if (posOfFrstPoint == 0) {
        break;
      }

      if ((posOfFrstPoint & posOfSecPoint) != 0) {
        return false;
      }
      if ((posOfFrstPoint & (POS_LEFT | POS_RIGHT)) != 0) {
        float px = this.left;
        if ((posOfFrstPoint & POS_RIGHT) != 0) {
          px += getWidth();
        }
        y0 = y0 + (px - x0) * (y1 - y0) / (x1 - x0);
        x0 = px;
      }
      else {
        float py = this.top;
        if ((posOfFrstPoint & POS_BOTTOM) != 0) {
          py += getHeight();
        }
        x0 = x0 + (py - y0) * (x1 - x0) / (y1 - y0);
        y0 = py;
      }
    }
    return true;
  }

}
