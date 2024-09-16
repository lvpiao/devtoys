package com.antgroup.devtoys.server;

import cn.hutool.http.HttpUtil;

import java.io.Writer;

public class ServerMocker {

    public static void main(String[] args) {
        HttpUtil.createServer(8866).addAction("/hello",
                (request, response) -> {
                    Writer writer = response.getWriter();
                    writer.write("hello");
                    writer.close();
                }).start();


    }
}
