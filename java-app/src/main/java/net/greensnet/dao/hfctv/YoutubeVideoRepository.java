package net.greensnet.dao.hfctv;

import net.greensnet.domain.Fixture;
import net.greensnet.domain.hfctv.YoutubeVideo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YoutubeVideoRepository extends CrudRepository<YoutubeVideo, Long> {
    List<YoutubeVideo> findAllByLinkedFixtureOrderByLinkedFixtureDesc(Fixture fixture);

    List<YoutubeVideo> findFirst20ByOrderByTimeStampDesc();

    Optional<YoutubeVideo> findByLinkedNewsItem_Id(long id);

    void deleteByLinkedNewsItem_Id(long id);

  Optional<YoutubeVideo> findFirst1ByOrderByTimeStampDesc();
}
