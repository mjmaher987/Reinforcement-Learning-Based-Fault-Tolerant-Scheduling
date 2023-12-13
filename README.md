# Reinforcement-Learning-Based-Fault-Tolerant-Scheduling
Related to the article that is going to be published.


## Contributors
- Mohammad Javad Maheronnaghsh

## Helpers
- How to set up CloudSim : https://github.com/AtheerAlgherairy/Cloudsim-FCFS-SJF-RR-PSO-
- What is iFogSim : https://amirkabir-science.com/cloudsim-free-course/
- iFogSim Workshop : youtube.com/watch?v=rEy_rSRfXIc

## Final Update
- Due to the meeting held on September 11th, the rest of the project will be assigned to someone else (probably Mr. Soleymani)

## Last Step
- Send the pseudo Code of Double Q-Learning

  
## Comments
- The power cannot be reported beause there is no power-based method used in the process (like DVFS, etc.).
- Successful rate and memory have to be revised
  - for success rate we have to add "deadline"
  - for memory we have to give the current time (or change the nethod) -> it is always giving 1.0 right now

## Overall View of the remaind parts
![image](https://github.com/mjmaher987/Reinforcement-Learning-Based-Fault-Tolerant-Scheduling/assets/77095635/746d6f05-9d02-47f4-b34a-5edfd0a0da1e)

## Important Notes
- Delete these 2 files (Note: This issue may be resolved on the next versions)
   - Delete CommunicationTimeMatrix.txt
   - Delete ExecutionTimeMatrix.txt
- Other TODOs
  - Create the excel fils based on these
  - Literature Review
  - Graphical Abstract


## First Link
These are the objective of this project:
![image](https://github.com/mjmaher987/Reinforcement-Learning-Based-Fault-Tolerant-Scheduling/assets/77095635/c3958ff7-65fd-461e-9878-bc4222d721b9)

Here are the implemeneted classes:
![image](https://github.com/mjmaher987/Reinforcement-Learning-Based-Fault-Tolerant-Scheduling/assets/77095635/6d291f45-b091-4bfb-afc6-b1b97aa211f0)

And we can do the same thing for our project.


## Second Link - AmirKabir
![image](https://github.com/mjmaher987/Reinforcement-Learning-Based-Fault-Tolerant-Scheduling/assets/77095635/339f4514-fa7b-4574-8b31-46e9e841e0b2)
The above image shows the full structure of cloudsim.

- Machine Allocation in cloudsim:
  1. Time-Shared Policy: Like FIFO
  2. Space-Shared: Like Raund-Robin


## Third Link
TODO

## My Own Implementation
TODO




## Some Definitions
1. FogDevice:
   - Represents a fog device or edge device in the simulated environment.
   - Attributes include the device's processing capacity (mips), memory (ram), bandwidth (upBw and downBw), power consumption, and other characteristics.
   - Fog devices are connected in a hierarchy, with a parent-child relationship defined by the setParentId() method.
   - In the code, Fog devices are created for both fog nodes and the cloud.

2. Controller:
   - Represents the master controller in the simulated environment.
   - Manages the placement and execution of application modules on fog devices.
   - Controls the simulation flow and manages the submission and scheduling of applications.

3. Application:
   - Represents an application model that defines the structure and behavior of an application in terms of modules and their connections.
   - Modules are connected in a directed acyclic graph (DAG) representing the workflow of the application.
   - In the code, an application is created using the createApplication() method.

4. ModuleMapping:
   - Defines the mapping of application modules to fog devices.
   - Associates each module with a specific fog device where it will be deployed and executed.
   - In the code, moduleMapping is created using the createModuleMapping() method and populated using the addModuleToDevice() method.

Now, let's address your specific questions:

Setting the number of cloudlets and VMs:
- In iFogSim, cloudlets and VMs are not explicitly defined as in cloud-centric simulations. Instead, you define modules and their instances in the application model, and the framework handles the mapping of modules to VMs and execution of tasks.

Task type (soft-real time and periodic):
- iFogSim supports the modeling of real-time tasks by defining their characteristics in the application model.
- You can set the properties of tasks, such as their execution time, deadline, and periodicity, in the application model using the relevant methods.

Task scheduling policy (FCFS, A2C, SJR):
- iFogSim provides various module placement and task scheduling policies, including FCFS (First-Come, First-Served), A2C (Ant Colony Optimization), and SJR (Shortest Job Remaining).
- The choice of task scheduling policy depends on the placement algorithm you use and how you configure it in the code.

Exporting simulation results to an Excel file:
- To export simulation results to an Excel file, you can use libraries such as Apache POI or JExcelAPI in Java.
- Within the simulation code, you can collect the required metrics (makespan, completion time, wait time, response time, successful rate) and store them in variables or data structures.
- After the simulation ends, you can write the collected data to an Excel file using the selected library, specifying the format and layout of the output.

Objectives (minimize the maximum completion time and response time):
- In the provided code snippet, the objective function for minimizing the maximum completion time and response time is not explicitly defined.
- To incorporate such objectives, you would need to modify the code by implementing appropriate optimization algorithms or metrics to track and optimize the desired objectives.



