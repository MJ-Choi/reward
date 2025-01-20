package com.product.reward.artist.respository;

import com.product.reward.config.TestConfig;
import com.product.reward.entity.QArtist;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ArtistFinderRepository.class, TestConfig.class})
class ArtistFinderRepositoryTest {

    @Autowired
    ArtistFinderRepository repository;
    @Autowired
    JPAQueryFactory factory;

    @BeforeEach
    void init() {
        QArtist qArtist = QArtist.artist;
        List.of("a", "b")
                .forEach(artist ->
                        factory.insert(qArtist)
                                .columns(qArtist.name)
                                .values("artist-".concat(artist))
                                .execute()
                );
    }

    @Test
    void isExist() {
        Map<Long, String> artistMap = repository.getArtistMap();
        Long aid = artistMap.keySet().stream().toList().get(0);
        boolean result = repository.isExist(aid);
        Assertions.assertTrue(result);
    }

    @Test
    void getArtistMap() {
        Map<Long, String> artistMap = repository.getArtistMap();
        System.out.println("artistMap = " + artistMap);
        Assertions.assertNotNull(artistMap);
        Assertions.assertTrue(artistMap.size() >= 2);
    }
}