package com.lyt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 用户
 * @author: lyt
 * @create: 2020-04-29 15:47
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String account;

    private String name;

    private Integer age;
}
