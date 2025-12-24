package com.mimaja.job_finder_app.shared.enums;

import lombok.Getter;

@Getter
public enum FileFolderName {
    DOCUMENTS("documents"),
    PHOTOS("photos"),
    PROFILE_PHOTOS("profile-photos"),
    OFFER_PHOTO("offer-photos"),
    JOB_PHOTO("job-photos"),
    CVS("cvs");

    private final String folderName;

    FileFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFullPath(FileFolderName subFolder) {
        return folderName + "/" + subFolder.getFolderName();
    }
}
