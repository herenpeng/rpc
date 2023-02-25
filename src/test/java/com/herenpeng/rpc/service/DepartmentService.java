package com.herenpeng.rpc.service;

import com.herenpeng.rpc.annotation.RpcMethod;
import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.bean.Department;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author herenpeng
 * @since 2023-02-24 23:35
 */
@RpcService("department")
public class DepartmentService {

    @RpcMethod("get")
    public Department get(String name) {
        return new Department(1, name, new Date(), new Date());
    }

    @RpcMethod("list")
    public List<Department> list() {
        List<Department> list = new ArrayList<>();
        list.add(new Department(1, "行政部", new Date(), new Date()));
        list.add(new Department(2, "财务部", new Date(), new Date()));
        list.add(new Department(3, "技术部", new Date(), new Date()));
        list.add(new Department(4, "人事部", new Date(), new Date()));
        list.add(new Department(5, "运营部", new Date(), new Date()));
        list.add(new Department(6, "公关部", new Date(), new Date()));
        return list;
    }

}
