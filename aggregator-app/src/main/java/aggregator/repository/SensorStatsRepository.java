package aggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aggregator.entity.SensorStats;

public interface SensorStatsRepository extends JpaRepository<SensorStats, String>{
    
}
