import re

c_vari_pattern = re.compile(r"[a-zA-Z_][a-zA-Z0-9_]*")
uppercamel_pattern = re.compile(r"([A-Z][a-z]*[0-9]*)+")
c_float_pattern = re.compile(r"-?[0-9]*(.[0-9]+)?e-?[0-9]+")

re_pattern = [c_vari_pattern, uppercamel_pattern, c_float_pattern]

c_vari = ["hello123", "_hello123", "hell123o_"]
not_c_vari = ["hello 123", "1hello23", "hello*123"]
uppercamel = ["HelloWorld", "Hello123World", "HelloAWorld123"]
not_uppercamel = ["helloWorld", "He123lloWorld", "123HelloWorld"]
c_float = ["1e9", "-9.99e-9", "-.4e10"]
not_c_float = [".e10", "10000", "3e.9"]

test_str = [c_vari+not_c_vari, uppercamel+not_uppercamel, c_float+not_c_float]

for i in range(0,3):
    print(f"Pattern: {re_pattern[i]}: ")
    for j in test_str[i]:
        if re_pattern[i].fullmatch(j):
            print(f"\tMatch \"{j}\": true")
        else:
            print(f"\tMatch \"{j}\": false")
