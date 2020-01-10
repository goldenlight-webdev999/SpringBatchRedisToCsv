package com.example.putDataToRedis.repository;

import com.example.putDataToRedis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public class RedisRepositoryImpl implements RedisRepository {

    private static final String KEY = "User";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }


    public Map<Object, Object> findAllUsers() {
        return hashOperations.entries(KEY);
    }


    public List<Object> findRangeUsers(String fromId, String toId) {

        List<String> keySet = new ArrayList<>();

        for (int i = Integer.parseInt(fromId); i < Integer.parseInt(toId); i++) {
            keySet.add(String.valueOf(i));
        }

        List<Object> values = hashOperations.multiGet(KEY, keySet);
        return values;

    }


    public void add(final User user) {
        hashOperations.put(KEY, user.getId(), user);
    }


    public void delete(final String id) {
        hashOperations.delete(KEY, id);
    }

    public void remove(final Integer id) {
        hashOperations.delete(KEY, id);
    }

    public void deleteAll() {
        hashOperations.delete(KEY);
    }


    public User findUser(final String id) {
        return (User) hashOperations.get(KEY, id);
    }
}
