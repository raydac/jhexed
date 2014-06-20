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
import com.kitfox.svg.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import org.apache.commons.io.IOUtils;

public class SVGImage {

  private final SVGDiagram svgDiagram;
  private boolean antialias = true;
  private final byte [] savedImageData;

  private static byte [] readFullInputStream(final InputStream in) throws IOException {
    final byte [] buffer = new byte[16384];
    
    final ByteArrayOutputStream result = new ByteArrayOutputStream(16384);
    
    while(true){
      final int read = in.read(buffer);
      if (read<0) break;
      result.write(buffer, 0, read);
    }
    
    return result.toByteArray();
  }
  
  private static SVGDiagram loadDiagramFromStream(final InputStream in) throws IOException {    
    final SVGUniverse u = new SVGUniverse();
    final URI r = u.loadSVG(in, "igormaznitsa.com", true);
    final SVGDiagram result = u.getDiagram(r);
    result.setIgnoringClipHeuristic(true);
    return result;
  }

  public SVGImage(final File file) throws IOException {
    final FileInputStream inStream = new FileInputStream(file);
    try {
      this.savedImageData = readFullInputStream(inStream);
      this.svgDiagram = loadDiagramFromStream(new ByteArrayInputStream(this.savedImageData));
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

  public SVGImage(final InputStream in, final boolean zipped) throws IOException {
    final DataInputStream din = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
    if (zipped){
      final byte [] packedImageData = new byte[din.readInt()];
      IOUtils.readFully(din, packedImageData);
      this.savedImageData = Utils.unpackArray(packedImageData);
    }else{
      this.savedImageData = readFullInputStream(din);
    }
    this.antialias = din.readBoolean();

    this.svgDiagram = loadDiagramFromStream(new ByteArrayInputStream(this.savedImageData));
  }
  
  public void write(final OutputStream out, final boolean zipped) throws IOException {
    final DataOutputStream dout = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out);
    if (zipped){
      final byte [] packedImage = Utils.packByteArray(this.savedImageData);
      
      dout.writeInt(packedImage.length);
      dout.write(packedImage);
    }else{
      dout.write(this.savedImageData);
    }
    dout.writeBoolean(this.antialias);
  }

  public float getSVGWidth() {
    return this.svgDiagram.getWidth();
  }

  public float getSVGHeight() {
    return this.svgDiagram.getHeight();
  }

  public void setSpeedOptimization(final boolean flag) {
    this.svgDiagram.setIgnoringClipHeuristic(flag);
  }

  public boolean isSpeedOptimization() {
    return this.svgDiagram.ignoringClipHeuristic();
  }

  public void setAntialiased(final boolean flag) {
    this.antialias = flag;
  }

  public boolean isAntialiased() {
    return this.antialias;
  }

  public byte [] getImageData(){
    return this.savedImageData;
  }
  
  public void render(final Graphics2D g) throws IOException {
    final Object textHint = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
    final Object gfxHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    final Object alphaHint = g.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION);

    setAntialias(g);
    try {
      this.svgDiagram.render(g);
    }
    catch (SVGException ex) {
      throw new IOException("Detected SVG exception", ex);
    }
    finally {
      if (gfxHint != null) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, gfxHint);
      }
      if (textHint != null) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textHint);
      }
      if (alphaHint != null) {
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, alphaHint);
      }
    }
  }

  public BufferedImage rasterize(final int imageType) throws IOException {
    final int svgWidth = Math.round(this.svgDiagram.getWidth());
    final int svgHeight = Math.round(this.svgDiagram.getHeight());

    final BufferedImage result = new BufferedImage(svgWidth, svgHeight, imageType);
    final Graphics2D g = result.createGraphics();
    setAntialias(g);
    try {
      this.svgDiagram.render(g);
    }
    catch (SVGException ex) {
      throw new IOException("Detected SVG exception", ex);
    }
    g.dispose();
    return result;
  }

  public BufferedImage rasterize(final float scaleFactor, final int imageType) throws IOException {
    final int svgWidth = Math.round(this.svgDiagram.getWidth());
    final int svgHeight = Math.round(this.svgDiagram.getHeight());

    return this.rasterize(Math.round(svgWidth * scaleFactor), Math.round(svgHeight * scaleFactor), imageType);
  }

  public BufferedImage rasterize(final int width, final int height, final int imageType) throws IOException {
    final int svgWidth = Math.round(this.svgDiagram.getWidth());
    final int svgHeight = Math.round(this.svgDiagram.getHeight());

    final BufferedImage result = new BufferedImage(width, height, imageType);
    final Graphics2D g = result.createGraphics();
    final float xfactor = (float) width / (float) svgWidth;
    final float yfactor = (float) height / (float) svgHeight;
    g.setTransform(AffineTransform.getScaleInstance(xfactor, yfactor));
    setAntialias(g);
    try {
      this.svgDiagram.render(g);
    }
    catch (SVGException ex) {
      throw new IOException("Detected SVG exception", ex);
    }

    g.dispose();
    return result;
  }

  private void setAntialias(final Graphics2D g) {
    if (this.antialias) {
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
}
