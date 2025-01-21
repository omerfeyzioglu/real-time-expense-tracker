package org.example.project3.Service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaCassandraService {
    private final CqlSession session;

    @Autowired
    public KafkaCassandraService(CqlSession session) {
        this.session = session;
    }

    @KafkaListener(topics = "expenses-topic", groupId = "expense-group")
    public void consumeAndSave(String message) {
        try {
            log.info("Received record: {}", message);
            String[] values = message.split(",", -1);

            if (values.length == 7) {
                String empno = values[0].trim();
                String dateTime = values[1].trim();
                String description = values[2].trim() + ", " + values[3].trim();
                String type = values[4].trim();
                int count = Integer.parseInt(values[5].trim());
                double payment = Double.parseDouble(values[6].trim());

                String query = "INSERT INTO expenses (empno, date_time, description, type, count, payment) VALUES (?, ?, ?, ?, ?, ?)";
                session.execute(SimpleStatement.builder(query)
                        .addPositionalValues(empno, dateTime, description, type, count, payment)
                        .build());
                log.info("Data saved to Cassandra: {}", message);
            } else {
                log.error("Invalid record: Expected 7 fields but received {} - {}", values.length, message);
            }
        } catch (Exception e) {
            log.error("Error processing data: {}", e.getMessage(), e);
        }
    }
}