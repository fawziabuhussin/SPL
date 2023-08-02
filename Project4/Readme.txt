General Description

We want to build software that manages supermarket chains, called BGU Mart. The software should support managing a large number of employees and the buying/selling of products. The software should also manage the inventory and thus contact various suppliers, who supply products. Sells and deliveries of products should be also registered and logged for tax purposes.

We need your help to build the software by implementing a tool using Python and SQLite.
Method and Technical Description

You will build a sqlite3 database that will hold the employee, supplier, product, branch, and activity tables.
The database filename will be bgumart.db.
You will have to implement three Python modules: initiate.py, action.py, and printdb.py.

The Database Structure

The database bgumart.db has five tables.

    Employees: This table holds information about the employees.
    Suppliers: This table holds information about the suppliers.
    Products: This table holds information about the products.
    Branches: This table holds information about the branches.
    Activities: This table holds information about all activities of the chain including sales and deliveries.

Overall, the tables should be defined as follows:

            CREATE TABLE employees (
                id              INT         PRIMARY KEY,
                name            TEXT        NOT NULL,
                salary          REAL        NOT NULL,
                branche    INT REFERENCES branches(id)
            );
    
            CREATE TABLE suppliers (
                id                   INTEGER    PRIMARY KEY,
                name                 TEXT       NOT NULL,
                contact_information  TEXT
            );


            CREATE TABLE products (
                id          INTEGER PRIMARY KEY,
                description TEXT    NOT NULL,
                price       REAL NOT NULL,
                quantity    INTEGER NOT NULL
            );

            CREATE TABLE branches (
                id                  INTEGER     PRIMARY KEY,
                location            TEXT        NOT NULL,
                number_of_employees INTEGER
            );
   
            CREATE TABLE activities (
                product_id      INTEGER REFERENCES products(id),
                quantity        INTEGER NOT NULL,
                activator_id    INTEGER NOT NULL,
                date            TEXT    NOT NULL
            );

initiate.py

This module builds the database and inserts the initial data from the configuration file. When run, it will be given a configuration file as an argument. For example:

python3 initiate.py config.txt

If the database file already exists remove it.

Initiate.py should create a “fresh” database with the tables as specified, parse the configuration file, and store the data given in the configuration file in the database appropriately.

You may assume that the configuration file exists and that the syntax and the data are valid.
action.py

This module manages the supermarket activities, i.e. sales and deliveries (buys). When run, it will be given an actions file as an argument. It will perform each action in the order it appears in the file and then exits.

You may assume that the configuration file exists and that the syntax and the data are valid but you need to check that the quantity of the sold product is enough. If for any reason an action may not be fulfilled do NOTHING!! (Do not print an error message, Do not "sale" part of the quantity, etc...)
printdb.py

This module prints the database and will be checked automatically, therefore, you should follow the instruction very carefully.

    For each table print in a new line the name of the table followed by its records/tuples (each in a row). The tuples should be printed in ascending order of the primary key except for the activities table which it should be ordered by the date. The printing order of the tables is also in ascending order: Activities, Branches, Employees, Products, and Suppliers.
    Print a detailed employees report with the following information:
    Name, Salary, Working location, total sales income. The output should be printed in ascending order of the employee name. If two or more employees share the same name their inner print order is not important. You should print each employee in a row using a single space between the fields.
    Print a detailed activity report with the following information:
    date of activity, item description, quantity, name of seller, and the name of the supplier. If the activity is a sale the name of the supplier should be ‘None’, If the activity is supplying the name of the seller should be ‘None’. The tuples should be printed from the oldest to the newest. If there is no activity do not print. You must use SQL SELECT (with ‘join’, ‘order by’ etc…) to complete this.

Note:

    Use Python's print() function to print your data and tuples, do not format your output just use the default print behavior. However, If you need to print a list of Python’s objects use the __str__ function to override the default print of an object since the default print prints only the reference.  __str__ the equivalent of Java's toString.
    You may add modules as you need but you may not remove or change the file names of these three modules.
    You are not obligated to use the template provided, but it should make your life easier
    All three modules should run as a standalone program, i.e. can be run from the command line as the main module.
    We will run each of your module from the command line, for example:
    > initiate configfile.txt
    > printdb
    > action actionfile.txt
    > printdb

Configuration and action Files
Configuration file

Each line in the configuration file represents either an Employee(E), Supplier(S), Product(P) or Branch(C). 
For example:

B,3,Chicago,40 represents a branch with id 3 located in Chicago with 40 employees.

E,106,Sue Davis,75000,3 represents an employee with id 106, named Sue Davis with a 75000 yearly salary that is working in branch 3.

P,5,Mango,2,7 represents a product with id 5, its description is Mango, the price is 2 shekel and the quantity is 7.

S,6,Jkl Enterprises,(678) 901-2345 represents a supplier with id is 6, named Jkl Enterprises and its contact information is (678) 901-2345.

Note:

    E, S, P, and B at the beginning of each input row define record types and should not be inserted into the database. An example of a configuration file and its output is supplied on the assignment page.
    Employee IDs and Supplier IDs are unique, meaning an employee ID can not repeat itself as a supplier ID and vice versa.
    (Employee-IDs ꓵ Supplier-IDs = Ø).

Action file

Each line in the action file represents an activity. An activity can be either a sale or a supply arrival. When quantity < 0 it is a sale activity, when quantity > 0 it is a supply arrival, and quantity=0 is illegal. For example:

3, 500, 56, 20230110 represents that supplier 56 supplied 500 units of product 3 on 10/Jan/2023.

100, -500, 1234, 20230110 represents that employee 1234 sold 500 units of product 100 on 10/Jan/2023.

If the current product quantity is less than the quantity in the sale activity the action should be ignored. Do not print any message.
Development Environment

    You should use Python 3.9 (or above) and sqlite3. You can use a lower version of python, but it is not recommended.

    You can use any text editor to program the assignment, but make sure your code works when running it from the terminal as specified.
