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

import com.igormaznitsa.jhexed.engine.misc.*;
import com.igormaznitsa.jhexed.engine.renders.*;
import java.util.*;
import java.util.List;

/**
 * The Class implements a Hexagonal engine controller. It work as the central
 * manager of the engine and ensure communication between model and rendering
 * parts.
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 * @param <G> the type of graphics context to be used for rendering operations.
 * @see HexEngineModel
 * @see HexEngineRender
 */
public final class HexEngine<G> {

  /**
   * The Default coefficient for hexagon shape.
   */
  private static final float DEFAULT_COEFF = 0.22f;

  /**
   * The Value contains the current coefficient for hexagon shape.
   */
  private float coeff = DEFAULT_COEFF;

  /**
   * Horizontal orientation of hexagons.
   */
  public static final int ORIENTATION_HORIZONTAL = 0;
  /**
   * Vertical orientation of hexagons.
   */
  public static final int ORIENTATION_VERTICAL = 1;

  /**
   * The base cell width.
   */
  private float cellWidth;
  /**
   * The scaled cell width for the current scale X factor.
   */
  private float scaledCellWidth;

  /**
   * The base cell height.
   */
  private float cellHeight;

  /**
   * The scaled cell height for the current scale Y factor.
   */
  private float scaledCellHeight;

  /**
   * The Orientation of hexagons on the layer.
   */
  private int orientation;

  /**
   * The Offset of the edge side.
   */
  private float offsetForEdgeSide;
  /**
   * The scaled offset of the edge side.
   */
  private float scaledOffsetForEdgeSide;
  /**
   * The half of side.
   */
  private float halfOfSide;
  /**
   * The scaled half of side.
   */
  private float scaledHalfOfSide;

  /**
   * Points of the hexagon.
   */
  private HexPoint2D[] points;
  /**
   * Scaled points for the hexagon.
   */
  private HexPoint2D[] scaledPoints;

  /**
   * Inside locker for operations over renderer
   */
  private final Object rendererLock = new Object();
  /**
   * INside locker for operations over model
   */
  private final Object modelLock = new Object();

  /**
   * The Current engine renderer.
   */
  private HexEngineRender<G> renderer = new NullHexRender<G>();
  /**
   * The Current engine model.
   */
  private HexEngineModel<?> model = new DefaultIntegerHexModel(5, 5, -1);

  /**
   * List of listeners listening inside engine events
   */
  private final List<HexEngineListener> listeners = new ArrayList<HexEngineListener>();

  /**
   * The X scale factor.
   */
  private float scaleX = 1.0f;
  /**
   * The Y scale factor.
   */
  private float scaleY = 1.0f;

  /**
   * A Constructor.
   *
   * @param cellWidth the base cell width.
   * @param cellHeight the base cell height.
   * @param orientation the orientation of the layer.
   * @see #ORIENTATION_HORIZONTAL
   * @see #ORIENTATION_VERTICAL
   */
  public HexEngine(final float cellWidth, final float cellHeight, final int orientation) {
    this(cellWidth, cellHeight, DEFAULT_COEFF, orientation);
  }

  /**
   * Inside method to initialize engine parameters and prepare inside state for
   * them.
   *
   * @param cellWidth the cell width, must not be less than 1
   * @param cellHeight the cell height, must not be less than 1
   * @param coeff the coefficient for hexagon form-factor
   * @param orientation the hexagon orientation
   * @see #ORIENTATION_HORIZONTAL
   * @see #ORIENTATION_VERTICAL
   */
  private void _initMainParameters(final float cellWidth, final float cellHeight, final float coeff, final int orientation) {
    synchronized (rendererLock) {
      if (cellWidth < 1) {
        throw new IllegalArgumentException("Cell width must be greater than 2");
      }
      if (cellHeight < 1) {
        throw new IllegalArgumentException("Cell height must be greater than 2");
      }

      this.coeff = coeff;

      this.orientation = orientation;
      this.cellWidth = cellWidth;
      this.cellHeight = cellHeight;

      this.points = new HexPoint2D[6];

      switch (orientation) {
        case ORIENTATION_HORIZONTAL: {
          this.offsetForEdgeSide = Math.round(this.cellWidth * coeff);
          this.halfOfSide = Math.round(this.cellHeight / 2f);

          this.points[0] = new HexPoint2D(this.offsetForEdgeSide, 0.0f);
          this.points[1] = new HexPoint2D(this.cellWidth - this.offsetForEdgeSide, this.points[0].getY());
          this.points[2] = new HexPoint2D(this.cellWidth, this.halfOfSide);
          this.points[3] = new HexPoint2D(this.points[1], this.cellHeight);
          this.points[4] = new HexPoint2D(this.points[0].getX(), this.points[3].getY());
          this.points[5] = new HexPoint2D(0, this.points[2]);
        }
        break;
        case ORIENTATION_VERTICAL: {
          this.offsetForEdgeSide = Math.round(this.cellHeight * coeff);
          this.halfOfSide = Math.round(this.cellWidth / 2f);

          this.points[0] = new HexPoint2D(this.halfOfSide, 0);
          this.points[1] = new HexPoint2D(this.cellWidth, offsetForEdgeSide);
          this.points[2] = new HexPoint2D(this.points[1], this.cellHeight - offsetForEdgeSide);
          this.points[3] = new HexPoint2D(this.points[0], this.cellHeight);
          this.points[4] = new HexPoint2D(0, this.points[2]);
          this.points[5] = new HexPoint2D(0, this.points[1]);
        }
        break;
        default: {
          throw new Error("Unsupported orientation " + orientation);
        }
      }

      if (!this.listeners.isEmpty()) {
        final List<HexEngineListener> temp = new ArrayList<HexEngineListener>(this.listeners);
        this.listeners.clear();
        setScale(this.scaleX, this.scaleY);
        this.listeners.addAll(temp);
        for (final HexEngineListener l : this.listeners) {
          l.onEngineReconfigured(this);
        }
      }
      else {
        setScale(this.scaleX, this.scaleY);
      }
    }
  }

