package org.example.project3.Service;



import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.util.Collections;
import java.util.Properties;
import java.text.SimpleDateFormat;

public class KafkaToCassandra {
    public static void main(String[] args) {
        // Kafka Consumer ayarları
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "localhost:9092");
        kafkaProps.put("group.id", "kafka-cassandra-consumer");
        kafkaProps.put("key.deserializer", StringDeserializer.class.getName());
        kafkaProps.put("value.deserializer", StringDeserializer.class.getName());
        kafkaProps.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);
        consumer.subscribe(Collections.singletonList("expenses-topic"));

        // Cassandra'ya bağlantı kuruluyor
        Cluster cluster = Cluster.builder().addContactPoint("localhost").withoutJMXReporting().build();
        Session session = cluster.connect("your_keyspace"); // your_keyspace: Cassandra keyspace'ınız

        // Tarih formatı için parser
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Kafka'dan veri alıp Cassandra'ya yazma
        try {
            while (true) {
                // Kafka'dan veri çekme
                consumer.poll(100).forEach(record -> {
                    // Debug: Log the record's value
                    System.out.println("Received record: " + record.value());

                    // Veriyi split ederken boşlukları dikkate almak için -1 parametresi kullanıyoruz
                    String[] values = record.value().split(",", -1);

                    if (values.length == 7) { // 7 alan bekliyoruz
                        try {
                            String empno = values[0].trim(); // user_id
                            String dateTime = values[1].trim(); // date_time
                            String description = values[2].trim() + ", " + values[3].trim(); // description, virgülle birleştirilmiş
                            String type = values[4].trim(); // type

                            // Verinin doğru tiplerde olduğunu kontrol et
                            int count = 0;
                            double payment = 0.0;

                            // count ve payment değerlerinin doğru tiplerde olduğunu kontrol et
                            try {
                                count = Integer.parseInt(values[5].trim()); // count
                            } catch (NumberFormatException e) {
                                System.err.println("Hata: count değeri sayıya dönüştürülemedi: " + values[5]);
                            }

                            try {
                                payment = Double.parseDouble(values[6].trim()); // payment
                            } catch (NumberFormatException e) {
                                System.err.println("Hata: payment değeri sayıya dönüştürülemedi: " + values[6]);
                            }

                            // Cassandra'ya veri yazma
                            String query = "INSERT INTO expenses (empno, date_time, description, type, count, payment) VALUES (?, ?, ?, ?, ?, ?)";
                            session.execute(query, empno, dateTime, description, type, count, payment);
                            System.out.println("Veri Cassandra'ya yazıldı: " + record.value());

                        } catch (Exception e) {
                            System.err.println("Veri işlenirken hata oluştu: " + e.getMessage());
                        }
                    } else {
                        // Hatalı veri durumunda
                        System.err.println("Invalid record: Expected 7 fields but received " + values.length + " - " + record.value());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
            session.close();
            cluster.close();
        }
    }
}
