package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import java.util.Arrays;
import com.opencsv.*;

/**
 * Evaluate Multi-Class Classification Model
 */
public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        int numClasses = 5;
        double crossEntropy = 0;
        int[][] confusionMatrix = new int[numClasses][numClasses];
        int totalSamples = 0;

        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            csvReader.close();

            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]) - 1; // Convert to 0-based index
                double[] y_predicted = new double[numClasses];
                for (int i = 0; i < numClasses; i++) {
                    y_predicted[i] = Double.parseDouble(row[i + 1]);
                }
                
                // Compute Cross Entropy
                crossEntropy += -Math.log(y_predicted[y_true] + 1e-10);
                
                // Determine predicted class (argmax)
                int y_pred_class = 0;
                double maxProb = y_predicted[0];
                for (int i = 1; i < numClasses; i++) {
                    if (y_predicted[i] > maxProb) {
                        maxProb = y_predicted[i];
                        y_pred_class = i;
                    }
                }
                
                // Update confusion matrix
                confusionMatrix[y_true][y_pred_class]++;
                totalSamples++;
            }
        } catch (Exception e) {
            System.out.println("Error reading the CSV file.");
            return;
        }
        
        crossEntropy /= totalSamples;
        
        // Print results
        System.out.printf("Cross-Entropy: %.5f\n", crossEntropy);
        System.out.println("Confusion Matrix:");
        for (int i = 0; i < numClasses; i++) {
            System.out.println(Arrays.toString(confusionMatrix[i]));
        }
    }
}
