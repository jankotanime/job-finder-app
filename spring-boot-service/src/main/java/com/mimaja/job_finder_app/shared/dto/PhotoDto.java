package com.mimaja.job_finder_app.shared.dto;

import com.mimaja.job_finder_app.shared.model.Photo;

public record PhotoDto(String name, String mimeType, byte[] data) {
    public static PhotoDto from(Photo photo) {
        if (photo == null) {
            return null;
        }
        return new PhotoDto(photo.getName(), photo.getMimeType(), photo.getData());
    }
}
