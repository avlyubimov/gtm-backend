package com.gtm.gtm.config;

import com.gtm.gtm.common.repository.SoftDeleteRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.gtm.gtm",
        repositoryBaseClass = SoftDeleteRepositoryImpl.class
)
public class JpaConfig {}
