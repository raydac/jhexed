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

/**
 * Interface describes an object which should be aware that it is connected to a HexEngine
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public interface AttachDetachAware {
  /**
   * Notification that the object has been attached to a HexEngine
   * @param engine the engine which the object is attached to
   */
  void attachedToEngine(HexEngine<?> engine);
  /**
   * Notification that the object has been detached from a HexEngine
   * @param engine  the engine which the object is detached from
   */
  void detachedFromEngine(HexEngine<?> engine);
}
