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
package com.igormaznitsa.jhexed.swing.editor.model;

import com.igormaznitsa.jhexed.engine.misc.HexPosition;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Igor Maznitsa (http://www.igormaznitsa.com)
 */
public class DocumentCellComments {

  private final Map<HexPosition, String> storage = new HashMap<HexPosition, String>();

  public DocumentCellComments() {

  }

  
  public boolean isEmpty() {
    return this.storage.isEmpty();
  }

  public Iterator<Entry<HexPosition, String>> iterator() {
    return this.storage.entrySet().iterator();
  }

  public void clear() {
    this.storage.clear();
  }

  public String getForHex(final HexPosition position) {
    String result = null;
    if (position != null) {
      result = this.storage.get(position);
    }
    return result;
  }

  public void setForHex(final HexPosition position, final String comment) {
    if (position != null) {
      if (comment == null || comment.isEmpty()) {
        this.storage.remove(position);
      }
      else {
        this.storage.put(position, comment);
      }
    }
  }

  public void fromByteArray(final byte[] data) throws IOException {
    final DataInputStream inStream = new DataInputStream(new ByteArrayInputStream(data));
    this.storage.clear();
    final int number = inStream.readInt();
    for(int i=0;i<number;i++){
      final long packed = inStream.readLong();
      final String text = inStream.readUTF();
      this.storage.put(new HexPosition((int)packed, (int)(packed>>>32)), text);
    }
  }

  public byte[] toByteArray() throws IOException {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream(16384);
    final DataOutputStream out = new DataOutputStream(buffer);
    
    out.writeInt(this.storage.size());
    for(final Entry<HexPosition,String> item : this.storage.entrySet()){
      final long packed = ((long)item.getKey().getRow()<<32)|((long)item.getKey().getColumn() & 0xFFFFFFFFL);
      out.writeLong(packed);
      out.writeUTF(item.getValue());
    }
    
    out.flush();
    out.close();
    return buffer.toByteArray();
  }
  
  
}
