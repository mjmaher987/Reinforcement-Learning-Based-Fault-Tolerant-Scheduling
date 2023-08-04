package DoubleQLearning;



// DoubleQLearningScheduler.java

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DoubleQLearningScheduler {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private static double[][] commMatrix;
    private static double[][] execMatrix;
    private static List<Cloudlet> resultList;

    public static void main(String[] args) {
        Log.printLine("Starting Double Q-Learning Scheduler...");

        execMatrix = GenerateMatrices.getExecMatrix();
        commMatrix = GenerateMatrices.getCommMatrix();

        try {
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;

            CloudSim.init(num_user, calendar, trace_flag);

            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }

            DoubleQLearningDatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            vmList = createVM(brokerId, Constants.NO_OF_DATA_CENTERS);
            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();

            resultList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            Log.printLine(DoubleQLearningScheduler.class.getName() + " finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static List<Vm> createVM(int userId, int vms) {
        LinkedList<Vm> list = new LinkedList<>();

        long size = 10000;
        int ram = 512;
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1;
        String vmm = "Xen";

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

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (Math.random() * Constants.NO_OF_DATA_CENTERS);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet[i].setUserId(userId);
            cloudlet[i].setVmId(dcId + 2);
            list.add(cloudlet[i]);
        }
        return list;
    }

    private static DoubleQLearningDatacenterBroker createBroker(String name) throws Exception {
        return new DoubleQLearningDatacenterBroker(name);
    }

    public static List<Cloudlet> getList() {
        return resultList;
    }

    public static double[][] getExecMatrix() {
        return execMatrix;
    }

    public static double[][] getCommMatrix() {
        return commMatrix;
    }
}