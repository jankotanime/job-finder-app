package com.mimaja.job_finder_app.shared.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank private String name;

    @Column(name = "mime_type")
    @NotBlank
    private String mimeType;

    @Column(columnDefinition = "bytea")
    @NotNull private byte[] data;

    public Photo(byte[] data, String mimeType, String name) {
        this.data = Arrays.copyOf(data, data.length);
        this.mimeType = mimeType;
        this.name = name;
    }
}
