import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// add colors to the output
class Color {
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE
}
// Exception handling
class TypeNotSupportedException extends Exception {
    public TypeNotSupportedException(String message) {
        super(message);
    }
}

// PrintJob class
class PrintJob {
    private String fileName;
    private String fileType;

    public PrintJob(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }
    // Getters
    public String getFileName() {
        return fileName;
    }
    // Getters
    public String getFileType() {
        return fileType;
    }
}

// SharedQueue class
class SharedQueue {
    // Queue to store print jobs
    private Queue<PrintJob> queue;
    // Capacity of the queue
    private int capacity;
    // Lock to synchronize access to the queue
    private Lock lock;
    // Conditions to wait and notify when the queue is full or empty
    private Condition queueNotFull;
    // Conditions to wait and notify when the queue is full or empty
    private Condition queueNotEmpty;

    // Constructor
    public SharedQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
        this.queueNotFull = lock.newCondition();
        this.queueNotEmpty = lock.newCondition();
    }
    // Add print job to the queue
    public void addPrintJob(PrintJob job) throws InterruptedException {
        // Acquire the lock
        lock.lock();
        // Add the print job to the queue
        try {
            while (queue.size() >= capacity) {
                queueNotFull.await();
            }

            queue.add(job);

            System.out.println(Color.CYAN + "Added job: " +Color.RESET+ job.getFileName() +  " to the queue");
            queueNotEmpty.signalAll();
        }
        // Release the lock
        finally {
            lock.unlock();
        }
    }

    public PrintJob getPrintJob() throws InterruptedException {
        // Acquire the lock
        lock.lock();
        // Remove the print job from the queue
        try {
            while (queue.isEmpty()) {
                queueNotEmpty.await();
            }
            PrintJob job = queue.remove();
            System.out.println(Color.YELLOW + " Removed job: "+Color.RESET + job.getFileName() + " from the queue");
            queueNotFull.signalAll();
            return job;
        } 
        // Release the lock
        finally {
            lock.unlock();
        }
    }
    // Check if the queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Computer class
class Computer {
    // Name of the computer
    private String name;
    // Shared queue to add print jobs
    private SharedQueue sharedQueue;
    // File type
    private static String fileT;


    // Constructor
    public Computer(String name, SharedQueue sharedQueue) {
        this.name = name;
        this.sharedQueue = sharedQueue;
    }

    // Create a print job
    public void createPrintJob(String fileName, String fileType) {
        // Add the print job to the shared queue
        try {
            if (!isFileTypeSupported(fileType)) {
                throw new TypeNotSupportedException(Color.RED + "\nFile type '" + fileType + "' is not supported.\n"+ Color.RESET);
            }
            sharedQueue.addPrintJob(new PrintJob(fileName, fileType));
        } 
        // Exception handling
        catch (InterruptedException | TypeNotSupportedException e) {
            System.out.println(e.getMessage());
        }
    }
    // Check if the file type is supported
    private boolean isFileTypeSupported(String fileType) {
        return fileType.equals("pdf") || fileType.equals("txt") || fileType.equals("doc");
    }

}
// Printer class
class Printer {
    // Name of the printer
    private String name;
    // Shared queue to get print jobs
    private SharedQueue sharedQueue;
    // Flag to stop the printer
    private volatile boolean running;

    // Constructor
    public Printer(String name, SharedQueue sharedQueue) {
        this.name = name;
        this.sharedQueue = sharedQueue;
        this.running = true;
    }
    // Stop the printer
    public void stop() {
        running = false;
    }
    // Read a file
    public static void ReadAFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(Color.PURPLE+"\n" + line  + Color.RESET+ "\n");
            }
        } catch (IOException e) {
            System.out.println(Color.RED + "\n Include text in the file or create the file\n" + Color.RESET);       
        }
    }
    // Process print jobs
    public void processPrintJobs() {
        try {
            boolean end=false;
            while (!end) {
                PrintJob job = sharedQueue.getPrintJob();
                if (job != null) {
                    System.out.println( Color.GREEN + "Printer " + name + " processing job: " + job.getFileName()+ Color.RESET);
                    // Print the file
                    if(job.getFileType()=="txt"){
                        String filename;
                        filename = job.getFileName()+"."+job.getFileType();
                        try{
                            ReadAFile(filename);
                        }
                        catch (Exception e){
                            System.out.println("Include text in the file or create the file");
                        }
                    }
                    // Sleep for 1 second
                    Thread.sleep(1000);
                    if(sharedQueue.isEmpty()){
                        end=true;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

}


// Main class
public class Main {
    public static void main(String[] args) {
        // Create a shared queue
        SharedQueue sharedQueue = new SharedQueue(5);
        Computer computer1 = new Computer("Computer 1", sharedQueue);
        Computer computer2 = new Computer("Computer 2", sharedQueue);
        Computer computer3 = new Computer("Computer 3", sharedQueue);
        Printer printer1 = new Printer("Printer 1", sharedQueue);
        Printer printer2 = new Printer("Printer 2", sharedQueue);

        // Create threads for computers and printers
        Thread computerThread1 = new Thread(() -> computer1.createPrintJob("file1", "pdf"));
        Thread computerThread2 = new Thread(() -> computer2.createPrintJob("file2", "txt"));
        Thread computerThread3 = new Thread(() -> computer3.createPrintJob("file3", "dox"));
        Thread computerThread4 = new Thread(() -> computer1.createPrintJob("file4", "pdf"));
        Thread computerThread5 = new Thread(() -> computer2.createPrintJob("file5", "txt"));
        Thread computerThread6 = new Thread(() -> computer3.createPrintJob("file6", "doc"));
        Thread printerThread1 = new Thread(() -> printer1.processPrintJobs());
        Thread printerThread2 = new Thread(() -> printer2.processPrintJobs());

        // Start the threads
        System.out.println("\n");
        computerThread1.start();
        computerThread2.start();
        computerThread3.start();
        computerThread4.start();
        computerThread5.start();
        computerThread6.start();
        printerThread1.start();
        printerThread2.start();

        // Wait for the threads to finish
        try {
            computerThread1.join();
            computerThread2.join();
            computerThread3.join();
            computerThread4.join();
            computerThread5.join();
            computerThread6.join();
            printerThread1.join();
            printerThread2.join();
            // Stop both printers
            printer1.stop();
            printer2.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            System.out.println(Color.YELLOW);
            System.out.println("No pending files for printing.....");
            System.out.println(Color.RESET);

        }



    }
}