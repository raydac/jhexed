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

import com.igormaznitsa.jhexed.extapp.Application;
import com.igormaznitsa.jhexed.swing.editor.ui.MainForm;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

  private static Application findApplication() {
    final ServiceLoader<Application> applications = ServiceLoader.load(Application.class);
    final Iterator<Application> iterator = applications.iterator();
    if (iterator.hasNext()) {
      return iterator.next();
    }
    return null;
  }

  public static void main(final String... args) {
    final Application theApplication = findApplication();

    if (theApplication != null) {
      // Start in application mode
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          final JFrame frame;
          try{
            frame = new MainForm(theApplication);
          }catch(Exception ex){
            Log.error("Error", ex);
            System.exit(-1);
            return;
          }
          frame.setVisible(true);
        }
      });
    }
    else {

      try {
        final String lookandfeel = MainForm.REGISTRY.get("lookandfeel", null);

        javax.swing.UIManager.LookAndFeelInfo landf = null;
        if (lookandfeel == null) {
          for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            final String name = info.getName().trim().toLowerCase(Locale.ENGLISH);
            if (name.startsWith("windows")) {
              landf = info;
            }
            else if (landf == null && name.contains("gtk")) {
              landf = info;
            }
          }
        }
        else {
          for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if (info.getClassName().equals(lookandfeel)) {
              landf = info;
              break;
            }
          }
        }
        if (landf != null) {
          System.out.println("Selected L&F: " + landf.getClassName());
          javax.swing.UIManager.setLookAndFeel(landf.getClassName());
        }
        else {
          System.out.println("Can't find needed L&F");
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

      final String file = args.length > 0 ? args[0] : null;

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          new MainForm(file).setVisible(true);
        }
      });
    }
  }
}
