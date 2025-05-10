package com.burbujas.gestionlimpia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("es", "AR"));  // Configuramos el locale para Argentina
        return slr;
    }

    @Bean()
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/vendor/**")
                .addResourceLocations("classpath:/static/vendor/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");

        registry.addViewController("/caja/movimientoCaja").setViewName("caja/movimientoCaja");
        registry.addViewController("/caja/movimientosCaja").setViewName("caja/movimientosCaja");

        registry.addViewController("/clientes/clientes").setViewName("clientes/clientes");
        registry.addViewController("/clientes/cliente").setViewName("clientes/cliente");

        registry.addViewController("/inventario/inventario").setViewName("inventario/inventario");
        registry.addViewController("/inventario/producto").setViewName("inventario/producto");
        registry.addViewController("/inventario/proveedores/proveedor").setViewName("inventario/proveedores/proveedor");
        registry.addViewController("/inventario/proveedores/proveedores").setViewName("inventario/proveedores/proveedores");
        registry.addViewController("/inventario/reabastecimientos/reabastecimiento").setViewName("inventario/reabastecimientos/reabastecimiento");
        registry.addViewController("/inventario/reabastecimientos/reabastecimientos").setViewName("inventario/reabastecimientos/reabastecimientos");

        registry.addViewController("/maquinas").setViewName("maquinas/maquinas");

        registry.addViewController("/pedido").setViewName("pedidos/pedido");
        registry.addViewController("/pedidos").setViewName("pedidos/pedidos");

        registry.addViewController("/administracion").setViewName("administracion");

        registry.addViewController("/notificaciones/bajo-stock").setViewName(null);

        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/logout").setViewName("redirect:/login?logout");
    }
}
