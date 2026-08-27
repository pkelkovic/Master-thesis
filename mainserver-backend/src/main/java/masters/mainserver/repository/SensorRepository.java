package masters.mainserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import masters.mainserver.entity.SensorInfo;


@Repository
public interface SensorRepository extends JpaRepository<SensorInfo, String> {

} 
