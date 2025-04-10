package com.card_management.application.configuration;

import com.card_management.technical.util.CardEncryptor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.keygen.KeyGenerators;

@Setter
@Getter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "card.encryptor")
public class AppConfig {
    private String password;
    private String saltCard;

    @Bean
    @Scope("prototype")
    public CardEncryptor cardEncryptor() {
        this.saltCard = KeyGenerators.string().generateKey();
        return new CardEncryptor(password, saltCard);
    }
}
