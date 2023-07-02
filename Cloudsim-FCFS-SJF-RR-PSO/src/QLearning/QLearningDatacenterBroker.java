package QLearning;


import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.ArrayList;
import java.util.List;
import utils.Constants;
import utils.DatacenterCreator;
import utils.GenerateMatrices;



class QLearningDatacenterBroker extends DatacenterBroker {

    private static final double ALPHA = 0.1; // Learning rate
    private static final double GAMMA = 0.9; // Discount factor
    private static final double EPSILON = 0.9; // Exploration rate
    private static final int NUM_EPISODES = 1000; // Number of training episodes
    private static final int MAX_STEPS = 100; // Maximum number of steps per episode

    private double[][] qTable;
    private int currentState;
    private int totalStates;

    public QLearningDatacenterBroker(String name) throws Exception {
        super(name);
        totalStates = Constants.NO_OF_TASKS;
        qTable = new double[totalStates][totalStates];
        initializeQTable();
    }

    private void initializeQTable() {
        for (int i = 0; i < totalStates; i++) {
            for (int j = 0; j < totalStates; j++) {
                qTable[i][j] = 0;
            }
        }
    }

    private int selectAction() {
        int action;
        if (Math.random() < EPSILON) {
            action = getBestAction(currentState);
        } else {
            action = getRandomAction();
        }
        return action;
    }

    private int getBestAction(int state) {
        if (state == -1){
            return 0;
        }
        int bestAction = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int action = 0; action < totalStates; action++) {
            double value = qTable[state][action];
            if (value > bestValue) {
                bestValue = value;
                bestAction = action;
            }
        }
        return bestAction;
    }

    private int getRandomAction() {
        return (int) (Math.random() * totalStates);
    }

    private void updateQTable(int state, int action, double reward, int nextState) {
        if (nextState == -1){
            return;
        }
        double oldValue = qTable[state][action];
        double maxNextStateValue = getMaxValue(nextState);
        double newValue = (1 - ALPHA) * oldValue + ALPHA * (reward + GAMMA * maxNextStateValue);
        qTable[state][action] = newValue;
    }

    private double getMaxValue(int state) {
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int action = 0; action < totalStates; action++) {
            double value = qTable[state][action];
            if (value > maxValue) {
                maxValue = value;
            }
        }
        return maxValue;
    }

