package masters.mainserver.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import masters.mainserver.entity.ErrorMessage;
import masters.mainserver.entity.SensorInfo;
import masters.mainserver.entity.SensorStats;
import masters.mainserver.entity.SensorStatus;
import masters.mainserver.entity.Status;
import masters.mainserver.repository.ErrorRepository;
import masters.mainserver.repository.SensorRepository;
import masters.mainserver.repository.StatsRepository;
import masters.mainserver.repository.StatusRepository;
import masters.mainserver.service.DataService;

@Service
public class DataServiceImpl implements DataService{
    // Database connection:
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private ErrorRepository errorRepository;


    @Override
    public List<SensorInfo> getSensorInfo() {
        return sensorRepository.findAll();
    }


    @Override
    public List<SensorStats> getSensorStats(String sensorId) {
        return statsRepository.findStats(sensorId);
    }


    @Override
    public void addSensorStats(SensorStats stats) {
        statsRepository.save(stats);
    }

    @Override
    public void addSensorInfo(SensorInfo info) {
        sensorRepository.save(info);
    }

    @Override
    public void addErrorMessage(String msg) {
        ErrorMessage error = new ErrorMessage();
        error.setMessage(msg);
        error.setReceivedAt(Instant.now());
        errorRepository.save(error);
    }

    @Override
    public List<String> getErrorMessages() {
        return errorRepository.findAllByOrderByReceivedAtDesc()
                .stream()
                .map(ErrorMessage::getMessage)
                .toList();
    }
    @Override
    public void updateSensorStatus(String id, Status status) {
        Optional<SensorStatus> existingStatus = statusRepository.findById(id);

        // update the sensor's status if it doesn't have one yet or if the value has changed
        if (existingStatus.isEmpty() || existingStatus.get().getStatus() != status) {
            SensorStatus newStatus = new SensorStatus(id, status);
            statusRepository.save(newStatus);
        }
    }

    @Override
    public Status getSensorStatus(String id) {
        return statusRepository.findById(id)
                .map(SensorStatus::getStatus)
                .orElse(null);
    }

}
