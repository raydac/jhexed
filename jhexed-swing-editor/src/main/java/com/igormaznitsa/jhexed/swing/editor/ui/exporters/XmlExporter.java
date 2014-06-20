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
package com.igormaznitsa.jhexed.swing.editor.ui.exporters;

import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.values.HexColorValue;
import com.igormaznitsa.jhexed.values.HexSVGImageValue;
import com.igormaznitsa.jhexed.values.HexFieldValue;
import com.igormaznitsa.jhexed.swing.editor.ui.Utils;
import com.igormaznitsa.jhexed.swing.editor.ui.dialogs.*;
import java.awt.Color;
import java.io.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class XmlExporter implements Exporter {
  private final DocumentOptions docOptions;
  private final SelectLayersExportData exportData;
  
  public XmlExporter(final DocumentOptions docOptions, final SelectLayersExportData exportData){
    this.docOptions = docOptions;
    this.exportData = exportData;
  }
  
  @Override
  public void export(final File file) throws IOException {
    final StringBuilder buffer = new StringBuilder(256000);
    
    buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    buffer.append("<jhexed>\n");
    
    buffer.append("\t<docOptions>\n");
    buffer.append("\t\t<columns>").append(this.docOptions.getColumns()).append("</columns>\n");
    buffer.append("\t\t<rows>").append(this.docOptions.getRows()).append("</rows>\n");
    buffer.append("\t\t<commentary>").append(StringEscapeUtils.escapeXml10(this.docOptions.getCommentary())).append("</commentary>\n");
    buffer.append("\t\t<hexBorderWidth>").append(this.docOptions.getLineWidth()).append("</hexBorderWidth>\n");
    buffer.append("\t\t<hexBorderColor>").append(Utils.color2html(this.docOptions.getColor(),false)).append("</hexBorderColor>\n");
    buffer.append("\t\t<hexOrientation>").append(this.docOptions.getHexOrientation() == HexEngine.ORIENTATION_VERTICAL ? "vertical" : "horizontal").append("</hexOrientation>\n");
    buffer.append("\t</docOptions>\n");
    if (exportData.isBackgroundImageExport() && this.docOptions.getImage() != null){
      buffer.append("\t<backImage>").append(Utils.byteArray2String(this.docOptions.getImage().getImageData(), true, true)).append("</backImage>\n");
    }
    buffer.append("\t<layers>\n");
    for(final LayerExportRecord f : exportData.getLayers()){
      if (f.isAllowed()){
        buffer.append("\t\t<layer name=\"").append(StringEscapeUtils.escapeXml10(f.getLayer().getLayerName())).append("\" commentary=\"").append(StringEscapeUtils.escapeXml10(f.getLayer().getComments())).append("\">\n");
        buffer.append("\t\t\t<values>\n");
        for(int i=1; i<f.getLayer().getHexValuesNumber();i++){
          final HexFieldValue vl = f.getLayer().getHexValueForIndex(i);
          buffer.append("\t\t\t\t<value index=\"").append(i).append("\" name=\"").append(StringEscapeUtils.escapeXml10(vl.getName())).append("\" commentary=\"").append(StringEscapeUtils.escapeXml10(vl.getComment())).append("\">\n");
          if (vl instanceof HexColorValue){
            final Color clr = ((HexColorValue)vl).getColor();
            buffer.append("\t\t\t\t\t<color color=\"").append(Utils.color2html(clr,true)).append("\"/>\n");
          }else if (vl instanceof HexSVGImageValue){
            buffer.append("\t\t\t\t\t<svg>").append(Utils.byteArray2String(((HexSVGImageValue)vl).getImage().getImageData(), true, true)).append("</svg>\n");
          }
          buffer.append("\t\t\t\t</value>\n");
        }
        buffer.append("\t\t\t</values>\n");
        buffer.append("\t\t\t<array>").append(Utils.byteArray2String(f.getLayer().getArray(), true, true)).append("</array>\n");
        buffer.append("\t\t</layer>\n");
      }
    }
    buffer.append("\t</layers>\n");
    buffer.append("</jhexed>\n");
    
    FileUtils.write(file, buffer.toString());
  }
}
