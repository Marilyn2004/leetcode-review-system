package com.ziyi.leetcodereviewsystem;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@Profile("local")
@ConditionalOnProperty(name = "catalog.seed.enabled", havingValue = "true")
public class CatalogSeedRunner implements ApplicationRunner {
    private final CatalogLoader loader;
    private final String url;
    public CatalogSeedRunner(CatalogLoader loader, @Value("${spring.datasource.url}") String url) {
        this.loader = loader;
        this.url = url;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!url.matches("jdbc:postgresql://(?:localhost|127\\.0\\.0\\.1):[0-9]+/[a-zA-Z0-9_]+")) {
            throw new IllegalStateException("Catalog seed requires a loopback PostgreSQL datasource");
        }
        loader.load();
    }
}
