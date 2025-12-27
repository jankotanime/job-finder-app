package com.mimaja.job_finder_app.shared.interfaces;

import java.io.InputStream;

public interface FileSource {
    InputStream getInputStream();

    String getOriginalFilename();

    String getContentType();

    long getSize();

    byte[] getBytes();
}
