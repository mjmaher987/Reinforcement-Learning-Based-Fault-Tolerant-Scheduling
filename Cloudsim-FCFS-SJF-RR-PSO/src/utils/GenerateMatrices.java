package utils;


import java.io.*;

// Last update: July 7th
public class GenerateMatrices {
    private static double[][] commMatrix, execMatrix;
    private final File commFile;
    private final File execFile;



    public GenerateMatrices() {

        // We have to create a file corresponding to the hyperparameters
        File directory = new File("Matrices/".concat(String.valueOf(Constants.NO_OF_DATA_CENTERS)).concat("_").concat(String.valueOf(Constants.NO_OF_TASKS)));
        directory.mkdirs();
        commFile = new File("Matrices/".concat(String.valueOf(Constants.NO_OF_DATA_CENTERS)).concat("_").concat(String.valueOf(Constants.NO_OF_TASKS)).concat("/CommunicationTimeMatrix.txt"));
        execFile = new File("Matrices/".concat(String.valueOf(Constants.NO_OF_DATA_CENTERS)).concat("_").concat(String.valueOf(Constants.NO_OF_TASKS)).concat("/ExecutionTimeMatrix.txt"));




        commMatrix = new double[Constants.NO_OF_TASKS][Constants.NO_OF_DATA_CENTERS];
        execMatrix = new double[Constants.NO_OF_TASKS][Constants.NO_OF_DATA_CENTERS];
        try {
            initCostMatrix();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCostMatrix() throws IOException {
        System.out.println("Initializing new Matrices...");
        BufferedWriter commBufferedWriter = new BufferedWriter(new FileWriter(commFile));
        BufferedWriter execBufferedWriter = new BufferedWriter(new FileWriter(execFile));

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            for (int j = 0; j < Constants.NO_OF_DATA_CENTERS; j++) {
                commMatrix[i][j] = Math.random() * 600 + 20;
                execMatrix[i][j] = Math.random() * 500 + 10;
                commBufferedWriter.write(String.valueOf(commMatrix[i][j]) + ' ');
                execBufferedWriter.write(String.valueOf(execMatrix[i][j]) + ' ');
            }
            commBufferedWriter.write('\n');
            execBufferedWriter.write('\n');
        }
        commBufferedWriter.close();
        execBufferedWriter.close();
    }


    public static double[][] getCommMatrix() {
        return commMatrix;
    }

    public static double[][] getExecMatrix() {
        return execMatrix;
    }
}
