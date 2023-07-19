// import necessary packages

import A2C.A2C_Scheduler;
import DoubleQLearning.DoubleQLearningScheduler;
import FCFS.FCFS_Scheduler;
import QLearning.QLearningScheduler;
import SJF.SJF_Scheduler;
import com.opencsv.CSVWriter;
import org.cloudbus.cloudsim.Cloudlet;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import utils.Constants;
import utils.GenerateMatrices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 7th
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad


public class Main {
    public static void main(String[] args) {
        double[] tasks = {10, 20, 30, 40, 50, 200, 400, 600, 800, 1000};
        double[] datacenters = {2, 5, 8, 10, 50, 100, 150, 200};

        // Iterate over all possible number of task_n and datacenter_n
        for (double task : tasks) {

            Constants.NO_OF_TASKS = (int) task;
            for (double datacenter : datacenters) {
                Constants.NO_OF_DATA_CENTERS = (int) datacenter;
                do_everything(args);
            }

        }


        List<String> types = new ArrayList<>();
        types.add("fcfs");
        types.add("sjf");
        types.add("a2c");
        types.add("qlearning");
        types.add("double");


        // Save based on task number


        for (double task : tasks) {
            List<double[]> datacenterDataList_makespan = new ArrayList<>();
            List<double[]> datacenterDataList_avg_completion = new ArrayList<>();
            List<double[]> datacenterDataList_avg_cost = new ArrayList<>();
            List<double[]> datacenterDataList_avg_wait = new ArrayList<>();
            for (String type : types) {
                double[] datacenter_makespan = new double[datacenters.length];
                double[] datacenter_avg_completion = new double[datacenters.length];
                double[] datacenter_avg_cost = new double[datacenters.length];
                double[] datacenter_avg_wait = new double[datacenters.length];
                int counter = 0;
                for (double datacenter : datacenters) {

                    TargetEntry e = find_proper_record(task, datacenter, type);

                    assert e != null;
                    datacenter_makespan[counter] = e.getMakespan();
                    datacenter_avg_completion[counter] = e.getAvg_completion();
                    datacenter_avg_cost[counter] = e.getAvg_cost();
                    datacenter_avg_wait[counter] = e.getAvg_wait();


                    counter += 1;


                }
                datacenterDataList_makespan.add(datacenter_makespan);
                datacenterDataList_avg_completion.add(datacenter_avg_completion);
                datacenterDataList_avg_cost.add(datacenter_avg_cost);
                datacenterDataList_avg_wait.add(datacenter_avg_wait);
            }


            show_chart((int) task, "Makespan", datacenterDataList_makespan, types, datacenters);
            show_chart((int) task, "Average Completion Time", datacenterDataList_avg_completion, types, datacenters);
            show_chart((int) task, "Average Cost", datacenterDataList_avg_cost, types, datacenters);
            show_chart((int) task, "Average Waiting Time", datacenterDataList_avg_wait, types, datacenters);


        }

    }

    private static void show_chart(int task, String yAxis, List<double[]> datacenterDataList, List<String> types, double[] datacenters) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .title("Task = ".concat(String.valueOf(task)))
                .xAxisTitle("Datacenters")
                .yAxisTitle(yAxis)
                .build();

        int cntr = 0;
        for (double[] datacenterData : datacenterDataList) {
            chart.addSeries(types.get(cntr), datacenters, datacenterData);
            cntr += 1;
        }


//        // Display the chart in a JFrame
//        JFrame frame = new JFrame(yAxis.concat(" Chart"));
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(new XChartPanel<>(chart));
//        frame.pack();
//        frame.setVisible(true);

        String outputPath = "charts/"; // Replace with the desired output folder path
        String outputFileName = String.valueOf(task).concat("-").concat(yAxis).concat(".png"); // Replace with the desired output file name

