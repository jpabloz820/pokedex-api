package com.pokedex.pokedex_api.controllers;

import com.pokedex.pokedex_api.entities.*;
import com.pokedex.pokedex_api.entities.dtos.*;
import com.pokedex.pokedex_api.repositories.PokemonRepository;
import com.pokedex.pokedex_api.services.PokemonService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<Pokemon>>> searchPokemons(@RequestBody PokemonFilterDto filter) {
        try {
            Page<Pokemon> result = pokemonService.filterPokemons(
                    filter.getName(),
                    filter.getType(),
                    filter.getPage(),
                    filter.getSize());
            if (result.isEmpty()) {
                ApiResponse<Page<Pokemon>> emptyResponse = new ApiResponse<>(
                        false,
                        "No se encontraron Pokémon con los criterios proporcionados.",
                        result);
                return ResponseEntity.ok(emptyResponse);
            }
            ApiResponse<Page<Pokemon>> response = new ApiResponse<>(
                    true,
                    "Pokémon encontrados.",
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Page<Pokemon>> errorResponse = new ApiResponse<>(
                    false,
                    "Ocurrió un error al buscar Pokémon: " + e.getMessage(),
                    null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Autowired
    private PokemonRepository pokemonRepository;

    @GetMapping(value = "/{id}", produces = { "application/json", "application/xml"})
    public ResponseEntity<?> getPokemonById(@PathVariable Long id) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            Optional<Pokemon> optionalPokemon = pokemonRepository.findById(id);
            if (optionalPokemon.isPresent()) {
                return ResponseEntity.ok(optionalPokemon.get());
            } else {
                response.setIsSuccess(false);
                response.setMessage("No se encontró ningún Pokémon con el ID: " + id);
                response.setResult(null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setMessage("Error al buscar el Pokémon: " + e.getMessage());
            response.setResult(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Pokemon>> createPokemon(@RequestBody Pokemon pokemon) {
        ApiResponse<Pokemon> response = new ApiResponse<>();
        try {
            Pokemon saved = pokemonRepository.save(pokemon);
            response.setIsSuccess(true);
            response.setMessage("Pokémon creado exitosamente.");
            response.setResult(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setMessage("Error al crear el Pokémon: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Pokemon>> updatePokemon(@PathVariable Long id,
            @RequestBody Pokemon updatedPokemon) {
        ApiResponse<Pokemon> response = new ApiResponse<>();
        try {
            Optional<Pokemon> existingOpt = pokemonRepository.findById(id);
            if (existingOpt.isPresent()) {
                Pokemon existing = existingOpt.get();
                existing.setName(updatedPokemon.getName());
                existing.setType(updatedPokemon.getType());
                existing.setDescription(updatedPokemon.getDescription());
                existing.setAbility(updatedPokemon.getAbility());
                existing.setAttack(updatedPokemon.getAttack());
                existing.setDefense(updatedPokemon.getDefense());
                existing.setSpeed(updatedPokemon.getSpeed());
                existing.setImageUrl(updatedPokemon.getImageUrl());
                existing.setLegendary(updatedPokemon.isLegendary());
                Pokemon saved = pokemonRepository.save(existing);
                response.setIsSuccess(true);
                response.setMessage("Pokémon actualizado correctamente.");
                response.setResult(saved);
                return ResponseEntity.ok(response);
            } else {
                response.setIsSuccess(false);
                response.setMessage("No se encontró el Pokémon con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setMessage("Error al actualizar el Pokémon: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePokemon(@PathVariable Long id) {
        ApiResponse<Void> response = new ApiResponse<>();
        try {
            if (pokemonRepository.existsById(id)) {
                pokemonRepository.deleteById(id);
                response.setIsSuccess(true);
                response.setMessage("Pokémon eliminado correctamente.");
                return ResponseEntity.ok(response);
            } else {
                response.setIsSuccess(false);
                response.setMessage("No se encontró el Pokémon con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setMessage("Error al eliminar el Pokémon: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
