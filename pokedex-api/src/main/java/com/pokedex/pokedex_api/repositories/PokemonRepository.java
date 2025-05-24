package com.pokedex.pokedex_api.repositories;

import com.pokedex.pokedex_api.entities.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PokemonRepository extends JpaRepository<Pokemon, Long>,JpaSpecificationExecutor<Pokemon> { }