  /**
   * A Constructor.
   *
   * @param cellWidth the base cell width.
   * @param cellHeight the base cell height.
   * @param coeff the coefficient for the offset of the edge side.
   * @param orientation the orientation of the layer.
   * @see #ORIENTATION_HORIZONTAL
   * @see #ORIENTATION_VERTICAL
   */
  public HexEngine(final float cellWidth, final float cellHeight, final float coeff, final int orientation) {
    _initMainParameters(cellWidth, cellHeight, coeff, orientation);
  }

  /**
   * Allows to change engine parameters.
   *
   * @param cellWidth the cell width
   * @param cellHeight the cell height
   * @param coeff the hexagon for-factor coefficient
   * @param orientation the hexagon orientation
   * @see #ORIENTATION_HORIZONTAL
   * @see #ORIENTATION_VERTICAL
   */
  public void changeEnginebaseParameters(final float cellWidth, final float cellHeight, final float coeff, final int orientation) {
    _initMainParameters(cellWidth, cellHeight, coeff, orientation);
  }

  /**
   * Allows to change engine parameters.
   * @param cellWidth the cell width
   * @param cellHeight the cell height
   * @param orientation the hexagon orientation
   * @see #ORIENTATION_HORIZONTAL
   * @see #ORIENTATION_VERTICAL
   */
  public void changeEngineBaseParameters(final float cellWidth, final float cellHeight, final int orientation) {
    this.changeEnginebaseParameters(cellWidth, cellHeight, this.coeff, orientation);
  }

  /**
   * Get cell width
   *
   * @return the cell width
   */
  public float getCellWidth() {
    return this.cellWidth;
  }

  /**
   * Get scaled cell width
   *
   * @return the current scaled cell width
   */
  public float getScaledCellWidth() {
    return this.scaledCellWidth;
  }

  /**
   * Get cell height
   * @return the cell height
   */
  public float getCellHeight() {
    return this.cellHeight;
  }

  /**
   * Get scaled cell height
   *
   * @return the current scaled cell height
   */
  public float getScaledCellHeight() {
    return this.scaledCellHeight;
  }
  
  /**
   * Register a hex engine listener.
   * @param listener
   */
  public void addHexLayerListener(final HexEngineListener listener) {
    if (listener == null) {
      throw new NullPointerException("Listener is null");
    }
    this.listeners.add(listener);
  }

  /**
   * Unregister a hex engine listener.
   * @param listener
   */
  public void removeHexLayerListener(final HexEngineListener listener) {
    if (listener == null) {
      throw new NullPointerException("Listener is null");
    }
    this.listeners.remove(listener);
  }

  /**
   * Get the hexagon form-factor coefficient.
   * @return the form-factor of the hexagon
   */
  public float getHexCoefficient() {
    return this.coeff;
  }

  /**
   * Set scale coefficient for rendered area.
   *
   * @param scaleX the X scale factor.
   * @param scaleY the Y scale factor.
   */
  @SuppressWarnings("unchecked")
  public void setScale(final float scaleX, final float scaleY) {
    if (scaleX <= 0.0f) {
      throw new IllegalArgumentException("Too small value as scale X [" + scaleX + ']');
    }
    if (scaleY <= 0.0f) {
      throw new IllegalArgumentException("Too small value as scale Y [" + scaleY + ']');
    }

    synchronized (this.rendererLock) {
      this.scaleX = scaleX;
      this.scaleY = scaleY;

      this.scaledCellWidth = this.cellWidth * this.scaleX;
      this.scaledCellHeight = this.cellHeight * this.scaleY;

      this.scaledPoints = new HexPoint2D[6];
      for (int i = 0; i < 6; i++) {
        this.scaledPoints[i] = new HexPoint2D(this.points[i], scaleX, scaleY);
      }

      switch (this.orientation) {
        case ORIENTATION_HORIZONTAL: {
          this.scaledHalfOfSide = this.halfOfSide * scaleY;
          this.scaledOffsetForEdgeSide = this.offsetForEdgeSide * scaleX;
        }
        break;
        case ORIENTATION_VERTICAL: {
          this.scaledHalfOfSide = this.halfOfSide * scaleX;
          this.scaledOffsetForEdgeSide = this.offsetForEdgeSide * scaleY;
        }
        break;
        default:
          throw new Error("Unsupported orientation");
      }
    }

    if (!this.listeners.isEmpty()) {
      for (final HexEngineListener l : this.listeners) {
        l.onScaleFactorChanged(this, scaleX, scaleY);
      }
    }
  }

