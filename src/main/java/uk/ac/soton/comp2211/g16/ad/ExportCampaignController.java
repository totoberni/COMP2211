package uk.ac.soton.comp2211.g16.ad;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import javafx.application.Platform;
import javafx.print.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.awt.image.BufferedImage;


import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;


public class ExportCampaignController  {

    @FXML
    private TabPane TabPaneMenu;
    @FXML
    private Line blueBarRecent;
    @FXML
    private Line blueBarOpenFolder;
    @FXML
    private Line blueBarOpenFiles;
    @FXML
    private javafx.scene.control.TextField txt_email;

    @FXML
    private TextField txt_emailServer;

    @FXML
    private TextField txt_emailPort;

    @FXML
    private TextField txt_emailAccount;

    @FXML
    private PasswordField txt_emailPwd;
    @FXML
    private Label emailStatus;

    @FXML
    private Label menuTextExport;
    @FXML
    private Label menuTextEmail;
    @FXML
    private Label menuTextPrint;
    @FXML
    private SplitPane godPane;
    private ArrayList<String> statistics;
    private ArrayList<PieChart> pieCharts;
    private LineChart<String, Number> overviewLineChart;
    private AreaChart<String, Number> analysisAreaChart;
    private BarChart<String, Number> serverAnalysisChart;
    private LineChart<String, Number> analysisLineChart;



