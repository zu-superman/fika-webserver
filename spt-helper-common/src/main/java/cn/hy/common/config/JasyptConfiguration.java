package cn.hy.common.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfiguration {

    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm(algorithm);
        encryptor.setPassword(password);
        return encryptor;
    }
}
