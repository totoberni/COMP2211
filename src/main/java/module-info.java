module uk.ac.soton.comp2211.g16.ad {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  //requires validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires eu.hansolo.tilesfx;
  requires java.sql;
//  requires org.sqlite.jdbc4;
  requires org.apache.commons.io;
  requires org.xerial.sqlitejdbc;
  requires druid;
  requires java.management;
  requires java.desktop;
  requires itextpdf;
  requires jfreechart;
  requires activation;
  requires java.mail;
  //requires pdfbox.app;
  requires org.apache.pdfbox;
  opens uk.ac.soton.comp2211.g16.ad to
      javafx.fxml;

  exports uk.ac.soton.comp2211.g16.ad;
  exports uk.ac.soton.comp2211.g16.ad.data;

}