//    protected void cloudletExecution(Cloudlet cloudlet) {
//        if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
//            Log.printLine(CloudSim.clock() + ": " + getName() + ": All cloudlets executed. Finishing...");
//            CloudSim.terminateSimulation();
//        } else {
//            if (cloudlet.getCloudletStatus() == Cloudlet.CREATED) {
//                cloudlet.setCloudletStatus(Cloudlet.INEXEC);
//                cloudlet.setExecStartTime(CloudSim.clock());
//                updateAllocatedMips(cloudlet.getVm(), cloudlet.getVm().getMips() - cloudlet.getCloudletLength());
//            } else if (cloudlet.getRemainingCloudletLength() == 0) {
//                cloudlet.setCloudletStatus(Cloudlet.SUCCESS);
//                cloudlet.setExecFinishTime(CloudSim.clock());
//                cloudlet.getVm().setMips(cloudlet.getVm().getMips() + cloudlet.getCloudletLength());
//                cloudletsSubmitted++;
//                updateQTable(currentState, cloudlet.getCloudletId(), cloudlet.getExecFinishTime(), -1);
//            }
//        }
//    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.CLOUDLET_SUBMIT:
                processCloudletSubmit(ev);
                break;
            case CloudSimTags.CLOUDLET_RETURN:
                processCloudletReturn(ev);
                break;
            default:
                super.processEvent(ev);
                break;
        }
    }

    private void processCloudletSubmit(SimEvent ev) {
//        Cloudlet cloudlet = (Cloudlet) ev.getData();
//        getCloudletList().add(cloudlet);
//        cloudlet.setCloudletStatus(Cloudlet.INEXEC);
//        cloudlet.setVmId(cloudlet.getVmId());
//        cloudlet.setExecStartTime(CloudSim.clock());
//        updateAllocatedMips(cloudlet.getVmId(), cloudlet.getVm().getMips() - cloudlet.getCloudletLength());
//        int action = selectAction();
//        updateQTable(currentState, action, CloudSim.clock(), cloudlet.getCloudletId());
//        currentState = cloudlet.getCloudletId();
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletList().add(cloudlet);
        try {
            cloudlet.setCloudletStatus(Cloudlet.INEXEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int vmId = cloudlet.getVmId();
        Vm vm = getVmList().stream().filter(v -> v.getId() == vmId).findFirst().orElse(null);
        if (vm != null) {
            cloudlet.setVmId(vm.getId());
            cloudlet.setExecStartTime(CloudSim.clock());
            updateAllocatedMips(vm.getId(), vm.getMips() - cloudlet.getCloudletLength());
            int action = selectAction();
            updateQTable(currentState, action, CloudSim.clock(), cloudlet.getCloudletId());
            currentState = cloudlet.getCloudletId();
        } else {
            Log.printLine(CloudSim.clock() + ": " + getName() + ": No available VMs. Postponing cloudlet " + cloudlet.getCloudletId());
        }
    }

//    private void updateAllocatedMips(int vmId, double mipsToUpdate) {
//        Vm vm = getVmList().stream().filter(v -> v.getId() == vmId).findFirst().orElse(null);
//        if (vm != null) {
//            double currentMips = vm.getMips();
//            double updatedMips = currentMips - mipsToUpdate;
//            vm.setMips(updatedMips);
//        }
//    }
//    private void updateAllocatedMips(int vmId, double mipsToUpdate) {
//        Vm vm = getVmList().stream().filter(v -> v.getId() == vmId).findFirst().orElse(null);
//        if (vm != null) {
//            double currentMips = vm.getCurrentRequestedTotalMips();
//            double updatedMips = currentMips - mipsToUpdate;
//            double[] mipsArray = { updatedMips };
//            CloudSim.getInstance().getVmAllocationPolicy().deallocatePesForVm(vm);
//            CloudSim.getInstance().getVmAllocationPolicy().allocatePesForVm(vm, mipsArray);
//        }
//    }
    private void updateAllocatedMips(int vmId, double mipsToUpdate) {
        CustomVm vm = (CustomVm) getVmList().stream().filter(v -> v.getId() == vmId).findFirst().orElse(null);
        if (vm != null) {
            double currentMips = vm.getCurrentRequestedTotalMips();
            double updatedMips = currentMips - mipsToUpdate;
            vm.updateMips(updatedMips);
        }
    }


    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        try {
            cloudlet.setCloudletStatus(Cloudlet.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cloudletsSubmitted++;
        int nextState;
        if (cloudletsSubmitted < totalStates) {
            nextState = cloudletsSubmitted;
        } else {
            nextState = -1;
        }
        int action = selectAction();
        updateQTable(currentState, action, CloudSim.clock(), nextState);
        currentState = nextState;
//        cloudlet.setExecFinishTime(CloudSim.clock());
//        cloudlet.getVm().setMips(cloudlet.getVm().getMips() + cloudlet.getCloudletLength());
//        cloudletsSubmitted++;
//        int nextState;
//        if (cloudletsSubmitted < totalStates) {
//            nextState = cloudletsSubmitted;
//        } else {
//            nextState = -1;
//        }
//        int action = selectAction();
//        updateQTable(currentState, action, cloudlet.getExecFinishTime(), nextState);
//        currentState = nextState;
    }

    @Override
    public void startEntity() {
        Log.printLine(getName() + " is starting...");
        schedule(getId(), Constants.NO_OF_DATA_CENTERS, CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
    }

    @Override
    public void shutdownEntity() {
        Log.printLine(getName() + " is shutting down...");
    }

//    @Override
    public void allocateVmForCloudlet(Cloudlet cloudlet, List<CustomVm> vmList) throws Exception {
        if (vmList.size() == 0) {
            Log.printLine(CloudSim.clock() + ": " + getName() + ": No available VMs. Postponing cloudlet " + cloudlet.getCloudletId());
        } else {
            CustomVm vm = vmList.get(0);
            cloudlet.setVmId(vm.getId());
            getCloudletList().add(cloudlet);
            cloudlet.setCloudletStatus(Cloudlet.INEXEC);
            cloudlet.setVmId(vm.getId());
            cloudlet.setExecStartTime(CloudSim.clock());
            updateAllocatedMips(vm.getId(), vm.getMips() - cloudlet.getCloudletLength());
            int action = selectAction();
            updateQTable(currentState, action, CloudSim.clock(), cloudlet.getCloudletId());
            currentState = cloudlet.getCloudletId();
        }
    }
}