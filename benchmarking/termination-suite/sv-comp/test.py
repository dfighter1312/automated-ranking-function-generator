
import os 
from difflib import SequenceMatcher

def similar(a, b):
    return SequenceMatcher(None, a, b).ratio()


def get_most_similar(x, lst):

    tmplist = [(similar(x, t) , t) for t in lst]
    tmplist = sorted(tmplist, key=lambda x: x[0])
    
    print(tmplist[-1])
    print(x)
    return tmplist[-1][1]


l = [os.path.join(path, name) for path, subdirs, files in os.walk(".") for name in files]
ll = list(filter(lambda x: x.endswith(".java"), l))


cfiles = [os.path.join(path, name) for path, subdirs, files in os.walk("./termination-crafted-lit-C") for name in files]
cfiles = list(filter(lambda x: x.endswith(".c"), cfiles))
cfiles = list(map(lambda x: x[28:], cfiles))


#print(cfiles)

final = []
for x in ll:
    if os.path.exists(x):

        jar = x[:-4] + "jar"
        #if not os.path.exists(jar):
        #    print("No Jar")

        #inp = "./termination-crafted-lit-C/" + input("c file: ")
        inp = get_most_similar(x[38:].split("/")[1], cfiles)
        #print((x,jar,inp))
        final.append((x,jar,"./termination-crafted-lit-C/" + inp))
        #print()

print()
print()
print()
print()
print()
print()
print()
print()
print()


t = "done"

for a,b,c in final:
    if os.path.exists(a) and os.path.exists(b) and os.path.exists(c):
        print("{}, {}, {}, ".format(a,b,c))
    else:
        print("error")
        t = "error"
        exit(0)

print(t)