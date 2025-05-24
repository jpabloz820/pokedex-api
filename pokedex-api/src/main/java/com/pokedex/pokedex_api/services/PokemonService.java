package com.pokedex.pokedex_api.services;

import java.util.List;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import com.pokedex.pokedex_api.entities.Pokemon;
import org.springframework.data.domain.PageRequest;
import com.pokedex.pokedex_api.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PokemonService {

    @Autowired
    private PokemonRepository pokemonRepository;

    public Page<Pokemon> filterPokemons(String name, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); //Comienza desde la pagina cero 
        return pokemonRepository.findAll((root, query, cb) -> {  //CB es un criterio de busqueda del JPA
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (type != null && !type.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("type")), "%" + type.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}