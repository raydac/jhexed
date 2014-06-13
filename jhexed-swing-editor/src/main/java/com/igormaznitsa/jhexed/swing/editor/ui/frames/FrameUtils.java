package com.igormaznitsa.jhexed.swing.editor.ui.frames;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

public final class FrameUtils {
  private FrameUtils(){
    
  }
  
  public static void ensureVisibilityAtParent(final JInternalFrame frame){
    final JComponent parent = (JComponent)frame.getParent();
    final Rectangle parentRect = SwingUtilities.calculateInnerArea(parent, null);
    final Rectangle frameRect = frame.getBounds();

    final int x = Math.min(frameRect.x,Math.max(0, parentRect.width-frameRect.width));
    final int y = Math.min(frameRect.y, Math.max(0, parentRect.height-frameRect.height));
  
    frame.setLocation(x, y);
  }
}
