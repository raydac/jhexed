def getPluginName(){
  return 'Number of a value'
}

def getDescription(){
  return 'Calculate the number of a value on a layer'
}

def tostr(layer,index){
  hex_val = hexValue(layer,index)
  if (hex_val.getName().isEmpty()) return index;
  return index+'('+hex_val.getName()+')';
}

def doWork(activeTool, activeLayer){
  selected_layer = selectLayerDialog('Select layer');
  if (selected_layer){
    value = selectValueDialog('Select value to calculate',selected_layer);
    if (value>0){
      number = 0;
      for(x in 0..selected_layer.getColumnNumber()){
        for(y in 0..selected_layer.getRowNumber()){
          if (getHex(selected_layer,x,y)==value) number++;
        }
      }
      info('Found '+number+' cell(s) of value '+tostr(selected_layer,value)+' on \''+selected_layer.getLayerName()+'\'\n')
    }
  }else{
    warn('To use the plugin, you must select a layer');
  }
}
