# ShinePrinters - Printing Shop System

ShinePrinters is a printing shop near the University with multiple computers and printers connected over a network. Customers can use any computer to create print jobs. These print jobs are then placed in a shared queue, from which the printers can retrieve and process them. In this lab exercise, we will implement a producer-consumer-based solution using Java to handle the print jobs in ShinePrinters.

## Classes

1. **PrintJob**: Represents a print job.
2. **Computer**: Represents a computer in ShinePrinters.
3. **Printer**: Represents a printer in ShinePrinters.
4. **SharedQueue**: Represents the shared queue for print jobs.
5. **Main**: Main class to execute the program.

## Memory Consistency and Exception Handling

In a multi-threaded system like ShinePrinters, memory consistency errors can occur due to concurrent access to shared data structures. We will ensure thread safety using appropriate synchronization mechanisms such as locks or concurrent data structures. Additionally, we will handle exceptions like `TypeNotSupportedException` when a print job's file type is not supported by the system.

## File Type Validation

Printers in ShinePrinters can handle only certain types of files. Hence, when creating the print job, we will check if the system supports the type of the print job and throw `TypeNotSupportedException` if not.

## Web Interface Integration

ShinePrinters plans to introduce a simple web interface for their customers to send print jobs before coming to the shop. We will implement the `ReadAFile` method to read a given text file and add the text content to an object of the `TextFile` class. This will help customers efficiently get their jobs done.

## Implementation

We will follow appropriate Java best practices to ensure code readability, maintainability, and efficiency.

For more details, refer to the code implementation in the provided Java files.

---
**Note:** Ensure proper error handling and documentation for better code understanding and maintenance.
