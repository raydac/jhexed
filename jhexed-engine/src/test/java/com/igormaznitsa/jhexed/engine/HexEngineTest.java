package com.igormaznitsa.jhexed.engine;

import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.engine.misc.WidthHeightPair;
import org.junit.Test;
import static org.junit.Assert.*;

public class HexEngineTest {

  private void assertNearestNeighbours(final HexEngine<?> eng, final HexPosition pos, final int... coords) {
    assertEquals(12, coords.length);

    for (int i = 0; i < 6; i++) {
      final int col = coords[i * 2];
      final int row = coords[i * 2 + 1];
      final HexPosition n = eng.getNearestNeighbourPosition(pos, i);
      assertEquals("Column for " + i + " must be " + col, col, n.getColumn());
      assertEquals("Row for " + i + " must be " + row, row, n.getRow());
    }

  }

  private void assertPositions(final HexPosition[] pos, final int... coords) {
    final int pairs = coords.length / 2;

    assertEquals(pos.length, pairs);

    for (int i = 0; i < pairs; i++) {
      final int col = coords[i * 2];
      final int row = coords[i * 2 + 1];
      assertEquals("Column [" + i + ']', col, pos[i].getColumn());
      assertEquals("Row [" + i + ']', row, pos[i].getRow());
    }
  }

  private void assertPositions(final int[] pos, final int... coords) {
    final int pairs = coords.length / 2;

    assertEquals(pos.length, pairs);

    for (int i = 0; i < pairs; i++) {
      final int col = coords[i * 2];
      final int row = coords[i * 2 + 1];
      assertEquals("Column [" + i + ']', col, HexEngine.extractColumn(pos[i]));
      assertEquals("Row [" + i + ']', row, HexEngine.extractRow(pos[i]));
    }
  }

  @Test
  public void testPointToHex_Horizontal(){
    final HexEngine<Object> eng = new HexEngine<Object>(32, 16, HexEngine.ORIENTATION_HORIZONTAL);
    assertEquals(new HexPosition(-1, -1), eng.pointToHex(0, 0));
    assertEquals(new HexPosition(0, 0), eng.pointToHex(16, 8));
    assertEquals(new HexPosition(0, 0), eng.pointToHex(1, 8));
    assertEquals(new HexPosition(0, 0), eng.pointToHex(31, 8));
    assertEquals(new HexPosition(1, 0), eng.pointToHex(48, 16));
    assertEquals(new HexPosition(0, 1), eng.pointToHex(16, 24));
    assertEquals(new HexPosition(0, 2), eng.pointToHex(16, 40));
    
    for(int c = -128; c < 128; c++){
      for(int r = -5; r < 128; r++){
        final float hexx = eng.calculateX(c, r);
        final float hexy = eng.calculateY(c, r);
        assertEquals(new HexPosition(c<0 ? -1 : c, r<0? -1 : r),eng.pointToHex(hexx+16, hexy+8));
      }
    }
    
  }
  
  @Test
  public void testPointToHex_Vertical(){
    final HexEngine<Object> eng = new HexEngine<Object>(32, 16, HexEngine.ORIENTATION_VERTICAL);
    assertEquals(new HexPosition(0, 0), eng.pointToHex(16, 8));
    assertEquals(new HexPosition(0, 0), eng.pointToHex(1, 8));
    assertEquals(new HexPosition(0, 0), eng.pointToHex(31, 8));
    assertEquals(new HexPosition(0, 0), eng.pointToHex(16, 1));
    assertEquals(new HexPosition(0, 0), eng.pointToHex(16, 15));
    assertEquals(new HexPosition(1, 0), eng.pointToHex(48, 8));
    assertEquals(new HexPosition(0, 1), eng.pointToHex(32, 16));
    assertEquals(new HexPosition(0, 2), eng.pointToHex(16, 32));
    assertEquals(new HexPosition(-1, 1), eng.pointToHex(1, 24));

    for (int c = -128; c < 128; c++) {
      for (int r = -5; r < 128; r++) {
        final float hexx = eng.calculateX(c, r);
        final float hexy = eng.calculateY(c, r);
        assertEquals(new HexPosition(c < 0 ? -1 : c, r < 0 ? -1 : r), eng.pointToHex(hexx + 16, hexy + 8));
      }
    }

  }
  
