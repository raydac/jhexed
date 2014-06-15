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

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;

/**
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class LayerExportRecord {
  private boolean allowed;
  private final HexFieldLayer layer;

  public LayerExportRecord(final boolean allowed, final HexFieldLayer dataField) {
    this.allowed = allowed;
    this.layer = dataField;
  }

  public boolean isAllowed() {
    return this.allowed;
  }

  public void setAllowed(final boolean flag) {
    this.allowed = flag;
  }

  public HexFieldLayer getLayer() {
    return this.layer;
  }
  
}
