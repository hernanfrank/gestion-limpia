package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.repositories.IConfigRepository;
import com.burbujas.gestionlimpia.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service("ConfigServiceImpl")
public class ConfigServiceImpl implements IConfigService{
    private final IConfigRepository configRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public ConfigServiceImpl(IConfigRepository configRepository, BCryptPasswordEncoder passwordEncoder, InMemoryUserDetailsManager userDetailsManager, AuthenticationProvider authenticationProvider) {
        this.configRepository = configRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
        this.authenticationProvider = authenticationProvider;

    }

    @Override
    public Config findById(Long id) {
        return this.configRepository.findById(id).orElse(null);
    }

    @Override
    public boolean verificarClaveAcceso(String claveIngresada) {
        Config config = configRepository.findById(1L).orElse(null);
        if (config == null) {
            throw new IllegalArgumentException("Configuración de administración no encontrada");
        }

        // chequea que sean iguales si viene en texto plano o si viene ya encriptada
        return passwordEncoder.matches(claveIngresada, config.getClaveAcceso()) || config.getClaveAcceso().equals(claveIngresada);
    }

    @Override
    public int cambiarEmail(String emailActual, String emailNuevo){
        try {
            // validamos el nuevo email
            if (!Validator.validarEmail(emailNuevo)) {
                return 3; // no pasa la validacion
            }
            if(emailActual.equals(emailNuevo)){
                return 4;
            }

            Config config = this.configRepository.findById(1L).orElse(null);
            if (config == null) {
                return 0;
            }
            if (config.getEmailAcceso().equals(emailActual)) {
                config.setEmailAcceso(emailNuevo);
                this.configRepository.save(config);
                this.actualizarUsuarioEnMemoria(emailActual, emailNuevo, config.getClaveAcceso());
            } else {
                return 2;
            }
        } catch (Exception e) {
            System.out.println("ConfigServiceImpl: Excepción capturada al cambiar el email: " + e);
            throw e;
        }
        return 1;

    }

    private void actualizarUsuarioEnMemoria(String emailActual, String emailNuevo, String claveAcceso) {
       // creo el nuevo usuario con la misma clave y el email nuevo
        UserDetails updatedUser = User.builder()
                .username(emailNuevo)
                .password(claveAcceso)// ya viene encriptada de la bdd
                .roles("USER")
                .build();
        this.userDetailsManager.createUser(updatedUser);

        // elimino el user anterior
        this.userDetailsManager.deleteUser(emailActual);
    }

    @Override
    public void save(Config config) {
        this.configRepository.save(config);
    }
}
