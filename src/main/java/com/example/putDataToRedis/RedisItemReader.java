package com.example.putDataToRedis;

import com.example.putDataToRedis.model.User;
import org.springframework.batch.item.ItemReader;

import java.util.List;

public class RedisItemReader implements ItemReader<User> {
    private int nextUserIndex;
    private List<User> userData;

    RedisItemReader() {
        initialize();
    }

    private void initialize() {

    }

    @Override
    public User read() throws Exception {
        User nextUser = null;
        if (nextUserIndex < userData.size()) {
            nextUser = userData.get(nextUserIndex);
            nextUserIndex++;
        }

        return nextUser;

    }

}
