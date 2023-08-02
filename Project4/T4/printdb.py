from base64 import decode
from persistence import *
import sqlite3
import string

def main():

# Connect to the database
    # Create a cursor
    cursor = repo._conn.cursor()
    # Execute a SELECT statement with the ORDER BY clause
    print("Activities")
    cursor.execute("SELECT * FROM activities ORDER BY date ASC")
    results = cursor.fetchall()
    for row in results:
        new_tup = tuple(x.decode() if type(x) == bytes else x for x in row)
        print(new_tup)  
    print("Branches")
    cursor.execute("SELECT * FROM branches ORDER BY id ASC")
    results = cursor.fetchall()
    for row in results:
        new_tup = tuple(x.decode() if type(x) == bytes else x for x in row)
        print(new_tup)    
    print("Employees")
    cursor.execute("SELECT * FROM employees ORDER BY id ASC")
    results = cursor.fetchall()
    for row in results:
        new_tup = tuple(x.decode() if type(x) == bytes else x for x in row)
        print(new_tup) 
    print("Products")
    cursor.execute("SELECT * FROM products ORDER BY id ASC")
    results = cursor.fetchall()
    for row in results:
        new_tup = tuple(x.decode() if type(x) == bytes else x for x in row)
        print(new_tup)
    print("Suppliers")
    cursor.execute("SELECT * FROM suppliers ORDER BY id ASC")
    results = cursor.fetchall()
    for row in results:
        new_tup = tuple(x.decode() if type(x) == bytes else x for x in row)
        print(new_tup)  
    print()
    print("Employees report")
    cursor.execute("SELECT employees.name, employees.salary, branches.location, COALESCE(SUM((-1) * activities.quantity * products.price),0) AS total_sales_income FROM employees JOIN branches ON employees.branche = branches.id LEFT JOIN activities ON employees.id = activities.activator_id LEFT JOIN products ON activities.product_id = products.id GROUP BY employees.id ORDER BY employees.name ASC")
    results = cursor.fetchall()
    for row in results:
        print(" ".join(i.decode() if isinstance(i, bytes) else str(i) for i in row))
    print()
    print("Activities report")
    cursor.execute("SELECT activities.date, products.description, activities.quantity, COALESCE(employees.name, 'NULL FOUND') AS seller_name, COALESCE(suppliers.name, 'NULL FOUND') AS supplier_name FROM activities LEFT JOIN products ON activities.product_id = products.id LEFT JOIN employees ON activities.activator_id = employees.id LEFT JOIN suppliers ON activities.activator_id = suppliers.id ORDER BY activities.date ASC")
    results = cursor.fetchall()
    for row in results:
        new_row = [i.decode() if isinstance(i, bytes) else i for i in row]   
        new_tuple = tuple(new_row)
        output = "("
        for value in new_tuple:
            if value == str("NULL FOUND"):
                output += " None,"
            elif isinstance(value, int):
                output += " "+ str(value) + ","
            else:
                if new_tuple[0] == value:    
                    output += "'" + str(value) + "',"
                else:
                    output += " '" + str(value) + "',"
        output = output[:-1] + ")"
        print(output)   
        cursor.close()


if __name__ == '__main__':
    main()