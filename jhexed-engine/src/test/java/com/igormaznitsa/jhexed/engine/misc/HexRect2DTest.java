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

package com.igormaznitsa.jhexed.engine.misc;

import org.junit.Test;

public class HexRect2DTest {
  
  @Test(timeout = 1000L)
  public void testIsLineIntersects() {
    final HexRect2D rect = new HexRect2D(574.0f, 751.0f, 240.0f, 282.0f);
    rect.isLineIntersects(814.0f, 768.0f, 800.0f, 736.0f);
  }
}
