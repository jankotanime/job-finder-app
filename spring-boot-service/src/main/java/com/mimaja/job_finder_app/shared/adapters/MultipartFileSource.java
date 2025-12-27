package com.mimaja.job_finder_app.shared.adapters;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.shared.interfaces.FileSource;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public record MultipartFileSource(@NotNull MultipartFile multipartFile) implements FileSource {
    @Override
    public InputStream getInputStream() {
        try {
            return multipartFile.getInputStream();
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION, e);
        }
    }

    @Override
    public String getOriginalFilename() {
        return Optional.ofNullable(multipartFile.getOriginalFilename())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.FILE_NAME_MISSING))
                .toLowerCase();
    }

    @Override
    public String getContentType() {
        return Optional.ofNullable(multipartFile.getContentType())
                .orElseThrow(
                        () ->
                                new ApplicationException(
                                        ApplicationExceptionReason.CONTENT_TYPE_UNKNOWN));
    }

    @Override
    public long getSize() {
        return multipartFile.getSize();
    }

    @Override
    public byte[] getBytes() {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION, e);
        }
    }
}
