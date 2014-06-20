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
package com.igormaznitsa.jhexed.values;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;

public abstract class HexFieldValue {

  protected static final int TYPE_NULL = 0;
  protected static final int TYPE_COLOR = 1;
  protected static final int TYPE_SVGIMAGE = 2;
  
  protected String name;
  protected String comment;
  protected transient int index;
  
  public static final HexFieldValue NULL = new NullHexValue();
  
  public static HexFieldValue readValue(final InputStream in) throws IOException {
    final int index = in.read();
    if (index<0) throw new EOFException();
    switch(index){
      case TYPE_NULL : return NULL;
      case TYPE_COLOR : return new HexColorValue(in);
      case TYPE_SVGIMAGE : return new HexSVGImageValue(in);
      default: throw new IOException("unsupported value index "+index);
    }
  }
  
  public int getIndex(){
    return this.index;
  }
  
  public void setIndex(final int index){
    this.index = index;
  }
  
  public HexFieldValue(final HexFieldValue val){
    this(val.name, val.comment, val.index);
  }
  
  public HexFieldValue(final String name, final String comment, final int index){
    this.name = name == null ? "" : name;
    this.comment = comment == null ? "" : comment;
    this.index = index;
  }
  
  public HexFieldValue(final InputStream in) throws IOException {
    final DataInputStream din = in instanceof DataInputStream ? (DataInputStream)in : new DataInputStream(in);
    this.name = din.readUTF();
    this.comment = din.readUTF();
  }
  
  protected static Shape makeTransformedPathForSize(final int width, final int height, final Path2D shape){
    final Rectangle2D rect = shape.getBounds2D();
    final double coeffw = width/rect.getWidth();
    final double coeffh = height/rect.getHeight();
    return shape.createTransformedShape(AffineTransform.getScaleInstance(coeffw, coeffh));
  }
  
  public void write(final OutputStream out) throws IOException {
    final DataOutputStream dout = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out);
    dout.writeUTF(this.name);
    dout.writeUTF(this.comment);
  }
  
  public abstract void prerasterizeIcon(final Shape shape);
  
  public abstract BufferedImage makeIcon(final int width, final int height, final Path2D shape, boolean allowAlpha);
 
  public abstract HexFieldValue cloneValue();
  
  public String getName(){
    return this.name;
  }
  
  public void setName(final String name){
    this.name = name;
  }
  
  public String getComment(){
    return this.comment;
  }
  
  public void setComment(final String comment){
    this.comment = comment;
  }

  public Image getPrerasterized(){
    return null;
  }

  public void load(final HexFieldValue val) {
    this.name = val.getName();
    this.comment = val.getComment();
  }
}
