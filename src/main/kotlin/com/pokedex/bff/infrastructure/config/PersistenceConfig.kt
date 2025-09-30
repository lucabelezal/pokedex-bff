package com.pokedex.bff.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["com.pokedex.bff.adapters.out.persistence.repository"])
class PersistenceConfig
