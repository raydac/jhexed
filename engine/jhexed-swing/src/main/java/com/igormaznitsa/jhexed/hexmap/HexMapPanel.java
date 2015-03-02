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
package com.igormaznitsa.jhexed.hexmap;

import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.engine.HexEngineModel;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.renders.svg.SVGImage;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class HexMapPanel extends JPanel {

  private static final long serialVersionUID = -7851676965546147053L;

  public static final int EVENT_ZOOM_CHANGED = 0;

  private volatile float zoom = 1.0f;

  private final SVGImageFieldComponent mapImage;
  private final HexFieldComponent hexField;

  private final List<HexMapPanelListener> listeners = new ArrayList<HexMapPanelListener>();

  private boolean antialias = true;

  public HexMapPanel(final HexEngineModel<?> hexmodel) {
    super();
    setOpaque(false);

    final OverlayLayout layout = new OverlayLayout(this);
    setLayout(layout);

    this.mapImage = new SVGImageFieldComponent(this);
    
    final HexMapPanel thePanel = this;
    
    this.hexField = new HexFieldComponent(this, hexmodel){
      private static final long serialVersionUID = -1609785839309289037L;
      @Override
      public void afterPaint(final HexEngine<?> engine, final Graphics g) {
        for(final HexMapPanelListener l : listeners){
          l.onAfterPaint(thePanel, engine, g);
        }
      }
    };

    this.hexField.setAntialiased(this.antialias);

    add(this.hexField);
    add(this.mapImage);

    init();
  }

  public final void init(){
    this.hexField.getRenderer().setLineWidth(0.2f);
    this.hexField.getRenderer().setCommonBorderColor(Color.black);
    this.getHexEngine().changeEngineBaseParameters(32, 32, HexEngine.ORIENTATION_HORIZONTAL);
    this.setImage(null);
  }
  
  public SVGImage getImage() {
    return this.mapImage.getSVGImage();
  }

  public void setImage(final SVGImage image) {
    if (image != null) {
      image.setQuality(this.antialias);
    }
    this.mapImage.setSVGImage(image);
    updateHexLayoutForImage();
  }

  private void updateHexLayoutForImage() {
    final SVGImage img = this.mapImage == null ? null : this.mapImage.getSVGImage();
    final float imgWith = img == null ? HexFieldComponent.DEFAULT_ADAPT_WIDTH : img.getSVGWidth();
    final float imgHeight = img == null ? HexFieldComponent.DEFAULT_ADAPT_HEIGHT : img.getSVGHeight();

    final int calcw = Math.round(imgWith * this.zoom);
    final int calch = Math.round(imgHeight * this.zoom);
    this.hexField.adaptToSize(calcw, calch);
    revalidate();
    repaint();
  }

  public boolean isAntialiasing() {
    return this.antialias;
  }

  public void setAntialiasing(final boolean flag) {
    this.antialias = flag;
    if (this.mapImage.getSVGImage() != null) {
      this.mapImage.getSVGImage().setQuality(flag);
    }
    this.hexField.setAntialiased(flag);
    revalidate();
    repaint();
  }

  public float getZoom() {
    return this.zoom;
  }

  private void fireEvent(final int event) {
    for (final Component c : getComponents()) {
      if (c instanceof HexMapPanelInsideComponent) {
        ((HexMapPanelInsideComponent) c).onHexMapPanelInsideChange(this, event);
      }
    }
  }

  public void addHexMapPanelListener(final HexMapPanelListener l) {
    this.listeners.add(l);
  }

  public void removeHexMapPanelListener(final HexMapPanelListener l) {
    this.listeners.remove(l);
  }

  public void setZoom(final float zoom) {
    if (zoom < 0.3f) {
      throw new IllegalArgumentException("Zooom factor less than 0.3");
    }
    this.zoom = zoom;
    fireEvent(EVENT_ZOOM_CHANGED);

    for (final HexMapPanelListener l : this.listeners) {
      l.onZoomChanged(this, zoom);
    }

    revalidate();
    repaint();
  }

  public boolean changeHexOrientation(final int orientation) {
    if (this.hexField.changeEngineParameters(orientation)) {
      updateHexLayoutForImage();
      return true;
    }
    return false;
  }

  public LayerableHexValueSourceRender getHexRenderer() {
    return this.hexField.getRenderer();
  }

  public HexPosition getHexPosition(final Point point) {
    final Point pnt = SwingUtilities.convertPoint(this, point, this.hexField);
    return this.hexField.getHexCoordForPoint(pnt);
  }

  public int getHexOrientation() {
    return this.hexField.getHexOrientation();
  }

  public Path2D getHexShape() {
    return this.hexField.getHexShape();
  }

  public boolean isValidPosition(final HexPosition position) {
    if (position == null) return false;
    return this.hexField.isPositionValid(position);
  }

  public HexEngine<?> getHexEngine() {
    return this.hexField.getHexEngine();
  }

  public void setShowBackImage(final boolean show) {
    this.mapImage.setShowImage(show);
  }
  
  public boolean isShowBackImage(){
    return this.mapImage.isShowImage();
  }
}
