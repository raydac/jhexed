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
