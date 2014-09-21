def getPluginName(){
  return 'Number of a value over base'
}

def getDescription(){
  return 'Calculate the number of a value on a layer over a value of another layer'
}

def tostr(hex_val){
  if (hex_val.getName().isEmpty()) return hex_val.getIndex();
  return hex_val.getIndex()+'('+hex_val.getName()+')';
}

def tolist(layer, hex_vals){
  result = 'Over cells in \''+layer.getLayerName()+'\':\n';
  for(v in hex_vals){
    result += tostr(v)+'\n'
  }
  return result
}

def tovrt(hex_vals){
  result = ''
  for(v in hex_vals){
    if (!result.isEmpty()) result += ','
    result += tostr(v)
  }
  return result
}

def doWork(activeTool, activeLayer){
  selected_base_layer = selectLayerDialog('Select base layer');
  if (!selected_base_layer) return;
  selected_base_values = selectValuesDialog('Select base values',selected_base_layer);
  if (!selected_base_values) return;

  selected_check_layer = selectLayerDialog('Select layer to check');
  if (!selected_check_layer) return;
  selected_check_values = selectValuesDialog('Select values to calculate',selected_check_layer);
  if (!selected_check_values) return;

  number=0;
  for(x in 0..selected_base_layer.getColumnNumber()){
    for(y in 0..selected_base_layer.getRowNumber()){
      if (getHex(selected_base_layer,x,y) in selected_base_values && getHex(selected_check_layer,x,y) in selected_check_values) number++;
    }
  }
  info('Found '+number+' cell(s) of value(s) '+tovrt(selected_check_values)+' on \''+selected_check_layer.getLayerName()+'\'\n'+tolist(selected_base_layer,selected_base_values))
}
