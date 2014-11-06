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
package com.igormaznitsa.jhexed.swing.editor.ui.exporters;

import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.engine.HexEngineModel;
import com.igormaznitsa.jhexed.engine.misc.HexPoint2D;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.engine.renders.HexEngineRender;
import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.renders.svg.SVGImage;
import com.igormaznitsa.jhexed.swing.editor.Log;
import com.igormaznitsa.jhexed.swing.editor.model.DocumentCellComments;
import com.igormaznitsa.jhexed.swing.editor.ui.Utils;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import com.igormaznitsa.jhexed.values.*;
import java.awt.Color;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class SVGImageExporter implements Exporter, HexEngineModel<SVGImageExporter> {

  private enum Mode {

    LAYERS,
    BORDER,
    COMMENTS;
  }

  private static final String HEX_SHAPE = "b";
  private static final String HEX_MASK = "m";
  private static final String BACKGROUND = "backImage";
  private static final Charset UTF8 = Charset.forName("UTF-8");

  private final DocumentOptions docOptions;
  private final SelectLayersExportData exportData;
  private final DocumentCellComments cellComments;

  private int exportingLayerCounter;
  private Mode currentMode;

  private static final DecimalFormat formatter = new DecimalFormat("0.000");

  public SVGImageExporter(final DocumentOptions docOptions, final SelectLayersExportData exportData, final DocumentCellComments cellComments) {
    this.docOptions = docOptions;
    this.exportData = exportData;
    this.cellComments = cellComments;
  }

  private static final String num2str(final float value) {
    return formatter.format(value);
  }

  private void addHexMask(final StringBuilder buffer, final HexPoint2D[] points) {
    buffer.append("<clipPath id=\"").append(HEX_MASK).append("\">").append(startPolygonWithCoords(null, points)).append("/></clipPath>");
  }

  private static void addSvgElement(final StringBuilder buffer, final int width, final int height) {
    buffer.append("<svg xmlns=\"http://www.w3.org/2000/svg\"  xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"").append(width).append("\" height=\"").append(height).append("\">\n");
  }

  private static void addEndSvgElement(final StringBuilder buffer) {
    buffer.append("</svg>\n");
  }

  private void drawCell(final StringBuilder buffer, final float x, final float y, final float cellWidth, final float cellHeight, final int col, final int row) {
    switch (currentMode) {
      case LAYERS: {
        final HexFieldLayer thelayer = this.exportData.getLayers().get(this.exportingLayerCounter).getLayer();
        final HexFieldValue thevalue = thelayer.getHexValueAtPos(col, row);
        if (thevalue != null) {
          final String id = vid(this.exportingLayerCounter, thevalue.getIndex());
          buffer.append("<g transform=\"translate(").append(num2str(x)).append(',').append(num2str(y)).append(")\" clip-path=\"url(#").append(HEX_MASK).append(")\">")
                  .append("<use xlink:href=\"#").append(id).append("\"/></g>\n");
        }
      }
      break;
      case BORDER: {
        buffer.append("<use xlink:href=\"#").append(HEX_SHAPE).append("\" x=\"").append(num2str(x)).append("\" y=\"").append(num2str(y)).append("\"/>\n");
      }
      break;
      case COMMENTS: {
        final String text = this.cellComments.getForHex(new HexPosition(col, row));
        if (text != null) {
          final float thex = x+(cellWidth/2.0f);
          buffer.append("<text x=\"").append(num2str(thex)).append("\" y=\"").append(num2str(y-5.0f)).append("\" style=\"text-anchor:middle;font-size:12;font-weight:bold;font-family:Arial;fill:white;stroke:black;stroke-width:0.5\">")
                  .append(StringEscapeUtils.escapeXml10(text))
                  .append("</text>\n");
        }
      }
      break;
    }
  }

  private String startPolygonWithCoords(final String id, final HexPoint2D[] points) {
    final StringBuilder buffer = new StringBuilder();
    buffer.append("<polygon");
    if (id != null) {
      buffer.append(" id=\"").append(id).append('\"');
    }
    buffer.append(" points=\"");
    boolean nofirst = false;
    for (final HexPoint2D p : points) {
      if (nofirst) {
        buffer.append(' ');
      }
      else {
        nofirst = true;
      }
      buffer.append(num2str(p.getX())).append(',').append(num2str(p.getY()));
    }
    buffer.append('\"');
    return buffer.toString();
  }

  private void defineGridCell(final StringBuilder buffer, final HexPoint2D[] hexPoints) {
    buffer.append(startPolygonWithCoords(HEX_SHAPE, hexPoints)).append(" style=\"stroke:").append(Utils.color2html(this.docOptions.getColor(), false))
            .append(";stroke-width:").append(num2str(this.docOptions.getLineWidth()))
            .append(";fill:none;\"/>\n");
  }

  private static String alphaToSVG(final Color color) {
    final int a = color.getAlpha();
    if (a == 0) {
      return "0.0";
    }
    if (a >= 255) {
      return "1.0";
    }
    return num2str((float) a / (float) 255);
  }

  private static String vid(final int layerIndex, final int valueIndex) {
    return "l" + layerIndex + '_' + valueIndex;
  }

  private static String svgToImageTag(final String id, final SVGImage svgImage, final float cellWidth, final float cellHeight, final boolean scale) {
    final String baseEncoded = new Base64().encodeAsString(svgImage.getImageData());
    final StringBuilder result = new StringBuilder("<image id=\"").append(id).append('\"');

    final String xscale = scale ? num2str(cellWidth / svgImage.getSVGWidth()) : null;
    final String yscale = scale ? num2str(cellHeight / svgImage.getSVGHeight()) : null;

    result.append(" xlink:href=\"data:image/svg+xml;base64,").append(baseEncoded).append('\"');
    result.append(" width=\"").append(num2str(svgImage.getSVGWidth())).append("\" height=\"").append(svgImage.getSVGHeight()).append('\"');
    if (scale) {
      result.append(" transform=\"scale(").append(xscale).append(',').append(yscale).append(")\"");
    }

    result.append("/>");
    return result.toString();
  }

  private void defineLayerValues(final StringBuilder buffer, final HexEngine<?> engine) {
    int layerindex = 0;

    final HexPoint2D[] shape = engine.getHexScaledPoints();

    final float cellwidth = engine.getScaledCellWidth();
    final float cellheight = engine.getScaledCellHeight();

    for (final LayerExportRecord rec : this.exportData.getLayers()) {
      if (rec.isAllowed()) {
        final HexFieldLayer layer = rec.getLayer();

        for (int i = 0; i < layer.getHexValuesNumber(); i++) {
          final HexFieldValue v = layer.getHexValueForIndex(i);
          if (v == null) {
            continue;
          }
          final String id = vid(layerindex, v.getIndex());
          if (v instanceof NullHexValue) {
            continue;
          }
          else {
            if (v instanceof HexColorValue) {
              final HexColorValue thevalue = (HexColorValue) v;
              buffer.append(startPolygonWithCoords(id, shape)).append(" style=\"stroke:none;fill:").append(Utils.color2html(thevalue.getColor(), false)).append(";fill-opacity:").append(alphaToSVG(thevalue.getColor())).append("\"/>\n");
            }
            else if (v instanceof HexSVGImageValue) {
              final HexSVGImageValue thevalue = (HexSVGImageValue) v;
              buffer.append(svgToImageTag(id, thevalue.getImage(), cellwidth, cellheight, true)).append('\n');
            }
          }
        }
      }
      layerindex++;
    }
  }

  public byte[] generateImage() throws IOException {
    final StringBuilder buffer = new StringBuilder(128000);
    buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
    buffer.append("<!--").append(StringEscapeUtils.escapeXml10("Generated by the JHexed Swing Editor (https://code.google.com/p/jhexed/)")).append("-->\n");
    
    final HexEngine<SVGImageExporter> engine = new HexEngine<SVGImageExporter>(64, 64, this.docOptions.getHexOrientation());
    engine.setModel(this);

    if (this.docOptions.getImage() != null) {
      final float imageWidth = this.docOptions.getImage().getSVGWidth();
      final float imageHeight = this.docOptions.getImage().getSVGHeight();
      engine.setScale(imageWidth / engine.getVisibleSize().getWidth(), imageHeight / engine.getVisibleSize().getHeight());
    }

    addSvgElement(buffer, engine.getVisibleSize().getWidthAsInt(), engine.getVisibleSize().getHeightAsInt());

    buffer.append("<defs>");
    addHexMask(buffer, engine.getHexScaledPoints());
    if (this.exportData.isExportHexBorders()) {
      defineGridCell(buffer, engine.getHexScaledPoints());
    }
    
    defineLayerValues(buffer, engine);
    buffer.append("</defs>\n");

    if (this.exportData.isBackgroundImageExport() && this.docOptions.getImage() != null) {
      buffer.append(svgToImageTag(BACKGROUND, this.docOptions.getImage(), exportingLayerCounter, exportingLayerCounter, false)).append('\n');
    }

    engine.setRenderer(new HexEngineRender<SVGImageExporter>() {

      @Override
      public void renderHexCell(final HexEngine<SVGImageExporter> engine, final SVGImageExporter gfx, final float x, final float y, final int col, final int row) {
        drawCell(buffer, x, y, engine.getScaledCellWidth(),engine.getScaledCellHeight(),col, row);
      }

      @Override
      public void attachedToEngine(final HexEngine<?> engine) {
      }

      @Override
      public void detachedFromEngine(final HexEngine<?> engine) {
      }

    });

    buffer.append("<g id=\"LAYERS\">\n");
    currentMode = Mode.LAYERS;
    for (int i = exportData.getLayers().size()-1; i>=0; i--) {
      exportingLayerCounter = i;
      final LayerExportRecord rec = exportData.getLayers().get(i);
      if (rec.isAllowed()) {
        buffer.append("<g id=\"layer").append(exportingLayerCounter).append("\">\n");
        engine.draw(this);
        buffer.append("</g>\n");
      }
    }
    buffer.append("</g>\n");

    if (exportData.isExportHexBorders()) {
      currentMode = Mode.BORDER;
      buffer.append("<g id=\"HEXES\">\n");
      engine.draw(this);
      buffer.append("</g>\n");
    }

    if (exportData.isCellCommentariesExport()) {
      currentMode = Mode.COMMENTS;
      buffer.append("<g id=\"COMMENTS\">\n");
      engine.draw(this);
      buffer.append("</g>\n");
    }

    addEndSvgElement(buffer);

    return buffer.toString().getBytes(UTF8);
  }

  @Override
  public void export(final File file) throws IOException {
    final byte[] img = generateImage();
    if (img != null && !Thread.currentThread().isInterrupted()) {
      FileUtils.writeByteArrayToFile(file, img);
    }
    else {
      Log.warn("SVG export thread has been interrupted");
    }
  }

  @Override
  public int getColumnNumber() {
    return this.docOptions.getColumns();
  }

  @Override
  public int getRowNumber() {
    return this.docOptions.getRows();
  }

  @Override
  public SVGImageExporter getValueAt(final int col, final int row) {
    return this;
  }

  @Override
  public SVGImageExporter getValueAt(final HexPosition pos) {
    return this;
  }

  @Override
  public void setValueAt(int col, int row, SVGImageExporter value) {
  }

  @Override
  public void setValueAt(HexPosition pos, SVGImageExporter value) {
  }

  @Override
  public boolean isPositionValid(int col, int row) {
    return true;
  }

  @Override
  public boolean isPositionValid(HexPosition pos) {
    return true;
  }

  @Override
  public void attachedToEngine(HexEngine<?> engine) {
  }

  @Override
  public void detachedFromEngine(HexEngine<?> engine) {
  }
}
