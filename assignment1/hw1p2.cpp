#include <chrono>
#include <cmath>
#include <cstring>
#include <iostream>

using namespace std;

const int arraySize = 10000000;

int ConstantFolding(int param)
{
    char arr[10000];
    int a = 0x107;
    int b = a * sizeof(arr);
    int c = sqrt(2.0);
    return a * param + (a + 0x15 / c + strlen("Hello") * b - 0x37) / 4;
}

int CommonSubExpressionElimination(int param1, int param2)
{
    int a = (param2 + 0x107);
    int b = param1 * (param2 + 0x107) + a;
    return a * (param2 + 0x107) + b * (param2 + 0x107);
}

int DeadCode(int param1, int param2)
{
    if (param1 < param2 && param1 > param2) {
        printf("This test can never be true!\n");
    }
    // Empty for loop
    for (int i = 0; i < 1000; i++)
        ;
    // If/else that does the same operation in both cases
    if (param1 == param2) {
        param1++;
    } else {
        param1++;
    }
    // If/else that more trickily does the same operation in both cases
    if (param1 == 0) {
        return 0;
    } else {
        return param1;
    }
}

int StrengthReduction(int* param1, int param2)
{
    int a = param2 * 32;
    int b = a * 7;
    int c = b / 3;
    int d = param2 % 2;
    for (int i = 0; i <= param2; i++) {
        c += param1[i] + 0x107 * i;
    }
    return c + d;
}

int CodeMotion(int* arr)
{
    int sum = 0;
    int foo = 10;
    int bar = 3;
    int n = arraySize;
    for (int i = 0; i < n; i++) {
        sum += arr[i] + foo * (bar + 3);
    }
    return sum;
}

int TailRecursion(int n)
{
    if (n <= 1)
        return 1;
    else
        return n * TailRecursion(n - 1);
}

int LoopUnrolling(int* arr)
{
    int sum = 0;
    int n = arraySize;
    for (int i = 0; i < n; ++i) {
        sum += arr[i];
    }
    return sum;
    // for (int i = 0; i <= n - 4; i += 4) {
    //     sum += arr[i];
    //     sum += arr[i + 1];
    //     sum += arr[i + 2];
    //     sum += arr[i + 3];
    // } // after the loop handle any leftovers
}

int main()
{
    auto t1 = std::chrono::high_resolution_clock::now();

    int* arr = new int[arraySize];
    for (int i = 0; i < arraySize; ++i) {
        arr[i] = i;
    }
    int v1 = ConstantFolding(1000);
    int v2 = CommonSubExpressionElimination(100, 200);
    int v3 = DeadCode(1000, 2000);
    int v4 = StrengthReduction(arr, 1000);
    int v5 = CodeMotion(arr);
    int v6 = TailRecursion(500);
    int v7 = LoopUnrolling(arr);

    auto t2 = std::chrono::high_resolution_clock::now();

    chrono::duration<double, std::milli> fp_ms = t2 - t1;

    cout << "The program\'s output is " << v1 + v2 + v3 + v4 + v5 + v6 + v7 << endl;
    cout << "The program took " << fp_ms.count() << " ms.\n";
    return 0;
}