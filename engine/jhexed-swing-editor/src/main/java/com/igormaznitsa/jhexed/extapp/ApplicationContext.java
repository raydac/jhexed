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

import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.extapp.hexes.HexLayer;
import com.igormaznitsa.jhexed.extapp.lookup.Lookup;
import com.igormaznitsa.jhexed.renders.svg.SVGImage;
import java.util.List;

public interface ApplicationContext extends Lookup {
  List<HexLayer> getHexLayers();
  HexEngine<?> getHexEngine();
  void upHexLayer(HexLayer layer);
  void downHexLayer(HexLayer layer);
  HexLayer findHexLayerForName(String name);
  HexLayer makeHexLayer(String name, String comment);
  HexLayer prerasterize(HexLayer layer);
  void deleteHexLayer(HexLayer layer);
  SVGImage getBackgroundImage();
  void refreshUi();
  MapOptions getMapOptions();
  void setMapOptions(MapOptions options);
  void endWork();
}
