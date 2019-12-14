package sample;

import Graphics.SignalGraph;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.PointLight;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.converter.NumberStringConverter;

public class MainController {

    @FXML
    private AnchorPane AnchorPaneID;
    @FXML
    private NumberAxis xAxisSignalID, yAxisSignalID, xAxisProcessedSignalID, yAxisProcessedSignalID;
    @FXML
    private Button buttonGenerateSignal;
    @FXML
    private TextField startWindow, endWindow;
    @FXML
    private MenuItem newFile, loadFile, saveFile, deleteFile, about;
    @FXML
    private LineChart<Double, Double> signalId, processedSignalId;
    private SignalGraph signalGraph, signalGraphProcessed;

    //  slider
    @FXML
    private Slider sliderStep, idAlphaFilted, idMediereFilter;
    @FXML
    private Label labelSlider, labelXYSignal, labelXYProcessedSignal, idAlphaLabel, idMediereLabel;

    private int endIntv = 32;

    private final Rectangle zoomRect = new Rectangle();
    private final Rectangle zoomRectProcessedSignal = new Rectangle();



    public void init() {
        labelSlider.setVisible(true);
        labelXYProcessedSignal.setVisible(true);
        labelXYSignal.setVisible(true);

        idAlphaLabel.setText(String.format(idAlphaFilted.valueProperty().getValue().toString(), ".2f"));
        idMediereLabel.setText(String.format(idMediereFilter.valueProperty().getValue().toString(), ".2f"));

        labelSlider.setText(String.format(sliderStep.valueProperty().getValue().toString(), ".2f"));
        Double valueSlider = 1 / sliderStep.getValue();


        startWindow.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        endWindow.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        //  check value not be 0.0
        if(valueSlider == 0.0)
            valueSlider += 0.1;

        signalGraph = new SignalGraph(signalId, valueSlider, endIntv);
        signalGraphProcessed = new SignalGraph(processedSignalId, valueSlider, endIntv);


        zoomRect.setManaged(false);
        zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        AnchorPaneID.getChildren().add(zoomRect);
        setUpZooming(zoomRect, signalId, false);

        zoomRectProcessedSignal.setManaged(false);
        zoomRectProcessedSignal.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        AnchorPaneID.getChildren().add(zoomRectProcessedSignal);
        setUpZooming(zoomRectProcessedSignal, processedSignalId, true);
    }

    public void onNewFile(ActionEvent event) {
        signalId.getData().clear();
        processedSignalId.getData().clear();
    }

    public void onLoadFile(ActionEvent event) {
        //  load graph from disk
        signalGraph.loadFromFile();
        yAxisSignalID.setLowerBound(signalGraph.getMinValue() - 1.0);
        yAxisSignalID.setUpperBound(signalGraph.getMaxValue() + 1.0);
        xAxisSignalID.setUpperBound(signalGraph.getNoPoints() * 1.0/sliderStep.getValue());
        signalGraph.plotGraphOneSingleAxes(sliderStep.getValue());
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About program");
        alert.setHeaderText(null);
        alert.setContentText("I have a great program for signal processing!");

        alert.showAndWait();
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

        signalGraph.derivateValuesFromGraph(signalGraphProcessed, 1.0 / sliderStep.getValue(), sliderStep.getValue());

        //  temporary i will keep this
        yAxisProcessedSignalID.setLowerBound(signalGraphProcessed.getMinValue() - 1.0);
        yAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getMaxValue() + 1.0);

        xAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getNoPoints() * 1/sliderStep.getValue());

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
        signalGraph.integrateValuesFromGraph(signalGraphProcessed, 1.0 /sliderStep.getValue(), sliderStep.getValue());

        //  temporary i will keep this
        yAxisProcessedSignalID.setLowerBound(signalGraphProcessed.getMinValue() - 1.0);
        yAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getMaxValue() + 1.0);
        xAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getNoPoints() * 1/sliderStep.getValue());
    }

    public void onLowPass(ActionEvent event) {
        double[] filter = {1/3.0, 1/3.0, 1/3.0};
        signalGraph.applyFilter(signalGraphProcessed, 1.0 /sliderStep.getValue(), filter, 3, sliderStep.getValue());

        //  temporary i will keep this
        yAxisProcessedSignalID.setLowerBound(signalGraphProcessed.getMinValue() - 1.0);
        yAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getMaxValue() + 1.0);
        xAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getNoPoints() * 1/sliderStep.getValue());
    }

    public void onHighPass(ActionEvent event) {
        double[] filter = {-1 / 3.0, 2/3.0, -1/3.0};
        signalGraph.applyFilter(signalGraphProcessed, 1.0 /sliderStep.getValue(), filter, 3, sliderStep.getValue());

        //  temporary i will keep this
        yAxisProcessedSignalID.setLowerBound(signalGraphProcessed.getMinValue() - 1.0);
        yAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getMaxValue() + 1.0);
        xAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getNoPoints() * 1/sliderStep.getValue());
    }

    public void onLoadFileButton(ActionEvent event) {

        signalGraph.loadFromFile("D:\\A.C\\APD\\APD_P\\signal_test.txt");

        yAxisSignalID.setLowerBound(signalGraph.getMinValue() - 1.0);
        yAxisSignalID.setUpperBound(signalGraph.getMaxValue() + 1.0);
        xAxisSignalID.setUpperBound(signalGraph.getNoPoints() * 1.0/sliderStep.getValue());
        signalGraph.plotGraphOneSingleAxes(sliderStep.getValue());
    }

    public void onZoomSignal(ActionEvent event) {
        signalGraph.zoomOnRectangle(new Rectangle(zoomRect.getX(), zoomRect.getY(), zoomRect.getWidth(), zoomRect.getHeight()), xAxisSignalID, yAxisSignalID);
        zoomRect.setFill(null);
    }

    private void setUpZooming(final Rectangle rect, final Node zoomingNode, boolean isRight) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        zoomingNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseAnchor.set(new Point2D(event.getX(), event.getY()));
                rect.setWidth(0);
                rect.setHeight(0);
            }
        });
        zoomingNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
                double x = event.getX() ;
                double y = event.getY();
                if(isRight) {
                    rect.setX(Math.min(x + 865, mouseAnchor.get().getX()) + 865);
                } else {
                    rect.setX(Math.min(x + 15, mouseAnchor.get().getX()) + 15);
                }
                rect.setY(Math.min(y + 36, mouseAnchor.get().getY()) + 36);
                rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
                rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
            }
        });

    }

    public void onResetZoomSignal(ActionEvent event) {
        xAxisSignalID.setLowerBound(0);
        xAxisSignalID.setUpperBound(80);
        yAxisSignalID.setLowerBound(-5);
        yAxisSignalID.setUpperBound(5);
    }

    public void OnZoomProcessedSignal(ActionEvent event) {
        signalGraph.zoomOnRectangle(new Rectangle(zoomRectProcessedSignal.getX(), zoomRectProcessedSignal.getY(), zoomRectProcessedSignal.getWidth(), zoomRectProcessedSignal.getHeight()), xAxisProcessedSignalID, yAxisProcessedSignalID);
        zoomRectProcessedSignal.setFill(null);
    }

    public void OnResetProcessedSignal(ActionEvent event) {

//        by default
        xAxisProcessedSignalID.setLowerBound(0);
        xAxisProcessedSignalID.setUpperBound(80);
        yAxisProcessedSignalID.setLowerBound(-5);
        yAxisProcessedSignalID.setUpperBound(5);
    }


    public void onFilterWithAlpha(ActionEvent event) {
        signalGraph.alphaFilter(signalGraphProcessed, 1.0/sliderStep.getValue(), idAlphaFilted.getValue(), sliderStep.getValue());

        yAxisProcessedSignalID.setLowerBound(signalGraphProcessed.getMinValue() - 1.0);
        yAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getMaxValue() + 1.0);
        xAxisProcessedSignalID.setUpperBound(signalGraphProcessed.getNoPoints() * 1/sliderStep.getValue());
    }

    public void onMediere(ActionEvent event) {
        signalGraph.mediereFilter(signalGraphProcessed, 1.0 / sliderStep.getValue(), sliderStep.getValue(), (int)idMediereFilter.getValue());
    }

    public void onMouseReleasedSliderAlpha(MouseEvent mouseEvent) {
        idAlphaFilted.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                idAlphaLabel.setText(String.format("%.2f", newVal));
            }
        });
    }

    public void onMouseRelesedMediere(MouseEvent mouseEvent) {
        idMediereFilter.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                idMediereLabel.setText(String.format("%d", (int)newVal));
            }
        });

    }

    public void onWindowing(ActionEvent event) {


        boolean isNumeric = true;
        int startGraphIntv = 0, endtGraphIntv = 0;

        try{
            startGraphIntv = Integer.parseInt(startWindow.getText());
            endtGraphIntv = Integer.parseInt(endWindow.getText());
        } catch (NumberFormatException e) {
            isNumeric = false;
            System.out.println(e.getMessage());
        }

        if (isNumeric)
            if(startGraphIntv < endtGraphIntv && signalGraph != null)
                signalGraph.sqareWindowing(startGraphIntv, endtGraphIntv, 1.0/sliderStep.getValue());

    }
}
