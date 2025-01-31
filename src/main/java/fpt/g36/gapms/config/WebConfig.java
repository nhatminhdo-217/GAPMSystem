package fpt.g36.gapms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { //đăng ký bộ xử lý tài nguyên tĩnh.
        registry.addResourceHandler("/uploads/**") /*Dòng này khai báo rằng bất kỳ
                                                                  yêu cầu nào có URL bắt đầu bằng /uploads/
                                                                     sẽ được xử lý bởi bộ xử lý tài nguyên tĩnh.*/
                .addResourceLocations("file:uploads/");
    }
}
