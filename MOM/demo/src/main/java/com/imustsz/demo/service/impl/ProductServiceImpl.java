package com.imustsz.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imustsz.demo.domain.FileItem;
import com.imustsz.demo.domain.MesData;
import com.imustsz.demo.service.ProductService;
import com.imustsz.demo.utils.ConfigUtil;
import com.imustsz.demo.utils.HttpMultipartUtil;

import javax.jws.WebService;
import java.util.Base64;

@WebService(endpointInterface = "com.imustsz.demo.service.ProductService")
public class ProductServiceImpl implements ProductService {

    @Override
    public String uploadMesData(String sContent) {
        System.out.println("WebService 收到 MES 数据，准备转发...");

        // 获取 Spring Boot 接口地址
        String targetUrl;

        try {
            ObjectMapper mapper = new ObjectMapper();
            MesData data = mapper.readValue(sContent, MesData.class);

            if (data.getUniqueFlag().equals(ConfigUtil.get("unique.flag.craft"))){
                targetUrl = ConfigUtil.get("target.address.craft");
            }else if (data.getUniqueFlag().equals(ConfigUtil.get("unique.flag.order")))
                targetUrl = ConfigUtil.get("target.address.order");
            else
                return "ERROR: 无效的标识";

            if (data.getFileData() != null) {
                for (FileItem file : data.getFileData()) {
                    String fileName = file.getFileName();
                    String base64Str = file.getFileData();

                    if (base64Str == null || base64Str.isEmpty()) {
                        continue;
                    }

                    // 1. 去除 Base64 前缀（如果有）
                    if (base64Str.contains(",")) {
                        base64Str = base64Str.split(",")[1];
                    }

                    // 2. 解码成纯二进制字节数组 (此时文件在内存中)
                    byte[] fileBytes = Base64.getDecoder().decode(base64Str);
                    System.out.println("文件 [" + fileName + "] 解码成功，准备发送给 Spring Boot...");

                    System.out.println("目标地址" + targetUrl);
                    // 3. 调用工具类，直接将字节数组发送给 Spring Boot
                    String result = HttpMultipartUtil.sendFileBytes(targetUrl, fileName, fileBytes);

                    System.out.println("转发结果: " + result);
                }
            }

            return "SUCCESS: 数据接收并全部转发完毕";

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "ERROR: Base64 格式错误";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: 系统异常 - " + e.getMessage();
        }
    }
}