  /**
   * Get the X scale factor.
   *
   * @return the X scale factor as float
   */
  public float getScaleX() {
    return this.scaleX;
  }

  /**
   * Get the Y scale factor.
   *
   * @return the Y scale factor as float
   */
  public float getScaleY() {
    return this.scaleY;
  }

  /**
   * Set cell renderer.
   *
   * @param renderer the cell renderer for the layer, must not be null.
   */
  @SuppressWarnings("unchecked")
  public void setRenderer(final HexEngineRender<G> renderer) {
    if (renderer == null) {
      throw new NullPointerException("Renderer must not be null");
    }

    final HexEngineRender<G> old;
    synchronized (this.rendererLock) {
      old = this.renderer;
      if (this.renderer != null) {
        this.renderer.detachedFromEngine(this);
      }
      this.renderer = renderer;
      this.renderer.attachedToEngine(this);
    }
    if (!this.listeners.isEmpty()) {
      for (final HexEngineListener l : this.listeners) {
        l.onRenderChanged(this, old, renderer);
      }
    }
  }

  /**
   * Get the current cell renderer.
   *
   * @return the current cell renderer.
   */
  public HexEngineRender<G> getRenderer() {
    return this.renderer;
  }

  /**
   * Set the model for the layer.
   *
   * @param model a model to be set as the current one, must not be null.
   */
  public void setModel(final HexEngineModel<?> model) {
    if (model == null) {
      throw new NullPointerException("Model must not be null");
    }
    final HexEngineModel<?> old;

    synchronized (this.modelLock) {
      old = this.model;

      if (this.model != null) {
        this.model.detachedFromEngine(this);
      }
      this.model = model;
      this.model.attachedToEngine(this);
    }

    if (!this.listeners.isEmpty()) {
      for (final HexEngineListener l : this.listeners) {
        l.onModelChanged(this, old, model);
      }
    }
  }

  /**
   * Get the current model for the layer.
   *
   * @return the current model.
   */
  public HexEngineModel<?> getModel() {
    return this.model;
  }

