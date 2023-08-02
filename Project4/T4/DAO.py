#Data Access Objects:
#------------------------------------EMPLOYEESTABLE------------------------------------------------------------------
from DTO import*


class Employees:
    def __init__(self, conn):
        self.conn = conn

    def insert(self, employee):
        self.conn.execute(""" INSERT INTO employees(id,name,salary,branche) VALUES(?,?,?,?)""",
                          [employee.id, employee.name, employee.salary, employee.branche])
        self.conn.commit()


#------------------------------------SUPPLIERTABLE------------------------------------------------------------------

class Suppliers:
    def __init__(self, conn):
        self.conn = conn

    def insert(self, supplier):
        self.conn.execute(""" INSERT INTO suppliers (id,name,contact_information) VALUES (?,?,?)
        """, [supplier.id, supplier.name,supplier.contact_information])
        self.conn.commit()

#-------------------------------------PRODUCTSTABLE-----------------------------------------------------------------------

class Products:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, product):
        self._conn.execute(""" INSERT INTO products(id, description, price, quantity) VALUES(?,?,?,?) """,
                          [product.id, product.description, product.price, product.quantity])
        self._conn.commit()

    def find_product(self, id):
        cursor = self._conn.cursor()
        cursor.execute(""" SELECT id, quantity FROM products 
        WHERE id = (?) """, [id,])
        return (cursor.fetchone())

    def update_the_quantity(self, _id, quantity):
        cursor = self._conn.cursor()
        cursor.execute(""" UPDATE products SET quantity = (?) WHERE id = (?) """, [quantity,_id])
        self._conn.commit()


#-------------------------------------BRANCHESTABLE-----------------------------------------------------------------------



class Branches:
    def __init__(self, conn):
        self.conn = conn

    def insert(self, branche):
        self.conn.execute(""" INSERT INTO branches(id, location, number_of_employees) VALUES(?,?,?) """,
                          [branche.id, branche.location, branche.number_of_employees])
        self.conn.commit()


#-------------------------------------ACTIVATESTABLE-----------------------------------------------------------------------


class Activates:
    def __init__(self, conn):
        self.conn = conn

    def insert(self, activite):
        self.conn.execute(""" INSERT INTO activities (product_id, quantity, activator_id, date) VALUES(?,?,?,?) """,
                          [activite.product_id, activite.quantity, activite.activator_id, activite.date])
        self.conn.commit()

