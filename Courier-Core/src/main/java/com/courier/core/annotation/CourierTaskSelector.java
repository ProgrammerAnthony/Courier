package com.courier.core.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
public class CourierTaskSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{ComponentScanConfig.class.getName()};
    }
}
