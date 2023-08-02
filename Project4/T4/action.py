from DAO import *
from persistence import *

import sys

def main(args : list):
    inputfilename : str = args[1]
    with open(inputfilename) as inputfile:
        for line in inputfile:
            splittedline : list = line.strip().split(", ")
            # apply the action (and insert to the table) if possible
            new_activitie = Activitie(splittedline[0],splittedline[1],splittedline[2],splittedline[3])
            repo.activities.insert(new_activitie)
            pId = int(splittedline[0])
            a_quantity = int(splittedline[1])
            prdt = repo.products.find_product(pId)
            if int(splittedline[1]) > 0: #supply
                repo.products.update_the_quantity(pId, prdt[1] + a_quantity)
            else :             # sale
                if prdt[1] > (a_quantity)*(-1):
                    repo.products.update_the_quantity(pId, prdt[1] + a_quantity)
            

if __name__ == '__main__':
    main(sys.argv)