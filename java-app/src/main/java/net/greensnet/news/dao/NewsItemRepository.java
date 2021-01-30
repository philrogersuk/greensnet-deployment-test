/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.news.dao;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsItemRepository extends CrudRepository<NewsItemEntity, Long> {

    List<NewsItemEntity> findFirst30ByTimeOfReleaseLessThanOrderByTimeOfReleaseDesc(LocalDateTime now);

    List<NewsItemEntity> findFirst30ByOrderByTimeOfReleaseDesc();

    List<NewsItemEntity> findAllByTimeOfReleaseBetweenOrderByTimeOfReleaseDesc(LocalDateTime from, LocalDateTime to);

    Optional<NewsItemEntity> findFirst1ByOrderByTimeOfReleaseAsc();

    Optional<NewsItemEntity> findFirst1ByTimeOfReleaseGreaterThanOrderByTimeOfReleaseAsc(LocalDateTime now);

    Optional<NewsItemEntity> findFirst1ByTimeOfReleaseLessThanOrderByTimeOfReleaseDesc(LocalDateTime now);
}
