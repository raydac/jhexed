def getPluginName(){
  return 'Number of values'
}

def getDescription(){
  return 'Calculate the number of values on a layer'
}

def tostr(values){
  result = ''
  for(value in values){
    if (!result.isEmpty()) result+=','
    if (value.getName().isEmpty())
    result += value.getIndex();
    else result += value.getIndex()+'('+value.getName()+')';
  }
  return result
}

def doWork(activeTool, activeLayer){
  selected_layer = selectLayerDialog('Select layer');
  if (selected_layer){
    values = selectValuesDialog('Select values to calculate',selected_layer);
    if (values){
      number = 0;
      for(x in 0..selected_layer.getColumnNumber()){
        for(y in 0..selected_layer.getRowNumber()){
          if (getHex(selected_layer,x,y) in values) number++;
        }
      }
      info('Statistics','Found '+number+' cell(s) of value(s) '+tostr(values)+' on \''+selected_layer.getLayerName()+'\'\n')
    }
  }else{
    warn('To use the plugin, you must select a layer');
  }
}
