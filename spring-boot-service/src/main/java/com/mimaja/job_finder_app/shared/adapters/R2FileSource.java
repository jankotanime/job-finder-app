package com.mimaja.job_finder_app.shared.adapters;

import com.mimaja.job_finder_app.core.handler.exception.ApplicationException;
import com.mimaja.job_finder_app.core.handler.exception.ApplicationExceptionReason;
import com.mimaja.job_finder_app.shared.interfaces.FileSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public record R2FileSource(
        @NotNull ResponseInputStream<GetObjectResponse> response, @NotBlank String filename)
        implements FileSource {
    @Override
    public InputStream getInputStream() {
        return response;
    }

    @Override
    public String getOriginalFilename() {
        return filename;
    }

    @Override
    public String getContentType() {
        return response.response().contentType();
    }

    @Override
    public long getSize() {
        return response.response().contentLength();
    }

    @Override
    public byte[] getBytes() {
        try {
            return response.readAllBytes();
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionReason.FILE_UPLOAD_EXCEPTION, e);
        }
    }
}
