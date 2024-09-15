package com.antgroup.devtoys.server;

import cn.hutool.http.HttpUtil;
import com.antgroup.devtoys.test.GraySceneService;

import java.io.Writer;

public class ServerMocker {
    static GraySceneService graySceneService;

    static {
        graySceneService = new GraySceneService();
    }

    public static void main(String[] args) {
        HttpUtil.createServer(8866).addAction("/hello",
                (request, response) -> {
                    Writer writer = response.getWriter();
                    writer.write("hello");
                    writer.close();
                }).start();


        if (graySceneService.grayServiceTest()) {

        }
    }
}
