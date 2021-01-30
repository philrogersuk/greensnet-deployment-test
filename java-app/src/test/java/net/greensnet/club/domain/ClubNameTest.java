package net.greensnet.club.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClubNameTest {
	@Test
	void shouldGetFullNameAsShortest() {
		ClubName club = ClubName.builder().name("Harrow Borough").build();
		assertThat(club.getShortestName()).isEqualTo("Harrow Borough");
	}

	@Test
	void shouldGetShortNameAsShortest() {
		ClubName club = ClubName.builder().name("Harrow Borough")
				.shortName("Harrow").build();
		assertThat(club.getShortestName()).isEqualTo("Harrow");
	}

	@Test
	void shouldGetAcronymAsShortest() {
		ClubName club = ClubName.builder().name("Harrow Borough")
				.shortName("Harrow")
				.acronym("HAR")
				.build();
		assertThat(club.getShortestName()).isEqualTo("HAR");
	}


}
