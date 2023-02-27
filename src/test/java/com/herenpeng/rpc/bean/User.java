package com.herenpeng.rpc.bean;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

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

    private Integer id;
    private String username;
    private Boolean gender;
    private Integer age;

    private Date birthDate;
    private Date createDate;
    private Date updateDate;

}
