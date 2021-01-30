/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.service;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.dao.ClubNameEntity;
import net.greensnet.club.dao.ClubRepository;
import net.greensnet.club.dao.OldClubNameRepository;
import net.greensnet.club.domain.Club;
import net.greensnet.club.domain.ClubEntityToDomainConverter;
import net.greensnet.club.domain.ClubName;
import net.greensnet.club.domain.ClubNameEntityToDomainConverter;
import net.greensnet.club.event.producer.ClubUpdateProducer;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.FixtureService;
import net.greensnet.util.DateHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static net.greensnet.club.service.ClubServiceImpl.HENDON_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceImplTest {

    @Mock
    private ClubRepository clubRepository;
    @Mock
    private OldClubNameRepository oldClubNameRepository;
    @Mock
    private DateHelper dateHelper;
    @Mock
    private ClubUpdateProducer clubUpdateNotifier;
    @Mock
    private FixtureService fixtureService;

    private final ClubEntityToDomainConverter clubConverter = new ClubEntityToDomainConverter(new ClubNameEntityToDomainConverter());

    private ClubService clubService;

    @BeforeEach
    void setUp() {
        clubService = new ClubServiceImpl(clubRepository,
                clubConverter,
                oldClubNameRepository,
                dateHelper,
                clubUpdateNotifier,
                fixtureService);
    }


    @Test
    void shouldGetExpectedClub() {
        long id = 1823;
        ClubEntity clubEntity = ClubEntity.builder().name("Expected").build();
        Club expected = Club.builder().internalName("Expected").build();

        when(clubRepository.findById(id)).thenReturn(Optional.of(clubEntity));

        Club actual = clubService.getClub(id);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenClubNotFound() {
        long id = 1823;

        when(clubRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> clubService.getClub(id))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetClubEntity() {
        long id = 1823;
        ClubEntity clubEntity = ClubEntity.builder().name("Expected").build();

        when(clubRepository.findById(id)).thenReturn(Optional.of(clubEntity));

        ClubEntity actual = clubService.getClubEntity(id);

        assertThat(actual).isEqualTo(clubEntity);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenClubEntityNotFound() {
        long id = 1823;

        when(clubRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> clubService.getClubEntity(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldReturnAllClubs() {
        ClubEntity clubEntity1 = ClubEntity.builder().name("Expected").build();
        ClubEntity clubEntity2 = ClubEntity.builder().name("Also expected").build();
        Club club1 = Club.builder().internalName("Expected").build();
        Club club2 = Club.builder().internalName("Also expected").build();
        List<ClubEntity> clubEntities = newArrayList(clubEntity1, clubEntity2);
        List<Club> expected = newArrayList(club1, club2);


        when(clubRepository.findAll()).thenReturn(clubEntities);

        List<Club> actual = clubService.getAllClubs();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnAllClubsStartingWithA() {
        ClubEntity clubEntity1 = ClubEntity.builder().name("A club").build();
        Club club1 = Club.builder().internalName("A club").build();
        List<ClubEntity> clubEntities = newArrayList(clubEntity1);
        List<Club> expected = newArrayList(club1);

        when(clubRepository.findByNameStartingWithOrderByName('A')).thenReturn(clubEntities);

        List<Club> actual = clubService.getClubsByInitial('A');

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnAllClubsStartingWithLowercaseA() {
        ClubEntity clubEntity1 = ClubEntity.builder().name("A club").build();
        Club club1 = Club.builder().internalName("A club").build();
        List<ClubEntity> clubEntities = newArrayList(clubEntity1);
        List<Club> expected = newArrayList(club1);

        when(clubRepository.findByNameStartingWithOrderByName('A')).thenReturn(clubEntities);

        List<Club> actual = clubService.getClubsByInitial('a');

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnAllClubsForSeasonInAlphabeticalOrder() {
        ClubEntity clubEntity1 = ClubEntity.builder().name("Expected").build();
        ClubEntity clubEntity2 = ClubEntity.builder().name("Also expected").build();
        Club club1 = Club.builder().internalName("Expected").build();
        Club club2 = Club.builder().internalName("Also expected").build();
        Set<Long> clubIds = newHashSet(3L, 8L);
        Set<Long> clubIdsWithHendon = newHashSet(1L, 3L, 8L);
        List<ClubEntity> clubEntities = newArrayList(clubEntity1, clubEntity2);

        when(dateHelper.getCurrentSeason()).thenReturn(2010);
        when(fixtureService.getOpponentIdsForSeason(2010)).thenReturn(clubIds);
        when(clubRepository.findAllById(clubIdsWithHendon)).thenReturn(clubEntities);

        List<Club> actual = clubService.getClubsForCurrentSeason();

        assertThat(actual).containsExactly(club2, club1);
    }

    @Test
    void shouldNotifyListenersIfClubIsCreated() {
        Club club = Club.builder().internalName("Hendon").build();
        ClubEntity entity = ClubEntity.builder().name("Hendon").build();
        ClubEntity savedEntity = ClubEntity.builder().id(1L).name("Hendon").build();
        Club savedClub = Club.builder().id(1L).internalName("Hendon").build();

        when(clubRepository.save(entity)).thenReturn(savedEntity);
        clubService.createClub(club);

        verify(clubUpdateNotifier).publishCreate(savedClub);
    }

    @Test
    void shouldNotNotifyListenersIfClubCreationFails() {
        Club club = Club.builder().internalName("Hendon").build();
        ClubEntity entity = ClubEntity.builder().name("Hendon").build();

        when(clubRepository.save(entity)).thenThrow(new IllegalArgumentException());

        assertThatThrownBy(() -> clubService.createClub(club))
                .isInstanceOf(IllegalArgumentException.class);
        verify(clubUpdateNotifier, never()).publishCreate(any());
    }

    @Test
    void shouldGetNameForHendonInAGivenSeason() {
        ClubEntity clubEntity = ClubEntity.builder().name("Hendon").build();

        when(clubRepository.findById(HENDON_ID)).thenReturn(Optional.of(clubEntity));

        String actual = clubService.getHendonTeamName(1908);

        assertThat(actual).isEqualTo("Hendon");
    }

    @Test
    void shouldNotifyListenersIfClubIsUpdated() {
        long id = 1L;
        Club newClubDetails = Club.builder().internalName("Harrow").build();
        ClubEntity oldClubDetails = ClubEntity.builder().name("Hendon").build();
        ClubEntity expectedEntity = ClubEntity.builder().name("Harrow").build();
        Club expectedMessageEntity = Club.builder().internalName("Harrow").build();

        when(clubRepository.findById(id)).thenReturn(Optional.of(oldClubDetails));
        clubService.editClub(id, newClubDetails);

        verify(clubRepository).save(expectedEntity);
        verify(clubUpdateNotifier).publishUpdate(expectedMessageEntity);
    }

    @Test
    void shouldNotNotifyListenersIfClubUpdateFails() {
        long id = 1L;
        Club newClubDetails = Club.builder().internalName("Hendon").build();
        ClubEntity oldClubDetails = ClubEntity.builder().name("Hendon").build();
        ClubEntity expectedEntity = ClubEntity.builder().name("Hendon").build();

        when(clubRepository.findById(id)).thenReturn(Optional.of(oldClubDetails));
        when(clubRepository.save(expectedEntity)).thenThrow(new IllegalArgumentException());

        assertThatThrownBy(() -> clubService.editClub(id, newClubDetails))
                .isInstanceOf(IllegalArgumentException.class);

        verify(clubUpdateNotifier, never()).publishUpdate(any());
    }

    @Test
    void shouldNotNotifyListenersIfClubIsNotFoundForUpdate() {
        long id = 1L;
        Club newClubDetails = Club.builder().internalName("Hendon").build();

        when(clubRepository.findById(id)).thenThrow(new NotFoundException());


        assertThatThrownBy(() -> clubService.editClub(id, newClubDetails))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldNotifyListenersWhenOldNameAdded() {
        long id = 1823;
        ClubNameEntity clubNameEntity = ClubNameEntity.builder().clubId(id).build();
        ClubName clubName = ClubName.builder().build();
        ClubEntity clubEntity = ClubEntity.builder().id(id).name("Expected").build();
        ClubEntity updatedEntity = ClubEntity.builder()
                .id(id)
                .name("Expected")
                .oldNames(newHashSet(clubNameEntity)).build();
        ClubEntity returnedEntity = ClubEntity.builder()
                .name("Returned")
                .oldNames(newHashSet(clubNameEntity)).build();
        Club notificationEntity = Club.builder()
                .internalName("Returned")
                .names(newHashSet(clubName)).build();

        when(clubRepository.findById(id)).thenReturn(Optional.of(clubEntity));
        when(clubRepository.save(updatedEntity)).thenReturn(returnedEntity);
        clubService.addOldName(id, clubName);

        verify(clubUpdateNotifier).publishUpdate(notificationEntity);
    }

    @Test
    void shouldNotNotifyListenersWhenCouldNotFindParentOfOldName() {
        long id = 1823;
        ClubName clubName = ClubName.builder().build();

        when(clubRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clubService.addOldName(id, clubName))
            .isInstanceOf(NotFoundException.class);

        verify(clubUpdateNotifier, never()).publishUpdate(any());
    }

    @Test
    void shouldNotNotifyListenersWhenCouldNotSaveOldName() {
        long id = 1823;
        ClubNameEntity clubNameEntity = ClubNameEntity.builder().clubId(id).build();
        ClubName clubName = ClubName.builder().build();
        ClubEntity clubEntity = ClubEntity.builder().id(id).name("Expected").build();
        ClubEntity updatedEntity = ClubEntity.builder()
                .id(id)
                .name("Expected")
                .oldNames(newHashSet(clubNameEntity)).build();

        when(clubRepository.findById(id)).thenReturn(Optional.of(clubEntity));
        when(clubRepository.save(updatedEntity)).thenThrow(new IllegalArgumentException());


        assertThatThrownBy(() -> clubService.addOldName(id, clubName))
            .isInstanceOf(IllegalArgumentException.class);

        verify(clubUpdateNotifier, never()).publishUpdate(any());
    }

    @Test
    void shouldDeleteOldClubName() {
        long clubNameId = 138183;
        long clubId = 948174;

        ClubNameEntity clubNameEntity = ClubNameEntity.builder().clubId(clubId).build();
        ClubEntity clubEntity = ClubEntity.builder().name("Expected").build();
        Club club = Club.builder().internalName("Expected").build();

        when(oldClubNameRepository.findById(clubNameId)).thenReturn(Optional.of(clubNameEntity));
        when(clubRepository.findById(clubId)).thenReturn(Optional.of(clubEntity));

        clubService.removeOldName(clubNameId);

        verify(clubUpdateNotifier).publishUpdate(club);
    }
}
