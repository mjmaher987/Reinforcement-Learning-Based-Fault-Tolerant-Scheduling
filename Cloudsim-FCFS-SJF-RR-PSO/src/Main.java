// import necessary packages

import A2C.A2C_Scheduler;
import FCFS.FCFS_Scheduler;
import QLearning.QLearningScheduler;
import SJF.SJF_Scheduler;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;


import utils.Constants;


// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 1st
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad

public class Main {
    public static void main(String[] args){
        // Running 4 schedulers separately
        FCFS_Scheduler.main(args);
        printCloudletList(FCFS_Scheduler.getList(), FCFS_Scheduler.getExecMatrix(), FCFS_Scheduler.getCommMatrix());


        SJF_Scheduler.main(args);
        printCloudletList(SJF_Scheduler.getList(), SJF_Scheduler.getExecMatrix(), SJF_Scheduler.getCommMatrix());

        A2C_Scheduler.main(args);
        printCloudletList(A2C_Scheduler.getList(), A2C_Scheduler.getExecMatrix(), A2C_Scheduler.getCommMatrix());

        QLearningScheduler.main(args);
        printCloudletList(QLearningScheduler.getList(), QLearningScheduler.getExecMatrix(), QLearningScheduler.getCommMatrix());

    }

    public static void printCloudletList(List<Cloudlet> list, double[][] execMatrix, double[][] commMatrix) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" +
                indent + "Data center ID" +
                indent + "VM ID" +
                indent + indent + "Time" +
                indent + indent+ "Start Time" +
                indent + indent+ indent+"Finish Time"+
                indent + "Waiting"+
                indent + "Completion"+
                indent + "Cost");

        //HERE:
        double totalCompletionTime=0;
        double totalCost=0;
        double totalWaitingTime=0;
        //-------------------------

        DecimalFormat dft = new DecimalFormat("####.##");
        dft.setMinimumIntegerDigits(2);
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + dft.format(cloudlet.getCloudletId()) + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                //HERE:
                double completionTime= cloudlet.getActualCPUTime()+ cloudlet.getWaitingTime();
                double cost= cloudlet.getCostPerSec()* cloudlet.getActualCPUTime() ;

                //Note: the execution time for a task is cloudlet.getActualCPUTime()
                //----------------------
                Log.printLine(indent + indent + dft.format(cloudlet.getResourceId()) +
                        indent + indent + indent + dft.format(cloudlet.getVmId()) +
                        indent + indent +indent + dft.format(cloudlet.getActualCPUTime()) +
                        indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent  +indent+indent+ dft.format(cloudlet.getFinishTime())+
                        indent + indent  +indent+ dft.format(cloudlet.getWaitingTime() )+
                        indent + indent  + dft.format(completionTime )+
                        indent + indent + dft.format(cost));
                //HERE:
                totalCompletionTime += completionTime;
                totalCost += cost;
                totalWaitingTime+=cloudlet.getWaitingTime();
                //-----------------------------------------
            }
        }
        double makespan = calcMakespan(list, execMatrix, commMatrix);
        Log.printLine("Makespan using QLearning: " + makespan);
        //Added:
        Log.printLine("Total Completion Time: " + totalCompletionTime +" Avg Completion Time: "+ (totalCompletionTime/size));
        Log.printLine("Total Cost : " + totalCost+ " Avg cost: "+ (totalCost/size));
        Log.printLine("Avg Waiting Time: "+ (totalWaitingTime/size));
    }
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
