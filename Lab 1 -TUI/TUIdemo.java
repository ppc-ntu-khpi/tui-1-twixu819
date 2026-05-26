package com.mybank.tui;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;
import com.mybank.domain.Bank;
import com.mybank.domain.Customer;
import com.mybank.domain.Account;
import com.mybank.domain.SavingsAccount;
import com.mybank.domain.CheckingAccount;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        initializeBankData();

        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 10, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText().trim());
                    if (custNum >= 0 && custNum < Bank.getNumberOfCustomers()) {
                        Customer customer = Bank.getCustomer(custNum);

                        String nameLine = "Owner Name: " + customer.getFirstName() + " " + customer.getLastName();
                        String typeLine = "Account Type: No accounts";
                        String balanceLine = "Account Balance: $0.00";
                        if (customer.getNumberOfAccounts() > 0) {
                            Account account = customer.getAccount(0);

                            if (account instanceof SavingsAccount) {
                                typeLine = "Account Type: Savings";
                            } else if (account instanceof CheckingAccount) {
                                typeLine = "Account Type: Checking";
                            } else {
                                typeLine = "Account Type: Custom";
                            }

                            balanceLine = "Account Balance: $" + account.getBalance();
                        }

                        details.setText(nameLine + "\n" + typeLine + "\n" + balanceLine);
                    } else {
                        messageBox("Error", "Customer with ID " + custNum + " does not exist!").show();
                    }
                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }

    private void initializeBankData() {
        Bank.addCustomer("Jane", "Simms");
        Customer jane = Bank.getCustomer(0);
        jane.addAccount(new SavingsAccount(500.00, 0.05));

        Bank.addCustomer("Owen", "Bryant");
        Customer owen = Bank.getCustomer(1);
        owen.addAccount(new CheckingAccount(200.00, 100.00));
    }
}