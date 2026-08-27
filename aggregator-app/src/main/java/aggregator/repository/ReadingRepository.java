package aggregator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import aggregator.entity.Reading;

public interface ReadingRepository extends JpaRepository<Reading, String>{

}
