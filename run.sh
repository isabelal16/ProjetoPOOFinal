#!/bin/bash
# Tells the operating system shell to interpret this automation script using Bash

echo "=== Compiling JavaLibrary ==="

# Creates the target output directory (-p prevents errors if the folder already exists)
mkdir -p out

# Executes the Java Compiler saving bytecodes inside the 'out' directory
# The backslash '\' allows long single commands to be neatly broken into multiple lines in Unix terminals
javac -d out -sourcepath src \
  src/exception/LibraryException.java \
  src/model/User.java src/model/Administrator.java src/model/Librarian.java \
  src/model/Book.java src/model/Patron.java src/model/Loan.java \
  src/controller/DataManager.java src/controller/AuthenticationService.java \
  src/controller/BookService.java src/controller/PatronService.java \
  src/controller/LoanService.java src/controller/ReportService.java \
  src/controller/LibraryManager.java \
  src/view/UIConstants.java src/view/UIHelper.java \
  src/view/BooksPanel.java src/view/PatronsPanel.java \
  src/view/LoansPanel.java src/view/ReportsPanel.java \
  src/view/LibraryGUI.java src/Main.java

# Checks if the exit status code ($?) of the compilation command is Not Equal (-ne) to 0 (indicating failure)
[ $? -ne 0 ] && echo "[ERROR] Compilation failed." && exit 1

echo "=== Running JavaLibrary ==="

# Boostraps the Java virtual environment referencing 'out' as the classpath index and launches Main
java -cp out Main