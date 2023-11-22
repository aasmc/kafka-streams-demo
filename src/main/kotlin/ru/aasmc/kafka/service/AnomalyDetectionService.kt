package ru.aasmc.kafka.service

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration

private val log = LoggerFactory.getLogger(AnomalyDetectionService::class.java)

@Service
class AnomalyDetectionService {

    @Autowired
    fun processAnomalyDetection(builder: StreamsBuilder) {
        // Read the source stream.  In this example, we ignore whatever is stored in the record key and
        // assume the record value contains the username (and each record would represent a single
        // click by the corresponding user).
        val views: KStream<String, String> = builder.stream("UserClicks")

        val anomalousUsers: KTable<Windowed<String>, Long> = views
            // map the user name as key, because the subsequent counting is performed based on the key
            .map { ignoredKey, userName -> KeyValue(userName, userName) }
            // count users, using one-minute tumbling windows;
            // no need to specify explicit serdes because the resulting key and value types match
            // our default serde settings
            .groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
            .count()
            .filter { windowedUserId, count -> count >= 3 }


        // Note: The following operations would NOT be needed for the actual anomaly detection,
        // which would normally stop at the filter() above.  We use the operations below only to
        // "massage" the output data so it is easier to inspect on the console via
        // kafka-console-consumer.

        val anomalousUsersForConsole: KStream<String, Long> = anomalousUsers
            // get rid of windows (and the underlying KTable) by transforming the KTable to a KStream
            .toStream()
            // sanitize the output by removing null record values (again, we do this only so that the
            // output is easier to read via kafka-console-consumer combined with LongDeserializer
            // because LongDeserializer fails on null values, and even though we could configure
            // kafka-console-consumer to skip messages on error the output still wouldn't look pretty)
            .filter { windowedUserId, count -> count != null }
            .map { windowedUserId, count -> KeyValue(windowedUserId.toString(), count) }

        // write to the result topic
        anomalousUsersForConsole.to("AnomalousUsers", Produced.with(Serdes.String(), Serdes.Long()))
    }

}