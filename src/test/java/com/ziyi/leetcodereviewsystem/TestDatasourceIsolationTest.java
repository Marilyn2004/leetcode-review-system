package com.ziyi.leetcodereviewsystem;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:postgresql://remote.invalid:5432/forbidden",
    "spring.datasource.username=forbidden",
    "spring.datasource.password=forbidden-test-placeholder",
    "spring.datasource.hikari.jdbc-url=jdbc:postgresql://remote.invalid:5432/forbidden",
    "spring.datasource.hikari.username=forbidden",
    "spring.datasource.hikari.password=forbidden-test-placeholder",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(LocalTestDatabaseConfiguration.class)
class TestDatasourceIsolationTest {
    @Autowired HikariDataSource dataSource;
    @Autowired Environment environment;
    @Autowired EntityManagerFactory entityManagerFactory;

    @Test void fullContextUsesDedicatedLocalDatabaseDespiteHostileProperties() throws Exception {
        assertEquals("jdbc:postgresql://remote.invalid:5432/forbidden",
                environment.getProperty("spring.datasource.url"));
        assertEquals(LocalTestDatabaseConfiguration.URL, dataSource.getJdbcUrl());
        assertEquals("marilyn", dataSource.getUsername());
        assertEquals("", dataSource.getPassword());
        assertTrue(entityManagerFactory.isOpen());
        try (var connection = dataSource.getConnection()) {
            assertEquals(LocalTestDatabaseConfiguration.URL, connection.getMetaData().getURL());
            assertEquals("leetcode_review_test", connection.getCatalog());
        }
    }
}
