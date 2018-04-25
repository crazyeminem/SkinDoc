package com.dmt.skindoc.httpTask;

import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileBinary implements  Binary {

    private File file;

    public FileBinary(File file) {
        if (file==null||!file.exists())
            throw new IllegalArgumentException("File Not Found!");


        this.file = file;
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public String getMimeType() {
        String mimeType="application/octet-stream";
            String extension= MimeTypeMap.getFileExtensionFromUrl(file.getName());
            if (extension!=null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }


        return mimeType;
    }

    @Override
    public long getFileLength() {
        return file.length();
    }

    @Override
    public void onWriteBinary(OutputStream outputStream) throws IOException {
        InputStream inputStream=new FileInputStream(file);
        byte[] buffer=new byte[2048];
        int len;
        while((len=inputStream.read(buffer))!=-1)
        {
            outputStream.write(buffer,0,len);
        }

    }
}
