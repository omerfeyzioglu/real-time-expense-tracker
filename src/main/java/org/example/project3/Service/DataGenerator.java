package org.example.project3.Service;



import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class DataGenerator {

    private static KafkaProducer<String, String> producer;

    public static void main(String[] args) {
        // Kafka Producer ayarları
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);

        try {
            // PostgreSQL'e bağlanma
            String url = "jdbc:postgresql://localhost:5432/company_workers";
            String user = "postgres";
            String password = "123";
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT empno FROM emp");

            // Empno değerlerini alıp Kafka'ya gönderme
            while (resultSet.next()) {
                int empno = resultSet.getInt("empno");
                System.out.println("Veritabanından alınan empno: " + empno);  // Burada empno'yu konsola yazdırıyoruz

                String expense = generateRandomExpense(empno);
                ProducerRecord<String, String> record = new ProducerRecord<>("expenses-topic", expense);
                producer.send(record);
                System.out.println("Gönderilen harcama: " + expense);

                // Biraz bekleme ekleyelim
                Thread.sleep(1000);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        producer.close();
    }

    private static String generateRandomExpense(int empno) {
        Random rand = new Random();
        String[] descriptions = {"Macaroni, food", "Jacket, clothe", "Car, vehicle"};
        String[] types = {"food", "clothe", "vehicle"};

        // Tarih ve saat oluşturma
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(new Date());

        // Rastgele ödeme ve type seçimi
        double payment = Math.round((50 + rand.nextDouble() * 950) * 100.0) / 100.0;
        String description = descriptions[rand.nextInt(descriptions.length)];
        String type = types[rand.nextInt(types.length)];

        // Empno'yu veri formatında göndermek için birleştiriyoruz
        return empno + "," +  // Gerçek empno
                dateTime + "," + // date_time
                description + "," + // description
                type + "," + // type
                1 + "," + // count, sabit
                payment; // payment
    }
}
