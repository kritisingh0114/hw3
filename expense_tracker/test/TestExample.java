// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;


import model.Filter.TransactionFilter;
import javax.swing.JOptionPane;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  private ExpenseTrackerApp app;

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
    app = new ExpenseTrackerApp();
  }

  @After
  public void tearDown(){
    model = null;
    view = null;
    controller = null;
    app = null;
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    @Test
    public void testAddTransactionView() {
        // Pre-condition: Transaction table is empty
        assertEquals(0, view.getTransactionsTable().getRowCount());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: Transaction table contains only
	//                 the added transaction (row count will be 2)
        assertEquals(2, view.getTransactionsTable().getRowCount());
    
        // Check the contents of the view
        int expectedSerial = view.getTransactionsTable().getRowCount()-1;
        int viewSerial = (int)view.getTransactionsTable().getModel().getValueAt(0, 0);
        assertEquals(expectedSerial, viewSerial);

        double viewAmount = (double)view.getTransactionsTable().getModel().getValueAt(0, 1);
        assertEquals(amount, viewAmount, 0.01);

        String viewCategory = (String)view.getTransactionsTable().getModel().getValueAt(0, 2);
        assertEquals(category, viewCategory);

        Transaction firstTransaction = model.getTransactions().get(0);
        String expectedDateString = firstTransaction.getTimestamp();
        String viewDate = (String)view.getTransactionsTable().getModel().getValueAt(0, 3);
        assertEquals(expectedDateString, viewDate);

	// Check the total amount
        double expectedTotal = 50.0;
        double viewTotal = (double)view.getTransactionsTable().getModel().getValueAt(1, 3);
        assertEquals(expectedTotal, viewTotal, 0.01);
    }

    @Test
    public void testAddInvalidCategoryInput() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: "Add" a transaction with invalid category
	double amount = 50.0;
	String category = "foods";
        assertFalse(controller.addTransaction(amount, category));

        // Cofirm that the proper exception is sent
        try {
            Transaction t = new Transaction(amount, category);
        } catch (Exception e) {
            assertEquals("java.lang.IllegalArgumentException", e.getClass().getName());
        }

        // Post-condition: List of transactions contains 0 transactions	
        assertEquals(0, model.getTransactions().size());
	
	// Check the total amount
        assertEquals(0, getTotalCost(), 0.01);
    }

    @Test
    public void testAddInvalidAmountInput() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: "Add" a transaction with invalid amount
	double amount = -50.0;
	String category = "food";
        assertFalse(controller.addTransaction(amount, category));

        // Cofirm that the proper exception is sent
        try {
            Transaction t = new Transaction(amount, category);
        } catch (Exception e) {
            assertEquals("java.lang.IllegalArgumentException", e.getClass().getName());
        }
    
        // Post-condition: List of transactions contains 0 transactions	
        assertEquals(0, model.getTransactions().size());
	
	// Check the total amount
        assertEquals(0, getTotalCost(), 0.01);
    }

    @Test
    public void testAmountFilter() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a few transactions and get the filtered transactions
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        amount = 60.0;
	category = "food";
        assertTrue(controller.addTransaction(amount, category));

        amount = 60.0;
	category = "other";
        assertTrue(controller.addTransaction(amount, category));

        AmountFilter amountFilter = new AmountFilter(60.0);
        List<Transaction> transactions = model.getTransactions();
        List<Transaction> filteredTransactions = amountFilter.filter(transactions);

        // Post-condition: List of transactions contains 2 transactions	
        assertEquals(2, filteredTransactions.size());

        //check the contents of these two transactions (the amount for both should be 60.0)
        //first filtered trasnaction expectation: 60.0, food, currentDate (the second transaction in the transactions list)
        Transaction firstTransaction = filteredTransactions.get(0);
        Transaction firstTransactionModel = model.getTransactions().get(1);

        double filteredAmount = firstTransaction.getAmount();
        assertEquals(firstTransactionModel.getAmount(), filteredAmount, 0.01);

        String filteredCategory = firstTransaction.getCategory();
        assertEquals(firstTransactionModel.getCategory(), filteredCategory);
        
        String expectedDateString = firstTransactionModel.getTimestamp();
        String filteredDate = firstTransaction.getTimestamp();
        assertEquals(expectedDateString, filteredDate);

        //second filtered trasnaction expectation: 60.0, other, currentDate (the third transaction in the transactions list)
        Transaction secondTransaction = filteredTransactions.get(1);
        Transaction secondTransactionModel = model.getTransactions().get(2);

        filteredAmount = secondTransaction.getAmount();
        assertEquals(secondTransactionModel.getAmount(), filteredAmount, 0.01);

        filteredCategory = secondTransaction.getCategory();
        assertEquals(secondTransactionModel.getCategory(), filteredCategory);

        expectedDateString = firstTransactionModel.getTimestamp();
        filteredDate = secondTransaction.getTimestamp();
        assertEquals(expectedDateString, filteredDate);
	
	// Check the total amount
        assertEquals(170.0, getTotalCost(), 0.01);
    }

    @Test
    public void testCategoryFilter() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a few transactions and get the filtered transactions
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        amount = 60.0;
	category = "food";
        assertTrue(controller.addTransaction(amount, category));

        amount = 60.0;
	category = "other";
        assertTrue(controller.addTransaction(amount, category));

        CategoryFilter categoryFilter = new CategoryFilter("food");
        List<Transaction> transactions = model.getTransactions();
        List<Transaction> filteredTransactions = categoryFilter.filter(transactions);

        // Post-condition: List of transactions contains 2 transactions	
        assertEquals(2, filteredTransactions.size());

        //check the contents of these two transactions (the amount for both should be 60.0)
        //first filtered trasnaction expectation: 50.0, food, currentDate (the second transaction in the transactions list)
        Transaction firstTransaction = filteredTransactions.get(0);
        Transaction firstTransactionModel = model.getTransactions().get(0);

        double filteredAmount = firstTransaction.getAmount();
        assertEquals(firstTransactionModel.getAmount(), filteredAmount, 0.01);

        String filteredCategory = firstTransaction.getCategory();
        assertEquals(firstTransactionModel.getCategory(), filteredCategory);
        
        String expectedDateString = firstTransactionModel.getTimestamp();
        String filteredDate = firstTransaction.getTimestamp();
        assertEquals(expectedDateString, filteredDate);

        //second filtered trasnaction expectation: 60.0, food, currentDate (the third transaction in the transactions list)
        Transaction secondTransaction = filteredTransactions.get(1);
        Transaction secondTransactionModel = model.getTransactions().get(1);

        filteredAmount = secondTransaction.getAmount();
        assertEquals(secondTransactionModel.getAmount(), filteredAmount, 0.01);

        filteredCategory = secondTransaction.getCategory();
        assertEquals(secondTransactionModel.getCategory(), filteredCategory);

        expectedDateString = firstTransactionModel.getTimestamp();
        filteredDate = secondTransaction.getTimestamp();
        assertEquals(expectedDateString, filteredDate);
	
	// Check the total amount
        assertEquals(170.0, getTotalCost(), 0.01);
    }


    @Test
    public void testUndoButtonWhenEmpty() {
        // Pre-condition: Transaction table is empty
        JTable newtable = view.getTransactionsTable();
        assertEquals(0, newtable.getRowCount());

        // Perform the action: Undo a transaction on an empty table
        String errormessage = controller.removeTransaction(0, newtable);

        // Post-condition: The correct error message should be returned and the table should still be empty
        assertEquals("Please add a transaction first!", errormessage); 
        assertEquals(0, newtable.getRowCount());
    }

    @Test
    public void testUndoAllowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, view.getTransactionsTable().getRowCount());

        // Perform the action: adding a transaction
        double amount = 50.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        DefaultTableModel newtable = (DefaultTableModel) view.getTransactionsTable().getModel();
        assertEquals(2, newtable.getRowCount());
        assertEquals(amount, newtable.getValueAt(0, 1));
        assertEquals(category, newtable.getValueAt(0, 2));

        //Perform the action: removing the transaction
        JTable anothertable = view.getTransactionsTable();
        controller.removeTransaction(0, anothertable);

        // Post-condition: View does not contain any transactions	
        assertEquals(1, anothertable.getRowCount());
        double viewTotal = (double)view.getTransactionsTable().getModel().getValueAt(0, 3);
        assertEquals(0, viewTotal, 0.01);
    }


}
