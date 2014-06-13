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
