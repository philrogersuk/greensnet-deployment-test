package net.greensnet.club.service;

import com.google.common.collect.Lists;
import net.greensnet.club.dao.DirectionsEntity;
import net.greensnet.club.dao.StadiumEntity;
import net.greensnet.club.dao.StadiumRepository;
import net.greensnet.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StadiumServiceImpl implements StadiumService {

    @Autowired
    private final StadiumRepository repository;

    private StadiumServiceImpl(StadiumRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean createStadium(String colloquialName,
                                 String actualName,
                                 String address,
                                 String postcode,
                                 String byCar,
                                 String byTrain,
                                 String byBus,
                                 String byTube) {
        DirectionsEntity directions = DirectionsEntity.builder().bus(byBus)
                .car(byCar)
                .tube(byTube)
                .train(byTrain).build();

        StadiumEntity stadium = StadiumEntity.builder().actualName(actualName)
                .colloquialName(colloquialName)
                .address(address)
                .postcode(postcode)
                .directions(directions).build();

        repository.save(stadium);

        return true;
    }

    @Override
    public boolean editStadium(long id,
                               String colloquialName,
                               String actualName,
                               String address,
                               String postcode,
                               String byCar,
                               String byTrain,
                               String byBus,
                               String byTube) {
        StadiumEntity stadium = repository.findById(id)
                                    .orElseThrow(() -> new NotFoundException("Could not find stadium with ID=" + id));

        stadium.setActualName(actualName);
        stadium.setAddress(address);
        stadium.setColloquialName(colloquialName);
        stadium.setPostcode(postcode);
        stadium.getDirections().setBus(byBus);
        stadium.getDirections().setCar(byCar);
        stadium.getDirections().setTrain(byTrain);
        stadium.getDirections().setTube(byTube);

        repository.save(stadium);

        return true;
    }

    @Override
    public boolean removeStadium(long stadiumId) {
        repository.deleteById(stadiumId);
        return true;
    }

    @Override
    public List<StadiumEntity> getAll() {
        Iterable<StadiumEntity> results = repository.findAll();
        return Lists.newArrayList(results);
    }
}
