package uk.ac.soton.comp2211.g16.ad;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uk.ac.soton.comp2211.g16.ad.charts.ClickCostChart;
import uk.ac.soton.comp2211.g16.ad.data.DbConnector;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static uk.ac.soton.comp2211.g16.ad.data.DbConnector.getDateRange;


public class ApplicationController implements Runnable {
  @FXML private SplitPane godSplitPane;
  @FXML private VBox godVBox;

  private static int nCampBeforeClick =0;
  private static int nCampAfterClick = 0;
  private static boolean ischart = false;
  @FXML private TabPane TabPaneMenu;
  @FXML private Line blueBarOverview;
  @FXML private Line blueBarAnalysis;
  @FXML private Line blueBarCampaigns;
  @FXML private Line blueBarSettings;
  @FXML private Label menuTextOverview;
  @FXML private Label menuTextAnalysis;
  @FXML private Label menuTextCampaigns;
  @FXML private Label menuTextSettings;

  @FXML private Label lbl_campaignBeginDate;
  @FXML private Label lbl_campaignEndDate;

  @FXML private ImageView fullScreenIcon;
  @FXML private Label lbl_fullScreen;

  // @FXML private Label metricSelector;
  @FXML private LineChart<String, Number> overviewLineChart;
  @FXML private MenuButton lineChartMenuButton;
  @FXML private ChoiceBox timeGranularityBox = new ChoiceBox(FXCollections.observableArrayList("Day", "Week", "Month","Hour"));
  @FXML private AreaChart<String, Number> analysisAreaChart = new AreaChart<>(new CategoryAxis(), new NumberAxis());
  @FXML private BarChart<String, Number> serverAnalysisChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
  @FXML private MenuButton areaChartMenu = new MenuButton();
  private String currentAreaChartItem = "CPA";
  private Boolean currentAreaChartChanged = false;
  private String currentLineChartItem = "Clicks";
  private Boolean currentLineChartChanged = false;
  private String currentAdvertChartItem = "CTR";
  private Boolean currentAdvertChartChanged = false;
  @FXML private MenuButton advertMenu = new MenuButton();
  @FXML private LineChart<String, Number> advertChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
  @FXML private PieChart genderPieChart = new PieChart();
  @FXML private PieChart agePieChart = new PieChart();
  @FXML private PieChart incomePieChart = new PieChart();
  @FXML private PieChart contextPieChart = new PieChart();
  @FXML private LineChart<String, Number> userGroupChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
  @FXML private BubbleChart<Number, Number> analysisBubbleChart;

  @FXML private Button btn_editFilter;
  @FXML private MenuButton btn_addFilter;


  @FXML private Label totalCostLabel;
  @FXML private Label numberOfImpressionsLabel;
  @FXML private Label numberOfClicksLabel;
  @FXML private Label numberOfUniquesLabel;
  @FXML private Label numberOfBouncesLabel;
  @FXML private Label numberOfAcquistionsLabel;
  @FXML private Label totalCostLabelAnalysis;
  @FXML private Label numberOfImpressionsLabelAnalysis;
  @FXML private Label numberOfClicksLabelAnalysis;
  @FXML private Label numberOfUniquesLabelAnalysis;
  @FXML private Label numberOfBouncesLabelAnalysis;
  @FXML private Label numberOfAcquistionsLabelAnalysis;
  @FXML private Label cpaLabel;
  @FXML private Label cpmLabel;
  @FXML private Label cpcLabel;
  @FXML private Label ctrLabel;
  @FXML private Label bounceRateLabel;
  @FXML private Label averageVisitTimeLabel;
  @FXML private Label cpaLabelAnalysis;
  @FXML private Label cpmLabelAnalysis;
  @FXML private Label cpcLabelAnalysis;
  @FXML private Label ctrLabelAnalysis;


  @FXML private MenuItem menuItem_CancelDelete;
  @FXML private CheckBox ckb_Male;
  @FXML private CheckBox ckb_Female;
  @FXML private CheckBox ckb_youngerThan25;
  @FXML private CheckBox ckb_25to34;
  @FXML private CheckBox ckb_35to44;
  @FXML private CheckBox ckb_45to54;
  @FXML private CheckBox ckb_olderThan54;
  @FXML private CheckBox ckb_LowIncome;
  @FXML private CheckBox ckb_MidIncome;
  @FXML private CheckBox ckb_HighIncome;
  @FXML private CheckBox ckb_Blog;
  @FXML private CheckBox ckb_News;
  @FXML private CheckBox ckb_Shopping;
  @FXML private CheckBox ckb_SocialMedia;
  @FXML private CheckBox ckb_Hobbies;
  @FXML private CheckBox ckb_Travel;
  @FXML private CustomMenuItem customMenuItem_filter;
  @FXML private DatePicker startDateText;
  @FXML private DatePicker endDateText;
  @FXML private RadioButton rb_pageViewed;
  @FXML private RadioButton rb_timeVisited;
  @FXML private ListView filterListView;
  @FXML
  private ChoiceBox fontSizeChoiceBox;
  @FXML
  private ChoiceBox colourSchemeChoiceBox;
  @FXML
  private ChoiceBox languageChoiceBox;
  @FXML
  private RadioButton DarkModeRadioBtn;
  @FXML
  private RadioButton SepiaModeRadioBtn;
  @FXML
  private RadioButton LightModeRadioBtn;
  @FXML
  private AnchorPane campaignBar;
  @FXML
  private GridPane overviewGridOne;
  @FXML
  private GridPane overviewGridTwo;


  @FXML private ImageView loadingGif1;
  @FXML private ImageView loadingGif2;
  @FXML private ImageView loadingGif3;
  @FXML private ImageView loadingGif4;
  Boolean isAnalysisInitialized=false;

  @FXML
  private Tab LCAButton;
  @FXML
  private Tab UGAButton;
  @FXML
  private Tab APAButton;
  @FXML
  private Tab SDAButton;
  @FXML
  private TabPane tabPaneAnalysis;

  @FXML ListView campaignsList;





  private Hashtable<ArrayList<String>, ArrayList<String>> filterOptions = new Hashtable<>();
  private Hashtable<ArrayList<String>, ArrayList<String>> filteredBounceOptions = new Hashtable<>();
  private Boolean isCampaignLoaded =false;
  private java.util.List<HashMap<String, Boolean>> filterGroups = new ArrayList<>();

  private HashMap<Map<String,Boolean>, HashMap<String,XYChart.Series>> currentCharts = new HashMap<>();

  private HashMap<String, XYChart.Series> currentCharts2 = new HashMap<>();