    public ExportCampaignController(ArrayList<String> statistics, ArrayList<PieChart> pieCharts, LineChart<String, Number> overviewLineChart, AreaChart<String, Number> analysisAreaChart, BarChart<String, Number> serverAnalysisChart, LineChart<String, Number> analysisLineChart) {
        this.statistics = statistics;
        this.pieCharts = pieCharts;
        this.overviewLineChart = overviewLineChart;
        this.analysisAreaChart = analysisAreaChart;
        this.serverAnalysisChart = serverAnalysisChart;
        this.analysisLineChart = analysisLineChart;
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
                    setBackgroundForAllComponents(color, (TabPane) node);
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
                    setBackgroundForAllComponents(color, (TabPane) node);
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
                    setBackgroundForAllComponents(color, (TabPane) node);
                }
            }
        } catch (Exception e) {
        }
    }



    @FXML
    protected void emailButton() {
        String smtpHost = txt_emailServer.getText();
        int smtpPort = Integer.parseInt(txt_emailPort.getText()); // or 465 for SSL/TLS
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        //props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", smtpPort);

        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        // Authenticate with SMTP server (if required)
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(txt_emailAccount.getText(), txt_emailPwd.getText());
            }
        };

        // Create a new email session with SMTP server
        Session session = Session.getInstance(props, auth);
        session.setDebug(true);
        String filename = "tempCampaign.pdf";
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(txt_emailAccount.getText()));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(txt_email.getText()));

            message.setSubject("Campaign Breakdown Information");

            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("The breakdown is attached as a PDF.");
            multipart.addBodyPart(messageBodyPart);


            exportToPdf(filename,null);
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            Transport.send(message);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Email sent successfully!");
            alert.showAndWait();
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Failed to send email. Error message: " + e.getMessage());
            alert.showAndWait();
            System.out.println("Failed to send email. Error message: " + e.getMessage());
        }
        File location = new File(filename);
        location.delete();
    }


    @FXML
    private void exportToTxt() {
        try {
            String fileName = "emailAccount.txt";
            FileWriter writer = new FileWriter(fileName);
            writer.write(txt_emailServer.getText() + System.lineSeparator());
            writer.write(txt_emailPort.getText() + System.lineSeparator());
            writer.write(txt_emailAccount.getText() + System.lineSeparator());
            writer.write(txt_emailPwd.getText() + System.lineSeparator());
            writer.close();

            System.out.println("Email credential saved to: emailAccount.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importEmailCredential() {
        emailStatus.setVisible(false);
        try {
            String fileName = "emailAccount.txt";
            File file = new File(fileName);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                txt_emailServer.setText(reader.readLine());
                txt_emailPort.setText(reader.readLine());
                txt_emailAccount.setText(reader.readLine());
                txt_emailPwd.setText(reader.readLine());
                reader.close();
            } else {
                System.out.println("no email credential file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void testEmailLogin() {
        String emailServer = txt_emailServer.getText().trim();
        String emailPort = txt_emailPort.getText().trim();
        String emailAccount = txt_emailAccount.getText().trim();
        String emailPwd = txt_emailPwd.getText();
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            //props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", "465");

            props.put("mail.smtp.host", emailServer);
            props.put("mail.smtp.port", emailPort);


            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailAccount, emailPwd);
                }
            });

            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Login Success!");
            alert.showAndWait();
            emailStatus.setVisible(true);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Login Failed:\n" + e.getMessage());
            alert.showAndWait();
            emailStatus.setVisible(false);
        }
    }

    @FXML
    private void printPdf() {
        String filename = "tempCampaign.pdf";
        exportToPdf(filename,null);
        File file = new File(filename);

        if (!file.exists()) {
            System.out.println("Can't find pdf file");
        } else {
            try{
                PDDocument document = PDDocument.load(file);
                PrinterJob job = PrinterJob.getPrinterJob();

                if (job.printDialog()) {
                    job.setPageable(new PDFPageable(document));
                    job.print();
                    System.out.println("Print success");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Info");
                    alert.setHeaderText(null);
                    alert.setContentText("Print success");
                    alert.showAndWait();
                }
                document.close();
            }catch(IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Info");
                alert.setHeaderText(null);
                alert.setContentText("Can't print Pdf, reason: " + e.getMessage());
                alert.showAndWait();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        File location = new File(filename);
        location.delete();
        clearTempPng();
    }


    @FXML
    protected void onMenuExportClick() {
        changeMenuIndex(0);
    }
    @FXML
    protected void onMenuEmailClick() {
        changeMenuIndex(1);
    }
    @FXML
    protected void onMenuImagesClick() {
        changeMenuIndex(2);
    }
    private void changeMenuIndex(int newIndex){
        int oldIndex = TabPaneMenu.getSelectionModel().getSelectedIndex();
        System.out.println("Menu index selected: " + newIndex);

        if(oldIndex==0){
            blueBarRecent.setVisible(false);
            menuTextExport.setTextFill(Color.valueOf("#565759"));
        }else if(oldIndex==1){
            blueBarOpenFolder.setVisible(false);
            menuTextEmail.setTextFill(Color.valueOf("#565759"));
        }else if(oldIndex==2){
            blueBarOpenFiles.setVisible(false);
            menuTextPrint.setTextFill(Color.valueOf("#565759"));
        }else{
            System.err.println("Menu index error");
        }

        if(newIndex==0){
            blueBarRecent.setVisible(true);
            menuTextExport.setTextFill(Color.valueOf("#185abc"));
        }else if(newIndex==1){
            blueBarOpenFolder.setVisible(true);
            menuTextEmail.setTextFill(Color.valueOf("#185abc"));
            importEmailCredential();
        }else if(newIndex==2){
            blueBarOpenFiles.setVisible(true);
            menuTextPrint.setTextFill(Color.valueOf("#185abc"));
        }else{
            System.err.println("Menu index error");
        }

        TabPaneMenu.getSelectionModel().select(newIndex);
    }

    @FXML
    protected void exportButtonClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Please choose a folder to export PDF file..");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            exportToPdf("campaign.pdf", selectedDirectory);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("PDF exported successfully!");
            alert.showAndWait();
        }
    }



    @FXML
    protected void exportGraphsButtonClicked() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Please choose a folder to export graphs..");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            exportGraphs("",selectedDirectory);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Graphs exported successfully!");
            alert.showAndWait();
        }


    }

    protected void exportGraphs(String outputNameAddition,File targetFolder) {
        int width = 500;
        int height = 500;
        String path="";
        if (targetFolder == null) {
        } else {
            path=targetFolder.toString()+"/";
        }
        //4x PIE CHARTs
        try {
            for (PieChart pieChart : pieCharts
            ) {
                DefaultPieDataset dataset = new DefaultPieDataset();

                for (var d : pieChart.getData()) {
                    dataset.setValue(d.getName(), d.getPieValue());
                }
                JFreeChart chart = ChartFactory.createPieChart(pieChart.getTitle(), dataset, false, false, false);
                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setSimpleLabels(true);
                ChartUtilities.saveChartAsPNG(new File(path+pieChart.getTitle() + outputNameAddition + ".png"), chart, width, height);




                //then do line chart when pie chart can be seen xd
            }

            //LINE CHART
            XYSeriesCollection lineChartDataset = new XYSeriesCollection();
            for (XYChart.Series<String, Number> series : analysisLineChart.getData()) {
                XYSeries jfreeSeries = new XYSeries(series.getName());
                for (XYChart.Data<String, Number> data : series.getData()) {
                    //System.out.println(data.XValueProperty());
                    jfreeSeries.add(Double.parseDouble(data.getXValue()), data.getYValue());
                }
                lineChartDataset.addSeries(jfreeSeries);
            }

            JFreeChart lineChart = ChartFactory.createXYLineChart(analysisLineChart.getTitle(), analysisLineChart.getXAxis().getLabel(),  analysisLineChart.getYAxis().getLabel(), lineChartDataset, PlotOrientation.VERTICAL, true, true, false);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            Rectangle2D chartArea = new Rectangle2D.Double(0, 0, width, height);
            lineChart.draw(graphics, chartArea);
            ChartUtilities.saveChartAsPNG(new File(path+ analysisLineChart.getTitle() + outputNameAddition +  ".png"), lineChart, width, height);



            //AREA CHART
            DefaultCategoryDataset areaChartDataset = new DefaultCategoryDataset();
            for (XYChart.Series<String, Number> series : analysisAreaChart.getData()) {
                String seriesName = series.getName();
                seriesName = "no series name now - Lyuyan";
                for (XYChart.Data<String, Number> data : series.getData()) {
                    String category = data.getXValue();
                    Double value = data.getYValue().doubleValue();
                    //System.out.println("value: "+value +" seriesName: "+ seriesName+" category:"+ category);
                    areaChartDataset.addValue(value, seriesName, category);
                }
            }
            JFreeChart areaChart = ChartFactory.createStackedAreaChart(
                    analysisAreaChart.getTitle(), // chart title
                    analysisAreaChart.getXAxis().getLabel(), // domain axis label
                    analysisAreaChart.getYAxis().getLabel(), // range axis label
                    areaChartDataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    false, // include legend
                    false, // tooltips
                    false // urls
            );
            CategoryPlot areaPlot = areaChart.getCategoryPlot();
            areaPlot.setDataset(1, areaChartDataset);
            areaPlot.setRenderer(1, new AreaRenderer());
            areaPlot.getRangeAxis().setRange(areaPlot.getRangeAxis().getLowerBound(), areaPlot.getRangeAxis().getUpperBound());
            areaPlot.setForegroundAlpha(0.5f);

            ChartUtilities.saveChartAsPNG(new File(path+analysisAreaChart.getTitle() + outputNameAddition +  ".png"), areaChart, width, height);


            //BAR CHART
            XYSeriesCollection barChartDataset = new XYSeriesCollection();

            for (XYChart.Series<String, Number> series : serverAnalysisChart.getData()) {
                // Create a new XYSeries for the current series
                XYSeries xySeries = new XYSeries("Page Viewed");

                // Loop through each data point in the current series
                for (XYChart.Data<String, Number> data : series.getData()) {
                    // Add the data point to the XYSeries
                    xySeries.add(Double.parseDouble(data.getXValue()), data.getYValue());
                }

                // Add the XYSeries to the dataset
                barChartDataset.addSeries(xySeries);
            }

            JFreeChart barChart = ChartFactory.createXYLineChart(
                    serverAnalysisChart.getTitle(), // chart title
                    serverAnalysisChart.getXAxis().getLabel(), // x-axis label
                    serverAnalysisChart.getYAxis().getLabel(), // y-axis label
                    barChartDataset, // dataset
                    PlotOrientation.VERTICAL, // orientation
                    true, // legend
                    true, // tooltips
                    false // urls
            );
            ChartUtilities.saveChartAsPNG(new File(path+serverAnalysisChart.getTitle() + outputNameAddition +  ".png"), barChart, width, height);

            System.out.println("Graphs exported successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    protected void exportToPdf(String pdfName,File targetFolder) {
        System.out.println("PDF export start");
        try {
            Document document = new Document();

            File location;
            if (targetFolder == null) {
                location = new File(pdfName);
            } else {
                location = new File(targetFolder, pdfName);
            }


            location.delete();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(location));

            document.setPageSize(new Rectangle(PageSize.A4.getWidth(), PageSize.A4.getHeight()));
            //document.setPageSize(new Rectangle(1000, 1000));
            document.setMargins(10,10,10,10);
            //document.setPageCount(10);
            document.open();
            document.newPage();


            Paragraph title = new Paragraph("Overview");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);


            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            //Headers
            ArrayList<String> statisticsHeader = new ArrayList<>();
            statisticsHeader.add("Total Cost");
            statisticsHeader.add("Impressions");
            statisticsHeader.add("Clicks");
            statisticsHeader.add("Uniques");
            statisticsHeader.add("Bounces");
            statisticsHeader.add("Acquisition");
            statisticsHeader.add("CPA");
            statisticsHeader.add("CPC");
            statisticsHeader.add("CPM");
            statisticsHeader.add("CTR");

            for (String stat : statisticsHeader) {
                PdfPCell cell = new PdfPCell(new Phrase(stat));
                cell.setBackgroundColor(BaseColor.GRAY);
                table.addCell(cell);
            }
            for (String stat: statistics
            ) {
                table.addCell(stat);
            }

            document.add(table);

            Paragraph title2 = new Paragraph("User Group Analysis");
            title2.setAlignment(Element.ALIGN_CENTER);
            document.add(title2);

            exportGraphs("_temp",null);
            int imagesPerRow = 2;
            int imageSpacing = 20;
            for (int i = 0; i < pieCharts.size(); i += imagesPerRow * 2) {
                float[] columnWidths = new float[imagesPerRow];
                Arrays.fill(columnWidths, document.getPageSize().getWidth() / imagesPerRow);
                PdfPTable pdfTable = new PdfPTable(columnWidths);
                pdfTable.setWidthPercentage(100);

                for (int j = i; j < i + imagesPerRow * 2 && j < pieCharts.size(); j++) {
                    PieChart pieChart = pieCharts.get(j);

                    //load from temp file
                    BufferedImage bufferedImage = ImageIO.read(new File(pieChart.getTitle() + "_temp.png"));

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                    byte[] imageBytes = byteArrayOutputStream.toByteArray();

                    Image image = Image.getInstance(imageBytes);

                    image.scaleToFit(100, 100);


                    PdfPCell cell = new PdfPCell(image, true);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfTable.addCell(cell);
                }

                // Add the table to the document and start a new page
                pdfTable.completeRow();
                pdfTable.setSpacingAfter(imageSpacing);
                document.add(pdfTable);
                document.newPage();
            }
            addImageToPdf(document, analysisLineChart.getTitle()+ "_temp.png");

            Paragraph title3 = new Paragraph("Cost Analysis");
            title3.setAlignment(Element.ALIGN_CENTER);
            document.add(title3);
            addImageToPdf(document, analysisAreaChart.getTitle()+ "_temp.png");


            Paragraph title4 = new Paragraph("Server Analysis");
            title4.setAlignment(Element.ALIGN_CENTER);
            document.add(title4
            );

            addImageToPdf(document, serverAnalysisChart.getTitle()+ "_temp.png");
            document.close();
            System.out.println("PDF exported successfully!");
            clearTempPng();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    private void addImageToPdf(Document document, String imagePath) throws IOException, DocumentException {


        BufferedImage bufferedImage = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        Image image = Image.getInstance(imageBytes);
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleToFit(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin(), 0);
        //document.add(image);


        PdfPTable pdfTable = new PdfPTable(1);
        pdfTable.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(image, true);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfTable.addCell(cell);

        document.add(pdfTable);
        document.newPage();
    }

        public void clearTempPng() {
            String currentDir = System.getProperty("user.dir");
            File dir = new File(currentDir);
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith("_temp.png")) {
                    file.delete();
                    //System.out.println(file.getName()+" deleted");
                }
            }
        }

}
