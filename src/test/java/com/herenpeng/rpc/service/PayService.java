package com.herenpeng.rpc.service;

import com.herenpeng.rpc.annotation.RpcMethod;
import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.bean.Money;
import com.herenpeng.rpc.bean.User;

/**
 * @author herenpeng
 * @since 2023-02-24 23:35
 */
@RpcService("pay")
public class PayService {

    @RpcMethod("get")
    public Money getMontyInfo(long money) {
        User user = new User(15, "雷", true, 18, System.currentTimeMillis());
        Money info = new Money();
        info.setUser(user);
        info.setAccount(money);
        return info;
    }

}
