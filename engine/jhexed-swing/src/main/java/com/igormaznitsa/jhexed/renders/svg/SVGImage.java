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
package com.igormaznitsa.jhexed.renders.svg;

import com.igormaznitsa.jhexed.renders.Utils;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import org.apache.batik.bridge.*;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.svg.*;

public class SVGImage {

  private final GraphicsNode svgGraphicsNode;
  private boolean quality = true;
  private final byte[] originalNonParsedImageData;
  private final Dimension2D documentSize = new Dimension();

  private static byte[] readFullInputStream(final InputStream in) throws IOException {
    final byte[] buffer = new byte[16384];

    final ByteArrayOutputStream result = new ByteArrayOutputStream(16384);

    while (true) {
      final int read = in.read(buffer);
      if (read < 0) {
        break;
      }
      result.write(buffer, 0, read);
    }

    return result.toByteArray();
  }

  private static GraphicsNode loadDiagramFromStream(final InputStream in, final Dimension2D docSize) throws IOException {
    final String parser = XMLResourceDescriptor.getXMLParserClassName();
    final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
    final SVGDocument doc = factory.createSVGDocument("http://www.igormaznitsa.com/jhexed/svg", in);

    String strDocWidth = doc.getRootElement().getAttribute("width");
    String strDocHeight = doc.getRootElement().getAttribute("height");

    final GVTBuilder bldr = new GVTBuilder();

    final UserAgent userAgent = new UserAgentAdapter();
    final DocumentLoader loader = new DocumentLoader(userAgent);
    final BridgeContext ctx = new BridgeContext(userAgent, loader);

    final GraphicsNode result = bldr.build(ctx, doc);

    strDocWidth = strDocWidth.isEmpty() ? Double.toString(result.getSensitiveBounds().getWidth()) : strDocWidth;
    strDocHeight = strDocHeight.isEmpty() ? Double.toString(result.getSensitiveBounds().getHeight()) : strDocHeight;

    docSize.setSize(Double.parseDouble(strDocWidth.trim()), Double.parseDouble(strDocHeight.trim()));

    return result;
  }

  public SVGImage(final File file) throws IOException {
    final FileInputStream inStream = new FileInputStream(file);
    try {
      this.originalNonParsedImageData = readFullInputStream(inStream);
      this.svgGraphicsNode = loadDiagramFromStream(new ByteArrayInputStream(this.originalNonParsedImageData), this.documentSize);
    }
    finally {
      try {
        inStream.close();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public SVGImage(final InputStream in, final boolean packed) throws IOException {
    final DataInputStream din = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
    if (packed) {
      final byte[] packedImageData = new byte[din.readInt()];
      IOUtils.readFully(din, packedImageData);
      this.originalNonParsedImageData = Utils.unpackArray(packedImageData);
    }
    else {
      this.originalNonParsedImageData = readFullInputStream(din);
    }
    this.quality = din.readBoolean();

    this.svgGraphicsNode = loadDiagramFromStream(new ByteArrayInputStream(this.originalNonParsedImageData), this.documentSize);
  }

  public void write(final OutputStream out, final boolean zipped) throws IOException {
    final DataOutputStream dout = out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);
    if (zipped) {
      final byte[] packedImage = Utils.packByteArray(this.originalNonParsedImageData);

      dout.writeInt(packedImage.length);
      dout.write(packedImage);
    }
    else {
      dout.write(this.originalNonParsedImageData);
    }
    dout.writeBoolean(this.quality);
  }

  public float getSVGWidth() {
    return (float) this.documentSize.getWidth();
  }

  public float getSVGHeight() {
    return (float) this.documentSize.getHeight();
  }

  public void setQuality(final boolean flag) {
    this.quality = flag;
  }

  public boolean isQuality() {
    return this.quality;
  }

  public byte[] getImageData() {
    return this.originalNonParsedImageData;
  }

  public void render(final Graphics2D g) throws IOException {
    final Object antialiasText = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
    final Object antialiasDraw = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    final Object antialiasAlpha = g.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION);
    try {
      processAntialias(this.quality, g);
      this.svgGraphicsNode.primitivePaint(g);
    }
    finally {
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antialiasText == null ? RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT : antialiasText);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasDraw == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : antialiasDraw);
      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, antialiasAlpha == null ? RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT : antialiasAlpha);
    }
  }

  public BufferedImage rasterize(final int imageType) throws IOException {
    final int svgWidth = Math.round(getSVGWidth());
    final int svgHeight = Math.round(getSVGHeight());

    final BufferedImage result = new BufferedImage(svgWidth, svgHeight, imageType);
    final Graphics2D g = result.createGraphics();

    processAntialias(this.quality, g);
    this.svgGraphicsNode.primitivePaint(g);

    g.dispose();
    return result;
  }

  public BufferedImage rasterize(final float scaleFactor, final int imageType) throws IOException {
    final int svgWidth = Math.round(getSVGWidth());
    final int svgHeight = Math.round(getSVGHeight());

    return this.rasterize(Math.round(svgWidth * scaleFactor), Math.round(svgHeight * scaleFactor), imageType);
  }

  public BufferedImage rasterize(final int width, final int height, final int imageType) throws IOException {
    final BufferedImage result = new BufferedImage(width, height, imageType);
    final float xfactor = (float) width / getSVGWidth();
    final float yfactor = (float) height / getSVGHeight();

    final Graphics2D g = result.createGraphics();

    processAntialias(this.quality, g);
    g.setTransform(AffineTransform.getScaleInstance(xfactor, yfactor));
    this.svgGraphicsNode.primitivePaint(g);

    g.dispose();
    return result;
  }

  private static void processAntialias(final boolean flag, final Graphics2D g) {
    if (flag) {
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }
    else {
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    }
  }
  
  public GraphicsNode getSVGGraphicsNode(){
    return this.svgGraphicsNode;
  }
}
