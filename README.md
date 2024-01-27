# CFG to CNF Conversion Project

This project provides a tool to convert a Context-Free Grammar (CFG) language into Chomsky Normal Form (CNF). CNF is a formal grammar type where each production rule contains at most two symbols.

## Usage

The project is written in Java programming language and includes the main class file `Program.java`. You can use this class for the conversion process.

1. Download the project files and open them in a Java development environment (IDE).
2. Run the `Program.java` file.
3. The program will read CFG information from the `CFG.txt` file and perform the conversion process.
4. You can view the converted CNF in the console.

## CFG File Format

For example, a `CFG.txt` file might look like this:

E=a,b,c

S-aA|bB

A-cA|cD

B-aC

C-bA

D-bD|cD|€

## Notes

- This project assumes that the CFG is in a specific format. If the format of the CFG is different, you may need to modify the code.
- Some CFGs may not be convertible to CNF, or may require some modifications during conversion. In such cases, careful examination of the output is important.