  private HashSet<String> currentCharts3 = new HashSet<>();
  protected HashMap<HashMap<String, Boolean>, String> filterIndexToString = new HashMap<>();
  @FXML
  protected void onBtn_whatThisMeanClick() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText("Please import the campaign data before using the software to view the data. \n" +
            "You can use folders or individual files to import. You can also select History to open");
    alert.showAndWait();
  }
  @FXML
  protected void onBtn_addCampaignClick() {
    nCampBeforeClick ++;
    try {
      Stage stage = new Stage();
      FXMLLoader fxmlLoader =
              new FXMLLoader(SelectCampaign.class.getResource("selectCampaign.fxml"));
      Scene scene = new Scene(fxmlLoader.load(), 600, 400);
      stage.setTitle("ECS Ad - Please select a Campaign to open");
      stage.getIcons().clear();
      stage.getIcons().add(new Image("ECSAD.png"));
      stage.setResizable(false);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(scene);

      Color colour;
      if (LightModeRadioBtn.isSelected())
        colour = Color.GHOSTWHITE;
      else if (DarkModeRadioBtn.isSelected())
        colour = Color.DARKGREY;
      else
        colour = Color.ANTIQUEWHITE;
      setBackgroundForAllComponents(colour, (SplitPane) stage.getScene().getRoot());

      stage.getScene().getWindow().addEventHandler(LoadingFinishedEvent.LOADING_FINISHED, new EventHandler<LoadingFinishedEvent>() {
        @Override
        public void handle(LoadingFinishedEvent event) {

          isCampaignLoaded=true;
          Platform.runLater(() -> {
            onMenuOverviewClick();
          });
        }
      });


      stage.showAndWait();
      System.out.println(stage.getTitle());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  protected void lineChartExportClicked() {
    try {
      ArrayList<String> dataToExport = new ArrayList<>();
      dataToExport.add(totalCostLabel.getText());
      dataToExport.add(numberOfImpressionsLabel.getText());
      dataToExport.add(numberOfClicksLabel.getText());
      dataToExport.add(numberOfUniquesLabel.getText());
      dataToExport.add(numberOfBouncesLabel.getText());
      dataToExport.add(numberOfAcquistionsLabel.getText());
      dataToExport.add(cpaLabel.getText());
      dataToExport.add(cpcLabel.getText());
      dataToExport.add(cpmLabel.getText());
      dataToExport.add(ctrLabel.getText());

      ArrayList<PieChart> pieCharts = new ArrayList<>();
      pieCharts.add(agePieChart);
      pieCharts.add(contextPieChart);
      pieCharts.add(genderPieChart);
      pieCharts.add(incomePieChart);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("exportCampaign.fxml"));
        ExportCampaignController exportCampaignController = new ExportCampaignController(dataToExport, pieCharts, overviewLineChart, analysisAreaChart, serverAnalysisChart, advertChart);
        loader.setController(exportCampaignController);
        Scene scene = new Scene(loader.load(), 600, 400);

      Stage stage = new Stage();
      stage.setTitle("ECS Ad - Please select how you would like to export");
      stage.getIcons().clear();
      stage.getIcons().add(new Image("ECSAD.png"));
      stage.setResizable(false);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(scene);
      stage.setMinWidth(600);
      stage.setMinHeight(400);

      //stage.getScene().getWindow().addEventHandler(ExportedButtoncClick, (EventHandler<LoadingFinishedEvent>) event -> Platform.runLater(() -> {
      //  onMenuOverviewClick();
      //}));

      Color colour;
      if (LightModeRadioBtn.isSelected())
        colour = Color.GHOSTWHITE;
      else if (DarkModeRadioBtn.isSelected())
        colour = Color.DARKGREY;
      else
        colour = Color.ANTIQUEWHITE;
      setBackgroundForAllComponents(colour, (SplitPane) stage.getScene().getRoot());


      stage.showAndWait();
      System.out.println(stage.getTitle());

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @FXML
  protected void lineChartExportLabelClicked() {
    lineChartExportClicked();
  }

  @FXML
  protected void fullscreen() {
    boolean fullScreen = ((Stage) TabPaneMenu.getScene().getWindow()).isFullScreen();
    ((Stage) TabPaneMenu.getScene().getWindow()).setFullScreen(!fullScreen);

    if (lbl_fullScreen.getText().equals("Full Screen")){
      Image exit= new Image(getClass().getResourceAsStream("/uk/ac/soton/comp2211/g16/ad/image/exitfullscreen.png"));
      //full-size_white.png
      fullScreenIcon.setImage(exit);
      lbl_fullScreen.setText("Exit");
    }else{
      lbl_fullScreen.setText("Full Screen");

      Image fullScreenPNG= new Image(getClass().getResourceAsStream("/uk/ac/soton/comp2211/g16/ad/image/full-size_white.png"));
      fullScreenIcon.setImage(fullScreenPNG);
    }



  }

  @FXML
  protected void onLineChartClicks() {
    currentLineChartItem = "Clicks";
    currentLineChartChanged = true;
    initialiseOverviewCharts();
  }

  @FXML
  protected void onLineChartImpressions() {
    currentLineChartItem = "Impressions";
    currentLineChartChanged = true;
    initialiseOverviewCharts();
  }

  @FXML
  protected void onMenuOverviewClick() {
    changeMenuIndex(1);
  }

  @FXML
  protected void onMenuAnalysisClick() {
    changeMenuIndex(2);
  }

  @FXML
  protected void onMenuCampaignsClick() {
    changeMenuIndex(3);
  }

  @FXML
  protected void onMenuSettingsClick() {
    changeMenuIndex(4);
  }

  @FXML
  protected void onAreaChartCPA() {
    currentCharts3.removeIf(s -> s.endsWith("CostChart"));
    currentAreaChartItem = "CPA";
    currentAreaChartChanged = true;
    initializeAreaChart();
  }
  @FXML
  protected void onAreaChartCPTI() {
    currentCharts3.removeIf(s -> s.endsWith("CostChart"));
    currentAreaChartItem = "CPTI";
    currentAreaChartChanged = true;
    initializeAreaChart();
  }
  @FXML
  protected void onAreaChartTotalCost() {
    currentCharts3.removeIf(s -> s.endsWith("CostChart"));
    currentAreaChartItem = "Total Cost";
    currentAreaChartChanged = true;
    initializeAreaChart();
  }
  @FXML
  protected void onAreaChartCPC() {
    currentCharts3.removeIf(s -> s.endsWith("CostChart"));
    currentAreaChartItem = "CPC";
    currentAreaChartChanged = true;
    initializeAreaChart();
  }



  @FXML
  protected void onAdvertChartCTR() {
    currentAdvertChartItem = "CTR";
    currentAdvertChartChanged = true;
    initialiseAdvertChart();
  }

  @FXML
  protected void onAdvertChartBounces() {
    currentAdvertChartItem = "Bounces";
    currentAdvertChartChanged = true;
    initialiseAdvertChart();
  }

  @FXML
  protected void onAdvertChartBounceRate() {
    currentAdvertChartItem = "BounceRate";
    currentAdvertChartChanged = true;
    initialiseAdvertChart();
  }


  private void instantiateFilter() {
    ckb_Male.setSelected(true);
    ckb_Female.setSelected(true);
    ckb_youngerThan25.setSelected(true);
    ckb_25to34.setSelected(true);
    ckb_35to44.setSelected(true);
    ckb_45to54.setSelected(true);
    ckb_olderThan54.setSelected(true);
    ckb_LowIncome.setSelected(true);
    ckb_MidIncome.setSelected(true);
    ckb_HighIncome.setSelected(true);
    ckb_Blog.setSelected(true);
    ckb_News.setSelected(true);
    ckb_Shopping.setSelected(true);
    ckb_SocialMedia.setSelected(true);
    ckb_Hobbies.setSelected(true);
    ckb_Travel.setSelected(true);
    rb_pageViewed.setSelected(true);
    rb_timeVisited.setSelected(false);
  }


  /*@FXML
  protected void updateFontSize() {
    //maybe make a global class that every thing gets its font colour and langauge from:??!??!?!??!
    //Or I can just pass it all from here like a chad
    //or have a settings file I get these from?

    //CHECK WHICH FOTN THEN IF DEFAULT ETC

    // Set the default font for all text in the application

    javafx.scene.text.Font font = new javafx.scene.text.Font("Arial", 14);
    setFontForAllComponents(font, godVBox);
  }

  private void setFontForAllComponents(Font font, Pane container) {
    try {
      for (javafx.scene.Node node : container.getChildren()) {
        if (node instanceof Label) {
          ((Label) node).setFont(font);
        } else if (node instanceof javafx.scene.control.TextField) {
          ((javafx.scene.control.TextField) node).setFont(font);
        } else if (node instanceof Button) {
          ((Button) node).setFont(font);
        } else {
          setFontForAllComponents(font, (Pane) node);
        }
      }
    } catch (Exception e) {
    }
  }
*/

  public ApplicationController() {
    Platform.runLater(this::run);
  }

  @Override
  public void run() {
    Color colour = Color.LIGHTGREY;
    setBackgroundForAllComponents(Color.GHOSTWHITE, godVBox);
    setBackgroundForAllComponents(colour, campaignBar);
    setBackgroundForAllComponents(colour, overviewGridOne);
    setBackgroundForAllComponents(colour, overviewGridTwo);
    tabPaneAnalysis.setStyle("-fx-background-color: ghostwhite;");
  }

  @FXML
  protected void onRadioButtonDarkChange() {

    if (!LightModeRadioBtn.isSelected() && !SepiaModeRadioBtn.isSelected())
      DarkModeRadioBtn.setSelected(true);
    Color colour = Color.GREY;
    setBackgroundForAllComponents(Color.DARKGRAY, godVBox);
    setBackgroundForAllComponents(colour, campaignBar);
    setBackgroundForAllComponents(colour, overviewGridOne);
    setBackgroundForAllComponents(colour, overviewGridTwo);
    tabPaneAnalysis.setStyle("-fx-background-color: darkgrey;");
    //String tabStyle = "-fx-background-color: grey;";
    //LCAButton.setStyle(tabStyle);
    //UGAButton.setStyle(tabStyle);
    //APAButton.setStyle(tabStyle);
    //SDAButton.setStyle(tabStyle);
    LightModeRadioBtn.setSelected(false);
    SepiaModeRadioBtn.setSelected(false);
  }

  @FXML
  protected void onRadioButtonLightChange() {
    if (!DarkModeRadioBtn.isSelected() && !SepiaModeRadioBtn.isSelected())
      LightModeRadioBtn.setSelected(true);
    Color colour = Color.LIGHTGREY;
    setBackgroundForAllComponents(Color.GHOSTWHITE, godVBox);
    setBackgroundForAllComponents(colour, campaignBar);
    setBackgroundForAllComponents(colour, overviewGridOne);
    setBackgroundForAllComponents(colour, overviewGridTwo);
    tabPaneAnalysis.setStyle("-fx-background-color: ghostwhite;");
    //String tabStyle = "-fx-background-color: lightgrey;";
    //LCAButton.setStyle(tabStyle);
    //UGAButton.setStyle(tabStyle);
    //APAButton.setStyle(tabStyle);
    //SDAButton.setStyle(tabStyle);
    DarkModeRadioBtn.setSelected(false);
    SepiaModeRadioBtn.setSelected(false);

  }

  @FXML
  protected void onRadioButtonSepiaChange() {
    if (!DarkModeRadioBtn.isSelected() && !LightModeRadioBtn.isSelected())
      SepiaModeRadioBtn.setSelected(true);
    Color colour = Color.SLATEGREY;
    setBackgroundForAllComponents(Color.ANTIQUEWHITE, godVBox);
    setBackgroundForAllComponents(colour, campaignBar);
    setBackgroundForAllComponents(colour, overviewGridOne);
    setBackgroundForAllComponents(colour, overviewGridTwo);
    //setBackgroundForAllComponents(Color.SLATEGREY, tabPaneAnalysis);
    //tabPaneAnalysis.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));
    tabPaneAnalysis.setStyle("-fx-background-color: antiquewhite;");
    //String tabStyle = "-fx-background-color: slategray;";
    //LCAButton.setStyle(tabStyle);
    //UGAButton.setStyle(tabStyle);
    //APAButton.setStyle(tabStyle);
    //SDAButton.setStyle(tabStyle);
    LightModeRadioBtn.setSelected(false);
    DarkModeRadioBtn.setSelected(false);
  }

  private static void setBackgroundForAllComponents(Color color, Pane container) {
    try {
      container.setBackground(new Background(new BackgroundFill(color, null, null)));
      for (javafx.scene.Node node : container.getChildren()) {
        if (node instanceof Pane) {
          setBackgroundForAllComponents(color, (Pane) node);
        } else if (node instanceof AnchorPane) {
          setBackgroundForAllComponents(color, (AnchorPane) node);
          ((AnchorPane) node).setBackground(new Background(new BackgroundFill(color, null, null)));
        } else if (node instanceof SplitPane) {
          setBackgroundForAllComponents(color, (SplitPane) node);
        } else if (node instanceof TabPane) {
          //setBackgroundForAllComponents(color, (TabPane) node);
        }
      }
    } catch (Exception e) {
    }
  }

  private static void setBackgroundForAllComponents(Color color, TabPane container) {
    try {
      container.setBackground(new Background(new BackgroundFill(color, null, null)));
      for (javafx.scene.Node node : container.getChildrenUnmodifiable()) {
        if (node instanceof Pane) {
          setBackgroundForAllComponents(color, (Pane) node);
        } else if (node instanceof AnchorPane) {
          setBackgroundForAllComponents(color, (AnchorPane) node);
        } else if (node instanceof SplitPane) {
          setBackgroundForAllComponents(color, (SplitPane) node);
        } else if (node instanceof TabPane) {
          //setBackgroundForAllComponents(color, (TabPane) node);
        }
      }
    } catch (Exception e) {
    }
  }

  private static void setBackgroundForAllComponents(Color color, SplitPane container) {
    try {
      container.setBackground(new Background(new BackgroundFill(color, null, null)));
      for (javafx.scene.Node node : container.getItems()) {
        if (node instanceof Pane) {
          setBackgroundForAllComponents(color, (Pane) node);
        } else if (node instanceof AnchorPane) {
          setBackgroundForAllComponents(color, (AnchorPane) node);
        } else if (node instanceof SplitPane) {
          setBackgroundForAllComponents(color, (SplitPane) node);
        } else if (node instanceof TabPane) {
          //setBackgroundForAllComponents(color, (TabPane) node);
        }
      }
    } catch (Exception e) {
    }
  }

  @FXML
  protected void updateLanguage() {

  }


  public void setDates() {

    HashMap<String,String> campaignDetail = DbConnector.getCampaignDetail();
    lbl_campaignBeginDate.setText(campaignDetail.get("StartDate"));
    lbl_campaignEndDate.setText(campaignDetail.get("EndDate"));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime minDateTime = LocalDateTime.parse(campaignDetail.get("StartDate"), formatter);
    LocalDate minDate = minDateTime.toLocalDate();

    LocalDateTime maxDateTime = LocalDateTime.parse(campaignDetail.get("EndDate"), formatter);
    LocalDate maxDate = maxDateTime.toLocalDate();
    startDateText.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(date.isBefore(minDate)||date.isAfter(maxDate));
      }
    });
    startDateText.setValue(minDate);

    endDateText.setDayCellFactory(picker -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        setDisable(date.isBefore(minDate)||date.isAfter(maxDate));
      }
    });
    endDateText.setValue(maxDate);



    averageVisitTimeLabel.setText("N/A");
  }

  public void changeMenuIndex(int newIndex) {
    int oldIndex = TabPaneMenu.getSelectionModel().getSelectedIndex();

    if (!isCampaignLoaded && (newIndex == 3)) {
      TabPaneMenu.getSelectionModel().select(newIndex);
      blueBarOverview.setVisible(false);
      menuTextOverview.setTextFill(Color.valueOf("#565759"));

      initaliseCampaigns();
      blueBarCampaigns.setVisible(true);
      menuTextCampaigns.setTextFill(Color.valueOf("#185abc"));
    }
    else if (!isCampaignLoaded && ((oldIndex == 0) || (oldIndex == 3))){
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Information");
      alert.setHeaderText("Please load campaign first");
      alert.showAndWait();
    }else{
      TabPaneMenu.getSelectionModel().select(newIndex);
      //System.out.println("Menu index selected: " + newIndex);
      if (oldIndex == 0) {
        //onBtn_addFilterClick();
        setDates();
        //Platform.runLater(this::getFilteredMetrics);
        blueBarOverview.setVisible(false);
        menuTextOverview.setTextFill(Color.valueOf("#565759"));
        initialiseOverviewCharts();
      } else if (oldIndex == 1) {
        blueBarOverview.setVisible(false);
        menuTextOverview.setTextFill(Color.valueOf("#565759"));
      }
      else if (oldIndex == 2) {
        blueBarAnalysis.setVisible(false);
        menuTextAnalysis.setTextFill(Color.valueOf("#565759"));
      } else if (oldIndex == 3) {
        setDates();
        //onBtn_addFilterClick();
        //Platform.runLater(this::getFilteredMetrics);
        blueBarCampaigns.setVisible(false);
        menuTextCampaigns.setTextFill(Color.valueOf("#565759"));
        initialiseOverviewCharts();
      } else if (oldIndex == 4) {
        blueBarSettings.setVisible(false);
        menuTextSettings.setTextFill(Color.valueOf("#565759"));
      } else {
        System.err.println("Menu index error");
      }

      if (newIndex == 0 || newIndex == 1) {
        blueBarOverview.setVisible(true);
        menuTextOverview.setTextFill(Color.valueOf("#185abc"));
        if (!isAnalysisInitialized)
          getFilteredMetrics();
      } else if(newIndex == 2 && !isAnalysisInitialized) {

//      Runnable task = () -> {
//
//        Platform.runLater(() -> {
//          loadingGif1.setVisible(true);
//          loadingGif2.setVisible(true);
//          loadingGif3.setVisible(true);
//          loadingGif4.setVisible(true);
//
//        });
//
//        Platform.runLater(() -> {
//          instantiateFilter();
//          getBaseMetrics();
//          initialiseOverviewCharts(); // needed for pdf
//          initializePieChart();
//          initializeAreaChart();
//          initialiseAdvertChart();
//          intialiseServerDataAnalysisChart();
//          initialiseUserGroupChart();
//          initialiseTimeGranularity();
//        });
//
//        Platform.runLater(() -> {
//          loadingGif1.setVisible(false);
//          loadingGif2.setVisible(false);
//          loadingGif3.setVisible(false);
//          loadingGif4.setVisible(false);
//          isAnalysisInitialized = true;
//        });
//      };
//
//      Thread thread = new Thread(task);
//      thread.setDaemon(true);
//      thread.start();

        loadingGif1.setVisible(true);
        loadingGif2.setVisible(true);
        loadingGif3.setVisible(true);
        loadingGif4.setVisible(true);
//1


        //【】【】】【】【】【】【】【】【】【
        //onBtn_addFilterClick();


        String allFilters = getFilterAndDateSQLString();
//2

        String startDate = "";
        String endDate = "";
        try {
          startDate = startDateText.getValue().toString();
        } catch (Exception e3) {
        }
        try {
          endDate = endDateText.getValue().toString();
        } catch (Exception e3) {
        }
        String impressionFilter = getFilterAndDateSQLString();
        String finalStartDate = startDate;
        String finalEndDate = endDate;
        Service<Map<String, Object>> service = new Service<Map<String, Object>>() {
          @Override
          protected Task<Map<String, Object>> createTask() {
            return new Task<Map<String, Object>>() {
              @Override
              protected Map<String, Object> call() throws Exception {
                Map<String, Object> map = DataUtil.getdata1(allFilters);
                map.putAll(DataUtil.getData2(finalStartDate, finalEndDate,impressionFilter,currentAdvertChartItem,rb_pageViewed.isSelected()));

//              Thread.sleep(10000);
                return map;
              }
            };
          }
        };
        service.setOnRunning(event -> {

        });
        service.setOnSucceeded(event -> {
          instantiateFilter();
          long x1=System.currentTimeMillis();
          setDates();
          //if (!isAnalysisInitialized)
          //  onBtn_addFilterClick();
          getFilteredMetrics();
          System.out.println(System.currentTimeMillis()-x1);

          long x2=System.currentTimeMillis();
          initialiseOverviewCharts(); // needed for pdf
          System.out.println(System.currentTimeMillis()-x2);

          long x3=System.currentTimeMillis();
          initializePieChart2(service.getValue());
          System.out.println(System.currentTimeMillis()-x3);

          long x4=System.currentTimeMillis();
          initializeAreaChart();
          System.out.println(System.currentTimeMillis()-x4);

          long x5=System.currentTimeMillis();
          initialiseAdvertChart2(service.getValue());
          System.out.println(System.currentTimeMillis()-x5);

          long x6=System.currentTimeMillis();
          intialiseServerDataAnalysisChart();
          System.out.println(System.currentTimeMillis()-x6);
          long x7=System.currentTimeMillis();
          initialiseUserGroupChart();
          System.out.println(System.currentTimeMillis()-x7);
          long x8=System.currentTimeMillis();
          initialiseTimeGranularity();
          System.out.println(System.currentTimeMillis()-x8);
          loadingGif1.setVisible(false);
          loadingGif2.setVisible(false);
          loadingGif3.setVisible(false);
          loadingGif4.setVisible(false);
          isAnalysisInitialized = true;
        });
        service.start();




        blueBarAnalysis.setVisible(true);
        menuTextAnalysis.setTextFill(Color.valueOf("#185abc"));
      }else if(newIndex == 2) {
        //graphs are loaded already, no need to refresh
        blueBarAnalysis.setVisible(true);
        menuTextAnalysis.setTextFill(Color.valueOf("#185abc"));
      }else if (newIndex == 3) {
        initaliseCampaigns();
        blueBarCampaigns.setVisible(true);
        menuTextCampaigns.setTextFill(Color.valueOf("#185abc"));
      } else if (newIndex == 4) {
        blueBarSettings.setVisible(true);
        menuTextSettings.setTextFill(Color.valueOf("#185abc"));
      } else {
        System.err.println("Menu index error");
      }

    }

  }

  public void initaliseCampaigns() {
    campaignsList.getItems().removeAll(campaignsList.getItems());
    System.out.println("load recent history");
    //load recent history
    File directory = new File("./");
    File[] files = directory.listFiles((dir, name) -> name.endsWith(".db"));
    if (files != null) {
      for (File file : files) {
        if(file.length() > 0){
          String fileName = file.getName();
          System.out.println("Adding "+ fileName);
          campaignsList.getItems().add(fileName);
        }
      }
    }else{
      System.out.println("no db file found");
    }

  }

  @FXML
  private void loadRecentBtnClick() {
    if (campaignsList.getSelectionModel().getSelectedIndex() == -1) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Information");
      alert.setHeaderText("Please select a campaign file");
      alert.showAndWait();
      System.out.println("NOT selected");
    } else {
      System.out.println("Change data source to: " + (String) campaignsList.getItems().get(campaignsList.getSelectionModel().getSelectedIndex()));
      SqliteUtil.changeSource((String) campaignsList.getItems().get(campaignsList.getSelectionModel().getSelectedIndex()));
      isCampaignLoaded = true;
      for (var filter : filterOptions.keySet()
           ) {
        filterOptions.remove(filter);
      }
      updateFilters();
    }
  }

  @FXML
  private void deleteRecentBtnClick() {
    int index = campaignsList.getSelectionModel().getSelectedIndex();
    if (index == -1) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Information");
      alert.setHeaderText("Please select a campaign file");
      alert.showAndWait();
      System.out.println("NOT selected");
      isCampaignLoaded = false;
    } else {
      File file = new File((String) campaignsList.getItems().get(index));
      if (file.delete()) {
        campaignsList.getItems().remove(campaignsList.getItems().get(index));
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Information");
        alert.setHeaderText("Cannot delete the active campaign");
        alert.showAndWait();
        System.out.println("Cannot delete active campaign");
      }
    }
  }


  public void initialiseAdvertChart2(Map<String, Object> map) {
    advertChart.getXAxis().setLabel("Time");

//    String startDate = "";
//    String endDate = "";
//    try {
//      startDate = startDateText.getValue().toString();
//    } catch (Exception e3) {
//    }
//    try {
//      endDate = endDateText.getValue().toString();
//    } catch (Exception e3) {
//    }

//    String clickFilter = DbConnector.getDateRangeForClickLog(startDate, endDate);
//    String serverFilter = DbConnector.getDateRangeForServerLog(startDate,endDate);
//    String impressionFilter = getFilterAndDateSQLString();
    ArrayList<Number> data = null;
    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

    if (filterGroups.isEmpty()) {
      System.out.println("--IS EMPTY--"); //No filters added. Usually at start
      return;
    } else {
      if (currentAdvertChartChanged) {
        advertChart.getData().removeAll(advertChart.getData());
        System.out.println("--IS CHANGED--"); //Dropdown menu changed
      } else if (currentCharts2.containsKey(getFilterAndDateSQLString() + "Advert" + rb_pageViewed.isSelected() + rb_timeVisited.isSelected())){
        System.out.println("--CONTAINS KEY--"); //If this chart has already been added?
        System.out.println(currentCharts2);
        return;
      }
    }


    switch (currentAdvertChartItem) {
      case "CTR":
        //advertChart.getData().removeAll(advertChart.getData());
        data = (ArrayList<Number>) map.get("e");
        advertChart.setTitle("Click Through Rate");
        advertChart.getYAxis().setLabel("CTR");
        dataSeries.setName("CTR");
        advertMenu.setText("CTR");
        //advertMenu.show();
        break;

      case "Bounces":
        //advertChart.getData().removeAll(advertChart.getData());
        if (rb_pageViewed.isSelected()) {
          data = (ArrayList<Number>) map.get("e");
          dataSeries.setName("Pages Viewed < 2");
        } else {
          data = (ArrayList<Number>) map.get("e");
          dataSeries.setName("Time Spent <= 10s");
        }
        advertChart.setTitle("Bounces");
        advertChart.getYAxis().setLabel("Bounces");
        advertMenu.setText("Bounces");
        //advertMenu.show();
        break;
      case "BounceRate":
        //advertChart.getData().removeAll(advertChart.getData());
        if (rb_pageViewed.isSelected()) {
          data = (ArrayList<Number>) map.get("e");
          dataSeries.setName("Pages Viewed < 2");
        } else {
          data = (ArrayList<Number>) map.get("e");
          dataSeries.setName("Time Spent <= 10s");
        }
        advertChart.setTitle("Bounce Rate");
        advertChart.getYAxis().setLabel("Bounce Rate");
        advertMenu.setText("Bounce Rate");
        //advertMenu.show();
        break;
      case "ConvRate":
        break;
      default:
        break;
    }


    Integer day = 0;
    for (int i = 0;i < data.size(); i++) {
      dataSeries.getData().add(new XYChart.Data(day.toString(), data.get(i))); ///Test Data?
      day++;
    }

    currentCharts2.put(getFilterAndDateSQLString() + "Advert" + rb_pageViewed.isSelected() + rb_timeVisited.isSelected(), dataSeries);


    Object currSelectedListView = filterListView.getItems().get(filterListView.getSelectionModel().getSelectedIndex());
    if (filterViewToChartMap.containsKey(currSelectedListView)) {
      filterViewToChartMap.get(currSelectedListView).add(dataSeries);
    } else {
      ArrayList<XYChart.Series<String, Number>> tempList = new ArrayList<>();
      tempList.add(dataSeries);
      filterViewToChartMap.put(currSelectedListView, (ArrayList<XYChart.Series<String, Number>>) tempList.clone());
    }
    currentAdvertChartChanged = false;

    advertChart.getData().add(dataSeries);
    advertChart.autosize();
  }


  public void initializePieChart2(Map<String, Object> map) {
    genderPieChart.setAnimated(false);
    incomePieChart.setAnimated(false);
    agePieChart.setAnimated(false);
    contextPieChart.setAnimated(false);
    incomePieChart.animatedProperty().set(false);

//    String allFilters = getFilterAndDateSQLString();

    //System.out.println("This is actually working");
//    HashMap<String, Integer> genderMap = DbConnector.getDataForPieCharts("Gender", "ImpressionLog", allFilters);
//    ObservableList<PieChart.Data> genderDataList = FXCollections.observableArrayList();
//    genderMap.remove("Gender");
//    for (String key: genderMap.keySet()) {
//      genderDataList.add(new PieChart.Data(key, genderMap.get(key)));
//    }
    genderPieChart.getData().clear();
    genderPieChart.setData((ObservableList<PieChart.Data>) map.get("a"));


//    HashMap<String, Integer> incomeMap = DbConnector.getDataForPieCharts("Income", "ImpressionLog", allFilters);
//    ObservableList<PieChart.Data> incomeDataList = FXCollections.observableArrayList();
//    incomeMap.remove("Income");
//    for (String key: incomeMap.keySet()) {
//      incomeDataList.add(new PieChart.Data(key, incomeMap.get(key)));
//
//    }
    incomePieChart.getData().clear();
    incomePieChart.setData((ObservableList<PieChart.Data>) map.get("b"));

//    HashMap<String, Integer> ageMap = DbConnector.getDataForPieCharts("Age", "ImpressionLog", allFilters);
//    ageMap.remove("Age");
//    ObservableList<PieChart.Data> ageDataList = FXCollections.observableArrayList();
//    for (String key: ageMap.keySet()) {
//      ageDataList.add(new PieChart.Data(key, ageMap.get(key)));
//    }
    agePieChart.getData().clear();
    agePieChart.setData((ObservableList<PieChart.Data>) map.get("c"));

//    HashMap<String, Integer> contextMap = DbConnector.getDataForPieCharts("Context", "ImpressionLog", allFilters);
//    ObservableList<PieChart.Data> contextDataList = FXCollections.observableArrayList();
//    contextMap.remove("Context");
//    for (String key: contextMap.keySet()) {
//      contextDataList.add(new PieChart.Data(key, contextMap.get(key)));
//    }
    contextPieChart.getData().clear();
    contextPieChart.setData((ObservableList<PieChart.Data>) map.get("d"));

    genderPieChart.layout();
    agePieChart.layout();
    incomePieChart.layout();
    contextPieChart.layout();


    //refreshPieCharts();
  }

  private void initialiseTimeGranularity() {
    if (timeGranularityBox.getItems().size()!=4){
      timeGranularityBox.getItems().add("Day");
      timeGranularityBox.getItems().add("Week");
      timeGranularityBox.getItems().add("Month");
      timeGranularityBox.getItems().add("Hour");
      timeGranularityBox.getSelectionModel().select(0);

      timeGranularityBox.setOnAction(this::changeTimeGranularity);
    }

  }

  private void changeTimeGranularity(Event event) {
    String selectedItem = timeGranularityBox.valueProperty().getValue().toString();
    String currentTimeGranularityStatement = "%y-%m-%d";

    switch (selectedItem) {
      case "Week":
        currentTimeGranularityStatement = "\'%Y-%W\'";
        break;
      case "Month":
        currentTimeGranularityStatement = "\'%Y-%m\'";
        break;
      case "Hour":
        currentTimeGranularityStatement = "\'%Y-%m-%d %H\'";
        break;
      case "Day":
        currentTimeGranularityStatement = "\'%Y-%m-%d\'";
        break;
      default:
        System.out.println("Time Granularity Not Found");
    }
    DbConnector.setTimeGranularityQuerySnippet(currentTimeGranularityStatement);
    initialiseOverviewCharts(); //needed for pdf
    initializePieChart();
    initializeAreaChart();
    initialiseAdvertChart();
    intialiseServerDataAnalysisChart();
    initialiseUserGroupChart();
  }


  private void getBaseMetrics() {
    ArrayList<String> filtersAsBool = new ArrayList<>();
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("True");
    filtersAsBool.add("NO DATE");
    filtersAsBool.add("NO DATE");
    try {
      ArrayList<String> newFilters = filterOptions.get(filtersAsBool);
      totalCostLabel.setText(newFilters.get(0));
      numberOfClicksLabel.setText(newFilters.get(1));
      numberOfImpressionsLabel.setText(newFilters.get(2));
      numberOfUniquesLabel.setText(newFilters.get(3));
      numberOfAcquistionsLabel.setText(newFilters.get(4));
      cpaLabel.setText(newFilters.get(5));
      cpmLabel.setText(newFilters.get(6));
      cpcLabel.setText(newFilters.get(7));
      ctrLabel.setText(newFilters.get(8));
    } catch (NullPointerException e) {

      float totalCost = DbConnector.getTotalCost("", "");
      DecimalFormat df = new DecimalFormat("#.##");
      String totalCostStr = "£" + df.format(totalCost);
      totalCostLabel.setText(totalCostStr);

      numberOfImpressionsLabel.setText(Integer.toString((int)DbConnector.getNumberOfStatistic("ID", "ImpressionLog", "", "")));
      numberOfClicksLabel.setText(Integer.toString((int)DbConnector.getNumberOfStatistic("*", "ClickLog", "", "")));
      numberOfUniquesLabel.setText(Integer.toString((int)DbConnector.getNumberOfStatistic("DISTINCT ID", "ClickLog", "", "")));

      float numberOfBounces = DbConnector.getBouncesViaPagesVisited("", "", "");
      numberOfBouncesLabel.setText(Integer.toString(Math.round(numberOfBounces)));
      numberOfAcquistionsLabel.setText(Integer.toString((int)DbConnector.getNumberOfStatistic("*", "ServerLog", "AND Conversion = \"Yes\"", "")));



      String cpaStr = "£" + df.format(DbConnector.getCPA("", null, null));
      cpaLabel.setText(cpaStr);

      String cpmStr = "£" + df.format(DbConnector.getCPM("", ""));
      cpmLabel.setText(cpmStr);

      String cpcStr = "£" + df.format(DbConnector.getCPC("",""));
      cpcLabel.setText(cpcStr);

      String ctrStr = df.format(DbConnector.getCTR("", ""))+"%";
      ctrLabel.setText(ctrStr);

      //Default Bounces are these don't question it
      String bounceRateStr = df.format(DbConnector.getBounceRateViaPagesVisited("", "", ""))+"%";
      bounceRateLabel.setText(bounceRateStr);


      HashMap<String,String> campaignDetail = DbConnector.getCampaignDetail();
      lbl_campaignBeginDate.setText(campaignDetail.get("StartDate"));
      lbl_campaignEndDate.setText(campaignDetail.get("EndDate"));

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime minDateTime = LocalDateTime.parse(campaignDetail.get("StartDate"), formatter);
      LocalDate minDate = minDateTime.toLocalDate();

      LocalDateTime maxDateTime = LocalDateTime.parse(campaignDetail.get("EndDate"), formatter);
      LocalDate maxDate = maxDateTime.toLocalDate();
      startDateText.setDayCellFactory(picker -> new DateCell() {
        @Override
        public void updateItem(LocalDate date, boolean empty) {
          super.updateItem(date, empty);
          setDisable(date.isBefore(minDate)||date.isAfter(maxDate));
        }
      });
      startDateText.setValue(minDate);

      endDateText.setDayCellFactory(picker -> new DateCell() {
        @Override
        public void updateItem(LocalDate date, boolean empty) {
          super.updateItem(date, empty);
          setDisable(date.isBefore(minDate)||date.isAfter(maxDate));
        }
      });
      endDateText.setValue(maxDate);



      averageVisitTimeLabel.setText("N/A");
//maybe make this bounce check box in main menu

      ArrayList<String> newFilters = new ArrayList<>();
      newFilters.add(totalCostLabel.getText());
      newFilters.add(numberOfClicksLabel.getText());
      newFilters.add(numberOfImpressionsLabel.getText());
      newFilters.add(numberOfUniquesLabel.getText());
      newFilters.add(numberOfAcquistionsLabel.getText());
      newFilters.add(cpaLabel.getText());
      newFilters.add(cpmLabel.getText());
      newFilters.add(cpcLabel.getText());
      newFilters.add(ctrLabel.getText());
      filterOptions.put(filtersAsBool, newFilters);
    }



    //Analysis Page
    totalCostLabelAnalysis.setText(totalCostLabel.getText());
    numberOfClicksLabelAnalysis.setText(numberOfClicksLabel.getText());
    numberOfImpressionsLabelAnalysis.setText(numberOfImpressionsLabel.getText());
    numberOfUniquesLabelAnalysis.setText(numberOfUniquesLabel.getText());
    numberOfAcquistionsLabelAnalysis.setText(numberOfAcquistionsLabel.getText());
    cpaLabelAnalysis.setText(cpaLabel.getText());
    cpmLabelAnalysis.setText(cpmLabel.getText());
    cpcLabelAnalysis.setText(cpcLabel.getText());
    ctrLabelAnalysis.setText(ctrLabel.getText());
    numberOfBouncesLabelAnalysis.setText(numberOfBouncesLabel.getText());
  }

  private void getFilteredMetrics() {
      ArrayList<String> filtersAsBool = new ArrayList<>();
    try {
      Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
      filtersAsBool.add(selectedGroup.get("male").toString());
      filtersAsBool.add(selectedGroup.get("female").toString());
      filtersAsBool.add(selectedGroup.get("youngerThan25").toString());
      filtersAsBool.add(selectedGroup.get("25to34").toString());
      filtersAsBool.add(selectedGroup.get("35to44").toString());
      filtersAsBool.add(selectedGroup.get("45to54").toString());
      filtersAsBool.add(selectedGroup.get("olderThan54").toString());
      filtersAsBool.add(selectedGroup.get("lowIncome").toString());
      filtersAsBool.add(selectedGroup.get("midIncome").toString());
      filtersAsBool.add(selectedGroup.get("highIncome").toString());
      filtersAsBool.add(selectedGroup.get("blog").toString());
      filtersAsBool.add(selectedGroup.get("news").toString());
      filtersAsBool.add(selectedGroup.get("shopping").toString());
      filtersAsBool.add(selectedGroup.get("socialMedia").toString());
      filtersAsBool.add(selectedGroup.get("hobbies").toString());
      filtersAsBool.add(selectedGroup.get("travel").toString());

      if (startDateText.getValue() == null)
        filtersAsBool.add("NO DATE");
      else
        filtersAsBool.add(startDateText.getValue().toString());
      if (endDateText.getValue() == null)
        filtersAsBool.add("NO DATE");
      else
        filtersAsBool.add(endDateText.getValue().toString());
      try {
        ArrayList<String> newFilters = filterOptions.get(filtersAsBool);
        totalCostLabel.setText(newFilters.get(0));
        numberOfClicksLabel.setText(newFilters.get(1));
        numberOfImpressionsLabel.setText(newFilters.get(2));
        numberOfUniquesLabel.setText(newFilters.get(3));
        numberOfAcquistionsLabel.setText(newFilters.get(4));
        cpaLabel.setText(newFilters.get(5));
        cpmLabel.setText(newFilters.get(6));
        cpcLabel.setText(newFilters.get(7));
        ctrLabel.setText(newFilters.get(8));
      } catch (Exception e) {
        String filter = DbConnector.getWHEREOnFilter(selectedGroup.get("male"), selectedGroup.get("female"), selectedGroup.get("youngerThan25"), selectedGroup.get("25to34"), selectedGroup.get("35to44"), selectedGroup.get("45to54"), selectedGroup.get("olderThan54"), selectedGroup.get("lowIncome"), selectedGroup.get("midIncome"), selectedGroup.get("highIncome"), selectedGroup.get("blog"), selectedGroup.get("news"), selectedGroup.get("shopping"), selectedGroup.get("socialMedia"), selectedGroup.get("hobbies"), selectedGroup.get("travel"));
        String dateRange;

        String startDate = "";
        String endDate = "";
        try{
          startDate = startDateText.getValue().toString();
        } catch(Exception e3){
        }
        try{
          endDate = endDateText.getValue().toString();
        } catch(Exception e3){
        }
        try {
          dateRange = getDateRange(startDate.toString(), endDate.toString());

          DecimalFormat df = new DecimalFormat("#.##");

          String cpaStr = "£" + df.format(DbConnector.getCPA(filter, startDate.toString(), endDate.toString()));
          cpaLabel.setText(cpaStr);


          float numberOfBounces = DbConnector.getNumberOfStatisticFromServerLog("*", filter + " AND Conversion = \"Yes\"", startDate.toString(), endDate.toString());
          numberOfAcquistionsLabel.setText(Integer.toString(Math.round(numberOfBounces)));
        } catch (Exception e2) {
          dateRange = "";

          DecimalFormat df = new DecimalFormat("#.##");
          String cpaStr = "£" + df.format(DbConnector.getCPA(filter, null, null));
          cpaLabel.setText(cpaStr);

          float numberOfBounces = DbConnector.getNumberOfStatisticFromServerLog("*", filter + " AND Conversion = \"Yes\"", null, null);
          numberOfAcquistionsLabel.setText(Integer.toString(Math.round(numberOfBounces)));
        }
        DecimalFormat df = new DecimalFormat("#.##");
        String totalCostStr = "£" + df.format(DbConnector.getTotalCost(filter, dateRange));
        totalCostLabel.setText(totalCostStr);


        numberOfClicksLabel.setText(Integer.toString(Math.round(DbConnector.getNumberOfStatistic("*", "ClickLog", filter, dateRange))));
        numberOfImpressionsLabel.setText(Integer.toString(Math.round(DbConnector.getNumberOfStatistic("ID", "ImpressionLog", filter, dateRange))));
        numberOfUniquesLabel.setText(Integer.toString(Math.round(DbConnector.getNumberOfStatistic("DISTINCT ClickLog.ID", "ClickLog", filter, dateRange))));


        String cpmStr = "£" + df.format(DbConnector.getCPM(filter, dateRange));
        cpmLabel.setText(cpmStr);


        String cpcStr = "£" + df.format(DbConnector.getCPC(filter, dateRange));
        cpcLabel.setText(cpcStr);

        String ctrStr = df.format(DbConnector.getCTR(filter, dateRange))+"%";
        ctrLabel.setText(ctrStr);

        ArrayList<String> newFilters = new ArrayList<>();
        newFilters.add(totalCostLabel.getText());
        newFilters.add(numberOfClicksLabel.getText());
        newFilters.add(numberOfImpressionsLabel.getText());
        newFilters.add(numberOfUniquesLabel.getText());
        newFilters.add(numberOfAcquistionsLabel.getText());
        newFilters.add(cpaLabel.getText());
        newFilters.add(cpmLabel.getText());
        newFilters.add(cpcLabel.getText());
        newFilters.add(ctrLabel.getText());
        filterOptions.put(filtersAsBool, newFilters);
      }
      filtersAsBool.add(selectedGroup.get("pageViewed").toString());
      filtersAsBool.add(selectedGroup.get("timeVisited").toString());

      try {
          ArrayList<String> newFilters = filteredBounceOptions.get(filtersAsBool);
          numberOfBouncesLabel.setText(newFilters.get(0));
          bounceRateLabel.setText(newFilters.get(1));
      } catch (Exception e2) {
        String filter = DbConnector.getWHEREOnFilter(selectedGroup.get("male"), selectedGroup.get("female"), selectedGroup.get("youngerThan25"), selectedGroup.get("25to34"), selectedGroup.get("35to44"), selectedGroup.get("45to54"), selectedGroup.get("olderThan54"), selectedGroup.get("lowIncome"), selectedGroup.get("midIncome"), selectedGroup.get("highIncome"), selectedGroup.get("blog"), selectedGroup.get("news"), selectedGroup.get("shopping"), selectedGroup.get("socialMedia"), selectedGroup.get("hobbies"), selectedGroup.get("travel"));
        String startDate = "";
        String endDate = "";
        try {
          startDate = startDateText.getValue().toString();
        } catch (Exception e3) {
        }
        try {
          endDate = endDateText.getValue().toString();
        } catch (Exception e3) {
        }

        if (rb_pageViewed.isSelected()) {
          numberOfBouncesLabel.setText(Float.toString(DbConnector.getBouncesViaPagesVisited(filter, startDate.toString(), endDate.toString())));
          bounceRateLabel.setText(Float.toString(DbConnector.getBounceRateViaPagesVisited(filter, startDate.toString(), endDate.toString())));
        } else {
          numberOfBouncesLabel.setText(Float.toString(DbConnector.getBouncesViaTimeSpent(filter, startDate.toString(), endDate.toString())));
          bounceRateLabel.setText(Float.toString(DbConnector.getBounceRateViaTimeSpent(filter, startDate.toString(), endDate.toString())));
        }

        ArrayList<String> newFilters = new ArrayList<>();
        newFilters.add(numberOfBouncesLabel.getText());
        newFilters.add(bounceRateLabel.getText());
        filteredBounceOptions.put(filtersAsBool, newFilters);
      }

        averageVisitTimeLabel.setText("N/A");

        totalCostLabelAnalysis.setText(totalCostLabel.getText());
        numberOfClicksLabelAnalysis.setText(numberOfClicksLabel.getText());
        numberOfImpressionsLabelAnalysis.setText(numberOfImpressionsLabel.getText());
        numberOfUniquesLabelAnalysis.setText(numberOfUniquesLabel.getText());
        numberOfAcquistionsLabelAnalysis.setText(numberOfAcquistionsLabel.getText());
        cpaLabelAnalysis.setText(cpaLabel.getText());
        cpmLabelAnalysis.setText(cpmLabel.getText());
        cpcLabelAnalysis.setText(cpcLabel.getText());
        ctrLabelAnalysis.setText(ctrLabel.getText());
        numberOfBouncesLabelAnalysis.setText(numberOfBouncesLabel.getText());
    } catch (Exception e6) {
      onBtn_addFilterClick();
      setDates();
      getFilteredMetrics(); //no filters
    }
  }

  @FXML private void updateFilters() {

/*
    initializeAreaChart();
    initializePieChart();
    initialiseAdvertChart();
    initialiseUserGroupChart();
    initialiseOverviewCharts();
    intialiseServerDataAnalysisChart();
*/

    loadingGif1.setVisible(true);
    loadingGif2.setVisible(true);
    loadingGif3.setVisible(true);
    loadingGif4.setVisible(true);
//1
    String allFilters = getFilterAndDateSQLString();
//2

    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }
    String impressionFilter = getFilterAndDateSQLString();
    String finalStartDate = startDate;
    String finalEndDate = endDate;
    Service<Map<String, Object>> service = new Service<Map<String, Object>>() {
      @Override
      protected Task<Map<String, Object>> createTask() {
        return new Task<Map<String, Object>>() {
          @Override
          protected Map<String, Object> call() throws Exception {
            Map<String, Object> map = DataUtil.getdata1(allFilters);
            map.putAll(DataUtil.getData2(finalStartDate, finalEndDate,impressionFilter,currentAdvertChartItem,rb_pageViewed.isSelected()));

//              Thread.sleep(10000);
            return map;
          }
        };
      }
    };
    service.setOnRunning(event -> {

    });
    service.setOnSucceeded(event -> {
      long x1=System.currentTimeMillis();
      if (filterListView.getItems().size() == 0){
        setDates();
        //if (!isAnalysisInitialized)
         // onBtn_addFilterClick();
        getFilteredMetrics();
      } else{
        getFilteredMetrics();
      }
      System.out.println(System.currentTimeMillis()-x1);


      long x2=System.currentTimeMillis();
      initialiseOverviewCharts();
      System.out.println(System.currentTimeMillis()-x2);

      long x3=System.currentTimeMillis();
      initializePieChart2(service.getValue());
      System.out.println(System.currentTimeMillis()-x3);

      long x4=System.currentTimeMillis();
      initializeAreaChart();
      System.out.println(System.currentTimeMillis()-x4);

      long x5=System.currentTimeMillis();
      initialiseAdvertChart2(service.getValue());
      System.out.println(System.currentTimeMillis()-x5);

      long x6=System.currentTimeMillis();
      intialiseServerDataAnalysisChart();
      System.out.println(System.currentTimeMillis()-x6);
      long x7=System.currentTimeMillis();
      initialiseUserGroupChart();
      System.out.println(System.currentTimeMillis()-x7);
      loadingGif1.setVisible(false);
      loadingGif2.setVisible(false);
      loadingGif3.setVisible(false);
      loadingGif4.setVisible(false);
      isAnalysisInitialized = true;
    });
    service.start();


  }

  @FXML private void startDateListener() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ChronoLocalDate date = null;
    try {
       date = LocalDate.parse(lbl_campaignBeginDate.getText(), formatter);
    } catch (DateTimeParseException e) {
    }

    if (startDateText.getValue().isEqual(date)){
      //do nothing
    }else{
      //other date selected
      getFilteredMetrics();
      initializeAreaChart();
      initialiseOverviewCharts();
      initializePieChart();
      initialiseAdvertChart();
      initialiseUserGroupChart();
      intialiseServerDataAnalysisChart();
    }


  }

  @FXML private void endDateListener() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ChronoLocalDate date = null;
    try {
      date = LocalDate.parse(lbl_campaignEndDate.getText(), formatter);
    } catch (DateTimeParseException e) {
    }

    if (endDateText.getValue().isEqual(date)){
      //do nothing

    }else{
      //other date selected
      getFilteredMetrics();
      initializeAreaChart();
      initializePieChart();
      initialiseAdvertChart();
      initialiseUserGroupChart();
      initialiseOverviewCharts();
      intialiseServerDataAnalysisChart();
    }
  }

  public void refreshPieChart(){
  }
  // TODO: parameters of this method should be the date interval chosen by the user.
  // TODO: Series name is either the filter applied or campaign viewed (Extension)
  // TODO: The "cost" section has to be edited so it becomes a variable affected by what button is

  public void initializeOldLineChart() {

    ischart = true;

    if (nCampBeforeClick == nCampAfterClick|| ischart) return;
    ArrayList<String> dateStorage = DbConnector.getDates();
    ArrayList<Double> priceStorage = DbConnector.getAverageCPC();
    ClickCostChart clc = new ClickCostChart(dateStorage.get(0), dateStorage.get(13));

    // Defining Axes
    //oldLineChart.setTitle("Overview");
    //oldLineChart.getXAxis().setLabel("Time");
    //oldLineChart.getYAxis().setLabel("Cost");
    // Creating series and naming it
    XYChart.Series<Number, Number> series = new XYChart.Series();
    series.setName("Cost" + " for " + " Campaign " + nCampBeforeClick);


    // Using Datetime formatter to create dates for graph
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("-MM-dd");
    LocalDate start = LocalDate.parse(clc.getStartDate(), dtf);
    LocalDate end = LocalDate.parse(clc.getEndDate(), dtf);
    if (start.isAfter(end)|| end.isBefore(start)){
      throw new IllegalArgumentException("The chosen dates are conflicting");
    }

    //Making ArrayList with all dates that must be displayed
    int numDates = dateStorage.size();
    int i = 0;

    while (numDates >=  0|| !start.equals(end)|| i >= priceStorage.size()) {
      series.getData().add(new XYChart.Data(start, priceStorage.get(i)));
      numDates--;
      i++;
      start = ChronoUnit.DAYS.addTo(start, 1);

    }

    //oldLineChart.getData().add(series);
    //oldLineChart.autosize();

  }
  public void initialiseOverviewCharts() {
    overviewLineChart.getXAxis().setLabel("Time");

    String filter = getFilterAndDateSQLString();
    ArrayList<Integer> dataPerDay = null;
    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();


    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }

    switch (currentLineChartItem) {
      case "Clicks":

        overviewLineChart.setTitle("Clicks Analysis");

        String fullFilterString = DbConnector.getDateRangeForServerLog(startDate,endDate);

        overviewLineChart.getData().removeAll(overviewLineChart.getData());
        overviewLineChart.getYAxis().setLabel("Clicks");
        dataPerDay = DbConnector.getDataForClicksPerDayChart("*", "ClickLog", filter);
        dataSeries.setName("Clicks");
        lineChartMenuButton.setText("Clicks");
        //lineChartMenuButton.show();
        break;
      case "Impressions":
        overviewLineChart.setTitle("Impression Analysis");
        overviewLineChart.getYAxis().setLabel("Impressions");
        overviewLineChart.getData().removeAll(overviewLineChart.getData());
        dataPerDay = DbConnector.getDataForImpressionPerDayChart("*", "ImpressionLog", filter);
        dataSeries.setName("Impressions");
        lineChartMenuButton.setText("Impressions");
        //lineChartMenuButton.show();
        break;
      default:
        break;
    }

    Integer day = 0;
    for (int i = 0;i < dataPerDay.size(); i++) {
      dataSeries.getData().add(new XYChart.Data(day, dataPerDay.get(i))); ///Test Data?
      day++;
    }


    overviewLineChart.getData().add(dataSeries);
    overviewLineChart.autosize();

  }

  public void initialiseAdvertChart() {
    advertChart.getXAxis().setLabel("Time");

    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }

    StringBuilder clickFilter = new StringBuilder(DbConnector.getDateRangeForClickLog(startDate, endDate));
    StringBuilder serverFilter = new StringBuilder(DbConnector.getDateRangeForServerLog(startDate,endDate));
    String filter = "";
    try {
      Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
      filter = DbConnector.getWHEREOnFilter(selectedGroup.get("male"), selectedGroup.get("female"), selectedGroup.get("youngerThan25"), selectedGroup.get("25to34"), selectedGroup.get("35to44"), selectedGroup.get("45to54"), selectedGroup.get("olderThan54"), selectedGroup.get("lowIncome"), selectedGroup.get("midIncome"), selectedGroup.get("highIncome"), selectedGroup.get("blog"), selectedGroup.get("news"), selectedGroup.get("shopping"), selectedGroup.get("socialMedia"), selectedGroup.get("hobbies"), selectedGroup.get("travel"));
      if (!filter.equals("")) {
        if (!serverFilter.equals("")) {
          serverFilter.delete(0, 5); //Deletes last OR
          serverFilter = new StringBuilder(" AND" + serverFilter);
        }
        serverFilter = new StringBuilder(filter + serverFilter);
        if (!clickFilter.equals("")) {
          clickFilter.delete(0, 5); //Deletes last OR
          clickFilter = new StringBuilder(" AND" + clickFilter.toString());
        }
        clickFilter = new StringBuilder(filter + clickFilter);
      }
    }
    catch (Exception e) {}

    String impressionFilter = getFilterAndDateSQLString();
    ArrayList<Number> data = null;
    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

    if (filterGroups.isEmpty()) {
      System.out.println("--IS EMPTY--"); //No filters added. Usually at start
      return;
    } else {
      if (currentAdvertChartChanged) {
        advertChart.getData().removeAll(advertChart.getData());
        System.out.println("--IS CHANGED--"); //Dropdown menu changed
      } else if (currentCharts2.containsKey(getFilterAndDateSQLString() + "Advert" + rb_pageViewed.isSelected() + rb_timeVisited.isSelected())){
        System.out.println("--CONTAINS KEY--"); //If this chart has already been added?
        System.out.println(currentCharts2);
        return;
      }
    }



    switch (currentAdvertChartItem) {
      case "CTR":
        //advertChart.getData().removeAll(advertChart.getData());
        data = DbConnector.getCTRChart(impressionFilter);
        advertChart.setTitle("Click Through Rate");
        advertChart.getYAxis().setLabel("CTR");
        dataSeries.setName("CTR");
        advertMenu.setText("CTR");
        //advertMenu.show();
        break;

      case "Bounces":
        //advertChart.getData().removeAll(advertChart.getData());
        if (rb_pageViewed.isSelected()) {
          data = DbConnector.getBouncesChart(clickFilter.toString(), getBouncesCriteriaForChart("Pages"));
          dataSeries.setName("Pages Viewed < 2");
        } else {
          data = DbConnector.getBouncesChart(clickFilter.toString(), getBouncesCriteriaForChart("Time"));
          dataSeries.setName("Time Spent <= 10s");
        }
        advertChart.setTitle("Bounces");
        advertChart.getYAxis().setLabel("Bounces");
        advertMenu.setText("Bounces");
        //advertMenu.show();
        break;
      case "BounceRate":
        //advertChart.getData().removeAll(advertChart.getData());
        if (rb_pageViewed.isSelected()) {
          data = DbConnector.getBounceRateChart(serverFilter.toString(), getBouncesCriteriaForChart("Pages"));
          dataSeries.setName("Pages Viewed < 2");
        } else {
          data = DbConnector.getBounceRateChart(serverFilter.toString(), getBouncesCriteriaForChart("Time"));
          dataSeries.setName("Time Spent <= 10s");
        }
        advertChart.setTitle("Bounce Rate");
        advertChart.getYAxis().setLabel("Bounce Rate");
        advertMenu.setText("Bounce Rate");
        //advertMenu.show();
        break;
      case "ConvRate":
        break;
      default:
        break;
    }


    Integer day = 0;
    for (int i = 0;i < data.size(); i++) {
      dataSeries.getData().add(new XYChart.Data(day.toString(), data.get(i))); ///Test Data?
      day++;
    }

    currentCharts2.put(getFilterAndDateSQLString() + "Advert" + rb_pageViewed.isSelected() + rb_timeVisited.isSelected(), dataSeries);


    Object currSelectedListView = filterListView.getItems().get(filterListView.getSelectionModel().getSelectedIndex());
    if (filterViewToChartMap.containsKey(currSelectedListView)) {
      filterViewToChartMap.get(currSelectedListView).add(dataSeries);
    } else {
      ArrayList<XYChart.Series<String, Number>> tempList = new ArrayList<>();
      tempList.add(dataSeries);
      filterViewToChartMap.put(currSelectedListView, (ArrayList<XYChart.Series<String, Number>>) tempList.clone());
    }
    currentAdvertChartChanged = false;


    advertChart.getData().add(dataSeries);
    advertChart.autosize();
  }

  private String getBouncesCriteriaForChart(String pagesOrTime) {
    if (pagesOrTime.equals("Pages")) {
      return "ServerLog.PagesViewed < 2";
    } else if (pagesOrTime.equals("Time")) {
      return "(ExitDate - EntryDate <= 10)";
    } else {
      return "1==1"; //SQL Injection much?
    }
  }

  private String getDateRangeForChart() {
    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }
    String filter = DbConnector.getDateRangeForClickLog(startDate,endDate);
    return filter;
  }

  public void initializeAreaChart() {


    analysisAreaChart.getXAxis().setLabel("Time");

    String filter = getFilterAndDateSQLString();
    ArrayList<Number> dataPerDay = null;
    XYChart.Series<String, Number> dataPerDaySeries = new XYChart.Series<>();

    //To check if the chart has already been added before

    //String path1 = String.valueOf(getFilterAndDateSQLString().hashCode() + timeGranularityBox.valueProperty().getValue().toString());
    String path2 = String.valueOf(filterListView.getItems().get(filterListView.getSelectionModel().getSelectedIndex()).hashCode());
    String uniqueChartPath = path2 + "CostChart";

    if (filterGroups.isEmpty()) {
      System.out.println("--IS EMPTY--"); //No filters added. Usually at start
      return;
    } else {
      if (currentAreaChartChanged) {
        analysisAreaChart.getData().removeAll(analysisAreaChart.getData());
        System.out.println("--IS CHANGED--"); //Dropdown menu changed
      } else if (currentCharts3.contains(uniqueChartPath)){
        System.out.println("--CONTAINS KEY--"); //If this chart has already been added?
        System.out.println("CostChart key:" + currentCharts3);
        System.out.println("CostChart val:" + uniqueChartPath);
        return;
      }
    }


    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }

    String serverFilter = DbConnector.getDateRangeForServerLog(startDate, endDate);

    switch (currentAreaChartItem) {
      case "CPA":


        //analysisAreaChart.getData().removeAll(analysisAreaChart.getData());
        analysisAreaChart.setTitle("Cost Per Acquisition");
        analysisAreaChart.getYAxis().setLabel("CPA");
        dataPerDay = DbConnector.getDataForCPAChart(filter);
        areaChartMenu.setText("Cost Per Acquisition");
        //areaChartMenu.show();

        break;
      case "Total Cost":
        analysisAreaChart.setTitle("Total Cost");
        analysisAreaChart.getYAxis().setLabel("Total Cost");
        //analysisAreaChart.getData().removeAll(analysisAreaChart.getData());
        dataPerDay = DbConnector.getDataForCostPerDayChart("*", "ImpressionLog", filter);
        areaChartMenu.setText("Total Cost");
        //areaChartMenu.show();
        break;
      case "CPTI":
        analysisAreaChart.setTitle("Cost Per Thousand Impressions");
        analysisAreaChart.getYAxis().setLabel("Cost");
        //analysisAreaChart.getData().removeAll(analysisAreaChart.getData());
        dataPerDay = DbConnector.getDataForCPTIChart("*", "ImpressionLog", filter);
        areaChartMenu.setText("Cost Per Thousand Impressions");
        //areaChartMenu.show();
        break;
      case "CPC":
        //analysisAreaChart.getData().removeAll(analysisAreaChart.getData());
        analysisAreaChart.setTitle("CPC");
        analysisAreaChart.getYAxis().setLabel("Cost Per Click");
        dataPerDay = DbConnector.getDateForCPCChart(filter);
        areaChartMenu.setText("Cost Per Click");
        //areaChartMenu.show();

      default:
        break;
    }


    Integer day = 0;
    for (int i = 0; i < dataPerDay.size(); i++) {
      dataPerDaySeries.getData().add(new XYChart.Data(day.toString(), dataPerDay.get(i).intValue())); ///Test Data?
      day++;
    }

    //Add chart to currently displayed cache of charts

    /*
    private HashMap<String, HashMap<String,XYChart.Series>> currentCharts = new HashMap<>();
    protected HashMap<HashMap<String, Boolean>, String> filterIndexToString = new HashMap<>();
  */

    currentCharts2.put(getFilterAndDateSQLString().hashCode() + "CostChart", dataPerDaySeries);
    currentCharts3.add(uniqueChartPath);

    Object currSelectedListView = filterListView.getItems().get(filterListView.getSelectionModel().getSelectedIndex());
    if (filterViewToChartMap.containsKey(currSelectedListView)) {
      filterViewToChartMap.get(currSelectedListView).add(dataPerDaySeries);
    } else {
      ArrayList<XYChart.Series<String, Number>> tempList = new ArrayList<>();
      tempList.add(dataPerDaySeries);
      filterViewToChartMap.put(currSelectedListView, (ArrayList<XYChart.Series<String, Number>>) tempList.clone());
    }
    currentAreaChartChanged = false;

    analysisAreaChart.getData().add(dataPerDaySeries);
    analysisAreaChart.layout();

    /*
    HashMap<String, Integer> incomeMap = DbConnector.getDataForPieCharts("Income", "ImpressionLog", allFilters);
    ObservableList<PieChart.Data> incomeDataList = FXCollections.observableArrayList();
    for (String key: incomeMap.keySet()) {
      incomeDataList.add(new PieChart.Data(key, incomeMap.get(key)));
      System.out.println(key + "     "+ incomeMap.get(key));
    }
    incomePieChart.getData().clear();
    incomePieChart.setData(incomeDataList);
     */

  }

  private void initialiseUserGroupChart() {

    if (filterGroups.isEmpty()) {
      System.out.println("--IS EMPTY--"); //No filters added. Usually at start
      return;
    } else {
      if (currentCharts2.containsKey(getFilterAndDateSQLString() + "UserGroup")){
        System.out.println("--CONTAINS KEY--"); //If this chart has already been added?
        return;
      }
    }


    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }

    StringBuilder allFilters = new StringBuilder(DbConnector.getDateRangeForClickLog(startDate,endDate));
    String filter = "";
    try {
      Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
      filter = DbConnector.getWHEREOnFilter(selectedGroup.get("male"), selectedGroup.get("female"), selectedGroup.get("youngerThan25"), selectedGroup.get("25to34"), selectedGroup.get("35to44"), selectedGroup.get("45to54"), selectedGroup.get("olderThan54"), selectedGroup.get("lowIncome"), selectedGroup.get("midIncome"), selectedGroup.get("highIncome"), selectedGroup.get("blog"), selectedGroup.get("news"), selectedGroup.get("shopping"), selectedGroup.get("socialMedia"), selectedGroup.get("hobbies"), selectedGroup.get("travel"));
      if (!filter.equals("")) {
        if (!allFilters.equals("")) {
          allFilters.delete(0, 5); //Deletes last OR
          allFilters = new StringBuilder(" AND" + allFilters);
        }
        allFilters = new StringBuilder(filter + allFilters);
      }
    }
    catch (Exception e) {}
    userGroupChart.setTitle("Unique Users Clicked");
    userGroupChart.getYAxis().setLabel("Unique Users");
    //userGroupChart.getData().removeAll(userGroupChart.getData());
    ArrayList<Integer> dataPerDay = DbConnector.getDataForUserGroupChart(allFilters.toString());
    XYChart.Series<String, Number> dataPerDaySeries = new XYChart.Series<>();



    Integer day = 0;
    for (int i = 0;i < dataPerDay.size(); i++) {
      dataPerDaySeries.getData().add(new XYChart.Data(day.toString(), dataPerDay.get(i).intValue())); ///Test Data?
      day++;
    }

    currentCharts2.put(getFilterAndDateSQLString() + "UserGroup", dataPerDaySeries);


    Object currSelectedListView = filterListView.getItems().get(filterListView.getSelectionModel().getSelectedIndex());
    if (filterViewToChartMap.containsKey(currSelectedListView)) {
      filterViewToChartMap.get(currSelectedListView).add(dataPerDaySeries);
    } else {
      ArrayList<XYChart.Series<String, Number>> tempList = new ArrayList<>();
      tempList.add(dataPerDaySeries);
      filterViewToChartMap.put(currSelectedListView, (ArrayList<XYChart.Series<String, Number>>) tempList.clone());
    }

    userGroupChart.getData().add(dataPerDaySeries);
    userGroupChart.layout();

  }

  private String getFilterAndDateSQLString() {
    //getFilteredMetrics();
    String filter = "";
    try {
    Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
      filter = DbConnector.getWHEREOnFilter(selectedGroup.get("male"), selectedGroup.get("female"), selectedGroup.get("youngerThan25"), selectedGroup.get("25to34"), selectedGroup.get("35to44"), selectedGroup.get("45to54"), selectedGroup.get("olderThan54"), selectedGroup.get("lowIncome"), selectedGroup.get("midIncome"), selectedGroup.get("highIncome"), selectedGroup.get("blog"), selectedGroup.get("news"), selectedGroup.get("shopping"), selectedGroup.get("socialMedia"), selectedGroup.get("hobbies"), selectedGroup.get("travel"));
    }
    catch (Exception e) {}
    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }

    String fullFilterString = DbConnector.generateFilterString(filter, getDateRange(startDate,endDate));
    return fullFilterString;
  }

  public void initializePieChart() {
    genderPieChart.setAnimated(false);
    incomePieChart.setAnimated(false);
    agePieChart.setAnimated(false);
    contextPieChart.setAnimated(false);
    incomePieChart.animatedProperty().set(false);

    String allFilters = getFilterAndDateSQLString();

    //System.out.println("This is actually working");
    HashMap<String, Integer> genderMap = DbConnector.getDataForPieCharts("Gender", "ImpressionLog", allFilters);
    ObservableList<PieChart.Data> genderDataList = FXCollections.observableArrayList();
    genderMap.remove("Gender");
    for (String key: genderMap.keySet()) {
      genderDataList.add(new PieChart.Data(key, genderMap.get(key)));
    }
    genderPieChart.getData().clear();
    genderPieChart.setData(genderDataList);


    HashMap<String, Integer> incomeMap = DbConnector.getDataForPieCharts("Income", "ImpressionLog", allFilters);
    ObservableList<PieChart.Data> incomeDataList = FXCollections.observableArrayList();
    incomeMap.remove("Income");
    for (String key: incomeMap.keySet()) {
      incomeDataList.add(new PieChart.Data(key, incomeMap.get(key)));

    }
    incomePieChart.getData().clear();
    incomePieChart.setData(incomeDataList);

    HashMap<String, Integer> ageMap = DbConnector.getDataForPieCharts("Age", "ImpressionLog", allFilters);
    ageMap.remove("Age");
    ObservableList<PieChart.Data> ageDataList = FXCollections.observableArrayList();
    for (String key: ageMap.keySet()) {
      ageDataList.add(new PieChart.Data(key, ageMap.get(key)));
    }
    agePieChart.getData().clear();
    agePieChart.setData(ageDataList);

    HashMap<String, Integer> contextMap = DbConnector.getDataForPieCharts("Context", "ImpressionLog", allFilters);
    ObservableList<PieChart.Data> contextDataList = FXCollections.observableArrayList();
    contextMap.remove("Context");
    for (String key: contextMap.keySet()) {
      contextDataList.add(new PieChart.Data(key, contextMap.get(key)));
    }
    contextPieChart.getData().clear();
    contextPieChart.setData(contextDataList);

    genderPieChart.layout();
    agePieChart.layout();
    incomePieChart.layout();
    contextPieChart.layout();


    //refreshPieCharts();
  }

  @FXML
  private void refreshCharts() {

    System.out.println("【】】【】【】testing function ");

    SqliteUtil.changeSource("twoWeek.db");
  }
  private void refreshPieChart(PieChart pieChart, HashMap<String, Integer> dataMap, String removeKey) {
    ObservableList<PieChart.Data> dataList = FXCollections.observableArrayList();
    dataMap.remove(removeKey);
    for (String key : dataMap.keySet()) {
      dataList.add(new PieChart.Data(key, dataMap.get(key)));
    }
    pieChart.getData().clear();
    pieChart.setData(dataList);
    pieChart.layout();
  }



  public void intialiseServerDataAnalysisChart() {

    if (filterGroups.isEmpty()) {
      System.out.println("--IS EMPTY--"); //No filters added. Usually at start
      return;
    } else {
      if (currentCharts2.containsKey(getFilterAndDateSQLString() + "ServerData")){
        System.out.println("--CONTAINS KEY--"); //If this chart has already been added?
        return;
      }
    }

    //serverAnalysisChart.getData().removeAll(serverAnalysisChart.getData());
    serverAnalysisChart.getXAxis().setLabel("Pages Viewed");
    serverAnalysisChart.getYAxis().setLabel("Converted");
    serverAnalysisChart.setTitle("Server Analysis");

    String startDate = "";
    String endDate = "";
    try {
      startDate = startDateText.getValue().toString();
    } catch (Exception e3) {
    }
    try {
      endDate = endDateText.getValue().toString();
    } catch (Exception e3) {
    }

    StringBuilder allFilters = new StringBuilder(DbConnector.getDateRangeForServerLog(startDate,endDate));
    String filter = "";
    try {
      Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
      filter = DbConnector.getWHEREOnFilter(selectedGroup.get("male"), selectedGroup.get("female"), selectedGroup.get("youngerThan25"), selectedGroup.get("25to34"), selectedGroup.get("35to44"), selectedGroup.get("45to54"), selectedGroup.get("olderThan54"), selectedGroup.get("lowIncome"), selectedGroup.get("midIncome"), selectedGroup.get("highIncome"), selectedGroup.get("blog"), selectedGroup.get("news"), selectedGroup.get("shopping"), selectedGroup.get("socialMedia"), selectedGroup.get("hobbies"), selectedGroup.get("travel"));
      if (!filter.equals("")) {
        if (!allFilters.equals("")) {
          allFilters.delete(0, 5); //Deletes last OR
          allFilters = new StringBuilder(" AND" + allFilters);
        }
        allFilters = new StringBuilder(filter + allFilters);
      }
    }
    catch (Exception e) {}
    HashMap<Integer, Float> yesConvPerView = DbConnector.getDataForServerYesNoConversion(allFilters.toString(), "Yes");
    HashMap<Integer, Float> noConvPerView = DbConnector.getDataForServerYesNoConversion(allFilters.toString(), "No");

    XYChart.Series yesConvSeries = new XYChart.Series();
    XYChart.Series noConvSeries = new XYChart.Series();

    Set<Integer> fullPagesViewedSet = new HashSet<>();
    fullPagesViewedSet.addAll(noConvPerView.keySet());
    fullPagesViewedSet.addAll(yesConvPerView.keySet());

    Integer pages = 0;
    for (Integer pagesViewed:fullPagesViewedSet) {
      if (yesConvPerView.containsKey(pagesViewed)) {
        yesConvSeries.getData().add(new XYChart.Data<>(pages.toString(), yesConvPerView.get(pagesViewed)));
      } else {
        yesConvSeries.getData().add(new XYChart.Data<>(pages.toString(), 0));
      }
      pages++;
    }

    currentCharts2.put(getFilterAndDateSQLString() + "ServerData", yesConvSeries);


    Object currSelectedListView = filterListView.getItems().get(filterListView.getSelectionModel().getSelectedIndex());
    if (filterViewToChartMap.containsKey(currSelectedListView)) {
      filterViewToChartMap.get(currSelectedListView).add(yesConvSeries);
    } else {
      ArrayList<XYChart.Series<String, Number>> tempList = new ArrayList<>();
      tempList.add(yesConvSeries);
      filterViewToChartMap.put(currSelectedListView, (ArrayList<XYChart.Series<String, Number>>) tempList.clone());
    }

    serverAnalysisChart.getData().add(yesConvSeries);
    serverAnalysisChart.layout();

  }

  public void initialiseAdvertPerformanceAnalysisChart() {
    String allFilters = getFilterAndDateSQLString();

  }
  @FXML
  protected void onMenuBtn_CancelDeleteClick(){
    //customMenuItem_filter.setHideOnClick(true);

    //Remove the filter from charts
    removeFilteredChartSeries(filterListView.getSelectionModel().getSelectedIndex());

    filterGroups.remove(filterListView.getSelectionModel().getSelectedIndex());
    filterListView.getItems().remove(filterListView.getSelectionModel().getSelectedIndex());

    if ((filterListView.getItems().size()-1)==-1){
      //do nothing
    }else{
      filterListView.getSelectionModel().select(filterListView.getItems().size()-1);
      updateFilters();
    }

  }

  HashMap<Object, ArrayList<XYChart.Series<String, Number>>> filterViewToChartMap = new HashMap<>();


  protected String getCurrentPathToChart() {

    String path1 = String.valueOf(getFilterAndDateSQLString().hashCode() + timeGranularityBox.getSelectionModel().getSelectedIndex());
    return path1;
  }
  protected void removeFilteredChartSeries(Integer filterIndex) {

    System.out.println("Index: " + filterGroups.get(filterIndex));

    String mapToBeDeleted = getFilterAndDateSQLString();

    //XYChart.Series<String, Number> chartTobeDeleted = currentCharts2.get(mapToBeDeleted);

    Object currSelectedListView = filterListView.getItems().get(filterListView.getSelectionModel().getSelectedIndex());

    if (filterViewToChartMap.containsKey(currSelectedListView)) {
      //List of charts to remove
      ArrayList<XYChart.Series<String, Number>> chartsToRemove = filterViewToChartMap.get(currSelectedListView);

      for (XYChart.Series<String, Number> chart: chartsToRemove) {
        if (analysisAreaChart.getData().contains(chart)) {
          System.out.println("REMOVING COST CHARTS");
          currentCharts3.remove(getCurrentPathToChart() + "CostChart");
          analysisAreaChart.getData().removeAll(chart);
          analysisAreaChart.layout();
        } else if (serverAnalysisChart.getData().contains(chart)) {
          System.out.println("REMOVING SERVER CHARTS");
          currentCharts2.remove(getFilterAndDateSQLString() + "ServerData");
          serverAnalysisChart.getData().removeAll(chart);
          analysisAreaChart.layout();
        } else if (userGroupChart.getData().contains(chart)) {
          System.out.println("REMOVING USER CHARTS");
          currentCharts2.remove(getFilterAndDateSQLString() + "UserGroup");
          userGroupChart.getData().removeAll(chart);
          analysisAreaChart.layout();
        } else if (advertChart.getData().contains(chart)) {
          System.out.println("REMOVING ADVERT CHARTS");
          currentCharts2.remove(getFilterAndDateSQLString() + "Advert");
          advertChart.getData().removeAll(chart);
          analysisAreaChart.layout();
        } else {
          {
            System.out.println("W?");
            System.out.println(analysisAreaChart.getData());
            System.out.println("_______________TO BE DELETED_____________");
            System.out.println(chart.getData());
          }
        }
      }
    }
  }


  @FXML
  protected void onFilterChange(ActionEvent event) {
    System.out.printf(event.getSource().toString());
    CheckBox sourceCheckBox = (CheckBox) event.getSource();
    System.out.println("CheckBox changed: " + sourceCheckBox.getId());

    if (!(ckb_Male.isSelected()||ckb_Female.isSelected())){
      sourceCheckBox.setSelected(true);
    }
    if (!(ckb_youngerThan25.isSelected()||ckb_25to34.isSelected()||ckb_35to44.isSelected()||ckb_45to54.isSelected()||ckb_olderThan54.isSelected())){
      sourceCheckBox.setSelected(true);
    }
    if (!(ckb_LowIncome.isSelected()||ckb_MidIncome.isSelected()||ckb_HighIncome.isSelected())){
      sourceCheckBox.setSelected(true);
    }
    if (!(ckb_Blog.isSelected()||ckb_News.isSelected()||ckb_Shopping.isSelected()||ckb_SocialMedia.isSelected()||ckb_Hobbies.isSelected()||ckb_Travel.isSelected())){
      sourceCheckBox.setSelected(true);
    }

    Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
    selectedGroup.clear();
    selectedGroup.put("male", ckb_Male.isSelected());
    selectedGroup.put("female", ckb_Female.isSelected());
    selectedGroup.put("youngerThan25", ckb_youngerThan25.isSelected());
    selectedGroup.put("25to34", ckb_25to34.isSelected());
    selectedGroup.put("35to44", ckb_35to44.isSelected());
    selectedGroup.put("45to54", ckb_45to54.isSelected());
    selectedGroup.put("olderThan54", ckb_olderThan54.isSelected());
    selectedGroup.put("lowIncome", ckb_LowIncome.isSelected());
    selectedGroup.put("midIncome", ckb_MidIncome.isSelected());
    selectedGroup.put("highIncome", ckb_HighIncome.isSelected());
    selectedGroup.put("blog", ckb_Blog.isSelected());
    selectedGroup.put("news", ckb_News.isSelected());
    selectedGroup.put("shopping", ckb_Shopping.isSelected());
    selectedGroup.put("socialMedia", ckb_SocialMedia.isSelected());
    selectedGroup.put("hobbies", ckb_Hobbies.isSelected());
    selectedGroup.put("travel", ckb_Travel.isSelected());
    selectedGroup.put("pageViewed", rb_pageViewed.isSelected());
    selectedGroup.put("timeVisited", rb_timeVisited.isSelected());

    VBox x =new VBox();
    x.getChildren().add(new Label(getGenderDescription(ckb_Male.isSelected(),ckb_Female.isSelected())));
    Text lbl_age = new Text(getAgeDescription(ckb_youngerThan25.isSelected(),ckb_25to34.isSelected(),ckb_35to44.isSelected(),ckb_45to54.isSelected(),ckb_olderThan54.isSelected()));
    lbl_age.setWrappingWidth(240);
    x.getChildren().add(lbl_age);
    x.getChildren().add(new Label(getIncomeDescription(ckb_LowIncome.isSelected(),ckb_MidIncome.isSelected(),ckb_HighIncome.isSelected())));
    Text lbl_context = new Text(getContextDescription(ckb_Blog.isSelected(),ckb_News.isSelected(),ckb_Shopping.isSelected(),ckb_SocialMedia.isSelected(),ckb_Hobbies.isSelected(),ckb_Travel.isSelected()));
    lbl_context.setWrappingWidth(240);
    x.getChildren().add(lbl_context);

    int selectedIndex = filterListView.getSelectionModel().getSelectedIndex();
    if (filterViewToChartMap.containsKey(filterListView.getItems().get(selectedIndex))) {
      System.out.println("FILTER UPDATE -> CHART REMOVAL");
      removeFilteredChartSeries(selectedIndex);
    }


    filterListView.getItems().set(selectedIndex,x);
    filterListView.getSelectionModel().select(filterListView.getItems().size()-1);

    //When an edit happens, need to call
  }
  @FXML
  protected void onRadioButtonPageChange(){
    if (!rb_timeVisited.isSelected())
      rb_pageViewed.setSelected(true);
    rb_timeVisited.setSelected(false);

    Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
    selectedGroup.replace("pageViewed", rb_pageViewed.isSelected());
    selectedGroup.replace("timeVisited", rb_timeVisited.isSelected());
  }

  @FXML
  protected void onRadioButtonTimeChange(){
    if (!rb_pageViewed.isSelected())
      rb_timeVisited.setSelected(true);
    rb_pageViewed.setSelected(false);

    Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());
    selectedGroup.replace("pageViewed", rb_pageViewed.isSelected());
    selectedGroup.replace("timeVisited", rb_timeVisited.isSelected());
  }


  public String getContextDescription(boolean blog, boolean news, boolean shopping, boolean socialMedia, boolean hobbies, boolean travel){
    if (blog && news && shopping && socialMedia && hobbies && travel){
      return "All Context, ";
    }else{
      String contextStr="";
      if (blog){
        contextStr += "Blog, ";
      }
      if (news){
        contextStr += "News, ";
      }
      if (shopping){
        contextStr += "Shopping, ";
      }
      if (socialMedia){
        contextStr += "SocialMedia, ";
      }
      if (hobbies){
        contextStr += "Hobbies, ";
      }
      if (travel){
        contextStr += "Travel, ";
      }
      return contextStr;
    }
  }


  public String getIncomeDescription(boolean low, boolean mid, boolean high){
    if (low&&mid&high){
      return "All Income, ";
    }else{
      String incomeStr="";
      if (low){
        incomeStr += "Low income, ";
      }
      if (mid){
        incomeStr += "Medium income, ";
      }
      if (high){
        incomeStr += "High income, ";
      }
      return incomeStr;
    }
  }

  public String getAgeDescription(boolean youngerThan25,boolean age25to34,boolean age35to44,boolean age45to54,boolean olderThan54){
    if (youngerThan25 && age25to34 && age35to44 && age45to54 && olderThan54){
      return "All Age, ";
    }else if (youngerThan25 && age25to34 && age35to44 && age45to54){
      return "Younger than 55, ";
    }else if (age25to34 && age35to44 && age45to54 && olderThan54){
      return "Older than 24, ";
    }else if (youngerThan25 && age25to34 && age35to44  && (!olderThan54)){
      return "Younger than 45, ";
    }else if (age35to44 && age45to54 && olderThan54 && (!youngerThan25) ){
      return "Older than 34, ";
    }else if (age25to34 && age35to44 && age45to54 ){
      return "From 25 to 54, ";
    }else if (youngerThan25 && age25to34 && (!age35to44) && (!age45to54) && (!olderThan54)){
      return "Younger than 35, ";
    }else if (age25to34 && age35to44 && (!youngerThan25) && (!age45to54) && (!olderThan54)){
      return "From 25 to 44, ";
    }else if (age35to44 && age45to54 && (!youngerThan25) && (!age25to34) ){
      return "From 35 to 54, ";
    }else if (age45to54 && olderThan54 && (!youngerThan25) && (!age25to34) ){
      return "Older than 44, ";
    }else{
      String ageStr="";
      if (youngerThan25){
        ageStr += "Younger Than 25, ";
      }
      if (age25to34){
        ageStr += "25-34, ";
      }
      if (age35to44){
        ageStr += "35-44, ";
      }
      if (age45to54){
        ageStr += "45-54, ";
      }
      if (olderThan54){
        ageStr += "Older than 54";
      }
      return ageStr;
    }
  }

  public String getGenderDescription(boolean male,boolean female){
    if (male&&female){
      return "All Gender, ";
    }else if (male){
      return "Only Male, ";
    }else{
      return "Only Female, ";
    }
  }


  @FXML
  protected void onBtn_addFilterClick() {
    //initializeAeraChart();
    //initializePieChart();
    //getFilteredMetrics(); //IDK WHERE THIS SHOULD GO (It's to create the filtered stats so it's here for now) //not here bitches - I moved them

    instantiateFilter();


    Map<String, Boolean> group = new HashMap<>();
    group.put("male", true);
    group.put("female", true);
    group.put("youngerThan25", true);
    group.put("25to34", true);
    group.put("35to44", true);
    group.put("45to54", true);
    group.put("olderThan54", true);
    group.put("lowIncome", true);
    group.put("midIncome", true);
    group.put("highIncome", true);
    group.put("blog", true);
    group.put("news", true);
    group.put("shopping", true);
    group.put("socialMedia", true);
    group.put("hobbies", true);
    group.put("travel", true);
    group.put("pageViewed", false);
    group.put("timeVisited", true);
    filterGroups.add((HashMap<String, Boolean>) group);

    VBox x =new VBox();
    x.getChildren().add(new Label("All Gender, "));
    Text lbl_age = new Text("All age, ");
    lbl_age.setWrappingWidth(240);
    x.getChildren().add(lbl_age);
    x.getChildren().add(new Label("All Income, "));
    Text lbl_context = new Text("All Context, ");
    lbl_context.setWrappingWidth(240);
    x.getChildren().add(lbl_context);
    filterListView.getItems().add(x);
    filterListView.getSelectionModel().select(filterListView.getItems().size()-1);

    customMenuItem_filter.setHideOnClick(false);
  }

  @FXML
  protected  void onBtn_aboutClick(){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("About Us");
    alert.setHeaderText("Welcome to ECS AD! We are a development team consisting of the following members:\n" +
            "\n" +
            "Lyuyan Chen     (E-Mail: lc18g21@soton.ac.uk)\n" +
            "Theo Kenney     (E-Mail: tdk1g21@soton.ac.uk)\n" +
            "Andrew Neasom   (E-Mail: ajn1g21@soton.ac.uk)\n" +
            "Sabeeh Islam    (E-Mail: ssi1n21@soton.ac.uk)\n" +
            "Akash Aravindan (E-Mail: aapr1g21@soton.ac.uk)\n" +
            "Alberto Berni   (E-Mail: ab3u21@soton.ac.uk)\n" +
            "\n" +
            "We are students at the University of Southampton and are committed to providing you with\n" +
            "high-quality, reliable, and user-friendly software. We adhere to the principle of user-first\n" +
            "and continuously optimize our software to meet your needs and expectations. If you have any\n" +
            "questions or suggestions about our team or software, please feel free to contact us.\n" +
            "Thank you!");
    alert.showAndWait();
  }
  @FXML
  protected void onBtn_helpClick() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Open User Guide");
    alert.setHeaderText("Do you want to open the user manual?");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK){
      String url = "https://github.com/QQ89932/COMP2211-SEG-Ad-Dashboard/raw/main/UserGuide.pdf";
      try {
        Desktop.getDesktop().browse(new URI(url));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  @FXML
  protected void onBtn_editFilterClick() {
    if (filterListView.getItems().size()==0){
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Information");
      alert.setHeaderText("Please add one filter first");
      alert.showAndWait();
    }else{
      btn_addFilter.show(); // display filter menu
      Map<String, Boolean> selectedGroup = filterGroups.get(filterListView.getSelectionModel().getSelectedIndex());

      ckb_Male.setSelected(selectedGroup.get("male"));
      ckb_Female.setSelected(selectedGroup.get("female"));
      ckb_youngerThan25.setSelected(selectedGroup.get("youngerThan25"));
      ckb_25to34.setSelected(selectedGroup.get("25to34"));
      ckb_35to44.setSelected(selectedGroup.get("35to44"));
      ckb_45to54.setSelected(selectedGroup.get("45to54"));
      ckb_olderThan54.setSelected(selectedGroup.get("olderThan54"));
      ckb_LowIncome.setSelected(selectedGroup.get("lowIncome"));
      ckb_MidIncome.setSelected(selectedGroup.get("midIncome"));
      ckb_HighIncome.setSelected(selectedGroup.get("highIncome"));
      ckb_Blog.setSelected(selectedGroup.get("blog"));
      ckb_News.setSelected(selectedGroup.get("news"));
      ckb_Shopping.setSelected(selectedGroup.get("shopping"));
      ckb_SocialMedia.setSelected(selectedGroup.get("socialMedia"));
      ckb_Hobbies.setSelected(selectedGroup.get("hobbies"));
      ckb_Travel.setSelected(selectedGroup.get("travel"));
      rb_pageViewed.setSelected(selectedGroup.get("pageViewed"));
      rb_timeVisited.setSelected(selectedGroup.get("timeVisited"));
      customMenuItem_filter.setHideOnClick(false);
    }
  }

  @FXML
  protected void onBtn_checkUpdateClick() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText("Thanks for using EcsAD. You are using the latest version:\n" +
            "v1.0504");
    alert.showAndWait();
  }

  @FXML
  protected void onBtn_revertToDefaultClick() {

    LightModeRadioBtn.setSelected(true);
    DarkModeRadioBtn.setSelected(false);
    SepiaModeRadioBtn.setSelected(false);
    onRadioButtonLightChange();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText("Default settings have been restored");
    alert.showAndWait();
  }

  @FXML
  protected void onBtn_sendFeedbackClick() {

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText("An email window will pop up soon. \n" +
            "Please describe your problem and we will reply as soon as possible");
    alert.showAndWait();

    String recipient = "lc18g21@soton.ac.uk"; // 邮件收件人地址

    String url = "mailto:" + recipient;

    try {
      Desktop.getDesktop().browse(new URI(url));
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
    }
  }


}

