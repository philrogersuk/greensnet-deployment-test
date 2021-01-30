package net.greensnet.dao.hfctv;

import net.greensnet.domain.hfctv.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

}
