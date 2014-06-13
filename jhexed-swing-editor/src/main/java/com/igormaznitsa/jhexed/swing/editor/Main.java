package com.igormaznitsa.jhexed.swing.editor;

import com.igormaznitsa.jhexed.swing.editor.ui.MainForm;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(final String... args) {
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }
    catch (Exception ex) {
      java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new MainForm().setVisible(true);
      }
    });
  }  
}
