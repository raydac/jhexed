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

import com.igormaznitsa.jhexed.swing.editor.model.DocumentCellComments;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

public class JavaConstantExporter implements Exporter {

  private final static String SPECSYMBOLS_REPLACE_BY_UNDERLINE = "[]{} -+()\"\'~!#$%^&*<>/\\:;|\t\r\n\b";
  
  private final DocumentOptions docOptions;
  private final SelectLayersExportData exportData;
  private final DocumentCellComments cellComments;

  public JavaConstantExporter(final DocumentOptions docOptions, final SelectLayersExportData exportData, final DocumentCellComments cellComments) {
    this.docOptions = docOptions;
    this.exportData = exportData;
    this.cellComments = cellComments;
  }

  private static String adaptTextForJava(final String text) {
    final StringBuilder buffer = new StringBuilder(text.length() * 2);

    for (final char chr : text.toCharArray()) {
      if ((chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z')) {
        buffer.append(Character.toUpperCase(chr));
      }
      else if (chr == '&') {
        buffer.append("_AND_");
      }
      else if (Character.isWhitespace(chr)) {
        buffer.append('_');
      }
      else if (chr >= '0' && chr <= '9') {
        if (buffer.length() == 0) {
          buffer.append('_');
        }
        buffer.append(chr);
      }
      else if (SPECSYMBOLS_REPLACE_BY_UNDERLINE.indexOf(chr)>=0){
        buffer.append('_');
      }
      else {
        // russian abc
        switch (Character.toLowerCase(chr)) {
          case 'а':
            buffer.append('A');
            break;
          case 'б':
            buffer.append('B');
            break;
          case 'в':
            buffer.append('V');
            break;
          case 'г':
            buffer.append('G');
            break;
          case 'д':
            buffer.append('D');
            break;
          case 'е':
            buffer.append('E');
            break;
          case 'ё':
            buffer.append('E');
            break;
          case 'ж':
            buffer.append("ZH");
            break;
          case 'з':
            buffer.append('Z');
            break;
          case 'и':
            buffer.append('I');
            break;
          case 'й':
            buffer.append('J');
            break;
          case 'к':
            buffer.append('K');
            break;
          case 'л':
            buffer.append('L');
            break;
          case 'м':
            buffer.append('M');
            break;
          case 'н':
            buffer.append('N');
            break;
          case 'о':
            buffer.append('O');
            break;
          case 'п':
            buffer.append('P');
            break;
          case 'р':
            buffer.append('R');
            break;
          case 'с':
            buffer.append('S');
            break;
          case 'т':
            buffer.append('T');
            break;
          case 'у':
            buffer.append('U');
            break;
          case 'ф':
            buffer.append('F');
            break;
          case 'х':
            buffer.append('H');
            break;
          case 'ц':
            buffer.append("TS");
            break;
          case 'ч':
            buffer.append("CH");
            break;
          case 'ш':
            buffer.append("SH");
            break;
          case 'щ':
            buffer.append("SHCH");
            break;
          case 'ъ':
            buffer.append("");
            break;
          case 'ы':
            buffer.append('Y');
            break;
          case 'ь':
            buffer.append("");
            break;
          case 'э':
            buffer.append('E');
            break;
          case 'ю':
            buffer.append("YU");
            break;
          case 'я':
            buffer.append("YA");
            break;
          default:
            throw new ExportException("Can't recognize char '"+chr+"' in the word '" + text + "\' to convert into latin");
        }
      }
    }

    return buffer.toString();
  }

  private static void printTab(final StringBuilder buffer) {
    buffer.append("    ");
  }

  private static void nextLine(final StringBuilder buffer) {
    buffer.append(System.lineSeparator());
  }

  private static void endLineComment(final StringBuilder buffer, final String text) {
    buffer.append("// ").append(text.replace('\n', ' '));
    nextLine(buffer);
  }

  private static void commentLine(final StringBuilder buffer, final String text) {
    buffer.append(" * ").append(text);
    nextLine(buffer);
  }

  @Override
  public void export(final File file) throws IOException {
    final StringBuilder buffer = new StringBuilder(16384);

    final Set<String> processedLayerNames = new HashSet<String>();
    final Set<String> processedConstantNames = new HashSet<String>();

    int layerIndex = -1;

    final String className = FilenameUtils.getBaseName(file.getName());

    buffer.append("/**");
    nextLine(buffer);
    commentLine(buffer, this.docOptions.getCommentary().replace('\n', ' '));
    commentLine(buffer, "");
    commentLine(buffer, "Generated by the JHexedSwingEditor");
    commentLine(buffer, "The Project page : https://code.google.com/p/jhexed/");
    commentLine(buffer, SimpleDateFormat.getInstance().format(new Date()));
    buffer.append(" */");
    nextLine(buffer);

    buffer.append("public interface ").append(className).append(" {");
    nextLine(buffer);

    printTab(buffer);buffer.append("public static final int HEX_EMPTY = 0; // The Empty hex value");
    nextLine(buffer);
    nextLine(buffer);
    
    for (final LayerExportRecord r : exportData.getLayers()) {
      layerIndex++;

      if (r.isAllowed()) {
        final String layerName = r.getLayer().getLayerName().trim();
        final String layerComment = r.getLayer().getComments().trim();
        final String preparedLayerName;
        if (layerName.isEmpty()) {
          preparedLayerName = "L"+Integer.toString(layerIndex);
        }
        else {
          preparedLayerName = adaptTextForJava(layerName);
        }

        if (processedLayerNames.contains(preparedLayerName)) {
          throw new ExportException("Can't make export because there is duplicated identifier for layer '" + layerName + "' (" + preparedLayerName + ')');
        }
        processedLayerNames.add(preparedLayerName);

        printTab(buffer);
        endLineComment(buffer, "Layer " + layerIndex);
        if (!layerComment.isEmpty()) {
          printTab(buffer);
          endLineComment(buffer, layerComment);
        }

        printTab(buffer);
        buffer.append("public static final int LAYER_").append(preparedLayerName).append(" = ").append(layerIndex).append(';');
        nextLine(buffer);
        nextLine(buffer);
        
        for (int e = 1; e < r.getLayer().getHexValuesNumber(); e++) {
          final HexFieldValue value = r.getLayer().getHexValueForIndex(e);
          final String valueName = value.getName().trim();
          final String valueComment = value.getComment();

          final String preparedValueName;
          if (valueName.isEmpty()) {
            preparedValueName = "VALUE" + e;
          }
          else {
            preparedValueName = adaptTextForJava(valueName);
          }

          final String fullName = preparedLayerName + '_' + preparedValueName;
          if (processedConstantNames.contains(fullName)) {
            throw new ExportException("Can't make export because there is duplicated identifier for layer value '" + valueName + "\' (" + fullName + ')');
          }
          processedConstantNames.add(fullName);
        
          printTab(buffer);
          buffer.append("public static final int ").append(fullName).append(" = ").append(e).append("; ");
          endLineComment(buffer, valueComment);
        }
        nextLine(buffer);
      }
    }
    buffer.append('}');
    nextLine(buffer);
    
    Writer writer = null;
    try {
      writer = new BufferedWriter(new FileWriterWithEncoding(file, "UTF-8", false));
      writer.write(buffer.toString());
      writer.flush();
    }
    finally {
      IOUtils.closeQuietly(writer);
    }
  }

}
