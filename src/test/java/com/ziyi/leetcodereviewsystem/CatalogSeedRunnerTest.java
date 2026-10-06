package com.ziyi.leetcodereviewsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogSeedRunnerTest {
    @Test void acceptsLocalDatasource() throws Exception {
        CatalogLoader loader = mock(CatalogLoader.class);
        new CatalogSeedRunner(loader, "jdbc:postgresql://127.0.0.1:5432/leetcode_review_test")
            .run(new DefaultApplicationArguments());
        verify(loader).load();
    }
    @Test void rejectsRemoteDatasourceBeforeLoading() {
        CatalogLoader loader = mock(CatalogLoader.class);
        assertThrows(IllegalStateException.class, () -> new CatalogSeedRunner(loader,
            "jdbc:postgresql://remote.example:5432/database").run(new DefaultApplicationArguments()));
        verifyNoInteractions(loader);
    }
}
