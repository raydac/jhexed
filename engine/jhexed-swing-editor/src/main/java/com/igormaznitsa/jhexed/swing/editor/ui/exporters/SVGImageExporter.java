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

import com.igormaznitsa.jhexed.engine.*;
import com.igormaznitsa.jhexed.engine.misc.*;
import com.igormaznitsa.jhexed.engine.renders.HexEngineRender;
import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.renders.Utils;
import com.igormaznitsa.jhexed.swing.editor.model.DocumentCellComments;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import com.igormaznitsa.jhexed.values.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.*;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class SVGImageExporter implements Exporter {

  private final DocumentOptions docOptions;
  private final SelectLayersExportData exportData;
  private final DocumentCellComments cellComments;

  public SVGImageExporter(final DocumentOptions docOptions, final SelectLayersExportData exportData, final DocumentCellComments cellComments) {
    this.docOptions = docOptions;
    this.exportData = exportData;
    this.cellComments = cellComments;
  }

  public byte[] generateImage() throws IOException {
    final DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
    final String svgNS = "http://www.w3.org/2000/svg";
    final Document myFactory = impl.createDocument(svgNS, "svg", null);

    final SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(myFactory);
    ctx.setPrecision(3);
    final SVGGraphics2D g2d = new SVGGraphics2D(ctx, false);

    if (exportData.isBackgroundImageExport() && this.docOptions.getImage() != null) {
      this.docOptions.getImage().render(g2d);
    }

    final HexEngine<SVGGraphics2D> engine = new HexEngine<SVGGraphics2D>(48, 48, this.docOptions.getHexOrientation());

    final List<HexFieldLayer> reversedNormalizedStack = new ArrayList<HexFieldLayer>();
    for (int i = this.exportData.getLayers().size() - 1; i >= 0; i--) {
      final LayerExportRecord rec = this.exportData.getLayers().get(i);
      if (rec.isAllowed()) {
        reversedNormalizedStack.add(rec.getLayer());
      }
    }

    final HexFieldValue[] stackOfValues = new HexFieldValue[reversedNormalizedStack.size()];

    engine.setModel(new HexEngineModel<HexFieldValue[]>() {

      @Override
      public int getColumnNumber() {
        return docOptions.getColumns();
      }

      @Override
      public int getRowNumber() {
        return docOptions.getRows();
      }

      @Override
      public HexFieldValue[] getValueAt(final int col, final int row) {
        Arrays.fill(stackOfValues, null);

        for (int index = 0; index < reversedNormalizedStack.size(); index++) {
          stackOfValues[index] = reversedNormalizedStack.get(index).getHexValueAtPos(col, row);
        }
        return stackOfValues;
      }

      @Override
      public HexFieldValue[] getValueAt(final HexPosition pos) {
        return this.getValueAt(pos.getColumn(), pos.getRow());
      }

      @Override
      public void setValueAt(int col, int row, HexFieldValue[] value) {
      }

      @Override
      public void setValueAt(HexPosition pos, HexFieldValue[] value) {
      }

      @Override
      public boolean isPositionValid(final int col, final int row) {
        return col >= 0 && col < docOptions.getColumns() && row >= 0 && row < docOptions.getRows();
      }

      @Override
      public boolean isPositionValid(final HexPosition pos) {
        return this.isPositionValid(pos.getColumn(), pos.getRow());
      }

      @Override
      public void attachedToEngine(final HexEngine<?> engine) {
      }

      @Override
      public void detachedFromEngine(final HexEngine<?> engine) {
      }
    });

    final HexRect2D visibleSize = engine.getVisibleSize();
    if (this.docOptions.getImage() != null) {
      final float xcoeff = this.docOptions.getImage().getSVGWidth() / visibleSize.getWidth();
      final float ycoeff = this.docOptions.getImage().getSVGHeight() / visibleSize.getHeight();
      engine.setScale(xcoeff, ycoeff);
    }

    final Path2D hexShape = Utils.getHexShapeAsPath(engine, true);
    final int cellWidth = hexShape.getBounds().width;
    final int cellHeight = hexShape.getBounds().height;

    final HexFieldValue[][] cachedIcons = new HexFieldValue[this.exportData.getLayers().size()][];
    engine.setRenderer(new HexEngineRender<SVGGraphics2D>() {
      @Override
      public void renderHexCell(final HexEngine<SVGGraphics2D> engine, final SVGGraphics2D gfx, final float x, final float y, final int col, final int row) {
        final HexFieldValue[] stackValues = (HexFieldValue[]) engine.getModel().getValueAt(col, row);
        for (int i = 0; i < stackValues.length; i++) {
          final HexFieldValue valueToDraw = stackValues[i];
          if (valueToDraw == null) {
            continue;
          }
          drawValue(gfx, x, y, cachedIcons[i][valueToDraw.getIndex()]);
        }
      }

      private void drawValue(final SVGGraphics2D gfx, float x, float y, final HexFieldValue val) {
        if (val instanceof HexColorValue) {
          final HexColorValue v = (HexColorValue) val;
          gfx.setPaint(v.getColor());
          hexShape.transform(AffineTransform.getTranslateInstance(x, y));
          gfx.fill(hexShape);
          hexShape.transform(AffineTransform.getTranslateInstance(-x, -y));
        }
        else if (val instanceof HexSVGImageValue) {
          final HexSVGImageValue v = (HexSVGImageValue) val;

          final GraphicsNode obj = v.getImage().getSVGGraphicsNode();
          final AffineTransform t = obj.getTransform();
          try {
            final AffineTransform tr = AffineTransform.getTranslateInstance(x, y);
            tr.scale(cellWidth / v.getImage().getSVGWidth(), cellHeight / v.getImage().getSVGHeight());
            obj.setTransform(tr);
            obj.paint(gfx);
          }
          finally {
            if (t == null) {
              obj.setTransform(new AffineTransform());
            }
            else {
              obj.setTransform(t);
            }
          }
        }
      }

      @Override
      public void attachedToEngine(final HexEngine<?> engine) {
      }

      @Override
      public void detachedFromEngine(final HexEngine<?> engine) {
      }

    });

    for (int layerIndex = 0; layerIndex < reversedNormalizedStack.size(); layerIndex++) {
      final HexFieldLayer theLayer = reversedNormalizedStack.get(layerIndex);
      final HexFieldValue[] cacheLineForLayer = new HexFieldValue[theLayer.getHexValuesNumber()];
      for (int valueIndex = 1; valueIndex < theLayer.getHexValuesNumber(); valueIndex++) {
        cacheLineForLayer[valueIndex] = theLayer.getHexValueForIndex(valueIndex);
      }
      cachedIcons[layerIndex] = cacheLineForLayer;
    }

    engine.draw(g2d);

    if (this.exportData.isExportHexBorders()) {
      g2d.setStroke(new BasicStroke(docOptions.getLineWidth()));
      g2d.setColor(docOptions.getColor());
      for (int x = 0; x < engine.getModel().getColumnNumber(); x++) {
        for (int y = 0; y < engine.getModel().getRowNumber(); y++) {
          final float cx = engine.calculateX(x, y);
          final float cy = engine.calculateY(x, y);
          g2d.translate(cx, cy);
          g2d.draw(hexShape);
          g2d.translate(-cx, -cy);
        }
      }
    }

    if (this.exportData.isCellCommentariesExport()) {
      final Iterator<Entry<HexPosition, String>> iterator = this.cellComments.iterator();
      g2d.setFont(new Font("Arial", Font.BOLD, 12));
      while (iterator.hasNext()) {
        final Entry<HexPosition, String> item = iterator.next();
        final HexPosition pos = item.getKey();
        final String text = item.getValue();
        final float x = engine.calculateX(pos.getColumn(), pos.getRow());
        final float y = engine.calculateY(pos.getColumn(), pos.getRow());

        final Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(text, g2d);

        final float dx = x - ((float) textBounds.getWidth() - engine.getCellWidth()) / 2;

        g2d.setColor(Color.BLACK);
        g2d.drawString(text, dx, y);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, dx - 2, y - 2);
      }
    }

    final ByteArrayOutputStream result = new ByteArrayOutputStream(256000);
    final Writer writer = new OutputStreamWriter(result, "UTF-8");
    g2d.stream(writer, true);
    writer.close();
    g2d.dispose();

    return result.toByteArray();
  }

  @Override
  public void export(final File file) throws IOException {
    FileUtils.writeByteArrayToFile(file, generateImage());
  }
}
