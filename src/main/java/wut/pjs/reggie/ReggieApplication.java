package wut.pjs.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author: Jayson_P
 * @Date: 2023/06/03/23:21
 */
@Slf4j
@SpringBootApplication//SpringBoot项目启动项
@ServletComponentScan//扫描每个包下的Bean注解
@EnableCaching //springCache框架
@EnableTransactionManagement//事务管理
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("SpringBoot项目启动！");
    }
}
