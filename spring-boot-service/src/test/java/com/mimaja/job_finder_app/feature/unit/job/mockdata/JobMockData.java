package com.mimaja.job_finder_app.feature.unit.job.mockdata;

import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcher;
import com.mimaja.job_finder_app.feature.job.jobDispatcher.model.JobDispatcherIssueStatus;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.shared.dto.ProcessedFileDetails;
import com.mimaja.job_finder_app.shared.enums.MimeType;
import java.util.HashSet;
import java.util.UUID;

public class JobMockData {
    public static final String TEST_STORAGE_KEY = "example-key";
    public static final String TEST_FILE_NAME = "example.jpg";
    public static final String TEST_FILE_DESCRIPTION = "example";
    public static final int TEST_FILE_SIZE = 1024;

    public static Job createTestJob() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setStatus(JobStatus.READY);
        return job;
    }

    public static JobDispatcher createTestJobDispatcher() {
        JobDispatcher dispatcher = new JobDispatcher();
        dispatcher.setIssueStatusOwner(JobDispatcherIssueStatus.NONE);
        dispatcher.setIssueStatusContractor(JobDispatcherIssueStatus.NONE);
        dispatcher.setContractiorApprovals(new HashSet<>());
        return dispatcher;
    }

    public static Offer createTestOffer() {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setPhoto(null);
        return offer;
    }

    public static ProcessedFileDetails createTestFileDetails() {
        return new ProcessedFileDetails(
                TEST_STORAGE_KEY,
                TEST_FILE_NAME,
                MimeType.JPG,
                TEST_FILE_DESCRIPTION,
                TEST_FILE_SIZE,
                null);
    }
}
