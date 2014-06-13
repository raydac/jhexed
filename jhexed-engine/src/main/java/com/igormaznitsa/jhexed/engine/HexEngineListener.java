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
package com.igormaznitsa.jhexed.engine;

import com.igormaznitsa.jhexed.engine.renders.HexEngineRender;

/**
 * The Interface describes a listener to get notifications about inside hex engine events.
 * 
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 * 
 * @see HexEngine
 */
public interface HexEngineListener {
  /**
   * Notification that the scale factor of a hex engine has been cnahged
   * @param source the source hex engine
   * @param scaleX the new scale factor for X
   * @param scaleY the new scale factor for Y
   */
  void onScaleFactorChanged(HexEngine<?> source, float scaleX, float scaleY);
  
  /**
   * Notification that an engine has changed its render
   * @param source the source hex engine
   * @param oldRender the old render
   * @param newRender the new render
   */
  void onRenderChanged(HexEngine<?> source, HexEngineRender<?> oldRender, HexEngineRender<?> newRender);
  
  /**
   * Notification that an engine has changed its model
   * @param source the source hex engine
   * @param oldModel the old model
   * @param newModel the new model
   */
  void onModelChanged(HexEngine<?> source, HexEngineModel<?> oldModel, HexEngineModel<?> newModel);
  
  /**
   * Notification that vital parameters of engine were reconfigured (width,height,orientation)
   * @param source the source hex engine
   */
  void onEngineReconfigured(HexEngine<?> source);
}
