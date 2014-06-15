package com.igormaznitsa.jhexed.swing.editor.model;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class InsideApplicationBus {

  public enum AppBusEvent {
    SELECTED_TOOL_CHANGED,
    SELECTED_LAYER_CHANGED,
    A_FRAME_CHANGED_ITS_STATUS,
    HEX_SHAPE,
    LAYER_NEEDS_EDITION,
    REQUEST_EVENT,
    HEX_FIELD_NEEDS_REPAINT
  }
  
  public interface AppBusListener{
    void onAppBusEvent(Object source,InsideApplicationBus bus, AppBusEvent event, Object ... objects);
  }
  
  private static final InsideApplicationBus instance = new InsideApplicationBus();
  
  private InsideApplicationBus(){
    
  }
  
  public static InsideApplicationBus getInstance(){
    return instance;
  }
  
  private final List<AppBusListener> listeners = new CopyOnWriteArrayList<AppBusListener>();
  
  public void addAppBusListener(final AppBusListener l){
    this.listeners.add(l);
  }
  
  public void removeAppBusListener(final AppBusListener l){
    this.listeners.remove(l);
  }

  public void fireEvent(final Object source, final AppBusEvent event, final Object ... args){
    for(final AppBusListener l : this.listeners){
      if (source!=l){
        l.onAppBusEvent(source, this, event, args);
      }
    }
  }
  
}
