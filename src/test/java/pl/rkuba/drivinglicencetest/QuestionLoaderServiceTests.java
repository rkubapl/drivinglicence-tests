package pl.rkuba.drivinglicencetest;

import org.junit.jupiter.api.Test;
import pl.rkuba.drivinglicencetest.model.BasicQuestion;
import pl.rkuba.drivinglicencetest.model.Category;
import pl.rkuba.drivinglicencetest.model.Question;
import pl.rkuba.drivinglicencetest.model.SpecialistQuestion;
import pl.rkuba.drivinglicencetest.services.QuestionLoaderService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static pl.rkuba.drivinglicencetest.model.Category.*;

public class QuestionLoaderServiceTests {
    private final QuestionLoaderService service = new QuestionLoaderService();

    private String[] validBasic() {
        return new String[]{"1", "99", "Czy w tej sytuacji masz obowiązek zatrzymać pojazd?", "", "", "", "T", "AK_D05_06_org.wmv", "PODSTAWOWY", "3", "A,B,C,D,T,AM,A1,A2,B1,C1,D1", "", "", "", "", "Are you obliged to stop the vehicle in the presented situation?", "", "", "", "Bist du in der dargestellten Situation dazu verpflichtet, das Fahrzeug anzuhalten?", "", "", "", "Чи зобов'язані Ви зупинити свій транспортний засіб у цій ситуації?", "", "", ""};
    }

    private String[] validSpecialist() {
        return new String[]{"1917","7249","Z jaką maksymalną dopuszczalną prędkością możesz jechać, kierując samochodem osobowym o dopuszczalnej masie całkowitej 3 t, na drodze ekspresowej jednojezdniowej?","120 km/h.","100 km/h.","90 km/h.","B","5A109.jpg","SPECJALISTYCZNY","3","B","","","","","At what maximum speed limit can you drive a passenger car of maximum permissible weight 3 t, on a single-carriage expressway?","120 km/h.","100 km/h.","90 km/h.","Mit welcher Höchstgeschwindigkeit darf man einen PkW mit dem zulässigen Gesamtgewicht auf einer Schnellstraße mit einer Fahrbahn fahren?","120 km/h","100 km/h","90 km/h","З якою максимальною швидкістю може їхати легковий автомобіль з максимальною дозволеною масою 3 т на швидкісній дорозі (S) з однією проїзною частиною?","120 км/год.","100 км/год.","90 км/год."};
    }

    @Test
    void testBasicQuestion() {
        String[] testData = validBasic();

        Question result = service.getQuestion(testData);
        assertNotNull(result);
        assertInstanceOf(BasicQuestion.class, result);
        assertEquals(99, result.getQuestionNumber());
        assertEquals("Czy w tej sytuacji masz obowiązek zatrzymać pojazd?", result.getQuestion());
        assertEquals("AK_D05_06_org.wmv", result.getMedia());
        assertEquals(3, result.getPoints());
        Set<Category> categorySet = Set.of(A,B,C,D,T,AM,A1,A2,B1,C1,D1);
        assertEquals(categorySet, result.getCategories());

        BasicQuestion questionResult = (BasicQuestion) result;
        assertTrue(questionResult.getCorrectAnswer());
    }

    @Test
    void testSpecialistQuestion() {
        String[] testData = validSpecialist();

        Question result = service.getQuestion(testData);
        assertNotNull(result);
        assertInstanceOf(SpecialistQuestion.class, result);
        assertEquals(7249, result.getQuestionNumber());
        assertEquals("Z jaką maksymalną dopuszczalną prędkością możesz jechać, kierując samochodem osobowym o dopuszczalnej masie całkowitej 3 t, na drodze ekspresowej jednojezdniowej?", result.getQuestion());
        assertEquals("5A109.jpg", result.getMedia());
        assertEquals(3, result.getPoints());
        Set<Category> categorySet = Set.of(B);
        assertEquals(categorySet, result.getCategories());

        SpecialistQuestion questionResult = (SpecialistQuestion) result;
        assertEquals("120 km/h.", questionResult.getAnswerA());
        assertEquals("100 km/h.", questionResult.getAnswerB());
        assertEquals("90 km/h.", questionResult.getAnswerC());
        assertEquals("B", questionResult.getCorrectAnswerString());
    }

    @Test
    void testTypeOverrideToSpecialistQuestion() {
        String[] testData = validSpecialist();
        testData[QuestionLoaderService.COL_TYPE] = "PODSTAWOWY";

        Question result = service.getQuestion(testData);
        assertNotNull(result);
        assertInstanceOf(SpecialistQuestion.class, result);
    }

    @Test
    void testTypeOverrideToBasicQuestion() {
        String[] testData = validBasic();
        testData[QuestionLoaderService.COL_TYPE] = "SPECJALISTYCZNY";

        Question result = service.getQuestion(testData);
        assertNotNull(result);
        assertInstanceOf(BasicQuestion.class, result);
    }

    @Test
    void testInvalidAnswerBasicQuestion() {
        String[] testData = validBasic();
        testData[QuestionLoaderService.COL_CORRECT_ANSWER] = "TAK";

        assertThrows(IllegalArgumentException.class, () -> service.getQuestion(testData));
    }

    @Test
    void testInvalidAnswerSpecialistQuestion() {
        String[] testData = validSpecialist();
        testData[QuestionLoaderService.COL_CORRECT_ANSWER] = "D";

        assertThrows(IllegalArgumentException.class, () -> service.getQuestion(testData));
    }

    @Test
    void testEmptyAnswerSpecialistQuestion() {
        String[] testData = validSpecialist();
        testData[QuestionLoaderService.COL_ANSWER_C] = "";

        assertThrows(IllegalArgumentException.class, () -> service.getQuestion(testData));
    }

    @Test
    void testEmptyQuestionNumber() {
        String[] testData = validBasic();
        testData[QuestionLoaderService.COL_NUM] = "";

        assertNull(service.getQuestion(testData));
    }

    @Test
    void testNullData() {
        assertNull(service.getQuestion(null));
    }

    @Test
    void testInvalidPoints() {
        String[] testData = validBasic();
        testData[QuestionLoaderService.COL_POINTS] = "a";

        assertThrows(NumberFormatException.class, () -> service.getQuestion(testData));
    }

    @Test
    void testInvalidCategories() {
        String[] testData = validBasic();
        testData[QuestionLoaderService.COL_CATEGORIES] = "B,o,f,d";

        assertThrows(IllegalArgumentException.class, () -> service.getQuestion(testData));
    }

    @Test
    void testEmptyMedia() {
        String[] testData = validBasic();
        testData[QuestionLoaderService.COL_MEDIA] = "";

        Question result = service.getQuestion(testData);
        assertNotNull(result);
        assertEquals(99, result.getQuestionNumber());
        assertEquals("", result.getMedia());
    }
}
