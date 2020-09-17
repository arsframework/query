package wuyq.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 服务启动类
 *
 * @author wuyongqiang
 * @date 2020/9/17
 */
@EnableAspectJAutoProxy
@SpringBootApplication(scanBasePackages = "wuyq.query")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}