/*
 * Copyright 2014 Igor Maznitsa.
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
package com.igormaznitsa.jhexed.swing.editor.ui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public abstract class AbstractDialog extends JDialog {
  private static final long serialVersionUID = 6753570320611930587L;

  public AbstractDialog() {
  }

  public AbstractDialog(Frame owner) {
    super(owner);
  }

  public AbstractDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  public AbstractDialog(Frame owner, String title) {
    super(owner, title);
  }

  public AbstractDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  public AbstractDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
    super(owner, title, modal, gc);
  }

  public AbstractDialog(Dialog owner) {
    super(owner);
  }

  public AbstractDialog(Dialog owner, boolean modal) {
    super(owner, modal);
  }

  public AbstractDialog(Dialog owner, String title) {
    super(owner, title);
  }

  public AbstractDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  public AbstractDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
    super(owner, title, modal, gc);
  }

  public AbstractDialog(Window owner) {
    super(owner);
  }

  public AbstractDialog(Window owner, ModalityType modalityType) {
    super(owner, modalityType);
  }

  public AbstractDialog(Window owner, String title) {
    super(owner, title);
  }

  public AbstractDialog(Window owner, String title, ModalityType modalityType) {
    super(owner, title, modalityType);
  }

  public AbstractDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
    super(owner, title, modalityType, gc);
  }

  
  
  @Override
  protected JRootPane createRootPane() {
    final JRootPane theRootPane = new JRootPane();
    final KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
    final Action actionListener = new AbstractAction() {
      private static final long serialVersionUID = -5644390861803492172L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        processEscape(e);
      }
    };
    final InputMap inputMap = theRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(stroke, "ESCAPE");
    theRootPane.getActionMap().put("ESCAPE", actionListener);

    return theRootPane;
  }

  public abstract void processEscape(final ActionEvent e);
}
