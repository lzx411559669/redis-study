package com.example.redisstudy.service;

import com.example.redisstudy.entity.Customer;
import com.example.redisstudy.mapper.CustomerMapper;
import com.example.redisstudy.utils.CheckUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    public static final String CACHE_KEY_CUSTOMER = "customer:";

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CheckUtils checkUtils;

    public void addCustomer(Customer customer){
        int i = customerMapper.insert(customer);
        if (i > 0){
            //到数据库里面，重新捞出新数据出来，做缓存
            Customer result = customerMapper.selectOneById(customer.getId());
            String key = CACHE_KEY_CUSTOMER+result.getId();
            //写进缓存
            redisTemplate.opsForValue().set(key,result);
        }
    }

    public Customer findByCustomerId(Integer id){
        Customer customer = null;
        String key = CACHE_KEY_CUSTOMER+id;
        //1 查询redis
        customer = (Customer) redisTemplate.opsForValue().get(key);
        //redis无，进一步查询mysql
        if (customer == null){
            //2 从mysql查出来customer
            customer = customerMapper.selectOneById(id);
            // mysql有，redis无
            if (customer!=null){
                //3 把mysql捞到的数据写入redis，方便下次查询能redis命中。
                redisTemplate.opsForValue().set(key,customer);
            }
        }
        return customer;
    }

    public Customer findCustomerByIdWithBloomFilter(Integer id){
        Customer customer = null;
        String key = CACHE_KEY_CUSTOMER+id;

        //布隆过滤器check，无是绝对无，有是可能有
        boolean exist = checkUtils.checkWithBloomFilter("whitelistCustomer", key);

        if (!exist){
            log.info("白名单无此顾客信息:{}",key);
            return null;
        }
        //1 查询redis
        customer = (Customer) redisTemplate.opsForValue().get(key);
        //redis无，进一步查询mysql
        if (customer == null){
            //2 从mysql查出来customer
            customer = customerMapper.selectOneById(id);
            // mysql有，redis无
            if (customer!=null){
                //3 把mysql捞到的数据写入redis，方便下次查询能redis命中。
                redisTemplate.opsForValue().set(key,customer);
            }
        }
        return customer;
    }
}
