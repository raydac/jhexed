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
package com.igormaznitsa.jhexed.swing.editor;

import com.igormaznitsa.jhexed.swing.editor.ui.MainForm;
import java.util.Locale;
import javax.swing.SwingUtilities;

public class Main {
  public static void main(final String... args) {
    try {
      javax.swing.UIManager.LookAndFeelInfo landf = null;
      
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        final String name = info.getName().trim().toLowerCase(Locale.ENGLISH);
        if (name.startsWith("windows")) {
          landf = info;
        } else if (landf==null && name.contains("gtk")){
          landf = info;
        }
      }
      if (landf!=null){
        System.out.println("Selected L&F: "+landf.getClassName());
        javax.swing.UIManager.setLookAndFeel(landf.getClassName());
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    final String file = args.length>0 ? args[0] : null;
    
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new MainForm(file).setVisible(true);
      }
    });
  }  
}
