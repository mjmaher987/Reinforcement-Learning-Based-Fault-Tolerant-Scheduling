package A2C;

import org.cloudbus.cloudsim.*;
//import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.CloudSimTags;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 1st
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad


public class A2CDataCenterBroker extends DatacenterBroker {

    public A2CDataCenterBroker(String name) throws Exception {
        super(name);
    }

//    @Override
//    protected void processCloudletReturn(SimEvent ev) {
//        List<Cloudlet> finishedCloudlets = CloudSim.getCloudResourceList().get(0).getCloudletFinishedList();
//
//        for (Cloudlet cloudlet : finishedCloudlets) {
//            int cloudletId = cloudlet.getCloudletId();
//            int vmId = cloudlet.getVmId();
//
//            Vm vm = getVmList().stream().filter(v -> v.getId() == vmId).findFirst().orElse(null);
//            if (vm != null) {
//                double reward = calculateReward(cloudlet);
//                double[] state = calculateState(vm, cloudlet);
//
//                // Update the actor and critic networks using the A2C algorithm
//                updateNetworks(state, reward);
//                // Your code for updating the networks goes here
//
//                // Select an action using the actor network
//                int selectedVmId = selectAction(vm, state);
//                // Your code for selecting an action goes here
//
//                // Assign the selected action to the cloudlet's VM
//                cloudlet.setVmId(selectedVmId);
//
//                // Submit the cloudlet for execution on the assigned VM
//                CloudSim.send(getVmsToDatacentersMap().get(vm.getId()), cloudlet.getCloudletId(), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
//            }
//        }
//    }

    @Override
    protected void processCloudletReturn(SimEvent ev) {
        Cloudlet cloudlet = (Cloudlet) ev.getData();
        getCloudletReceivedList().add(cloudlet);
        Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId()
                + " received");
        cloudletsSubmitted--;
        int vmId = cloudlet.getVmId();

        Vm vm = getVmList().stream().filter(v -> v.getId() == vmId).findFirst().orElse(null);
        if (vm != null) {
            double reward = calculateReward(cloudlet);
            double[] state = calculateState(vm, cloudlet);

            // Update the actor and critic networks using the A2C algorithm
            updateNetworks(state, reward);
            // Your code for updating the networks goes here

            // Select an action using the actor network
            int selectedVmId = selectAction(vm, state);
            // Your code for selecting an action goes here

            // Assign the selected action to the cloudlet's VM
            cloudlet.setVmId(selectedVmId);
//            scheduleTaskstoVms();
//            cloudletExecution(cloudlet);

            // Submit the cloudlet for execution on the assigned VM
//            CloudSim.send(getVmsToDatacentersMap().get(vm.getId()), cloudlet.getCloudletId(), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
        }
    }

    public void scheduleTaskstoVms() {

        ArrayList<Cloudlet> clist = new ArrayList<Cloudlet>();

        for (Cloudlet cloudlet : getCloudletSubmittedList()) {
            clist.add(cloudlet);
        }

        setCloudletReceivedList(clist);
    }



    private int selectAction(Vm vm, double[] state) {
        // Select an action using the actor network
        // Your code for selecting an action goes here
//        int numActions = 4; // Get the number of available actions
        double[] actionProbabilities = getActionProbabilities(state); // Get the action probabilities

        // Select an action index based on the action probabilities
        int selectedActionIndex = selectActionIndexBasedOnProbabilities(actionProbabilities);

        // Map the selected action index to the corresponding VM ID
//        List<Vm> vmList = getVmList();
//        int selectedVmId = vmList.get(selectedActionIndex).getId();

        return selectedActionIndex;
    }

    private void updateNetworks(double[] state, double reward) {
        // Update the actor and critic networks using the A2C algorithm
        // TODO: Implement the A2C network update algorithm

        // 1. Calculate the target value for the critic network
        double discountFactor = 0.5; // it can vary
        double targetValue = reward + discountFactor * estimateNextStateValue(state);

        // 2. Compute the advantage value
        double advantage = targetValue - estimateStateValue(state);

        // 3. Update the critic network weights
        updateCriticWeights(state, targetValue);

        // 4. Update the actor network weights
        updateActorWeights(state, advantage);
    }

    private double estimateStateValue(double[] state) {
        // Estimate the value of the current state using the critic network
        // TODO: Implement the critic network to estimate the state value
        double stateValue = 0.0; // Replace this with your actual implementation
        return stateValue;
    }

    private double estimateNextStateValue(double[] state) {
        // Estimate the value of the next state using the critic network
        // TODO: Implement the critic network to estimate the state value
        double nextStateValue = 0.0; // Replace this with your actual implementation
        return nextStateValue;
    }

    private void updateCriticWeights(double[] state, double targetValue) {
        // Update the weights of the critic network based on the advantage value and critic loss gradient
        // TODO: Implement the critic network weight update algorithm
        double currentStateValue = estimateStateValue(state);

        // Step 2: Calculate the critic loss gradient
        double criticLossGradient = targetValue - currentStateValue;

        // Step 3: Update the critic network weights based on the gradient
//        updateWeights(state, criticLossGradient);
        double learningRate = 0.1;
        for (int i = 0; i < state.length; i++) {
            state[i] += learningRate * criticLossGradient * state[i];
        }

    }

    private void updateActorWeights(double[] state, double advantage) {
        // Update the weights of the actor network based on the advantage value and actor loss gradient
        // TODO: Implement the actor network weight update algorithm
        double[] actionProbabilities = this.getActionProbabilities(state);

        // Step 2: Calculate the actor loss gradient
        double actorLossGradient = calculateActorLossGradient(state, actionProbabilities, advantage);

        // Step 3: Update the actor network weights based on the gradient
//        actorNetwork.updateWeights(state, actorLossGradient);
        double learningRate = 0.1;
        for (int i = 0; i < state.length; i++) {
            state[i] += learningRate * actorLossGradient * state[i];
        }
    }

    private double[] getActionProbabilities(double[] state) {
        // Here's a placeholder implementation that returns a uniform distribution
        int numActions = 4; // Get the number of available actions
        double[] actionProbabilities = new double[numActions];
        double probability = 1.0 / numActions;
        Arrays.fill(actionProbabilities, probability);

        return actionProbabilities;
    }


    private double calculateActorLossGradient(double[] state, double[] actionProbabilities, double advantage) {
        // Calculate the actor loss gradient based on the state, action probabilities, and advantage
        // TODO: Implement the actor loss gradient calculation

        // Here's an example implementation using the REINFORCE algorithm

        // Get the action index corresponding to the selected action
        int selectedActionIndex = selectActionIndexBasedOnProbabilities(actionProbabilities);

        // Calculate the actor loss gradient for the selected action
        double actorLossGradient = advantage * (-1.0 / actionProbabilities[selectedActionIndex]);

        return actorLossGradient;
    }

    private int selectActionIndexBasedOnProbabilities(double[] actionProbabilities) {
        // Select an action index based on the action probabilities
        // TODO: Implement a suitable method to select an action index based on the probabilities

        // Here's an example implementation using a simple random selection method

        double randomValue = Math.random();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < actionProbabilities.length; i++) {
            cumulativeProbability += actionProbabilities[i];

            if (randomValue <= cumulativeProbability) {
                return i;
            }
        }

        // If no action is selected, return the index of the last action
        return actionProbabilities.length - 1;
    }


