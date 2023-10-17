
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import uk.ac.soton.comp2211.g16.ad.Application;
import uk.ac.soton.comp2211.g16.ad.ApplicationController;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class BasicFunctionTest {
    private ApplicationController ac;

    @BeforeAll
    public static void initJFX() {
        Platform.startup(() -> {});
    }
    //@AfterAll
    //public static void tearDown() {
    //    Platform.exit();
    //}
    @Test
    @DisplayName("Get Context Description()")
    public void testGetContextDescription() {
        this.ac=new ApplicationController();
        // Test Case 1: All options are true
        String result = ac.getContextDescription(true, true, true, true, true, true);
        assertEquals("All Context, ", result, "All options are true, expected 'All Context, '");

        // Test Case 2: Only blog is true
        result = ac.getContextDescription(true, false, false, false, false, false);
        assertEquals("Blog, ", result, "Only blog is true, expected 'Blog, '");

        // Test Case 3: Only news is true
        result = ac.getContextDescription(false, true, false, false, false, false);
        assertEquals("News, ", result, "Only news is true, expected 'News, '");

        // Test Case 4: Multiple options are true
        result = ac.getContextDescription(true, false, true, false, true, false);
        assertEquals("Blog, Shopping, Hobbies, ", result, "Multiple options are true, expected 'Blog, Shopping, Hobbies, '");
    }

    @Test
    @DisplayName("Get Gender Description()")
    public void testGetGenderDescription() {
        this.ac=new ApplicationController();
        // Test Case 1: All options are true
        String result = ac.getGenderDescription(true,true);
        assertEquals("All Gender, ", result, "All options are true, expected 'All Gender, '");

        // Test Case 2: Only male is true
        result = ac.getGenderDescription(true,false);
        assertEquals("Only Male, ", result, "Only male is true, expected 'Only Male, '");

        // Test Case 3: Only female is true
        result = ac.getGenderDescription(false,true);
        assertEquals("Only Female, ", result, "Only male is true, expected 'Only Female, '");
        }

    @Test
    @DisplayName("Get Age Description()")
    public void testGetAgeDescription() {
        this.ac=new ApplicationController();
        // Test Case 1: All options are true
        String result = ac.getAgeDescription(true,true,true,true,true);
        assertEquals("All Age, ", result, "All options are true, expected 'All Age, '");

        // Test Case 2: Age range from 25 to 54
        result = ac.getAgeDescription(false,true,true,true,false);
        assertEquals("From 25 to 54, ", result, "Age range from 25 to 54, expected 'From 25 to 54, '");

        // Test Case 3: Age range 35+
        result = ac.getAgeDescription(false,false,true,true,true);
        assertEquals("Older than 34, ", result, "Age range 35+, expected 'Older than 34, '");
    }


    @Test
    @DisplayName("Get Income Description()")
    public void testGetIncomeDescription() {
        this.ac=new ApplicationController();
        // Test Case 1: All options are true
        String result = ac.getIncomeDescription(true,true,true);
        assertEquals("All Income, ", result, "All options are true, expected 'All Income, '");

        // Test Case 2: Only Low income and High income are true
        result = ac.getIncomeDescription(true,false,true);
        assertEquals("Low income, High income, ", result, "Only Low income and High income are true, expected 'Low income, High income, '");

        // Test Case 3: Only Medium income
        result = ac.getIncomeDescription(false,true,false);
        assertEquals("Medium income, ", result, "Only Medium income, expected 'Medium income, '");
    }
}
