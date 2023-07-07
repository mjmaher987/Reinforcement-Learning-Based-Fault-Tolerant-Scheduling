package A2C;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import utils.Constants;

import java.util.*;


// Implementing A2C DataCenterBroker: Advantage Actor-Critic
// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 7th
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad


public class A2CDataCenterBroker extends DatacenterBroker {

    // Class attributes
    private List<Vm> vmList;
    // Note: Actor and Critic Model can be changed For example, they can be replaced by neural networks
    private Map<String, Double> actorModel;
    private Map<String, Double> criticModel;
    private double actorLearningRate;
    private double criticLearningRate;
    private double discountFactor;
    private Random random;

    /*
      Constructor of the class
    */
    public A2CDataCenterBroker(String name, double actorLearningRate, double criticLearningRate, double discountFactor) throws Exception {
        super(name);
        this.vmList = new ArrayList<>();
        this.actorModel = new HashMap<>();
        this.criticModel = new HashMap<>();
        this.actorLearningRate = actorLearningRate;
        this.criticLearningRate = criticLearningRate;
        this.discountFactor = discountFactor;
        this.random = new Random();
    }

    /*
     Function Name:
        processCloudletReturn
     Functionality:
        Submit the cloudlet for execution on the selected VM
     input(s):
        SimEvent ev:
     output(s):
        void:it does not have output, rather it has to submit cloudlets (tasks)
    */
    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId() + " received");
        cloudletsSubmitted++;

        int vmId = cloudlet.getVmId();
        Vm vm = getVmList().stream().filter(v -> v.getId() == vmId).findFirst().orElse(null);
        // If the cloudlet (task) is executed, we should not enter it again)
        if (vm != null && !cloudlet.isFinished()) {
            double reward = calculateReward(cloudlet);
            double[] state = calculateState(vm, cloudlet);

            // Update the actor and critic models
            updateModels(state, reward);

            // Select an action (using the actor model)
            int selectedVmId = selectAction(vm, state);
            cloudlet.setVmId(selectedVmId);

            // Submit the cloudlet for execution on the selected VM
            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
        }
    }



    /*
     Function Name:
        updateModels
     Functionality:
        Update the actor and critic models using the A2C algorithm
     input(s):
        double[] state: current state
        double reward: reward taken from doing the specific actions
     output(s):
        void: it does not have output, rather it has to submit cloudlets (tasks)
    */
    private void updateModels(double[] state, double reward) {

        // 1. Estimate the value of the current state using the critic model
        double currentStateValue = estimateStateValue(state);

        // 2. Estimate the value of the next state using the critic model (assuming the next state is terminal)
        double nextStateValue = 0.0;

        // Find the average value of the next possible states
        for (int action = 0; action < Constants.NO_OF_DATA_CENTERS; action++) {
            double[] currentState = new double[state.length];
            System.arraycopy(state, 0, currentState, 0, state.length);
            Vm selectedVm = getVmList().get(action);
            currentState[action] = selectedVm.getCurrentRequestedTotalMips();
            nextStateValue += estimateStateValue(currentState);
        }
        nextStateValue /= Constants.NO_OF_DATA_CENTERS;

        // 3. Compute the TD error
        double tdError = reward + discountFactor * nextStateValue - currentStateValue;

        // 4. Update the critic model
        updateCriticModel(state, currentStateValue, tdError);

        // 5. Update the actor model
        updateActorModel(state, tdError);
    }



    /*
     Function Name:
        estimateStateValue
     Functionality:
        Estimate the value of the current state using the critic model
     input(s):
        double[] state: input state
     output(s):
        double: estimated value of the input state
    */
    private double estimateStateValue(double[] state) {
        StringBuilder stateKey = new StringBuilder();
        for (double value : state) {
            stateKey.append(value).append(":");
        }
        String key = stateKey.toString();
        return criticModel.getOrDefault(key, 0.0);
    }



    /*
     Function Name:
        updateCriticModel
     Functionality:
        Update the critic model based on the TD error
     input(s):
        double[] state: input state
        double currentStateValue: value of the input state
        double tdError: the td-error which we are going to update the critic model based on ti
     output(s):
        void: it doesn't update anything, rather it updates the critical model
    */
    private void updateCriticModel(double[] state, double currentStateValue, double tdError) {
        StringBuilder stateKey = new StringBuilder();
        for (double value : state) {
            stateKey.append(value).append(":");
        }
        String key = stateKey.toString();

        double updatedValue = currentStateValue + criticLearningRate * tdError;
        criticModel.put(key, updatedValue);
    }


    /*
     Function Name:
        updateActorModel
     Functionality:
        Update the actor model based on the TD error
     input(s):
        double[] state: input state
        double tdError: the td-error which we are going to update the critic model based on ti
     output(s):
        void: it doesn't update anything, rather it updates the actor model
    */
    private void updateActorModel(double[] state, double tdError) {
        StringBuilder stateKey = new StringBuilder();
        for (double value : state) {
            stateKey.append(value).append(":");
        }
        String key = stateKey.toString();

        double actionProbability = actorModel.getOrDefault(key, 0.0);
        double updatedProbability = actionProbability + actorLearningRate * tdError;
        actorModel.put(key, updatedProbability);
    }


    /*
     Function Name:
        selectAction
     Functionality:
        Select an action using the actor model
     input(s):
        Vm vm: the current virtual machine
        double[] state: input state
     output(s):
        int: the action that should be taken
    */
    private int selectAction(Vm vm, double[] state) {
        StringBuilder stateKey = new StringBuilder();
        for (double value : state) {
            stateKey.append(value).append(":");
        }
        String key = stateKey.toString();

        double actionProbability = actorModel.getOrDefault(key, 0.0);
        double randomValue = random.nextDouble();

        if (randomValue <= actionProbability) {
            return vm.getId();
        } else {
            List<Vm> otherVms = new ArrayList<>(vmList);
            otherVms.remove(vm);
            if (otherVms.isEmpty()) {
                return vm.getId();
            } else {
                int randomIndex = random.nextInt(otherVms.size());
                return otherVms.get(randomIndex).getId();
            }
        }
    }


    /*
     Function Name:
        calculateReward
     Functionality:
        Calculate the reward for the given cloudlet
     input(s):
        Cloudlet cloudlet: input task
     output(s):
        double: reward of the cloudlet
    */
    private double calculateReward(Cloudlet cloudlet) {
        double executionTime = cloudlet.getFinishTime() - cloudlet.getExecStartTime();
        return 1.0 / executionTime; // Reward is inversely proportional to the execution time // It can vary
    }


    /*
     Function Name:
        calculateState
     Functionality:
        Calculate the state representation based on the VM and cloudlet
     input(s):
        Cloudlet cloudlet: input task
     output(s):
        double[] state: it is the calculated state
    */
    private double[] calculateState(Vm vm, Cloudlet cloudlet) {
        // assume the state is a vector containing (VM's MIPS) and (cloudlet's length) and (file size)
        // It has 3 features (It can have more or less features)
        double[] state = new double[3];
        state[0] = vm.getMips();
        state[1] = cloudlet.getCloudletLength();
        state[2] = cloudlet.getCloudletFileSize();
        return state;
    }
}