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
