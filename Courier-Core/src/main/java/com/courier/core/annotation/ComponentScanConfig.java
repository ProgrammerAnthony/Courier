package com.courier.core.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
@Configuration
@ComponentScan(value = {"com.courier.core"})
@MapperScan(basePackages = {"com.courier.core.mapper"})
public class ComponentScanConfig {
}
