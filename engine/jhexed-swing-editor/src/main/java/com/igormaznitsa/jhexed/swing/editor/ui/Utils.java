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
package com.igormaznitsa.jhexed.swing.editor.ui;

import java.awt.Color;
import java.awt.Image;
import java.io.*;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.IOUtils;

public final class Utils {

  public static final FileFilter JHX_FILE_FILTER = new FileFilter() {

    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".jhx");
    }

    @Override
    public String getDescription() {
      return "JHexed Map files (*.jhx)";
    }
  };

  public static final FileFilter SVG_FILE_FILTER = new FileFilter() {

    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".svg");
    }

    @Override
    public String getDescription() {
      return "SVG Image files (*.jhx)";
    }
  };

  public static final FileFilter PNG_FILE_FILTER = new FileFilter() {

    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".png");
    }

    @Override
    public String getDescription() {
      return "PNG Image files (*.png)";
    }
  };

  public static final FileFilter XML_FILE_FILTER = new FileFilter() {

    @Override
    public boolean accept(File f) {
      return f.isDirectory() || f.getName().toLowerCase(Locale.ENGLISH).endsWith(".xml");
    }

    @Override
    public String getDescription() {
      return "XML Image files (*.xml)";
    }
  };

  private Utils() {
  }

  public static int calculateBrightness(final Color color) {
    return (int) Math.sqrt(
            color.getRed() * color.getRed() * .241d
            + color.getGreen() * color.getGreen() * .691d
            + color.getBlue() * color.getBlue() * .068d);
  }

  public static String color2html(final Color c, final boolean alpha) {
    final String ac = Integer.toHexString(c.getAlpha()).toUpperCase(Locale.ENGLISH);
    final String rc = Integer.toHexString(c.getRed()).toUpperCase(Locale.ENGLISH);
    final String gc = Integer.toHexString(c.getGreen()).toUpperCase(Locale.ENGLISH);
    final String bc = Integer.toHexString(c.getBlue()).toUpperCase(Locale.ENGLISH);

    final StringBuilder result = new StringBuilder(7);
    result.append('#');
    if (alpha) {
      if (ac.length() == 1) {
        result.append('0');
      }
      result.append(ac);
    }
    if (rc.length() == 1) {
      result.append('0');
    }
    result.append(rc);
    if (gc.length() == 1) {
      result.append('0');
    }
    result.append(gc);
    if (bc.length() == 1) {
      result.append('0');
    }
    result.append(bc);

    return result.toString();
  }

  public static Color getLabelForegroundColor() {
    final Color labelForeground = UIManager.getColor("Label.foreground");
    return labelForeground == null ? Color.BLACK : labelForeground;
  }

  public static Color getPanelBackgroundColor() {
    final Color panelBack = UIManager.getColor("Panel.background");
    return panelBack == null ? Color.GRAY : panelBack;
  }

  public static boolean isDarkLAF() {
    final Color panelBack = UIManager.getColor("Panel.background");
    if (panelBack == null) {
      return false;
    }
    else {
      return calculateBrightness(panelBack) < 150;
    }

  }

  public static boolean isUnderNimbusLookAndFeel() {
    return UIManager.getLookAndFeel().getName().contains("Nimbus");
  }

  public static boolean isUnderWindowsClassicLookAndFeel() {
    return UIManager.getLookAndFeel().getName().equals("Windows Classic");
  }

  public static boolean isUnderGTKLookAndFeel() {
    return UIManager.getLookAndFeel().getName().contains("GTK");
  }

  public static Color getTreeTextBackground() {
    return UIManager.getColor("Tree.textBackground");
  }

  public static Color getListForeground(final boolean selected) {
    if (selected) {
      final Color color = UIManager.getColor("List.selectionForeground");
      if (color == null) {
        return UIManager.getColor("List[Selected].textForeground"); // Nimbus
      }
      return color;
    }
    else {
      return UIManager.getColor("List.foreground");
    }
  }

  public static Color getListBackground(final boolean selected) {
    if (selected) {
      if (isUnderNimbusLookAndFeel()) {
        return UIManager.getColor("List[Selected].textBackground"); // Nimbus
      }
      return UIManager.getColor("List.selectionBackground");
    }
    else {
      if (isUnderNimbusLookAndFeel()) {
        final Color color = UIManager.getColor("List.background");
        //noinspection UseJBColor
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
      }
      // Under GTK+ L&F "Table.background" often has main panel color, which looks ugly
      return isUnderGTKLookAndFeel() ? getTreeTextBackground() : UIManager.getColor("List.background");
    }
  }

  public static Image loadIcon(final String name) throws IOException {
    final String path = "com/igormaznitsa/jhexed/swing/editor/icons/" + name;
    final InputStream inStream = Utils.class.getClassLoader().getResourceAsStream(path);
    try {
      return ImageIO.read(inStream);
    }
    finally {
      IOUtils.closeQuietly(inStream);
    }
  }

  public static String byteArray2String(final byte[] array, final boolean signed, final boolean hex) {
    final StringBuilder result = new StringBuilder(array.length * 3);

    boolean nofirst = false;

    for (final byte b : array) {
      if (nofirst) {
        result.append(',');
      }
      else {
        nofirst = true;
      }

      final int val = signed ? b : b & 0xFF;
      if (hex) {
        final String hx = Integer.toHexString(val).toUpperCase(Locale.ENGLISH);
        result.append("0x");
        if (hx.length() < 2) {
          result.append('0').append(hx);
        }
        else {
          result.append(hx);
        }
      }
      else {
        result.append(val);
      }
    }

    return result.toString();
  }
}
