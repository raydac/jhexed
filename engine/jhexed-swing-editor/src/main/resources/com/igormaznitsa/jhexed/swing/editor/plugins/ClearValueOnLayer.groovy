def getPluginName(){
  return 'Clear value on a layer'
}

def getDescription(){
  return 'The Plugin allows to clear a selected value on a layer'
}

def doWork(activeTool, activeLayer){
  selected_layer = selectLayerDialog("Select layer");
  if (selected_layer){
    value = selectValueDialog("Select value to clear",selected_layer);
    if (value>0){
      addUndo(selected_layer);
      for(x in 0..selected_layer.getColumnNumber()){
        for(y in 0..selected_layer.getRowNumber()){
          if (getHex(selected_layer,x,y)==value) setHex(selected_layer,x,y,0);
        }
      }
    }
  }else{
    warn('To use the plugin, you must select a layer');
  }
}
