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
package com.igormaznitsa.jhexed.swing.editor;

import java.io.File;
import java.util.logging.*;

public class Log {
  private final static Logger LOGGER = Logger.getLogger("JHexedMapEditor");  

  static {
    LOGGER.setLevel(Level.ALL);
    
    try{
      final File logFolder = new File("./log");
      logFolder.mkdirs();
      
      final FileHandler fileHandler = new FileHandler(logFolder.getAbsolutePath()+"/jhxeditor.log", false);
      fileHandler.setFormatter(new SimpleFormatter());
      LOGGER.addHandler(fileHandler);
    }catch(Throwable  ex){
      ex.printStackTrace();
    }
  }

  public static void info(final String text){
    LOGGER.info(text);
  }

  public static void warn(final String text){
    LOGGER.warning(text);
  }

  public static void error(final String text, final Throwable thr){
    LOGGER.log(Level.SEVERE,text,thr);
  }
}
