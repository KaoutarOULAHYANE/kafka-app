import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StreamConsumer {
    private String KAFKA_BROKER_URL = "localhost:9092";
    private String TOPIC_NAME = "kafka_topic_1";

    public static void main(String[] args) {
        new StreamConsumer();
    }

    public StreamConsumer() {
        /*Configuration du KafkaConsumer*/
        Properties properties = new Properties();
        /*Spécifier l'adresse du Broker Kafka*/
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER_URL);
        /*Spécifier le groupe du consumer*/
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-1");

        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        /*Spécifier la durée de vie de la session*/
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        /*La deserialisation de la clé de l'enregistrement*/
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        /*La deserialisation de la valeur de l'enregistrement*/
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

        /*Chaque enregi strement est sous forme <key,value>*/
        /*Par défaut, la clé et la valeur sont de type String*/
        KafkaConsumer<String, String> kafkaConsumer =
                new KafkaConsumer<String, String>(properties);
        /*La souscription vers une topic*/
        kafkaConsumer.subscribe(Collections.singletonList(TOPIC_NAME));
        /*Creation d'un thread avec Timeout*/
        /*corePoolSize : nombre des threads*/
        /*initialDelay : durée entre deux polls (1000 => Faire un poll chaque seconde)*/
        /*period : durée de décalage au démarrage*/
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    /*Le traitement*/
                    System.out.println("------------------------------------------------");
                    /*récupérer les enregistrements produits dans les dernièrs '10 ms'*/
                    ConsumerRecords<String, String> consumerRecords =
                            kafkaConsumer.poll(Duration.ofMillis(1000));
                    consumerRecords.forEach(record -> {
                        System.out.println("> Receiving record : ");
                        System.out.println("\tKey : " + record.key());
                        System.out.println("\tValue : " + record.value());
                        System.out.println("\tOffset : " + record.offset());
                    });
                }, 1000, 1000, TimeUnit.MILLISECONDS);
    }
}
