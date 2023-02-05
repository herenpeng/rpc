package com.herenpeng.rpc.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author herenpeng
 * @since 2023-02-04 10:52
 */
public class ClassScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);

    private final Set<Class<?>> classSet = new HashSet<>();

    /**
     * 使用'/'分割的路径字符串
     */
    private final String packagePath;
    private final Predicate<Class<?>> predicate;
    private final ClassLoader classLoader;

    /**
     * @param packageName 需要扫描的包名
     */
    public ClassScanner(String packageName) {
        this.packagePath = packagePath(packageName);
        this.predicate = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        this.classLoader = loader == null ? ClassScanner.class.getClassLoader() : loader;
    }

    public ClassScanner(String packageName, Predicate<Class<?>> predicate) {
        this.packagePath = packagePath(packageName);
        this.predicate = predicate;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        this.classLoader = loader == null ? ClassScanner.class.getClassLoader() : loader;
    }

    private String packagePath(String packageName) {
        String path = packageName.replace('.', '/');
        return path.endsWith("/") ? path : path + '/';
    }

    public List<Class<?>> listClass() {
        try {
            List<URL> urls = getResources();
            for (URL url : urls) {
                String protocol = url.getProtocol();
                if ("jar".equals(protocol)) {
                    scannerJar(url);
                } else if ("file".equals(protocol)) {
                    scannerFile(url);
                }
            }
        } catch (Exception e) {
            logger.error("[RPC服务端]rpc包扫描错误，包扫描路径：{}，e：{}", packagePath, e);
            e.printStackTrace();
        }
        return new ArrayList<>(classSet);
    }

    private List<URL> getResources() throws IOException {
        Enumeration<URL> urlEnumeration = classLoader.getResources(packagePath);
        Set<URL> urls = new HashSet<>();
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            urls.add(url);
        }
        return new ArrayList<>(urls);
    }


    private void scannerJar(URL url) throws Exception {
        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
            JarURLConnection jarUrlConn = (JarURLConnection) connection;
            try (JarFile jarFile = jarUrlConn.getJarFile()) {
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.endsWith(".class") && jarEntryName.startsWith(packagePath)) {
                        // 字符串 .class 长度为6
                        jarEntryName = jarEntryName.substring(0, jarEntryName.length() - 6).replace('/', '.');
                        scannerClass(jarEntryName);
                    }
                }
            }
        }
    }

    private void scannerFile(URL url) {
        String path = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
        File file = new File(path);
        String classPath = getClassPath(file);
        scannerFile(file, classPath);
    }

    private String getClassPath(File file) {
        // 获取文件的绝对路径
        String absolutePath = file.getAbsolutePath();
        if (!absolutePath.endsWith(File.separator)) {
            // 为了防止包扫描路径就是绝对路径的情况，在绝对路径末尾加上文件路径符号
            absolutePath = absolutePath + File.separator;
        }
        String ret = packagePath.replace('/', File.separatorChar);
        // 获取包扫描路径在绝对路径上的开始索引
        int index = absolutePath.lastIndexOf(ret);
        if (index != -1) {
            // 切割，获得包扫描路径上一级的路径字符串
            absolutePath = absolutePath.substring(0, index);
        }
        return absolutePath;
    }


    private void scannerFile(File file, String classPath) {
        if (file.isFile()) {
            // 获取文件的绝对路径字符串
            String absolutePath = file.getAbsolutePath();
            // 如果是.class文件
            if (absolutePath.endsWith(".class")) {
                // 将class文件的绝对路径切割开头的顶级绝对路径和末尾的文件后缀.class
                String className = absolutePath.substring(classPath.length(), absolutePath.length() - 6)
                        .replace(File.separatorChar, '.');
                scannerClass(className);
            }
        } else if (file.isDirectory()) {
            // 获取文件夹目录下的所有文件，递归处理
            File[] files = file.listFiles();
            if (Objects.isNull(files)) {
                return;
            }
            for (File value : files) {
                scannerFile(value, classPath);
            }

        }
    }


    private void scannerClass(String className) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            if (clazz != null && !classSet.contains(clazz)) {
                // 过滤器，如果过滤器为空，或者通过过滤器的过滤，则加入容器中
                if (predicate == null || predicate.test(clazz)) {
                    classSet.add(clazz);
                }
            }
        } catch (Throwable e) {
            logger.error("[RPC服务端]加载class文件错误，加载的class文件名称，{}，e：{}", className, e);
        }
    }

}
