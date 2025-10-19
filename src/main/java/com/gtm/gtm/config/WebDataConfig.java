package com.gtm.gtm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebDataConfig implements PageableHandlerMethodArgumentResolverCustomizer {

    @Override
    public void customize(org.springframework.data.web.PageableHandlerMethodArgumentResolver r) {
        r.setOneIndexedParameters(false);
        r.setMaxPageSize(200);
    }
}
