package net.greensnet.club.service;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.dao.CrestEntity;
import net.greensnet.club.dao.CrestRepository;
import net.greensnet.service.files.FileService;
import net.greensnet.service.files.ImageSize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static net.greensnet.service.files.ImageSize.Type.HEIGHT;
import static net.greensnet.service.files.ImageSize.Type.ORIGINAL;

@Service
public class CrestServiceImpl implements CrestService {

    private final CrestRepository crestRepository;
    private final FileService fileService;

    public CrestServiceImpl (CrestRepository crestRepository,
                             FileService fileService) {
        this.crestRepository = crestRepository;
        this.fileService = fileService;
    }

    public boolean createCrest(MultipartFile fileStream, ClubEntity club, int firstSeason, int lastSeason) {
        CrestEntity crest = new CrestEntity();
        crest.setClub(club);
        crest.setFirstSeason(firstSeason);
        crest.setLastSeason(lastSeason);
        crestRepository.save(crest);

        String filename = "crests/" + club.getId() + "-" + club.getName() + "." + Files.getFileExtension(fileStream.getOriginalFilename());
        List<ImageSize> sizes = Lists.newArrayList(new ImageSize(HEIGHT, 40), new ImageSize(HEIGHT, 100), new ImageSize(ORIGINAL));

        fileService.uploadImage(fileStream, filename, sizes);
        crest.setAwsUrl(fileService.getUrl(new ImageSize(HEIGHT, 100).renameFile(filename)).orElse(null));
        crestRepository.save(crest);

        return true;
    }

    public List<CrestEntity> getByClub(long clubId) {
        return crestRepository.findAllByClub_IdEqualsOrderByFirstSeasonDesc(clubId);
    }

    @Override
    public Optional<CrestEntity> getByClubForSeason(long clubId, int season) {
        return getByClub(clubId).stream()
                .filter(crest -> crest.validForSeason(season))
                .findAny();
    }

    @Override
    public Map<Long, CrestEntity> getByClubs(Set<Long> clubs) {
        return crestRepository.findAllByClub_IdInOrderByFirstSeasonDesc(clubs).stream()
                .collect(toMap(e -> e.getClub().getId(), e -> e));
    }
}
