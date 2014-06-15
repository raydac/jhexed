package com.igormaznitsa.jhexed.values;

import com.igormaznitsa.jhexed.renders.svg.SVGImage;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import org.apache.commons.io.IOUtils;

public class HexSVGImageValue extends HexFieldValue {

  private SVGImage image;
  private Image prerasterized;

  private HexSVGImageValue(final HexSVGImageValue val) {
    super(val);
    this.image = val.image;
    this.prerasterized = val.prerasterized;
  }

  public HexSVGImageValue(final String name, final String comment, final SVGImage image, final int index) {
    super(name, comment, index);
    this.image = image;
  }

  public HexSVGImageValue(final InputStream in) throws IOException {
    super(in);
    final DataInputStream din = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
    final byte[] buffer = new byte[din.readInt()];
    IOUtils.readFully(in, buffer);
    this.image = new SVGImage(new ByteArrayInputStream(buffer),true);
  }

  @Override
  public HexFieldValue cloneValue() {
    return new HexSVGImageValue(this);
  }

  public SVGImage getImage() {
    return this.image;
  }

  public void setImage(final SVGImage value) {
    this.image = value;
  }

  @Override
  public void load(final HexFieldValue val) {
    super.load(val);
    this.image = ((HexSVGImageValue) val).getImage();
  }    

  @Override
  public void write(final OutputStream out) throws IOException {
    out.write(TYPE_SVGIMAGE);
    super.write(out);
    
    final ByteArrayOutputStream imageDataBuffer = new ByteArrayOutputStream(4096);
    this.image.write(imageDataBuffer, true);
    final byte [] imageData = imageDataBuffer.toByteArray();
    
    final DataOutputStream dout = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out);
    dout.writeInt(imageData.length);
    dout.write(imageData);
  }

  @Override
  public BufferedImage makeIcon(final int width, final int height, final Path2D shape) {
    try {
      final BufferedImage img = this.image.rasterize(width, height, BufferedImage.TYPE_INT_ARGB);
      if (shape == null) {
        return img;
      }
      else {
        final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(makeTransformedPathForSize(width, height, shape));
        g.drawImage(img, 0,0, null);
        g.dispose();
        return result;
      }

    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  @Override
  public void prerasterizeIcon(final Shape shape) {
    try {
      final Rectangle r = shape.getBounds();
      final BufferedImage img = this.image.rasterize(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
      final BufferedImage result = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
      final Graphics2D g = result.createGraphics();
      g.setClip(shape);
      g.drawImage(img, 0, 0, null);
      g.dispose();
      this.prerasterized = result;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public Image getPrerasterized() {
    return this.prerasterized;
  }

  
}
