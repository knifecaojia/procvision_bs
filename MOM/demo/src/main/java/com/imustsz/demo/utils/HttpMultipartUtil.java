package com.imustsz.demo.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpMultipartUtil {

    /**
     * 将字节数组以文件流的形式发送到指定接口
     *
     * @param targetUrl 目标 Spring Boot 接口地址
     * @param fileName  文件名
     * @param fileBytes 文件的字节数组（Base64解码后的数据）
     * @return 服务器响应结果
     */
    public static String sendFileBytes(String targetUrl, String fileName, byte[] fileBytes) {
        HttpURLConnection conn = null;
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis(); // 定义分界线
        String CRLF = "\r\n"; // 换行符必须是 \r\n

        try {
            URL url = new URL(targetUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            // 设置请求头为 multipart/form-data
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (DataOutputStream request = new DataOutputStream(conn.getOutputStream())) {
                // 1. 写入文件的分界线和头信息
                request.writeBytes("--" + boundary + CRLF);
                // 注意这里的 name=\"file\" 必须和 Spring Boot 接口里的 @RequestParam("file") 保持一致
                request.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + CRLF);
                request.writeBytes("Content-Type: application/octet-stream" + CRLF);
                request.writeBytes(CRLF);

                // 2. 写入真正的文件二进制流 (直接写 byte[])
                request.write(fileBytes);

                // 3. 写入结尾的分界线
                request.writeBytes(CRLF);
                request.writeBytes("--" + boundary + "--" + CRLF);
                request.flush();
            }

            // 获取响应码和响应内容
            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }

            return "响应码: " + responseCode + ", 结果: " + response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "发送失败: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
