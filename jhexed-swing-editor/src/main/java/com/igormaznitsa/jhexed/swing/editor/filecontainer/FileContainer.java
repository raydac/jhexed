package com.igormaznitsa.jhexed.swing.editor.filecontainer;

import java.io.*;
import java.util.*;
import org.apache.commons.io.IOUtils;

public class FileContainer implements Iterable<FileContainerSection> {

  private final List<FileContainerSection> sections;

  private static final int MAGIC = 0xCCAA01BB;
  private static final int FORMAT_VERSION = 0x0100;

  public FileContainer(final File file) throws IOException {
    FileInputStream in = null;
    try {
      in = new FileInputStream(file);
      this.sections = loadFromStream(in);
    }
    finally {
      IOUtils.closeQuietly(in);
    }
  }

  public FileContainer(final FileContainerSection... sections) {
    this.sections = new ArrayList<FileContainerSection>();
    this.sections.addAll(Arrays.asList(sections));
  }

  public void addSection(final FileContainerSection section) {
    if (section == null) {
      throw new NullPointerException("Section is null");
    }
    for (final FileContainerSection s : this.sections) {
      if (section.getSectionName().equals(s.getSectionName())) {
        throw new IllegalArgumentException("Duplicated section name '" + s.getSectionName() + '\'');
      }
      if (section.getUID().equals(s.getUID())) {
        throw new IllegalArgumentException("Duplicated UUID name '" + s.getUID() + '\'');
      }
    }
    this.sections.add(section);
  }

  public void write(final OutputStream out) throws IOException {
    final DataOutputStream dout = out instanceof DataOutputStream ? (DataOutputStream) out : new DataOutputStream(out);
    dout.writeInt(MAGIC);
    dout.writeShort(FORMAT_VERSION);

    dout.writeShort(this.sections.size());
    for (final FileContainerSection s : this.sections) {
      s.write(dout);
    }
    dout.writeInt(MAGIC);

    dout.flush();
  }

  private List<FileContainerSection> loadFromStream(final InputStream in) throws IOException {
    final DataInputStream din = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);

    if (din.readInt() != MAGIC) {
      throw new IOException("Wrong format, can't find magic");
    }

    final int version = din.readShort() & 0xFFFF;

    if (version > FORMAT_VERSION) {
      throw new IllegalArgumentException("Detected unsupported version [" + version + ']');
    }

    final int sectionNumber = din.readUnsignedShort();

    final List<FileContainerSection> result = new ArrayList<FileContainerSection>(Math.max(5, sectionNumber));

    for (int i = 0; i < sectionNumber; i++) {
      final FileContainerSection s = new FileContainerSection(in);
      result.add(s);
    }
    if (din.readInt() != MAGIC) {
      throw new IOException("Can't detecte the end MAGIC");
    }

    return result;
  }

  public int getSectionsNumber() {
    return this.sections.size();
  }

  public void clear() {
    this.sections.clear();
  }

  public FileContainerSection findSectionForName(final String str) {
    for (final FileContainerSection s : this.sections) {
      if (s.getSectionName().equals(str)) {
        return s;
      }
    }
    return null;
  }

  @Override
  public Iterator<FileContainerSection> iterator() {
    return this.sections.iterator();
  }

}