  @Test
  public void testGetClockwisePositionsAroundHex_Horizontal_0() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertPositions(eng.getNeighbourPositions(4, 8, 0), 4, 8);
  }

  @Test
  public void testGetClockwisePositionsAroundHex_Horizontal_1_oddColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertPositions(eng.getNeighbourPositions(5, 6, 1), 5, 5, 6, 6, 6, 7, 5, 7, 4, 7, 4, 6);
  }

  @Test
  public void testGetPackedClockwisePositionsAroundHex_Horizontal_1_oddColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertPositions(eng.getPackedNeighbourPositions(null,5, 6, 1), 5, 5, 6, 6, 6, 7, 5, 7, 4, 7, 4, 6);
  }

  @Test
  public void testGetClockwisePositionsAroundHex_Vertical_1_oddColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);
    assertPositions(eng.getNeighbourPositions(5, 6, 1), 5, 5, 6, 6, 5, 7, 4, 7, 4, 6, 4, 5);
  }

  @Test
  public void testGetPackedClockwisePositionsAroundHex_Vertical_1_oddColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);
    assertPositions(eng.getPackedNeighbourPositions(null,5, 6, 1), 5, 5, 6, 6, 5, 7, 4, 7, 4, 6, 4, 5);
  }

  @Test
  public void testGetClockwisePositionsAroundHex_Horizontal_1_evenColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertPositions(eng.getNeighbourPositions(10, 11, 1), 10, 10, 11, 10, 11, 11, 10, 12, 9, 11, 9, 10);
  }

  @Test
  public void testGetPackedClockwisePositionsAroundHex_Horizontal_1_evenColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertPositions(eng.getPackedNeighbourPositions(null, 10, 11, 1), 10, 10, 11, 10, 11, 11, 10, 12, 9, 11, 9, 10);
  }

  @Test
  public void testGetClockwisePositionsAroundHex_Vertical_1_evenColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);
    assertPositions(eng.getNeighbourPositions(10, 11, 1), 11, 10, 11, 11, 11, 12, 10, 12, 9, 11, 10, 10);
  }

  @Test
  public void testGetPackedClockwisePositionsAroundHex_Vertical_1_evenColumn() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);
    assertPositions(eng.getPackedNeighbourPositions(null,10, 11, 1), 11, 10, 11, 11, 11, 12, 10, 12, 9, 11, 10, 10);
  }

  @Test
  public void testGetClockwisePositionsAroundHex_Horizontal_4() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertPositions(eng.getNeighbourPositions(5, 6, 4), 5, 2, 6, 3, 7, 3, 8, 4, 9, 4, 9, 5, 9, 6, 9, 7, 9, 8, 8, 9, 7, 9, 6, 10, 5, 10, 4, 10, 3, 9, 2, 9, 1, 8, 1, 7, 1, 6, 1, 5, 1, 4, 2, 4, 3, 3, 4, 3);
  }

  @Test
  public void testGetPackedClockwisePositionsAroundHex_Horizontal_4() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertPositions(eng.getPackedNeighbourPositions(null,5, 6, 4), 5, 2, 6, 3, 7, 3, 8, 4, 9, 4, 9, 5, 9, 6, 9, 7, 9, 8, 8, 9, 7, 9, 6, 10, 5, 10, 4, 10, 3, 9, 2, 9, 1, 8, 1, 7, 1, 6, 1, 5, 1, 4, 2, 4, 3, 3, 4, 3);
  }

  @Test
  public void testGetClockwisePositionsAroundHex_Vertical_4() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);
    assertPositions(eng.getNeighbourPositions(5, 6, 4), 7, 2, 7, 3, 8, 4, 8, 5, 9, 6, 8, 7, 8, 8, 7, 9, 7, 10,
            6, 10, 5, 10, 4, 10, 3, 10,
            2, 9, 2, 8, 1, 7, 1, 6,
            1, 5, 2, 4, 2, 3, 3, 2, 4, 2, 5, 2, 6, 2);
  }

  @Test
  public void testGetClockwisePositionsAroundHex_Vertical_3() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);
    assertPositions(eng.getNeighbourPositions(5, 6, 3), 6, 3, 7, 4, 7, 5, 8, 6, 7, 7, 7, 8, 6, 9, 5, 9, 4, 9, 3, 9,
            3, 8, 2, 7, 2, 6, 2, 5, 3, 4, 3, 3, 4, 3, 5, 3);
  }

  @Test
  public void testNearestNeighboursPosition_Horizontal() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);

    assertNearestNeighbours(eng, new HexPosition(5, 6), 5, 5, 6, 6, 6, 7, 5, 7, 4, 7, 4, 6);
    assertNearestNeighbours(eng, new HexPosition(5, 5), 5, 4, 6, 5, 6, 6, 5, 6, 4, 6, 4, 5);
    assertNearestNeighbours(eng, new HexPosition(2, 2), 2, 1, 3, 1, 3, 2, 2, 3, 1, 2, 1, 1);
    assertNearestNeighbours(eng, new HexPosition(8, 7), 8, 6, 9, 6, 9, 7, 8, 8, 7, 7, 7, 6);
  }

  @Test
  public void testNearestNeighboursPosition_Vertical() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);

    assertNearestNeighbours(eng, new HexPosition(5, 6), 5, 5, 6, 6, 5, 7, 4, 7, 4, 6, 4, 5);
    assertNearestNeighbours(eng, new HexPosition(5, 5), 6, 4, 6, 5, 6, 6, 5, 6, 4, 5, 5, 4);
    assertNearestNeighbours(eng, new HexPosition(2, 2), 2, 1, 3, 2, 2, 3, 1, 3, 1, 2, 1, 1);
    assertNearestNeighbours(eng, new HexPosition(8, 7), 9, 6, 9, 7, 9, 8, 8, 8, 7, 7, 8, 6);
    assertNearestNeighbours(eng, new HexPosition(9, 17), 10, 16, 10, 17, 10, 18, 9, 18, 8, 17, 9, 16);
    assertNearestNeighbours(eng, new HexPosition(3, 18), 3, 17, 4, 18, 3, 19, 2, 19, 2, 18, 2, 17);
    assertNearestNeighbours(eng, new HexPosition(8, 8), 8, 7, 9, 8, 8, 9, 7, 9, 7, 8, 7, 7);
  }

  @Test
  public void testCalculateNumberOfHex_Vertical() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_VERTICAL);
    assertEquals(new WidthHeightPair(0, 0), eng.calculateHexesForRectangle(15, 15));
    assertEquals(new WidthHeightPair(1, 1), eng.calculateHexesForRectangle(21, 21));
    assertEquals(new WidthHeightPair(10, 7), eng.calculateHexesForRectangle(215, 123));
  }

  @Test
  public void testCalculateNumberOfHex_Horizontal() {
    final HexEngine<Object> eng = new HexEngine<Object>(20, 20, HexEngine.ORIENTATION_HORIZONTAL);
    assertEquals(new WidthHeightPair(0, 0), eng.calculateHexesForRectangle(15, 15));
    assertEquals(new WidthHeightPair(1, 1), eng.calculateHexesForRectangle(21, 21));
    assertEquals(new WidthHeightPair(13, 5), eng.calculateHexesForRectangle(215, 123));
  }
}
