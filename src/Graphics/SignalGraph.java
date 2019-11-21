package Graphics;

import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.function.Function;

import static javafx.scene.chart.XYChart.*;

public class SignalGraph {

    private LineChart<Double, Double> lineGraph;
    private double steps, minValue, maxValue, endIntv;
    private int range, noPoints, startIntv;


    private XYChart.Series<Double, Double> seriesPoints;

    private ArrayList<Double> values;

    public int getNoPoints() {
        return noPoints;
    }

    public void setNoPoints(int noPoints) {
        this.noPoints = noPoints;
    }

    public double getEndIntv() {
        return endIntv;
    }

    public void setEndIntv(double endIntv) {
        this.endIntv = endIntv;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public Series<Double, Double> getSeriesPoints() {
        return seriesPoints;
    }

    public LineChart<Double, Double> getLineGraph() {
        return lineGraph;
    }

    public void setSeriesPoints(Series<Double, Double> seriesPoints) {
        this.seriesPoints = seriesPoints;
    }

    public void setLineGraph(LineChart<Double, Double> lineGraph) {
        this.lineGraph = lineGraph;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public SignalGraph(LineChart<Double, Double> lineGraph, double steps, int range) {
        this.lineGraph = lineGraph;
        this.values = new ArrayList<>();
        this.seriesPoints = new XYChart.Series<Double, Double>();
        this.steps = steps;
        this.range = range;
    }

    public SignalGraph(LineChart<Double, Double> lineGraph, double steps, int startIntv, int endIntv) {
        this.lineGraph = lineGraph;
        this.steps = steps;
        this.startIntv = startIntv;
        this.endIntv = endIntv;
    }

    public void plotGraph(Function<Double, Double> function) {
        //populating the series with data

        //clear something value if you want try again plot

        for(double i = 0; i < 32; i+=0.5) {
            seriesPoints.getData().add(new Data(i, function.apply(i)));
        }

        lineGraph.getData().add(seriesPoints);
    }

    public void plotGraphOneSingleAxes(Double frequency) {

        //clear something value if you want try again plot
        double periodSamples = 1.0 / frequency;
        int endIntv = (int)(periodSamples * noPoints);

        for(double i = 0; i < endIntv; i+=periodSamples) {
            if(!values.isEmpty())
                seriesPoints.getData().add(new Data(i, values.remove(0)));
            else
                break;
        }
        lineGraph.getData().add(seriesPoints);
    }

    public void loadFromFile() {

        //  function to read from file

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if(selectedFile == null) {
            System.out.println("no file selected");
        } else {
            try {
                lineGraph.getData().clear();
                Scanner scanner = new Scanner(selectedFile);
                while (scanner.hasNextDouble()) {
                    this.values.add(scanner.nextDouble());
                }
                this.minValue = Collections.min(this.values);
                this.maxValue = Collections.max(this.values);
                this.noPoints = values.size();
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadFromFile(String file) {

        //  function to read from file
        File selectedFile = new File(file);

        if(selectedFile == null) {
            System.out.println("no file selected");
        } else {
            try {
                lineGraph.getData().clear();
                Scanner scanner = new Scanner(selectedFile);
                while (scanner.hasNextDouble()) {
                    this.values.add(scanner.nextDouble());
                }
                this.minValue = Collections.min(this.values);
                this.maxValue = Collections.max(this.values);
                this.noPoints = values.size();
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void toFile(String nameFile) {
        //  must change name of file

        String currentDirectory = System.getProperty("user.dir");
        String absolutePath = currentDirectory + "\\" + nameFile + ".txt";

        StringBuilder sb = new StringBuilder();


        if(!seriesPoints.getData().isEmpty()) {

            for (Data<Double, Double> entry : seriesPoints.getData()) {
                sb.append(entry.getYValue().toString() + "\n");
            }

        } else {
            //  change in future with pop up
            System.out.println("Empty graph");
        }

        try (PrintWriter out = new PrintWriter(absolutePath)) {
            out.println(sb);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void derivateValuesFromGraph(SignalGraph derivateSignal, Double step, Double frequency) {

        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        ArrayList<Double> derivatedValue = new ArrayList<>();

        // get all point from graph_1
        for(Data<Double, Double> entry : seriesPoints.getData()) {
            values.add(entry.getYValue());
        }


        if(!values.isEmpty()) {
            derivateSignal.getLineGraph().getData().clear();
            for (int i = 0; i < values.size() - 1; i++) {
                derivatedValue.add((values.get(i+1)-values.get(i))/step);
            }

            int endIntv = (int)(1.0/frequency * derivatedValue.size());
            derivateSignal.setMinValue(Collections.min(derivatedValue));
            derivateSignal.setMaxValue(Collections.max(derivatedValue));
            derivateSignal.setNoPoints(derivatedValue.size());

            for(double i = 0; i < endIntv; i+=step) {
                if(!derivatedValue.isEmpty())
                    series.getData().add(new Data<>(i, derivatedValue.remove(0)));
                else
                    break;
            }

            derivateSignal.setSeriesPoints(series);
            derivateSignal.getLineGraph().getData().add(series);
        } else {
            System.out.println("Empty list with point");
        }
    }

    public void integrateValuesFromGraph(SignalGraph integrateSignal, Double steps, Double frequency) {

        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        ArrayList<Double> integratedValue = new ArrayList<>();
        ArrayList<Double> dataValue = new ArrayList<>();

        for(Data<Double, Double> entry : seriesPoints.getData()) {
            dataValue.add(entry.getYValue());
        }

        Double prev = 0.0;
        for (int i = 0; i < dataValue.size() - 1; i++) {
            integratedValue.add(prev + steps*(dataValue.get(i + 1) + dataValue.get(i)) / 2.0);
            prev = integratedValue.get(i);
        }

        integrateSignal.setMinValue(Collections.min(integratedValue));
        integrateSignal.setMaxValue(Collections.max(integratedValue));
        int endIntv = (int)(1.0 / frequency * integratedValue.size());
        integrateSignal.setNoPoints(integratedValue.size());

        if(!integratedValue.isEmpty()) {
            integrateSignal.getLineGraph().getData().clear();
            for(double i = 0; i < endIntv; i+=steps) {
                if(!integratedValue.isEmpty()) {
                    series.getData().add(new Data<>(i, integratedValue.remove(0)));
                } else {
                    break;
                }
            }

            integrateSignal.setSeriesPoints(series);
            integrateSignal.getLineGraph().getData().add(series);
        } else {
            System.out.println("Empty list of integrated values");
        }
    }

    public void applyFilter(SignalGraph filteredSignal, Double steps, double[] filter, int range, double frequency) {


        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        ArrayList<Double> valuesFiltered = new ArrayList<>();
        double temporarySum = 0;
        int offset = range / 2;

        // get all point from graph_1
        if(!values.isEmpty()) {
            values.clear();
        }

        for(Data<Double, Double> entry : seriesPoints.getData()) {
            values.add(entry.getYValue());
        }


        if(!values.isEmpty()) {
            filteredSignal.getLineGraph().getData().clear();
            for (int i = range / 2; i < values.size() - range / 2; i++) {
                for(int j = -range/2; j <=  range / 2; ++ j) {
                    temporarySum += values.get(i + j) * filter[j + range / 2];
                }

                valuesFiltered.add(temporarySum);
                temporarySum = 0;

            }

            int endIntv = (int)(valuesFiltered.size() * 1.0/frequency);

            filteredSignal.setMinValue(Collections.min(valuesFiltered));
            filteredSignal.setMaxValue(Collections.max(valuesFiltered));
            filteredSignal.setNoPoints(valuesFiltered.size());

            for(double i = 0; i < endIntv; i+=steps) {
                if(!valuesFiltered.isEmpty())
                    series.getData().add(new Data<>(i, valuesFiltered.remove(0)));
                else
                    break;
            }

            filteredSignal.setSeriesPoints(series);
            filteredSignal.getLineGraph().getData().add(series);
        } else {
            System.out.println("Empty list with point");
        }

    }

    public void zoomOnRectangle(Rectangle zoomRect, NumberAxis xAxis, NumberAxis yAxis) {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = zoomTopLeft.getX() - yAxisInScene.getX() ;
        double yOffset = zoomBottomRight.getY() - xAxisInScene.getY();
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }
}
