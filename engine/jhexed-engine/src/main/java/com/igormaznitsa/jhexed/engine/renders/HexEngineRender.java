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
package com.igormaznitsa.jhexed.engine.renders;

import com.igormaznitsa.jhexed.engine.*;

/**
 * The Interface describes a hex renderer. It will be called by an engine to render hexagons in a position.
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 * @param <G> The Type of Graphic context to be used for rendering.
 * @see HexEngine
 */
public interface HexEngineRender<G> extends AttachDetachAware {
  /**
   * Method to render a hexagon in a position.
   * @param engine The Engine that is calling the method.
   * @param gfx the Graphic context to be used for the operation
   * @param x the X coordinate of Top-Left X of the drawing area
   * @param y the Y coordinate of Top-Left Y of the drawing -area
   * @param col the column of the hexagon
   * @param row the row of the hexagon
   */
  void renderHexCell(HexEngine<G> engine, G gfx, float x, float y, int col, int row);
}
