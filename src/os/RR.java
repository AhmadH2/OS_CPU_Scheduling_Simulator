/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os;

import java.util.ArrayList;
import static os.OS.processes;

/**
 *
 * @author ahmad horyzat
 */
public class RR {
    // processes initially stored in arrivingProcesses arraylist sorted as arriving time
    // when the arriving time come the process moves to the readyQueue arraylist to be executed
    static ArrayList<Process> readyQueue = new ArrayList<>(); 
    static ArrayList<Process> arrivingProcesses = new ArrayList<>(); 
   
    
    public static ArrayList<String> RRM(int cs,int q){
        // store processes read from the file to arraylist copy is made to not alter processes information
        for(Process p: processes) {
            arrivingProcesses.add(new Process(p.getProcessId(), p.getArrivalTime(), p.getCpuBurst(), p.getSize()));
        }
        
        // sort them as the time they arrive
        arrivingProcesses = sortArrival(arrivingProcesses);
        ArrayList<String> GanttChart = new ArrayList<>();  // Ganti chart array
        Process executingProcess = null ;
        MyThread thread = new MyThread(); // thread to add processes to ready Queue
        boolean finish = true; // indicate that the slice is finished
        boolean processCompleted = false; // indicate that the process finished
        int counter = 0; // process burst counter
        int cntr = 0; // contex switch counter
        thread.start();
        int time = 0;
        
        // the loop finish executing when there are NO processes in ready queue
        // AND No more processes will arrive in the future(empty arrylist)
        // AND the last process currently occupying the cpu finish executiog
        while(time < 555  && (!readyQueue.isEmpty() || !arrivingProcesses.isEmpty() || !finish)) {

            if(!arrivingProcesses.isEmpty())
                if(time  == arrivingProcesses.get(0).getArrivalTime()) {
                    thread.addToWaitingQueue();
       
                }
             boolean flag = false;       
            
            if(finish) {
                if(!readyQueue.isEmpty()) {
                    executingProcess  = readyQueue.get(0);
                    readyQueue.remove(0);
                    counter = 0;
                    cntr = 0;
                    finish = false;
                    processCompleted = false;
                }  
                else { // No processes in the ready queue to execute 
                    // the cpu sit idle until a process come to ready queue
                    time++;
                    flag = true;
                }
            }
            
            if(q > counter && executingProcess.getCpuBurst() > counter ) {
                // executing the process represented by filling an arrayList with "P" + id
                GanttChart.add("P" + executingProcess.getProcessId());
                counter++; 
                time++;
            }
            else{
                if(executingProcess.getCpuBurst() == counter)
                    processCompleted = true;
                
                if(cs == 0) {
                    finish = true;
                        if(!processCompleted) {
                            executingProcess.setCpuBurst(executingProcess.getCpuBurst() - q);
                            readyQueue.add(executingProcess);
                        }

                        if(flag) //performing context switch represented as filling an arrayList with "contex Switch"
                            GanttChart.add("context Switch");
                            
                }
                if(cs > 0 ) {
                    if(cntr == cs-1 ) {
                        finish = true;
                        if(!processCompleted) {
                            executingProcess.setCpuBurst(executingProcess.getCpuBurst() - q);
                            readyQueue.add(executingProcess);
                            
                        }
                    
                    }
                    
                    //if(!readyQueue.isEmpty() || !arrivingProcesses.isEmpty()) {
                        GanttChart.add("context Switch");
                        cntr++;
                        if(!flag)
                            time++;

                }
            }     
                

            
        }

        // remove the context switch after the last process in Gantt Chart
        for(int i=0; i<cs; i++) {
            GanttChart.remove(GanttChart.size()-1);
        }
        return GanttChart;
    }
    
    
    // sort processes as their arrinving time in ascending order using bubble sort
    public static ArrayList<Process> sortArrival(ArrayList<Process> s){
        if(s.size()>1) {
            Process temp;
            int b = s.size() -1;
            for(int i = 0; i<b; i++) {
                for(int j = 0 ; j < b ; j++){
                    if(s.get(j).getArrivalTime()> s.get(j+1).getArrivalTime()) {
                        temp = s.get(j);
                        s.set(j, s.get(j+1));
                        s.set(j+1, temp);
                    }    
                }
            }
        return s;
        }
        
       return s;
 
    }
    
    static class MyThread extends Thread {

        public MyThread() {
            super();
        }

        // adding processes to readu queue
        public void addToWaitingQueue() {
            readyQueue.add(arrivingProcesses.get(0));
            int time = arrivingProcesses.get(0).getArrivalTime();
            arrivingProcesses.remove(0);
            // in case of more than one process arrived in the same time unit as it read from a file
            for(int i=0; i<arrivingProcesses.size(); i++) {
                if(time == arrivingProcesses.get(i).getArrivalTime()) {
                    readyQueue.add(arrivingProcesses.get(0));
                    arrivingProcesses.remove(0);
                }
            }
        }
    }
    
}
