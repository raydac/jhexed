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

import com.igormaznitsa.jhexed.engine.DefaultIntegerHexModel;
import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.engine.HexEngineModel;
import org.junit.Test;
import static org.junit.Assert.*;

public class HexPositionTest {

  @Test
  public void testConstructor() {
    final HexPosition pos = new HexPosition(0xAABBCCDD, 0xCAFEBABE);
    assertEquals(0xAABBCCDD,pos.getColumn());
    assertEquals(0xCAFEBABE,pos.getRow());
  }
  
  @Test
  public void testConstructor_Negative() {
    final HexPosition pos = new HexPosition(-1, -1);
    assertEquals(-1,pos.getColumn());
    assertEquals(-1,pos.getRow());
  }
  
  @Test
  public void testIsAtModel(){
    final HexEngineModel<Integer> model = new DefaultIntegerHexModel(32, 16, -1);
    
    assertTrue(new HexPosition(0, 0).isAtModel(model));
    assertTrue(new HexPosition(31, 15).isAtModel(model));
    assertFalse(new HexPosition(-1, 0).isAtModel(model));
    assertFalse(new HexPosition(0, -1).isAtModel(model));
    assertFalse(new HexPosition(32, 16).isAtModel(model));
    assertFalse(new HexPosition(32, 15).isAtModel(model));
    assertFalse(new HexPosition(31, 16).isAtModel(model));
  }

  @Test
  public void testCalcDistance(){
    final HexPosition base = new HexPosition(10,10);
    assertEquals(0,base.calcDistance(base));
    assertEquals(1,base.calcDistance(new HexPosition(11, 10)));
    assertEquals(1,base.calcDistance(new HexPosition(11, 11)));
    assertEquals(1,base.calcDistance(new HexPosition(10, 11)));
    assertEquals(14,base.calcDistance(new HexPosition(0, 0)));
  }
  
}
