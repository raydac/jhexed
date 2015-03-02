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
package com.igormaznitsa.jhexed.renders.swing;

import com.igormaznitsa.jhexed.engine.*;
import com.igormaznitsa.jhexed.engine.renders.HexEngineRender;
import com.igormaznitsa.jhexed.renders.Utils;
import java.awt.*;
import java.awt.geom.Path2D;

public class ColorHexRender implements HexEngineRender<Graphics2D>, HexEngineListener {

  private Path2D hexPath;
  private final Stroke stroke = new BasicStroke(0.5f);

  private final Font textFont = new Font("arial", Font.PLAIN, 8);

  protected boolean antialias = true;

  public ColorHexRender() {
  }

  public void setAntialias(final boolean flag){
    this.antialias = flag;
  }
  
  public boolean isAntialias(){
    return this.antialias;
  }
  
  public void detachedFromEngine(final HexEngine<?> engine) {
    engine.removeHexLayerListener(this);
  }

  public static Font scaleFont(final String text, final Rectangle rect, final Graphics g, final Font font) {
    float fontSize = 20.0f;

    Font pfont = font.deriveFont(fontSize);
    final int width = g.getFontMetrics(pfont).stringWidth(text);
    fontSize = ((float) rect.width / (float) width) * fontSize;
    return pfont.deriveFont(fontSize);
  }

  @Override
  public void renderHexCell(final HexEngine<Graphics2D> engine, final Graphics2D g, final float x, final float y, int col, int row) {
    if (this.antialias) {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    else {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    final int xoff = Math.round(x);
    final int yoff = Math.round(y);

    g.translate(xoff, yoff);
    try {

      final Color fillColor = getFillColor(engine.getModel(), col, row);
      final Color borderColor = getBorderColor(engine.getModel(), col, row);

      drawUnderBorder(engine, g, col, row, borderColor, fillColor);
      
      final Path2D theShape = getHexPath();
      if (theShape != null) {

        if (fillColor != null) {
          g.setColor(fillColor);
          g.fill(theShape);
        }
        if (borderColor != null) {
          final Stroke theStroke = getStroke();
          if (theStroke != null) {
            g.setStroke(theStroke);
          }
          g.setColor(borderColor);
          g.draw(theShape);
        }
      }

      drawExtra(engine, g, col, row, borderColor, fillColor);
    }
    finally {
      g.translate(-xoff, -yoff);
    }
  }

  public void drawUnderBorder(final HexEngine<Graphics2D> engine, final Graphics2D g, final int col, final int row, final Color borderColor, final Color fillColor){
    
  }
  
  public void drawExtra(final HexEngine<Graphics2D> engine, final Graphics2D g, final int col, final int row, final Color borderColor, final Color fillColor) {
    final Color drawColor;

    if (fillColor == null) {
      drawColor = Color.gray;
    }
    else {
      drawColor = new Color((fillColor.getRGB() ^ 0xFFFFFF) & 0x00FFFFFF);
    }

    final String stringToDraw = Integer.toString(col) + ',' + Integer.toString(row);

    final Rectangle rect = this.hexPath.getBounds();
    final int centerX = rect.width / 2;
    final int centerY = rect.height / 2;

    rect.grow(-rect.height / 4, -rect.width / 4);
    g.setFont(scaleFont(stringToDraw, rect, g, this.textFont));
    g.setColor(drawColor);
    g.drawString(stringToDraw, centerX - rect.width / 2, centerY + g.getFontMetrics().getAscent() / 2);
  }

  public Path2D getHexPath() {
    return this.hexPath;
  }

  public Stroke getStroke() {
    return this.stroke;
  }

  public Color getBorderColor(final HexEngineModel<?> model, final int col, final int row) {
    return Color.BLACK;
  }

  public Color getFillColor(final HexEngineModel<?> model, final int col, final int row) {
    return Color.WHITE;
  }

  public void onEngineReconfigured(final HexEngine<?> engine) {
    this.hexPath = Utils.getHexShapeAsPath(engine, true);
  }

  public void onScaleFactorChanged(final HexEngine<?> engine, final float scaleX, final float scaleY) {
    onEngineReconfigured(engine);
  }

  public void detachedRenderer(HexEngine<Graphics2D> canvas) {
    canvas.removeHexLayerListener(this);
  }

  @SuppressWarnings("unchecked")
  public void attachedToEngine(final HexEngine<?> engine) {
    engine.addHexLayerListener(this);
    onEngineReconfigured(engine);
  }

  public void onRenderChanged(final HexEngine<?> source, final HexEngineRender<?> oldRenderer, final HexEngineRender<?> newRenderer) {
  }

  public void onModelChanged(final HexEngine<?> source, final HexEngineModel<?> oldModel, final HexEngineModel<?> newModel) {
  }

}
