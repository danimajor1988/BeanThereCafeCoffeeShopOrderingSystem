# Bean There Cafe

Coffee, tea, and pastry ordering system for CS1410 SLCC Fall 2026.

## Run from Eclipse

1. Choose **File > Import > Existing Projects into Workspace** and select the cloned repository.
2. Confirm Eclipse has a JDK/JRE configured for the project.
3. Run `src/cafe/MainFrame.java` as a Java application.

## Run from a terminal

From the repository root, compile and run with a JDK:

```sh
mkdir -p out
javac -d out src/cafe/*.java
java -cp "out:src" cafe.MainFrame
```

On Windows, use `;` instead of `:` in the Java classpath:

```bat
java -cp "out;src" cafe.MainFrame
```

## Pricing

Coffee and matcha start at $3.00; tea starts at $2.00. Medium and large sizes add
$0.50 and $1.00. Iced and frozen drinks add $0.50 and $1.00, oat milk adds
$0.75, extra shots add $1.00 each, syrup adds $0.70, cold foam adds $1.00,
and whipped cream adds $0.50. Sprinkles are free with coffee and matcha.
Croissants and cinnamon rolls are $2.50; chocolate chip cookies and bagels are
$1.00.

The bagel image is not included yet; the receipt displays a text placeholder for
that item.
