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
import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.renders.swing.ColorHexRender;
import com.igormaznitsa.jhexed.swing.editor.model.DocumentCellComments;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class PNGImageExporter implements Exporter {

  private final DocumentOptions docOptions;
  private final SelectLayersExportData exportData;
  private final DocumentCellComments cellComments;
  
  public PNGImageExporter(final DocumentOptions docOptions, final SelectLayersExportData exportData, final DocumentCellComments cellComments) {
    this.docOptions = docOptions;
    this.exportData = exportData;
    this.cellComments = cellComments;
  }

  public BufferedImage generateImage() throws IOException {
    final int DEFAULT_CELL_WIDTH = 48;
    final int DEFAULT_CELL_HEIGHT = 48;

    final int imgWidth = this.docOptions.getImage() == null ? DEFAULT_CELL_WIDTH * this.docOptions.getColumns() : Math.round(this.docOptions.getImage().getSVGWidth());
    final int imgHeight = this.docOptions.getImage() == null ? DEFAULT_CELL_HEIGHT * this.docOptions.getRows() : Math.round(this.docOptions.getImage().getSVGHeight());

    final BufferedImage result;
    if (exportData.isBackgroundImageExport() && this.docOptions.getImage() != null) {
      result = this.docOptions.getImage().rasterize(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    }
    else {
      result = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
    }

    final Graphics2D gfx = result.createGraphics();
    gfx.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    gfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    final HexEngine<Graphics2D> engine = new HexEngine<Graphics2D>(DEFAULT_CELL_WIDTH, DEFAULT_CELL_HEIGHT, this.docOptions.getHexOrientation());

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
    final float xcoeff = (float) result.getWidth() / visibleSize.getWidth();
    final float ycoeff = (float) result.getHeight() / visibleSize.getHeight();
    engine.setScale(xcoeff, ycoeff);

    final Image[][] cachedIcons = new Image[this.exportData.getLayers().size()][];
    engine.setRenderer(new ColorHexRender() {

      private final Stroke stroke = new BasicStroke(docOptions.getLineWidth());

      @Override
      public Stroke getStroke() {
        return this.stroke;
      }

      @Override
      public Color getFillColor(HexEngineModel<?> model, int col, int row) {
        return null;
      }

      @Override
      public Color getBorderColor(HexEngineModel<?> model, int col, int row) {
        return exportData.isExportHexBorders() ? docOptions.getColor() : null;
      }

      @Override
      public void drawExtra(HexEngine<Graphics2D> engine, Graphics2D g, int col, int row, Color borderColor, Color fillColor) {
      }

      @Override
      public void drawUnderBorder(final HexEngine<Graphics2D> engine, final Graphics2D g, final int col, final int row, final Color borderColor, final Color fillColor) {
        final HexFieldValue[] stackValues = (HexFieldValue[]) engine.getModel().getValueAt(col, row);
        for (int i = 0; i < stackValues.length; i++) {
          final HexFieldValue valueToDraw = stackValues[i];
          if (valueToDraw == null) {
            continue;
          }
          g.drawImage(cachedIcons[i][valueToDraw.getIndex()], 0, 0, null);
        }
      }

    });

    final Path2D hexShape = ((ColorHexRender) engine.getRenderer()).getHexPath();
    final int cellWidth = hexShape.getBounds().width;
    final int cellHeight = hexShape.getBounds().height;

    for (int layerIndex = 0; layerIndex < reversedNormalizedStack.size(); layerIndex++) {
      final HexFieldLayer theLayer = reversedNormalizedStack.get(layerIndex);
      final Image[] cacheLineForLayer = new Image[theLayer.getHexValuesNumber()];
      for (int valueIndex = 1; valueIndex < theLayer.getHexValuesNumber(); valueIndex++) {
        cacheLineForLayer[valueIndex] = theLayer.getHexValueForIndex(valueIndex).makeIcon(cellWidth, cellHeight, hexShape, true);
      }
      cachedIcons[layerIndex] = cacheLineForLayer;
    }

    engine.draw(gfx);

    if (this.exportData.isCellCommentariesExport()){
      final Iterator<Entry<HexPosition,String>> iterator = this.cellComments.iterator();
      gfx.setFont(new Font("Arial",Font.BOLD,12));
      while(iterator.hasNext()){
        final Entry<HexPosition,String> item = iterator.next();
        final HexPosition pos = item.getKey();
        final String text = item.getValue();
        final float x = engine.calculateX(pos.getColumn(), pos.getRow());
        final float y = engine.calculateY(pos.getColumn(), pos.getRow());
      
        final Rectangle2D textBounds = gfx.getFontMetrics().getStringBounds(text, gfx);
        
        final float dx = x-((float) textBounds.getWidth()-engine.getCellWidth())/2;
        
        gfx.setColor(Color.BLACK);
        gfx.drawString(text, dx, y);
        gfx.setColor(Color.WHITE);
        gfx.drawString(text, dx-2, y-2);
      }
    }
    
    gfx.dispose();

    return result;
  }

  @Override
  public void export(final File file) throws IOException {
    final BufferedImage img = generateImage();
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(1000000);
    ImageIO.write(img, "png", buffer);
    buffer.flush();
    FileUtils.writeByteArrayToFile(file, buffer.toByteArray());
  }
}
