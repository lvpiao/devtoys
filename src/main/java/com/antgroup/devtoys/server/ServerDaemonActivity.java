package com.antgroup.devtoys.server;


import cn.hutool.http.HttpUtil;
import com.antgroup.devtoys.util.Constant;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ServerDaemonActivity implements ProjectActivity {
    private static final Logger LOG = Logger.getInstance(ServerDaemonActivity.class);

    private static Thread serverHolder;

    private static final Lock lock = new ReentrantLock();

    public static Process process = null;

    /**
     * @return
     */
    private static boolean checkServerOnline() {
        try {
            String res = HttpUtil.get(Constant.SERVER_ADDRESS + "hello");
            return StringUtils.equals(res, "hello");
        } catch (Exception e) {
            LOG.info("Server exit", e);
        }
        return false;
    }

    private static void startServer() {

        if (checkServerOnline()) {
            LOG.info("服务已启动");
            System.out.println("服务已启动");
            return;
        }
        System.out.println("startServer begin");
        boolean res = lock.tryLock();
        if (!res) {
            return;
        }


        try (InputStream jarInputStream = ServerDaemonActivity.class.getResourceAsStream("/main.jar")) {
            // 获取资源文件夹中 JAR 文件的输入流
            if (jarInputStream == null) {
                throw new IllegalArgumentException("JAR file not found!");
            }

            // 创建临时文件
            File tempJarFile = Files.createTempFile("tempJar", ".jar").toFile();
            tempJarFile.deleteOnExit();

            // 将输入流写入临时文件
            try (FileOutputStream outputStream = new FileOutputStream(tempJarFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = jarInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("startServer begin2");
            // 使用 ProcessBuilder 启动临时文件
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", tempJarFile.getAbsolutePath());
            process = processBuilder.start();

            System.out.println("startServer End");
            LOG.info("服务已启动");
            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.info("Server fail", e);
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
            lock.unlock();
            LOG.info("Server exit");
        }
    }


    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        if (checkServerOnline()) {
            return Unit.INSTANCE;
        }
        serverHolder = new Thread(ServerDaemonActivity::startServer);
        serverHolder.setDaemon(true);
        serverHolder.start();
        return Unit.INSTANCE;
    }

}
