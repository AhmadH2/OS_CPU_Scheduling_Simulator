/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os;

import java.util.ArrayList;
import java.util.Arrays;
import static os.FCFS.sortArrival;
import static os.OS.processes;

/**
 *
 * @author ahmad horyzat
 */
public class SJFMethods {
    
    // processes initially stored in arrivingProcesses arraylist sorted as arriving time
    // when the arriving time come the process moves to the readyQueue arraylist to be executed
    static ArrayList<Process> arrivingProcesses = new ArrayList<>();
    static ArrayList<Process> readyQueue = new ArrayList<>();
        
    public static ArrayList<String> SJF(int cs){
       
        ArrayList<String> GanttChart = new ArrayList<>();

        int time = 0;
        
        arrivingProcesses.addAll(Arrays.asList(processes)); // store processes read from the file to arraylist
        arrivingProcesses = sortArrival(arrivingProcesses); // sort them as the time they arrive

        Process executingProcess = null;
        MyThread thread = new MyThread(); // thread to add processes to ready Queue
        boolean finish = true;   
        int counter = 0; // process burst counter
        int cntr = 0; // contex switch counter
        thread.start();
        
        // the loop finish executing when there are NO processes in ready queue
        // AND No more processes will arrive in the future(empty arrylist)
        // AND the last process currently occupying the cpu finish executiog
        while(time < (555 ) && (!readyQueue.isEmpty() || !arrivingProcesses.isEmpty() || !finish)) {

            if(!arrivingProcesses.isEmpty())
                if(time  == arrivingProcesses.get(0).getArrivalTime()) 
                    thread.addToWaitingQueue();
            boolean flag = false;
            if(finish) {
                if(!readyQueue.isEmpty()) {
                    executingProcess  = readyQueue.get(0);
                    readyQueue.remove(0);
                    counter = 0;
                    cntr = 0;
                    finish = false; // flag when the process finish executing
                }  
                else { // No processes in the ready queue to execute 
                    // the cpu sit idle until a process come to ready queue
                    time++;
                    flag = true;
                }
            }
            
            if(executingProcess.getCpuBurst() != counter) {
                GanttChart.add("P" + executingProcess.getProcessId());
                counter++;
                time++;
            }
            else{
                if(cs == 0) {
                    if(flag)
                        GanttChart.add("context Switch");
                }
                
                if(cs != 0) {
                    if(cntr == cs-1 )
                        finish = true;
                        //performing context switch represented as filling an arrayList with "contex Switch"
                        GanttChart.add("context Switch");
                        if(!flag)
                            time++;
                        
                    cntr++;
                }
                else { 
                    finish = true;
                }

            }

            
        }
        // remove the context switch after the last process in Gantt Chart
        for(int i=0; i<cs; i++) {
            GanttChart.remove(GanttChart.size()-1);
        }
  
        return GanttChart;
        
        }
        
    // sort processes as their cpu burst in ascending order using bubble sort
    public static ArrayList<Process> sortBurst(ArrayList<Process> s){
        if(s.size()>1) {
            Process temp;
            int b = s.size() -1;
            for(int i = 0; i < b; i++) {
                for(int j = 0 ; j < b - i ; j++){
                    if(s.get(j).getCpuBurst() > s.get(j+1).getCpuBurst()) {
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
            readyQueue = sortBurst(readyQueue);
        }
    }
    
}
