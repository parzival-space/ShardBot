package space.parzival.shardbot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ShardbotApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
