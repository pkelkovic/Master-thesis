package masters.mainserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import masters.mainserver.entity.SensorStats;

public interface StatsRepository extends JpaRepository<SensorStats, String>{
    @Query("""
    SELECT s
    FROM SensorStats s
    WHERE s.statsId.sensorId = :sensorId
    """)
    List<SensorStats> findStats(@Param("sensorId") String sensorId);
}
