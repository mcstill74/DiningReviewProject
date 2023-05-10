package com.example.diningReviewProject.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.diningReviewProject.entities.Reviewer;

public interface UserRepository extends CrudRepository<Reviewer, Long> {
    Boolean existsByUserName(String userName);
    Optional<Reviewer> findByUserName(String userName);
    Optional<Reviewer> findByName(String name);
    Boolean existsByName(String submitter);
}
