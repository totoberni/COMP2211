
import javafx.application.Platform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import uk.ac.soton.comp2211.g16.ad.Application;
import uk.ac.soton.comp2211.g16.ad.ApplicationController;
import uk.ac.soton.comp2211.g16.ad.SelectCampaign;
import uk.ac.soton.comp2211.g16.ad.SelectCampaignController;
import uk.ac.soton.comp2211.g16.ad.data.CsvFileHandler;
import uk.ac.soton.comp2211.g16.ad.data.DbConnector;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;


public class GeneralTest {


    //testName = "Application Class Compile"
    @Test()
    @DisplayName("Application Class Compile")
    public void testApplicationClassCompile() {
        Application cls = new Application();
        assertNotNull(cls);
    }

    //testName = "ApplicationController Class Compile"
    @Test()
    @DisplayName("ApplicationController Class Compile")
    public void testApplicationControllerClassCompile() {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> {
            ApplicationController cls = new ApplicationController();
            assertNotNull(cls);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //testName = "SelectCampaign Class Compile"
    @Test()
    @DisplayName("SelectCampaign Class Compile")
    public void testSelectCampaignClassCompile() {
        SelectCampaign cls = new SelectCampaign();
        assertNotNull(cls);
    }

    //testName = "SelectCampaignController Class Compile"
    @Test()
    @DisplayName("SelectCampaignController Class Compile")
    public void testSelectCampaignControllerClassCompile() {
        SelectCampaignController cls = new SelectCampaignController();
        assertNotNull(cls);
    }

    //testName = "CsvFileHandler Class Compile"
    @Test()
    @DisplayName("CsvFileHandler Class Compile")
    public void testCsvFileHandlerClassCompile() {
        CsvFileHandler cls = new CsvFileHandler("2_week_campaign_2");
        assertNotNull(cls);
    }


    //testName = "DbConnector Class Compile"
    @Test()
    @DisplayName("DbConnector Class Compile")
    public void testDbConnectorClassCompile() {
        DbConnector cls = new DbConnector();
        assertNotNull(cls);
    }
}