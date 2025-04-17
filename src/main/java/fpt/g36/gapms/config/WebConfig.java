package fpt.g36.gapms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { //đăng ký bộ xử lý tài nguyên tĩnh.
        if ("prod".equals(activeProfile)) { //Nếu profile hiện tại là "prod", thì không cần thêm bộ xử lý tài nguyên tĩnh.
            return;
        } else {
            // Nếu không phải là profile "prod", thêm bộ xử lý tài nguyên tĩnh cho thư mục uploads
            registry.addResourceHandler("/uploads/**") /*Dòng này khai báo rằng bất kỳ
                                                                  yêu cầu nào có URL bắt đầu bằng /uploads/
                                                                     sẽ được xử lý bởi bộ xử lý tài nguyên tĩnh.*/
                    .addResourceLocations("file:uploads/");
        }
    }
}
