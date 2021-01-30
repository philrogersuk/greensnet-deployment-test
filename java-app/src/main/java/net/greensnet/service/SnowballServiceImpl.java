/*
 * Copyright (c) 2008, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.dao.SnowballDrawRepository;
import net.greensnet.domain.SnowballDraw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Phil
 */
@Service
public class SnowballServiceImpl implements SnowballService {

    private final SnowballDrawRepository repository;

    @Autowired
    public SnowballServiceImpl(SnowballDrawRepository repository) {
        this.repository = repository;
    }


    @Override
    public SnowballDraw getDraw(Long id) {
        Optional<SnowballDraw> optional = repository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public boolean createDraw(LocalDateTime date, String name, int numEntries, float fund, int firstPrizeWinner,
                              float firstPrize, int secondPrizeWinner, float secondPrize, float trustContribution,
                              float snowballContribution) {
        SnowballDraw draw = new SnowballDraw();
        populateDraw(date, name, numEntries, fund, firstPrizeWinner, firstPrize, secondPrizeWinner, secondPrize,
                trustContribution, snowballContribution, draw);
        repository.save(draw);
        return true;
    }

    private static void populateDraw(LocalDateTime date, String name, int numEntries, float fund, int firstPrizeWinner,
                                     float firstPrize, int secondPrizeWinner, float secondPrize, float trustContribution,
                                     float snowballContribution, SnowballDraw draw) {
        draw.setDate(date);
        draw.setName(name);
        draw.setNumEntries(numEntries);
        draw.setFund(fund);
        draw.setFirstPrize(firstPrize);
        draw.setFirstPrizeWinner(firstPrizeWinner);
        draw.setSecondPrize(secondPrize);
        draw.setSecondPrizeWinner(secondPrizeWinner);
        draw.setTrustContribution(trustContribution);
        draw.setSnowballContribution(snowballContribution);
    }

    @Override
    public List<SnowballDraw> getDraws() {
        return repository.findAllByOrderByDateDesc();
    }

    @Override
    public boolean deleteDraw(Long drawId) {
        repository.deleteById(drawId);
        return true;
    }

    @Override
    public boolean editDraw(Long id, LocalDateTime date, String name, int numEntries, float fund, int firstPrizeWinner,
                            float firstPrize, int secondPrizeWinner, float secondPrize, float trustContribution,
                            float snowballContribution) {

        Optional<SnowballDraw> optional = repository.findById(id);
        if (!optional.isPresent()) {
            return false;
        }
        populateDraw(date, name, numEntries, fund, firstPrizeWinner, firstPrize, secondPrizeWinner, secondPrize,
                trustContribution, snowballContribution, optional.get());
        repository.save(optional.get());
        return true;
    }
}
