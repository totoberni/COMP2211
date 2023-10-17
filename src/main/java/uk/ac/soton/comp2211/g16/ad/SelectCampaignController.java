package uk.ac.soton.comp2211.g16.ad;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import uk.ac.soton.comp2211.g16.ad.data.CsvFileHandler;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.*;


public class SelectCampaignController implements Runnable {

  Stage stage;
  File initialDirectory;
  CsvFileHandler handler = new CsvFileHandler();

  @FXML
  private TabPane TabPaneMenu;
  @FXML
  private Line blueBarRecent;
  @FXML
  private Line blueBarOpenFolder;
  @FXML
  private Line blueBarOpenFiles;

  @FXML
  private Label menuTextRecent;
  @FXML
  private Label menuTextOpenFolder;
  @FXML
  private Label menuTextOpenFiles;
  @FXML
  private Label lbl_clickFilePass;
  @FXML
  private Label lbl_impressionFilePass;
  @FXML
  private Label lbl_serverFilePass;

  @FXML
  private Label lbl_clickFolderFilePass;
  @FXML
  private Label lbl_impressionFolderFilePass;
  @FXML
  private Label lbl_serverFolderFilePass;
  @FXML
  private Label lbl_loadFolderTips;



  @FXML
  private TextField txt_folder;
  @FXML
  private TextField txt_clickLogLocation;
  @FXML
  private TextField txt_impressionLogLocation;
  @FXML
  private TextField txt_serverLogLocation;

  @FXML
  private TextField txt_campaignName=new TextField();
  @FXML
  private TextField txt_campaignName1=new TextField();


  @FXML
  private ProgressBar loadFolderProgressBar;
  @FXML
  private ProgressBar loadHistoryProgressBar;
  @FXML
  private ProgressBar loadFilesProgressBar;
  @FXML
  private ListView ListView_recent;
  String dbName="";

  String timestamp;


  public SelectCampaignController(){

    LocalDateTime currentTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
    timestamp = currentTime.format(formatter);

    Platform.runLater(this::run);
    SqliteUtil.changeSource(timestamp+".db");
  }


  @Override
  public void run() {
    System.out.println("load recent history");
    //load recent history
    File directory = new File("./");
    File[] files = directory.listFiles((dir, name) -> name.endsWith(".db"));
    if (files != null) {
      for (File file : files) {
        if(file.length() > 0){
          String fileName = file.getName();
          System.out.println("Adding "+ fileName);
          Platform.runLater(() -> ListView_recent.getItems().add(fileName));
        }
      }
    }else{
      System.out.println("no db file found");
    }


  }


