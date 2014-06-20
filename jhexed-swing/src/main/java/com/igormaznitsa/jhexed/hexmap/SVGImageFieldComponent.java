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

import com.igormaznitsa.jhexed.renders.svg.SVGImage;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import javax.swing.*;

final class SVGImageFieldComponent extends JComponent implements HexMapPanelInsideComponent {

  private static final long serialVersionUID = 7548716206830303193L;

  private SVGImage svgImage;
  private final HexMapPanel parent;

  private boolean showImage = true;

  public SVGImageFieldComponent(final HexMapPanel parent) {
    super();
    this.parent = parent;

    setDoubleBuffered(false);
    updateSizeForImage();
  }

  public void setSVGImage(final SVGImage img) {
    this.svgImage = img;
    updateSizeForImage();
    revalidate();
    repaint();
  }

  public SVGImage getSVGImage() {
    return this.svgImage;
  }

  private void updateSizeForImage() {
    float w = 0;
    float h = 0;

    if (this.svgImage != null) {
      w = this.svgImage.getSVGWidth();
      h = this.svgImage.getSVGHeight();
    }

    final float zoom = this.parent.getZoom();

    w *= zoom;
    h *= zoom;

    final Dimension d = new Dimension(Math.round(w), Math.round(h));
    if (SwingUtilities.isEventDispatchThread()) {
      setSize(d);
      setMinimumSize(d);
      setMaximumSize(d);
      setPreferredSize(d);
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          setSize(d);
          setMinimumSize(d);
          setMaximumSize(d);
          setPreferredSize(d);
        }
      });
    }
  }

  @Override
  protected void paintComponent(final Graphics g) {
    if (this.showImage) {
      try {
        final Container c = getParent();
        final Graphics2D g2d = (Graphics2D) g;
        final AffineTransform t = g2d.getTransform();

        final AffineTransform z = new AffineTransform(t);

        final float zoom = this.parent.getZoom();

        z.scale(zoom, zoom);

        g2d.setTransform(z);

        svgImage.render(g2d);

        g2d.setTransform(t);

        g2d.setColor(Color.red);
        g2d.drawRect(0, 0, getBounds().width, getBounds().height);

      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  @Override
  public void onHexMapPanelInsideChange(final HexMapPanel parent, final int eventId) {
    if (eventId == HexMapPanel.EVENT_ZOOM_CHANGED) {
      updateSizeForImage();
      revalidate();
      repaint();
    }
  }

  public boolean isShowImage() {
    return this.showImage;
  }

  public boolean setShowImage(final boolean flag) {
    if (this.showImage != flag) {
      this.showImage = flag;
      revalidate();
      repaint();
      return true;
    }
    return false;
  }
}