//    private double calculateReward(Cloudlet cloudlet) {
//        // Calculate the reward for the given cloudlet based on its completion time, cost, etc.
//        double completionTime = cloudlet.getActualCpuTime(); // Get the completion time of the cloudlet
//        double cost = cloudlet.getCostPerSec() * completionTime; // Calculate the cost based on the completion time and cost per second
//        // Your code for calculating the reward goes here
//        // You can incorporate additional factors such as quality of service, penalty for delay, etc.
//
//        double reward = 0.0; // Replace this with your actual implementation
//        return reward;
//    }
    private double calculateReward(Cloudlet cloudlet) {
        long length = cloudlet.getCloudletLength(); // Get the length of the cloudlet
        double executionTime = cloudlet.getFinishTime() - cloudlet.getExecStartTime(); // Calculate the execution time

        // Your code for calculating the reward goes here
        // You can incorporate additional factors such as quality of service, penalty for delay, etc.

        double reward = length / executionTime; // Example calculation: Reward is proportional to the cloudlet's length-to-execution-time ratio
        return reward;
    }

    private double[] calculateState(Vm vm, Cloudlet cloudlet) {
        // Define the number of features for the state representation
        int numFeatures = 4;

        // Create an array to store the state values
        double[] state = new double[numFeatures];

        // Feature 1: VM's MIPS (Million Instructions Per Second) capacity
        double vmMips = vm.getMips(); // Get the VM's MIPS capacity
        state[0] = vmMips;

        // Feature 2: Cloudlet's length (in MI)
        long cloudletLength = cloudlet.getCloudletLength(); // Get the cloudlet's length
        state[1] = cloudletLength;

        // Feature 3: VM's current CPU utilization
        double vmUtilization = vm.getTotalUtilizationOfCpu(CloudSim.clock()); // Get the VM's current CPU utilization
        state[2] = vmUtilization;

        // Feature 4: Cloudlet's remaining length (in MI)
        long executedLength = cloudlet.getCloudletFinishedSoFar(); // Get the executed length of the cloudlet
        long remainingLength = cloudletLength - executedLength; // Calculate the remaining length
        state[3] = remainingLength;

        // You can add more features based on your requirements

        return state;
    }

}
