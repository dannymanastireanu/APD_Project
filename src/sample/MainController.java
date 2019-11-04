package sample;

import Graphics.SignalGraph;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

public class MainController {
    @FXML
    private Button buttonGenerateSignal;
    @FXML
    private MenuItem newFile, loadFile, saveFile, deleteFile, about;
    @FXML
    private LineChart<Double, Double> signalId, processedSignalId;
    private SignalGraph signalGraph, signalGraphProcessed;

    //  slider
    @FXML
    private Slider sliderStep;
    @FXML
    private Label labelSlider;


    public void init() {
        labelSlider.setVisible(true);
        labelSlider.setText(String.format(sliderStep.valueProperty().getValue().toString(), ".2f"));
        Double valueSlider = sliderStep.getValue();

        //  check value not be 0.0
        if(valueSlider == 0.0)
            valueSlider += 0.1;

        signalGraph = new SignalGraph(signalId, valueSlider, 20);
        signalGraphProcessed = new SignalGraph(processedSignalId, valueSlider, 20);
    }

    public void onNewFile(ActionEvent event) {
        signalId.getData().clear();
        processedSignalId.getData().clear();
    }

    public void onLoadFile(ActionEvent event) {
        //  load graph from disk
        signalGraph.loadFromFile();
        signalGraph.plotGraphOneSingleAxes();
    }

    public void onSaveFile(ActionEvent event) {
        //  save graph on disk
        signalGraphProcessed.toFile("processed_signal");
    }

    public void onDeleteFile(ActionEvent event) {
        // delete file on the disk


        signalId.getData().clear();
        processedSignalId.getData().clear();
    }

    public void onAboutProgram(ActionEvent event) {

    }

    public void onMouseSlide(MouseEvent mouseEvent) {
        sliderStep.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                labelSlider.setText(String.format("%.2f", newVal));
            }
        });
    }

    public void onDerivate(ActionEvent event) {
        //  plot derivative graph

        signalGraph.derivateValuesFromGraph(signalGraphProcessed, sliderStep.getValue(), 20);

    }

    public void onSine(ActionEvent event) {
        signalGraph.plotGraph(e->Math.sin(e));
    }

    public void onCosine(ActionEvent event) {
        signalGraph.plotGraph(e->Math.cos(e));
    }

    public void onExponential(ActionEvent event) {
        signalGraph.plotGraph(e->Math.exp(e));
    }

    public void onIntegrate(ActionEvent event) {
        signalGraph.integrateValuesFromGraph(signalGraphProcessed, sliderStep.getValue(), 20);
    }

    public void onLowPass(ActionEvent event) {
        double[] filter = {0.0, 1.0, 0.0};
        signalGraph.applyFilter(signalGraphProcessed, sliderStep.getValue(), 20, filter, 3);
    }

    public void onHighPass(ActionEvent event) {
        double[] filter = {1/2, 1/2, 1/2};
        signalGraph.applyFilter(signalGraphProcessed, sliderStep.getValue(), 20, filter, 3);
    }
}
