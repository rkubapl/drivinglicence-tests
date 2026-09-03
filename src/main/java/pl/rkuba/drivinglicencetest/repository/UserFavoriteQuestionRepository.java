package pl.rkuba.drivinglicencetest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.rkuba.drivinglicencetest.model.entity.Question;
import pl.rkuba.drivinglicencetest.model.entity.UserFavoriteQuestion;

public interface UserFavoriteQuestionRepository extends JpaRepository<UserFavoriteQuestion, Long> {
    Long deleteUserFavoriteQuestionByQuestionAndUserId(Question question, String userId);
}