        try {
            BitmapEncoder.saveBitmap(chart, outputPath + outputFileName, BitmapEncoder.BitmapFormat.PNG);
            System.out.println("Chart saved as: " + outputPath + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TargetEntry find_proper_record(double task, double datacenter, String type) {
        for (TargetEntry t : TargetEntry.getAllTargetEntries()) {
            if (t.getCloudletNumber() == task && t.getVmNumber() == datacenter && t.getType().equals(type)) {
                return t;
            }
        }
        return null;
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
        String hyperparameters = "data/".concat(String.valueOf(Constants.NO_OF_DATA_CENTERS))
                .concat("_").concat(String.valueOf(Constants.NO_OF_TASKS));

        File directory = new File(hyperparameters);
        directory.mkdirs();

        new GenerateMatrices();

        // Execute the FCFS Scheduler
        FCFS_Scheduler.main(args);
        save_outputs(FCFS_Scheduler.getList(), FCFS_Scheduler.getExecMatrix(), FCFS_Scheduler.getCommMatrix(), hyperparameters.concat("/fcfs_data.csv"), 0);

        // Execute the SJF Scheduler
        SJF_Scheduler.main(args);
        save_outputs(SJF_Scheduler.getList(), SJF_Scheduler.getExecMatrix(), SJF_Scheduler.getCommMatrix(), hyperparameters.concat("/sjf_data.csv"), 1);

        // Execute the A2C Scheduler
        A2C_Scheduler.main(args);
        save_outputs(A2C_Scheduler.getList(), A2C_Scheduler.getExecMatrix(), A2C_Scheduler.getCommMatrix(), hyperparameters.concat("/a2c_data.csv"), 2);

        // Execute the Q-Learning Scheduler
        QLearningScheduler.main(args);
        save_outputs(QLearningScheduler.getList(), QLearningScheduler.getExecMatrix(), QLearningScheduler.getCommMatrix(), hyperparameters.concat("/qlearning_data.csv"), 3);

        // Execute the Double-Q-Learning Scheduler
        DoubleQLearningScheduler.main(args);
        save_outputs(DoubleQLearningScheduler.getList(), DoubleQLearningScheduler.getExecMatrix(), DoubleQLearningScheduler.getCommMatrix(), hyperparameters.concat("/double_qlearning_data.csv"), 4);


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
    public static void save_outputs(List<Cloudlet> list, double[][] execMatrix, double[][] commMatrix, String csvFilePath, int type) {
        int size = list.size();
        Cloudlet cloudlet;


        // Create a CSV writer
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            String[] header = {"Cloudlet ID", "Status", "Data center ID", "VM ID", "Time",
                    "Start Time", "Finish Time", "Waiting", "Completion", "Cost", "CPU Utilization"};
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
                int dcId = cloudlet.getVmId() % Constants.NO_OF_DATA_CENTERS;
                String cpuUtilization = dft.format(cloudlet.getActualCPUTime() / execMatrix[i][dcId]);

                String[] row = {cloudletId, status, dataCenterId, vmId, time, startTime, finishTime, waitingTime,
                        completionTime, cost, cpuUtilization};
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


            if (type == 0) {
                new TargetEntry("fcfs", Constants.NO_OF_DATA_CENTERS, Constants.NO_OF_TASKS, makespan, avgCompletionTime, avgCost, avgWaitingTime);
            } else if (type == 1) {
                new TargetEntry("sjf", Constants.NO_OF_DATA_CENTERS, Constants.NO_OF_TASKS, makespan, avgCompletionTime, avgCost, avgWaitingTime);
            } else if (type == 2) {
                new TargetEntry("a2c", Constants.NO_OF_DATA_CENTERS, Constants.NO_OF_TASKS, makespan, avgCompletionTime, avgCost, avgWaitingTime);
            } else if (type == 3) {
                new TargetEntry("qlearning", Constants.NO_OF_DATA_CENTERS, Constants.NO_OF_TASKS, makespan, avgCompletionTime, avgCost, avgWaitingTime);
            } else if (type == 4) {
                new TargetEntry("double", Constants.NO_OF_DATA_CENTERS, Constants.NO_OF_TASKS, makespan, avgCompletionTime, avgCost, avgWaitingTime);
            }


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


