// import necessary packages
import A2C.A2C_Scheduler;
import FCFS.FCFS_Scheduler;
import QLearning.QLearning_Scheduler;
import SJF.SJF_Scheduler;


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
        FCFS_Scheduler fcfs = new FCFS_Scheduler();
        fcfs.main(args);

        SJF_Scheduler sjf = new SJF_Scheduler();
        sjf.main(args);

        A2C_Scheduler a2c = new A2C_Scheduler();
        a2c.main(args);

        QLearning_Scheduler q_learning = new QLearning_Scheduler();
        q_learning.main(args);


    }
}
