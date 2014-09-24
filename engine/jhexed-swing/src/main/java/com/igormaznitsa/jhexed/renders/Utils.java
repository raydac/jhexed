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
package com.igormaznitsa.jhexed.renders;

import com.igormaznitsa.jhexed.engine.HexEngine;
import com.igormaznitsa.jhexed.engine.misc.HexPoint2D;
import java.awt.geom.Path2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public final class Utils {
  private Utils(){
    
  }
  
  public static byte [] packByteArray(final byte [] array) throws IOException {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(Math.max(512,array.length/2));
    
    final DataOutputStream dout = new DataOutputStream(buffer);
    dout.writeInt(array.length);
    final DeflaterOutputStream zout = new DeflaterOutputStream(dout);
    zout.write(array, 0, array.length);
    zout.finish();
    zout.flush();
    
    return buffer.toByteArray();
  }
  
  public static byte [] unpackArray(final byte [] packedArray) throws IOException {
    final DataInputStream din = new DataInputStream(new ByteArrayInputStream(packedArray));
    
    final int unpackedLength = din.readInt();
    
    final byte [] result = new byte[unpackedLength];

    final InflaterInputStream zin = new InflaterInputStream(din);
    
    int len = unpackedLength;
    int pos = 0;
    while(len>0){
      final int read = zin.read(result, pos, len);
      len -= read;
      pos += read;
    }
    
    return result;
  }

  public static Path2D getHexShapeAsPath(final HexEngine<?> engine, final boolean allowScaling){
    final HexPoint2D[] points = engine.getHexPoints();

    final Path2D path = new Path2D.Float();
    if (allowScaling){
      path.moveTo(points[0].getX() * engine.getScaleX(), points[0].getY() * engine.getScaleY());
      path.lineTo(points[1].getX() * engine.getScaleX(), points[1].getY() * engine.getScaleY());
      path.lineTo(points[2].getX() * engine.getScaleX(), points[2].getY() * engine.getScaleY());
      path.lineTo(points[3].getX() * engine.getScaleX(), points[3].getY() * engine.getScaleY());
      path.lineTo(points[4].getX() * engine.getScaleX(), points[4].getY() * engine.getScaleY());
      path.lineTo(points[5].getX() * engine.getScaleX(), points[5].getY() * engine.getScaleY());
    }else{
      path.moveTo(points[0].getX(), points[0].getY());
      path.lineTo(points[1].getX(), points[1].getY());
      path.lineTo(points[2].getX(), points[2].getY());
      path.lineTo(points[3].getX(), points[3].getY());
      path.lineTo(points[4].getX(), points[4].getY());
      path.lineTo(points[5].getX(), points[5].getY());
    }   
      
   path.closePath();
      

    return path;
  }
}
