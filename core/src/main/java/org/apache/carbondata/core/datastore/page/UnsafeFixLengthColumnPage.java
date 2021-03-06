/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.carbondata.core.datastore.page;

import java.math.BigDecimal;

import org.apache.carbondata.core.datastore.compression.Compressor;
import org.apache.carbondata.core.memory.CarbonUnsafe;
import org.apache.carbondata.core.memory.MemoryBlock;
import org.apache.carbondata.core.memory.MemoryException;
import org.apache.carbondata.core.memory.UnsafeMemoryManager;
import org.apache.carbondata.core.metadata.datatype.DataType;

import static org.apache.carbondata.core.metadata.datatype.DataType.BYTE;

// This extension uses unsafe memory to store page data, for fix length data type only (byte,
// short, integer, long, float, double)
public class UnsafeFixLengthColumnPage extends ColumnPage {
  // memory allocated by Unsafe
  private MemoryBlock memoryBlock;

  // base address of memoryBlock
  private Object baseAddress;

  // base offset of memoryBlock
  private long baseOffset;

  private static final int byteBits = BYTE.getSizeBits();
  private static final int shortBits = DataType.SHORT.getSizeBits();
  private static final int intBits = DataType.INT.getSizeBits();
  private static final int longBits = DataType.LONG.getSizeBits();
  private static final int floatBits = DataType.FLOAT.getSizeBits();
  private static final int doubleBits = DataType.DOUBLE.getSizeBits();

  UnsafeFixLengthColumnPage(DataType dataType, int pageSize) throws MemoryException {
    super(dataType, pageSize);
    switch (dataType) {
      case BYTE:
      case SHORT:
      case INT:
      case LONG:
      case FLOAT:
      case DOUBLE:
        int size = pageSize << dataType.getSizeBits();
        memoryBlock = UnsafeMemoryManager.allocateMemoryWithRetry(size);
        baseAddress = memoryBlock.getBaseObject();
        baseOffset = memoryBlock.getBaseOffset();
        break;
      case DECIMAL:
      case STRING:
        throw new UnsupportedOperationException("invalid data type: " + dataType);
    }
  }

  @Override
  public void putByte(int rowId, byte value) {
    long offset = rowId << byteBits;
    CarbonUnsafe.unsafe.putByte(baseAddress, baseOffset + offset, value);
  }

  @Override
  public void putShort(int rowId, short value) {
    long offset = rowId << shortBits;
    CarbonUnsafe.unsafe.putShort(baseAddress, baseOffset + offset, value);
  }

  @Override
  public void putInt(int rowId, int value) {
    long offset = rowId << intBits;
    CarbonUnsafe.unsafe.putInt(baseAddress, baseOffset + offset, value);
  }

  @Override
  public void putLong(int rowId, long value) {
    long offset = rowId << longBits;
    CarbonUnsafe.unsafe.putLong(baseAddress, baseOffset + offset, value);
  }

  @Override
  public void putDouble(int rowId, double value) {
    long offset = rowId << doubleBits;
    CarbonUnsafe.unsafe.putDouble(baseAddress, baseOffset + offset, value);
  }

  @Override
  public void putBytes(int rowId, byte[] bytes) {
    throw new UnsupportedOperationException("invalid data type: " + dataType);
  }

  @Override
  public void putBytes(int rowId, byte[] bytes, int offset, int length) {
    throw new UnsupportedOperationException("invalid data type: " + dataType);
  }

  @Override
  public byte getByte(int rowId) {
    long offset = rowId << byteBits;
    return CarbonUnsafe.unsafe.getByte(baseAddress, baseOffset + offset);
  }

  @Override
  public short getShort(int rowId) {
    long offset = rowId << shortBits;
    return CarbonUnsafe.unsafe.getShort(baseAddress, baseOffset + offset);
  }

  @Override
  public int getInt(int rowId) {
    long offset = rowId << intBits;
    return CarbonUnsafe.unsafe.getInt(baseAddress, baseOffset + offset);
  }

  @Override
  public long getLong(int rowId) {
    long offset = rowId << longBits;
    return CarbonUnsafe.unsafe.getLong(baseAddress, baseOffset + offset);
  }

  @Override
  public float getFloat(int rowId) {
    long offset = rowId << floatBits;
    return CarbonUnsafe.unsafe.getFloat(baseAddress, baseOffset + offset);
  }

  @Override
  public double getDouble(int rowId) {
    long offset = rowId << doubleBits;
    return CarbonUnsafe.unsafe.getDouble(baseAddress, baseOffset + offset);
  }

  @Override
  public BigDecimal getDecimal(int rowId) {
    throw new UnsupportedOperationException("invalid data type: " + dataType);
  }

  @Override
  public byte[] getBytePage() {
    byte[] data = new byte[getPageSize()];
    for (int i = 0; i < data.length; i++) {
      long offset = i << byteBits;
      data[i] = CarbonUnsafe.unsafe.getByte(baseAddress, baseOffset + offset);
    }
    return data;
  }

  @Override
  public short[] getShortPage() {
    short[] data = new short[getPageSize()];
    for (int i = 0; i < data.length; i++) {
      long offset = i << shortBits;
      data[i] = CarbonUnsafe.unsafe.getShort(baseAddress, baseOffset + offset);
    }
    return data;
  }

