package me.yeon.week4.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .components(new Components())
        .info(appInfo());
  }

  private Info appInfo() {
    return new Info()
        .title("Scheduler API")
        .description("내일배움캠프 Week4 일정 관리 앱 API 문서")
        .version("1.0.0");
  }
}
