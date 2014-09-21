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
import javax.swing.SwingUtilities;

public class Main {
  public static void main(final String... args) {
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
        System.out.println("Found UI: "+info.getName());
        if ("Windows".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }
    catch (Exception ex) {
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
