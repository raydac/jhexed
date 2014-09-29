def getPluginName(){
  return 'Clear value on a layer'
}

def getDescription(){
  return 'The Plugin allows to clear a selected values on a layer'
}

def doWork(activeTool, activeLayer){
  selected_layer = selectLayerDialog("Select layer");
  if (selected_layer){
    values = selectValuesDialog("Select value(s) to clear",selected_layer);
    if (values){
      addUndo(selected_layer);
      for(x=0;x<selected_layer.getColumnNumber();x++){
        for(y=0;y<selected_layer.getRowNumber();y++){
          if (getHex(selected_layer,x,y) in values) resetHex(selected_layer,x,y);
        }
      }
    }
  }
}
