package com.herenpeng.rpc.bean;

import lombok.*;

import java.io.Serializable;

/**
 * @author herenpeng
 * @since 2023-02-24 23:37
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Money implements Serializable {

    private User user;
    private long account;

}
