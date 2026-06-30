@echo off
:: Hides the raw command text from the terminal to keep the output clean

echo === Compiling JavaLibrary ===

:: Creates the output directory for compiled binary files (.class) if it does not exist yet
if not exist out mkdir out

:: Executes the Java Compiler (javac)
:: -d out: Sends the compiled bytecode files to the 'out' directory
:: -sourcepath src: Points to the root directory where the human-readable source code lives
:: The '^' symbol acts as a visual line continuation character in the Windows command prompt
javac -d out -sourcepath src ^
  src\exception\LibraryException.java ^
  src\model\User.java ^
  src\model\Administrator.java ^
  src\model\Librarian.java ^
  src\model\Book.java ^
  src\model\Patron.java ^
  src\model\Loan.java ^
  src\controller\DataManager.java ^
  src\controller\AuthenticationService.java ^
  src\controller\BookService.java ^
  src\controller\PatronService.java ^
  src\controller\LoanService.java ^
  src\controller\ReportService.java ^
  src\controller\LibraryManager.java ^
  src\view\UIConstants.java ^
  src\view\UIHelper.java ^
  src\view\BooksPanel.java ^
  src\view\PatronsPanel.java ^
  src\view\LoansPanel.java ^
  src\view\ReportsPanel.java ^
  src\view\LibraryGUI.java ^
  src\Main.java

:: Checks if the compiler's exit code code is not equal to zero (which means a syntax error occurred)
IF %ERRORLEVEL% NEQ 0 ( 
    echo [ERROR] Compilation failed. 
    pause 
    exit /b 1 
)

echo === Running JavaLibrary ===

:: Initializes the JVM and fires up the main entry point class, looking for class dependencies inside 'out'
java -cp out Main

:: Holds the command prompt window open after closing the app so you can read any console logs or tracking outputs
pause