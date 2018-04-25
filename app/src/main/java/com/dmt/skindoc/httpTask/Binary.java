package com.dmt.skindoc.httpTask;

import java.io.IOException;
import java.io.OutputStream;

public interface Binary {

    String getFileName();

    String getMimeType();

    long getFileLength();

    void onWriteBinary(OutputStream outputStream)throws IOException;
}
