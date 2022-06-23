import ast

variable_list = []

class v(ast.NodeVisitor):
    def visit_Assign(self, node: ast.Assign):
        for target in node.targets:
            if isinstance(target, ast.Name) and target.id not in variable_list:
                variable_list.append(target.id)


if __name__ == "__main__":

    sample_file = open("./p2_sample.py", "r")
    sample_code = sample_file.read()
    sample_ast = ast.parse(sample_code)
    print(ast.dump(sample_ast))

    x = v()
    x.visit(sample_ast)
    for i in variable_list:
        print(i)
