package com.accreditationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.accreditationservice.entity.Accreditation;

@Repository
public interface AccreditationRepository extends JpaRepository<Accreditation, Long> {
}
