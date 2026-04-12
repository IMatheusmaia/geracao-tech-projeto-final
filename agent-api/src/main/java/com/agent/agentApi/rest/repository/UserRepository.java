package com.agent.agentApi.rest.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.agent.agentApi.rest.entity.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
