package org.example.studentmanagementsystem;

import org.example.studentmanagementsystem.logs.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RedisAndDatabaseConnectionTest {

    @MockBean
    private LogService logService;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testRedisLogging() {
        String message = "Test Redis logging message";
        logService.log(message);

        verify(logService).log(message);
    }

    @Test
    public void testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(1)).isTrue();
        } catch (Exception e) {
            assertThat(e).isNull();
        }
    }
}
