# Evaluating Test Suite Effectiveness Using PIT

ECE382C Software Testing semester project. Can PIT find weaknesses in a Java test suite after JaCoCo reports full line and branch coverage?

Three JUnit suites exercise the same `OrderRules` class.

| Maven profile | Cases |
| --- | --- |
| `basic` | Representative orders |
| `coverage` | Basic cases plus exceptions and missing paths |
| `mutation` | Coverage cases plus boundary inputs |

## Run

With JDK 17+, Maven, and Python 3.8+ installed, run:

```sh
python3 run_experiments.py
```

The script runs all three profiles, prints line and branch coverage and killed and survived mutant counts, and replaces [`results/results.csv`](results/results.csv) after all three runs succeed.

To run only one JUnit suite, use `mvn -Pcoverage test`. Maven creates `target/` for compiled classes and intermediate reports. `mvn clean` clears it.
