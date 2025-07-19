package org.example.insuranceapi.repository;


import org.example.insuranceapi.model.Offer;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsuranceRepository extends ListCrudRepository<Offer, Long> {


    Optional<Offer> findById(Long id);
}
