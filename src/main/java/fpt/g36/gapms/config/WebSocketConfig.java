package fpt.g36.gapms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${websocket.endpoint:/ws}")
    private String endpoint;

    @Value("${websocket.application-prefix:/app}")
    private String applicationPrefix;

    @Value("${websocket.broker-prefix:/topic,/queue}")
    private String brokerPrefix;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Tách các broker prefix thành mảng
        String[] brokerPrefixes = brokerPrefix.split(",");
        config.enableSimpleBroker(brokerPrefixes);

        // Đặt application destination prefix
        config.setApplicationDestinationPrefixes(applicationPrefix);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đây là endpoint để client kết nối tới WebSocket server
        registry.addEndpoint(endpoint)
                .setAllowedOriginPatterns(allowedOrigins) // Trong môi trường production, hãy giới hạn origin
                .withSockJS(); // Hỗ trợ fallback cho các browser không hỗ trợ WebSocket
    }
}
