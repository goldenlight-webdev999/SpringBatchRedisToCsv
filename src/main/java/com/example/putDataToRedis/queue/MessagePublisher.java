package com.example.putDataToRedis.queue;

public interface MessagePublisher {
    void publish(final String message);
}
