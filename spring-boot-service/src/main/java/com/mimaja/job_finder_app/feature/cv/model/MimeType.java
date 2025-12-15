package com.mimaja.job_finder_app.feature.cv.model;

import lombok.Getter;

@Getter
public enum MimeType {
    PDF(".pdf"),
    DOCX(".docx");

    private final String extension;

    MimeType(String extension) {
        this.extension = extension;
    }
}
