import com.igormaznitsa.jhexed.engine.misc.HexPosition
import java.util.Random

def getPluginName(){
  return 'Random spread'
}

def getDescription(){
  return 'The Plugin randomly spreads a value in a layer'
}

def doWork(activeTool, activeLayer){
  selected_layer = selectLayerDialog("Select layer for spreading");
  if (selected_layer){
    value = selectValueDialog("Select spread value",selected_layer);
    if (value){
      base_layer = null;
      base_layer_values = null;
      if (confirm("Use base layer","Do you want to use a base layer values?")){
        base_layer = selectLayerDialog("Select base layer");
        if (!base_layer) return;

        base_layer_values = selectValuesDialog("Select base values", base_layer);
        if (!base_layer_values) return;
      }

      Integer number = selectInt("Number of values","Number of values to spread",1,1,100000);
      if (!number) return;

      Random rnd = new Random();

      List<HexPosition> positions = new ArrayList<HexPosition>(number);

      if (base_layer){
        for(x=0;x<base_layer.getColumnNumber();x++){
          for(y=0;y<base_layer.getRowNumber();y++){
            if (getHex(selected_layer,x,y).getIndex()==0 && getHex(base_layer,x,y) in base_layer_values) positions.add(new HexPosition(x,y));
          }
        }
      }else{
        for(x=0;x<selected_layer.getColumnNumber();x++){
          for(y=0;y<selected_layer.getRowNumber();y++){
            if (getHex(selected_layer,x,y).getIndex()==0) positions.add(new HexPosition(x,y));
          }
        }
      }
      
      if (positions.size()<number){
        warn("Detected only "+positions.size()+" cell(s) for spread, can't continue the operation");
      }else{
        addUndo(selected_layer);

        for(x=0;x<number;x++){
          HexPosition pos = positions.remove(rnd.nextInt(positions.size()));
          setHex(selected_layer, pos.getColumn(), pos.getRow(), value);
        }
      }
    }
  }
}
