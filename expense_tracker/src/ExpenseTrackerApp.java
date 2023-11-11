import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import view.ExpenseTrackerView;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import javax.swing.*;

import java.util.List;

import model.Transaction;

public class ExpenseTrackerApp {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    // Create MVC components
    ExpenseTrackerModel model = new ExpenseTrackerModel();
    ExpenseTrackerView view = new ExpenseTrackerView();
    ExpenseTrackerController controller = new ExpenseTrackerController(model, view);
    

    // Initialize view
    view.setVisible(true);



    // Handle add transaction button clicks
    view.getAddTransactionBtn().addActionListener(e -> {
      // Get transaction data from view
      double amount = view.getAmountField();
      String category = view.getCategoryField();
      
      // Call controller to add transaction
      boolean added = controller.addTransaction(amount, category);
      
      if (!added) {
        JOptionPane.showMessageDialog(view, "Invalid amount or category entered");
        view.toFront();
      }
    });

      // Add action listener to the "Apply Category Filter" button
    view.addApplyCategoryFilterListener(e -> {
      try{
      String categoryFilterInput = view.getCategoryFilterInput();
      CategoryFilter categoryFilter = new CategoryFilter(categoryFilterInput);
      if (categoryFilterInput != null) {
          // controller.applyCategoryFilter(categoryFilterInput);
          controller.setFilter(categoryFilter);
          controller.applyFilter();
      }
     }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view, exception.getMessage());
    view.toFront();
   }});


    // Add action listener to the "Apply Amount Filter" button
    view.addApplyAmountFilterListener(e -> {
      try{
      double amountFilterInput = view.getAmountFilterInput();
      AmountFilter amountFilter = new AmountFilter(amountFilterInput);
      if (amountFilterInput != 0.0) {
          controller.setFilter(amountFilter);
          controller.applyFilter();
      }
    }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view,exception.getMessage());
    view.toFront();
   }});



    // Add action listener to the "Undo Transaction" button
    view.addUndoTransactionListener(e -> {
      JTable transactionsTable = view.getTransactionsTable();
      int selectedRow = transactionsTable.getSelectedRow();
      List<Transaction> transactions = model.getTransactions();
      String message = "";
      boolean removed = false;

      if (transactions.size() == 0) {
        message = "Please add a transaction first!";
        JOptionPane.showMessageDialog(view, message);
        view.toFront();
      }
      else if (selectedRow == -1) {
        message = "Please select a transaction to undo!";
        JOptionPane.showMessageDialog(view, message);
        view.toFront();
      }
      else if (selectedRow == transactionsTable.getRowCount()-1) {
        message = "Please select on a row that has transaction data!";
        JOptionPane.showMessageDialog(view, message);
        view.toFront();
      }
      else if (selectedRow != -1) {
        controller.removeTransaction(transactionsTable.getSelectedRow());
        removed = true;
      }

      if (!removed){
	      throw new IllegalArgumentException(message);
      }
    });
  }
}
