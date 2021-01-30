package net.greensnet.news.domain;


import net.greensnet.news.dao.NewsItemEntity;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class NewsItemEntityToDomainConverterTest {

    private final String AUTHOR = RandomStringUtils.random(20);
    private final String HEADLINE = RandomStringUtils.random(50);
    private final long ID = RandomUtils.nextLong();
    private final LocalDateTime TIME_OF_RELEASE = LocalDateTime.now();
    private final String ITEM = RandomStringUtils.random(1000);

    @Test
    public void shouldConvertEntityToDomainObject() {
        NewsItemEntity entity = NewsItemEntity.builder()
                .author(AUTHOR)
                .headline(HEADLINE)
                .id(ID)
                .timeOfRelease(TIME_OF_RELEASE)
                .item(ITEM)
                .build();
        NewsItem item = new NewsItemEntityToDomainConverter().convert(entity);

        assertThat(item.getAuthor()).isEqualTo(AUTHOR);
        assertThat(item.getHeadline()).isEqualTo(HEADLINE);
        assertThat(item.getId()).isEqualTo(ID);
        assertThat(item.getTimeOfRelease()).isEqualTo(TIME_OF_RELEASE);
        assertThat(item.getItem()).isEqualTo(ITEM);
    }
}
