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
}
