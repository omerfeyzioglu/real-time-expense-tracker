package org.example.project3.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class DataGeneratorService {
    private final KafkaProducer<String, String> producer;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataGeneratorService(KafkaProducer<String, String> producer, JdbcTemplate jdbcTemplate) {
        this.producer = producer;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void generateAndSendData() {
        List<Integer> empnos = jdbcTemplate.queryForList("SELECT empno FROM emp", Integer.class);
        for (Integer empno : empnos) {
            String expense = generateRandomExpense(empno);
            producer.send(new ProducerRecord<>("expenses-topic", expense));
            log.info("Generated expense for empno {}: {}", empno, expense);
        }
    }

    public void generateAndSendDataForEmployee(int empno) {
        String expense = generateRandomExpense(empno);
        producer.send(new ProducerRecord<>("expenses-topic", expense));
        log.info("Generated expense for new employee {}: {}", empno, expense);
    }

    private String generateRandomExpense(int empno) {
        Random rand = new Random();
        String[] descriptions = {"Macaroni, food", "Jacket, clothe", "Car, vehicle"};
        String[] types = {"food", "clothe", "vehicle"};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(new Date());

        double payment = Math.round((50 + rand.nextDouble() * 950) * 100.0) / 100.0;
        String description = descriptions[rand.nextInt(descriptions.length)];
        String type = types[rand.nextInt(types.length)];

        return empno + "," +  // Gerçek empno
                dateTime + "," + // date_time
                description + "," + // description
                type + "," + // type
                1 + "," + // count, sabit
                payment; // payment
    }
}