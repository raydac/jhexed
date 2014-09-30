def getPluginName(){
  return 'Calculate number of values'
}

def getDescription(){
  return 'Calculate the number of a value on a layer'
}

def tostr(hex_val){
  if (hex_val.getName().isEmpty()) return hex_val.getIndex();
  return hex_val.getIndex()+'('+hex_val.getName()+')';
}

def tolist(layer, hex_vals){
  result = 'Layer is \''+layer.getLayerName()+'\' cells are :\n';
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
  layer=selectLayerDialog('Select layer');
  String text = null;
  if (layer){
    values = selectValuesDialog('Select values to calculate',layer);
    if (values){
      if (confirm('Whole map or special places?','Calculate for whole map?')){
        number = 0;
        for(x=0;x<layer.getColumnNumber();x++){
          for(y=0;y<layer.getRowNumber();y++){
            if (getHex(layer,x,y) in values) number++;
          }
        }
        info('Calculation', 'Found '+number+' cell(s) of '+tovrt(values)+' on the whole map');
      }else{
        spec_layer = selectLayerDialog('Select special layer');
        if (spec_layer){
          spec_values = selectValuesDialog('Select base values',spec_layer);
          if (spec_values){
            number = 0;
            for(x=0;x<layer.getColumnNumber();x++){
              for(y=0;y<layer.getRowNumber();y++){
                if (getHex(layer,x,y) in values && getHex(spec_layer,x,y) in spec_values) number++;
              }
            }
            info('Calculation', 'Found '+number+' cell(s) of '+tovrt(values)+' placed over :\n'+tolist(spec_layer,spec_values));
          }
        }
      }
    }
  }
}

