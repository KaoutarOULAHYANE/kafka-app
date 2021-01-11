import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StreamProducer {
    private int counter;
    private String KAFKA_BROKER_URL = "localhost:9092";
    private String TOPIC_NAME = "kafka_topic_1";
    private String clientID = "client_1";

    public static void main(String[] args) {
        new StreamProducer();
    }

    public StreamProducer() {
        /*Configuration du KafkaProducer*/
        Properties properties = new Properties();
        /*Spécifier l'adresse du Broker Kafka*/
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER_URL);
        /*Spécifier l'ID du client*/
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientID);
        /*La serialisation de la clé de l'enregistrement*/
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        /*La serialisation de la valeur de l'enregistrement*/
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        /*Chaque enregistrement est sous forme <key,value>*/
        /*Par défaut, la clé et la valeur sont de type String*/
        KafkaProducer<String, String> kafkaProducer =
                new KafkaProducer<String, String>(properties);

        /*Creation d'un thread avec Timeout*/
        /*corePoolSize : nombre des threads*/
        /*initialDelay : durée entre deux polls (1000 => Faire un poll chaque seconde)*/
        /*period : durée de décalage au démarrage*/
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> {
                    /*Le traitement*/
                    String key = String.valueOf(++counter);
                    String record = String.valueOf(Math.random() * 1000);
                    kafkaProducer.send(
                            new ProducerRecord<String, String>(TOPIC_NAME, key, record),
                            ((recordMetadata, exception) -> {
                                System.out.println("> Sending record : ");
                                System.out.println("\tKey = " + key );
                                System.out.println("\tValue = " + record);
                                System.out.println("\tPartition = " + recordMetadata.partition());
                                System.out.println("\tOffset = " + recordMetadata.offset());
                            })
                    );
                }, 1000, 1000, TimeUnit.MILLISECONDS);
    }
}
