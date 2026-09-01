package pl.rkuba.drivinglicencetest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.rkuba.drivinglicencetest.model.entity.UserAnswer;
import pl.rkuba.drivinglicencetest.model.dto.UserStats;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    @Query("SELECT DATE(answeredAt) as date, COUNT(answeredAt) as total FROM UserAnswer WHERE userId = :userId GROUP BY DATE(answeredAt)")
    List<UserStats> countAnswersByDate(@Param("userId") String userId);

    Integer countByUserId(String userId);

    @Query("SELECT COUNT(DISTINCT ua.question) FROM UserAnswer ua WHERE ua.userId = :userId")
    Integer countDistinctByQuestion(@Param("userId") String userId);

//    @Query("")
//    Double percentageCorrectFirstAnswers();
}
