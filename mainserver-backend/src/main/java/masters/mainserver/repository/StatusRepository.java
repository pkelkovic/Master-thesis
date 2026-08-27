package masters.mainserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import masters.mainserver.entity.SensorStatus;

public interface StatusRepository extends JpaRepository<SensorStatus, String>{
    
}
