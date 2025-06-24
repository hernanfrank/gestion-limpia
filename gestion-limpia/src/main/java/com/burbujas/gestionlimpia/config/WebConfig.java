package com.burbujas.gestionlimpia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimiterInterceptor rateLimitInterceptor;

    @Autowired
    public WebConfig(RateLimiterInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**") // Aplica a todas las rutas
                .excludePathPatterns("/error", "/static/**"); // Excluye rutas específicas
    }


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

        registry.addViewController("/caja/movimientoCaja/editar/*").setViewName("caja/movimientoCaja");
        registry.addViewController("/caja/movimientoCaja/nuevo").setViewName("caja/movimientoCaja");
        registry.addViewController("/caja/movimientosCaja/listar").setViewName("caja/movimientosCaja");
        registry.addViewController("/caja/movimientosCaja/eliminados").setViewName("caja/movimientosCaja");

        registry.addViewController("/clientes/editar/*").setViewName("clientes/cliente");
        registry.addViewController("/clientes/nuevo").setViewName("clientes/cliente");
        registry.addViewController("/clientes/listar").setViewName("clientes/clientes");
        registry.addViewController("/clientes/eliminados").setViewName("clientes/clientes");

        registry.addViewController("/productos/editar/*").setViewName("inventario/producto");
        registry.addViewController("/productos/nuevo").setViewName("inventario/producto");
        registry.addViewController("/productos/listar").setViewName("inventario/inventario");
        registry.addViewController("/productos/eliminados").setViewName("inventario/inventario");

        registry.addViewController("/productos/proveedores/editar/*").setViewName("inventario/proveedores/proveedor");
        registry.addViewController("/productos/proveedores/nuevo").setViewName("inventario/proveedores/proveedor");
        registry.addViewController("/productos/proveedores/listar").setViewName("inventario/proveedores/proveedores");
        registry.addViewController("/productos/proveedores/eliminados").setViewName("inventario/proveedores/proveedores");

        registry.addViewController("/productos/reabastecimientos/editar/*").setViewName("inventario/reabastecimientos/reabastecimiento");
        registry.addViewController("/productos/reabastecimientos/nuevo").setViewName("inventario/reabastecimientos/reabastecimiento");
        registry.addViewController("/productos/reabastecimientos/listar").setViewName("inventario/reabastecimientos/reabastecimientos");
        registry.addViewController("/productos/reabastecimientos/eliminados").setViewName("inventario/reabastecimientos/reabastecimientos");

        registry.addViewController("/pedido/editar/*").setViewName("pedidos/pedido");
        registry.addViewController("/pedidos/nuevo/listado").setViewName("pedidos/pedidos");
        registry.addViewController("/pedidos/nuevo/index").setViewName("pedidos/pedidos");
        registry.addViewController("/pedidos/listar").setViewName("pedidos/pedidos");
        registry.addViewController("/pedidos/eliminados").setViewName("pedidos/pedidos");

        registry.addViewController("/estadisticas").setViewName("estadisticas");

        registry.addViewController("/administracion").setViewName("administracion");

        registry.addViewController("/notificaciones/bajoStock").setViewName(null);

        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/cambiarClave").setViewName("cambiarClave");
        registry.addViewController("/logout").setViewName("redirect:/login?logout");

        registry.addViewController("/error/404").setViewName("error/404");
        registry.addViewController("/error/default").setViewName("error/default");
    }
}
