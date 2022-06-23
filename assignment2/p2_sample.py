global1 = 1
global2 = 2

def fun1():
    tmp1 = 2
    tmp2 = 10
    global1 = tmp2
    print(global1)

def fun2(num):
    tmp3 = num
    print(tmp3)

if __name__ == "__main__":
    fun1()
    fun2(global2)