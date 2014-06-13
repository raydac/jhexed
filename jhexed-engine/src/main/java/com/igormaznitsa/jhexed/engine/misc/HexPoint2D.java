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
 * Class describes a 2D point.
 * 
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public final class HexPoint2D {
  /**
   * The X coordinate.
   */
  private final float x;
  /**
   * The Y coordinate.
   */
  private final float y;
  
  /**
   * A Constructor.
   * @param x the X coordinate.
   * @param y the Y coordinate.
   */
  public HexPoint2D(final float x, final float y){
    this.x = x;
    this.y = y;
  }
  
  /**
   * A Constructor.
   * @param x a point which X coordinate should be used.
   * @param y the Y coordinate.
   */
  public HexPoint2D(final HexPoint2D x, final float y){
    this.x = x.getX();
    this.y = y;
  }
  
  /**
   * A Constructor.
   *
   * @param x the X coordinate.
   * @param y a point which Y coordinate should be used.
   */
  public HexPoint2D(final float x, final HexPoint2D y){
    this.x = x;
    this.y = y.getY();
  }
  
  /**
   * A Constructor.
   * @param x a point which X coordinate should be used.
   * @param y a point which Y coordinate should be used.
   */
  public HexPoint2D(final HexPoint2D x, final HexPoint2D y){
    this.x = x.getX();
    this.y = y.getY();
  }
  
  /**
   * A Constructor.
   * @param point a point which coordinates should be used.
   */
  public HexPoint2D(final HexPoint2D point){
    this.x = point.getX();
    this.y = point.getY();
  }
  
  /**
   * A Constructor.
   * @param point a point which coordinates should be used.
   * @param scaleX the scale factor for X coordinate.
   * @param scaleY the scale factor for Y coordinate.
   */
  public HexPoint2D(final HexPoint2D point, final float scaleX, final float scaleY){
    this.x = point.getX()*scaleX;
    this.y = point.getY()*scaleY;
  }
  
  /**
   * Get the X coordinate.
   * @return the X coordinate of the point.
   */
  public float getX(){
    return this.x;
  }

  /**
   * Get the Y coordinate.
   *
   * @return the Y coordinate of the point.
   */
  public float getY(){
    return this.y;
  }

  
  /**
   * Check position of a point relative to vector.
   * @param linePointA line point A, the start point of the vector.
   * @param linePointB line point B, the end point of the vector.
   * @param x point X
   * @param y point Y
   * @return 1 - on the left side, -1 - on the right side, 0 - on the line
   */
  public static int getPointPositionRelativelyVector(final HexPoint2D linePointA, final HexPoint2D linePointB, final float x, final float y) {
    final float f = (linePointB.getX()-linePointA.getX())*(y-linePointA.getY())-(linePointB.getY()-linePointA.getY())*(x-linePointA.getX());
    return Float.compare(f, 0.0f);
  }

  /**
   * Check position of a point relative to vector.
   *
   * @param linePointA line point A, the start point of the vector.
   * @param linePointB line point B, the end point of the vector.
   * @param p the point to be checked
   * @return 1 - on the left side, -1 - on the right side, 0 - on the line
   */
  public static int getPointPositionRelativelyVector(final HexPoint2D linePointA, final HexPoint2D linePointB, final HexPoint2D p) {
    return getPointPositionRelativelyVector(linePointA, linePointB, p.x, p.y);
  }
  
  /**
   * Check position of a point relative to vector. The Point is the start point of the vector.
   * @param linePointB line point B, the end point of the vector
   * @param x point X
   * @param y point Y
   * @return 1 - on the left side, -1 - on the right side, 0 - on the line
   */
  public int getPointPositionRelativelyVector(final HexPoint2D linePointB, final float x, final float y){
    return getPointPositionRelativelyVector(this, linePointB, y, y);
  }

  /**
   * Check position of a point relative to vector. The Point is the start point
   * of the vector.
   *
   * @param linePointB line point B, the end point of the vector
   * @param p the next point of the vector
   * @return 1 - on the left side, -1 - on the right side, 0 - on the line
   */
  public int getPointPositionRelativelyVector(final HexPoint2D linePointB, final HexPoint2D p) {
    return this.getPointPositionRelativelyVector(linePointB, p.x, p.y);
  }

  /**
   * Check that the point inside of a ABC triangle. The point is the A point.
   * @param b the B point of the triangle.
   * @param c the C point of the triangle.
   * @param p the point to be check.
   * @return true if the point in the triangle.
   */
  public boolean isPointInTriangle(final HexPoint2D b, final HexPoint2D c, final HexPoint2D p) {
    return isPointInTriangle(this, b, c, p.x, p.y);
  }

  /**
   * Check that the point inside of a ABC triangle. The point is the A point.
   *
   * @param b the B point of the triangle.
   * @param c the C point of the triangle.
   * @param x the X coordinate of the point to be check.
   * @param y the Y coordinate of the point to be check.
   * @return true if the point in the triangle.
   */
  public boolean isPointInTriangle(final HexPoint2D b, final HexPoint2D c, final float x, final float y) {
    return isPointInTriangle(this, b, c, x, y);
  }
  
  /**
   * Check that the point inside of a ABC triangle.
   *
   * @param a the A point of the triangle.
   * @param b the B point of the triangle.
   * @param c the C point of the triangle.
   * @param p the point to be check.
   * @return true if the point in the triangle.
   */
  public static boolean isPointInTriangle(final HexPoint2D a, final HexPoint2D b, final HexPoint2D c, final HexPoint2D p) {
    return isPointInTriangle(a, b, c, p.x, p.y);
  }
  
  /**
   * Check that the point inside of a ABC triangle.
   * 
   * @param a the A point of the triangle.
   * @param b the B point of the triangle.
   * @param c the C point of the triangle.
   * @param x the point X coordinate to be check.
   * @param y the point Y coordinate to be check.
   * @return true if the point in the triangle.
   */
  public static boolean isPointInTriangle(final HexPoint2D a, final HexPoint2D b, final HexPoint2D c, final float x, final float y) {
    final int ab = getPointPositionRelativelyVector(a, b, x, y);
    final int bc = getPointPositionRelativelyVector(b, c, x, y);
    final int ca = getPointPositionRelativelyVector(c,a, x, y);

    return (ab == bc) && (bc == ca);
  }

  /**
   * Check that a vector AB intersects a rectangle. The point is the A for the vector.
   * @param pointB the point B of the vector.
   * @param rect the rectangle.
   * @param offsetX the offset X of point coordinates.
   * @param offsetY the offset Y of point coordinates.
   * @return true if the vector intersects the rectangle, false otherwise.
   */
  public boolean intersectsRectangle(final HexPoint2D pointB, final HexRect2D rect, final float offsetX, final float offsetY){
    return rect.isLineIntersects(this.x+offsetX, this.y+offsetY, pointB.x+offsetX, pointB.y+offsetY);
  }
  
  @Override
  public String toString(){
    return "HexPoint2D ("+this.x+','+this.y+')';
  }
}
