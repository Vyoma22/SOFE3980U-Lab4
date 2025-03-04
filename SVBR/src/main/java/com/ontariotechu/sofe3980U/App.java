package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.opencsv.*;

/**
 * Evaluate Binary Classification Model
 */
public class App {
    public static void main(String[] args) {
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        String bestModel = "";
        double bestPerformance = 0; // Higher is better (based on AUC-ROC)

        for (String filePath : filePaths) {
            double bce = 0;
            int TP = 0, TN = 0, FP = 0, FN = 0;
            int nPositive = 0, nNegative = 0;
            List<Double> thresholds = new ArrayList<>();
            List<Double> tprList = new ArrayList<>();
            List<Double> fprList = new ArrayList<>();
            
            try {
                FileReader filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                List<String[]> allData = csvReader.readAll();
                csvReader.close();
                
                for (String[] row : allData) {
                    int y_true = Integer.parseInt(row[0]);
                    double y_pred = Double.parseDouble(row[1]);
                    
                    // Compute BCE
                    if (y_true == 1) {
                        bce += -Math.log(y_pred + 1e-10);
                    } else {
                        bce += -Math.log(1 - y_pred + 1e-10);
                    }
                    
                    // Compute confusion matrix at default threshold 0.5
                    int y_pred_binary = (y_pred >= 0.5) ? 1 : 0;
                    if (y_true == 1 && y_pred_binary == 1) TP++;
                    if (y_true == 0 && y_pred_binary == 0) TN++;
                    if (y_true == 0 && y_pred_binary == 1) FP++;
                    if (y_true == 1 && y_pred_binary == 0) FN++;
                    
                    if (y_true == 1) nPositive++;
                    else nNegative++;
                    
                    thresholds.add(y_pred);
                }
            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                continue;
            }
            
            int total = TP + TN + FP + FN;
            bce /= total;
            double accuracy = (double) (TP + TN) / total;
            double precision = (TP + FP > 0) ? (double) TP / (TP + FP) : 0;
            double recall = (TP + FN > 0) ? (double) TP / (TP + FN) : 0;
            double f1_score = (precision + recall > 0) ? 2 * (precision * recall) / (precision + recall) : 0;
            
            // Compute AUC-ROC
            Collections.sort(thresholds);
            for (int i = 0; i <= 100; i++) {
                double threshold = i / 100.0;
                int tempTP = 0, tempFP = 0;
                for (double pred : thresholds) {
                    if (pred >= threshold) {
                        tempTP++;
                    } else {
                        tempFP++;
                    }
                }
                double tpr = (double) tempTP / nPositive;
                double fpr = (double) tempFP / nNegative;
                tprList.add(tpr);
                fprList.add(fpr);
            }
            
            double auc = 0;
            for (int i = 1; i < 100; i++) {
                auc += (tprList.get(i - 1) + tprList.get(i)) * Math.abs(fprList.get(i - 1) - fprList.get(i)) / 2;
            }
            
            System.out.println("Results for " + filePath + ":");
            System.out.printf("BCE: %.5f\n", bce);
            System.out.printf("Accuracy: %.5f\n", accuracy);
            System.out.printf("Precision: %.5f\n", precision);
            System.out.printf("Recall: %.5f\n", recall);
            System.out.printf("F1 Score: %.5f\n", f1_score);
            System.out.printf("AUC-ROC: %.5f\n", auc);
            System.out.println();
            
            if (auc > bestPerformance) {
                bestPerformance = auc;
                bestModel = filePath;
            }
        }
        
        System.out.println("Recommended model: " + bestModel);
    }
}
