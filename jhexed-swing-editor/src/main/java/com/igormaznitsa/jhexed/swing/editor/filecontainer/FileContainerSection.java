package com.igormaznitsa.jhexed.swing.editor.filecontainer;

import java.io.*;
import java.util.UUID;

public class FileContainerSection {
    private final String sectionName;
    private byte [] sectionData;
    private final UUID uid;

    public FileContainerSection(final InputStream in) throws IOException {
      final DataInputStream din = in instanceof DataInputStream ? (DataInputStream)in : new DataInputStream(in);
      this.uid = UUID.fromString(din.readUTF());
      this.sectionName = din.readUTF();
      this.sectionData = new byte[din.readInt()];
      din.readFully(this.sectionData);
    }
    
    public FileContainerSection(final String sectionName, final byte [] sectionData){
      if (sectionName == null) throw new NullPointerException("Name is null");
      if (sectionData == null) throw new NullPointerException("Data is null");
      this.sectionName = sectionName;
      this.sectionData = sectionData;
      this.uid = UUID.randomUUID();
    }
    
    public void write(final OutputStream out) throws IOException {
      final DataOutputStream dout = out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);
      dout.writeUTF(this.uid.toString());
      dout.writeUTF(this.sectionName);
      dout.writeInt(this.sectionData.length);
      dout.write(this.sectionData);
    }
    
    public UUID getUID(){
      return this.uid;
    }
    
    public String getSectionName(){
      return this.sectionName;
    }
    
    public byte [] getData(){
      return this.sectionData;
    }
    
    public void setData(final byte [] data){
      if (data == null) throw new NullPointerException("Data is null");
      this.sectionData = data;
    }
    
}
