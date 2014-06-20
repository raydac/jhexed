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

public class NullHexValue extends HexFieldValue {

  public NullHexValue() {
    super("Empty value", "The Default value", 0);
  }

  public NullHexValue(final InputStream in) throws IOException {
    this();
    throw new UnsupportedOperationException("Must not be called directly");
  }

  @Override
  public HexFieldValue cloneValue() {
    return this;
  }

  @Override
  public void write(OutputStream out) throws IOException {
    out.write(TYPE_NULL);
  }

  
  
  @Override
    public BufferedImage makeIcon(final int width, final int height, final Path2D shape, final boolean allowAlpha) {
    final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D g = img.createGraphics();
    g.setColor(Color.BLACK);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    
    if (shape != null) {
      g.setClip(makeTransformedPathForSize(width, height, shape));
    }
      g.fillRect(0, 0, width, height);
      final int w = width - 1;
      final int h = height - 1;
      g.setColor(Color.red.brighter());
      g.drawLine(0, 0, w, h);
      g.drawLine(0, h, w, 0);

    g.dispose();

    return img;
  }

  @Override
  public void prerasterizeIcon(final Shape shape) {
  }
}
