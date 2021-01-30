/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.domain.CompetitionType;
import net.greensnet.dao.FixtureAppearanceRepository;
import net.greensnet.dao.FixtureGoalRepository;
import net.greensnet.domain.AppearanceRow;
import net.greensnet.domain.PlayerAppearanceRow;
import net.greensnet.domain.StaffMember;
import net.greensnet.domain.TimeAtClub;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static net.greensnet.competition.domain.CompetitionType.*;

@Service
public class AppearanceServiceImpl implements AppearanceService {

    private final StaffService staffService;
    private final CompetitionService competitionService;
    private final FixtureAppearanceRepository fixtureAppearanceRepository;
    private final FixtureGoalRepository fixtureGoalRepository;
    private final DateHelper dateHelper;

    @Autowired
    public AppearanceServiceImpl(StaffService staffService,
                                 CompetitionService competitionService,
                                 FixtureAppearanceRepository fixtureAppearanceRepository,
                                 FixtureGoalRepository fixtureGoalRepository,
                                 DateHelper dateHelper) {
        this.staffService = staffService;
        this.competitionService = competitionService;
        this.fixtureAppearanceRepository = fixtureAppearanceRepository;
        this.fixtureGoalRepository = fixtureGoalRepository;
        this.dateHelper = dateHelper;
    }

    public List<PlayerAppearanceRow> getPlayerApps(StaffMember player) {
        List<Integer> seasonList = getSeasonsForPlayer(player);
        if (seasonList.isEmpty()) {
            return null;
        }
        List<PlayerAppearanceRow> table = new ArrayList<>();
        for (int season : seasonList) {
            PlayerAppearanceRow row = PlayerAppearanceRow.builder().build();
            row.setSeason(season);
            populateAppearanceDetails(player, season, row);
            table.add(row);
        }
        return table;
    }

    private void populateAppearanceDetails(StaffMember player, int season, PlayerAppearanceRow row) {
        row.setCupGoals(getCupGoals(player, season));
        row.setCupStarts(getCupStarts(player, season));
        row.setCupSub(getCupSubs(player, season));
        row.setLeagueGoals(getLeagueGoals(player, season));
        row.setLeagueStarts(getLeagueStarts(player, season));
        row.setLeagueSub(getLeagueSubs(player, season));
        row.setFriendlyGoals(getFriendlyGoals(player, season));
        row.setFriendlyStarts(getFriendlyStarts(player, season));
        row.setFriendlySub(getFriendlySubs(player, season));
    }

    private void populateAppearanceDetails(StaffMember player, int season, AppearanceRow row) {
        row.setCupGoals(getCupGoals(player, season));
        row.setCupStarts(getCupStarts(player, season));
        row.setCupSub(getCupSubs(player, season));
        row.setLeagueGoals(getLeagueGoals(player, season));
        row.setLeagueStarts(getLeagueStarts(player, season));
        row.setLeagueSub(getLeagueSubs(player, season));
        row.setFriendlyGoals(getFriendlyGoals(player, season));
        row.setFriendlyStarts(getFriendlyStarts(player, season));
        row.setFriendlySub(getFriendlySubs(player, season));
    }

    public List<AppearanceRow> getAppearances(int season) {
        List<AppearanceRow> table = getPlayersForSeason(season);
        addOwnGoals(table, season);
        return table;
    }

    private List<AppearanceRow> getPlayersForSeason(int season) {
        List<StaffMember> playerList = staffService.getPlayersForSeason(season, false, false);
        ArrayList<AppearanceRow> table = new ArrayList<>();
        for (StaffMember player : playerList) {
            AppearanceRow row = new AppearanceRow();
            row.setPlayer(player);
            row.setPlayerName(player.getFirstNameForSeason(season) + " " + player.getLastNameForSeason(season));
            row.setSortName(player.getLastNameForSeason(season) + "|" + player.getFirstNameForSeason(season));
            row.setOnLoan(player.isOnLoan(season));
            populateAppearanceDetails(player, season, row);
            table.add(row);
        }
        sortTable(table);
        return table;
    }

    private List<AppearanceRow> sortTable(ArrayList<AppearanceRow> table) {
        List<AppearanceRow> list = Collections.synchronizedList(table);
        Collections.sort(list);
        return list;
    }

    private void addOwnGoals(List<AppearanceRow> table, int season) {
        AppearanceRow row = new AppearanceRow();
        row.setLeagueGoals(getLeagueGoals(null, season));
        row.setCupGoals(getCupGoals(null, season));
        row.setFriendlyGoals(getFriendlyGoals(null, season));
        table.add(row);
    }

    private int getLeagueGoals(StaffMember player, int season) {
        return getGoals(player, season, LEAGUE);
    }

    private int getCupGoals(StaffMember player, int season) {
        return getGoals(player, season, CUP);
    }

    private int getFriendlyGoals(StaffMember player, int season) {
        return getGoals(player, season, FRIENDLY);
    }

    private int getGoals(StaffMember player, int season, CompetitionType type) {
        if (Objects.isNull(player)) {
            Optional<Integer> result = fixtureGoalRepository.getOwnGoals(season, type);
            return result.orElse(0);
        }

        Optional<Integer> result = fixtureGoalRepository.getGoalsScored(season, type, player);
        return result.orElse(0);
    }

    private int getLeagueStarts(StaffMember player, int season) {
        return getStarts(player, season, LEAGUE);
    }

    private int getCupStarts(StaffMember player, int season) {
        return getStarts(player, season, CUP);
    }

    private int getFriendlyStarts(StaffMember player, int season) {
        return getStarts(player, season, FRIENDLY);
    }

    private List<Integer> getSeasonsForPlayer(StaffMember player) {
        List<Integer> list = new ArrayList<>();
        if (null == player.getTimeAtClub()) {
            return list;
        }
        for (int i = 0; i < player.getTimeAtClub().size(); i++) {
            TimeAtClub period = player.getTimeAtClub().get(i);
            if (!"Player".equals(period.getStaffRole().getName())) {
                continue;
            }
            int endSeason = period.getEndSeason();
            if (-1 == endSeason) {
                endSeason = dateHelper.getCurrentSeason();
            }
            for (int j = period.getStartSeason(); j <= endSeason; j++) {
                list.add(j);
            }
        }
        Integer temp;
        Integer item1;
        Integer item2;
        for (int i = list.size(); i > 0; i--) {
            for (int j = 1; j < i; j++) {
                item1 = list.get(j - 1);
                item2 = list.get(j);
                if (item1.compareTo(item2) < 0) {
                    temp = list.get(j - 1);
                    list.set(j - 1, list.get(j));
                    list.set(j, temp);
                }
            }
        }
        return list;
    }

    private int getStarts(StaffMember player, int season, CompetitionType type) {
        Optional<Integer> result = fixtureAppearanceRepository.getStartAppearances(season, type, player);
        return result.orElse(0);
    }

    private int getLeagueSubs(StaffMember player, int season) {
        return getSubs(player, season, LEAGUE);
    }

    private int getCupSubs(StaffMember player, int season) {
        return getSubs(player, season, CUP);
    }

    private int getFriendlySubs(StaffMember player, int season) {
        return getSubs(player, season, FRIENDLY);
    }

    private int getSubs(StaffMember player, int season, CompetitionType type) {
        Optional<Integer> result = fixtureAppearanceRepository.getSubAppearances(season, type, player);
        return result.orElse(0);
    }
}
