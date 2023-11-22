package ru.aasmc.kafka.config

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.TopicBuilder

@EnableKafkaStreams
@Configuration
class KafkaConfig {

    @Bean
    fun userClicksTopic(): NewTopic =
        TopicBuilder
            .name("UserClicks")
            .partitions(1)
            .replicas(1)
            .build()

    @Bean
    fun anomalousUsersTopic(): NewTopic =
        TopicBuilder
            .name("AnomalousUsers")
            .partitions(1)
            .replicas(1)
            .build()

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kStreamsConfig(): KafkaStreamsConfiguration {
        val props = hashMapOf<String, Any>(
            StreamsConfig.APPLICATION_ID_CONFIG to "kafka-streams-demo",
            StreamsConfig.CLIENT_ID_CONFIG to "kafka-streams-demo-client",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String()::class.java.name,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String()::class.java.name,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            StreamsConfig.PROCESSING_GUARANTEE_CONFIG to "at_least_once",
            StreamsConfig.COMMIT_INTERVAL_MS_CONFIG to 500
        )
        return KafkaStreamsConfiguration(props)
    }
}