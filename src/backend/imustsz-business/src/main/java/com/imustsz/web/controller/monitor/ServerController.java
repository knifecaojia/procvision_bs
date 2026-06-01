package com.imustsz.web.controller.monitor;

import com.imustsz.common.annotation.Log;
import com.imustsz.common.core.domain.AjaxResult;
import com.imustsz.common.enums.BusinessType;
import com.imustsz.framework.web.domain.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务器监控
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController
{

    // 建议将这些配置提取到 application.yml 中
    @Value("${spring.datasource.druid.master.username}")
    private String username;

    @Value("${spring.datasource.druid.master.password}")
    private String password;

    // 你的数据库名
    private String databaseName = "procvision";
    // 备份文件存放路径 (根据你的Linux服务器或Docker挂载路径调整)
    private String backupLocation = "/home/backup/";

    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public AjaxResult getInfo() throws Exception
    {
        Server server = new Server();
        server.copyTo();
        return AjaxResult.success(server);
    }

    /**
     * 备份数据库
     */
    @PreAuthorize("@ss.hasPermi('monitor:server:backup')")
    @Log(title = "服务监控", businessType = BusinessType.EXPORT)
    @PostMapping("/backupDb")
    public AjaxResult backupDatabase() {
        try {
            File backupDir = new File(backupLocation);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String fileName = databaseName + "_" + timestamp + ".sql";
            String filePath = backupLocation + fileName;

            // 构建 mysqldump 命令 (注意：运行环境必须安装并配置了mysql/mysqldump环境变量)
            // 如果是在Docker中运行，需确保基础镜像中包含 mysql-client
            String command = String.format("mysqldump -u%s -p%s %s > %s",
                    username, password, databaseName, filePath);

            // 针对 Linux 系统执行 sh -c
            String[] shellCommand = new String[]{"/bin/sh", "-c", command};
            Process process = Runtime.getRuntime().exec(shellCommand);

            int processComplete = process.waitFor();
            if (processComplete == 0) {
                return AjaxResult.success("数据库备份成功，已保存至：" + filePath);
            } else {
                return AjaxResult.error("数据库备份失败，请检查服务器日志或 mysqldump 环境。");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("数据库备份发生异常：" + e.getMessage());
        }
    }
}
