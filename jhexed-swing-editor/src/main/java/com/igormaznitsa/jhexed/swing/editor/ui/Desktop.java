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
package com.igormaznitsa.jhexed.swing.editor.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Desktop extends JDesktopPane {

  private static final long serialVersionUID = 2920189108672529836L;

  private final JScrollPane scrollPane;
  private Rectangle oldSize;

  private Point dragPoint = new Point();
  
  public Desktop() {
    super();
    setBackground(javax.swing.UIManager.getColor("Panel.background"));

    setBorder(null);
    setOpaque(true);
    getInsets().set(0, 0, 0, 0);
    scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getInsets().set(0, 0, 0, 0);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    putClientProperty("JDesktopPane.dragMode", "outline");

    scrollPane.setLocation(0, 0);
    scrollPane.getViewport().setDoubleBuffered(true);

    add(scrollPane, JDesktopPane.DEFAULT_LAYER);

    addComponentListener(new ComponentListener() {

      @Override
      public void componentResized(ComponentEvent e) {
        processResize();
      }

      @Override
      public void componentMoved(ComponentEvent e) {
      }

      @Override
      public void componentShown(ComponentEvent e) {
      }

      @Override
      public void componentHidden(ComponentEvent e) {
      }
    });
  }

  public void initDrag(final Point pnt) {
    this.dragPoint = pnt;
    this.scrollPane.getViewport().getView().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  public void endDrag() {
    this.dragPoint = null;
    this.scrollPane.getViewport().getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
  }

  public void processDrag(final Point pnt) {
    int deltaX = pnt.x - dragPoint.x;
    int deltaY = pnt.y - dragPoint.y;
    this.dragPoint = pnt;

    JViewport view = this.scrollPane.getViewport();
    final Point pos = view.getViewPosition();
    pos.x = Math.max(0,pos.x-deltaX);
    pos.y = Math.max(0,pos.y-deltaY);
    
    view.setViewPosition(pos);

    this.dragPoint.x = pnt.x - deltaX;
    this.dragPoint.y = pnt.y - deltaY;
    
    view.repaint();
  }

  public void setContentPane(final JComponent compo) {
    this.scrollPane.setViewportView(compo);
    this.scrollPane.getViewport().getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
  }

  public void addFrame(final JInternalFrame frame) {
    this.add(frame, JLayeredPane.PALETTE_LAYER);
  }

  private void processResize() {
    final Rectangle visibleRect = this.getVisibleRect();

    alignInternalFrames(this.oldSize, visibleRect);
    this.oldSize = visibleRect;

    scrollPane.setSize(visibleRect.width, visibleRect.height);
    scrollPane.updateUI();
  }

  private void alignInternalFrames(final Rectangle oldVisibleSize, final Rectangle newVisibleSize) {
    if (oldVisibleSize != null) {
      for (final JInternalFrame f : this.getAllFrames()) {
        final Rectangle frameRectangle = f.getBounds();

        final boolean oldLeft = frameRectangle.x < (oldVisibleSize.width / 2);
        final boolean oldTop = frameRectangle.y < (oldVisibleSize.height / 2);

        int x = frameRectangle.x;
        int y = frameRectangle.y;

        if (oldLeft) {
          if (x + (frameRectangle.width / 2) >= newVisibleSize.width) {
            x = 5;
          }
        }
        else {
          if (x + frameRectangle.width > newVisibleSize.width) {
            x = Math.max(0, newVisibleSize.width - frameRectangle.width);
          }
        }

        if (oldTop) {
          if (y + (frameRectangle.height / 2) >= newVisibleSize.height) {
            y = 5;
          }
        }
        else {
          if (y + frameRectangle.height > newVisibleSize.height) {
            y = Math.max(0, newVisibleSize.height - frameRectangle.height);
          }
        }

        f.setLocation(x, y);
      }
    }
  }

  @Override
  public void paintComponent(final Graphics g) {
    super.paintComponent(g);
    final Color back = getBackground();
    if (back != null) {
      g.setColor(back);
      g.fillRect(0, 0, getWidth(), getHeight());
    }
  }
}
