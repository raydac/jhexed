def getPluginName(){
  return 'Clear value on a layer'
}

def getDescription(){
  return 'The Plugin allows to clear a selected values on a layer'
}

def doWork(activeTool, activeLayer){
  selected_layer = selectLayerDialog("Select layer");
  if (selected_layer){
    values = selectValuesDialog("Select values to clear",selected_layer);
    if (values){
      addUndo(selected_layer);
      for(x in 0..selected_layer.getColumnNumber()){
        for(y in 0..selected_layer.getRowNumber()){
          if (getHex(selected_layer,x,y) in values) resetHex(selected_layer,x,y);
        }
      }
    }
  }else{
    warn('To use the plugin, you must select a layer');
  }
}
