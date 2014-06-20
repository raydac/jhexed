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
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;

public class HexColorValue extends HexFieldValue {

  private Color color;
  private Image prerasterizedImage;

  private HexColorValue(final HexColorValue value){
    super(value);
    this.color = value.color;
    this.prerasterizedImage = value.prerasterizedImage;
  }
  
  public HexColorValue(final String name, final String comment, final Color color, final int index) {
    super(name, comment, index);
    this.color = color;
  }

  public HexColorValue(final InputStream in) throws IOException {
    super(in);
    final int r = in.read();
    final int g = in.read();
    final int b = in.read();
    final int a = in.read();
    if (r < 0 || g < 0 || b < 0 || a < 0) {
      throw new EOFException("Can't read color");
    }

    this.color = new Color(r, g, b, a);
  }

  @Override
  public HexFieldValue cloneValue() {
    return new HexColorValue(this);
  }

  @Override
  public void write(final OutputStream out) throws IOException {
    out.write(TYPE_COLOR);
    super.write(out);
    out.write(color.getRed());
    out.write(color.getGreen());
    out.write(color.getBlue());
    out.write(color.getAlpha());
  }

  public Color getColor() {
    return this.color;
  }

  public void setColor(final Color color) {
    this.color = color;
  }

  @Override
  public BufferedImage makeIcon(final int width, final int height, final Path2D iconShape, final boolean allowAlpha) {
    final BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = result.createGraphics();
    
    g.setStroke(new BasicStroke(0.05f));
    
    final Color c;
    if (allowAlpha){
      c = new Color(this.color.getRed(),this.color.getGreen(),this.color.getBlue(),this.color.getAlpha());
    }else{
      c = new Color(this.color.getRGB());
    }
    
    if (iconShape!=null){
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g.setColor(c);
      final Shape path = makeTransformedPathForSize(width, height, iconShape);
      g.fill(path);
    }else{
      g.setColor(c);
      g.fillRect(0, 0, width, height);
    }
    g.dispose();
    return result;
  }

  @Override
  public void load(final HexFieldValue val) {
    super.load(val); 
    this.color = ((HexColorValue)val).getColor();
  }

  @Override
  public Image getPrerasterized() {
    return this.prerasterizedImage;
  }

  @Override
  public void prerasterizeIcon(final Shape shape) {
    final Rectangle r = shape.getBounds();
    final BufferedImage prerasterized = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = prerasterized.createGraphics();
    g.setStroke(new BasicStroke(0.05f));
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setColor(this.color);
    g.fill(shape);
    g.dispose();
    this.prerasterizedImage = prerasterized;
  }
}
