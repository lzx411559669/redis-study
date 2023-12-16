package com.example.redisstudy.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@Table("user")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Column(ignore = true)
    private Lock lock = new ReentrantLock();

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private Integer age;
    private Date birthday;
    private boolean sex;
    public void increateAge() {
        lock.lock();
        try {
            this.age = this.age + 1;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    ;
}
