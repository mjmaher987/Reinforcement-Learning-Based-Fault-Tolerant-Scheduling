// import necessary packages

import A2C.A2C_Scheduler;
import FCFS.FCFS_Scheduler;
import QLearning.QLearningScheduler;
import SJF.SJF_Scheduler;
import com.opencsv.CSVWriter;
import org.cloudbus.cloudsim.Cloudlet;
import utils.Constants;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;



// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 1st
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad

public class Main {
    public static void main(String[] args) {
        // Running 4 schedulers separately
        int[] tasks = {10, 20, 30, 40, 50, 200, 400, 600, 800, 1000};
        int[] datacenters = {2, 5, 8, 10, 50, 100, 150, 200};
//        for (int i = 0; i < 10; i++) {
//            do_everything(args);
//
//            Constants.NO_OF_TASKS = tasks[i];
//            for (int j = 0; j < 8; j++) {
//                Constants.NO_OF_DATA_CENTERS = datacenters[j];
//            }
//
//
//        }
        do_everything(args);
    }

    private static void do_everything(String[] args) {
        FCFS_Scheduler.main(args);
        printCloudletList(FCFS_Scheduler.getList(), FCFS_Scheduler.getExecMatrix(), FCFS_Scheduler.getCommMatrix(), "data/fcfs_data.csv");


        SJF_Scheduler.main(args);
        printCloudletList(SJF_Scheduler.getList(), SJF_Scheduler.getExecMatrix(), SJF_Scheduler.getCommMatrix(), "data/sjf_data.csv");

        A2C_Scheduler.main(args);
        printCloudletList(A2C_Scheduler.getList(), A2C_Scheduler.getExecMatrix(), A2C_Scheduler.getCommMatrix(), "data/a2c_data.csv");

        QLearningScheduler.main(args);
        printCloudletList(QLearningScheduler.getList(), QLearningScheduler.getExecMatrix(), QLearningScheduler.getCommMatrix(), "data/qlearning_data.csv");

    }

    public static void printCloudletList(List<Cloudlet> list, double[][] execMatrix, double[][] commMatrix, String csvFilePath) {
        int size = list.size();
        Cloudlet cloudlet;

        // Create a CSV writer
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            String[] header = {"Cloudlet ID", "Status", "Data center ID", "VM ID", "Time",
                    "Start Time", "Finish Time", "Waiting", "Completion", "Cost"};
            writer.writeNext(header);

            DecimalFormat dft = new DecimalFormat("####.##");
            dft.setMinimumIntegerDigits(2);

            double totalCompletionTime = 0;
            double totalCost = 0;
            double totalWaitingTime = 0;

            for (int i = 0; i < size; i++) {
                cloudlet = list.get(i);

                String cloudletId = dft.format(cloudlet.getCloudletId());
                String status = cloudlet.getCloudletStatus() == Cloudlet.SUCCESS ? "SUCCESS" : "Failure";
                String dataCenterId = dft.format(cloudlet.getResourceId());
                String vmId = dft.format(cloudlet.getVmId());
                String time = dft.format(cloudlet.getActualCPUTime());
                String startTime = dft.format(cloudlet.getExecStartTime());
                String finishTime = dft.format(cloudlet.getFinishTime());
                String waitingTime = dft.format(cloudlet.getWaitingTime());
                String completionTime = dft.format(cloudlet.getActualCPUTime() + cloudlet.getWaitingTime());
                String cost = dft.format(cloudlet.getCostPerSec() * cloudlet.getActualCPUTime());

                String[] row = {cloudletId, status, dataCenterId, vmId, time, startTime, finishTime, waitingTime,
                        completionTime, cost};
                writer.writeNext(row);

                if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                    totalCompletionTime += Double.parseDouble(completionTime);
                    totalCost += Double.parseDouble(cost);
                    totalWaitingTime += Double.parseDouble(waitingTime);
                }
            }

            double makespan = calcMakespan(list, execMatrix, commMatrix);
            String[] makespanRow = {"Makespan using QLearning:", String.valueOf(makespan)};
            writer.writeNext(makespanRow);

            double avgCompletionTime = totalCompletionTime / size;
            String[] completionTimeRow = {"Total Completion Time:", String.valueOf(totalCompletionTime)};
            writer.writeNext(completionTimeRow);
            String[] avgCompletionTimeRow = {"Avg Completion Time:", String.valueOf(avgCompletionTime)};
            writer.writeNext(avgCompletionTimeRow);

            double avgCost = totalCost / size;
            String[] costRow = {"Total Cost:", String.valueOf(totalCost)};
            writer.writeNext(costRow);
            String[] avgCostRow = {"Avg Cost:", String.valueOf(avgCost)};
            writer.writeNext(avgCostRow);

            double avgWaitingTime = totalWaitingTime / size;
            String[] waitingTimeRow = {"Avg Waiting Time:", String.valueOf(avgWaitingTime)};
            writer.writeNext(waitingTimeRow);

