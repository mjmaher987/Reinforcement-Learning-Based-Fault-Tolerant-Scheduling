import org.cloudbus.cloudsim.Cloudlet;

import java.io.*;
import java.util.List;


// Author: Mohammad Javad Maheronnaghsh
// CloudSim and IFogSim
// Setup Java SDK version 16
// Last Update: July 1st
// Associated with Sharif University of Technology
// Professors: Dr. Mohsen Anasari, Dr.Sepideh Safari
// Supervisors: Abolfazl Younesi, Elyas Oustad


public class FaultToleranceManager {
    private static final String CHECKPOINT_FILE_PATH = "checkpoint.csv";

    public static boolean isCheckpointAvailable() {
        File checkpointFile = new File(CHECKPOINT_FILE_PATH);
        return checkpointFile.exists();
    }

    public static List<Cloudlet> loadCheckpointData() {
        try (FileInputStream fis = new FileInputStream(CHECKPOINT_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Cloudlet>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveCheckpointData(List<Cloudlet> list) {
        try (FileOutputStream fos = new FileOutputStream(CHECKPOINT_FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCheckpointFile() {
        File checkpointFile = new File(CHECKPOINT_FILE_PATH);
        checkpointFile.delete();
    }
}
