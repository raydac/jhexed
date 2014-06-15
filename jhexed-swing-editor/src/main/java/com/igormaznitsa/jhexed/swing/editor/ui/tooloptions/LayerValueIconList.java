package com.igormaznitsa.jhexed.swing.editor.ui.tooloptions;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jdesktop.swingx.WrapLayout;

public class LayerValueIconList extends JScrollPane {
  private static final long serialVersionUID = 4067088203855017500L;
 
  private static final int ICON_SIZE = 48;
  private HexFieldLayer currentLayerField;

  private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
  private static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.BLUE, 2);
  
  public interface LayerIconListListener {
    void onLeftClick(final HexFieldValue h, final ImageIcon icon);
    void onRightClick(final HexFieldValue h, final ImageIcon icon);
  }
  
  public static class HexButton extends JLabel {
    private static final long serialVersionUID = -6733971540369351944L;
    private final HexFieldValue value;
    private final LayerValueIconList parent;
    
    public HexFieldValue getHexValue(){
      return this.value;
    }
    
    public HexButton(final LayerValueIconList parent, final HexFieldValue hex){
      super();
      this.setBorder(EMPTY_BORDER);
      
      this.parent = parent;
      this.value = hex;
      setOpaque(false);
      setIcon(new ImageIcon(hex.makeIcon(ICON_SIZE, ICON_SIZE, this.parent.iconHexShape)));

      final HexButton theInstance = this;
      
      addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
          parent.processMouseClick(theInstance,e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
      });

      if (!hex.getName().isEmpty() || !hex.getComment().isEmpty()){
        final String name = hex.getName().isEmpty() ? "": "<h3>"+StringEscapeUtils.escapeHtml4(hex.getName())+"</h3>";
        final String comment = hex.getComment().isEmpty() ? "": "<p>"+StringEscapeUtils.escapeHtml4(hex.getComment())+"</p>";
        this.setToolTipText("<html>"+name+comment+"</html>");
      }
    }

    public ImageIcon getHexIcon(){
      return (ImageIcon) getIcon();
    }
  }
  
  private final JPanel content;
  private Path2D iconHexShape;
  
  private final List<LayerIconListListener> listeners = new ArrayList<LayerIconListListener>();
  
  public LayerValueIconList(){
    super();
    
    this.content = new JPanel(new WrapLayout(WrapLayout.LEFT));
    this.getViewport().setBackground(Color.white);
    this.content.setBackground(Color.white);
  
    this.getViewport().add(this.content);
    
  }

  protected void processMouseClick(final HexButton source, final MouseEvent evt){
    switch(evt.getButton()){
      case MouseEvent.BUTTON1:{
        for (final LayerIconListListener l : this.listeners) {
          l.onLeftClick(source.getHexValue(), source.getHexIcon());
        }
      }break;
      case MouseEvent.BUTTON3:{
        for(final LayerIconListListener l : this.listeners){
          l.onRightClick(source.getHexValue(),source.getHexIcon());
        }
      }break;
    }
  }
  
  public void setSelectedHexValue(final HexFieldValue value){
    for(final Component c : this.content.getComponents()){
      final HexButton b = (HexButton)c;
      if (b.getHexValue() == value){
        b.setBorder(LINE_BORDER);
      }else{
        b.setBorder(EMPTY_BORDER);
      }
    }
    this.content.repaint();
  }
  
  public void addLayerIconListListener(final LayerIconListListener l){
    this.listeners.add(l);
  }
  
  public void removeLayerIconListListener(final LayerIconListListener l){
    this.listeners.remove(l);
  }
  
  public void setLayerField(final HexFieldLayer layer){
    this.currentLayerField = layer;  
    refill();
  }

  public void setIconShape(final Path2D hexShape){
    this.iconHexShape = hexShape;
    refill();
  }
  
  private void refill(){
    this.content.removeAll();

    if (currentLayerField != null) {
      for (int i = 0; i < currentLayerField.getHexValuesNumber(); i++) {
        final HexFieldValue value = currentLayerField.getHexValueForIndex(i);
        this.content.add(new HexButton(this, value));
      }
    }

    this.content.revalidate();
    repaint();
  }
  
}
