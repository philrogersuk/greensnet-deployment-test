package net.greensnet.dao;

import net.greensnet.domain.Sponsor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SponsorRepository extends CrudRepository<Sponsor, Long> {

    List<Sponsor> findAllByActiveEquals(boolean active);
}
