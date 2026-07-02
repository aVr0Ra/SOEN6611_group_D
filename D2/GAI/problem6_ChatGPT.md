Recommended metric: **Cognitive Complexity by SonarSource**

It is the best fit for iBank because the goal is **readability / understandability**, not only test-path counting. Cognitive Complexity was designed to estimate how difficult code is for a developer to understand, and it applies directly to Java methods, classes, and applications.

| Option                                | Strength                                   | Limitation                                              |
| ------------------------------------- | ------------------------------------------ | ------------------------------------------------------- |
| Flesch / natural-language readability | Easy to calculate                          | Not designed for source code                            |
| Halstead metrics                      | Measures operator/operand complexity       | Hard to explain qualitatively for OO Java readability   |
| Cyclomatic Complexity                 | Authoritative and useful for testing       | Measures independent paths, not human understandability |
| **Cognitive Complexity**              | Designed for source-code understandability | Requires static analysis or manual rule application     |

**Calculation method:**
For each Java method, start at 0 and add points for control-flow structures such as `if`, `else if`, `switch`, loops, `catch`, logical operator sequences, recursion, and additional nesting. Nesting increases the score because deeply nested logic is harder to follow.

**Threshold interpretation:**
Use Sonar’s standard rule: **Cognitive Complexity per method should not exceed 15**. Scores above 15 suggest the method may be difficult to understand and should be considered for refactoring. ([SonarQube][1])

**Recommended slide-ready wording:**

> For iBank, we selected **Cognitive Complexity** as the readability / understandability metric. Unlike Cyclomatic Complexity, which focuses mainly on the number of execution paths, Cognitive Complexity estimates the mental effort required to understand a method. This makes it more appropriate for a Java Swing application such as iBank, where readability depends on event handlers, GUI logic, service methods, conditionals, and nesting.
>
> We interpret methods with Cognitive Complexity **≤ 15** as acceptable. Methods above this threshold indicate potential understandability issues and should be reviewed for simplification, extraction, or reduced nesting.

**Qualitative conclusion template for iBank:**

> The iBank codebase is considered understandable when most methods remain below the Cognitive Complexity threshold of 15. Low scores indicate that the GUI panels, services, and model classes use straightforward control flow. Any methods exceeding 15 should be treated as readability hotspots, especially if they contain nested event-handling logic or complex validation rules.

[1]: https://next.sonarqube.com/sonarqube/coding_rules?open=java%3AS3776&rule_key=java%3AS3776&utm_source=chatgpt.com "Cognitive Complexity of methods should not be too high - Rules"
