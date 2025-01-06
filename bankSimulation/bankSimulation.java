package bankSimulation;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class bankSimulation {
    static final String DB_URL =  "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static void main(String[] args) throws SQLException {
        Connection conn = null;
        Scanner scan = new Scanner(System.in);
        int menuOption = 5;
        do {
            boolean validCredentials = false;
            while(!validCredentials){
                try{
                    System.out.println("Enter Oracle user id: ");
                    String user = scan.nextLine();
                    System.out.println("Enter Oracle user password: ");
                    String password = scan.nextLine();
                    conn = DriverManager.getConnection(DB_URL, user, password); 
                    System.out.println("Connection successful");
                    validCredentials = true;
                }catch(SQLException se){
                    System.out.println("[Error]: Connection error. Re-enter login data");
                } 
            }
                boolean loopNeeded = true; //loop to control flow, is kept to true until a successful query
                printMainMenu();
                while(loopNeeded){
                
                    if(scan.hasNextInt()){ //ensures the user entered an integer that corresponds to the menu options (1-3)
                            int dummy = scan.nextInt();
                            if ((dummy == 1 || dummy == 2) || dummy == 3 || dummy == 4){
                                menuOption = dummy;
                            }else {
                                System.out.println("Invalid input. Please enter either 1, 2, 3 or 4.");
                                continue;
                            }
                    }else {
                            System.out.println("Invalid input. Please enter either 1, 2, 3 or 4.");
                            String junk = scan.nextLine(); //junk variable to clear line in the case of mistyped input
                            continue;
                    }

                    if(menuOption == 1){
                        managerMenu(conn);
                    }
                    if(menuOption == 2){
                        customerMenu(conn);
                    }
                    if(menuOption == 3){
                        newCustomerMenu(conn);
                    }
                    if(menuOption == 4){ //if user entered 3, the system terminates
                        System.out.println("Closing bank services. Thank you!");
                        loopNeeded = false;
                        break;
                    }

                    scan.nextLine();
                }
                conn.close(); //closes connection
        }while(conn == null);
    }



    //Simple function used to print out the main menu
    public static void printMainMenu(){
        System.out.println();
        System.out.println("==========================");
        System.out.println("|        LUBank         |");
        System.out.println("==========================");
        System.out.println();
        System.out.println("Welcome to LUBank! Please enter an integer corresponding to your role:");
        System.out.println();
        System.out.println("\t 1) Bank Manager");
        System.out.println("\t 2) Returning customer");
        System.out.println("\t 3) New customer");
        System.out.println("\t 4) Exit bank");
        System.out.println();
        System.out.print("Choice: ");
    }


    
    public static void managerMenu(Connection conn){
        Scanner scan  = new Scanner(System.in);
        try{
            boolean IDLoopNeeded = true;
            String inputID = "";
            System.out.println();
            PreparedStatement managerIDStatement = conn.prepareStatement("SELECT manager_id FROM Manager");
            ResultSet managerIDSet = managerIDStatement.executeQuery();
            List<String> dum = new ArrayList<>();
            List<String> managerIDs = new ArrayList<>();
            while(managerIDSet.next()){
                dum.add(managerIDSet.getString("manager_id"));
            }
            for(int i = 0; i < dum.size(); i ++){
                managerIDs.add(dum.get(i).trim());
            }
            System.out.println("Please input your Manager ID from the list below, or 'e' to return to the main menu.");
            for(int i = 0; i < managerIDs.size(); i++){
                System.out.print("(" + managerIDs.get(i).trim() + ")   " );
            }
            System.out.println();
            System.out.println();
            System.out.print("Choice: ");
            while(IDLoopNeeded){
                String dummy = scan.next();
                if(managerIDs.contains(dummy.trim())){
                    inputID = dummy;
                    System.out.println("Success! Signing you in...");
                    IDLoopNeeded = false;
                }else if (dummy.trim().equals("e")){
                    System.out.println("Returning you to main menu...");
                    printMainMenu();
                    return;
                }else{
                    System.out.println("Invalid Manager ID inputted. Please input your Manager ID from the given list.");
                    continue;
                }
            }

            boolean managerMenuLoop = true;
            System.out.println();
            System.out.println("Welcome to the Manager menu! Please select a menu option below.");
            while(managerMenuLoop){
                System.out.println("\t 1) View loans");
                System.out.println("\t 2) View all accounts");
                System.out.println("\t 3) View daily, monthly, and yearly transaction averages");
                System.out.println("\t 4) Return to main menu");
                System.out.println();
                System.out.print("Choice: ");
                int managerOption = 0;
                boolean managerOptionLoop = true;
                while(managerOptionLoop){
                    if(scan.hasNextInt()){ //ensures the user entered an integer that corresponds to the menu options (1-4)
                        int dummy = scan.nextInt();
                        if ((dummy == 1 || dummy == 2 || dummy == 3 || dummy == 4)){
                            managerOption = dummy;
                            managerOptionLoop = false;
                        }else {
                            System.out.println("Invalid input. Please enter either 1, 2, 3 or 4.");
                            continue;
                        }
                    }else {
                        System.out.println("Invalid input. Please enter either 1, 2, 3 or 4.");
                        String junk = scan.nextLine(); //junk variable to clear line in the case of mistyped input
                        continue;
                    }
                }
                switch(managerOption){
                    case 1: //View all loans
                        PreparedStatement loanStatement = conn.prepareStatement("select * from loans inner join account on account.account_id = loans.account_id inner join customer on customer.customer_id = account.customer_id");
                        ResultSet loanSet = loanStatement.executeQuery();
                        System.out.println();
                        while(loanSet.next()) {
                            System.out.println("Loan ID: " + loanSet.getString("loan_id"));
                            System.out.println("Name: " + loanSet.getString("name"));
                            System.out.println("Loan Reason: " + loanSet.getString("loan_reason"));
                            System.out.println("Loan Type: " + loanSet.getString("loan_type"));
                            System.out.println("Loan Amount: $" + loanSet.getDouble("loan_amount"));
                            System.out.println("Monthly Payment: $" + loanSet.getDouble("monthly_pay"));
                            System.out.println("Interest Rate: " +  loanSet.getDouble("interest_rate") + "%");
                            if(loanSet.getDouble("loan_amount") == 0.0){
                                System.out.println("Loan Status: Paid Off/Closed");
                            }else{
                                System.out.println("Loan Status: Not Paid Off/Open");
                            }
                            System.out.println("----------------------------------------------");
                        }
                        System.out.println();
                        PreparedStatement loanSum = conn.prepareStatement("select sum(loan_amount) from loans");
                        ResultSet loanSumSet = loanSum.executeQuery();
                        if(loanSumSet.next()){
                            System.out.println("Total sum of loans within LUBank is: $" + loanSumSet.getDouble("sum(loan_amount)"));
                        }
                        System.out.println();
                    break;

                    case 2: //View all accounts
                        PreparedStatement accountStatement = conn.prepareStatement("select * from account inner join customer on account.customer_id = customer.customer_id");
                        ResultSet accountSet = accountStatement.executeQuery();
                        System.out.println();
                        while(accountSet.next()){
                            System.out.println("Name: " + accountSet.getString("name"));
                            System.out.println("Customer ID: " + accountSet.getString("customer_id"));
                            System.out.println("Account ID: " +  accountSet.getString("account_id"));
                            System.out.println("Manager ID (manager linked to account): " + accountSet.getString("manager_id"));
                            System.out.println("Account Type: " + accountSet.getString("account_type"));
                            System.out.println("Account Balance: $" + accountSet.getDouble("acct_balance"));
                            System.out.println("Interest rate: " + accountSet.getDouble("interest_rate") + "%");
                            if(accountSet.getString("account_type").equals("Investment")) {
                                System.out.println("Asset List: " + accountSet.getString(("asset_list")));
                            }
                            if(accountSet.getString("account_type").equals("Savings")){
                                System.out.println("Minimum Balance: $" + accountSet.getDouble("min_balance"));
                            }
                            System.out.println("---------------------------------------------------");
                        }
                        System.out.println();
                        int savings = 0;
                        int checking = 0;
                        int investment = 0;
                        PreparedStatement accountsTotal = conn.prepareStatement("select count(*) from account");
                        ResultSet accountsTotalSet = accountsTotal.executeQuery();
                        if(accountsTotalSet.next()){
                            System.out.println("There are a total of " + accountsTotalSet.getInt("count(*)") + " accounts registered in LUBank.");
                        }
                        PreparedStatement savingsTotal = conn.prepareStatement("select count(*) from account where account_type = 'Savings'");
                        ResultSet savingsTotalSet = savingsTotal.executeQuery();
                        if(savingsTotalSet.next()){
                           savings = savingsTotalSet.getInt("count(*)");
                        }
                        PreparedStatement checkingTotal = conn.prepareStatement("select count(*) from account where account_type = 'Checking'");
                        ResultSet checkingTotalSet = checkingTotal.executeQuery();
                        if(checkingTotalSet.next()){
                           checking = checkingTotalSet.getInt("count(*)");
                        }
                        PreparedStatement investmentTotal = conn.prepareStatement("select count(*) from account where account_type = 'Investment'");
                        ResultSet investmentTotalSet = investmentTotal.executeQuery();
                        if(investmentTotalSet.next()){
                           investment = investmentTotalSet.getInt("count(*)");
                        }
                        System.out.println("There are " + savings + " savings accounts, " + checking + " checking accounts, and " + investment + " investment accounts.");

                        PreparedStatement accountSum = conn.prepareStatement("select sum(acct_balance) from account");
                        ResultSet accountSumSet = accountSum.executeQuery();
                        if(accountSumSet.next()){
                            System.out.println("Total sum of account balances in LUBank is: $"  + accountSumSet.getDouble("sum(acct_balance)"));
                        }
                        System.out.println("---------------------------------------------------");
                        System.out.println();
                    break;

                    case 3: //view all daily, monthly, and yearly transaction averages
                        int paymentCount = 0;
                        double paymentSum = 0.0;
                        int purchaseCount = 0;
                        double purchaseSum = 0.0;
                        int withdrawalCount = 0;
                        double withdrawalSum = 0.0;
                        int depositCount = 0;
                        double depositSum = 0.0;
                        int cardPaymentCount = 0;
                        double cardTotalSum = 0.0;
                        int fundTransferCount = 0;
                        double fundTransferSum = 0.0;
                        double totalCount = 0;
                        double totalSum = 0.0;

                        PreparedStatement cardPaymentsCount = conn.prepareStatement("select count(*) from cardpayments where CPayment_Date >= add_months(sysdate, -12)");
                        ResultSet cardPaymentsCountSet = cardPaymentsCount.executeQuery();
                        if(cardPaymentsCountSet.next()){
                            cardPaymentCount = cardPaymentsCountSet.getInt("count(*)");
                        }
                        PreparedStatement cardPaymentsSum = conn.prepareStatement("select sum(CPayment_Amount) from cardpayments where cpayment_date >= add_months(sysdate, -12)");
                        ResultSet cardPaymentsSumSet = cardPaymentsSum.executeQuery();
                        if(cardPaymentsSumSet.next()){
                            cardTotalSum = cardPaymentsSumSet.getDouble("sum(cpayment_amount)");
                        }

                    
                        PreparedStatement loanPaymentsCount = conn.prepareStatement("select count(*) from loanpayments where payment_date >= add_months(sysdate, -12)"); //retrieves total number of loanpayments made in the last 12 months
                        ResultSet loanPaymentsCountSet = loanPaymentsCount.executeQuery();
                        if(loanPaymentsCountSet.next()){
                            paymentCount = loanPaymentsCountSet.getInt("count(*)");
                        }
                        PreparedStatement loanPaymentsSum = conn.prepareStatement("select sum(payment_amount) from loanpayments where payment_date >= add_months(sysdate, -12)");
                        ResultSet loanPaymentsSumSet = loanPaymentsSum.executeQuery();
                        if(loanPaymentsSumSet.next()){
                            paymentSum = loanPaymentsSumSet.getDouble("sum(payment_amount)");
                        }


                        PreparedStatement purchaseCountStmt = conn.prepareStatement("select count(*) from purchases where purchase_date >= add_months(sysdate, -12)");
                        ResultSet purchaseCountSet = purchaseCountStmt.executeQuery();
                        if(purchaseCountSet.next()){
                            purchaseCount = purchaseCountSet.getInt("count(*)");
                        }
                        PreparedStatement purchaseSumStmt = conn.prepareStatement("select sum(purchase_amount) from purchases where purchase_date >= add_months(sysdate, -12)");
                        ResultSet purchaseSumSet = purchaseSumStmt.executeQuery();
                        if(purchaseSumSet.next()){
                            purchaseSum = purchaseSumSet.getDouble("sum(purchase_amount)");
                        }


                        PreparedStatement withdrawalCountStmt = conn.prepareStatement("select count(*) from withdrawals where withdrawal_date >= add_months(sysdate, -12)");
                        ResultSet withdrawalCountSet = withdrawalCountStmt.executeQuery();
                        if(withdrawalCountSet.next()){
                            withdrawalCount = withdrawalCountSet.getInt("count(*)");
                        }
                        PreparedStatement withdrawalSumStmt = conn.prepareStatement("select sum(withdrawal_amount) from withdrawals where withdrawal_date >= add_months(sysdate, -12)");
                        ResultSet withdrawalSumSet = withdrawalSumStmt.executeQuery();
                        if(withdrawalSumSet.next()){
                            withdrawalSum = withdrawalSumSet.getDouble("sum(withdrawal_amount)");
                        }


                        PreparedStatement depositsCountStmt = conn.prepareStatement("select count(*) from deposits where deposit_date >= add_months(sysdate, -12)"); 
                        ResultSet depositsCountSet = depositsCountStmt.executeQuery();
                        if(depositsCountSet.next()){
                            depositCount = depositsCountSet.getInt("count(*)");
                        }
                        PreparedStatement depositSumStmt = conn.prepareStatement("select sum(deposit_amount) from deposits where deposit_date >= add_months(sysdate, -12)");
                        ResultSet depositSumSet = depositSumStmt.executeQuery();
                        if(depositSumSet.next()){
                            depositSum = depositSumSet.getDouble("sum(deposit_amount)");
                        }

                        PreparedStatement transfersCountStmt = conn.prepareStatement("select count(*) from fundtransfers where transfer_date >= add_months(sysdate, -12)"); 
                        ResultSet transferCountSet = transfersCountStmt.executeQuery();
                        if(transferCountSet.next()){
                            fundTransferCount = transferCountSet.getInt("count(*)");
                        }
                        PreparedStatement transferSumStmt = conn.prepareStatement("select sum(transfer_amount) from fundtransfers where transfer_date >= add_months(sysdate, -12)");
                        ResultSet transferSumSet = transferSumStmt.executeQuery();
                        if(transferSumSet.next()){
                            fundTransferSum = transferSumSet.getDouble("sum(transfer_amount)");
                        }

                        totalCount = paymentCount + purchaseCount + withdrawalCount + depositCount + cardPaymentCount + fundTransferCount;
                        totalSum = paymentSum + purchaseSum + withdrawalSum + depositSum + cardTotalSum + fundTransferSum;

                        System.out.println();
                        System.out.println("Within the last 12 months, on all active cards/accounts: ");
                        System.out.println();
                        System.out.println("There have been " + paymentCount + " loan payments, totaling $" + paymentSum);
                        System.out.println("There have been " + purchaseCount + " card purchases, totaling $" + purchaseSum);
                        System.out.println("There have been " + withdrawalCount + " withdrawals, totaling $" + withdrawalSum);
                        System.out.println("There have been " + depositCount + " deposits, totaling $" + depositSum);
                        System.out.println("There have been " + cardPaymentCount + " card balance payments, totaling $" + cardTotalSum);
                        System.out.println("There have been " + fundTransferCount + " fund transfers, totaling $" + fundTransferSum);
                        System.out.println("This results in " + (int)totalCount + " total transactions, totaling $" + totalSum + " for the year.");
                        System.out.println("Monthly average: " + String.format("%.2f", totalCount / 12.0) + " transactions and $" + String.format("%.2f", totalSum / 12.0) + " in dollar volume.");
                        System.out.println("Daily average: " + String.format("%.2f", totalCount / 365.0) + " transactions and $" + String.format("%.2f", totalSum / 365.0) + " in dollar volume.");
                        System.out.println("---------------------------------------------------");
                        System.out.println();
                    break;

                    case 4: //back to main menu
                        System.out.println("Returning to main menu...");
                        printMainMenu();
                        return;
                }
            }

        }catch(SQLException se){
            se.printStackTrace();
            System.out.println("[Error]: Connection error. Re-enter login data");
        }
    }

    public static void customerMenu(Connection conn){
        Scanner scan = new Scanner(System.in);
        try{
            boolean IDLoopNeeded = true;
            String inputID = "";
            System.out.println();
            PreparedStatement customerIDStatement = conn.prepareStatement("select customer_id from customer");
            ResultSet customerIDSet = customerIDStatement.executeQuery();
            List<String> dum = new ArrayList<>();
            List<String> customerIDs = new ArrayList<>();
            while(customerIDSet.next()){
                dum.add(customerIDSet.getString("customer_id"));
            }
            for(int i = 0; i < dum.size(); i++){
                customerIDs.add(dum.get(i).trim());
            }
            System.out.println("Please input your Customer ID from the list below, or 'e' to return to the main menu.");
            for(int i = 0; i < customerIDs.size(); i++){
                System.out.print("(" + customerIDs.get(i).trim() + ")  ");
            }
            System.out.println();
            System.out.println();
            System.out.print("Choice: ");
            while(IDLoopNeeded){
                String dummy = scan.next();
                if(customerIDs.contains(dummy.trim())){
                    inputID = dummy;
                    System.out.println("Success! Signing you in...");
                    IDLoopNeeded = false;
                }else if(dummy.trim().equals("e")){
                    System.out.println("Returning you to main menu...");
                    printMainMenu();
                    return;
                }else{
                    System.out.println("Invalid Customer ID inputted. Please input your Customer ID from the given list.");
                    String junk = scan.nextLine();
                    continue;
                }
            }

            boolean customerMenuLoop = true;
            System.out.println("Welcome to the Customer menu! Please select a menu option below.");
            System.out.println();
            while(customerMenuLoop){
                System.out.println("\t 1) View account information"); //implemented
                System.out.println("\t 2) View all card information"); //implemented
                System.out.println("\t 3) View all loan information"); //implemented
                System.out.println("\t 4) Make payment on a loan"); //implemented
                System.out.println("\t 5) Make payment on a credit card");
                System.out.println("\t 6) Make a deposit"); //implemented
                System.out.println("\t 7) Make a withdrawal"); //implemented
                System.out.println("\t 8) Open new account"); //implemented
                System.out.println("\t 9) Obtain new or replacement card"); //implemented
                System.out.println("\t 10) Take out a new loan"); //implemented
                System.out.println("\t 11) Make a card purchase"); //implemented
                System.out.println("\t 12) Fund transfer"); //implemented
                System.out.println("\t 13) Return to main menu"); //implemented
                int customerOption = 0;
                boolean customerOptionLoop = true;
                while(customerOptionLoop){
                    if(scan.hasNextInt()){
                        int dummy = scan.nextInt();
                        if((dummy == 1 || dummy == 2 || dummy == 3 || dummy == 4 || dummy == 5 || dummy == 6 || dummy == 7 || dummy == 8 || dummy == 9 || dummy == 10 || dummy == 11 || dummy == 12 || dummy == 13)){
                            customerOption = dummy;
                            customerOptionLoop = false;
                        }else {
                            System.out.println("Invalid input. Please enter either a menu option 1-13.");
                            continue;
                        }
                    }else {
                        System.out.println("Invalid input. Please enter either a menu option 1-13.");
                        String junk = scan.nextLine(); //junk variable to clear line in the case of mistyped input
                        continue;
                    }
                }
                //switch to handle customer's input 
                switch(customerOption){
                    case 1: //view account information
                        PreparedStatement accountCustomer = conn.prepareStatement("select * from account inner join customer on account.customer_id = customer.customer_id where TRIM(customer.customer_id) = ?");
                        accountCustomer.setString(1, inputID.trim());
                        ResultSet accountCustomerSet = accountCustomer.executeQuery();
                        if(!accountCustomerSet.isBeforeFirst()){
                            System.out.println("No accounts listed under Customer ID: " + inputID);
                            System.out.println();
                            break;
                        }
                        System.out.println();
                        System.out.println("Printing all account(s) information related to Customer ID: " + inputID);
                        System.out.println();
                        while(accountCustomerSet.next()){
                            System.out.println("Name: " + accountCustomerSet.getString("name"));
                            System.out.println("Customer ID: " + accountCustomerSet.getString("customer_id"));
                            System.out.println("Account ID: " +  accountCustomerSet.getString("account_id"));
                            System.out.println("Manager ID (manager linked to account): " + accountCustomerSet.getString("manager_id"));
                            System.out.println("Account Type: " + accountCustomerSet.getString("account_type"));
                            System.out.println("Account Balance: $" + accountCustomerSet.getDouble("acct_balance"));
                            System.out.println("Interest rate: " + accountCustomerSet.getDouble("interest_rate") + "%");
                            if(accountCustomerSet.getString("account_type").equals("Investment")) {
                                System.out.println("Asset List: " + accountCustomerSet.getString(("asset_list")));
                            }
                            if(accountCustomerSet.getString("account_type").equals("Savings")){
                                System.out.println("Minimum Balance: $" + accountCustomerSet.getDouble("min_balance"));
                            }
                            System.out.println("---------------------------------------------------");
                        }
                        System.out.println();
                    break;

                    case 2: //View all card information
                        PreparedStatement accountCredit = conn.prepareStatement("select * from creditcard inner join card on card.card_number = creditcard.card_number inner join account on card.account_id = account.account_id where TRIM(account.customer_id) = ?");
                        accountCredit.setString(1, inputID);
                        ResultSet accountCreditSet = accountCredit.executeQuery();

                        PreparedStatement accountDebit = conn.prepareStatement("select * from debitcard inner join card on card.card_number = debitcard.card_number inner join account on card.account_id = account.account_id where TRIM(account.customer_id) = ?");
                        accountDebit.setString(1, inputID);
                        ResultSet accountDebitSet = accountDebit.executeQuery();

                        if(!accountCreditSet.isBeforeFirst() && !accountDebitSet.isBeforeFirst()){
                            System.out.println("No cards listed under Customer ID: " + inputID);
                            System.out.println();
                            break;
                        }
                        System.out.println();
                        System.out.println("Displaying all card(s) attached to Customer ID: " + inputID);
                        System.out.println();
                        while(accountCreditSet.next()){
                            System.out.println("Card number: " + accountCreditSet.getString("card_number"));
                            System.out.println("Card type: Credit");
                            System.out.println("Balance due: $" + accountCreditSet.getDouble("balance_due"));
                            System.out.println("Credit limit: $" + accountCreditSet.getDouble("credit_limit"));
                            System.out.println("Interest rate: " + accountCreditSet.getDouble("interest_rate") + "%");
                            System.out.println("Running balance: $" + accountCreditSet.getDouble("running_balance"));
                            System.out.println("Minimum monthly payment: $" + accountCreditSet.getDouble("monthly_payment"));
                            System.out.println("---------------------------------------------------");
                        }

                        while(accountDebitSet.next()){
                            System.out.println("Card number: " + accountDebitSet.getString("card_number"));
                            System.out.println("Card type: Debit");
                            System.out.println("Card balance: $" + accountDebitSet.getDouble("acct_balance"));
                            System.out.println("---------------------------------------------------");
                        }

                    break;

                    case 3: //view all loan informaiton
                        PreparedStatement accountLoans = conn.prepareStatement("select * from loans inner join account on loans.account_id = account.account_id inner join customer on customer.customer_id = account.customer_id where TRIM(customer.customer_id) = ?");
                        accountLoans.setString(1, inputID);
                        ResultSet accountLoanSet = accountLoans.executeQuery();
                        if(!accountLoanSet.isBeforeFirst()){
                            System.out.println("No loans listed under Customer ID: " + inputID);
                            System.out.println();
                            break;
                        }
                        System.out.println();
                        System.out.println("Displaying all loans attached to Customer ID: " + inputID);
                        System.out.println();
                        while(accountLoanSet.next()){
                            System.out.println("Loan ID: " + accountLoanSet.getString("loan_id"));
                            System.out.println("Account ID: " + accountLoanSet.getString("account_id"));
                            System.out.println("Loan Reason: " + accountLoanSet.getString("loan_reason"));
                            System.out.println("Loan Type: " + accountLoanSet.getString("loan_type"));
                            System.out.println("Loan Amount: $" + accountLoanSet.getDouble("loan_amount"));
                            System.out.println("Monthly Payment: $" + accountLoanSet.getDouble("monthly_pay"));
                            System.out.println("Interest Rate: " +  accountLoanSet.getDouble("interest_rate") + "%");
                            if(accountLoanSet.getDouble("loan_amount") > 0.0){
                                System.out.println("Loan Status: Not Paid Off/Open");
                            }else{
                                System.out.println("Loan Status: Paid Off/Closed");
                            }
                            System.out.println("----------------------------------------------");
                        }
                        System.out.println();
                        PreparedStatement accountLoanSum = conn.prepareStatement("select sum(loan_amount) from loans inner join account on loans.account_id = account.account_id inner join customer on customer.customer_id = account.customer_id where TRIM(customer.customer_id) = ?");
                        accountLoanSum.setString(1, inputID);
                        ResultSet accountLoanSumSet = accountLoanSum.executeQuery();
                        if(accountLoanSumSet.next()){
                            System.out.println("Total sum of loans under Customer ID: " + inputID + " is: $" + accountLoanSumSet.getDouble("sum(loan_amount)"));
                        }
                        System.out.println();
                    break;

                    case 4: //make a payment on a loan
                    
                        PreparedStatement loanIDSetPay = conn.prepareStatement("select loan_id from loans inner join account on loans.account_id = account.account_id where TRIM(account.customer_id) = ? and loan_amount > 0.0");
                        loanIDSetPay.setString(1, inputID);
                        ResultSet loanIDsSet = loanIDSetPay.executeQuery();
            
                        List<String> loanIDsForPay = new ArrayList<>();
                        while(loanIDsSet.next()){
                            loanIDsForPay.add(loanIDsSet.getString("loan_id").trim());
                        }
            
                        if(loanIDsForPay.isEmpty()){
                            System.out.println("No loans attached to the Customer ID: " + inputID + " with an amount greater than $0. Cancelling loan payment.");
                            System.out.println();
                            break;
                        }

                        PreparedStatement getLoanInfo = conn.prepareStatement(("select * from loans inner join account on loans.account_id = account.account_id where TRIM(account.customer_id) = ? and loan_amount > 0.0"));
                        getLoanInfo.setString(1, inputID);
                        ResultSet getLoanInfoSet = getLoanInfo.executeQuery();

                        System.out.println();
                        System.out.println("Printing all loans attached to Customer ID: " + inputID);
                        System.out.println();
                        while(getLoanInfoSet.next()){
                            System.out.println("Loan ID: " + getLoanInfoSet.getString("loan_id"));
                            System.out.println("Loan amount remaining: $" + getLoanInfoSet.getDouble("loan_amount"));
                            System.out.println("Monthly payment on loan: $" + getLoanInfoSet.getDouble("monthly_pay"));
                            System.out.println("----------------------------------------");
                        }
                        System.out.println();

                        System.out.println("Please input the Loan ID attached to the loan you would like to make a payment on: ");
                        for(int i = 0; i < loanIDsForPay.size(); i++){
                            System.out.print("(" + loanIDsForPay.get(i).trim() + ")   " );
                        }

                        boolean loanPayIDLoop = true;
                        String loanIDToPay = "";
                        System.out.println();
                        System.out.print("Choice: ");
                        while(loanPayIDLoop){
                            String dummy = scan.next();
                            if(loanIDsForPay.contains(dummy.trim())){
                                loanIDToPay = dummy;
                                System.out.println("Successful ID inputted!");
                                loanPayIDLoop = false;
                            }else{
                                System.out.println("Invalid Loan ID inputted. Please input the Loan ID from the given list.");
                                continue;
                            }
                        }

                        PreparedStatement getLoanInfoForPay = conn.prepareStatement("select * from loans where TRIM(loan_id) = ?");
                        getLoanInfoForPay.setString(1, loanIDToPay);
                        ResultSet getLoanPayInfoSet = getLoanInfoForPay.executeQuery();
                
                        //loan ID: loanIDToPay
                        double currentLoanAmount = 0.0;
                        double loanMonthlyPay = 0.0;
                        String accountIDForLoanPayment = "";
                        while(getLoanPayInfoSet.next()){
                            currentLoanAmount = getLoanPayInfoSet.getDouble("loan_amount");
                            loanMonthlyPay = getLoanPayInfoSet.getDouble("monthly_pay");
                            accountIDForLoanPayment = getLoanPayInfoSet.getString("account_id").trim();
                        }

                        if(currentLoanAmount == 0.0){
                            System.out.println("Loan is already paid off.");
                            break;
                        }

                        PreparedStatement getAccountForLoanPay = conn.prepareStatement("select * from account where TRIM(account_id) = ?");
                        getAccountForLoanPay.setString(1, accountIDForLoanPayment);
                        ResultSet accountForLoanSet = getAccountForLoanPay.executeQuery();

                        String loanPayAccountType = "";
                        double loanPayMinBal = 0.0;
                        double currentBalanceAccountLoanPay = 0.0;
                        while(accountForLoanSet.next()){
                            loanPayAccountType = accountForLoanSet.getString("account_type");
                            currentBalanceAccountLoanPay = accountForLoanSet.getDouble("acct_balance");
                            if(loanPayAccountType.equals("Savings")){
                                loanPayMinBal = accountForLoanSet.getDouble("min_balance");
                            }
                        }

                        //loan ID: loanIDToPay
                        //double currentLoanAmount = 0.0;
                        //double loanMonthlyPay = 0.0;

                        System.out.println();
                        System.out.println("Printing attached account information: ");
                        System.out.println("Account ID: " + accountIDForLoanPayment);
                        System.out.println("Account type: " + loanPayAccountType);
                        System.out.println("Account balance: $" + currentBalanceAccountLoanPay);
                        if(loanPayAccountType.equals("Savings")){
                            System.out.println("Minimum balance required: $" + loanPayMinBal);
                        }
                        System.out.println();
                        System.out.println("Re-printing loan information: ");
                        System.out.println("Loan ID: " + loanIDToPay);
                        System.out.println("Loan amount remaining: $" + currentLoanAmount);
                        System.out.println("Monthly minimum payment on loan: $" + loanMonthlyPay);

                        System.out.println();
                        System.out.println("Enter the amount you would like to pay towards your loan? Must be greater or equal to your usual monthly payment: ");
                        double loanPaymentAmount = 0.0;
                        boolean loanPaymentAmountLoop = true;
                        
                        
                        // was written using Cursor
                        while (loanPaymentAmountLoop) {
                            if (scan.hasNextDouble()) {
                                double dummy = scan.nextDouble();
                                
                                // Check if payment amount is valid
                                if (dummy <= 0) {
                                    System.out.println("Invalid input. Payment amount must be greater than $0.");
                                    scan.nextLine();
                                    continue;
                                }
                                
                                if (dummy > currentLoanAmount) {
                                    System.out.println("Invalid input. Payment cannot exceed remaining loan amount: $" + currentLoanAmount);
                                    scan.nextLine();
                                    continue;
                                }
                                
                                // Special case: if remaining loan is less than monthly payment
                                if (currentLoanAmount < loanMonthlyPay) {
                                    if (dummy != currentLoanAmount) {
                                        System.out.println("Invalid input. When remaining balance ($" + currentLoanAmount + 
                                                        ") is less than monthly payment ($" + loanMonthlyPay + 
                                                        "), you must pay the exact remaining balance.");
                                        scan.nextLine();
                                        continue;
                                    }
                                } else {
                                    // Normal case: check if payment meets minimum monthly payment
                                    if (dummy < loanMonthlyPay) {
                                        System.out.println("Invalid input. Payment must be at least the monthly payment amount: $" + loanMonthlyPay);
                                        scan.nextLine();
                                        continue;
                                    }
                                }
                                
                                // Payment is valid
                                loanPaymentAmount = dummy;
                                loanPaymentAmountLoop = false;
                                
                            } else {
                                System.out.println("Invalid input. Please enter a valid numeric payment amount.");
                                scan.nextLine(); // Clear the invalid input
                            }
                        }



                        double newLoanBalance = currentLoanAmount - loanPaymentAmount;
                        double newAccountBalAfterLoan = currentBalanceAccountLoanPay - loanPaymentAmount;

                        if(newAccountBalAfterLoan < 0 && loanPayAccountType.equals("Checking")){
                            System.out.println("Error: loan payment would make checking account balance negative. Cancelling loan payment.");
                        }else{
                            System.out.println();
                            System.out.println("Printing loan payment information: ");
                            System.out.println();
                            System.out.println("Account ID: " + accountIDForLoanPayment);
                            System.out.println("Account type: " + loanPayAccountType);
                            System.out.println("Loan ID: " + loanIDToPay);
                            System.out.println("Loan payment amount: $" + loanPaymentAmount);
                            System.out.println("Loan amount remaining after payment: $" + String.format("%.2f", newLoanBalance));
                            System.out.println("New account balance after payment: $" + String.format("%.2f", newAccountBalAfterLoan));
                            System.out.println();
                            System.out.println("Would you like to accept this loan payment? Enter Y for Yes or N for No");
                            if(loanPayAccountType.equals("Savings")){
                                System.out.println("Reminder: a $35 penalty will be added to your account if you are below your minimum balance of $" + loanPayMinBal);
                            }
                            System.out.println();
                            System.out.println("Choice: ");
                            boolean newLoanPayAcceptLoop = true;
                            String newLoanPayChoice = "";
                            while(newLoanPayAcceptLoop){
                                String dummy = scan.next();
                                if(dummy.equals("Y") || dummy.equals("N")){
                                    newLoanPayChoice = dummy;
                                    newLoanPayAcceptLoop = false;
                                }else{
                                    System.out.println("Invalid input. Please enter either Y or N");
                                    continue;
                                }
                            }

                            String newLoanPaymentID = "";
                            if(newLoanPayChoice.equals("Y")){
                                boolean loanPayCheck = true;
                                PreparedStatement getLPIDStmt = conn.prepareStatement(("select payment_id from loanpayments"));
                                ResultSet getLPIDSet = getLPIDStmt.executeQuery();
                                List<String> allLPIDs = new ArrayList<>();
                                while(getLPIDSet.next()){
                                    allLPIDs.add(getLPIDSet.getString("payment_id").trim());
                                }
                                Random random = new Random();
                                while(loanPayCheck){
                                    int LPIDBase = 10000 + random.nextInt(100000 - 10000);
                                    String newLPID = "P" + LPIDBase;
                                    if(!allLPIDs.contains(newLPID)){
                                        if(loanPayAccountType.equals("Checking")){
                                            PreparedStatement insertLPWithCheck = conn.prepareStatement("insert into loanpayments(loan_id, payment_id, payment_amount, payment_date) values (?, ?, ?, ?)");
                                            insertLPWithCheck.setString(1, loanIDToPay);
                                            insertLPWithCheck.setString(2, newLPID);
                                            insertLPWithCheck.setDouble(3, loanPaymentAmount);
                                            insertLPWithCheck.setDate(4, Date.valueOf(LocalDate.now()));
                                            insertLPWithCheck.executeUpdate();

                                            PreparedStatement updateLoanAccountWithCheck = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                            updateLoanAccountWithCheck.setDouble(1, newAccountBalAfterLoan);
                                            updateLoanAccountWithCheck.setString(2, accountIDForLoanPayment);
                                            updateLoanAccountWithCheck.executeUpdate();

                                            PreparedStatement updateLoanWithCheck = conn.prepareStatement("update loans set loan_amount = ? where UPPER(TRIM(loan_id)) = UPPER(TRIM(?))");
                                            updateLoanWithCheck.setDouble(1, newLoanBalance);
                                            updateLoanWithCheck.setString(2, loanIDToPay);
                                            updateLoanWithCheck.executeUpdate();

                                            System.out.println("Loan payment successful!");
                                            System.out.println();

                                            loanPayCheck = false;
                                        }else if(loanPayAccountType.equals("Savings") && newAccountBalAfterLoan < loanPayMinBal){
                                            newAccountBalAfterLoan = newAccountBalAfterLoan - 35.00;
                                            PreparedStatement insertLPWithSav = conn.prepareStatement("insert into loanpayments(loan_id, payment_id, payment_amount, payment_date) values (?, ?, ?, ?)");
                                            insertLPWithSav.setString(1, loanIDToPay);
                                            insertLPWithSav.setString(2, newLPID);
                                            insertLPWithSav.setDouble(3, loanPaymentAmount);
                                            insertLPWithSav.setDate(4, Date.valueOf(LocalDate.now()));
                                            insertLPWithSav.executeUpdate();

                                            PreparedStatement updateLoanAccountWithSav = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                            updateLoanAccountWithSav.setDouble(1, newAccountBalAfterLoan);
                                            updateLoanAccountWithSav.setString(2, accountIDForLoanPayment);
                                            updateLoanAccountWithSav.executeUpdate();

                                            PreparedStatement updateLoanWithSav = conn.prepareStatement("update loans set loan_amount = ? where UPPER(TRIM(loan_id)) = UPPER(TRIM(?))");
                                            updateLoanWithSav.setDouble(1, newLoanBalance);
                                            updateLoanWithSav.setString(2, loanIDToPay);
                                            updateLoanWithSav.executeUpdate();

                                            System.out.println("Loan payment successful! Since your account balance is below your minimum balance, a $35 penalty was carried out.");
                                            System.out.println();

                                            loanPayCheck = false;
                                        }else if(loanPayAccountType.equals("Savings") && newAccountBalAfterLoan > loanPayMinBal){
                                            PreparedStatement insertLPWithSavOK = conn.prepareStatement("insert into loanpayments(loan_id, payment_id, payment_amount, payment_date) values (?, ?, ?, ?)");
                                            insertLPWithSavOK.setString(1, loanIDToPay);
                                            insertLPWithSavOK.setString(2, newLPID);
                                            insertLPWithSavOK.setDouble(3, loanPaymentAmount);
                                            insertLPWithSavOK.setDate(4, Date.valueOf(LocalDate.now()));
                                            insertLPWithSavOK.executeUpdate();

                                            PreparedStatement updateLoanAccountWithSavOK = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                            updateLoanAccountWithSavOK.setDouble(1, newAccountBalAfterLoan);
                                            updateLoanAccountWithSavOK.setString(2, accountIDForLoanPayment);
                                            updateLoanAccountWithSavOK.executeUpdate();

                                            PreparedStatement updateLoanWithSavOK = conn.prepareStatement("update loans set loan_amount = ? where UPPER(TRIM(loan_id)) = UPPER(TRIM(?))");
                                            updateLoanWithSavOK.setDouble(1, newLoanBalance);
                                            updateLoanWithSavOK.setString(2, loanIDToPay);
                                            updateLoanWithSavOK.executeUpdate();

                                            System.out.println("Loan payment successful!");
                                            System.out.println();

                                            loanPayCheck = false;
                                        }
                                    }
                                }
                            }else{
                                System.out.println("Cancelling loan payment.");
                                System.out.println();
                            }
                        }
                    break;

                    

                    case 5: //pay off credit card
                        PreparedStatement ccNumberStmt = conn.prepareStatement("select creditcard.card_number from creditcard inner join card on creditcard.card_number = card.card_number inner join account on card.account_id = account.account_id where TRIM(account.customer_id) = ? and creditcard.balance_due > 0.0");
                        ccNumberStmt.setString(1, inputID);
                        ResultSet ccNumberSet = ccNumberStmt.executeQuery();

                        List<String> ccNumbersForPay = new ArrayList<>();
                        while(ccNumberSet.next()){
                            ccNumbersForPay.add(ccNumberSet.getString("card_number").trim());
                        }

                        if(ccNumbersForPay.isEmpty()){
                            System.out.println("No credit cards attached to the Customer ID: " + inputID + " where balance due is greater than $0. Cancelling credit card payment.");
                            System.out.println();
                            break;
                        }

                        PreparedStatement getCardInfoPay = conn.prepareStatement("select * from creditcard inner join card on creditcard.card_number = card.card_number inner join account on card.account_id = account.account_id where TRIM(account.customer_id) = ? and creditcard.balance_due > 0.0"); 
                        getCardInfoPay.setString(1, inputID);
                        ResultSet getCardInfoPaySet = getCardInfoPay.executeQuery();

                        System.out.println();
                        System.out.println("Printing out all credit cards attached to Customer ID: " + inputID);
                        System.out.println();
                        while(getCardInfoPaySet.next()){
                            System.out.println("Card number: " + getCardInfoPaySet.getString("card_number"));
                            System.out.println("Balance due: $" + getCardInfoPaySet.getDouble("balance_due"));
                            System.out.println("Running balance: $" + getCardInfoPaySet.getDouble("running_balance"));
                            System.out.println("Credit limit: $" + getCardInfoPaySet.getDouble("credit_limit"));
                            System.out.println("Interest rate: " + getCardInfoPaySet.getDouble("interest_rate") + "%");
                            System.out.println("---------------------------------------------");

                        }
                        System.out.println();

                        System.out.println("Please input the card number attached to the card you would like to make a payment on: ");
                        for(int i = 0; i < ccNumbersForPay.size(); i++){
                            System.out.print("(" + ccNumbersForPay.get(i).trim() + ")   " );
                        }

                        boolean ccPayIDLoop = true;
                        String ccNumberToPay = "";
                        System.out.println();
                        System.out.print("Choice: ");
                        while(ccPayIDLoop){
                            String dummy = scan.next();
                            if(ccNumbersForPay.contains(dummy.trim())){
                                ccNumberToPay = dummy;
                                System.out.println("Successful card number inputted!");
                                ccPayIDLoop = false;
                            }else{
                                System.out.println("Invalid card number inputted. Please input the card number from the given list.");
                                continue;
                            }
                        }

                        //card number to be paid off: ccNumberToPay

                        PreparedStatement getCardInfoForPay = conn.prepareStatement("select * from creditcard inner join card on creditcard.card_number = card.card_number where TRIM(creditcard.card_number) = ?");
                        getCardInfoForPay.setString(1, ccNumberToPay);
                        ResultSet getCardInfoForPaySet = getCardInfoForPay.executeQuery();

                        double currentCardBalance = 0.0;
                        double currentCardRunningBalance = 0.0;
                        String accountIDForCardPayment = "";
                        double minMonthlyPayCardPay = 0.0;
                        while(getCardInfoForPaySet.next()){
                            currentCardBalance = getCardInfoForPaySet.getDouble("balance_due");
                            currentCardRunningBalance = getCardInfoForPaySet.getDouble("running_balance");
                            accountIDForCardPayment = getCardInfoForPaySet.getString("account_id").trim();
                            minMonthlyPayCardPay = getCardInfoForPaySet.getDouble("monthly_payment");
                        }

                        if(currentCardBalance < 0.0){
                            System.out.println("Error: can not make payments on cards with no balance due.");
                            break;
                        }
                        
                        PreparedStatement getAccountForCardPay = conn.prepareStatement(("select * from account where TRIM(account_id) = ?"));
                        getAccountForCardPay.setString(1, accountIDForCardPayment);
                        ResultSet getAccountForCardPaySet = getAccountForCardPay.executeQuery();

                        String cardPayAccountType = "";
                        double cardPayMinBal = 0.0;
                        double currentActBalCardPay = 0.0;
                        while(getAccountForCardPaySet.next()){
                            cardPayAccountType = getAccountForCardPaySet.getString("account_type");
                            currentActBalCardPay = getAccountForCardPaySet.getDouble("acct_balance");
                            if(cardPayAccountType.equals("Savings")){
                                cardPayMinBal = getAccountForCardPaySet.getDouble("min_balance");
                            }
                        }

                        System.out.println();
                        System.out.println("Printing attached account information: ");
                        System.out.println("Account ID: " + accountIDForCardPayment);
                        System.out.println("Account type: " + cardPayAccountType);
                        System.out.println("Account balance: $" + currentActBalCardPay);
                        if(cardPayAccountType.equals("Savings")){
                            System.out.println("Minimum balance required: $" + cardPayMinBal);
                        }
                        System.out.println();
                        System.out.println("Re-printing card information: ");
                        System.out.println("Card number: " + ccNumberToPay);
                        System.out.println("Card balance: $" + currentCardBalance);
                        System.out.println("Card running balance: $" + currentCardRunningBalance);
                        System.out.println("Standard minimum monthly payment: $" + minMonthlyPayCardPay);

                        System.out.println();
                        System.out.println("Enter the amount you would like to pay towards your card balance: ");
                        double cardPaymentAmount = 0.0;
                        boolean cardPayAmountLoop = true;


                        //below was written by Cursor
                        while (cardPayAmountLoop) {
                            if (scan.hasNextDouble()) {
                                double dummy = scan.nextDouble();
                                
                                // Check if payment amount is valid
                                if (dummy <= 0) {
                                    System.out.println("Invalid input. Payment amount must be greater than $0.");
                                    scan.nextLine();
                                    continue;
                                }
                                
                                if (dummy > currentCardBalance) {
                                    System.out.println("Invalid input. Payment cannot exceed current balance due: $" + currentCardBalance);
                                    scan.nextLine();
                                    continue;
                                }
                                
                                // Special case: if balance is less than monthly minimum
                                if (currentCardBalance < minMonthlyPayCardPay) {
                                    if (dummy != currentCardBalance) {
                                        System.out.println("Invalid input. When balance ($" + currentCardBalance + 
                                                        ") is less than monthly minimum ($" + minMonthlyPayCardPay + 
                                                        "), you must pay the exact remaining balance.");
                                        scan.nextLine();
                                        continue;
                                    }
                                } else {
                                    // Normal case: check if payment meets minimum monthly payment
                                    if (dummy < minMonthlyPayCardPay) {
                                        System.out.println("Invalid input. Payment must be at least the monthly minimum payment amount: $" + minMonthlyPayCardPay);
                                        scan.nextLine();
                                        continue;
                                    }
                                }
                                
                                // Payment is valid
                                cardPaymentAmount = dummy;
                                cardPayAmountLoop = false;
                                
                            } else {
                                System.out.println("Invalid input. Please enter a valid numeric payment amount.");
                                scan.nextLine(); // Clear the invalid input
                            }
                        }

                        double newCardBalanceAfterPay = currentCardBalance - cardPaymentAmount;
                        double newCardRunningLimit = currentCardRunningBalance - cardPaymentAmount;
                        double newAccountBalanceAfterPay = currentActBalCardPay - cardPaymentAmount;

                        if(newAccountBalanceAfterPay < 0 && cardPayAccountType.equals("Checking")){
                            System.out.println("Error: card balance payment would make checking account balance negative. Cancelling credit card balance payment.");
                        }else{
                            System.out.println();
                            System.out.println("Printing credit card balance payment information: ");
                            System.out.println();
                            System.out.println("Account ID: " + accountIDForCardPayment);
                            System.out.println("Account type: " + cardPayAccountType);
                            System.out.println("Card number: " + ccNumberToPay);
                            System.out.println("Credit card balance payment amount: $" + String.format("%.2f", cardPaymentAmount));
                            System.out.println("Credit card balance after payment: $" + String.format("%.2f", newCardBalanceAfterPay));
                            System.out.println("Running balance after payment: $"+ String.format("%.2f", newCardRunningLimit));
                            System.out.println("New account balance after payment: $" + String.format("%.2f", newAccountBalanceAfterPay));
                            System.out.println();
                            System.out.println("Would you like to accept this credit card balance payment? Enter Y for Yes or N for No");
                            if(cardPayAccountType.equals("Savings")){
                                System.out.println("Reminder: a $35 penalty will be added to your account if you are below your minimum balance of $" + cardPayMinBal);
                            }
                            System.out.println();
                            System.out.println("Choice: ");
                            boolean newCardPayAcceptLoop = true;
                            String newCardPayChoice = "";
                            while(newCardPayAcceptLoop){
                                String dummy = scan.next();
                                if(dummy.equals("Y") || dummy.equals("N")){
                                    newCardPayChoice = dummy;
                                    newCardPayAcceptLoop = false;
                                }else{
                                    System.out.println("Invalid input. Please enter either Y or N");
                                    continue;
                                }
                            }

                            String newCCPaymentID = "";
                            if(newCardPayChoice.equals("Y")){
                                boolean cardPayCheck = true;
                                PreparedStatement getPIDStmt = conn.prepareStatement(("select CPAYMENT_ID from CardPayments"));
                                ResultSet getPIDSet = getPIDStmt.executeQuery();
                                List<String> allPIDs = new ArrayList<>();
                                while(getPIDSet.next()){
                                    allPIDs.add(getPIDSet.getString("CPAYMENT_ID").trim());
                                }
                                Random random = new Random();
                                while(cardPayCheck){
                                    int PIDBase = 10000 + random.nextInt(100000 - 10000);
                                    String pid = "CP" + PIDBase;
                                    if(!allPIDs.contains(pid)){
                                        if(cardPayAccountType.equals("Checking")){
                                            PreparedStatement insertCPWithCheck = conn.prepareStatement(("insert into CardPayments(card_number, CPayment_ID, CPayment_Amount, CPayment_Date) values (?, ?, ?, ?)"));
                                            insertCPWithCheck.setString(1, ccNumberToPay);
                                            insertCPWithCheck.setString(2, pid);
                                            insertCPWithCheck.setDouble(3, cardPaymentAmount);
                                            insertCPWithCheck.setDate(4, Date.valueOf(LocalDate.now()));
                                            insertCPWithCheck.executeUpdate();

                                            PreparedStatement updateCardAccountWithCheck = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                            updateCardAccountWithCheck.setDouble(1, newAccountBalanceAfterPay);
                                            updateCardAccountWithCheck.setString(2, accountIDForCardPayment);
                                            updateCardAccountWithCheck.executeUpdate();

                                            PreparedStatement updateCardWithCheck = conn.prepareStatement(("update creditcard set balance_due = ?, running_balance = ? where UPPER(TRIM(card_number)) = UPPER(TRIM(?))"));
                                            updateCardWithCheck.setDouble(1, newCardBalanceAfterPay);
                                            updateCardWithCheck.setDouble(2, newCardRunningLimit);
                                            updateCardWithCheck.setString(3, ccNumberToPay);
                                            updateCardWithCheck.executeUpdate();

                                            System.out.println("Card balance payment successful!");
                                            System.out.println();

                                            cardPayCheck = false;

                                        }else if(cardPayAccountType.equals("Savings") && newAccountBalanceAfterPay < 0){
                                            newAccountBalanceAfterPay = newAccountBalanceAfterPay - 35.00;
                                            PreparedStatement insertCPWithSav = conn.prepareStatement(("insert into CardPayments(card_number, CPayment_ID, CPayment_Amount, CPayment_Date) values (?, ?, ?, ?)"));
                                            insertCPWithSav.setString(1, ccNumberToPay);
                                            insertCPWithSav.setString(2, pid);
                                            insertCPWithSav.setDouble(3, cardPaymentAmount);
                                            insertCPWithSav.setDate(4, Date.valueOf(LocalDate.now()));
                                            insertCPWithSav.executeUpdate();

                                            PreparedStatement updateCardAccountWithSav = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                            updateCardAccountWithSav.setDouble(1, newAccountBalanceAfterPay);
                                            updateCardAccountWithSav.setString(2, accountIDForCardPayment);
                                            updateCardAccountWithSav.executeUpdate();

                                            PreparedStatement updateCardWithSav = conn.prepareStatement(("update creditcard set balance_due = ?, running_balance = ? where UPPER(TRIM(card_number)) = UPPER(TRIM(?))"));
                                            updateCardWithSav.setDouble(1, newCardBalanceAfterPay);
                                            updateCardWithSav.setDouble(2, newCardRunningLimit);
                                            updateCardWithSav.setString(3, ccNumberToPay);
                                            updateCardWithSav.executeUpdate();

                                            System.out.println("Card balance payment successful! Since your account balance is below your minimum balance, a $35 penalty was carried out.");
                                            System.out.println();

                                            cardPayCheck = false;
                                        }else if(cardPayAccountType.equals("Savings") && newAccountBalanceAfterPay > 0){
                                            PreparedStatement insertCPWithSavOK = conn.prepareStatement(("insert into CardPayments(card_number, CPayment_ID, CPayment_Amount, CPayment_Date) values (?, ?, ?, ?)"));
                                            insertCPWithSavOK.setString(1, ccNumberToPay);
                                            insertCPWithSavOK.setString(2, pid);
                                            insertCPWithSavOK.setDouble(3, cardPaymentAmount);
                                            insertCPWithSavOK.setDate(4, Date.valueOf(LocalDate.now()));
                                            insertCPWithSavOK.executeUpdate();

                                            PreparedStatement updateCardAccountWithSavOK = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                            updateCardAccountWithSavOK.setDouble(1, newAccountBalanceAfterPay);
                                            updateCardAccountWithSavOK.setString(2, accountIDForCardPayment);
                                            updateCardAccountWithSavOK.executeUpdate();

                                            PreparedStatement updateCardWithSavOK = conn.prepareStatement(("update creditcard set balance_due = ?, running_balance = ? where UPPER(TRIM(card_number)) = UPPER(TRIM(?))"));
                                            updateCardWithSavOK.setDouble(1, newCardBalanceAfterPay);
                                            updateCardWithSavOK.setDouble(2, newCardRunningLimit);
                                            updateCardWithSavOK.setString(3, ccNumberToPay);
                                            updateCardWithSavOK.executeUpdate();

                                            System.out.println("Card balance payment successful!");
                                            System.out.println();

                                            cardPayCheck = false;
                                        }
                                    }
                                }
                            }else if(newCardPayChoice.equals("N")){
                                System.out.println("Cancelling card payment.");
                                System.out.println();
                            }



                        }


                    break;


                    case 6: //make a deposit
                        PreparedStatement depositIDStmt = conn.prepareStatement("select account_id from account inner join customer on account.customer_id = customer.customer_id where TRIM(customer.customer_id) = ? ");
                        depositIDStmt.setString(1, inputID);
                        ResultSet depositSet = depositIDStmt.executeQuery();
            
                        List<String> depositAccountIDs = new ArrayList<>();
                        while(depositSet.next()){
                            depositAccountIDs.add(depositSet.getString("account_id").trim());
                        }

                        if(depositAccountIDs.isEmpty()){
                            System.out.println("No accounts attached to the Customer ID: " + inputID + ". Cancelling deposit.");
                            System.out.println();
                            break;
                        }
            
                        System.out.println("Please input your Account ID in which you would like to deposit the funds: ");
                        for(int i = 0; i < depositAccountIDs.size(); i++){
                            System.out.print("(" + depositAccountIDs.get(i).trim() + ")   " );
                        }

                        
            
                        boolean newDepositIDLoop = true;
                        String accountForDeposit = "";
                        System.out.println();
                        System.out.print("Choice: ");
                        while(newDepositIDLoop){
                            String dummy = scan.next();
                            if(depositAccountIDs.contains(dummy.trim())){
                                accountForDeposit = dummy;
                                System.out.println("Successful ID inputted!");
                                newDepositIDLoop = false;
                            }else{
                                System.out.println("Invalid Account ID inputted. Please input the Account ID from the given list.");
                                continue;
                            }
                        }

                        PreparedStatement getAccountDep = conn.prepareStatement(("select * from account where TRIM(account_id) = ?"));
                        getAccountDep.setString(1, accountForDeposit);
                        ResultSet getDepSet = getAccountDep.executeQuery();

                        String depAccountType = "";
                        double currentBalanceDep = 0.0;

                        while(getDepSet.next()){
                            depAccountType = getDepSet.getString("account_type");
                            currentBalanceDep = getDepSet.getDouble("acct_balance");
                        }

                        System.out.println();
                        System.out.println("Current balance in account: $" + currentBalanceDep);
                        System.out.println();

                        

                        System.out.println("Enter the amount you would like to deposit into Account " + accountForDeposit + ": ");
                        double depositAmount = 0.0;
                        boolean depositAmountLoop = true;
                        while(depositAmountLoop){
                            if(scan.hasNextDouble()){
                                double dummy = scan.nextDouble();
                                if(dummy > 0.0){
                                    depositAmount = dummy;
                                    depositAmountLoop = false;
                                }else{
                                    System.out.println("Invalid input. Deposit amounts must be greater than $0.");
                                    continue;
                                }
                                
                            }else{
                                System.out.println("Invalid input. Please enter a valid deposit amount.");
                                String junk = scan.nextLine();
                                continue;
                            }
                        }

                        //account ID used to deposit funds into: accountForDeposit
                        //deposit amount: depositAmount

                        

                        double newBalanceDep = currentBalanceDep + depositAmount;

                        System.out.println();
                        System.out.println("Printing deposit information: ");
                        System.out.println();
                        System.out.println("Account ID: " + accountForDeposit);
                        System.out.println("Account type: " + depAccountType);
                        System.out.println("Current account balance: $" + currentBalanceDep);
                        System.out.println("Deposit amount: $" + depositAmount);
                        System.out.println("Account balance after deposit is added: $" + newBalanceDep);
                        System.out.println();
                        System.out.println("Would you like to accept this deposit? Enter Y for Yes or N for No");
                        System.out.println();
                        System.out.println("Choice: ");
                        boolean newDepAcceptLoop = true;
                        String newDepChoice = "";
                        while(newDepAcceptLoop){
                            String dummy = scan.next();
                            if(dummy.equals("Y") || dummy.equals("N")){
                                newDepChoice = dummy;
                                newDepAcceptLoop = false;
                            }else{
                                System.out.println("Invalid input. Please enter either Y or N");
                                continue;
                            }
                        }

                        String newDepID = "";
                        if(newDepChoice.equals("Y")){
                            boolean depCheck = true;
                            PreparedStatement getDepIDs = conn.prepareStatement("select deposit_id from deposits");
                            ResultSet depIDSet = getDepIDs.executeQuery();
                            List<String> allDeps = new ArrayList<>();
                            while(depIDSet.next()){
                                allDeps.add(depIDSet.getString("deposit_id").trim());
                            }
                            Random random = new Random();
                            while(depCheck){
                                int depIDBase = 10000 + random.nextInt(100000 - 10000);
                                String depID = "D" + depIDBase;

                                if(!allDeps.contains(depID)){
                                    PreparedStatement insertDeposit = conn.prepareStatement(("insert into deposits(deposit_id, deposit_amount, account_id, deposit_date) values (?, ?, ?, ?)"));
                                     insertDeposit.setString(1, depID);
                                     insertDeposit.setDouble(2, depositAmount);
                                     insertDeposit.setString(3, accountForDeposit);
                                     insertDeposit.setDate(4, Date.valueOf(LocalDate.now()));
                                     insertDeposit.executeUpdate();

                                    PreparedStatement updateDepBal = conn.prepareStatement(("UPDATE account SET acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))"));
                                    updateDepBal.setDouble(1, newBalanceDep);
                                    updateDepBal.setString(2, accountForDeposit);
                                    updateDepBal.executeUpdate();

                                    System.out.println("Deposit successful!");
                                    System.out.println();

                                    depCheck = false;
                                }                            
                            }                            
                        }else if(newDepChoice.equals("N")){
                            System.out.println("Cancelling deposit.");
                        }
                    break;



                    case 7: //make a withdrawal
                        PreparedStatement withIDStmt = conn.prepareStatement("select account_id from account inner join customer on account.customer_id = customer.customer_id where TRIM(customer.customer_id) = ? ");
                        withIDStmt.setString(1, inputID);
                        ResultSet withSet = withIDStmt.executeQuery();
            
                        List<String> withAccountIDs = new ArrayList<>();
                        while(withSet.next()){
                            withAccountIDs.add(withSet.getString("account_id").trim());
                        }

                        if(withAccountIDs.isEmpty()){
                            System.out.println("No accounts attached to the Customer ID: " + inputID + ". Cancelling withdrawal.");
                            System.out.println();
                            break;
                        }
            
                        System.out.println("Please input your Account ID in which you would like to withdraw the funds: ");
                        for(int i = 0; i < withAccountIDs.size(); i++){
                            System.out.print("(" + withAccountIDs.get(i).trim() + ")   " );
                        }

            
                        boolean newWithIDLoop = true;
                        String accountForWith = "";
                        System.out.println();
                        System.out.print("Choice: ");
                        while(newWithIDLoop){
                            String dummy = scan.next();
                            if(withAccountIDs.contains(dummy.trim())){
                                accountForWith = dummy;
                                System.out.println("Successful ID inputted!");
                                newWithIDLoop = false;
                            }else{
                                System.out.println("Invalid Account ID inputted. Please input the Account ID from the given list.");
                                continue;
                            }
                        }

                        PreparedStatement getAccountWith = conn.prepareStatement(("select * from account where TRIM(account_id) = ?"));
                        getAccountWith.setString(1, accountForWith);
                        ResultSet getWithSet = getAccountWith.executeQuery();

                        String withAccountType = "";
                        double withMinBal = 0.0;
                        double currentBalanceWith = 0.0;

                        while(getWithSet.next()){
                            withAccountType = getWithSet.getString("account_type");
                            currentBalanceWith = getWithSet.getDouble("acct_balance");
                            if(withAccountType.equals("Savings")){
                                withMinBal = getWithSet.getDouble("min_balance");
                            }
                        }

                        System.out.println();
                        System.out.println("Current balance in account: $" + currentBalanceWith);
                        if(withAccountType.equals("Savings")){
                            System.out.println("Minimum balance required: $" + withMinBal);
                        }
                        System.out.println();

                        

                        System.out.println("Enter the amount you would like to withdraw from Account " + accountForWith + ": ");
                        double withAmount = 0.0;
                        boolean withAmountLoop = true;
                        while(withAmountLoop){
                            if(scan.hasNextDouble()){
                                double dummy = scan.nextDouble();
                                if(dummy > 0.0){
                                    withAmount = dummy;
                                    withAmountLoop = false;
                                }else{
                                    System.out.println("Invalid input. Withdrawal amounts must be greater than $0.");
                                    continue;
                                }
                                
                            }else{
                                System.out.println("Invalid input. Please enter a valid withdrawal amount.");
                                String junk = scan.nextLine();
                                continue;
                            }
                        }

                        double newBalanceWith = currentBalanceWith - withAmount;

                        if(newBalanceWith < 0 && withAccountType.equals("Checking")){
                            System.out.println("Error: withdrawal would make checking account balance negative. Cancelling withdrawal.");
                            System.out.println();
                        }else{
                            System.out.println();
                            System.out.println("Printing withdrawal information: ");
                            System.out.println();
                            System.out.println("Account ID: " + accountForWith);
                            System.out.println("Account type: " + withAccountType);
                            System.out.println("Current account balance: $" + currentBalanceWith);
                            System.out.println("Withdrawal amount: $" + withAmount);
                            System.out.println("Account balance after withdrawal is subtracted: $" + String.format("%.2f", newBalanceWith));
                            System.out.println();
                            System.out.println("Would you like to accept this withdrawal? Enter Y for Yes or N for No");
                            if(withAccountType.equals("Savings")){
                                System.out.println("Reminder: a $35 penalty will be added to your account if you are below your minimum balance of $" + withMinBal);
                            }
                            System.out.println();
                            System.out.println("Choice: ");
                            boolean newWithAcceptLoop = true;
                            String newWithChoice = "";
                            while(newWithAcceptLoop){
                                String dummy = scan.next();
                                if(dummy.equals("Y") || dummy.equals("N")){
                                    newWithChoice = dummy;
                                    newWithAcceptLoop = false;
                                }else{
                                    System.out.println("Invalid input. Please enter either Y or N");
                                    continue;
                                }
                            }


                            String newWithID = "";

                            if(newWithChoice.equals("Y")){
                                boolean withCheck = true;
                                PreparedStatement getWithIDs = conn.prepareStatement("select withdrawal_id from withdrawals");
                                ResultSet withIDSet = getWithIDs.executeQuery();
                                List<String> allWith = new ArrayList<>();
                                while(withIDSet.next()){
                                    allWith.add(withIDSet.getString("withdrawal_id").trim());
                                }
                                Random random = new Random();
                                while(withCheck){
                                    int withIDBase = 10000 + random.nextInt(100000 - 10000);
                                    String withID = "W" + withIDBase;

                                    if(!allWith.contains(withID)){

                                        if(withAccountType.equals("Checking")){
                                            PreparedStatement insertWithCheck = conn.prepareStatement("insert into withdrawals(withdrawal_id, withdrawal_amount, account_id, withdrawal_date) values (?, ?, ?, ?)");
                                                insertWithCheck.setString(1, withID);
                                                insertWithCheck.setDouble(2, withAmount);
                                                insertWithCheck.setString(3, accountForWith);
                                                insertWithCheck.setDate(4, Date.valueOf(LocalDate.now()));
                                                insertWithCheck.executeUpdate();

                                                PreparedStatement updateWithCheck = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                                updateWithCheck.setDouble(1, newBalanceWith);
                                                updateWithCheck.setString(2, accountForWith);
                                                updateWithCheck.executeUpdate();

                                                System.out.println("Withdrawal successful!");
                                                System.out.println();

                                                withCheck = false;                                       
                                        }else if(withAccountType.equals("Savings") && newBalanceWith < withMinBal){
                                                newBalanceWith = newBalanceWith - 35.00; //penalty for withdrawal causing balance to go under minimum balance 
                                                PreparedStatement insertWithSav = conn.prepareStatement("insert into withdrawals(withdrawal_id, withdrawal_amount, account_id, withdrawal_date) values (?, ?, ?, ?)");
                                                insertWithSav.setString(1, withID);
                                                insertWithSav.setDouble(2, withAmount);
                                                insertWithSav.setString(3, accountForWith);
                                                insertWithSav.setDate(4, Date.valueOf(LocalDate.now()));
                                                insertWithSav.executeUpdate();

                                                PreparedStatement updateWithSav = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                                updateWithSav.setDouble(1, newBalanceWith);
                                                updateWithSav.setString(2, accountForWith);
                                                updateWithSav.executeUpdate();

                                                System.out.println("Withdrawal successful! Since your account balance is below your minimum balance, a $35 penalty was carried out.");
                                                System.out.println();

                                                withCheck = false;
                                        }else if(withAccountType.equals("Savings") && newBalanceWith > withMinBal){
                                                PreparedStatement insertWithSavOK = conn.prepareStatement("insert into withdrawals(withdrawal_id, withdrawal_amount, account_id, withdrawal_date) values (?, ?, ?, ?)");
                                                insertWithSavOK.setString(1, withID);
                                                insertWithSavOK.setDouble(2, withAmount);
                                                insertWithSavOK.setString(3, accountForWith);
                                                insertWithSavOK.setDate(4, Date.valueOf(LocalDate.now()));
                                                insertWithSavOK.executeUpdate();

                                                PreparedStatement updateWithSavOK = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                                updateWithSavOK.setDouble(1, newBalanceWith);
                                                updateWithSavOK.setString(2, accountForWith);
                                                updateWithSavOK.executeUpdate();

                                                System.out.println("Withdrawal successful!");
                                                System.out.println();

                                                withCheck = false;
                                        }
                                    }
                                }

                            }else if(newWithChoice.equals("N")){
                                System.out.println("Cancelling withdrawal.");
                                System.out.println();
                            }
                        }
                    break;


                    case 8: //open new account
                        System.out.println();
                        System.out.println("Enter the account type you would like to open: Savings, Checking or Investment: ");
                        System.out.println();
                        System.out.print("Choice: ");
                        String newAccountType = "";
                        boolean newAccountTypeLoop = true;
                        while(newAccountTypeLoop){
                            String dummy = scan.next();
                            if(dummy.equals("Savings") || dummy.equals("Checking") || dummy.equals("Investment")){
                                newAccountType = dummy;
                                newAccountTypeLoop = false;
                            }else{
                                System.out.println("Invalid input. Please enter either Savings, Checking or Investment");
                                continue;
                            }
                        }
                        System.out.println();

                        if(newAccountType.equals("Checking")){
                                System.out.println("Enter the amount you like to deposit to start your account (must be a dollar amount greater than or equal to $0.0): ");
                                double checkStartBalance = 0.0;
                                boolean checkStartBalanceLoop = true;
                                while(checkStartBalanceLoop){
                                    if(scan.hasNextDouble()){
                                        double dummy = scan.nextDouble();
                                        if(dummy >= 0.0){
                                            checkStartBalance = dummy;
                                            checkStartBalanceLoop = false;
                                        }else{
                                            System.out.println("Invalid input: initial deposit amount must be greater than or equal to $0.0");
                                            continue;
                                        }
                                    }else{
                                        System.out.println("Invalid input: initial deposit amount must be a valid dollar amount.");
                                        scan.nextLine();
                                        continue;
                                    }
                                }
                                System.out.println();
                                System.out.println("Printing new account information: ");
                                System.out.println();
                                System.out.println("Customer ID: " + inputID);
                                System.out.println("New account type: " + newAccountType);
                                System.out.println("Starting account balance: $" + checkStartBalance);
                                System.out.println("Interest rate: 0.5%");
                                System.out.println();
                                System.out.println("Would you like to accept this new account? Enter Y for Yes or N for No");
                                System.out.println();
                                System.out.print("Choice: ");

                                boolean newCheckAcceptLoop = true;
                                String newCheckAcceptChoice = "";
                                while(newCheckAcceptLoop){
                                    String dummy = scan.next();
                                    if(dummy.equals("Y") || dummy.equals("N")){
                                        newCheckAcceptChoice = dummy;
                                        newCheckAcceptLoop = false;
                                    }else{
                                        System.out.println("Invalid input. Please enter either Y or N");
                                        continue;
                                    }
                                }

                                String newCheckAccountID = "";
                                if(newCheckAcceptChoice.equals("Y")){
                                    boolean newCheckIDCheck = true;
                                    PreparedStatement getCheckIDs = conn.prepareStatement("select account_id from account");
                                    ResultSet getCheckIDSet = getCheckIDs.executeQuery();
                                    List<String> allCheckIDs = new ArrayList<>();
                                    while(getCheckIDSet.next()){
                                        allCheckIDs.add(getCheckIDSet.getString("account_id").trim());
                                    }

                                    Random random = new Random();
                                    while(newCheckIDCheck){
                                        int newCheckIDBase = 10000 + random.nextInt(100000 - 10000);
                                        newCheckAccountID = "A" + newCheckIDBase;

                                        if(!allCheckIDs.contains(newCheckAccountID)){
                                            PreparedStatement insertNewCheckAccount = conn.prepareStatement("insert into account(account_id, account_type, customer_id, acct_balance, min_balance, asset_list, interest_rate) values(?, ?, ?, ?, ?, ?, ?)");
                                            insertNewCheckAccount.setString(1, newCheckAccountID);
                                            insertNewCheckAccount.setString(2, newAccountType);
                                            insertNewCheckAccount.setString(3, inputID);
                                            insertNewCheckAccount.setDouble(4, checkStartBalance);
                                            insertNewCheckAccount.setNull(5, java.sql.Types.DOUBLE);
                                            insertNewCheckAccount.setNull(6, java.sql.Types.DOUBLE);
                                            insertNewCheckAccount.setDouble(7, 0.5);
                                            insertNewCheckAccount.executeUpdate();

                                            System.out.println("New checking account created! New Account ID: " + newCheckAccountID);
                                            System.out.println();

                                            newCheckIDCheck = false;
                                        }
                                    }
                                }else if(newCheckAcceptChoice.equals("N")){
                                    System.out.println("Cancelling new account.");
                                    break;
                                }
                        }else if(newAccountType.equals("Savings")){
                            System.out.println("Enter the amount you like to deposit to start your account (must be a dollar amount greater than or equal to the minimum balance, which will be $250.0): ");
                                double savStartBalance = 0.0;
                                boolean savStartBalanceLoop = true;
                                while(savStartBalanceLoop){
                                    if(scan.hasNextDouble()){
                                        double dummy = scan.nextDouble();
                                        if(dummy >= 250.0){
                                            savStartBalance = dummy;
                                            savStartBalanceLoop = false;
                                        }else{
                                            System.out.println("Invalid input: initial deposit amount must be greater than or equal to $250.0");
                                            continue;
                                        }
                                    }else{
                                        System.out.println("Invalid input: initial deposit amount must be a valid dollar amount.");
                                        scan.nextLine();
                                        continue;
                                    }
                                }
                                System.out.println();
                                System.out.println("Printing new account information: ");
                                System.out.println();
                                System.out.println("Customer ID: " + inputID);
                                System.out.println("New account type: " + newAccountType);
                                System.out.println("Starting account balance: $" + savStartBalance);
                                System.out.println("Minimum balance: $250.0");
                                System.out.println("Interest rate: 1.5%");
                                System.out.println();
                                System.out.println("Would you like to accept this new account? Enter Y for Yes or N for No");
                                System.out.println();
                                System.out.print("Choice: ");

                                boolean newSavAcceptLoop = true;
                                String newSavAcceptChoice = "";
                                while(newSavAcceptLoop){
                                    String dummy = scan.next();
                                    if(dummy.equals("Y") || dummy.equals("N")){
                                        newSavAcceptChoice = dummy;
                                        newSavAcceptLoop = false;
                                    }else{
                                        System.out.println("Invalid input. Please enter either Y or N");
                                        continue;
                                    }
                                }

                                String newSavAccountID = "";
                                if(newSavAcceptChoice.equals("Y")){
                                    boolean newSavIDCheck = true;
                                    PreparedStatement getSavIDs = conn.prepareStatement("select account_id from account");
                                    ResultSet getSavIDSet = getSavIDs.executeQuery();
                                    List<String> allSavIDs = new ArrayList<>();
                                    while(getSavIDSet.next()){
                                        allSavIDs.add(getSavIDSet.getString("account_id").trim());
                                    }

                                    Random random = new Random();
                                    while(newSavIDCheck){
                                        int newSavIDBase = 10000 + random.nextInt(100000 - 10000);
                                        newSavAccountID = "A" + newSavIDBase;

                                        if(!allSavIDs.contains(newSavAccountID)){
                                            PreparedStatement insertNewSavAccount = conn.prepareStatement("insert into account(account_id, account_type, customer_id, acct_balance, min_balance, asset_list, interest_rate) values(?, ?, ?, ?, ?, ?, ?)");
                                            insertNewSavAccount.setString(1, newSavAccountID);
                                            insertNewSavAccount.setString(2, newAccountType);
                                            insertNewSavAccount.setString(3, inputID);
                                            insertNewSavAccount.setDouble(4, savStartBalance);
                                            insertNewSavAccount.setDouble(5, 250.0);
                                            insertNewSavAccount.setNull(6, java.sql.Types.DOUBLE);
                                            insertNewSavAccount.setDouble(7, 1.5);
                                            insertNewSavAccount.executeUpdate();

                                            System.out.println("New savings account created! New Account ID: " + newSavAccountID);
                                            System.out.println();

                                            newSavIDCheck = false;
                                        }
                                    }
                                }else if(newSavAcceptChoice.equals("N")){
                                    System.out.println("Cancelling new account.");
                                    break;
                                }


                        }else if(newAccountType.equals("Investment")){
                            System.out.println("Enter the asset you would like to invest in: Stocks, Bonds, Mutual Funds, Real Estate, Index Funds or Crypto");
                            String assetToInvestIn = "";
                            boolean assetInvestLoop = true;
                            scan.nextLine();
                            while(assetInvestLoop){
                                String dummy = scan.nextLine();
                                if(dummy.equals("Stocks") || dummy.equals("Bonds") || dummy.equals("Real Estate") || dummy.equals("Mutual Funds") || dummy.equals("Index Funds") || dummy.equals("Crypto")){
                                    assetToInvestIn = dummy;
                                    assetInvestLoop = false;
                                }else{
                                    System.out.println("Invalid: please choose an asset from the given list.");
                                }
                            }

                            System.out.println("Enter the amount you like to invest to start your account (must be a dollar amount greater than or equal to $500.0): ");
                                double investStartBalance = 0.0;
                                boolean investStartBalanceLoop = true;
                                while(investStartBalanceLoop){
                                    if(scan.hasNextDouble()){
                                        double dummy = scan.nextDouble();
                                        if(dummy >= 500.0){
                                            investStartBalance = dummy;
                                            investStartBalanceLoop = false;
                                        }else{
                                            System.out.println("Invalid input: initial investment amount must be greater than or equal to $500.0");
                                            continue;
                                        }
                                    }else{
                                        System.out.println("Invalid input: initial investment amount must be a valid dollar amount.");
                                        scan.nextLine();
                                        continue;
                                    }
                                }

                                System.out.println();
                                System.out.println("Printing new account information: ");
                                System.out.println();
                                System.out.println("Customer ID: " + inputID);
                                System.out.println("New account type: " + newAccountType);
                                System.out.println("Starting account balance: $" + investStartBalance);
                                System.out.println("Asset: " + assetToInvestIn);
                                System.out.println("Interest rate: 5.0%");
                                System.out.println();
                                System.out.println("Would you like to accept this new account? Enter Y for Yes or N for No");
                                System.out.println();
                                System.out.print("Choice: ");

                                boolean newInvestAcceptLoop = true;
                                String newInvestAcceptChoice = "";
                                while(newInvestAcceptLoop){
                                    String dummy = scan.next();
                                    if(dummy.equals("Y") || dummy.equals("N")){
                                        newInvestAcceptChoice = dummy;
                                        newInvestAcceptLoop = false;
                                    }else{
                                        System.out.println("Invalid input. Please enter either Y or N");
                                        continue;
                                    }
                                }

                                String newInvestAccountID = "";
                                if(newInvestAcceptChoice.equals("Y")){
                                    boolean newInvestIDCheck = true;
                                    PreparedStatement getInvestIDs = conn.prepareStatement("select account_id from account");
                                    ResultSet getInvestIDSet = getInvestIDs.executeQuery();
                                    List<String> allInvestIDs = new ArrayList<>();
                                    while(getInvestIDSet.next()){
                                        allInvestIDs.add(getInvestIDSet.getString("account_id").trim());
                                    }

                                    Random random = new Random();
                                    while(newInvestIDCheck){
                                        int newInvestIDBase = 10000 + random.nextInt(100000 - 10000);
                                        newInvestAccountID = "A" + newInvestIDBase;

                                        if(!allInvestIDs.contains(newInvestAccountID)){
                                            PreparedStatement insertNewInvestAccount = conn.prepareStatement("insert into account(account_id, account_type, customer_id, acct_balance, min_balance, asset_list, interest_rate) values(?, ?, ?, ?, ?, ?, ?)");
                                            insertNewInvestAccount.setString(1, newInvestAccountID);
                                            insertNewInvestAccount.setString(2, newAccountType);
                                            insertNewInvestAccount.setString(3, inputID);
                                            insertNewInvestAccount.setDouble(4, investStartBalance);
                                            insertNewInvestAccount.setNull(5, java.sql.Types.DOUBLE);
                                            insertNewInvestAccount.setString(6, assetToInvestIn);
                                            insertNewInvestAccount.setDouble(7, 5.0);
                                            insertNewInvestAccount.executeUpdate();

                                            System.out.println("New investment account created! New Account ID: " + newInvestAccountID);
                                            System.out.println();

                                            newInvestIDCheck = false;
                                        }
                                    }
                                }else if(newInvestAcceptChoice.equals("N")){
                                    System.out.println("Cancelling new account.");
                                    break;
                                }

                            
                        }

                        
                    break;
           
                    case 9: //obtain new or replacement card
            System.out.println("Select an option: ");
            System.out.println("\t 1) Obtain new card");
            System.out.println("\t 2) Request replacement card");
            int cardOption = 0;
            boolean cardOptionLoop = true;
            while(cardOptionLoop){
                if(scan.hasNextInt()){
                    int dummy = scan.nextInt();
                    if((dummy == 1 || dummy == 2)){
                        cardOption = dummy;
                        cardOptionLoop = false;
                    }else {
                        System.out.println("Invalid input. Please enter either 1 or 2.");
                        continue;
                    }
                }else{
                    System.out.println("Invalid input. Please enter either 1 or 2.");
                    scan.nextLine();
                    continue;
                }
            }

            PreparedStatement cardPur = conn.prepareStatement("select account_id from account inner join customer on account.customer_id = customer.customer_id where TRIM(customer.customer_id) = ? ");
            cardPur.setString(1, inputID);
            ResultSet newCards = cardPur.executeQuery();
            if(!newCards.isBeforeFirst()){
                System.out.println("No accounts attached to Customer ID: " + inputID + ". Cancelling new card.");
                System.out.println();
                break;
            }

            List<String> cardAccountIDs = new ArrayList<>();
            while(newCards.next()){
                cardAccountIDs.add(newCards.getString("account_id").trim());
            }

            System.out.println("Please input your Account ID in which you would like to use: ");
            for(int i = 0; i < cardAccountIDs.size(); i++){
                System.out.print("(" + cardAccountIDs.get(i).trim() + ")   " );
            }

            boolean newCardIDLoop = true;
            String accountForCard = "";
            System.out.println();
            System.out.print("Choice: ");
            while(newCardIDLoop){
                String dummy = scan.next();
                if(cardAccountIDs.contains(dummy.trim())){
                    accountForCard = dummy;
                    System.out.println("Successful ID inputted!");
                    newCardIDLoop = false;
                }else{
                    System.out.println("Invalid Account ID inputted. Please input the Account ID from the given list.");
                    continue;
                }
            }

            String accountTypeForCard = "";
            PreparedStatement getCardTypeNew = conn.prepareStatement("select account_type from account where TRIM(account_id) = ?");
            getCardTypeNew.setString(1, accountForCard);
            ResultSet accountTypeCard = getCardTypeNew.executeQuery();
            if(accountTypeCard.next()){
                accountTypeForCard = accountTypeCard.getString("account_type");
            }

            if(accountTypeForCard.equals("Investment")){
                System.out.println("Error - investment accounts cannot have an attached card.");
                System.out.println();
                break;
            }

            switch(cardOption){
                case 1: //new card
                    System.out.println("Are you applying for a Credit or Debit card?");
                    boolean newCardLoop = true;
                    String newCardType = "";
                    System.out.println();
                    System.out.println("Choice: ");
                    while(newCardLoop){
                        String dummy = scan.next();
                        if(dummy.equals("Credit") || dummy.equals("Debit")){
                            newCardType = dummy;
                            newCardLoop = false;
                        } else{
                            System.out.println("Invalid input. Please enter either 'Debit' or 'Credit' (case sensitive)");
                            continue;
                        }
                    }

                    if((newCardType.equals("Debit") && accountTypeForCard.equals("Checking")) || 
                    (newCardType.equals("Credit") && accountTypeForCard.equals("Checking")) || 
                    (newCardType.equals("Credit") && accountTypeForCard.equals("Savings"))) {
                        
                        if(newCardType.equals("Credit")) {
                            double newCreditLimit = 2000.0;
                            double newCreditInterestRate = 15.0 + (30.0 - 15.0) * Math.random();
                            newCreditInterestRate = Math.round(newCreditInterestRate * 100.0) / 100.0;

                            System.out.println();
                            System.out.println("Printing new credit card information");
                            System.out.println("Credit limit: $" + newCreditLimit);
                            System.out.println("Interest rate: " + newCreditInterestRate + "%");
                            System.out.println("Minimum monthly payment: $35.00");
                            System.out.println("Would you like to accept this card? Enter Y for Yes or N for No");
                            System.out.println();
                            System.out.println("Choice: ");
                            boolean newCardAcceptLoop = true;
                            String newCardAcceptChoice = "";
                            while(newCardAcceptLoop){
                                String dummy = scan.next();
                                if(dummy.equals("Y") || dummy.equals("N")){
                                    newCardAcceptChoice = dummy;
                                    newCardAcceptLoop = false;
                                }else{
                                    System.out.println("Invalid input. Please enter either Y or N");
                                    continue;
                                }
                            }

                            String newCreditNumber = "";
                            if(newCardAcceptChoice.equals("Y")){
                                Random random = new Random();
                                boolean creditNumberCheck = true;
                                PreparedStatement getCreditNums = conn.prepareStatement("select card_number from card");
                                ResultSet creditNumSet = getCreditNums.executeQuery();
                                List<String> allCreditNums = new ArrayList<>();
                                while(creditNumSet.next()){
                                    allCreditNums.add(creditNumSet.getString("card_number").trim());
                                }
                                while(creditNumberCheck){
                                    long firstHalf = (long) (1e8 + random.nextDouble() * 9e8); 
                                    long secondHalf = (long) (1e8 + random.nextDouble() * 9e8);
                                    String newCreditDummy = String.valueOf(firstHalf) + String.valueOf(secondHalf);
                                    if(!allCreditNums.contains(newCreditDummy)){
                                        newCreditNumber = newCreditDummy;
                                        PreparedStatement insertCard = conn.prepareStatement("insert into card(card_type, card_number, account_id) values (?, ?, ?)");
                                        insertCard.setString(1, newCardType);
                                        insertCard.setString(2, newCreditNumber);
                                        insertCard.setString(3, accountForCard);
                                        insertCard.executeUpdate();
                                        
                                        PreparedStatement insertCreditCard = conn.prepareStatement("insert into creditcard(card_number, card_type, credit_limit, balance_due, interest_rate, running_balance, monthly_payment) values (?, ?, ?, ?, ?, ?, ?)");
                                        insertCreditCard.setString(1, newCreditNumber);
                                        insertCreditCard.setString(2, newCardType);
                                        insertCreditCard.setDouble(3, 2000.0);
                                        insertCreditCard.setDouble(4, 0.0);
                                        insertCreditCard.setDouble(5, newCreditInterestRate);
                                        insertCreditCard.setDouble(6, 0.0);
                                        insertCreditCard.setDouble(7, 35.00);
                                        insertCreditCard.executeUpdate();

                                        System.out.println();
                                        System.out.println("Success! New credit card added. Here are the details: ");
                                        System.out.println();
                                        System.out.println("Card number: " + newCreditNumber);
                                        System.out.println("Account ID: " + accountForCard);
                                        System.out.println("Credit Limit: $2000");
                                        System.out.println("Interest rate: " + newCreditInterestRate + "%");
                                        System.out.println("Standard minimum monthly payment: $35.00");
                                        System.out.println("Starting balance due, running balance: $0");
                                        System.out.println();

                                        creditNumberCheck = false;
                                    }
                                }
                            } else if(newCardAcceptChoice.equals("N")){
                                System.out.println("Cancelling new card.");
                            }
                        } else if(newCardType.equals("Debit")) {
                            PreparedStatement getAccountBalance = conn.prepareStatement("select acct_balance from account where TRIM(account_id) = ?");
                            getAccountBalance.setString(1, accountForCard);
                            ResultSet getBal = getAccountBalance.executeQuery();
                            double debitBalance = 0.0;
                            if(getBal.next()){
                                debitBalance = getBal.getDouble("acct_balance");
                            }
                            
                            Random random = new Random();
                            PreparedStatement getDebitNums = conn.prepareStatement("select card_number from card");
                            ResultSet debitNumSet = getDebitNums.executeQuery();
                            List<String> allDebitNums = new ArrayList<>();
                            while(debitNumSet.next()){
                                allDebitNums.add(debitNumSet.getString("card_number").trim());
                            }
                            
                            boolean debitNumCheck = true;
                            String newDebitNum = "";
                            while(debitNumCheck){
                                long firstHalfDebit = (long) (1e8 + random.nextDouble() * 9e8); 
                                long secondHalfDebit = (long) (1e8 + random.nextDouble() * 9e8);
                                String newDebitDummy = String.valueOf(firstHalfDebit) + String.valueOf(secondHalfDebit);

                                if(!allDebitNums.contains(newDebitDummy)){
                                    newDebitNum = newDebitDummy;

                                    PreparedStatement insertCard2 = conn.prepareStatement("insert into card(card_type, card_number, account_id) values (?, ?, ?)");
                                    insertCard2.setString(1, newCardType);
                                    insertCard2.setString(2, newDebitNum);
                                    insertCard2.setString(3, accountForCard);
                                    insertCard2.executeUpdate();

                                    PreparedStatement insertDebitCard = conn.prepareStatement("insert into debitcard(card_number) values (?)");
                                    insertDebitCard.setString(1, newDebitNum);
                                    insertDebitCard.executeUpdate();

                                    PreparedStatement debBal = conn.prepareStatement("select * from account where TRIM(account_id) = ?");
                                    debBal.setString(1, accountForCard);
                                    ResultSet debBalSet = debBal.executeQuery();

                                    if(debBalSet.next()){
                                        System.out.println();
                                        System.out.println("Success! New debit card added. Here are the details: ");
                                        System.out.println("Card number: " + newDebitNum);
                                        System.out.println("Account ID: " + accountForCard);
                                        System.out.println("Card/account balance: $" + debBalSet.getDouble("acct_balance"));
                                        System.out.println();
                                    }
                                    debitNumCheck = false;
                                }
                            }
                        }
                    } else {
                        System.out.println("Error: Invalid card type for this account type. Debit cards are exclusive to checking accounts.");
                        System.out.println();
                    }
                    break;

                case 2: //replace card
                PreparedStatement getAllCards = conn.prepareStatement(("select * from card where TRIM(account_id) = ?"));
                getAllCards.setString(1, accountForCard);
                ResultSet cardData = getAllCards.executeQuery();
                List<String> cardNumbers = new ArrayList<>();
                while(cardData.next()){
                    cardNumbers.add(cardData.getString("card_number").trim());
                }
                if(cardNumbers.isEmpty()){ //ensures user has a card to replace
                    System.out.println("There are no cards currently attached to Customer ID: " + inputID + ". Unable to replace card.");
                    System.out.println();
                }else{
                    System.out.println("Printing card data: ");
                    System.out.println();
                    cardData = getAllCards.executeQuery();
                    while(cardData.next()){
                        System.out.println("Card number: " + cardData.getString("card_number").trim());
                        System.out.println("Card type: " + cardData.getString("card_type").trim());
                        System.out.println("-----------------------------------------");
                    }
                    boolean replaceCardLoop = true;
                    String cardToReplace = "";
                    System.out.println("Please enter the card number of the card you want to replace: ");
                    System.out.println();
                    System.out.print("Choice: ");
                    while(replaceCardLoop){
                        String dummy = scan.next();
                        if(cardNumbers.contains(dummy)){
                            cardToReplace = dummy;
                            replaceCardLoop = false;
                        }else{
                            System.out.println("Invalid input. Please input a valid card number from the given list.");
                            scan.nextLine();
                        }
                    }
                    PreparedStatement getCardType = conn.prepareStatement(("select card_type from card where TRIM(card_number) = ?"));
                    getCardType.setString(1, cardToReplace);
                    ResultSet cardTypeToReplace = getCardType.executeQuery();
                    String cardType = "";
                    while(cardTypeToReplace.next()){
                        cardType = cardTypeToReplace.getString("card_type").trim();
                    }

                    if(cardType.equals("Credit")){ //replace credit card
                        PreparedStatement getAllCredit = conn.prepareStatement(("select * from creditcard where TRIM(card_number) = ?"));
                        getAllCredit.setString(1, cardToReplace);
                        ResultSet oldDebitData = getAllCredit.executeQuery();
                        double oldCreditLimit = 0.0;
                        double oldBalanceDue = 0.0;
                        double oldInterestRate = 0.0;
                        double oldRunningBalance = 0.0;
                        while(oldDebitData.next()){ // get old card data
                            oldCreditLimit = oldDebitData.getDouble("credit_limit");
                            oldBalanceDue = oldDebitData.getDouble("balance_due");
                            oldInterestRate = oldDebitData.getDouble("interest_rate");
                            oldRunningBalance = oldDebitData.getDouble("running_balance");
                        }
                        PreparedStatement deleteCredit = conn.prepareStatement("delete from creditcard where TRIM(card_number) = ?"); //delete from creditcard
                        deleteCredit.setString(1, cardToReplace);
                        deleteCredit.executeUpdate();

                        PreparedStatement deletePurchases = conn.prepareStatement("delete from purchases where TRIM(card_number) = ?"); //delete all purchases related to old card number
                        deletePurchases.setString(1, cardToReplace);
                        deletePurchases.executeUpdate();

                        PreparedStatement deleteCCard = conn.prepareStatement("delete from card where TRIM(card_number) = ?"); //delete from card
                        deleteCCard.setString(1, cardToReplace);
                        deleteCCard.executeUpdate();

                        Random random = new Random();
                        boolean newCreditCheck = true;

                        PreparedStatement getCardNums = conn.prepareStatement("select card_number from card");
                        ResultSet cardNumSet = getCardNums.executeQuery();
                        List<String> allNums = new ArrayList<>();
                        while(cardNumSet.next()){
                            allNums.add(cardNumSet.getString("card_number").trim());
                        }
                        while(newCreditCheck){
                            long firstHalf = (long) (1e8 + random.nextDouble() * 9e8); 
                            long secondHalf = (long) (1e8 + random.nextDouble() * 9e8);
                            String replaceCreditNumber = String.valueOf(firstHalf) + String.valueOf(secondHalf); //new random number
                            if(!allNums.contains(replaceCreditNumber)){
                                PreparedStatement insertCard = conn.prepareStatement(("insert into card(card_type, card_number, account_id) values (?, ?, ?)"));
                                insertCard.setString(1, "Credit");
                                insertCard.setString(2, replaceCreditNumber);
                                insertCard.setString(3, accountForCard);
                                insertCard.executeUpdate();

                                PreparedStatement insertCreditCard = conn.prepareStatement(("insert into creditcard(card_number, card_type, credit_limit, balance_due, interest_rate, running_balance) values (?, ?, ?, ?, ?, ?)"));
                                insertCreditCard.setString(1, replaceCreditNumber);
                                insertCreditCard.setString(2, "Credit");
                                insertCreditCard.setDouble(3, oldCreditLimit);
                                insertCreditCard.setDouble(4, oldBalanceDue);
                                insertCreditCard.setDouble(5, oldInterestRate);
                                insertCreditCard.setDouble(6, oldRunningBalance);
                                insertCreditCard.executeUpdate();

                                System.out.println();
                                System.out.println("Success! Card " + cardToReplace + " was deleted and replaced with card " + replaceCreditNumber);
                                System.out.println();

                                newCreditCheck = false;
                            }
                        }

                    }else if(cardType.equals("Debit")){ //replace debit card

                        PreparedStatement deleteDebit = conn.prepareStatement("delete from debitcard where TRIM(card_number) = ?"); //delete from debitcard
                        deleteDebit.setString(1, cardToReplace);
                        deleteDebit.executeUpdate();

                        PreparedStatement deletePurchasesDebit = conn.prepareStatement("delete from purchases where TRIM(card_number) = ?"); //delete all purchases related to old card number
                        deletePurchasesDebit.setString(1, cardToReplace);
                        deletePurchasesDebit.executeUpdate();

                        PreparedStatement deleteDCard = conn.prepareStatement("delete from card where TRIM(card_number) = ?"); //delete from card
                        deleteDCard.setString(1, cardToReplace);
                        deleteDCard.executeUpdate();

                        Random random = new Random();
                        boolean newDebitCheck = true;
                        PreparedStatement getCardNums = conn.prepareStatement("select card_number from card");
                        ResultSet cardNumSet = getCardNums.executeQuery();
                        List<String> allNums = new ArrayList<>();
                        while(cardNumSet.next()){
                            allNums.add(cardNumSet.getString("card_number").trim());
                        }
                        while(newDebitCheck){
                            long firstHalf = (long) (1e8 + random.nextDouble() * 9e8); 
                            long secondHalf = (long) (1e8 + random.nextDouble() * 9e8);
                            String replaceDebitNumber = String.valueOf(firstHalf) + String.valueOf(secondHalf); //new random number
                            if(!allNums.contains(replaceDebitNumber)){
                                PreparedStatement insertCard = conn.prepareStatement(("insert into card(card_type, card_number, account_id) values (?, ?, ?)"));
                                insertCard.setString(1, "Debit");
                                insertCard.setString(2, replaceDebitNumber);
                                insertCard.setString(3, accountForCard);
                                insertCard.executeUpdate();

                                PreparedStatement insertCreditCard = conn.prepareStatement(("insert into debitcard(card_number) values (?)"));
                                insertCreditCard.setString(1, replaceDebitNumber);
                                insertCreditCard.executeUpdate();

                                System.out.println();
                                System.out.println("Success! Card " + cardToReplace + " was deleted and replaced with card " + replaceDebitNumber);
                                System.out.println();


                                newDebitCheck = false;
                            }
                        }


                    }
                }
            break;
            }
                        break; 

                        case 10: //take out a new loan
                        PreparedStatement newLoanStmt = conn.prepareStatement("select account_id from account inner join customer on account.customer_id = customer.customer_id where TRIM(customer.customer_id) = ? ");
                        newLoanStmt.setString(1, inputID);
                        ResultSet newLoanSet = newLoanStmt.executeQuery();
                        List<String> dumLoanIDs = new ArrayList<>();
                        List<String> newLoanIDs = new ArrayList<>();
                        while(newLoanSet.next()){
                            dumLoanIDs.add(newLoanSet.getString("account_id"));
                        }
                        for(int i = 0; i < dumLoanIDs.size(); i++){
                            newLoanIDs.add(dumLoanIDs.get(i).trim());
                        }
                        System.out.println("Please input your Account ID in which you would like to attach the new loan to.");
                        for(int i = 0; i < newLoanIDs.size(); i++){
                            System.out.print("(" + newLoanIDs.get(i).trim() + ")   " );
                        }
                        boolean newLoanIDLoop = true;
                        String newLoanAccountID = "";
                        System.out.println();
                        System.out.print("Choice: ");
                        while(newLoanIDLoop){
                            String dummy = scan.next();
                            if(newLoanIDs.contains(dummy.trim())){
                                newLoanAccountID = dummy;
                                System.out.println("Success!");
                                newLoanIDLoop = false;
                            }else{
                                System.out.println("Invalid Account ID inputted. Please input the Account ID from the given list.");
                                continue;
                            }
                        }
                        scan.nextLine();
                        System.out.println("What is the reason for your loan?");
                        String newLoanReason = scan.nextLine();
                        System.out.println("What is the loan type (select 'Secured' or 'Unsecured')");
                        boolean newLoanTypeLoop = true;
                        String newLoanType = "";
                        while(newLoanTypeLoop){
                            String dummy = scan.next();
                            if(dummy.equals("Secured") || dummy.equals("Unsecured")){
                                newLoanType = dummy;
                                newLoanTypeLoop = false;
                            }else{
                                System.out.println("Invalid input. Please select 'Secured' or 'Unsecured' (case sensitive)");
                                continue;
                            }
                        }
                        System.out.println("What is the loan amount?");
                        double newLoanAmount = 0.0;
                        boolean newLoanAmountLoop = true;
                        while(newLoanAmountLoop){
                            if(scan.hasNextDouble()){
                                double dummy = scan.nextDouble();
                                if(dummy > 0.0){
                                    newLoanAmount = dummy;
                                    newLoanAmountLoop = false;
                                }else{
                                    System.out.println("Invalid input. Loan amounts must be greater than $0.");
                                    continue;
                                }
                                
                            }else{
                                System.out.println("Invalid input. Please enter a valid loan amount.");
                                String junk = scan.nextLine();
                                continue;
                            }
                        }

                        double newLoanInterestRate = 0.0;
                        if(newLoanType.equals("Secured")){
                            newLoanInterestRate = 5.0 + (15.0 - 5.0) * Math.random(); //random interest rate between 5.0 and 15.0
                        }else if(newLoanType.equals("Unsecured")){
                            newLoanInterestRate = 15.0 + (30.0 - 15.0) * Math.random(); //random interest rate between 15 and 30
                        }
                        
                        newLoanInterestRate = Math.round(newLoanInterestRate * 100.0) / 100.0; //rounds to 2 digits after decimal
                        double newLoanMonPayment = (newLoanAmount / 36.0) / newLoanInterestRate;
                        newLoanMonPayment = Math.round(newLoanMonPayment * 100.0) / 100.0; //rounds to 2 digits after decimal

                        System.out.println();
                        System.out.println("Printing new loan information: ");
                        System.out.println("Account ID: " + newLoanAccountID);
                        System.out.println("Loan Reason: " + newLoanReason);
                        System.out.println("Loan Type: " + newLoanType);
                        System.out.println("Loan Amount: $" + newLoanAmount);
                        System.out.println("Monthly Payment: $" + newLoanMonPayment);
                        System.out.println("Interest Rate: " + newLoanInterestRate + "%");
                        System.out.println();
                        System.out.println("Would you like to accept this loan? Enter Y for Yes or N for No");
                        System.out.println();
                        System.out.println("Choice: ");
                        boolean newLoanAcceptLoop = true;
                        String newLoanChoice = "";
                        while(newLoanAcceptLoop){
                            String dummy = scan.next();
                            if(dummy.equals("Y") || dummy.equals("N")){
                                newLoanChoice = dummy;
                                newLoanAcceptLoop = false;
                            }else{
                                System.out.println("Invalid input. Please enter either Y or N");
                                continue;
                            }
                        }
                        
                        String newLoanID = "";
                        if(newLoanChoice.equals("Y")){ //user choices to accept new loan conditions
                            boolean IDCheck = true;
                            PreparedStatement getLoanIDs = conn.prepareStatement("select loan_id from loans");
                            ResultSet loanIDSet = getLoanIDs.executeQuery();
                            List<String> allLoans = new ArrayList<>();
                            while(loanIDSet.next()){
                                allLoans.add(loanIDSet.getString("loan_id").trim());
                            }
                            Random random = new Random();
                            while(IDCheck){
                                int loanIDBase = 10000 + random.nextInt(100000 - 10000); // Generate a random number
                                String dummyLoanID = "L" + loanIDBase;
                                
                                if(!allLoans.contains(dummyLoanID)){ // If the random LoanID is not already added, insert the new loan into the database
                                    newLoanID = dummyLoanID;

                                    PreparedStatement insertLoan = conn.prepareStatement("insert into loans(loan_reason, loan_type, loan_id, loan_amount, monthly_pay, interest_rate, account_id) values (?, ?, ?, ?, ?, ?, ?)");
                                    insertLoan.setString(1, newLoanReason);
                                    insertLoan.setString(2, newLoanType);
                                    insertLoan.setString(3, newLoanID);
                                    insertLoan.setDouble(4, newLoanAmount);
                                    insertLoan.setDouble(5, newLoanMonPayment);
                                    insertLoan.setDouble(6, newLoanInterestRate);
                                    insertLoan.setString(7, newLoanAccountID);
                                    insertLoan.executeUpdate();

                                    IDCheck = false;
                                }
                            }
                            System.out.println("Loan successfully added! Loan ID: " + newLoanID);
                            System.out.println();
                        }else if(newLoanChoice.equals("N")){ //user decides to cancel the new loan
                            System.out.println("Cancelling loan.");
                        }
                    break;
                    
                    case 11: //make a card purchase. Will simulate a real world user interface scenario, here users will input the transaction details themselves
                        PreparedStatement getAllCards = conn.prepareStatement(("select * from card inner join account on card.account_id = account.account_id where TRIM(account.customer_id) = ?"));
                        getAllCards.setString(1, inputID);
                        ResultSet cardData = getAllCards.executeQuery();
                        List<String> cardNumbers = new ArrayList<>();
                        while(cardData.next()){
                            cardNumbers.add(cardData.getString("card_number").trim());
                        }
                        if(cardNumbers.isEmpty()){ //ensures user has a card to replace
                            System.out.println("There are no cards currently attached to Customer ID: " + inputID + ". Unable to make a transaction.");
                            System.out.println();
                        }else{
                            PreparedStatement cardsPurchase = conn.prepareStatement("select * from card inner join account on card.account_id = account.account_id where TRIM(account.customer_id) = ?");
                            cardsPurchase.setString(1, inputID);
                            ResultSet cardPData = cardsPurchase.executeQuery();
                            List<String> cardsForPurchase = new ArrayList<>();
                            while(cardPData.next()){
                                cardsForPurchase.add(cardPData.getString("card_number").trim());
                            }
                            if(cardsForPurchase.isEmpty()){ //user has no cards on account
                                System.out.println("There are no cards currently attached to Customer ID: " + inputID + ". Unable to complete purchase.");
                            }else{
                                System.out.println("Printing card data: ");
                                System.out.println();
                                cardPData = cardsPurchase.executeQuery();
                                while(cardPData.next()){
                                    System.out.println("Card number: " + cardPData.getString("card_number").trim());
                                    System.out.println("Card type: " + cardPData.getString("card_type").trim());
                                    System.out.println("-----------------------------------------");
                                }
                                boolean validateCardLoop = true;
                                String cardForPurchase = "";
                                System.out.println("Please enter the card number of the card you want to use for this purchase: ");
                                System.out.println();
                                System.out.print("Choice: ");
                                while(validateCardLoop){ //validate user card choice
                                    String dummy = scan.next();
                                    if(cardsForPurchase.contains(dummy)){
                                        cardForPurchase = dummy;
                                        validateCardLoop = false;
                                    }else{
                                        System.out.println("Invalid input. Please input a valid card number from the given list.");
                                        scan.nextLine();
                                    }
                                }
                                PreparedStatement getCardTypeP = conn.prepareStatement(("select card_type from card where TRIM(card_number) = ?"));
                                getCardTypeP.setString(1, cardForPurchase);
                                ResultSet cardTypeForPurchase = getCardTypeP.executeQuery();
                                String cardTypeP = "";
                                while(cardTypeForPurchase.next()){
                                    cardTypeP = cardTypeForPurchase.getString("card_type").trim();
                                }

                                //card number used - cardForPurchase
                                //type of card used - cardTypeP

                                if(cardTypeP.equals("Credit")){
                                    PreparedStatement getCreditBal = conn.prepareStatement("select balance_due from creditcard where TRIM(card_number) = ? ");
                                    getCreditBal.setString(1, cardForPurchase);
                                    ResultSet getCreditBalSet = getCreditBal.executeQuery();
                                    double currentBalance = 0.0;
                                    if(getCreditBalSet.next()){
                                        currentBalance = getCreditBalSet.getDouble("balance_due");
                                    }
                                    PreparedStatement getRunningBal = conn.prepareStatement("select running_balance from creditcard where TRIM(card_number) = ? ");
                                    getRunningBal.setString(1, cardForPurchase);
                                    ResultSet getRunningBalSet = getRunningBal.executeQuery();
                                    double currentRun = 0.0;
                                    if(getRunningBalSet.next()){
                                        currentRun = getRunningBalSet.getDouble("running_balance");
                                    }

                                    PreparedStatement getCreditLimit = conn.prepareStatement("select credit_limit from creditcard where TRIM(card_number) = ?");
                                    getCreditLimit.setString(1, cardForPurchase);
                                    ResultSet getCreditLimitSet = getCreditLimit.executeQuery();
                                    double creditLimit = 0.0;
                                    if(getCreditLimitSet.next()){
                                        creditLimit = getCreditLimitSet.getDouble("credit_limit");
                                    }
                                    double creditLimitBalanceDiff = creditLimit - currentRun;
                                    System.out.println();
                                    System.out.println("Current card running balance: $" + currentRun);
                                    System.out.println("Credit limit: $" + creditLimit);
                                    System.out.println("Difference: $" + creditLimitBalanceDiff);
                                    System.out.println();
                                    scan.nextLine();
                                    System.out.print("Enter the vendor name: ");
                                    String vendorName = scan.nextLine();
                                    String vendorID = "V" + vendorName;
                                    double purchaseAmount = 0.0;
                                    boolean purchaseLoop = true;
                                    System.out.println("Enter the purchase amount: ");
                                    while(purchaseLoop){
                                        if(scan.hasNextDouble()){
                                            double dummy = scan.nextDouble();
                                            if(dummy > 0.0){
                                                purchaseAmount = dummy;
                                                purchaseLoop = false;
                                            }else{
                                                System.out.println("Invalid number. Purchases must be greather than $0.");
                                                continue;
                                            }
                                        }else{
                                            System.out.println("Invalid input. Please enter a dollar amount.");
                                            scan.nextLine();
                                            continue;
                                        }
                                    }
                                    if(creditLimitBalanceDiff > purchaseAmount){
                                        double newRunning = currentRun + purchaseAmount;
                                        System.out.println();
                                        System.out.println("Printing purchase information: ");
                                        System.out.println("Card number: " + cardForPurchase);
                                        System.out.println("Card type: " + cardTypeP);
                                        System.out.println("Purchase amount: $" + purchaseAmount);
                                        System.out.println("Vendor name: " + vendorName);
                                        System.out.println("Updated running balance after purchase: $" + newRunning);
                                        System.out.println("Would you like to accept this purchase? Enter Y for N or N for No");
                                        System.out.println();
                                        System.out.println("Choice: ");
                                        boolean newPurchaseLoop = true;
                                        String newPurchaseChoice = "";
                                        while(newPurchaseLoop){
                                            String dummy = scan.next();
                                            if(dummy.equals("Y") || dummy.equals("N")){
                                                newPurchaseChoice = dummy;
                                                newPurchaseLoop = false;
                                            }else{
                                                System.out.println("Invalid input. Please enter either Y or N");
                                                continue;
                                            }
                                        }

                                        String newPurchaseID = "";
                                        if(newPurchaseChoice.equals("Y")){
                                            boolean purCheck = true;
                                            PreparedStatement getPurchaseIDs = conn.prepareStatement("select purchase_id from purchases");
                                            ResultSet purchaseIDSet = getPurchaseIDs.executeQuery();
                                            List<String> allPurchases = new ArrayList<>();
                                            while(purchaseIDSet.next()){
                                                allPurchases.add(purchaseIDSet.getString("purchase_id").trim());
                                            }
                                            Random random = new Random();
                                            while(purCheck){
                                                int purchaseIDBase = 10000 + random.nextInt(100000-10000);
                                                newPurchaseID = "P" + purchaseIDBase;

                                                if(!allPurchases.contains(newPurchaseID)){
                                                    PreparedStatement updateBalance = conn.prepareStatement("UPDATE CreditCard SET running_balance = ? WHERE UPPER(TRIM(card_number)) = UPPER(TRIM(?))");
                                                    updateBalance.setDouble(1, newRunning);
                                                    updateBalance.setString(2, cardForPurchase);
                                                    updateBalance.executeUpdate();

                                                    //create new purchase entry
                                                    PreparedStatement insertPurchase = conn.prepareStatement("insert into purchases(purchase_id, vendor_name, vendor_id, card_number, purchase_amount, purchase_date) values (?, ?, ?, ?, ?, ?)");
                                                    insertPurchase.setString(1,newPurchaseID);
                                                    insertPurchase.setString(2, vendorName);
                                                    insertPurchase.setString(3, vendorID);
                                                    insertPurchase.setString(4, cardForPurchase);
                                                    insertPurchase.setDouble(5, purchaseAmount);
                                                    insertPurchase.setDate(6, Date.valueOf(LocalDate.now()));
                                                    insertPurchase.executeUpdate();

                                                    System.out.println("Success! Purchase went through.");
                                                    System.out.println();
                                                    purCheck = false;
                                                }
                                            }

                                        }else if(newPurchaseChoice.equals("N")){
                                            System.out.println("Cancelling purchase."); 
                                        }

                                    }else{
                                        System.out.println("Can not complete transaction - purchase amount would send you over credit limit.");
                                        System.out.println();

                                    }

                                }else if(cardTypeP.equals("Debit")){
                                    PreparedStatement getDebitBal = conn.prepareStatement("select acct_balance from account inner join card on account.account_id = card.account_id where TRIM(card.card_number) = ?");
                                    getDebitBal.setString(1, cardForPurchase);
                                    ResultSet getDebitBalSet = getDebitBal.executeQuery();
                                    double debitBalance = 0.0;
                                    if(getDebitBalSet.next()){
                                        debitBalance = getDebitBalSet.getDouble("acct_balance");
                                    }

                                    System.out.println();
                                    System.out.println("Current checking account balance: $" + debitBalance);
                                    System.out.println();
                                    scan.nextLine();
                                    System.out.print("Enter the vendor name: ");
                                    String vendorName = scan.nextLine();
                                    String vendorID = "V" + vendorName;
                                    double purchaseAmount = 0.0;
                                    boolean purchaseLoop = true;
                                    System.out.println("Enter the purchase amount: ");
                                    while(purchaseLoop){
                                        if(scan.hasNextDouble()){
                                            double dummy = scan.nextDouble();
                                            if(dummy > 0.0){
                                                purchaseAmount = dummy;
                                                purchaseLoop = false;
                                            }else{
                                                System.out.println("Invalid number. Purchases must be greater than $0.");
                                                continue;
                                            }
                                        }else{
                                            System.out.println("Invalid input. Please enter a dollar amount.");
                                            scan.nextLine();
                                            continue;
                                        }
                                    }

                                    //card number used - cardForPurchase
                                    //type of card used - cardTypeP

                                    if(debitBalance > purchaseAmount){
                                        double tempBal = debitBalance - purchaseAmount;
                                        System.out.println();
                                        System.out.println("Printing purchase information: ");
                                        System.out.println("Card number: " + cardForPurchase);
                                        System.out.println("Card type: " + cardTypeP);
                                        System.out.println("Purchase amount: $" + purchaseAmount);
                                        System.out.println("Vendor name: " + vendorName);
                                        System.out.printf("New balance: $%.2f%n", tempBal);
                                        System.out.println("Would you like to accept this purchase? Enter Y for N or N for No");
                                    
                                        System.out.println();
                                        System.out.println("Choice: ");
                                        boolean newPurchaseLoop = true;
                                        String newPurchaseChoice = "";
                                        while(newPurchaseLoop){
                                            String dummy = scan.next();
                                            if(dummy.equals("Y") || dummy.equals("N")){
                                                newPurchaseChoice = dummy;
                                                newPurchaseLoop = false;
                                            }else{
                                                System.out.println("Invalid input. Please enter either Y or N");
                                                continue;
                                            }
                                        }

                                        String newPurchaseID = "";
                                        if(newPurchaseChoice.equals("Y")){ //user accepts purchase
                                            double newBalance = debitBalance - purchaseAmount;
                                            boolean purCheck = true;
                                            PreparedStatement getPurchaseIDs = conn.prepareStatement("select purchase_id from purchases");
                                            ResultSet purchaseIDSet = getPurchaseIDs.executeQuery();
                                            List<String> allPurchases = new ArrayList<>();
                                            while(purchaseIDSet.next()){
                                                allPurchases.add(purchaseIDSet.getString("purchase_id").trim());
                                            }
                                            Random random = new Random();
                                            while(purCheck){
                                                int purchaseIDBase = 10000 + random.nextInt(100000 - 10000); // Generate a random number
                                                newPurchaseID = "P" + purchaseIDBase;

                                                if(!allPurchases.contains(newPurchaseID)){ //if the purchase ID is not already taken...
                                                    PreparedStatement getDebitID = conn.prepareStatement("select account.account_id from account inner join card on account.account_id = card.account_id where TRIM(card.card_number) = ?");
                                                    getDebitID.setString(1, cardForPurchase);
                                                    ResultSet getDebitIDSet = getDebitID.executeQuery();
                                                    String accountIDDeb = "";
                                                    if(getDebitIDSet.next()){
                                                        accountIDDeb = getDebitIDSet.getString("account_id");
                                                    }

                                                    //update account balance
                                                    PreparedStatement updateBal = conn.prepareStatement("update account SET acct_balance = ? where account_id = ?");
                                                    updateBal.setDouble(1, newBalance);
                                                    updateBal.setString(2, accountIDDeb);
                                                    updateBal.executeUpdate();

                                                    //create new purchase entry
                                                    PreparedStatement insertPurchase = conn.prepareStatement("insert into purchases(purchase_id, vendor_name, vendor_id, card_number, purchase_amount, purchase_date) values (?, ?, ?, ?, ?, ?)");
                                                    insertPurchase.setString(1,newPurchaseID);
                                                    insertPurchase.setString(2, vendorName);
                                                    insertPurchase.setString(3, vendorID);
                                                    insertPurchase.setString(4, cardForPurchase);
                                                    insertPurchase.setDouble(5, purchaseAmount);
                                                    insertPurchase.setDate(6, Date.valueOf(LocalDate.now()));
                                                    insertPurchase.executeUpdate();

                                                    System.out.println("Success! Purchase went through.");
                                                    System.out.println();
                                                    purCheck = false;
                                                }
                                            }

                                        }else if(newPurchaseChoice.equals("N")){ //user rejects purchase
                                            System.out.println("Cancelling purchase.");                                        
                                        }
                                    }else{
                                        System.out.println("Can not complete transaction - purchase amount is greater than account balance.");
                                    }
                                }

                            }
                        }


                    break;

                    case 12: //fund transfer
                    PreparedStatement fundTransferThrow = conn.prepareStatement("select account_id from account inner join customer on account.customer_id = customer.customer_id where TRIM(customer.customer_id) = ? ");
                    fundTransferThrow.setString(1, inputID);
                    ResultSet fundTransferThrowSet = fundTransferThrow.executeQuery();
                    if(!fundTransferThrowSet.isBeforeFirst()){
                        System.out.println("No accounts attached to Customer ID: " + inputID + ". Unable to make fund transfer.");
                        System.out.println();
                        break;
                    }
        
                    List<String> fundTransferThrowIDs = new ArrayList<>();
                    while(fundTransferThrowSet.next()){
                        fundTransferThrowIDs.add(fundTransferThrowSet.getString("account_id").trim());
                    }
        
                    System.out.println("Please input your Account ID in which you would like to transfer funds FROM: ");
                    for(int i = 0; i < fundTransferThrowIDs.size(); i++){
                        System.out.print("(" + fundTransferThrowIDs.get(i).trim() + ")   " );
                    }
        
                    boolean fundTransferIDThrowLoop = true;
                    String fundTransferIDThrow = "";
                    System.out.println();
                    System.out.print("Choice: ");
                    while(fundTransferIDThrowLoop){
                        String dummy = scan.next();
                        if(fundTransferThrowIDs.contains(dummy.trim())){
                            fundTransferIDThrow = dummy;
                            System.out.println("Successful ID inputted!");
                            fundTransferIDThrowLoop = false;
                        }else{
                            System.out.println("Invalid Account ID inputted. Please input the Account ID from the given list.");
                            continue;
                        }
                    }

                    fundTransferThrowIDs.remove(fundTransferIDThrow);
                    if(fundTransferThrowIDs.size() < 1){
                        System.out.println("Error: only one account attached to Customer ID: " + inputID + ". Cancelling fund transfer.");
                        System.out.println();
                        break;
                    }

                    System.out.println("Please input your Account ID in which you would like to transfer funds TO: ");
                    for(int i = 0; i < fundTransferThrowIDs.size(); i++){
                        System.out.print("(" + fundTransferThrowIDs.get(i).trim() + ")   " );
                    }
        
                    boolean fundTransferIDCatchLoop = true;
                    String fundTransferIDCatch = "";
                    System.out.println();
                    System.out.print("Choice: ");
                    while(fundTransferIDCatchLoop){
                        String dummy = scan.next();
                        if(fundTransferThrowIDs.contains(dummy.trim())){
                            fundTransferIDCatch = dummy;
                            System.out.println("Successful ID inputted!");
                            fundTransferIDCatchLoop = false;
                        }else{
                            System.out.println("Invalid Account ID inputted. Please input the Account ID from the given list.");
                            continue;
                        }
                    }
                    System.out.println("How much would you like to transfer from Account " + fundTransferIDThrow + " to Account " + fundTransferIDCatch);
                        double newTransferAmount = 0.0;
                        boolean newTransferAmountLoop = true;
                        while(newTransferAmountLoop){
                            if(scan.hasNextDouble()){
                                double dummy = scan.nextDouble();
                                if(dummy > 0.0){
                                    newTransferAmount = dummy;
                                    newTransferAmountLoop = false;
                                }else{
                                    System.out.println("Invalid input. Transfer amounts must be greater than $0.");
                                    continue;
                                }
                                
                            }else{
                                System.out.println("Invalid input. Please enter a valid numeric amount.");
                                String junk = scan.nextLine();
                                continue;
                            }
                        }
                    
                    PreparedStatement getTransferThrowInfo = conn.prepareStatement(("select * from account where TRIM(account_id) = ?"));
                    getTransferThrowInfo.setString(1, fundTransferIDThrow);
                    ResultSet getTransferThrowSet = getTransferThrowInfo.executeQuery();

                    String throwAccountType = "";
                    double throwAccountBal = 0.0;
                    double throwMinBal = 0.0;
                    while(getTransferThrowSet.next()){
                        throwAccountType = getTransferThrowSet.getString("account_type");
                        throwAccountBal = getTransferThrowSet.getDouble("acct_balance");
                        if(throwAccountType.equals("Savings")){
                            throwMinBal = getTransferThrowSet.getDouble("min_balance");
                        }
                    }

                    PreparedStatement getTransferCatchInfo = conn.prepareStatement(("select * from account where TRIM(account_id) = ?"));
                    getTransferCatchInfo.setString(1, fundTransferIDCatch);
                    ResultSet getTransferCatchSet = getTransferCatchInfo.executeQuery();

                    String catchAccountType = "";
                    double catchAccountBal = 0.0;
                    double catchMinBal = 0.0;
                    while(getTransferCatchSet.next()){
                        catchAccountType = getTransferCatchSet.getString("account_type");
                        catchAccountBal = getTransferCatchSet.getDouble("acct_balance");
                        if(catchAccountType.equals("Savings")){
                            catchMinBal = getTransferCatchSet.getDouble("min_balance");
                        }
                    }

                    double throwNewBalance = throwAccountBal - newTransferAmount;
                    double catchNewBalance = catchAccountBal + newTransferAmount;

                   if(throwAccountType.equals("Checking") && throwNewBalance < 0){
                    System.out.println("Error: loan payment would make checking account " + fundTransferIDThrow + " balance negative. Cancelling fund transfer. ");
                   }else{
                    System.out.println();
                    System.out.println("Printing fund transfer information: ");
                    System.out.println();
                    System.out.println("Action: Account " + fundTransferIDThrow + " depositing $" + newTransferAmount + " into Account " + fundTransferIDCatch);
                    System.out.println();
                    System.out.println("Account: " + fundTransferIDThrow);
                    System.out.println("Account type: " + throwAccountType);
                    System.out.println("Balance before transfer: $" + String.format("%.2f", throwAccountBal));
                    System.out.println("Balance after transfer $" + String.format("%.2f", throwNewBalance));
                    if(throwAccountType.equals("Savings")){
                        System.out.println("Minimum balance: $" + throwMinBal);
                    }
                    System.out.println();
                    System.out.println("Account: " + fundTransferIDCatch);
                    System.out.println("Account type: " + catchAccountType);
                    System.out.println("Balance before transfer: $" + String.format("%.2f", catchAccountBal));
                    System.out.println("Balance after transfer $" + String.format("%.2f", catchNewBalance));
                    if(catchAccountType.equals("Savings")){
                        System.out.println("Minimum balance: $" + catchMinBal);
                    }
                    System.out.println();
                    System.out.println("Do you accept this transfer? Enter Y for Yes or N for No");
                    if(throwAccountType.equals("Savings")){
                        System.out.println("Reminder: a $35 penalty will be added to your account if you are below your minimum balance of $" + throwMinBal);
                    }
                    System.out.println();
                    System.out.print("Choice: ");
                    boolean newTransferPayAcceptLoop = true;
                    String newTransferPayChoice = "";
                    while(newTransferPayAcceptLoop){
                        String dummy = scan.next();
                        if(dummy.equals("Y") || dummy.equals("N")){
                            newTransferPayChoice = dummy;
                            newTransferPayAcceptLoop = false;
                        }else{
                            System.out.println("Invalid input. Please enter either Y or N");
                            continue;
                        }
                    }

                    String newTransferPaymentID = "";
                    if(newTransferPayChoice.equals("Y")){
                        boolean fundTransferLoopCheck = true;
                        PreparedStatement getFTIDStmt = conn.prepareStatement(("select transfer_id from fundtransfers"));
                        ResultSet getFTIDSet = getFTIDStmt.executeQuery();
                        List<String> allFTIDs = new ArrayList<>();
                        while(getFTIDSet.next()){
                            allFTIDs.add(getFTIDSet.getString("transfer_id").trim());
                        }

                        Random random = new Random();
                        while(fundTransferLoopCheck){
                            int FTIDBase = 10000 + random.nextInt(100000 - 10000);
                            newTransferPaymentID = "T" + FTIDBase;

                            if(!allFTIDs.contains(newTransferPaymentID)){
                                if(throwAccountType.equals("Savings") && throwNewBalance < throwMinBal){
                                    throwNewBalance = throwNewBalance - 35.00;
                                    PreparedStatement insertFT = conn.prepareStatement("insert into FundTransfers(transfer_id, source_account_id, destination_account_id, transfer_amount, transfer_date) values (?, ?, ?, ?, ?)");
                                    insertFT.setString(1, newTransferPaymentID);
                                    insertFT.setString(2, fundTransferIDThrow);
                                    insertFT.setString(3, fundTransferIDCatch);
                                    insertFT.setDouble(4, newTransferAmount);
                                    insertFT.setDate(5, Date.valueOf(LocalDate.now()));
                                    insertFT.executeUpdate();

                                    PreparedStatement updateThrow = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                    updateThrow.setDouble(1, throwNewBalance);
                                    updateThrow.setString(2, fundTransferIDThrow);
                                    updateThrow.executeUpdate();

                                    PreparedStatement updateCatch = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                    updateCatch.setDouble(1, catchNewBalance);
                                    updateCatch.setString(2, fundTransferIDCatch);
                                    updateCatch.executeUpdate();

                                    System.out.println("Fund transfer successful! Since your account balance (Account ID: " + fundTransferIDThrow + ") is below your minimum balance, a $35 penalty was carried out.");
                                    System.out.println();

                                    fundTransferLoopCheck = false;

                                }else{
                                    PreparedStatement insertFTOK = conn.prepareStatement("insert into FundTransfers(transfer_id, source_account_id, destination_account_id, transfer_amount, transfer_date) values (?, ?, ?, ?, ?)");
                                    insertFTOK.setString(1, newTransferPaymentID);
                                    insertFTOK.setString(2, fundTransferIDThrow);
                                    insertFTOK.setString(3, fundTransferIDCatch);
                                    insertFTOK.setDouble(4, newTransferAmount);
                                    insertFTOK.setDate(5, Date.valueOf(LocalDate.now()));
                                    insertFTOK.executeUpdate();

                                    PreparedStatement updateThrowOK = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                    updateThrowOK.setDouble(1, throwNewBalance);
                                    updateThrowOK.setString(2, fundTransferIDThrow);
                                    updateThrowOK.executeUpdate();

                                    PreparedStatement updateCatchOK = conn.prepareStatement("update account set acct_balance = ? where UPPER(TRIM(account_id)) = UPPER(TRIM(?))");
                                    updateCatchOK.setDouble(1, catchNewBalance);
                                    updateCatchOK.setString(2, fundTransferIDCatch);
                                    updateCatchOK.executeUpdate();

                                    System.out.println("Fund transfer successful!");
                                    System.out.println();

                                    fundTransferLoopCheck = false;
                                }
                            }
                        }


                    }else{
                        System.out.println("Cancelling fund transfer.");
                        System.out.println();
                    }


                   }


                    
                    break;
            
                    case 13:
                        System.out.println("Returning to main menu...");
                        printMainMenu();
                        return;
                }
            }
        }catch(SQLException se){
            se.printStackTrace();
            System.out.println("[Error]: Connection error. Re-enter login data");
        }
    }

    public static void newCustomerMenu(Connection conn){
        Scanner scan = new Scanner(System.in);
        try{
            boolean newCIDCheck = true;
            String newCID = "";
            String newCManID = "";
            System.out.println("Enter your name: ");
            String userName = scan.nextLine();
            PreparedStatement getCustomerIDs = conn.prepareStatement("select customer_id from customer");
            ResultSet getCustomerIDSet = getCustomerIDs.executeQuery();
            List<String> allCIDs = new ArrayList<>();
            while(getCustomerIDSet.next()){
                allCIDs.add(getCustomerIDSet.getString("customer_id").trim());
            }
            System.out.println();

            Random randomManagaer = new Random();
            int randomNumber = randomManagaer.nextInt(5) + 1;
            if(randomNumber == 1){
                newCManID = "1001";
            }else if(randomNumber == 2){
                newCManID = "2002";
            }else if(randomNumber == 3){
                newCManID = "3003";
            }else if(randomNumber == 4){
                newCManID = "4004";
            }else if(randomNumber == 5){
                newCManID = "5005";
            }

            Random random = new Random();
            while(newCIDCheck){
                int newCustIDBase = 10000 + random.nextInt(100000 - 10000);
                newCID = String.valueOf(newCustIDBase);

                if(!allCIDs.contains(newCID)){
                    PreparedStatement insertNewCustomer = conn.prepareStatement("insert into customer(customer_id, name, manager_id) values (?, ?, ?)");
                    insertNewCustomer.setString(1, newCID);
                    insertNewCustomer.setString(2, userName);
                    insertNewCustomer.setString(3, newCManID);
                    insertNewCustomer.executeUpdate();

                    System.out.println("Success! New Customer information: ");
                    System.out.println("Name: " + userName);
                    System.out.println("Customer ID: " + newCID);
                    System.out.println("Manager ID attached: " + newCManID);
                    System.out.println();

                    newCIDCheck = false;
                }

            }

            printMainMenu();
            return;

        }catch(SQLException se){
            se.printStackTrace();
            System.out.println("[Error]: Connection error. Re-enter login data");
        }
    }
}
