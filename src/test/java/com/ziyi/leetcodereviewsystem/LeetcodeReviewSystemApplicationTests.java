package com.ziyi.leetcodereviewsystem;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Import(LocalTestDatabaseConfiguration.class)
class LeetcodeReviewSystemApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		assertTrue(context.getBeansOfType(CatalogSeedRunner.class).isEmpty());
	}

}