            System.out.println("Data saved to " + csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void printCloudletList(List<Cloudlet> list, double[][] execMatrix, double[][] commMatrix) {
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Cloudlet List");
//
//            // Create headers
//            Row headerRow = sheet.createRow(0);
//            headerRow.createCell(0).setCellValue("Cloudlet ID");
//            headerRow.createCell(1).setCellValue("Status");
//            headerRow.createCell(2).setCellValue("Data Center ID");
//            headerRow.createCell(3).setCellValue("VM ID");
//            headerRow.createCell(4).setCellValue("Time");
//            headerRow.createCell(5).setCellValue("Start Time");
//            headerRow.createCell(6).setCellValue("Finish Time");
//            headerRow.createCell(7).setCellValue("Waiting");
//            headerRow.createCell(8).setCellValue("Completion");
//            headerRow.createCell(9).setCellValue("Cost");
//
//            // Populate data
//            DecimalFormat dft = new DecimalFormat("####.##");
//            dft.setMinimumIntegerDigits(2);
//            for (int i = 0; i < list.size(); i++) {
//                Cloudlet cloudlet = list.get(i);
//                Row row = sheet.createRow(i + 1);
//                row.createCell(0).setCellValue(dft.format(cloudlet.getCloudletId()));
//                row.createCell(1).setCellValue(cloudlet.getCloudletStatus() == Cloudlet.SUCCESS ? "SUCCESS" : "Failure");
//                row.createCell(2).setCellValue(dft.format(cloudlet.getResourceId()));
//                row.createCell(3).setCellValue(dft.format(cloudlet.getVmId()));
//                row.createCell(4).setCellValue(dft.format(cloudlet.getActualCPUTime()));
//                row.createCell(5).setCellValue(dft.format(cloudlet.getExecStartTime()));
//                row.createCell(6).setCellValue(dft.format(cloudlet.getFinishTime()));
//                row.createCell(7).setCellValue(dft.format(cloudlet.getWaitingTime()));
//                double completionTime = cloudlet.getActualCPUTime() + cloudlet.getWaitingTime();
//                row.createCell(8).setCellValue(dft.format(completionTime));
//                double cost = cloudlet.getCostPerSec() * cloudlet.getActualCPUTime();
//                row.createCell(9).setCellValue(dft.format(cost));
//            }
//
//            // Create a sheet for execMatrix
//            Sheet execMatrixSheet = workbook.createSheet("Exec Matrix");
//            for (int i = 0; i < execMatrix.length; i++) {
//                Row row = execMatrixSheet.createRow(i);
//                for (int j = 0; j < execMatrix[i].length; j++) {
//                    row.createCell(j).setCellValue(execMatrix[i][j]);
//                }
//            }
//
//            // Create a sheet for commMatrix
//            Sheet commMatrixSheet = workbook.createSheet("Comm Matrix");
//            for (int i = 0; i < commMatrix.length; i++) {
//                Row row = commMatrixSheet.createRow(i);
//                for (int j = 0; j < commMatrix[i].length; j++) {
//                    row.createCell(j).setCellValue(commMatrix[i][j]);
//                }
//            }
//
//            try (FileOutputStream fileOut = new FileOutputStream("output.xlsx")) {
//                workbook.write(fileOut);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//    public static void printCloudletList(List<Cloudlet> list, double[][] execMatrix, double[][] commMatrix) {
//        int size = list.size();
//        Cloudlet cloudlet;
//
//        String indent = "    ";
//        Log.printLine();
//        Log.printLine("========== OUTPUT ==========");
//        Log.printLine("Cloudlet ID" + indent + "STATUS" +
//                indent + "Data center ID" +
//                indent + "VM ID" +
//                indent + indent + "Time" +
//                indent + indent+ "Start Time" +
//                indent + indent+ indent+"Finish Time"+
//                indent + "Waiting"+
//                indent + "Completion"+
//                indent + "Cost");
//
//        //HERE:
//        double totalCompletionTime=0;
//        double totalCost=0;
//        double totalWaitingTime=0;
//        //-------------------------
//
//        DecimalFormat dft = new DecimalFormat("####.##");
//        dft.setMinimumIntegerDigits(2);
//        for (int i = 0; i < size; i++) {
//            cloudlet = list.get(i);
//            Log.print(indent + dft.format(cloudlet.getCloudletId()) + indent + indent);
//
//            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
//                Log.print("SUCCESS");
//
//                //HERE:
//                double completionTime= cloudlet.getActualCPUTime()+ cloudlet.getWaitingTime();
//                double cost= cloudlet.getCostPerSec()* cloudlet.getActualCPUTime() ;
//
//                //Note: the execution time for a task is cloudlet.getActualCPUTime()
//                //----------------------
//                Log.printLine(indent + indent + dft.format(cloudlet.getResourceId()) +
//                        indent + indent + indent + dft.format(cloudlet.getVmId()) +
//                        indent + indent +indent + dft.format(cloudlet.getActualCPUTime()) +
//                        indent + indent + dft.format(cloudlet.getExecStartTime()) +
//                        indent + indent  +indent+indent+ dft.format(cloudlet.getFinishTime())+
//                        indent + indent  +indent+ dft.format(cloudlet.getWaitingTime() )+
//                        indent + indent  + dft.format(completionTime )+
//                        indent + indent + dft.format(cost));
//                //HERE:
//                totalCompletionTime += completionTime;
//                totalCost += cost;
//                totalWaitingTime+=cloudlet.getWaitingTime();
//                //-----------------------------------------
//            }
//        }
//        double makespan = calcMakespan(list, execMatrix, commMatrix);
//        Log.printLine("Makespan using QLearning: " + makespan);
//        //Added:
//        Log.printLine("Total Completion Time: " + totalCompletionTime +" Avg Completion Time: "+ (totalCompletionTime/size));
//        Log.printLine("Total Cost : " + totalCost+ " Avg cost: "+ (totalCost/size));
//        Log.printLine("Avg Waiting Time: "+ (totalWaitingTime/size));
//    }

    private static double calcMakespan(List<Cloudlet> list, double[][] execMatrix, double[][] commMatrix) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = list.get(i).getVmId() % Constants.NO_OF_DATA_CENTERS;
            if (dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] += execMatrix[i][dcId] + commMatrix[i][dcId];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }

}
