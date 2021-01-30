package net.greensnet.news.domain;

import net.greensnet.news.dao.NewsItemEntity;
import org.springframework.stereotype.Component;

@Component
public final class NewsItemEntityToDomainConverter {

    public NewsItem convert(NewsItemEntity entity) {
        return NewsItem.builder().author(entity.getAuthor())
                .headline(entity.getHeadline())
                .id(entity.getId())
                .item(entity.getItem())
                .timeOfRelease(entity.getTimeOfRelease())
                .build();
    }
}
