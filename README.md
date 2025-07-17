# OS_Project

!!!PROJECT OVEVIEW!!!

This project is a CPU Scheduling Simulator built using Java. It allows users to test and visualize how different CPU scheduling algorithms work.
The app supports manual input or random generation of processes, and displays the result with animated Gantt charts and process stats.

How to run the CPU Scheduling Simulator

-Upon executing the program, the simulator will appear. 

-First you need to choose what CPU scheduling process you want to test.

-Next, you can either change the value of the Quantum slice or keep its default value of 2.

-Same goes with the MLFQ Base (Allotment Time for priority level) but this is only applicable when you choose the MLFQ as your scheduling process. 

-You can also set a value for context switch (base on the instruction, no context switch is needed) having a default value of 0.

-Buttons are presented in the GUI and this include: 

  1. Add Process - You can manually add a process if you want to and will ask a value for the arrival time and burst time.

  2. Randomizer - Press this button if you want the simulator to provide you a random set of values for arrival time and burst time. Upon clicking this   button, it will ask the user the number of processes.

  3. Run - Press this button to execute the chosen scheduler.

  4. Reset - This will immediately delete all datas including the Gantt Chart, the table for the processes, and the results. 

Sample Output of the Simulator

<img width="1919" height="1027" alt="image" src="https://github.com/user-attachments/assets/84126bd1-d228-4d63-bcbd-5c24baa35efc" />




BUGS

To the best of our knowledge, no bugs were detected

Limitations

As per limitations, the simulator does not execute a process with I/O (based on the instruction our project)

Incomplete Feature

Saving the result and exporting it to a file was not done.


Members:

Brian Joseph B. Aratia

-  Assigned to do the GUI and also the Gantt Chart.

Robert Emmanuel C. Avelino

-  Assigned to do the logical code for each scheduling processes.

All in all, it was a group effort and each member shared and contributed their ideas to one another, coded together in school, and coded also together via discord. 
We also used AI to check if our codes doesn't work.   


