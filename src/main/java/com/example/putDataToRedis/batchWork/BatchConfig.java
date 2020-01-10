package com.example.putDataToRedis.batchWork;

import com.example.putDataToRedis.RedisItemReader;
import com.example.putDataToRedis.UserItemProcessor;
import com.example.putDataToRedis.model.User;
import com.example.putDataToRedis.repository.RedisRepository;
import com.opencsv.CSVWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfig {

    @Autowired
    private RedisRepository redisRepository;

    @Bean
    public void addNewUsers(){
        System.out.println("-------Start to put users--------");
        for (int i = 0; i < 100000; i++) {
            String id = String.valueOf(i);
            String name = id + "John";
            String email ="john@a.com";
            User user = new User(id, name, email);
            //redisRepository.add(user);          // To add new users into Redis
            //redisRepository.delete(id);         // To remove the users from Redis
        }

    }

    private static Integer counter = 0;

    @Scheduled(fixedRate = 60000)  // call at every 1 min
    public void getUser() throws IOException {


        if (counter>9) System.exit(0);

        final String startId = String.valueOf(counter*10000);
        final String endId = String.valueOf(counter*10000 + 10000);


        /*
        // =======Find All users =========

        ArrayList<User> myUsers = new ArrayList<>();

        Map<Object, Object> aa = redisRepository.findAllUsers();
        Map<String, Object> map = new HashMap<String, Object>();
        for(Map.Entry<Object, Object> entry : aa.entrySet()){
            String key = (String) entry.getKey();
            map.put(key, (Object) aa.get(key));
            myUsers.add((User) aa.get(key));
        }
        writeToCSV(myUsers, counter);
        */

        /**
         * Find only users in the Range(startId ~ endId)
         */
        List<Object> myUsers = redisRepository.findRangeUsers(startId, endId);

        //Write to CSV
        writeToCSV((ArrayList)myUsers, counter);
        counter++;

    }

    /**
     * Make CSV files from data and index
     * @param userList Data
     * @param counter index
     */
    public void writeToCSV(ArrayList<User> userList, Integer counter)
    {
        final String CSV_FILE_NAME = "output/export"+counter+".csv";
        final String CSV_SEPARATOR = ",";
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CSV_FILE_NAME), "UTF-8"));

            for (User user : userList)
            {
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(Integer.parseInt(user.getId()) < 0 ? "" : user.getId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(user.getName().trim().length() == 0? "" : user.getName());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(user.getEmail().trim().length() == 0? "" : user.getEmail());
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
}
