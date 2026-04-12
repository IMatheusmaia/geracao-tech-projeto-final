package com.agent.agentApi.rest.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agent.agentApi.rest.entity.UserEntity;
import com.agent.agentApi.rest.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }

    public UserEntity save(UserEntity user) {
        return repository.save(user);
    }

    public UserEntity findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
