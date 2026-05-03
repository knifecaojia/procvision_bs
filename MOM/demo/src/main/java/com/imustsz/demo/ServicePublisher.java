package com.imustsz.demo;

import com.imustsz.demo.service.impl.ProductServiceImpl;
import com.imustsz.demo.utils.ConfigUtil;

import javax.xml.ws.Endpoint;

public class ServicePublisher {
    public static void main(String[] args) {
        String port = System.getenv("SERVER_PORT");
        String ip = System.getenv("SERVER_IP");

        if (port == null || port.trim().isEmpty()) {
            port = ConfigUtil.get("server.port");
        }

        if (ip == null || ip.trim().isEmpty())
            ip = ConfigUtil.get("server.ip");

//        String url = "http://" + ip + ":" + port + "/ws/product";
        String url = "http://" + ip + ":" + port + "/service/IFService";

        System.out.println("正在发布服务，地址: " + url);

        Endpoint.publish(url, new ProductServiceImpl());
        System.out.println("服务启动成功！");
    }
}