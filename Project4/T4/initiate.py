from DTO import*
from persistence import *

import sys
import os


def add_branche(splittedline : list):
    new_branche = Branche(splittedline[0],splittedline[1],splittedline[2])
    repo.branches.insert(new_branche)

def add_supplier(splittedline : list):
    new_supplier = Supplier(splittedline[0],splittedline[1],splittedline[2])
    new_supplier.id = splittedline[0]
    new_supplier.name = splittedline[1]
    new_supplier.contact_information = splittedline[2]
    repo.suppliers.insert(new_supplier)


def add_product(splittedline : list):
    new_product = Product(splittedline[0],splittedline[1],splittedline[2],splittedline[3])
    repo.products.insert(new_product)


def add_employee(splittedline : list):
    new_employee = Employee(splittedline[0],splittedline[1],splittedline[2],splittedline[3])
    repo.employees.insert(new_employee)


adders = {  "B": add_branche,
            "S": add_supplier,
            "P": add_product,
            "E": add_employee}

def main(args : list):
    inputfilename = args[1]
    # delete the database file if it exists
    repo._close()
    # uncomment if needed
    if os.path.isfile("bgumart.db"):
        os.remove("bgumart.db")
    repo.__init__()
    repo.create_tables()
    with open(inputfilename) as inputfile:
        for line in inputfile:
            splittedline : list[str] = line.strip().split(",")
            adders.get(splittedline[0])(splittedline[1:])

if __name__ == '__main__':
    main(sys.argv)