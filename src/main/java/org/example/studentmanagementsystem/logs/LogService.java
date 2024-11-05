package org.example.studentmanagementsystem.logs;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LogService {

    private static final String LOG_KEY = "student_logs";
    private final RedisTemplate<String, String> redisTemplate;

    public LogService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void log(String action) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = action + " - " + currentTime;
        redisTemplate.opsForList().rightPush(LOG_KEY, logEntry);
        System.out.println("Log added: " + logEntry);
    }

    public List<String> getLogs() {
        return redisTemplate.opsForList().range(LOG_KEY, 0, -1);
    }
}