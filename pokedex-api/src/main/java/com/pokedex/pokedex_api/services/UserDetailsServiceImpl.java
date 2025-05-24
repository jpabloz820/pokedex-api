package com.pokedex.pokedex_api.services;

import com.pokedex.pokedex_api.entities.User;
import org.springframework.stereotype.Service;
import com.pokedex.pokedex_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService { //Se cargan los servicios

    @Autowired //Aturoiza una instancia
    private UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new UserDetailsImpl(user);
    }
}
