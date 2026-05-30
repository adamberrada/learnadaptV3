package com.anouar.elearning.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient aiChatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }
}
