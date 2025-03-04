package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App {
    public static void main(String[] args) {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        String bestModel = "";
        double minMSE = Double.MAX_VALUE;

        for (String filePath : filePaths) {
            double mse = 0, mae = 0, mare = 0;
            int n = 0;
            
            try {
                FileReader filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                List<String[]> allData = csvReader.readAll();
                csvReader.close();
                
                for (String[] row : allData) {
                    float y_true = Float.parseFloat(row[0]);
                    float y_predicted = Float.parseFloat(row[1]);
                    
                    mse += Math.pow(y_true - y_predicted, 2);
                    mae += Math.abs(y_true - y_predicted);
                    mare += Math.abs(y_true - y_predicted) / (Math.abs(y_true) + 1e-10); // epsilon to avoid division by zero
                    
                    n++;
                }
            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                continue;
            }
            
            if (n > 0) {
                mse /= n;
                mae /= n;
                mare = (mare / n) * 100;
            }
            
            System.out.println("Results for " + filePath + ":");
            System.out.printf("MSE: %.5f\n", mse);
            System.out.printf("MAE: %.5f\n", mae);
            System.out.printf("MARE: %.5f%%\n", mare);
            System.out.println();
            
            if (mse < minMSE) {
                minMSE = mse;
                bestModel = filePath;
            }
        }
        
        System.out.println("Recommended model: " + bestModel);
    }
}
