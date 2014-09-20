def getPluginName(){
  return 'Clear value on a layer'
}

def getDescription(){
  return 'The Plugin allows to clear a selected value on a layer'
}

def doWork(tool, layer){
  selectedlayer = selectLayer("Select layer");
  if (selectedlayer){
    value = (byte)selectValue("Select value to clear",selectedlayer);
    if (value>0){
      printf("Remove value %d from %s",value,selectedlayer.getLayerName())
      for(int x=0;x<selectedlayer.getColumnNumber();x++){
        for(int y=0;y<selectedlayer.getRowNumber();y++){
          if (selectedlayer.getValueAt(x,y)==value) selectedlayer.setValueAt(x,y,(byte)0);
        }
      }
    }
  }else{
    warn('To use the plugin, you must select a layer');
  }
}
