package com.product.reward.artist.respository;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import com.product.reward.artist.dto.ComicDto;
import com.product.reward.entity.QComic;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class ComicRepository {

    private final JPAQueryFactory factory;

    /**
     * 작품 단건 등록
     *
     * @param comicDto
     * @return
     */
    @Transactional
    public ComicDto register(ComicDto comicDto) {
        QComic qComic = QComic.comic;
        long execute = factory.insert(qComic)
                .columns(
                        qComic.aid,
                        qComic.name,
                        qComic.state
                )
                .values(
                        comicDto.getAid(),
                        comicDto.getName(),
                        comicDto.getState().getCode()
                )
                .execute();
        if (execute < 1) {
            log.error("failed to register comic: {}", comicDto);
            throw new ResponseException(ErrorCode.CHECKED);
        }
        // 생성된 id를 반환하기 위해 생성된 내역 조회
        return factory.select(Projections.constructor(ComicDto.class,
                        qComic.cid,
                        qComic.aid,
                        qComic.name,
                        qComic.state
                ))
                .from(qComic)
                .where(qComic.aid.eq(comicDto.getAid()))
                .where(qComic.name.eq(comicDto.getName()))
                .where(qComic.state.eq(comicDto.getState().getCode()))
                .fetchOne();
    }

    // todo: querydsl-sql 로 변환

    /**
     * 작품 수정
     * @param comicDtos
     * @return
     */
    @Transactional
    public boolean updateComics(List<ComicDto> comicDtos) {
        QComic qComic = QComic.comic;
        JPAUpdateClause updateClause = factory.update(qComic);
        boolean result = false;
        for (int i = 0; i < comicDtos.size(); i++) {
            ComicDto comicDto = comicDtos.get(i);
            // 쿼리 작성
            updateClause.where(QComic.comic.cid.eq(comicDto.getCid()));
            if (StringUtils.hasText(comicDto.getName())) {
                updateClause.set(QComic.comic.name, comicDto.getName());
            }
            if (comicDto.getState() != null) {
                updateClause.set(QComic.comic.state, comicDto.getState().getCode());
            }
            long execute = updateClause.execute();
            result = execute > 0;
        }
        return result;
    }

    public boolean isUniqueName(String comicName) {
        QComic qComic = QComic.comic;
        String name = factory.select(qComic.name)
                .from(qComic)
                .where(qComic.name.eq(comicName))
                .limit(1)
                .fetchOne();
        return !StringUtils.hasText(name);
    }

}