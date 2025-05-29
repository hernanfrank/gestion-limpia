package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.ClaveResetToken;
import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.repositories.IClaveResetTokenRepository;
import com.burbujas.gestionlimpia.utils.Validator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service("ClaveResetServiceImpl")
@Transactional
public class ClaveResetServiceImpl implements IClaveResetService{

    private final JavaMailSender mailSender;

    private final IClaveResetTokenRepository tokenRepository;

    private final IConfigService configService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final InMemoryUserDetailsManager userDetailsManager;

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    public ClaveResetServiceImpl(JavaMailSender mailSender, IClaveResetTokenRepository tokenRepository, IConfigService configService, BCryptPasswordEncoder passwordEncoder, InMemoryUserDetailsManager userDetailsManager) {
        this.mailSender = mailSender;
        this.tokenRepository = tokenRepository;
        this.configService = configService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    public void solicitarCambioClave(String email) {
        Config config = this.configService.findById(1L);
        if (!config.getEmailAcceso().equals(email)) {
            throw new RuntimeException("El email proporcionado no coincide con el registrado en el sistema.");
        }

        String token = generarToken();
        ClaveResetToken resetToken = new ClaveResetToken(token, config);
        this.tokenRepository.save(resetToken);

        enviarEmailCambioClave(email, token, config.getNombreLavanderia());
    }

    private String generarToken() {
        return UUID.randomUUID().toString();
    }

    private void enviarEmailCambioClave(String email, String token, String nombreLavanderia) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Cambio de clave de acceso - Lavandería "+nombreLavanderia);
        message.setText("Para cambiar su clave de acceso, por favor haga clic en el siguiente enlace:\n\n" +
                baseUrl + "/administracion/clave/cambiar/" + token + "\n\n" +
                "Este enlace será válido solo por los próximos 15 minutos.\n\n" +
                "Si usted no solicitó este cambio de contraseña, puede ignorar este mensaje.");
        this.mailSender.send(message);
    }

    @Override
    public int cambiarClave(String claveActual, String claveNueva) {
        try {
            // validamos la nueva clave
            if (!Validator.validarClave(claveNueva)) {
                return 3; // no pasa la validacion
            }

            Config config = configService.findById(1L);
            if (config == null) {
                return 0;
            }
            //chequeamos que la actual sea correcta
            if (this.configService.verificarClaveAcceso(claveActual)) {
                config.setClaveAcceso(claveNueva, passwordEncoder);
                this.configService.save(config);
                this.actualizarUsuarioEnMemoria(config.getEmailAcceso(), claveNueva);
            } else {
                return 2;
            }
        } catch (Exception e) {
            System.out.println("ClaveResetServiceImpl: Excepción capturada al cambiar la clave: " + e);
            throw e;
        }
        return 1;

    }

    @Override
    public int cambiarClaveConToken(String token, String claveNueva) {
        Optional<ClaveResetToken> optToken = this.tokenRepository.findByToken(token);

        if (optToken.isEmpty()) {
            throw new RuntimeException("Token inválido. Envíe el correo nuevamente.");
        }

        ClaveResetToken resetToken = optToken.get();

        if (resetToken.isExpired()) {
            this.tokenRepository.delete(resetToken);
            throw new RuntimeException("El token ha expirado. Envíe el correo nuevamente.");
        }

        int result = this.cambiarClave(resetToken.getConfig().getClaveAcceso(), claveNueva);

        if (result == 1) { // solo borramos token si se pudo cambiar la clave
            this.tokenRepository.delete(resetToken);
        }

        return result;
    }

    private void actualizarUsuarioEnMemoria(String emailAcceso, String claveAcceso) {
        UserDetails existingUser = this.userDetailsManager.loadUserByUsername(emailAcceso);
        UserDetails updatedUser = User.builder()
                .username(existingUser.getUsername())
                .password(this.passwordEncoder.encode(claveAcceso))
                .roles("USER")
                .build();
        this.userDetailsManager.updateUser(updatedUser);
    }


}
