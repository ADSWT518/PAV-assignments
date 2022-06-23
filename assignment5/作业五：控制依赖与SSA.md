# 作业五：控制依赖与SSA

<center>唐亚周 519021910804</center>

> **建议：**
>
> 由于本次作业需要作出多个树、图，对于作业中此类结构的呈现方式，给出一定建议：
>
> 若手写，对于下标等细节请务必保证清晰可读。
>
> 若使用电子文档，可尝试使用某些作图工具，如[Graphviz](http://www.graphviz.org/)、LaTeX的[tikz](https://github.com/pgf-tikz/pgf)等，可一定程度降低布局、排版的工作量。
>
> 对于SSA形式，若不便于作图或编写公式，可以用Toy IR的形式呈现。其中，局部变量%x的第0个版本用%x.0来表示，Phi函数用类似于%x.1 = phi [ %x.0 B1 ] [ %x.2 B2 ] 的形式表示。Toy IR的语法在作业四的基础上作如下修改：
>
> LOCAL := %\w+(\.\d+)?
>
> InstDef := ... | PhiInst
>
> PhiInst := LOCAL = phi ([ Value LABEL ])+

一、对龙书图9.10（595页）所示的控制流图，建立**后向支配树**，并以表格形式列出**控制依赖关系**（仿照课件PA03-CD第13页，应包含所有顶点和边）。

答：后向支配树如图所示

<img src="/home/adswt518/.config/Typora/typora-user-images/image-20220415095201452.png" alt="image-20220415095201452" style="zoom: 33%;" />

控制依赖关系如下：

|        | B1   | B2   | B3   | B4   | B5   | B6   |
| ------ | ---- | ---- | ---- | ---- | ---- | ---- |
| S->B1  |      |      |      |      |      |      |
| B1->B2 |      |      |      |      |      |      |
| B2->B3 |      |      |      |      |      |      |
| B3->B4 |      |      | √    | √    |      |      |
| B4->B3 |      |      |      |      |      |      |
| B3->B5 |      |      |      |      |      |      |
| B5->B2 |      | √    | √    |      | √    |      |
| B5->B6 |      |      |      |      |      |      |
| B6->E  |      |      |      |      |      |      |

二、对龙书图9.10所示的控制流图，建立**前向支配树**，并构造**SSA形式**。

答：前向支配树如图所示

<img src="/home/adswt518/.config/Typora/typora-user-images/image-20220415095220735.png" alt="image-20220415095220735" style="zoom:33%;" />

Dominance Frontier 如下

| DF(B1) | DF(B2) | DF(B3)   | DF(B4) | DF(B5) | DF(B6) |
| ------ | ------ | -------- | ------ | ------ | ------ |
| {}     | {B2}   | {B2, B3} | {B3}   | {B2}   | {}     |

SSA形式如下：

```assembly
define i32 @main() {
B1:
    %a.0 = 1
    %b.0 = 2
    jmp B2
B2:
	%e.0 = phi [%e.3 B5]
	%d.0 = phi [%d.1 B2] [%d.3 B3]
	%c.0 = phi [%c.1 B5]
	%b.1 = phi [%b.0 B1] [%b.2 B5]
    %c.1 = add %a.0 %b.1
    %d.1 = sub %c.1 %a.0
    jmp B3
B3:
	%e.1 = phi [%e.0 B2] [%e.2 B4]
    %d.2 = phi [%d.1 B2] [%d.4 B4]
    %d.3 = add %b.1 %d.2
    br 1 B5 B4
B4:
    %d.4 = add %a.0 %b.1
    %e.2 = add %e.1 1
    jmp B3
B5:
    %b.2 = add %a.0 %b.1
    %e.3 = sub %c.1 %a.0
    br 0 B6 B2
B6:
    %a.1 = mul %b.2 %d.3
    %b.3 = sub %a.1 %d.3
	ret 0
}
```

