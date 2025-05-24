package com.pokedex.pokedex_api.repositories;

import java.util.Optional;
import com.pokedex.pokedex_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username);
}
