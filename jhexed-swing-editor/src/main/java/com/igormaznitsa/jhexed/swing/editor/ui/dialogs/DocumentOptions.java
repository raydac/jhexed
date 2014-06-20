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
package com.igormaznitsa.jhexed.swing.editor.ui.dialogs;

import com.igormaznitsa.jhexed.renders.svg.SVGImage;
import com.igormaznitsa.jhexed.swing.editor.Log;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.ImageIcon;

public class DocumentOptions {
  private static final int ICON_WIDTH = 32;
  private static final int ICON_HEIGHT = 16;
  private SVGImage image;
  private int columns;
  private int rows;
  private int hexOrientation;
  private float lineWidth;
  private String comments;
  private Color border;

  public DocumentOptions(final SVGImage image, final int columns, final int rows, final int hexOrientation, final float lineWidth, final Color border, final String comments) {
    this.image = image;
    this.columns = columns;
    this.rows = rows;
    this.hexOrientation = hexOrientation;
    this.lineWidth = lineWidth;
    this.border = border;
    this.comments = comments == null ? "" : comments;
  }

  public DocumentOptions(final byte[] array) throws IOException {
    final DataInputStream din = new DataInputStream(new ByteArrayInputStream(array));
    if (din.readBoolean()) {
      this.image = new SVGImage(din, true);
    }
    else {
      this.image = null;
    }
    this.border = new Color(din.readInt());
    this.hexOrientation = din.readByte() & 0xFF;
    this.columns = din.readInt();
    this.rows = din.readInt();
    this.lineWidth = din.readFloat();
    this.comments = din.readUTF();
  }

  public byte[] toByteArray() throws IOException {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(65000);
    final DataOutputStream dos = new DataOutputStream(buffer);
    if (this.image == null) {
      dos.writeBoolean(false);
    }
    else {
      dos.writeBoolean(true);
      this.image.write(dos, true);
    }
    dos.writeInt(this.border.getRGB());
    dos.writeByte((byte) this.hexOrientation);
    dos.writeInt(this.columns);
    dos.writeInt(this.rows);
    dos.writeFloat(this.lineWidth);
    dos.writeUTF(this.comments);
    return buffer.toByteArray();
  }

  public ImageIcon setColor(final Color color) {
    this.border = color;
    if (color == null) {
      return null;
    }
    else {
      final BufferedImage img = new BufferedImage(32, 16, BufferedImage.TYPE_INT_RGB);
      final Graphics g = img.getGraphics();
      g.setColor(this.border);
      g.fillRect(0, 0, ICON_WIDTH, ICON_HEIGHT);
      g.dispose();
      return new ImageIcon(img);
    }
  }

  public String getCommentary(){
    return this.comments;
  }
  
  public void setCommentary(final String value){
    this.comments = value == null ? "" : value;
  }
  
  public Color getColor() {
    return this.border;
  }

  public int getColumns() {
    return this.columns;
  }

  public void setColumns(final int columns) {
    this.columns = columns;
  }

  public int getRows() {
    return this.rows;
  }

  public void setRows(final int rows) {
    this.rows = rows;
  }

  public int getHexOrientation() {
    return this.hexOrientation;
  }

  public void setHexOrientation(final int hexOrientation) {
    this.hexOrientation = hexOrientation;
  }

  public float getLineWidth() {
    return this.lineWidth;
  }

  public void setLineWidth(final float lineWidth) {
    this.lineWidth = lineWidth;
  }

  public SVGImage getImage() {
    return this.image;
  }

  public ImageIcon setImage(final SVGImage img) {
    this.image = img;
    if (img == null) {
      return null;
    }
    else {
      try {
        return new ImageIcon(img.rasterize(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_RGB));
      }
      catch (IOException ex) {
        Log.error("Can't rasterize image",ex);
        return null;
      }
    }
  }
  
}
