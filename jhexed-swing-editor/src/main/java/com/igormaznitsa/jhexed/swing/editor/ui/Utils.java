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

  public static String color2html(final Color c) {
    final String rc = Integer.toHexString(c.getRed()).toUpperCase(Locale.ENGLISH);
    final String gc = Integer.toHexString(c.getGreen()).toUpperCase(Locale.ENGLISH);
    final String bc = Integer.toHexString(c.getBlue()).toUpperCase(Locale.ENGLISH);

    final StringBuilder result = new StringBuilder(7);
    result.append('#');
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
}
