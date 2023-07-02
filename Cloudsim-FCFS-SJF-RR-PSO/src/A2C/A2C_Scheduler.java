package A2C;

import org.cloudbus.cloudsim.Log;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;



// Implementing A2C Scheduler: Advantage Actor-Critic
// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 1st
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad

public class A2C_Scheduler {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private static double[][] commMatrix;
    private static double[][] execMatrix;

    private static List<Vm> createVM(int userId, int vms) {
        LinkedList<Vm> list = new LinkedList<>();

        long size = 10000; // image size (MB)
        int ram = 512; // VM memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; // number of CPUs
        String vmm = "Xen"; // VMM name

        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }


    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        LinkedList<Cloudlet> list = new LinkedList<>();

        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

//        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (Math.random() * Constants.NO_OF_DATA_CENTERS);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            Cloudlet cl = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cl.setUserId(userId);
            cl.setVmId(dcId + 2);
            list.add(cl);
        }
        return list;
    }



    public static void main(String[] args) {
        Log.printLine("Starting A2C Scheduler...");

        new GenerateMatrices();
        execMatrix = GenerateMatrices.getExecMatrix();
        commMatrix = GenerateMatrices.getCommMatrix();

        try {
            int num_user = 1; // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false; // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }
            A2CDataCenterBroker broker = new A2CDataCenterBroker("Broker");

            cloudletList = createCloudlet(broker.getId(), Constants.NO_OF_TASKS, 0);
            vmList = createVM(broker.getId(), Constants.NO_OF_DATA_CENTERS);

            broker.submitCloudletList(cloudletList);
            broker.submitVmList(vmList);

            CloudSim.startSimulation();

            List<Cloudlet> resultList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            printCloudletList(resultList);

            Log.printLine("A2C Scheduler finished!");
        } catch (Exception e) {
            Log.printLine("Unwanted errors happen");
        }
    }

//    private static void printCloudletList(List<Cloudlet> list) {
//        int size = list.size();
//        Cloudlet cloudlet;
//
//        String indent = "    ";
//        Log.printLine();
////        Log.printLine(size);
//        Log.printLine("========== OUTPUT ==========");
//        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
//                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");
//
//        DecimalFormat dft = new DecimalFormat("###.##");
//
//        for (int i = 0; i < size; i++) {
//            cloudlet = list.get(i);
//            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
//
//            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
//                Log.print("SUCCESS");
//
//                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
//                        indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime()) +
//                        indent + indent + indent + dft.format(cloudlet.getFinishTime()));
//            }
//            else {
//                Log.print("Failure");
//            }
//        }
//    }
    private static void printCloudletList(List<Cloudlet> list) {
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
    }


}

