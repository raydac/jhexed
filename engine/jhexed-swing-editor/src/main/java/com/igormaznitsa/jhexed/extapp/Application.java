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
package com.igormaznitsa.jhexed.extapp;

import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.extapp.lookup.Lookup;
import com.igormaznitsa.jhexed.hexmap.HexMapPanel;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import javax.swing.JComponent;

public interface Application extends Lookup {
  void init(ApplicationContext context);
  void start(ApplicationContext context);
  boolean isReadyForDestroying(ApplicationContext context);
  void destroy(ApplicationContext context);
  String getID();
  InputStream getInitialMap(ApplicationContext context);
  JComponent getUIComponent(UIComponentPosition position);
  Image getApplicationIcon();
  String getTitle();
  String processHexAction(final HexMapPanel hexMapPanel, MouseEvent mouseEvent, final HexAction action, final HexPosition pos);
  boolean allowPopupTrigger();
}
