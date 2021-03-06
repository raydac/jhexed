/*
 * Copyright 2014 Igor Maznitsa.
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
package com.igormaznitsa.jhexed.extapp.hexes;

import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.extapp.ApplicationContext;

public interface HexController {
  boolean isLayerVisible(ApplicationContext context, HexLayer layer);
  boolean isValueVisible(ApplicationContext context, HexLayer layer, HexPosition position, int value);
  boolean isValueEnabled(ApplicationContext context, HexLayer layer, HexPosition position, int value);
  boolean allowedValueMovement(ApplicationContext context, HexLayer layer, HexPosition from, HexPosition to, int value);
}