  /**
   * Calculate the left X offset of a cell.It takes in count the scale factor.
   *
   * @param column the column number.
   * @param row the row number.
   * @return the calculated X.
   */
  public float calculateX(final int column, final int row) {
    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        return column * (this.scaledCellWidth - this.scaledOffsetForEdgeSide);
      }
      case ORIENTATION_VERTICAL: {
        return (column * this.scaledCellWidth) + ((row & 1) == 0 ? 0 : this.scaledHalfOfSide);
      }
      default: {
        throw new Error("Unsupported orientation");
      }
    }
  }

  /**
   * Calculate the covered hex position by a point coordinates in the hex
   * @param pointX the point x
   * @param pointY the point y
   * @return the hex coordinate
   */
  public HexPosition pointToHex(final float pointX, final float pointY) {
    return new HexPosition(calculateColumn(pointX, pointY), calculateRow(pointX, pointY));
  }

  /**
   * Calculate hexagon column for X,Y coordinates. It takes in count the scale
   * factor.
   *
   * @param x the X coordinate of point inside of the hexagon.
   * @param y the Y coordinate of point inside of the hexagon.
   * @return the hexagon column index.
   */
  public int calculateColumn(final float x, final float y) {

    if (x < 0) {
      return -1;
    }

    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        int column = (int) (x / (this.scaledCellWidth - this.scaledOffsetForEdgeSide));
        final int row = (int) ((y - ((column & 1) == 0 ? 0 : this.scaledHalfOfSide)) / this.scaledCellHeight);

        final float xoffset = calculateX(column, row);
        final float yoffset = calculateY(column, row);

        final float normalX = x - xoffset;
        final float normalY = y - yoffset;

        if (normalX < this.scaledOffsetForEdgeSide) {
          if (!this.scaledPoints[0].isPointInTriangle(this.scaledPoints[4], this.scaledPoints[5], normalX, normalY)) {
            column--;
          }
        }
        else if (normalX > this.scaledCellWidth - this.scaledOffsetForEdgeSide) {
          if (!this.scaledPoints[1].isPointInTriangle(this.scaledPoints[2], this.scaledPoints[3], normalX, normalY)) {
            column++;
          }
        }
        return column;
      }
      case ORIENTATION_VERTICAL: {
        final int row = (int) (y / (this.scaledCellHeight - this.scaledOffsetForEdgeSide));

        int column = (int) ((x - ((row & 1) == 0 ? 0 : this.scaledHalfOfSide)) / this.scaledCellWidth);

        final boolean emptyHexCase = x < this.scaledHalfOfSide && (row & 1) != 0;

        float xoffset = calculateX(column, row);
        final float yoffset = calculateY(column, row);

        final float normalX = x - xoffset;
        final float normalY = y - yoffset;

        if (normalY < this.scaledOffsetForEdgeSide) {
          if (!this.scaledPoints[5].isPointInTriangle(this.scaledPoints[0], this.scaledPoints[1], normalX, normalY)) {
            column += (normalX > this.scaledHalfOfSide ? row & 1 : -((row & 1) ^ 1));
          }
        }
        else if (emptyHexCase) {
          column = -1;
        }

        return column;
      }
      default:
        throw new Error("Unsupported orientation");
    }
  }

  /**
   * Calculate hexagon row for X,Y coordinates. It takes in count the scale
   * factor.
   *
   * @param x the X coordinate of point inside of the hexagon.
   * @param y the Y coordinate of point inside of the hexagon.
   * @return the hexagon row index.
   */
  public int calculateRow(final float x, final float y) {
    if (y < 0) {
      return -1;
    }

    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        final int column = calculateColumn(x, y);

        int row;

        if ((column & 1) == 0) {
          row = (int) (y / this.scaledCellHeight);
        }
        else {
          if (y < this.scaledHalfOfSide) {
            row = -1;
          }
          else {
            row = (int) ((y - this.scaledHalfOfSide) / this.scaledCellHeight);
          }
        }

        return row;
      }
      case ORIENTATION_VERTICAL: {
        int row = (int) (y / (this.scaledCellHeight - this.scaledOffsetForEdgeSide));
        final int column = (int) ((x - ((row & 1) == 0 ? 0 : this.scaledHalfOfSide)) / this.scaledCellWidth);

        final float xoffset = calculateX(column, row);
        final float yoffset = calculateY(column, row);

        final float normalX = x - xoffset;
        final float normalY = y - yoffset;

        if (normalY < this.scaledOffsetForEdgeSide) {
          if (!this.scaledPoints[5].isPointInTriangle(this.scaledPoints[0], this.scaledPoints[1], normalX, normalY)) {
            row--;
          }
        }
        else if (normalY > this.scaledCellHeight - this.scaledOffsetForEdgeSide) {
          if (!this.scaledPoints[2].isPointInTriangle(this.scaledPoints[3], this.scaledPoints[4], normalX, normalY)) {
            row++;
          }
        }

        return row;
      }
      default:
        throw new Error("Unsupported orientation");
    }
  }

  /**
   * Calculate the top Y offset of a cell.It takes in count the scale factor.
   *
   * @param column the column number.
   * @param row the row number.
   * @return the calculated Y.
   */
  public float calculateY(final int column, final int row) {
    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        return (row * this.scaledCellHeight) + ((column & 1) == 0 ? 0 : this.scaledHalfOfSide);
      }
      case ORIENTATION_VERTICAL: {
        return row * (this.scaledCellHeight - this.scaledOffsetForEdgeSide);
      }
      default: {
        throw new Error("Unsupported orientation");
      }
    }
  }

  /**
   * Check is the hexagon visible for a rectangle.
   *
   * @param pos the hexagon position.
   * @param rect the rectangle to check.
   * @param accurately true - to check the visibility accurately, false - rough
   * fast check.
   * @return true if the hexagon is visible, false otherwise
   */
  public boolean isPositionVisible(final HexPosition pos, final HexRect2D rect, final boolean accurately) {
    return this.isPositionVisible(pos.getColumn(), pos.getRow(), rect, accurately);
  }

  /**
   * Check is the hexagon visible for a rectangle.
   *
   * @param col the hexagon column.
   * @param row the hexagon row.
   * @param rect the rectangle to check.
   * @param accurately true - to check the visibility accurately, false - rough
   * fast check.
   * @return true if the hexagon is visible, false otherwise
   */
  public boolean isPositionVisible(final int col, final int row, final HexRect2D rect, final boolean accurately) {
    final float cellX = calculateX(col, row);
    final float cellY = calculateY(col, row);

    float cx0 = cellX;
    float cy0 = cellY;

    float cx1 = cx0 + this.scaledCellWidth;
    float cy1 = cy0 + this.scaledCellHeight;

    final float rw = rect.getWidth();
    final float rh = rect.getHeight();
    if (rw <= 0.0f || rh <= 0.0f) {
      return false;
    }

    final float rx0 = rect.getLeft();
    final float rx1 = rect.getRight();
    final float ry0 = rect.getTop();
    final float ry1 = rect.getBottom();

    if (cx0 >= rx1 || cx1 < rx0 || cy0 >= ry1 || cy1 < ry0) {
      return false;
    }

    if (accurately) {

      // check inside rectangle intersection to figure out that intersection is not in "gray" zones
      switch (this.orientation) {
        case ORIENTATION_VERTICAL: {
          cy0 += scaledOffsetForEdgeSide;
          cy1 -= scaledOffsetForEdgeSide;
          if (cx0 >= rx1 || cx1 < rx0 || cy0 >= ry1 || cy1 < ry0) {
            if (this.scaledPoints[5].intersectsRectangle(this.scaledPoints[0], rect, cellX, cellY)) {
              return true;
            }
            if (this.scaledPoints[0].intersectsRectangle(this.scaledPoints[1], rect, cellX, cellY)) {
              return true;
            }
            if (this.scaledPoints[2].intersectsRectangle(this.scaledPoints[3], rect, cellX, cellY)) {
              return true;
            }
            if (this.scaledPoints[3].intersectsRectangle(this.scaledPoints[4], rect, cellX, cellY)) {
              return true;
            }
            return false;
          }
        }
        break;
        case ORIENTATION_HORIZONTAL: {
          cx0 += this.scaledOffsetForEdgeSide;
          cx1 -= this.scaledOffsetForEdgeSide;
          if (cx0 >= rx1 || cx1 < rx0 || cy0 >= ry1 || cy1 < ry0) {
            if (this.scaledPoints[0].intersectsRectangle(this.scaledPoints[1], rect, cellX, cellY)) {
              return true;
            }
            if (this.scaledPoints[1].intersectsRectangle(this.scaledPoints[2], rect, cellX, cellY)) {
              return true;
            }
            if (this.scaledPoints[3].intersectsRectangle(this.scaledPoints[4], rect, cellX, cellY)) {
              return true;
            }
            return this.scaledPoints[4].intersectsRectangle(this.scaledPoints[5], rect, cellX, cellY);
          }
        }
        break;
        default:
          throw new Error("Unsupported orientation");
      }
    }
    return true;
  }

  /**
   * Draw a hexagon. It is used by internal operations.
   *
   * @param gfx the object to used for draw operation.
   * @param column the column of the hexagon.
   * @param row the row of the hexagon.
   */
  @SuppressWarnings("unchecked")
  private void _drawHex(final G gfx, final int column, final int row) {
    final float calcx = calculateX(column, row);
    final float calcy = calculateY(column, row);
    this.renderer.renderHexCell(this, gfx, calcx, calcy, column, row);
  }

  /**
   * Draw a hexagon.
   *
   * @param gfx the object to used for draw operation.
   * @param column the column of the hexagon.
   * @param row the row of the hexagon.
   */
  public void drawHex(final G gfx, final int column, final int row) {
    synchronized (this.rendererLock) {
      synchronized (this.modelLock) {
        _drawHex(gfx, column, row);
      }
    }
  }

  /**
   * Get list of hexagon positions covered by a rectangle.
   *
   * @param rect the rectangle area.
   * @param accurately true - to check accurately, false - rough fast checking.
   * @return the list of covered hexagonal positions by the rectangle.
   */
  public List<HexPosition> getCoveredHexes(final HexRect2D rect, final boolean accurately) {
    final int elementsNum = Math.round((rect.getWidth() / this.scaledCellWidth + 1) * (rect.getHeight() / this.scaledCellHeight + 1));
    final List<HexPosition> result = new ArrayList<HexPosition>(elementsNum);

    synchronized (this.modelLock) {
      final float rightx = rect.getRight();
      final float righty = rect.getBottom();

      int tlColumn = calculateColumn(rect.getLeft(), rect.getTop()) - 1;
      int tlRow = calculateRow(rect.getLeft(), rect.getTop()) - 1;
      int brColumn = calculateColumn(rightx, righty) + 1;
      int brRow = calculateRow(rightx, righty) + 1;

      for (int y = tlRow; y <= brRow; y++) {
        for (int x = tlColumn; x <= brColumn; x++) {
          if (this.model.isPositionValid(x, y) && isPositionVisible(x, y, rect, accurately)) {
            result.add(new HexPosition(x, y));
          }
        }
      }
    }
    return result;
  }

  /**
   * Draw hexagons covered by a rectangle.
   *
   * @param gfx a graphic object to be used for draw.
   * @param rect the rectangle area.
   * @param accurately true if to check coverage accurately, false if check
   * should be fast but rough.
   */
  public void drawArea(final G gfx, final HexRect2D rect, final boolean accurately) {
    synchronized (this.rendererLock) {
      synchronized (this.modelLock) {
        final float rightx = rect.getRight();
        final float righty = rect.getBottom();

        int tlColumn = calculateColumn(rect.getLeft(), rect.getTop()) - 1;
        int tlRow = calculateRow(rect.getLeft(), rect.getTop()) - 1;
        int brColumn = calculateColumn(rightx, righty) + 1;
        int brRow = calculateRow(rightx, righty) + 1;

        for (int y = tlRow; y <= brRow; y++) {
          for (int x = tlColumn; x <= brColumn; x++) {
            if (this.model.isPositionValid(x, y) && isPositionVisible(x, y, rect, accurately)) {
              _drawHex(gfx, x, y);
            }
          }
        }
      }
    }
  }

  /**
   * Draw whole layer.
   *
   * @param gfx a graphic object to be used for draw.
   */
  public void draw(final G gfx) {
    synchronized (this.rendererLock) {
      synchronized (this.modelLock) {
        for (int r = 0; r < this.model.getRowNumber(); r++) {
          for (int c = 0; c < this.model.getColumnNumber(); c++) {
            _drawHex(gfx, c, r);
          }
        }
      }
    }
  }

  /**
   * Draw whole layer with check of the current thread interruption. If the current thread is interrupted then the method will cancel its work
   *
   * @param gfx a graphic object to be used for draw.
   */
  public void drawWithThreadInterruptionCheck(final G gfx) {
    synchronized (this.rendererLock) {
      synchronized (this.modelLock) {
        for (int r = 0; r < this.model.getRowNumber(); r++) {
          for (int c = 0; c < this.model.getColumnNumber(); c++) {
            if (Thread.currentThread().isInterrupted()) {
              return;
            }
            _drawHex(gfx, c, r);
          }
        }
      }
    }
  }

  /**
   * Get the layer hexagon orientation.
   *
   * @return
   */
  public int getOrientation() {
    return this.orientation;
  }

  /**
   * Get pre-calculated points of a hexagon area for 0,0 position.
   *
   * @return pointe of a hexagon area for 0,0
   */
  public HexPoint2D[] getHexPoints() {
    return this.points;
  }

  /**
   * Get pre-calculated scaled points for current scale factor of a hexagon area
   * for 0,0 position.
   *
   * @return pointe of a hexagon area for 0,0
   */
  public HexPoint2D[] getHexScaledPoints() {
    return this.scaledPoints;
  }

  /**
   * Get the nearest neighbor position for an index.
   *
   * @param pos a hexagon position
   * @param neighborPositionIndex an index of a neighbor (0..5)
   * @return the hexagon position of a neighbor for the index.
   */
  public HexPosition getNearestNeighbourPosition(final HexPosition pos, final int neighborPositionIndex) {
    return this.getNearestNeighbourPosition(pos.getColumn(), pos.getRow(), neighborPositionIndex);
  }

  /**
   * Get the nearest neighbor position for an index.
   *
   * @param col the hexagon column
   * @param row the hexagon row
   * @param neighbourIndex an index of a neighbor (0..5)
   * @return the hexagon position of a neighbor for the index.
   */
  public HexPosition getNearestNeighbourPosition(final int col, final int row, final int neighbourIndex) {
    final HexPosition result;
    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        switch (neighbourIndex) {
          case 0: { // -
            result = new HexPosition(col, row - 1);
          }
          break;
          case 1: { // \
            result = new HexPosition(col + 1, row - (col & 1 ^ 1));
          }
          break;
          case 2: { // /
            result = new HexPosition(col + 1, row + (col & 1));
          }
          break;
          case 3: { // -
            result = new HexPosition(col, row + 1);
          }
          break;
          case 4: { // \
            result = new HexPosition(col - 1, row + (col & 1));
          }
          break;
          case 5: { // /
            result = new HexPosition(col - 1, row - (col & 1 ^ 1));
          }
          break;
          default:
            throw new IllegalArgumentException("Neighbour index must be 0..5 [" + neighbourIndex + ']');
        }
      }
      break;
      case ORIENTATION_VERTICAL: {
        switch (neighbourIndex) {
          case 0: {
            result = new HexPosition(col + (row & 1), row - 1);
          }
          break;
          case 1: {
            result = new HexPosition(col + 1, row);
          }
          break;
          case 2: {
            result = new HexPosition(col + (row & 1), row + 1);
          }
          break;
          case 3: {
            result = new HexPosition(col - (row & 1 ^ 1), row + 1);
          }
          break;
          case 4: {
            result = new HexPosition(col - 1, row);
          }
          break;
          case 5: {
            result = new HexPosition(col - (row & 1 ^ 1), row - 1);
          }
          break;
          default:
            throw new IllegalArgumentException("Neighbour index must be 0..5 [" + neighbourIndex + ']');
        }
      }
      break;
      default:
        throw new Error("Unsupported orientation");
    }
    return result;
  }

  /**
   * Get all neighbor positions for a hexagon position.
   *
   * @param pos the hexagon position.
   * @param distance the distance of neghbors.
   * @return array of neighbor positions for the distance, the length calculated
   * as distance*6
   */
  public HexPosition[] getNeighbourPositions(final HexPosition pos, final int distance) {
    return this.getNeighbourPositions(null, pos.getColumn(), pos.getRow(), distance);
  }

  /**
   * Get all neighbor positions for a hexagon position.
   *
   * @param array the array to be filled by the positions, if the array is null
   * or its length is less, then a new array will be created
   * @param pos the hexagon position.
   * @param distance the distance of neighbors.
   * @return array of neighbor positions for the distance, the length calculated
   * as distance*6
   */
  public HexPosition[] getNeighbourPositions(final HexPosition[] array, final HexPosition pos, final int distance) {
    return this.getNeighbourPositions(array, pos.getColumn(), pos.getRow(), distance);
  }

  /**
   * Get all neighbor positions for a hexagon positions.
   *
   * @param col the column of a hex
   * @param row the row of a hex
   * @param distance the distance of neighbors
   * @return array of neighbor positions for the distance, the length calculated
   * as distance*6
   */
  public HexPosition[] getNeighbourPositions(final int col, final int row, final int distance) {
    return this.getNeighbourPositions(null, col, row, distance);
  }

  /**
   * Calculate number of columns and rows for a rectangular area.
   *
   * @param areaWidth the width of a rectangular area
   * @param areaHeight the height of a rectangular area
   * @return a width-height object contains number of columns and rows for a
   * model which can fill the rectangular area
   */
  public WidthHeightPair calculateHexesForRectangle(final float areaWidth, final float areaHeight) {
    final WidthHeightPair result;

    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        int columns = (int) ((areaWidth - this.offsetForEdgeSide) / (this.cellWidth - this.offsetForEdgeSide));
        int rows = (int) ((areaHeight - (columns > 1 ? this.halfOfSide : 0)) / this.cellHeight);

        if (columns > 0 && rows == 0 && areaHeight >= this.cellHeight) {
          rows = 1;
          columns = 1;
        }

        result = new WidthHeightPair(columns, rows);
      }
      break;
      case ORIENTATION_VERTICAL: {
        int rows = (int) ((areaHeight - this.offsetForEdgeSide) / (this.cellHeight - this.offsetForEdgeSide));
        int columns = (int) ((areaWidth - (rows > 1 ? this.halfOfSide : 0)) / this.cellWidth);

        if (columns > 0 && rows == 0 && areaHeight >= this.cellHeight) {
          rows = 1;
          columns = 1;
        }

        result = new WidthHeightPair(columns, rows);
      }
      break;
      default:
        throw new Error("Unsupported orientation");
    }
    return result;
  }

  /**
   * Get size of the full hexagonal area for the current zoom.
   *
   * @return the rectangle to show whole hexagonal area for the current zoom
   */
  public HexRect2D getVisibleSize() {
    final int cellsAtHorz = this.model.getColumnNumber();
    final int cellsAtVert = this.model.getRowNumber();

    float w;
    float h;

    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        w = (this.scaledCellWidth - this.scaledOffsetForEdgeSide) * cellsAtHorz + this.scaledOffsetForEdgeSide;
        h = this.scaledCellHeight * cellsAtVert + (cellsAtHorz > 1 ? this.scaledHalfOfSide : 0);
      }
      break;
      case ORIENTATION_VERTICAL: {
        w = this.scaledCellWidth * cellsAtHorz + (cellsAtVert > 1 ? this.scaledHalfOfSide : 0);
        h = (this.scaledCellHeight - this.scaledOffsetForEdgeSide) * cellsAtVert + this.scaledOffsetForEdgeSide;
      }
      break;
      default: {
        throw new IllegalStateException("Unsupported orientation [" + this.orientation + ']');
      }
    }

    return new HexRect2D(0, 0, w, h);
  }

  /**
   * Auxiliary method to pack column-row into an integer value
   *
   * @param col the column (0...0xFFFF)
   * @param row the row (0...0xFFFF)
   * @return the packed value as integer
   */
  public static int packColumnRow(final int col, final int row) {
    final int c = col < 0 ? 0xFFFF : col;
    final int r = row < 0 ? 0xFFFF : row;
    return (c << 16) | r;
  }

  /**
   * Auxiliary method to extract the column value from a packed column-row pair.
   *
   * @param packedValue a packed column-row pair
   * @return the column value extracted from the pair
   */
  public static int extractColumn(final int packedValue) {
    final int result = packedValue >>> 16;
    return result == 0xFFFF ? -1 : result;
  }

  /**
   * Auxiliary method to extract the row value from a packed column-row pair.
   *
   * @param packedValue a packed column-row pair
   * @return the row value extracted from the pair
   */
  public static int extractRow(final int packedValue) {
    final int result = packedValue & 0xFFFF;
    return result == 0xFFFF ? -1 : result;
  }

  /**
   * Get neighbor positions as packed column-row pairs for a distance.
   *
   * @param array the array will be used for result but it can be null or it can
   * be smaller than needed one and in the case a new array will be generated
   * @param col the column of a hex to get its neighbors
   * @param row the row of a hex to get its neighbors
   * @param distance a distance
   * @return array with first distance*6 elements contains packed column-row
   * values
   */
  public int[] getPackedNeighbourPositions(final int[] array, final int col, final int row, final int distance) {
    if (distance <= 0) {
      return new int[]{packColumnRow(col, row)};
    }

    final int resultLen = distance * 6;

    final int[] result;
    if (array == null || array.length < resultLen) {
      result = new int[resultLen];
    }
    else {
      result = array;
    }

    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        int varCol = col;
        int varRow = row - distance;

        int pos = 0;

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = packColumnRow(varCol, varRow);
          varRow += varCol & 1;
          varCol++;
        }

        for (int i = 0; i < distance; i++) { // |
          result[pos++] = packColumnRow(varCol, varRow);
          varRow++;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = packColumnRow(varCol, varRow);
          varRow += varCol & 1;
          varCol--;
        }

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = packColumnRow(varCol, varRow);
          varCol--;
          varRow -= varCol & 1;
        }

        for (int i = 0; i < distance; i++) { // |
          result[pos++] = packColumnRow(varCol, varRow);
          varRow--;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = packColumnRow(varCol, varRow);
          varCol++;
          varRow -= varCol & 1;
        }
      }
      break;
      case ORIENTATION_VERTICAL: {
        int varCol = col + (distance >> 1) + (row & distance & 1);
        int varRow = row - distance;

        int pos = 0;

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = packColumnRow(varCol, varRow);
          varCol += varRow & 1;
          varRow++;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = packColumnRow(varCol, varRow);
          varRow++;
          varCol -= varRow & 1;
        }

        for (int i = 0; i < distance; i++) { // -
          result[pos++] = packColumnRow(varCol, varRow);
          varCol--;
        }

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = packColumnRow(varCol, varRow);
          varRow--;
          varCol -= varRow & 1;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = packColumnRow(varCol, varRow);
          varCol += varRow & 1;
          varRow--;
        }

        for (int i = 0; i < distance; i++) { // -
          result[pos++] = packColumnRow(varCol, varRow);
          varCol++;
        }
      }
      break;
      default:
        throw new Error("Unsupported orientation");
    }

    return result;
  }

  /**
   * Get all neighbor positions for a hexagon position.
   *
   * @param array array to be used for result if it is not null and length
   * enough
   * @param col the column of the hex
   * @param row the row of the hex
   * @param distance the distance of neighbors.
   * @return the filled array or a new array of neighbor positions for the
   * distance, the length calculated as distance*6
   */
  public HexPosition[] getNeighbourPositions(final HexPosition[] array, final int col, final int row, final int distance) {
    if (distance <= 0) {
      return new HexPosition[]{new HexPosition(col, row)};
    }

    final int resultLen = distance * 6;

    final HexPosition[] result;
    if (array == null || array.length < resultLen) {
      result = new HexPosition[resultLen];
    }
    else {
      result = array;
    }

    switch (this.orientation) {
      case ORIENTATION_HORIZONTAL: {
        int varCol = col;
        int varRow = row - distance;

        int pos = 0;

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = new HexPosition(varCol, varRow);
          varRow += varCol & 1;
          varCol++;
        }

        for (int i = 0; i < distance; i++) { // |
          result[pos++] = new HexPosition(varCol, varRow);
          varRow++;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = new HexPosition(varCol, varRow);
          varRow += varCol & 1;
          varCol--;
        }

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = new HexPosition(varCol, varRow);
          varCol--;
          varRow -= varCol & 1;
        }

        for (int i = 0; i < distance; i++) { // |
          result[pos++] = new HexPosition(varCol, varRow);
          varRow--;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = new HexPosition(varCol, varRow);
          varCol++;
          varRow -= varCol & 1;
        }
      }
      break;
      case ORIENTATION_VERTICAL: {
        int varCol = col + (distance >> 1) + (row & distance & 1);
        int varRow = row - distance;

        int pos = 0;

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = new HexPosition(varCol, varRow);
          varCol += varRow & 1;
          varRow++;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = new HexPosition(varCol, varRow);
          varRow++;
          varCol -= varRow & 1;
        }

        for (int i = 0; i < distance; i++) { // -
          result[pos++] = new HexPosition(varCol, varRow);
          varCol--;
        }

        for (int i = 0; i < distance; i++) { // \
          result[pos++] = new HexPosition(varCol, varRow);
          varRow--;
          varCol -= varRow & 1;
        }

        for (int i = 0; i < distance; i++) { // /
          result[pos++] = new HexPosition(varCol, varRow);
          varCol += varRow & 1;
          varRow--;
        }

        for (int i = 0; i < distance; i++) { // -
          result[pos++] = new HexPosition(varCol, varRow);
          varCol++;
        }
      }
      break;
      default:
        throw new Error("Unsupported orientation");
    }

    return result;
  }
}
