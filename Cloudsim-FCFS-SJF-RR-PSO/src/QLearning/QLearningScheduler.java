package QLearning;

// Implementing QL Scheduler: Q-Learning
// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 7th
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class QLearningScheduler {

    public static boolean doCheckpointing = true;
    public static int faultyVMPercentage = 10;


    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static PowerDatacenter[] datacenter;
    private static double[][] commMatrix;
    private static double[][] execMatrix;
    private static List<Cloudlet> resultList;


    /*
     Function Name:
        createVM
     Functionality:
        create virtual machine with the given userid and preferred general parameters
     input(s):
        int userId: the userid related to this VM (this is not important that much!)
        int vms: number of virtual machines to be created
     output(s):
        List<Vm> list: list of all  created VMs
    */
    private static List<Vm> createVM(int userId, int vms) {
        // Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<>();

        // VM Parameters
        long size = 10000; // image size (MB)
        int ram = 512; // vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; // number of cpus
        String vmm = "Xen"; // VMM name

        // Create VMs
        Vm[] vm = new Vm[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new Vm(datacenter[i].getId(), userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }

        return list;
    }


    /*
     Function Name:
        createCloudlet
     Functionality:
        create cloudlets (tasks) with the given userid and preferred general parameters
     input(s):
        int userId: the userid related to this VM (this is not important that much!)
        int cloudlets: number of cloudlets (tasks) to be created
        int idShift: Not important for this project!
     output(s):
        List<Vm> list: list of all  created VMs
    */
    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<>();

        // Cloudlet parameters
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (Math.random() * Constants.NO_OF_DATA_CENTERS);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // Setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            cloudlet[i].setVmId(dcId + 2);
            list.add(cloudlet[i]);
        }
        return enableFaults(list);
    }

    private static List<Cloudlet> enableFaults(List<Cloudlet> list) {
        if (doCheckpointing){
            int len = (faultyVMPercentage * vmList.size() / 100);
            for (int i=0; i<len; i++){

                int cnt = 0;
                int vmID = vmList.get(i).getId();

                for (Cloudlet c:list){
                    if(c.getVmId() == vmID){
                        cnt++;
                    }
                }

                cnt /= 2;

                for (Cloudlet c:list){
                    if((c.getVmId() == vmID) && cnt>=0){
                        c.setCloudletLength(c.getCloudletLength() * 2);
                        cnt --;
                    }
                }
            }
        }
        return list;
    }


    /*
     Function Name:
        main
     Functionality:
        run Q-Learning Scheduler
     input(s):
        String[] args: Not important.
     output(s):
        void: it doesn't return anything, it rather schedules the tasks based on the policy
    */
    public static double main(String[] args) {
        Log.printLine("Starting Q-Learning Scheduler...");

        execMatrix = GenerateMatrices.getExecMatrix();
        commMatrix = GenerateMatrices.getCommMatrix();

        double start_time = 0.0;
        double end_time = 0.0;

        try {
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            datacenter = new PowerDatacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }
            // getPower()


            // Third step: Create Broker
            QLearningDatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            // Fourth step: Create VMs and Cloudlets and send them to broker
            vmList = createVM(brokerId, Constants.NO_OF_DATA_CENTERS);
            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            // Fifth step: Starts the simulation
            start_time = CloudSim.clock();
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();

            end_time = CloudSim.clock();
            CloudSim.stopSimulation();

            QLearningScheduler.resultList = newList;

            Log.printLine(QLearningScheduler.class.getName() + " finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }

        return end_time - start_time;
    }



    /*
     Function Name:
        createBroker
     Functionality:
        create DataCenter Broker related to this scheduler
     input(s):
        String name: name of the broker (arbitrary; it is not important that much)
     output(s):
        QLearningDatacenterBroker: a datacenter broker object
    */
    private static QLearningDatacenterBroker createBroker(String name) throws Exception {
        return new QLearningDatacenterBroker(name);
    }

    public static List<Cloudlet> getList() {
        return resultList;
    }

    public static List<Cloudlet> getCloudletList(){
        return cloudletList;
    }

    public static double[][] getExecMatrix() {
        return execMatrix;
    }

    public static double[][] getCommMatrix() {
        return commMatrix;
    }

    public static PowerDatacenter[] getDatacenter(){
        return datacenter;
    }

}
