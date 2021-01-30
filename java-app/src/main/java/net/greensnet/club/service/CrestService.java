package net.greensnet.club.service;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.dao.CrestEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CrestService {

    boolean createCrest(MultipartFile fileStream, ClubEntity club, int firstSeason, int lastSeason);

    List<CrestEntity> getByClub(long clubId);

    Optional<CrestEntity> getByClubForSeason(long clubId, int season);

    Map<Long, CrestEntity> getByClubs(Set<Long> collect);
}
