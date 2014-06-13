package com.igormaznitsa.jhexed.engine;

import com.igormaznitsa.jhexed.engine.misc.HexPoint2D;
import org.junit.Test;
import static org.junit.Assert.*;

public class PointTest {
  
  @Test
  public void testGetPointPositionRelativelyVector_Vertical_LeftSide() {
    assertEquals(1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(0,50), -10, 25));
  }
  
  @Test
  public void testGetPointPositionRelativelyVector_Horizontal_LeftSide() {
    assertEquals(1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(50,0), 10, 10));
  }
  
  @Test
  public void testGetPointPositionRelativelyVector_VerticalNegative_LeftSide() {
    assertEquals(1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(0,-50), 10, -10));
  }
  
  @Test
  public void testGetPointPositionRelativelyVector_HorizontalNegative_LeftSide() {
    assertEquals(1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(-50,0), -10, -10));
  }
  
  @Test
  public void testGetPointPositionRelativelyVector_Vertical_RightSide() {
    assertEquals(-1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(0,50), 10, 25));
  }
  
  @Test
  public void testGetPointPositionRelativelyVector_Horizontal_RightSide() {
    assertEquals(-1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(50,0), 10, -10));
  }
  
  @Test
  public void testGetPointPositionRelativelyVector_VerticalNegative_RightSide() {
    assertEquals(-1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(0,-50), -10, -10));
  }
  
  @Test
  public void testGetPointPositionRelativelyVector_HorizontalNegative_RightSide() {
    assertEquals(-1,HexPoint2D.getPointPositionRelativelyVector(new HexPoint2D(0,0), new HexPoint2D(-50,0), -10, 10));
  }
  
  
}
