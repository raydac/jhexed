def getPluginName(){
  return 'Clear value on a layer'
}

def getDescription(){
  return 'The Plugin allows to clear a selected value on a layer'
}

def doWork(tool, layer){
  selected_layer = selectLayer("Select layer");
  if (selected_layer){
    value = (byte)selectValue("Select value to clear",selected_layer);
    if (value>0){
      printf("Remove value %d from %s",value,selected_layer.getLayerName())
      addUndo(selected_layer);
      for(int x=0;x<selected_layer.getColumnNumber();x++){
        for(int y=0;y<selected_layer.getRowNumber();y++){
          if (selected_layer.getValueAt(x,y)==value) selected_layer.setValueAt(x,y,(byte)0);
        }
      }
    }
  }else{
    warn('To use the plugin, you must select a layer');
  }
}
