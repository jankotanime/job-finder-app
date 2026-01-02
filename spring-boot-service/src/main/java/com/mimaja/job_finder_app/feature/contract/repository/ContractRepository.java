package com.mimaja.job_finder_app.feature.contract.repository;

import com.mimaja.job_finder_app.feature.contract.model.Contract;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, UUID> {}
