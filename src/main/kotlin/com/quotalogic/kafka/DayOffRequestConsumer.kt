package com.quotalogic.kafka

import com.quotalogic.services.DayOffService
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

private val logger = KotlinLogging.logger {}

//TODO Refactor Consumer - change finally, change logging policy,
class Consumer<K, V>(private val consumer: KafkaConsumer<K, V>, topic: String) : Closeable, Runnable {
    private val closed: AtomicBoolean = AtomicBoolean(false)
    private var finished = CountDownLatch(1)
    private val dayOffService = DayOffService()

    init {
        consumer.subscribe(listOf(topic))
    }

    override fun run() {
        try {
            while (!closed.get()) {
                val records = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS))
                for (record in records) {
                    logger.info { "topic = ${record.topic()}, partition = ${record.partition()}, offset = ${record.offset()}, key = ${record.key()}, value = ${record.value()}" }
                    val date = LocalDate.parse(record.value().toString())
                    val email = record.key().toString()

                    runBlocking {
                        dayOffService.addDayOff(email, date)
                        logger.info { "Booked $date for an employee with email: $email" }
                    }
                }
                if (!records.isEmpty) {
                    consumer.commitAsync { offsets, exception ->
                        if (exception != null) {
                            logger.error(exception) { "Commit failed for offsets $offsets" }
                        } else {
                            logger.info { "Offset committed  $offsets" }
                        }
                    }
                }
            }
            logger.info { "Finish consuming" }
        } catch (e: Throwable) {
            when (e) {
                is WakeupException -> logger.info { "Consumer waked up" }
                else -> logger.error(e) { "Polling failed" }
            }
        } finally {
            logger.info { "Commit offset synchronously" }
            consumer.commitSync()
            consumer.close()
            finished.countDown()
            logger.info { "Consumer successfully closed" }
        }
    }

    override fun close() {
        logger.info { "Close job..." }
        closed.set(true)
        consumer.wakeup()
        finished.await(3000, TimeUnit.MILLISECONDS)
        logger.info { "Job is successfully closed" }
    }
}

fun <K, V> buildConsumer(): Consumer<K, V> {
    val consumerProps = Properties().apply {
        this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        this[ConsumerConfig.CLIENT_ID_CONFIG] = "day-off-consumer"
        this[ConsumerConfig.GROUP_ID_CONFIG] = "day-off-consumer-group"
        this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
    }
    return Consumer(KafkaConsumer(consumerProps), "book-day-off-topic")
}