package com.mimaja.job_finder_app.feature.unit.contract.mockdata;

import com.mimaja.job_finder_app.feature.contract.enums.ContractStatus;
import com.mimaja.job_finder_app.feature.contract.model.Contract;
import com.mimaja.job_finder_app.feature.job.model.Job;
import com.mimaja.job_finder_app.feature.job.model.JobStatus;
import com.mimaja.job_finder_app.feature.offer.model.Offer;
import com.mimaja.job_finder_app.feature.user.model.User;
import java.util.UUID;

public class ContractMockData {
    public static User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    public static Offer createTestOffer(User owner, User candidate) {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID());
        offer.setOwner(owner);
        offer.setChosenCandidate(candidate);
        offer.setContract(null);
        return offer;
    }

    public static Contract createTestContract(User owner, User candidate) {
        Contract contract = new Contract();
        contract.setId(UUID.randomUUID());
        contract.setOffer(createTestOffer(owner, candidate));
        contract.setJob(createTestJob(owner, candidate));
        contract.setContractorAcceptance(ContractStatus.WAITING);
        return contract;
    }

    public static Job createTestJob(User owner, User contractor) {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setStatus(JobStatus.READY);
        job.setOwner(owner);
        job.setContractor(contractor);
        return job;
    }
}
