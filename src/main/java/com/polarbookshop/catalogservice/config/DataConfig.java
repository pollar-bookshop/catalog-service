package com.polarbookshop.catalogservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // 생성 날짜, 수정된 날짜 등 엔티티에 대한 감사를 활성화
public class DataConfig {
}
