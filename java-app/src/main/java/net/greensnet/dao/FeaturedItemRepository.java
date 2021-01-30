package net.greensnet.dao;

import net.greensnet.domain.FeaturedItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FeaturedItemRepository extends CrudRepository<FeaturedItem, Long> {

    List<FeaturedItem> findAllByOrderByActiveDesc();

    List<FeaturedItem> findAllByActiveEqualsOrderByPriorityAsc(boolean active);
}
