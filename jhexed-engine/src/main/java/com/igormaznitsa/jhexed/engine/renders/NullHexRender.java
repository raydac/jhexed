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

import com.igormaznitsa.jhexed.engine.HexEngine;

/**
 * A NULL-Renderer without any side effect.
 * 
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 * @param <G> the graphics object to be used to draw graphics.
 */
public final class NullHexRender <G> implements HexEngineRender<G> {

  public NullHexRender(){
  }
  
  public void renderHexCell(final HexEngine<G> engine, final G gfx, final float x, final float y, final int col, final int row) {
  }

  public void attachedToEngine(final HexEngine<?> engine) {
  }

  public void detachedFromEngine(final HexEngine<?> engine) {
  }
}
