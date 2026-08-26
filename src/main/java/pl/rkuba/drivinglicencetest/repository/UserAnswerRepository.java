package pl.rkuba.drivinglicencetest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rkuba.drivinglicencetest.model.UserAnswer;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
}
