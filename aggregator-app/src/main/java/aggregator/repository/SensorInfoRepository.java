package aggregator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import aggregator.entity.SensorInfo;

public interface SensorInfoRepository extends JpaRepository<SensorInfo, String> {
    @Query("SELECT s.sensorID FROM SensorInfo s")
    List<String> findAllSensorIDs();    
}
