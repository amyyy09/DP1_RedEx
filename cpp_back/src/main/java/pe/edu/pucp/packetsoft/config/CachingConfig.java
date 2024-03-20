package pe.edu.pucp.packetsoft.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<Cache>();

        //Cada vez que se usa un nuevo value, se actualiza esta zona
        caches.add(new ConcurrentMapCache("usuarios"));
        caches.add(new ConcurrentMapCache("personas"));
        caches.add(new ConcurrentMapCache("proyectos"));
        caches.add(new ConcurrentMapCache("actaDeConstitucion"));
        caches.add(new ConcurrentMapCache("edt"));
        caches.add(new ConcurrentMapCache("planDeCalidad"));
        caches.add(new ConcurrentMapCache("cronograma"));
        caches.add(new ConcurrentMapCache("categoriaHerramienta"));
        caches.add(new ConcurrentMapCache("herramienta"));
        caches.add(new ConcurrentMapCache("herramientaXProyecto"));
        caches.add(new ConcurrentMapCache("tablas"));
        caches.add(new ConcurrentMapCache("atributos"));
        caches.add(new ConcurrentMapCache("valores"));
        caches.add(new ConcurrentMapCache("etiquetas"));
        caches.add(new ConcurrentMapCache("etiquetasAtributos"));
        caches.add(new ConcurrentMapCache("tablasPersonas"));
        caches.add(new ConcurrentMapCache("valorPersona"));
        caches.add(new ConcurrentMapCache("proyectoPersona"));
        caches.add(new ConcurrentMapCache("productBacklog"));
        caches.add(new ConcurrentMapCache("plantilla"));
        caches.add(new ConcurrentMapCache("catalogoDeRiesgos"));
        caches.add(new ConcurrentMapCache("presupuesto"));
        caches.add(new ConcurrentMapCache("igv"));
        caches.add(new ConcurrentMapCache("partida"));
        caches.add(new ConcurrentMapCache("matrizDeResponsabilidades"));


        cacheManager.setCaches(caches);
        return cacheManager;
    }

}