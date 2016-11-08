import java.util.*;

class process{
	int processNum;
	int resourceAllocation[];
	String processName;

	public process(int processNum, int resourceAllocation[]){
		this.processNum = processNum;
		this.resourceAllocation = resourceAllocation;
		processName = "P" + String.valueOf(processNum);
	}

	public process(int processNum, int resourceAllocation[], String name){
		this.processNum = processNum;
		this.resourceAllocation = resourceAllocation;
		processName = name;
	}

	boolean checkFinish(process processNeeded, int work[], int resourceCount){
		for(int i = 0; i < resourceCount; i++){
			if(processNeeded.resourceAllocation[i]> work[i]){
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int choice;
		do{
			System.out.print("Menu\n==========\n1. Deadlock Prevention\n2. Deadlock Avoidance\n\n0. Exit\n\nInput choice: ");
			choice = scan.nextInt();

			System.out.println("\n");
			if(choice == 1){
				System.out.println("DEADLOCK PREVENTION\n");
				deadlock_prevention();
				System.out.println("\n");
			}
			else if(choice == 2){
				System.out.println("DEADLOCK AVOIDANCE\n");
				deadlock_avoidance();
				System.out.println("\n");
			}
		}while(choice != 0);

		System.out.println("EXIT");
	}

	static void deadlock_prevention(){
		ArrayList<process> processes = new ArrayList<>();

		int resources[];
		int work[] = new int[1];
		int safeSequence[], safeSequenceNum = 0, temp_finish = 0;
		boolean finish[], deadlocked = false, safe = false;

		Scanner scan = new Scanner(System.in);

		System.out.print("Input number of processes: ");
		int processCount = scan.nextInt();

		//Initialization
		safeSequence = new int[processCount];
		finish = new boolean[processCount];

		for(int i = 0; i < processCount; i++){
			resources = new int[1];

			scan.nextLine();
			System.out.print("Input process name for P" + i + ": ");
			String name = scan.nextLine();
			System.out.print("Input time for process " + name + ": ");
			resources[0] = scan.nextInt();

			process temp = new process(i, resources, name);
			processes.add(temp);

			System.out.println("==========");
		}

		System.out.print("Input the value of the available resources: ");
		work[0] = scan.nextInt();

		System.out.println();
		int i = 0;
		while(deadlocked != true){
			if(i < processCount){
				if(!finish[i]){
					if(processes.get(i).checkFinish(processes.get(i), work, 1)){
						safeSequence[safeSequenceNum] = i;
						safeSequenceNum++;

						work[0] += processes.get(i).resourceAllocation[0];

						finish[i] = true;
						System.out.println("FINISHED PROCESS " + processes.get(i).processName);
					}

				}

				i++;
			}

			else{
				if(temp_finish != safeSequenceNum){
					i = 0;
					temp_finish = safeSequenceNum;
				}

				else{
					if(safeSequenceNum == processCount){
						safe = true;
						System.out.println("State: SAFE");

						System.out.print("\nSafe sequence: <");
						for(i = 0; i < processCount; i++){
							if(i+1 == processCount){
								System.out.print(processes.get(safeSequence[i]).processName + ">");
							}
							else{
								System.out.print(processes.get(safeSequence[i]).processName + " , ");
							}
						}
					}

					else{
						deadlocked = true;
						System.out.println("State: DEADLOCKED");
					}
				}	
			}

			if(safe){
				break;
			}
		}
	}

	static void deadlock_avoidance(){
		ArrayList<process> processes = new ArrayList<>();
		ArrayList<process> processesMax = new ArrayList<>();
		ArrayList<process> processNeeded = new ArrayList<>();

		int safeSequence[], resources[], work[], safeSequenceNum = 0, temp_finish = 0;
		boolean finish[], deadlocked = false;

		Scanner scan = new Scanner(System.in);

		System.out.print("Input number of processes: ");
		int processCount = scan.nextInt();

		System.out.print("Input number of resources: ");
		int resourceCount = scan.nextInt();

		//Initialization
		safeSequence = new int[processCount];
		finish = new boolean[processCount];
		work = new int[resourceCount];

		//Input handling for each process
		System.out.println("\nResource Allocation for each process\n---");
		for(int i = 0; i < processCount; i++){
			resources = new int[resourceCount];

			for(int j = 0; j < resourceCount; j++){
				System.out.print("Input resource allocation " + (j+1) + " of P" + i + ": ");
				resources[j] = scan.nextInt();
			}

			process temp = new process(i, resources);
			processes.add(temp);

			System.out.println("==========");
		}

		//Input handling for max
		System.out.println("\nMaximum demand for each process\n---");
		for(int i = 0; i < processCount; i++){
			resources = new int[resourceCount];

			for(int j = 0; j < resourceCount; j++){
				System.out.print("Input max resource allocation " + (j+1) + " of P" + i + ": ");
				resources[j] = scan.nextInt();
			}

			process temp = new process(i, resources);
			processesMax.add(temp);

			System.out.println("==========");
		}

		//Input handling for work / available
		System.out.println("\nAvailable resources/work\n---");
		for(int i = 0; i < resourceCount; i++){
			System.out.print("Input available resources for resource " + (i+1) + ": ");
			work[i] = scan.nextInt();
		}

		//Calculate needed resources
		for(int i = 0; i < processCount; i++){
			resources = new int[resourceCount];
			for(int j = 0; j < resourceCount; j++){
				resources[j] = processesMax.get(i).resourceAllocation[j] - processes.get(i).resourceAllocation[j];
			}

			process temp = new process(i, resources);
			processNeeded.add(temp);
		}

		System.out.println("==========");

		//Display matrix
		System.out.println("\nResources");
		printProcess(processes, resourceCount);

		System.out.println("\nMax Resources");
		printProcess(processesMax, resourceCount);

		System.out.println("\nResources Needed");
		printProcess(processNeeded, resourceCount);

		//Deadlock Checking
		deadlocked = deadlock_avoidance_check(processCount, finish, processes, work, resourceCount, processNeeded, safeSequence, safeSequenceNum, temp_finish);

		if(!deadlocked){
			System.out.print("\nAdd new request?(1 - yes / 0 - no): ");
			int safe_choice = scan.nextInt();

			if(safe_choice == 1){
				System.out.print("Which process to change? ");
				int process_to_change = scan.nextInt();

				if(process_to_change < processCount){
					for(int k = 0; k < resourceCount; k++){
						System.out.print("Input resource request " + (k+1) + " for P" + process_to_change + ": ");
						int resource_request = scan.nextInt();

						if(resource_request < work[k]){
							processes.get(process_to_change).resourceAllocation[k] += resource_request;
							work[k] -= resource_request;

						}

						else{
							System.out.println("Invalid");
							return;
						}
					}

					//Calculate needed resources
					for(int i = 0; i < processCount; i++){
						for(int j = 0; j < resourceCount; j++){
							processNeeded.get(i).resourceAllocation[j] = processesMax.get(i).resourceAllocation[j] - processes.get(i).resourceAllocation[j];
						}	
					}

					System.out.println("==========");

					//Display matrix
					System.out.println("\nResources");
					printProcess(processes, resourceCount);

					System.out.println("\nMax Resources");
					printProcess(processesMax, resourceCount);

					System.out.println("\nResources Needed");
					printProcess(processNeeded, resourceCount);

					//Re initialize
					safeSequenceNum = 0;
					temp_finish = 0;
					safeSequence = new int[processCount];
					finish = new boolean[processCount];

					deadlocked = deadlock_avoidance_check(processCount, finish, processes, work, resourceCount, processNeeded, safeSequence, safeSequenceNum, temp_finish);
				}

				else{
					System.out.println("Process not found");
					return;
				}
			}
			else{
				return;
			}
		}

		return;
	}

	static boolean deadlock_avoidance_check(int processCount, 
		boolean finish[], 
		ArrayList<process> processes, 
		int work[], 
		int resourceCount, 
		ArrayList<process> processNeeded,
		int safeSequence[],
		int safeSequenceNum,
		int temp_finish){
		
		int i = 0;
		boolean deadlocked = false;

		while(deadlocked != true){
			if(i < processCount){
				if(!finish[i]){
					if(processes.get(i).checkFinish(processNeeded.get(i), work, resourceCount)){
						safeSequence[safeSequenceNum] = processes.get(i).processNum;
						safeSequenceNum++;

						for(int j = 0; j < resourceCount; j++){
							work[j] = processes.get(i).resourceAllocation[j] + work[j];
						}

						finish[i] = true;
						System.out.println("FINISHED PROCESS " + i);
					}
				}

				i++;
			}

			else{
				if(temp_finish != safeSequenceNum){
					i = 0;
					temp_finish = safeSequenceNum;
				}

				else{
					if(safeSequenceNum == processCount){
						System.out.println("State: SAFE");

						System.out.print("\nSafe sequence: <");
						for(i = 0; i < processCount; i++){
							if(i+1 == processCount){
								System.out.print(safeSequence[i] + ">");
							}
							else{
								System.out.print(safeSequence[i] + " , ");
							}
						}

						return false;
					}

					else{
						System.out.println("State: DEADLOCKED");
						deadlocked = true;
					}
				}
			}
		}	
		return deadlocked;
	}

	static void printProcess(ArrayList<process> temp, int resourceCount){
		for(int i = 0; i < temp.size(); i++){
			System.out.print("P" + temp.get(i).processNum);

			for(int j = 0; j < resourceCount; j++){
				System.out.print(" | " + temp.get(i).resourceAllocation[j]);
			}
			System.out.println();
		}

		System.out.println();
	}
}