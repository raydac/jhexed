def getPluginName(){
  return 'Number of a value over base'
}

def getDescription(){
  return 'Calculate the number of a value on a layer over a value of another layer'
}

def tostr(layer,index){
  hex_val = hexValue(layer,index)
  if (hex_val.getName().isEmpty()) return index;
  return index+'('+hex_val.getName()+')';
}

def doWork(activeTool, activeLayer){
  selected_base_layer = selectLayerDialog('Select base layer');
  if (!selected_base_layer) return;
  selected_base_value = selectValueDialog('Select base value',selected_base_layer);
  if (selected_base_value<0) return;

  selected_check_layer = selectLayerDialog('Select layer to check');
  if (!selected_check_layer) return;
  selected_check_value = selectValueDialog('Select value to calculate',selected_check_layer);
  if (selected_check_value<0) return;

  number=0;
  for(x in 0..selected_base_layer.getColumnNumber()){
    for(y in 0..selected_base_layer.getRowNumber()){
      if (getHex(selected_base_layer,x,y)==selected_base_value && getHex(selected_check_layer,x,y)==selected_check_value) number++;
    }
  }
  info('Found '+number+' cell(s) of value '+tostr(selected_check_layer,selected_check_value)+' on \''+selected_check_layer.getLayerName()+'\'\nplaced over '+tostr(selected_base_layer,selected_base_value)+' of \''+selected_base_layer.getLayerName()+'\'')
}
