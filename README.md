# Magic Square Generator

## About Magic Squares

Magic squares are a very ancient mathematical curiosity. They consist of a bidimensional array where every individual row, column and diagonal has a sum that is exactly the same. The array can only contain numbers from 1 to $n^2$, $n$ being the number of rows and columns. The magic sum can be calculated by the formula: $M=n(n^{2}+1)/2$. On the example below, since we have a 3x3 square, M is 15.

$M = 3(3^{2}+1)/2 = 3*10/2 = 15$

|     |     |     |
| --- | --- | --- |
| 8   | 3   | 4   |
| 1   | 5   | 9   |
| 6   | 7   | 2   |

If we use a brute force approach to generate magic squares, it's noticeable that is computationally expensive and the possibilities of squares grow exponentially with the dimensions. For 3x3, we have 9! possibilities, that is, 362880 different squares... A 4x4 square amounts to 16! possibilities, which is 20922789888000 (around 21 trillion). The number of possible magic squares for a given dimension also grows exponentially, as we can see from the table below:

| Dimension | Nº of squares | Nº of magic squares |
| --------- | ------------- | ------------------- |
| 3         | 9!            | 1                   |
| 4         | 16!           | 880                 |
| 5         | 25!           | 275305224           |

Obs: we don't count rotations and reflections as different squares, that's why a 3x3 square has only 1 possible magic square configuration.

## The project

The goal of this project is to treat the generation of magic squares as a [constraint satisfaction problem (CSP)](https://en.wikipedia.org/wiki/Constraint_satisfaction_problem). This is better than pure brute force because once you fill one position on the bidimensional array, we already exclude from consideration all the subsequent possibilities that are not compatible with a given constraint (in this case, obtaining the magic sum). There are two different implementations here: both use recursive backtracking, but one of them uses a forward checking technique and the other doesn't. Backtracking is a sort of refined brute force. At each node, we eliminate choices that are obviously not possible and proceed to recursively check only those that have potential. This way, at each depth of the tree, we mitigate the number of choices to consider in the future.

![recursive backtracking pseudo code](./recursive-backtracking.png)

On the implementation without forward checking, we have a domain which is the unassigned numbers. Before assigning a number, we check if that assignment is compatible with the constraints (if it doesn't result on a row, column or diagonal sum bigger than the magic sum). Forward checking in this case would mean having for each position of the square a different domain, that is updated every time a new number is assigned to the square. So, forward checking implies reducing considerably the number of visited nodes, since we exclude from the domain of each cell those numbers that are not compatible with our constraints. You can check the details of each implementation on the `recursive-backtracking/Main.java` and `forward-checking/Main.java` files of this project.

## How to run the project

1. Go to the folder of the implementation you wish to test, that is, either `recursive-backtracking`or `forward-checking`.
2. Compile the program:

```
javac Main.java
```

3. Run the program specifying as argument the dimension of the square. For instance, for 3x3 squares, execute:

```
java Main 3
```

## Benchmarking
