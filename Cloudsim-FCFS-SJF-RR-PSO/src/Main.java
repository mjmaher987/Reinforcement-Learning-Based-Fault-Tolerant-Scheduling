// import necessary packages

import A2C.A2C_Scheduler;
import FCFS.FCFS_Scheduler;
import QLearning.QLearningScheduler;
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
        FCFS_Scheduler.main(args);

        SJF_Scheduler.main(args);

        A2C_Scheduler.main(args);

        QLearningScheduler.main(args);

    }
}
