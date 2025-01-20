package com.product.reward.config;

import com.product.reward.artist.respository.*;
import com.product.reward.artist.service.ArtistService;
import com.product.reward.reward.repository.RankFinder;
import com.product.reward.reward.repository.RankFinderRedis;
import com.product.reward.reward.repository.RankFinderRepository;
import com.product.reward.reward.service.RedisDataInitiator;
import com.product.reward.util.CollectRedisKey;
import com.product.reward.util.DateUtils;
import com.product.reward.util.RedisUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;

@Configuration
@AllArgsConstructor
public class AppConfig {

    /**
     * CORS 설정
     *
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    @ConditionalOnProperty(name = "app.init-redis-datas", havingValue = "true")
    public RedisDataInitiator redisDataInitiator(ArtistFinderRepository artistFinder, ComicFinderRepository comicFinder, Clock clock, RedisUtils redisUtils) {
        return new RedisDataInitiator(artistFinder, comicFinder, clock, redisUtils);
    }

    @Bean
    @ConditionalOnProperty(name = "use-redis", havingValue = "false", matchIfMissing = true)
    public ArtistService artistServiceWithoutRedis(ComicRepository repository, ArtistFinder artistFinder, ComicFinder comicFinder) {
        return new ArtistService(repository, artistFinder, comicFinder);
    }

    @Bean
    @ConditionalOnProperty(name = "use-redis", havingValue = "true")
    public ArtistService artistService(ComicRepository repository, ArtistFinder artistFinder, ComicFinder comicFinder,
                                       RedisUtils redisUtils, CollectRedisKey collectRedisKey) {
        return new ArtistService(repository, artistFinder, comicFinder, redisUtils, collectRedisKey);
    }

    @Bean
    @ConditionalOnProperty(name = "use-redis", havingValue = "false", matchIfMissing = true)
    public ArtistFinder artistFinderWithoutRedis(JPAQueryFactory factory) {
        return new ArtistFinderRepository(factory);
    }

    @Bean
    @ConditionalOnProperty(name = "use-redis", havingValue = "true")
    public ArtistFinder artistFinder(RedisUtils redisUtils) {
        return new ArtistFinderRedis(redisUtils);
    }

    @Bean
    @ConditionalOnProperty(name = "use-redis", havingValue = "true")
    public ComicFinder comicFinder(CollectRedisKey collectKey, RedisUtils redisUtils) {
        return new ComicFinderRedis(collectKey, redisUtils);
    }

    @Bean
    @ConditionalOnProperty(name = "use-redis", havingValue = "true")
    public RankFinder rankFinder(CollectRedisKey collectKey, RedisUtils redisUtils) {
        return new RankFinderRedis(collectKey, redisUtils);
    }
}