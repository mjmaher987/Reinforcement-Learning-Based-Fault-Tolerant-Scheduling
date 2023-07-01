//package FCFS;
import FCFS.FCFS_Scheduler;
import SJF.SJF_Scheduler;


// Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 1st
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad

public class Main {
    public static void main(String[] args){
        FCFS_Scheduler fcfs = new FCFS_Scheduler();
        fcfs.main(args);

        SJF_Scheduler sjf = new SJF_Scheduler();
        sjf.main(args);


    }
}
