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
package com.igormaznitsa.jhexed.swing.editor.ui.extensions;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.swing.editor.model.LayerListModel;
import com.igormaznitsa.jhexed.swing.editor.ui.frames.layers.LayerRecordPanel;

public class Layers {
  private final LayerListModel model;
  
  Layers(final LayerListModel model){
    this.model = model;
  }
  
  public int size(){
    return this.model.getSize();
  }
  
  public HexFieldLayer get(final String name){
    for(int i=0;i<this.model.getSize();i++){
      final LayerRecordPanel panel = this.model.getElementAt(i);
      if (name.equalsIgnoreCase(panel.getHexField().getLayerName())){
        return panel.getHexField();
      }
    }
    return null;
  }
  
  public HexFieldLayer get(final int index){
    return this.model.getElementAt(index).getHexField();
  }
}
