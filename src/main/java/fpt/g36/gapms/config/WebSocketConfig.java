package fpt.g36.gapms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Đây là prefix cho các kênh mà client có thể subscribe để nhận thông báo
        config.enableSimpleBroker("/topic", "/queue");

        // Đây là prefix cho các endpoints mà client sẽ gửi message tới
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đây là endpoint để client kết nối tới WebSocket server
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Trong môi trường production, hãy giới hạn origin
                .withSockJS(); // Hỗ trợ fallback cho các browser không hỗ trợ WebSocket
    }
}
