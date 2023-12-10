package com.herenpeng.rpc.kit;

import com.herenpeng.rpc.annotation.RpcMethod;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcKit {

    public static void panic(RpcException e) {
        if (e != null) {
            throw e;
        }
    }

    public static RpcMethodLocator getMethodLocator(String className, Method method, String path) {
        RpcMethodLocator methodLocator = getMethodLocator(className, method);
        // 处理路径映射
        RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
        if (rpcMethod != null) {
            String subPath = StringUtils.formatPath(rpcMethod.value());
            methodLocator.setPath(path + subPath);
        }
        return methodLocator;
    }


    private static RpcMethodLocator getMethodLocator(String className, Method method) {
        RpcMethodLocator locator = new RpcMethodLocator();
        locator.setClassName(className);
        locator.setMethodName(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        int length = parameterTypes.length;
        // 是否是异步方法
        locator.setAsync(length > 0 && RpcCallback.class.isAssignableFrom(parameterTypes[length - 1]));
        // 真实参数长度
        length = locator.isAsync() ? length - 1 : length;
        String[] paramTypeNames = new String[length];
        for (int i = 0; i < length; i++) {
            String paramTypeName = parameterTypes[i].getName();
            paramTypeNames[i] = paramTypeName;
        }
        locator.setParamTypeNames(paramTypeNames);
        return locator;
    }


    public static RpcCallback<?> getRpcCallback(Object[] args) {
        if (ContainerKit.isEmpty(args)) {
            return null;
        }
        // 异步
        int callbackIndex = args.length - 1;
        if (RpcCallback.class.isAssignableFrom(args[callbackIndex].getClass())) {
            RpcCallback<?> callback = (RpcCallback<?>) args[callbackIndex];
            args[callbackIndex] = null;
            return callback;
        }
        return null;
    }


    public static Class<?> findApi(List<Class<?>> apiClassList, Class<?> serviceClass) {
        Class<?> api = serviceClass;
        for (Class<?> apiClass : apiClassList) {
            if (apiClass.isAssignableFrom(serviceClass)) {
                api = apiClass;
            }
        }
        return api;
    }


    public static String getClientIp(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getHostName() + ":" + socketAddress.getPort();
    }


    public static byte[] getBytes(ByteBuf buffer) {
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        return data;
    }


    public static byte[] compress(byte[] bytes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(bytes);
            //关闭压缩工具流
            gzip.finish();
            gzip.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("压缩数据错误！");
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] decompress(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream gzip = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int num;
            while ((num = gzip.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, num);
            }
            byte[] data = bos.toByteArray();
            bos.flush();
            return data;
        } catch (IOException e) {
            log.error("解压缩数据错误！");
            e.printStackTrace();
        }
        return null;
    }


}
