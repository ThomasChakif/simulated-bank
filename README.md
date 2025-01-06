This project is a banking simulation developed in Java that models a real-world banking interface. It manages many banking operations for both users and managers, including: loans, credit/debit cards (this includes opening or closing a new card or paying off a card balance), deposits/withdrawals, and fund transfers. The application uses JDBC statements to connect to an Oracle database and make any necessary changes. Included below are the project's directory, restrictions, and good testing practices.



COMMANDS I RUN IN simulated-bank DIRECTORY TO RUN:
To recreate jar file: 
jar cfmv bankSimulation.jar Manifest/Manifest.txt -C . bankSimulation
To run jar file: 
java -jar bankSimulation.jar 



## Directory:

    //simulated-bank

        -README.md

        -bankSimulation.jar

        //bankSimulation

            -bankSimulation.java

            -bankSimulation.class

        //Manifest

            -Manifest.txt

            -ojdbc11.jar
        
        //er-diagram
            -ThomasChakif_BankSimulation_Project_ER-Diagram.pdf

        //dataGeneration

            -dataGeneration.txt


WHAT TO KNOW FOR TESTING:
Good customer to test has Customer ID: 13805. Customer has savings, checking, and investment accounts as well as credit/debit cards attached to them.
They also have loans attached to these accounts, so you can test all functionalities: loan payments, credit card payments, deposits, withdrawals, 
new/replacement cards, card purchases, and fund transfers.

However, you can also select '3' in the first menu to create a blank new customer if testing is preferred that way.

Also, all input is case-sensitive. So 'savings' won't be accepted for 'Savings'.



Important information/constraints for each section:

## Managers:
-a manager is assigned to each customer in the bank 

-a manager is able to view all loans, all accounts, as well the sums of the total balances of all loans and accounts in the bank

-a manager is also able to view a yearly, monthly, and daily breakdown of transactions that take place in the bank


## Customers:
-a customer is able to view all of their accounts, cards, and loans

-a customer is able to make both loan payments and credit card payments

-a customer is able to make both deposits and withdrawals from any of their accounts

-a customer is able to add new accounts, take out new loans, and obtain/replace a card

-a customer is able to make both card purchases and transfers


## Accounts:
-3 types: savings, checking, and investment

-base interest rates: savings - 1.5%, checking - 0.5%, investment - 5.0%

-savings accounts have a starting minimum balance of $250

-a $35 penalty is added if a withdrawal, purchase, or other payment brings it below the minimum balance (this happens for every transaction made while under the minimum balance, not just the initial transaction that brings it below the minimum)

-if a purchase brings a saving account's running balance to above the credit limit, it is rejected

-if a purchase using a checking account is made, it is rejected if it brings the account balance below $0

-investment accounts cannot holds cards/make card transactions

-fund transfers can only be made between a customer's own accounts

-deposits/withdrawals can only be made between a customer's own accounts

-deposits made when starting a new account are purely cash (i.e., you cannot transfer funds from one account to fund the creation of a new account)

-different starting deposits are required depending on account type

-investment accounts can only choose between a set of given assets (each account can only hold one type of asset (stocks, bonds, index funds, etc.))




## Cards:
-2 types: credit and debit cards

-debit cards are exclusive to checking accounts, but credit cards are available to both savings and checking accounts

-investment accounts cannot holds cards/make card transactions

-the 'balance' of a debit card is just the balance of the attached checking account

-purchases using debit cards that bring the associated checking account's balance below $0 are rejected

-new credit cards have a base credit limit of $2000

-new credit card interest rates are random between 15.0% and 30.0%

-cards MUST be attached to a pre-existing savings or checking account. A customer cannot own a card without owning an account to attach it to

-when a new purchase on a credit card is made, the running balance is updated, not the balance due. The balance due represents the amount owed as of the user's last statement, and the running balance is all outstanding debt on the card

-can't make loan payments with credit cards, but can with debit cards through its checking account balance

-payments on credit cards can only be made on cards with a balance_due > 0 (doesn't look at running balance)

-users can replace cards. All information is stored, old card is deleted, and a card with a new number is made with the old card's info

-when a card is deleted, its past purchases are not preserved and therefore do not show up in the running totals within the manager menu

-card payments are recorded in the 'CardPayments' table with a unique payment id and payment date

-card payments can only be made with the balance of the account they're attached to


## Loans:
-2 types: secured and unsecured

-users are able to manually input their loan reason

-interest rates vary between secured and unsecured loans for NEW loans

-interest rates on secured loans: any value between 5.0 and 15.0, unsecured loans: any value between 15.0 and 30.0

-all new loan terms are 36 months, or 3 years for simplicity. Minimum payments are calculated (in a simple manner) as (loan payment/months left) / interest rate

-loans MUST be attached to an account (can be any kind). A custoemr cannot have a loan without owning an account to attach it to

-loans have a status: Not Paid Off/Open if the loan amount > 0 and Paid Off/Closed if the loan amount < 0

-loan payments are recorded in the 'LoanPayments' table with a unique payment id and payment date

-loan payments follow the same checking/saving account balance restraints as all other transactions (minimum balance, credit limit, checking balance constraints)

-loan payments can only be made with the balance of the account they're attached to


## Deposits/Withdrawals:
-I did not enforce the ATM/online only deposit/withdrawals constraint. Both deposits/withdrawals can be made through the interface 

-both deposits and withdrawals can only be made through a user's own accounts

-deposits are made on a cash-only basis - user inputs a cash amount and we assume it to be valid

-deposits can be made to any account type

-deposits are recorded in the 'Deposits' table with a unique deposit id and deposit date

-initial deposits when creating a new account are NOT recorded in the Deposits table

-withdrawals can not make a checking account balance go negative

-if a withdrawal causes a savings account balance to go below the minimum, a $35 penalty is added to the account balance for the initial and each subsequent transaction while under the minimum

-withdrawals are recorded in the 'Withdrawals' table with a unique withdrawal id and withdrawal date


## Card purchases:
-purchases using a debit card can not make the balance of the attached checking account go negative

-if a card purchase causes a savings account balance to go below the minimum, a $35 penalty is added to the account balance for the initial and each subsequent transaction while under the minimum

-debit card purchase lowers checking account balance, while a credit card purchase raises the running total on a credit card

-all card purchases are recorded in the 'Purchases' table with a unqique purchase id and purchase date


## Fund transfers:
-fund transfers can only be made between the same customer's accounts, cannot be made to accounts not owned by them

-fund transfers follow the same balance constraints for checking and savings accounts as previously mentioned for other transactions

-all fund transfers are recorded in the 'FundTransfers' table with a unique transfer id and transfer date