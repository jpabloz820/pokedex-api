package com.pokedex.pokedex_api.repositories;

import java.util.Optional;
import com.pokedex.pokedex_api.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>
{
    Optional<Role> findByName(String name);
}
