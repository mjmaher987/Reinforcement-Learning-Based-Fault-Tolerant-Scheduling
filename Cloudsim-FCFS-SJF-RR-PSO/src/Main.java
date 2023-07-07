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
        // Running schedulers separately
        int[] tasks = {10, 20, 30, 40, 50, 200, 400, 600, 800, 1000};
        int[] datacenters = {2, 5, 8, 10, 50, 100, 150, 200};

        // Iterate over all possible number of task_n and datacenter_n
        for (int task : tasks) {
            do_everything(args);

            Constants.NO_OF_TASKS = task;
            for (int datacenter : datacenters) {
                Constants.NO_OF_DATA_CENTERS = datacenter;
            }
            do_everything(args);


        }

//        // Set the number of tasks and number of datacenters arbitrarily
//        Constants.NO_OF_TASKS = tasks[5];
//        Constants.NO_OF_DATA_CENTERS = datacenters[2];


    }


    /*
     Function Name:
        do_everything
     Functionality:
        execute all scheduling policies
     input(s):
        String[] args: this is the first input of the main function for start execution
     output(s):
        void: this function doesn't return anything directly, rather it saves the necessary data
            to .csv files and also prints the log data
    */
    private static void do_everything(String[] args) {
        // Execute the FCFS Scheduler
        FCFS_Scheduler.main(args);
        save_outputs(FCFS_Scheduler.getList(), FCFS_Scheduler.getExecMatrix(), FCFS_Scheduler.getCommMatrix(), "data/fcfs_data.csv");

        // Execute the SJF Scheduler
        SJF_Scheduler.main(args);
        save_outputs(SJF_Scheduler.getList(), SJF_Scheduler.getExecMatrix(), SJF_Scheduler.getCommMatrix(), "data/sjf_data.csv");

        // Execute the A2C Scheduler
        A2C_Scheduler.main(args);
        save_outputs(A2C_Scheduler.getList(), A2C_Scheduler.getExecMatrix(), A2C_Scheduler.getCommMatrix(), "data/a2c_data.csv");

        // Execute the Q-Learning Scheduler
        QLearningScheduler.main(args);
        save_outputs(QLearningScheduler.getList(), QLearningScheduler.getExecMatrix(), QLearningScheduler.getCommMatrix(), "data/qlearning_data.csv");

    }


    /*
     Function Name:
        save_outputs
     Functionality:
        save the outputs of all schedulers to .csv files
     input(s):
        List<Cloudlet> list: the list of all tasks (cloudlets)
        double[][] execMatrix: execution time of cloudlets on different data centers.
        double[][] commMatrix: communication cost between different cloudlets and data centers
        String csvFilePath: filepath of the csv file (that has to be saved)
     output(s):
        void: this function doesn't return anything directly, rather it saves the necessary data
            to .csv files
    */
    public static void save_outputs(List<Cloudlet> list, double[][] execMatrix, double[][] commMatrix, String csvFilePath) {
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


    /*
     Function Name:
        calcMakespan
     Functionality:
        calculates the makespan of a set of cloudlets executed on different data centers.
        The makespan is a metric that represents the total time taken to complete all the cloudlets in the simulation.
     input(s):
        List<Cloudlet> list: the list of all tasks (cloudlets)
        double[][] execMatrix: execution time of cloudlets on different data centers.
        double[][] commMatrix: communication cost between different cloudlets and data centers
     output(s):
        double makespan: the makespan of the cloudlets on the datacenters (that is a float number)
    */
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
