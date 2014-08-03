package com.igormaznitsa.jhexed;

import com.igormaznitsa.jhexed.engine.*;
import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import com.igormaznitsa.jhexed.engine.misc.HexRect2D;
import com.igormaznitsa.jhexed.renders.swing.ColorHexRender;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;


public class Test {
  public static void main(String ... args){
    final JFrame frame = new JFrame("JHexed");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    
    final HexEngine<Graphics2D> engine = new HexEngine<Graphics2D>(48, 48, HexEngine.ORIENTATION_VERTICAL);
    engine.setModel(new DefaultIntegerHexModel(16, 16, -1));
    
    final Color [] ALLOWEDCOLORS = new Color []{Color.white, Color.orange, Color.blue, Color.red, Color.green, Color.magenta, Color.yellow, Color.GRAY};
    
    engine.setRenderer(new ColorHexRender(){

      @Override
      public Color getFillColor(HexEngineModel<?> model, int col, int row) {
        final DefaultIntegerHexModel intmodel = (DefaultIntegerHexModel) model;
        return ALLOWEDCOLORS[intmodel.getValueAt(col, row) % ALLOWEDCOLORS.length];
      }
      
    });
    
    final JComponent content = new JComponent(){
      private static final long serialVersionUID = -8711833404388194458L;
      
      @Override
      protected void paintComponent(Graphics g) {
        engine.draw((Graphics2D)g);
      }

      @Override
      public Dimension getPreferredSize() {
        final HexRect2D rect = engine.getVisibleSize();
        return new Dimension(rect.getWidthAsInt(), rect.getHeightAsInt());
      }
    };

    content.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        final HexPosition position = engine.pointToHex(e.getX(), e.getY());
        if (engine.getModel().isPositionValid(position)){
          final DefaultIntegerHexModel model = (DefaultIntegerHexModel) engine.getModel();
          Integer value = model.getValueAt(position);
          if (value > 7){
            value = 0;
          }else{
            value ++;
          }
          model.setValueAt(position, value);
        }
        content.repaint();
      }
      
    });
    
    frame.add(content,BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);
  }
}
