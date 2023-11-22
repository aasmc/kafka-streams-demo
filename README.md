# Примеры использования Kafka Streams

## AnomalyDetection
Демонстрирует возможность вести подсчет событий с течением времени. Вычитываются данные из
топика UserClicks, в котором значением является имя пользователя, совершившего клик по URL.
Сервис ведет подсчет кликов пользователей, и если появляется аномальное поведение - более 3
кликов в минуту, то отправляет данные о пользователе и количестве кликов в топик AnomalousUsers. 

Как запустить сервис:
1. Запустить docker контейнеры: docker compose up -d
2. Запустить в IDEA приложение KafkaStreamsDemoApplication
3. В терминале выполнитследующую команду по открытию сессии с консольным продъюсером Kafka:
```shell
docker compose exec -i kafka /bin/kafka-console-producer --broker-list kafka:29092 --topic UserClicks
```
4. После старта консольного продъюсера сделать несколько записей usernames в терминале,
   если в течение 1 минуты будет более трех одинаковых usernames, то тогда такой username попадет в 
   топик AnomalousUsers
5. Чтобы прочитать содержимое топика AnomalousUsers, выполните следующую команду:
```shell
docker compose exec -i kafka /bin/kafka-console-consumer --topic AnomalousUsers --from-beginning \
                                                         --bootstrap-server kafka:29092  \
                                                         --property print.key=true  \
                                                         --property value.deserializer=org.apache.kafka.common.serialization.LongDeserialzer
```