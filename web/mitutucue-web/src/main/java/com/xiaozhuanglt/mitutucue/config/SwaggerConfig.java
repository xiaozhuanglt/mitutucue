package com.xiaozhuanglt.mitutucue.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description: swagger配置文件
 * @author: hxz
 * @create: 2019-04-24 14:19
 **/
@Configuration    // 配置注解，自动在本类上下文加载一些环境变量信息
@EnableSwagger2  // 使swagger2生效
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xiaozhuanglt.mitutucue.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("mitutucue-swagger Api")
                .description("api根地址：http://localhost:8080/")
                .termsOfServiceUrl("https://xiaomo.info/")
                .contact("mitutu")
                .version("1.0")
                .build();
    }
}
