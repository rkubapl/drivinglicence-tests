package pl.rkuba.drivinglicencetest.services;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import pl.rkuba.drivinglicencetest.model.*;

import java.io.IOException;
import java.util.List;
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
        assertEquals(AnswerLetter.B, questionResult.getCorrectAnswerLetter());
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

        assertThrows(IllegalArgumentException.class, () -> service.getQuestion(testData));
    }

    @Test
    void testEmptyQuestion() {
        String[] testData = validBasic();
        testData[QuestionLoaderService.COL_QUESTION] = "";

        assertThrows(IllegalArgumentException.class, () -> service.getQuestion(testData));
    }


    @Test
    void testNullData() {
        assertThrows(IllegalArgumentException.class, () -> service.getQuestion(null));
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

    @Test
    void testMultipleQuestions() throws CsvValidationException, IOException {
        String inputData = """
        Lp,Numer pytania,Pytanie,Odpowiedź A,Odpowiedź B,Odpowiedź C,Poprawna odp,Media,Zakres struktury,Liczba punktów,Kategorie,Nazwa media tłumaczenie migowe (PJM) treść pyt,Nazwa media tłumaczenie migowe (PJM) treść odp A,Nazwa media tłumaczenie migowe (PJM) treść odp B,Nazwa media tłumaczenie migowe (PJM) treść odp C,Pytanie [EN],Odpowiedź A [EN],Odpowiedź B [EN],Odpowiedź C [EN],Pytanie [D],Odpowiedź A [D],Odpowiedź B [D],Odpowiedź C [D],Pytanie [UA],Odpowiedź A [UA],Odpowiedź B [UA],Odpowiedź C [UA]
        1,99,Czy w tej sytuacji masz obowiązek zatrzymać pojazd?,,,,T,AK_D05_06_org.wmv,PODSTAWOWY,3,"A,B,C,D,T,AM,A1,A2,B1,C1,D1",,,,,Are you obliged to stop the vehicle in the presented situation?,,,,"Bist du in der dargestellten Situation dazu verpflichtet, das Fahrzeug anzuhalten?",,,,Чи зобов'язані Ви зупинити свій транспортний засіб у цій ситуації?,,,
        2,100,Czy w tej sytuacji masz obowiązek zatrzymać pojazd?,,,,T,AK_D10_30_org.wmv,PODSTAWOWY,3,"A,B,C,D,T,AM,A1,A2,B1,C1,D1",pjm100.wmv,,,,Are you obliged to stop the vehicle in the presented situation?,,,,"Sind Sie in der dargestellten Situation dazu verpflichtet, das Fahrzeug anzuhalten?",,,,Чи зобов'язані Ви зупинити свій транспортний засіб у цій ситуації?,,,
        249,1864,"Jakiej kategorii prawo jazdy jest wymagane, gdy chcesz kierować czterokołowcem innym niż lekki?",B1.,A.,AM.,A,,SPECJALISTYCZNY,1,"B,B1",pjm1864.wmv,pjm1864a.wmv,pjm1864b.wmv,pjm1864c.wmv,What category driving license should you have when driving a four-wheeled vehicle other than a light one?,B1.,A.,AM.,"Welcher Kategorie Führerschein soll man haben, wenn man ein anderes als leichtes Vierrad-Fahrzeug lenkt?",B1.,A.,AM.,"Яка категорія водійських прав вимагається для керування квадро циклом (чотириколісним мікроавтомобілем), який не є легким квадроциклом?",B1.,A.,AM.
        """;
        Resource resource = new ByteArrayResource(inputData.getBytes());
        List<Question> response = service.getQuestionsFromResource(resource);
        assertEquals(3, response.size());
    }

    @Test
    void testMultipleQuestionsWithInvalid() throws CsvValidationException, IOException {
        String inputData = """
        Lp,Numer pytania,Pytanie,Odpowiedź A,Odpowiedź B,Odpowiedź C,Poprawna odp,Media,Zakres struktury,Liczba punktów,Kategorie,Nazwa media tłumaczenie migowe (PJM) treść pyt,Nazwa media tłumaczenie migowe (PJM) treść odp A,Nazwa media tłumaczenie migowe (PJM) treść odp B,Nazwa media tłumaczenie migowe (PJM) treść odp C,Pytanie [EN],Odpowiedź A [EN],Odpowiedź B [EN],Odpowiedź C [EN],Pytanie [D],Odpowiedź A [D],Odpowiedź B [D],Odpowiedź C [D],Pytanie [UA],Odpowiedź A [UA],Odpowiedź B [UA],Odpowiedź C [UA]
        1,99,Czy w tej sytuacji masz obowiązek zatrzymać pojazd?,,,,T,AK_D05_06_org.wmv,PODSTAWOWY,3,"A,B,C,D,T,AM,A1,A2,B1,C1,D1",,,,,Are you obliged to stop the vehicle in the presented situation?,,,,"Bist du in der dargestellten Situation dazu verpflichtet, das Fahrzeug anzuhalten?",,,,Чи зобов'язані Ви зупинити свій транспортний засіб у цій ситуації?,,,
        2,100,Czy w tej sytuacji masz obowiązek zatrzymać pojazd?,,,,T,AK_D10_30_org.wmv,PODSTAWOWY,3,"A,B,C,D,T,AM,A1,A2,B1,C1,D1",pjm100.wmv,,,,Are you obliged to stop the vehicle in the presented situation?,,,,"Sind Sie in der dargestellten Situation dazu verpflichtet, das Fahrzeug anzuhalten?",,,,Чи зобов'язані Ви зупинити свій транспортний засіб у цій ситуації?,,,
        249,1864,"Jakiej kategorii prawo jazdy jest wymagane, gdy chcesz kierować czterokołowcem innym niż lekki?",B1.,A.,AM.,A,,SPECJALISTYCZNY,1,"B,B1",pjm1864.wmv,pjm1864a.wmv,pjm1864b.wmv,pjm1864c.wmv,What category driving license should you have when driving a four-wheeled vehicle other than a light one?,B1.,A.,AM.,"Welcher Kategorie Führerschein soll man haben, wenn man ein anderes als leichtes Vierrad-Fahrzeug lenkt?",B1.,A.,AM.,"Яка категорія водійських прав вимагається для керування квадро циклом (чотириколісним мікроавтомобілем), який не є легким квадроциклом?",B1.,A.,AM.
        2,100,,,,,T,AK_D10_30_org.wmv,PODSTAWOWY,3,"A,B,C,D,T,AM,A1,A2,B1,C1,D1",pjm100.wmv,,,,Are you obliged to stop the vehicle in the presented situation?,,,,"Sind Sie in der dargestellten Situation dazu verpflichtet, das Fahrzeug anzuhalten?",,,,Чи зобов'язані Ви зупинити свій транспортний засіб у цій ситуації?,,,
        """;
        Resource resource = new ByteArrayResource(inputData.getBytes());
        List<Question> response = service.getQuestionsFromResource(resource);
        assertEquals(3, response.size());
    }

    @Test
    void testEmptyInput() throws CsvValidationException, IOException {
        String inputData = "";
        Resource resource = new ByteArrayResource(inputData.getBytes());
        List<Question> response = service.getQuestionsFromResource(resource);
        assertEquals(0, response.size());
    }
}
