package com.herenpeng.rpc.bean;

import lombok.*;

import java.io.Serializable;

/**
 * @author herenpeng
 * @since 2023-02-19 20:24
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private Integer userId;
    private String username;
    private Boolean gender;
    private Integer age;
    private Long time;

}
