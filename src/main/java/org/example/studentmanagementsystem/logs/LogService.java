package org.example.studentmanagementsystem.logs;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LogService {
    private Jedis jedis;
    private static final String LOG_KEY = "student_logs";

    public LogService() {
        this.jedis = new Jedis("localhost", 6379);
    }

    public void log(String action) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = action + " - " + currentTime;
        jedis.rpush(LOG_KEY, logEntry);
        System.out.println("Log added: " + logEntry);
    }
    public List<String> getLogs() {
        return jedis.lrange(LOG_KEY, 0, -1);
    }
}
