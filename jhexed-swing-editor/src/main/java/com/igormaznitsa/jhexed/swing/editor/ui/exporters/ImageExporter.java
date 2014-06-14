/*
 * Copyright 2014 Igor Maznitsa.
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

package com.igormaznitsa.jhexed.swing.editor.ui.exporters;

import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import java.io.*;

/**
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class ImageExporter implements Exporter {
  private final DocumentOptions docOptions;
  private final DialogSelectLayersForExport.SelectLayersExportData exportData;
  
  public ImageExporter(final DocumentOptions docOptions, final DialogSelectLayersForExport.SelectLayersExportData exportData){
    this.docOptions = docOptions;
    this.exportData = exportData;
  }
  
  @Override
  public void export(final File file) throws IOException {
    
  }
}
