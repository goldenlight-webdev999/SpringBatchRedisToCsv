package com.example.putDataToRedis.repository;

import com.example.putDataToRedis.model.User;

import java.util.List;
import java.util.Map;

public interface RedisRepository {

    /**
     * Return all users
     */
    Map<Object, Object> findAllUsers();

    /**
     * Return the users in range fromId ~ toId
     * @param fromId
     * @param toId
     * @return
     */
    List<Object> findRangeUsers(String fromId, String toId);

    void add(User user);

    void delete(String id);

    void remove(Integer id);

    void deleteAll();

    User findUser(String id);

}
