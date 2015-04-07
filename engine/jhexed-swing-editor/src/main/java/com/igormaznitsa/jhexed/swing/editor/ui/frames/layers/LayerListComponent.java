/* 
 * Copyright 2014 Igor Maznitsa (http://www.igormaznitsa.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jhexed.swing.editor.ui.frames.layers;

import com.igormaznitsa.jhexed.hexmap.HexFieldLayer;
import com.igormaznitsa.jhexed.swing.editor.model.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class LayerListComponent extends JList<LayerRecordPanel> implements ListSelectionListener {

  private static final long serialVersionUID = 3768100103591850526L;

  public LayerListComponent(final LayerListModel model) {
    super();

    setModel(model);

    final LayerListComponent theInstance = this;

    addMouseListener(new MouseAdapter() {

      @SuppressWarnings("unchecked")
      @Override
      public void mouseClicked(final MouseEvent evt) {
        final JList<LayerRecordPanel> list = (JList<LayerRecordPanel>) evt.getSource();

        final LayerRecordPanel focusedPanel = getFocusedListRecord(list, evt.getPoint());
        final JComponent componentAtPanel = getFocusedListComponent(list, evt.getPoint());
        if (componentAtPanel != null && !(componentAtPanel instanceof LayerRecordPanel)) {

          if (componentAtPanel instanceof JCheckBox) {
            ((JCheckBox) componentAtPanel).doClick();
          }
          else {
            if (evt.getClickCount() > 1) {
              InsideApplicationBus.getInstance().fireEvent(theInstance, InsideApplicationBus.AppBusEvent.LAYER_NEEDS_EDITION, focusedPanel);
            }
//            final LayerRecordPanel panel = (LayerRecordPanel) componentAtPanel.getParent();
//            final Point movedPoint = translatePointToListPanel(list, evt.getPoint(), panel);
//            evt.getPoint().translate(movedPoint.x, movedPoint.y);
//            evt.setSource(componentAtPanel);
//            componentAtPanel.dispatchEvent(evt);
          }
          list.repaint();
        }
      }
    });
    addMouseMotionListener(new MouseMotionAdapter() {

      @SuppressWarnings("unchecked")
      @Override
      public void mouseMoved(final MouseEvent evt) {
        final JList<LayerRecordPanel> list = (JList<LayerRecordPanel>) evt.getSource();
        final JComponent component = getFocusedListComponent(list, evt.getPoint());
        if (component != null) {
          list.setToolTipText(component.getToolTipText());
          list.setCursor(component.getCursor());
        }
        else {
          list.setToolTipText(null);
          list.setCursor(Cursor.getDefaultCursor());
        }
      }

    });

    setCellRenderer(new ListCellRenderer<LayerRecordPanel>() {
      private final JLabel label = new JLabel();

      @Override
      public Component getListCellRendererComponent(final JList<? extends LayerRecordPanel> list, final LayerRecordPanel value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        if (value instanceof LayerRecordPanel) {
          final LayerRecordPanel component = value;
          component.setSelected(isSelected);
          return component;
        }
        else {
          this.label.setText(String.valueOf(value));
          return this.label;
        }
      }
    });

    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    addListSelectionListener(this);
  }

  public boolean up(){
    final LayerRecordPanel panel = this.getSelectedValue();
    if (panel != null) {
      final int oldIndex = this.getSelectedIndex();
      if (((LayerListModel)this.getModel()).up(panel)){
        setSelectedIndex(oldIndex - 1);
        return true;
      }
    }
    return false;
  }
  
  public boolean down(){
    final LayerRecordPanel panel = this.getSelectedValue();
    if (panel != null) {
      final int oldIndex = this.getSelectedIndex();
      if (((LayerListModel)this.getModel()).down(panel)){
        setSelectedIndex(oldIndex+1);
        return true;
      }
    }
    return false;
  }
  
  private LayerRecordPanel getFocusedListRecord(final JList<LayerRecordPanel> list, final Point point) {
    final int index = list.locationToIndex(point);
    if (index >= 0) {
      return list.getModel().getElementAt(index);
    }
    return null;
  }

  private JComponent getFocusedListComponent(final JList<LayerRecordPanel> list, final Point point) {
    final int index = list.locationToIndex(point);

    JComponent result = null;

    if (index >= 0) {
      final LayerRecordPanel obj = list.getModel().getElementAt(index);

      final LayerRecordPanel panel = list.getModel().getElementAt(index);
      if (panel != null) {
        final Rectangle cellBounds = list.getCellBounds(index, index);
        panel.setBounds(cellBounds);
        panel.doLayout();
        final Point movedPoint = new Point(point.x - cellBounds.x, point.y - cellBounds.y);
        result = (JComponent) panel.getComponentAt(movedPoint);
      }
    }
    return result;
  }

  private Point translatePointToListPanel(final JList<LayerRecordPanel> list, final Point point, final LayerRecordPanel panel) {
    int index = -1;
    for (int i = 0; i < list.getModel().getSize(); i++) {
      if (panel == list.getModel().getElementAt(i)) {
        index = i;
        break;
      }
    }

    Point result = point;

    if (index >= 0) {
      final Rectangle cellBounds = list.getCellBounds(index, index);
      panel.setBounds(cellBounds);
      panel.doLayout();
      result = new Point(point.x - cellBounds.x, point.y - cellBounds.y);
    }
    return result;
  }

  @Override
  public void valueChanged(final ListSelectionEvent e) {
    final int index = this.getSelectedIndex();
    final HexFieldLayer layer = index < 0 ? null : (this.getModel().getElementAt(index)).getHexField();

    InsideApplicationBus.getInstance().fireEvent(this, InsideApplicationBus.AppBusEvent.SELECTED_LAYER_CHANGED, layer);
  }

  public void delete(final HexFieldLayer field) {
    ((LayerListModel)this.getModel()).removeLayer(field);
  }

}