  @FXML
  private void recentBtnClick() {
    if (ListView_recent.getSelectionModel().getSelectedIndex()==-1){
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Information");
      alert.setHeaderText("Please select a campaign file");
      alert.showAndWait();
      System.out.println("NOT selected");
    }else{

      System.out.println("Change data source to: "+ (String) ListView_recent.getItems().get(ListView_recent.getSelectionModel().getSelectedIndex()));
      SqliteUtil.changeSource((String) ListView_recent.getItems().get(ListView_recent.getSelectionModel().getSelectedIndex()));

      //notify loading finish
      new Thread(() -> {
        try {
          System.out.println("Loading finish");
          loadFolderProgressBar.getScene().getWindow().fireEvent(new LoadingFinishedEvent());
          Platform.runLater(() -> {
            Stage stage = (Stage) loadFolderProgressBar.getScene().getWindow();
            stage.close();
          });


        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();
    }


  }



  @FXML
  private void folderBtnClick() {
    if (lbl_loadFolderTips.textFillProperty().get().equals(Paint.valueOf("#549e3b"))){
      if (txt_campaignName.getText().isEmpty()){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Information");
        alert.setHeaderText("Please add a name for campaign");
        alert.showAndWait();
      }else {

        //SqliteUtil.changeSource(txt_campaignName.getText() + ".db");

        
        this.dbName=txt_campaignName.getText()+".db";
        System.out.println("db name:"+dbName);
        loadData();


      }

    }else{
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Information");
      alert.setHeaderText("Please select a valid folder");
      alert.showAndWait();
    }



  }




  @FXML
  private void fileBtnClick() {
    if (lbl_clickFilePass.getText().equals("✓ Check pass")&&lbl_serverFilePass.getText().equals("✓ Check pass")&&lbl_impressionFilePass.getText().equals("✓ Check pass")&&
            lbl_clickFilePass.isVisible()&&lbl_serverFilePass.isVisible()&&lbl_impressionFilePass.isVisible()){
      if (txt_campaignName1.getText().isEmpty()){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Information");
        alert.setHeaderText("Please add a name for campaign");
        alert.showAndWait();
      }else {
        //SqliteUtil.changeSource(txt_campaignName.getText()+".db");
        System.out.println("All check pass, importing files now");
        SqliteUtil.changeSource("dashboard.db");
        loadData();
      }
    }else{
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Information");
      alert.setHeaderText("Please select all the required files correctly and then click on load");
      alert.showAndWait();
    }
  }

  private void loadData(){
    ScheduledExecutorService sec = Executors.newScheduledThreadPool(1);
    Runnable progressTask = () -> {
      double progress = handler.getProgress();
      loadFolderProgressBar.setVisible(true);
      loadFolderProgressBar.setProgress(progress);
      loadFilesProgressBar.setVisible(true);
      loadFilesProgressBar.setProgress(progress);
    };
    sec.scheduleAtFixedRate(progressTask, 0, 50, TimeUnit.MILLISECONDS);
    //this.handler = new CsvFileHandler(txt_folder.textProperty().get());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<?> f = executorService.submit(() -> {
      try {
        handler.processCsvFiles();
      } catch (SQLException e) {
        e.printStackTrace();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    executorService.shutdown();

    new Thread(() -> {
      try {
        f.get();
        System.out.println("Loading finish");

        loadFolderProgressBar.getScene().getWindow().fireEvent(new LoadingFinishedEvent());

        sec.shutdown();

        Platform.runLater(() -> {
          Stage stage = (Stage) loadFolderProgressBar.getScene().getWindow();
          stage.close();
        });


      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();


  }

  @FXML
  protected void onBtn_browseFolderClick() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Please select a folder");

    File selectedDirectory = directoryChooser.showDialog(stage);
    if (selectedDirectory != null) {
      txt_folder.textProperty().set(selectedDirectory.getAbsolutePath());
      System.out.println("Path selected: "+ selectedDirectory.getAbsolutePath());
      this.handler = new CsvFileHandler(selectedDirectory.getAbsolutePath());

      ArrayList<Boolean> result = handler.folderChecker();
      if (result.get(0)){
        lbl_clickFolderFilePass.setText("1. click_log.csv                                ✓Found");
        lbl_clickFolderFilePass.setTextFill(Paint.valueOf("#549e3b"));
      }else{
        lbl_clickFolderFilePass.setText("1. click_log.csv                                ✖NOT FOUND");
        lbl_clickFolderFilePass.setTextFill(Color.RED);;
      }
      if (result.get(1)){
        lbl_impressionFolderFilePass.setText("2. impression_log.csv                      ✓Found");
        lbl_impressionFolderFilePass.setTextFill(Paint.valueOf("#549e3b"));
      }else{
        lbl_impressionFolderFilePass.setText("2. impression_log.csv                      ✖NOT FOUND");
        lbl_impressionFolderFilePass.setTextFill(Color.RED);;
      }
      if (result.get(2)){
        lbl_serverFolderFilePass.setText("3. server_log.csv                             ✓Found");
        lbl_serverFolderFilePass.setTextFill(Paint.valueOf("#549e3b"));
      }else{
        lbl_serverFolderFilePass.setText("3. server_log.csv                             ✖NOT FOUND");
        lbl_serverFolderFilePass.setTextFill(Color.RED);;
      }
      if (result.get(0)&&result.get(1)&&result.get(2)){
        lbl_loadFolderTips.setText("All required files have been found, format check passed.");
        lbl_loadFolderTips.setTextFill(Paint.valueOf("#549e3b"));
      }else{
        lbl_loadFolderTips.setText("Not enough files found, check the directory.                        You can also use \"Open Files\" to import each file individually.");
        lbl_loadFolderTips.setTextFill(Color.RED);;
      }

      lbl_clickFolderFilePass.setVisible(true);
      lbl_impressionFolderFilePass.setVisible(true);
      lbl_serverFolderFilePass.setVisible(true);
      lbl_loadFolderTips.setVisible(true);
    }



  }

  @FXML
  protected void onBtn_browseClickLogClick() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Please select click_log.csv");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
    fileChooser.setInitialDirectory(initialDirectory);
    File selectedDirectory = fileChooser.showOpenDialog(stage);
    if (selectedDirectory != null) {
      txt_clickLogLocation.textProperty().set(selectedDirectory.getAbsolutePath());

      initialDirectory = selectedDirectory.getParentFile();

      System.out.println("File selected: "+ selectedDirectory.getAbsolutePath());
      if(handler.csvHeaderChecker(selectedDirectory.getAbsolutePath(),"Date,ID,Click Cost")){
        lbl_clickFilePass.setText("✓ Check pass");
        lbl_clickFilePass.setTextFill(Paint.valueOf("#549e3b"));
      }else{
        lbl_clickFilePass.setText("✖ Invalid file");
        lbl_clickFilePass.setTextFill(Color.RED);
      }
      lbl_clickFilePass.setVisible(true);
    }
  }


  @FXML
  protected void onBtn_browseImpressionLogClick() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Please select impression_log.csv");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
    fileChooser.setInitialDirectory(initialDirectory);
    File selectedDirectory = fileChooser.showOpenDialog(stage);
    if (selectedDirectory != null) {
      txt_impressionLogLocation.textProperty().set(selectedDirectory.getAbsolutePath());

      initialDirectory = selectedDirectory.getParentFile();

      System.out.println("File selected: "+ selectedDirectory.getAbsolutePath());

      if(handler.csvHeaderChecker(selectedDirectory.getAbsolutePath(),"Date,ID,Gender,Age,Income,Context,Impression Cost")){
        lbl_impressionFilePass.setText("✓ Check pass");
        lbl_impressionFilePass.setTextFill(Paint.valueOf("#549e3b"));
      }else{
        lbl_impressionFilePass.setText("✖ Invalid file");
        lbl_impressionFilePass.setTextFill(Color.RED);
      }
      lbl_impressionFilePass.setVisible(true);
    }
  }


  @FXML
  protected void onBtn_browseServerLogClick() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Please select server_log.csv");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
    fileChooser.setInitialDirectory(initialDirectory);
    File selectedDirectory = fileChooser.showOpenDialog(stage);
    if (selectedDirectory != null) {
      txt_serverLogLocation.textProperty().set(selectedDirectory.getAbsolutePath());

      initialDirectory = selectedDirectory.getParentFile();

      System.out.println("File selected: "+ selectedDirectory.getAbsolutePath());
      if(handler.csvHeaderChecker(selectedDirectory.getAbsolutePath(),"Entry Date,ID,Exit Date,Pages Viewed,Conversion")){
        lbl_serverFilePass.setText("✓ Check pass");
        lbl_serverFilePass.setTextFill(Paint.valueOf("#549e3b"));
      }else{
        lbl_serverFilePass.setText("✖ Invalid file");
        lbl_serverFilePass.setTextFill(Color.RED);
      }
      lbl_serverFilePass.setVisible(true);
    }
  }




  @FXML
  protected void onMenuRecentClick() {
    changeMenuIndex(0);
  }
  @FXML
  protected void onMenuOpenFolderClick() {
    changeMenuIndex(1);
  }
  @FXML
  protected void onMenuOpenFilesClick() {
    changeMenuIndex(2);
  }
  private void changeMenuIndex(int newIndex){
    int oldIndex = TabPaneMenu.getSelectionModel().getSelectedIndex();
    System.out.println("Menu index selected: " + newIndex);

    if(oldIndex==0){
      blueBarRecent.setVisible(false);
      menuTextRecent.setTextFill(Color.valueOf("#565759"));
    }else if(oldIndex==1){
      blueBarOpenFolder.setVisible(false);
      menuTextOpenFolder.setTextFill(Color.valueOf("#565759"));
    }else if(oldIndex==2){
      blueBarOpenFiles.setVisible(false);
      menuTextOpenFiles.setTextFill(Color.valueOf("#565759"));
    }else{
      System.err.println("Menu index error");
    }

    if(newIndex==0){
      blueBarRecent.setVisible(true);
      menuTextRecent.setTextFill(Color.valueOf("#185abc"));
    }else if(newIndex==1){
      blueBarOpenFolder.setVisible(true);
      menuTextOpenFolder.setTextFill(Color.valueOf("#185abc"));
      txt_campaignName.setText(timestamp);
      txt_campaignName.setDisable(true);
    }else if(newIndex==2){
      blueBarOpenFiles.setVisible(true);
      menuTextOpenFiles.setTextFill(Color.valueOf("#185abc"));
      txt_campaignName1.setText(timestamp);
      txt_campaignName1.setDisable(true);
    }else{
      System.err.println("Menu index error");
    }

    TabPaneMenu.getSelectionModel().select(newIndex);
  }

  public void setStage(Stage stage){
    this.stage=stage;
  }
}