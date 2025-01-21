//not used in project
//package org.example.project3;
//
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//
//import java.util.Properties;
//
//public class KafkaConsumerService {
//
//    private KafkaConsumer<String, String> consumer;
//
//    public KafkaConsumerService(String bootstrapServers, String groupId) {
//        Properties properties = new Properties();
//        properties.put("bootstrap.servers", bootstrapServers);
//        properties.put("group.id", groupId);
//        properties.put("key.deserializer", StringDeserializer.class.getName());
//        properties.put("value.deserializer", StringDeserializer.class.getName());
//
//        consumer = new KafkaConsumer<>(properties);
//    }
//
//    public void consume(String topic) {
//        consumer.subscribe(java.util.Collections.singletonList(topic));
//
//        while (true) {
//            var records = consumer.poll(java.time.Duration.ofMillis(1000));
//            records.forEach(record -> {
//                // Her bir Kafka mesajını işleyin
//                System.out.println("Received message: " + record.value());
//                // Burada veriyi işleyebilir ve PostgreSQL'e yazabilirsiniz
//            });
//        }
//    }
//
//    public void close() {
//        consumer.close();
//    }
//}
