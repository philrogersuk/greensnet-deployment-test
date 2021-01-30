package net.greensnet.club.service;

import net.greensnet.club.dao.StadiumEntity;

import java.util.List;

public interface StadiumService {

    boolean createStadium(String colloquialName,
            String actualName,
            String address,
            String postcode,
            String byCar,
            String byTrain,
            String byBus,
            String byTube);

    boolean editStadium(long stadiumId,
                        String colloquialName,
                        String actualName,
                        String address,
                        String postcode,
                        String byCar,
                        String byTrain,
                        String byBus,
                        String byTube);

    boolean removeStadium(long stadiumId);

    List<StadiumEntity> getAll();

}
