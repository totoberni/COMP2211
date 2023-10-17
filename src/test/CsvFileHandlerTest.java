import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2211.g16.ad.data.CsvFileHandler;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

public class CsvFileHandlerTest {

    private CsvFileHandler csvFileHandler;


    @BeforeEach
    void setUp() {
        csvFileHandler = new CsvFileHandler();
    }

    @Test
    @DisplayName("Test CsvFileHandler can handle csv file without error")
    public void testHandleCsvFileWithoutError(){
        CsvFileHandler debug = new CsvFileHandler("2_week_campaign_2");
        try{
            debug.processCsvFiles();
        }catch(Exception e){
            //e.printStackTrace();
        }
    }
    @Test
    @DisplayName("Test CsvFileHandler can detect incorrect files")
    void testHeaderCheckerWithIncorrectFiles() {
        File wrongClickLogFile = new File("2_week_campaign_2/impression_log.csv");//Use impression_log here
        File wrongImpressionLogFile = new File("2_week_campaign_2/server_log.csv");//Use server_log here
        File wrongServerLogFile = new File("2_week_campaign_2/click_log.csv");//Use click_log here

        //System.out.println(wrongClickLogFile.getAbsolutePath());

        //Test Case 1: Use impression_log as click_log input
        assertFalse(csvFileHandler.csvHeaderChecker(wrongClickLogFile.getAbsolutePath(), "Date,ID,Click Cost"));

        //Test Case 2: Use server_log as impression_log input
        assertFalse(csvFileHandler.csvHeaderChecker(wrongImpressionLogFile.getAbsolutePath(), "Date,ID,Gender,Age,Income,Context,Impression Cost"));

        //Test Case 3: Use click_log as server_log input
        assertFalse(csvFileHandler.csvHeaderChecker(wrongServerLogFile.getAbsolutePath(), "Entry Date,ID,Exit Date,Pages Viewed,Conversion"));
    }

    @Test
    @DisplayName("Test CsvFileHandler can detect correct files")
    void testHeaderCheckerWithCorrectFiles() {
        File clickLogFile = new File("2_week_campaign_2/click_log.csv");
        File impressionLogFile = new File("2_week_campaign_2/impression_log.csv");
        File serverLogFile = new File("2_week_campaign_2/server_log.csv");

        //System.out.println(wrongClickLogFile.getAbsolutePath());

        //Test Case 1: Check click_log
        assertTrue(csvFileHandler.csvHeaderChecker(clickLogFile.getAbsolutePath(), "Date,ID,Click Cost"));

        //Test Case 2: Check impression_log
        assertTrue(csvFileHandler.csvHeaderChecker(impressionLogFile.getAbsolutePath(), "Date,ID,Gender,Age,Income,Context,Impression Cost"));

        //Test Case 3: Check server_log
        assertTrue(csvFileHandler.csvHeaderChecker(serverLogFile.getAbsolutePath(), "Entry Date,ID,Exit Date,Pages Viewed,Conversion"));
    }

    @Test
    @DisplayName("Test CsvFileHandler can detect a folder with three correct files")
    void testFolderCheckerWithCorrectFolder() {
        File clickLogFile = new File("2_week_campaign_2");
        CsvFileHandler csvFileHandlerNew = new CsvFileHandler(clickLogFile.getAbsolutePath());
        ArrayList<Boolean> expected = new ArrayList<Boolean>() {{
            add(true);
            add(true);
            add(true);
        }};

        assertEquals(expected, csvFileHandlerNew.folderChecker());
    }

    @Test
    @DisplayName("Test CsvFileHandler can detect a folder with missing file")
    void testFolderCheckerWithMissingFileFolder() {
        File clickLogFile = new File("2_week_campaign_2/partiallyInvalidFolder");
        CsvFileHandler csvFileHandlerNew = new CsvFileHandler(clickLogFile.getAbsolutePath());
        ArrayList<Boolean> expected = new ArrayList<Boolean>() {{
            add(true);
            add(false);
            add(true);
        }};

        assertEquals(expected, csvFileHandlerNew.folderChecker());
    }


    @Test
    @DisplayName("Test CsvFileHandler can detect a folder is invalid")
    void testFolderCheckerWithInvalidFolder() {
        File clickLogFile = new File("2_week_campaign_2/invalidFolder");
        CsvFileHandler csvFileHandlerNew = new CsvFileHandler(clickLogFile.getAbsolutePath());
        ArrayList<Boolean> expected = new ArrayList<Boolean>() {{
            add(false);
            add(false);
            add(false);
        }};

        assertEquals(expected, csvFileHandlerNew.folderChecker());
    }



}
