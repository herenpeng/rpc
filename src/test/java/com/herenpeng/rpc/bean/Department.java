package com.herenpeng.rpc.bean;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author herenpeng
 * @since 2023-02-24 23:37
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Department implements Serializable {

    private Integer id;
    private String name;
    private Date createDate;
    private Date updateDate;

}
