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
