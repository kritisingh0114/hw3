# hw1- Manual Review

The homework will be based on this project named "Expense Tracker",where users will be able to add/remove daily transaction. 

## Compile

To compile the code from terminal, use the following command:
```
cd src
javac ExpenseTrackerApp.java
java ExpenseTracker
```

You should be able to view the GUI of the project upon successful compilation. 

## Java Version
This code is compiled with ```openjdk 17.0.7 2023-04-18```. Please update your JDK accordingly if you face any incompatibility issue.

## New Functionality

The new functionality that we added was to let the user be able to undo a transaction.

There are a few cases where clicking on the undo button will cause the user to see an error message. These cases are:
1. When the user clicks on the undo button and the table has no transactions in it, the program will tell the user to add a transaction before trying to undo
2. When the user clicks on the undo button without selecting on a transaction to undo, the program will tell the user to select a transaction from the tbal to undo
3. When the user clicks on the undo button while selecting the "Total" row, the program will tell the user to select on a row that has transaction data

If the user has data in the table and selects a valid row, then clicks on the undo button, the program will remove that transaction from the table (removing that row) and update the total value (removing the cost of that transaction from the final total).