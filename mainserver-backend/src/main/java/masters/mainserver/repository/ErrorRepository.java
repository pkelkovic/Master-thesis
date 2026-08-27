package masters.mainserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import masters.mainserver.entity.ErrorMessage;


public interface ErrorRepository extends JpaRepository<ErrorMessage, Long>{
    List<ErrorMessage> findAllByOrderByReceivedAtDesc();
}
