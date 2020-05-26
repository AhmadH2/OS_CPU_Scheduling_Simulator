/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import static os.FCFS.FCFSM;
import static os.RR.RRM;
import static os.SJFMethods.SJF;

/**
 *
 * @author ahmad horyzat
 */
public class OS {

    /**
     * @param args the command line arguments
     */
    // array to store the information of the 5 processes
    static Process [] processes = new Process[5];
    public static void main(String[] args) {
        // TODO code application logic here
        
        int mSize;
        int pSize;
        int Q = 0;
        int CS = 0;
        int processId;
        int arrivalTime;
        int cpuBurst;
        int processSize;
        
        File file = new File("Processes.txt");
        try {
            Scanner infile = new Scanner(file);
            
            mSize = infile.nextInt();
            pSize = infile.nextInt();
            Q = infile.nextInt();
            CS = infile.nextInt();
            
            for(int i = 0; i< 5; i++){
                processId = infile.nextInt();
                arrivalTime = infile.nextInt();
                cpuBurst = infile.nextInt();
                processSize = infile.nextInt();
                // creating object for each process
                processes[i] = new Process(processId, arrivalTime, cpuBurst, processSize);
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
        }

        ArrayList<String> GanttChart = null;
        // menu
        System.out.println("The processes are waiting to be executed, choose, choose which algorithm: ");
        System.out.println("1.First come first served(FCFS) \n2.Shortest jop first(SJF)"
                + " \n3.Round Robin(RR");
        Scanner input = new Scanner(System.in);
        int choice;
        choice = input.nextInt();
        switch(choice) {
            case 1:
                GanttChart = FCFSM(CS);
                System.out.println("Processes sequance as stored: ");
                for(String str: GanttChart)
                    System.out.print(str + " ");
                System.out.println("\n");
                System.out.println("Gantt Chart for FCFS");
                buildGantt(GanttChart);
                break;
            case 2:
                GanttChart = SJF(CS);
                System.out.println("Processes sequance as stored: ");
                for(String str: GanttChart)
                    System.out.print(str + " ");
                System.out.println("\n");
                System.out.println("Gantt Chart for SJF");
                buildGantt(GanttChart);
                break;
            case 3:
                GanttChart = RRM(CS,Q);
                System.out.println("Processes sequance as stored: ");
                for(String str: GanttChart)
                    System.out.print(str + " ");
                System.out.println("\n");
                System.out.println("Gantt Chart for RR");
                buildGanttRR(GanttChart,Q);
                break;

            }     
        
        //output processes finish time in a table
        System.out.println("Process |  finish time");
        for(int i=0; i<5; i++) {
            System.out.print("    P" + i + "  |");
            System.out.println("    " + finishTime(GanttChart, "P" + i));
        }
        System.out.println();
        
        //output processes waiting time in a table
        System.out.println("Process |  waiting time");
        for(int i=0; i<5; i++) {
            System.out.print("    P" + i + "  |");
            System.out.println("    " + waitingTime(GanttChart, "P" + i));
        }
        System.out.println();
        
        //output processes turnaround time in a table
        System.out.println("Process |  turnaround time");
        for(int i=0; i<5; i++) {
            System.out.print("    P" + i + "  |");
            System.out.println("    " + turnaroundTime(GanttChart, "P" + i));
        }
        System.out.println();
        
        System.out.println("The average finish time: " + averageFinshTime(GanttChart));
        System.out.println("The average waiting time: " + averageWaitingTime(GanttChart));
        System.out.println("The average turnaround time: " + averageTurnaroundTime(GanttChart));
        System.out.println("CPU utilization: " + CPUutilization(GanttChart));

    }
    
    //built Gantt Chart dynamically for FCFS & SJF 
    public static void buildGantt(ArrayList<String> GanttChart) {
        
        int [][] GanttInfo = new int[2][5];
        int j=0;
        int count =1;
        //-----
        for(int i=0; i<GanttChart.size() -1 ; i++) {
            if(!GanttChart.get(i).equals("context Switch")) {
                if(!GanttChart.get(i).equals(GanttChart.get(i+1))) {
                    GanttInfo[0][j] = Integer.parseInt(GanttChart.get(i).substring(1));
                    j++;
                }
            } 

        }
        GanttInfo[0][j] = Integer.parseInt(GanttChart.get(GanttChart.size()-1).substring(1));

        j = 0;
        for(int i=0; i<GanttChart.size(); i++) {
            if(GanttChart.get(i).equals("context Switch")) {
                while(GanttChart.get(i).equals("context Switch")) {
                    i++;
                }
                
                GanttInfo[1][j] = i-1;
                j++;
            }
            else {
                
            }
        }
        GanttInfo[1][j] = GanttChart.size() - GanttChart.lastIndexOf("context Switch")
                + GanttChart.lastIndexOf("context Switch") - 1;
        
         System.out.println(" ___________________________________________________________");

        
        for(int i = 0; i < 5; i++) {
            
            System.out.print("|    P" + GanttInfo[0][i] + "     ");
        }   
        System.out.println("|");
        for(int i=0; i<GanttInfo[1].length; i++)
            System.out.print("|___________");
        System.out.println("|");
        System.out.print("0           " );
        
        for(int i=0; i<GanttInfo[1].length; i++) {
            System.out.format("%2S", (GanttInfo[1][i]+ 1 ));
            System.out.print("          " );
        }  
        System.out.println();
        
    }
    
    // built Gantt Chart dynamically for RR
    public static void buildGanttRR(ArrayList<String> GanttChart, int Q) {
        int cntr = 0;
        for(int i=0; i< processes.length; i++) {
            cntr += Math.ceil(processes[i].getCpuBurst() * 1.0/ Q);
        }
        int [][] GanttInfo = new int[2][cntr];
        for(int i=0; i<GanttInfo[0].length; i++) {
            GanttInfo[0][i] = -1;
            GanttInfo[1][i] = 0;
        }
        int j=0;
        for(int i=0; i<GanttChart.size() -1 ; i++) {
            if(!GanttChart.get(i).equals("context Switch")) {
                if(!GanttChart.get(i).equals(GanttChart.get(i+1))) {
                    GanttInfo[0][j] = Integer.parseInt(GanttChart.get(i).substring(1));
                    j++;
                }
            } 

        }

        GanttInfo[0][j] = Integer.parseInt(GanttChart.get(GanttChart.size()-1).substring(1));

 
        j = 0;
        for(int i=0; i<GanttChart.size(); i++) {
            if(GanttChart.get(i).equals("context Switch")) {
                while(GanttChart.get(i).equals("context Switch")) {
                    i++;
                }
                GanttInfo[1][j] = i - 1;
                j++;
            }

        }
        GanttInfo[1][j] = GanttChart.size() - GanttChart.lastIndexOf("context Switch")
                + GanttChart.lastIndexOf("context Switch") - 1;

        for(int i=0; i< GanttInfo[1].length; i++) {
            System.out.print(" ___________");
        }
        
        System.out.println();
         for(int i = 0; i < GanttInfo[1].length ; i++) {
            
            System.out.print("|    P" + GanttInfo[0][i] + "     ");
        }   
         System.out.print("|    P" + GanttInfo[0][Integer.parseInt(GanttChart.get(GanttChart.size() - 1).substring(1))] + "     ");
        System.out.println("|");
        for(int i=0; i<GanttInfo[1].length; i++)
            System.out.print("|___________");
        System.out.println("|");
        System.out.print("0           " );
        
        for(int i=0; i<GanttInfo[1].length ; i++) {
            System.out.format("%2S", (GanttInfo[1][i]+ 1 ));
            System.out.print("          " );
        }  
        System.out.println();  
    }
    
    public static int finishTime(ArrayList<String> GanttChart, String proc) {
        return GanttChart.lastIndexOf(proc) + 1;
    
    }
    
    public static int waitingTime(ArrayList<String> GanttChart , String proc) {
        int count = 0;
        for(int i = 0; i < GanttChart.lastIndexOf(proc); i++ ) {
            if(!GanttChart.get(i).equals(proc))
                count++;
        }
        
        int id = Integer.parseInt(proc.substring(1));
        return count - processes[id].getArrivalTime() ;
    }
    
    public static int turnaroundTime(ArrayList<String> GanttChart , String proc) {
        int id = Integer.parseInt(proc.substring(1));
        return waitingTime(GanttChart, proc) + processes[id].getCpuBurst();
    }
    
    public static double averageWaitingTime(ArrayList<String> GanttChart) {
        double sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += waitingTime( GanttChart, "P"+i);
        }
        sum /=5;
        return sum;
    }
    
    public static double averageTurnaroundTime(ArrayList<String> GanttChart) {
        double sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += turnaroundTime(GanttChart, "P"+i);
        }
        sum /=5;
        return sum;
    }
    
    public static double averageFinshTime(ArrayList<String> GanttChart) {
        double sum = 0;
        for (int i = 0; i < 5; i++) {
            sum += finishTime(GanttChart, "P"+i);
        }
        sum /=5;
        return sum;
    }
    
    public static int findIndex(String p) {
        int id = Integer.parseInt(p.substring(1));
        int i;
        for(i=0; i<processes.length; i++) {
            if(processes[i].getProcessId() == id)
                break;
        }
        return i; //process index in processes array
    }
    
    public static double CPUutilization(ArrayList<String> GanttChart) {
        double totalBurst = 0;
        for(Process p: processes) {
            totalBurst += p.getCpuBurst();
        }
        return totalBurst / GanttChart.size();
    }
    
}
