package com.example.redisstudy.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author liuzhengxing
 * @since 2023-10-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_customer")
public class Customer implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String cname;

    private Integer age;

    private String phone;

    private Integer sex;

    private Date birth;

}
