package com.example.redisstudy.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Table("user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private Integer age;
    private Date birthday;
}