  @Override
  public int[] getIntPage() {
    int[] data = new int[getPageSize()];
    for (int i = 0; i < data.length; i++) {
      long offset = i << intBits;
      data[i] = CarbonUnsafe.unsafe.getInt(baseAddress, baseOffset + offset);
    }
    return data;
  }

  @Override
  public long[] getLongPage() {
    long[] data = new long[getPageSize()];
    for (int i = 0; i < data.length; i++) {
      long offset = i << longBits;
      data[i] = CarbonUnsafe.unsafe.getLong(baseAddress, baseOffset + offset);
    }
    return data;
  }

  @Override
  public float[] getFloatPage() {
    float[] data = new float[getPageSize()];
    for (int i = 0; i < data.length; i++) {
      long offset = i << floatBits;
      data[i] = CarbonUnsafe.unsafe.getFloat(baseAddress, baseOffset + offset);
    }
    return data;
  }

  @Override
  public double[] getDoublePage() {
    double[] data = new double[getPageSize()];
    for (int i = 0; i < data.length; i++) {
      long offset = i << doubleBits;
      data[i] = CarbonUnsafe.unsafe.getDouble(baseAddress, baseOffset + offset);
    }
    return data;
  }

  @Override
  public byte[][] getByteArrayPage() {
    throw new UnsupportedOperationException("invalid data type: " + dataType);
  }

  @Override
  public byte[] getFlattenedBytePage() {
    throw new UnsupportedOperationException("invalid data type: " + dataType);
  }

  @Override
  public void setBytePage(byte[] byteData) {
    for (int i = 0; i < byteData.length; i++) {
      long offset = i << byteBits;
      CarbonUnsafe.unsafe.putByte(baseAddress, baseOffset + offset, byteData[i]);
    }
  }

  @Override
  public void setShortPage(short[] shortData) {
    for (int i = 0; i < shortData.length; i++) {
      long offset = i << shortBits;
      CarbonUnsafe.unsafe.putShort(baseAddress, baseOffset + offset, shortData[i]);
    }
  }

  @Override
  public void setIntPage(int[] intData) {
    for (int i = 0; i < intData.length; i++) {
      long offset = i << intBits;
      CarbonUnsafe.unsafe.putInt(baseAddress, baseOffset + offset, intData[i]);
    }
  }

  @Override
  public void setLongPage(long[] longData) {
    for (int i = 0; i < longData.length; i++) {
      long offset = i << longBits;
      CarbonUnsafe.unsafe.putLong(baseAddress, baseOffset + offset, longData[i]);
    }
  }

  @Override
  public void setFloatPage(float[] floatData) {
    for (int i = 0; i < floatData.length; i++) {
      long offset = i << floatBits;
      CarbonUnsafe.unsafe.putFloat(baseAddress, baseOffset + offset, floatData[i]);
    }
  }

  @Override
  public void setDoublePage(double[] doubleData) {
    for (int i = 0; i < doubleData.length; i++) {
      long offset = i << doubleBits;
      CarbonUnsafe.unsafe.putDouble(baseAddress, baseOffset + offset, doubleData[i]);
    }
  }

  @Override
  public void setByteArrayPage(byte[][] byteArray) {
    throw new UnsupportedOperationException("invalid data type: " + dataType);
  }

  public void freeMemory() {
    if (memoryBlock != null) {
      UnsafeMemoryManager.INSTANCE.freeMemory(memoryBlock);
      memoryBlock = null;
      baseAddress = null;
      baseOffset = 0;
    }
  }

  @Override
  public void encode(PrimitiveCodec codec) {
    int pageSize = getPageSize();
    switch (dataType) {
      case BYTE:
        for (int i = 0; i < pageSize; i++) {
          long offset = i << byteBits;
          codec.encode(i, CarbonUnsafe.unsafe.getByte(baseAddress, baseOffset + offset));
        }
        break;
      case SHORT:
        for (int i = 0; i < pageSize; i++) {
          long offset = i << shortBits;
          codec.encode(i, CarbonUnsafe.unsafe.getShort(baseAddress, baseOffset + offset));
        }
        break;
      case INT:
        for (int i = 0; i < pageSize; i++) {
          long offset = i << intBits;
          codec.encode(i, CarbonUnsafe.unsafe.getInt(baseAddress, baseOffset + offset));
        }
        break;
      case LONG:
        for (int i = 0; i < pageSize; i++) {
          long offset = i << longBits;
          codec.encode(i, CarbonUnsafe.unsafe.getLong(baseAddress, baseOffset + offset));
        }
        break;
      case FLOAT:
        for (int i = 0; i < pageSize; i++) {
          long offset = i << floatBits;
          codec.encode(i, CarbonUnsafe.unsafe.getFloat(baseAddress, baseOffset + offset));
        }
        break;
      case DOUBLE:
        for (int i = 0; i < pageSize; i++) {
          long offset = i << doubleBits;
          codec.encode(i, CarbonUnsafe.unsafe.getDouble(baseAddress, baseOffset + offset));
        }
        break;
      default:
        throw new UnsupportedOperationException("invalid data type: " + dataType);
    }
  }

  @Override
  public byte[] compress(Compressor compressor) {
    // TODO: use zero-copy raw compression
    return super.compress(compressor);
  }

}