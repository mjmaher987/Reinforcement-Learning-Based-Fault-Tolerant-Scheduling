package A2C;


import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;




// Implementing A2C Scheduler: Advantage Actor-Critic
// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 7th
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad

public class A2C_Scheduler {
    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
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
    public static void main(String[] args) {
        Log.printLine("Starting A2C Scheduler...");


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
            A2CDataCenterBroker broker = createBroker("Broker");

            cloudletList = createCloudlet(broker.getId(), Constants.NO_OF_TASKS, 0);
            vmList = createVM(broker.getId(), Constants.NO_OF_DATA_CENTERS);

            broker.submitCloudletList(cloudletList);
            broker.submitVmList(vmList);

            CloudSim.startSimulation();

            List<Cloudlet> resultList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();


            A2C_Scheduler.resultList = resultList;
            Log.printLine("A2C Scheduler finished!");
        } catch (Exception e) {
            Log.printLine("Unwanted errors happen");
        }
    }


    /*
      Function Name:
         createBroker
      Functionality:
         create DataCenter Broker related to this scheduler
      input(s):
         String name: name of the broker (arbitrary; it is not important that much)
      output(s):
         A2CDataCenterBroker: a datacenter broker object
     */
    private static A2CDataCenterBroker createBroker(String name) throws Exception {
        return new A2CDataCenterBroker(name, 0.1, 0.1, 0.1);
    }

    public static List<Cloudlet> getList() {
        return A2C_Scheduler.resultList;
    }

    public static double[][] getExecMatrix() {
        return A2C_Scheduler.execMatrix;
    }

    public static double[][] getCommMatrix() {
        return A2C_Scheduler.commMatrix;
    }
}

