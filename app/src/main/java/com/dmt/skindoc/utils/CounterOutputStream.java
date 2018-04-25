package com.dmt.skindoc.utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

public class CounterOutputStream extends OutputStream {

    //线程安全
    private AtomicLong mAtomicLong=new AtomicLong();
    public CounterOutputStream() {
        super();
    }

    /**
     * 获取长度
     * @return
     */
    public long get()
    {
        return mAtomicLong.get();
    }
    public void write(long b)throws IOException
    {
        mAtomicLong.addAndGet(b);
    }
    @Override
    public void write(int b) throws IOException {
        mAtomicLong.addAndGet(1);
    }

    @Override
    public void write(@NonNull byte[] b) throws IOException {
        mAtomicLong.addAndGet(b.length);
    }

    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        mAtomicLong.addAndGet(len);
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
