package aggregator.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aggregator.entity.Reading;
import aggregator.entity.SensorInfo;
import aggregator.entity.SensorStats;
import aggregator.repository.ReadingRepository;
import aggregator.repository.SensorInfoRepository;
import aggregator.repository.SensorStatsRepository;
import aggregator.service.DataResposibleService;
import aggregator.service.SensorStatsService;
import lombok.NonNull;


@Service
public class DataResposibleServiceImpl implements DataResposibleService{
    // Data repositories:
    @Autowired
    private SensorInfoRepository sensorRepo;
    @Autowired
    private ReadingRepository readingRepo;
    @Autowired
    private SensorStatsRepository statsRepo;

    @Autowired
    private SensorStatsService statsService;
    
    public void addNewSensor(@NonNull SensorInfo sensorInfo) {
        sensorRepo.save(sensorInfo);
        statsService.addNewSensor(sensorInfo.getSensorID());
    }

    public void removeSensor(@NonNull SensorInfo sensor) {
        sensorRepo.delete(sensor);
    }

    public void addNewReading(@NonNull Reading reading) {
        readingRepo.save(reading);
        statsService.addNewReading(reading);
    }

    public void addNewStats(@NonNull SensorStats stats) {
        statsRepo.save(stats);
    }

    public boolean existsById(@NonNull String sensorId) {
        return sensorRepo.existsById(sensorId);
    }

    public List<SensorInfo> getAllSensors() {
        return sensorRepo.findAll();
    }

}
