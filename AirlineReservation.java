
import java.util.Scanner;

import javax.lang.model.util.ElementScanner14;

import java.io.FileNotFoundException;
import java.io.File;
public class AirlineBookingProgram {
    /* Delimiters and Formatters */
    private static final String CSV_DELIMITER = ",";
    private static final String COMMAND_DELIMITER = " ";
    private static final String PLANE_FORMAT = "%d\t | %s | %s \n";

    /* Travel Classes */
    private static final int FIRST_CLASS = 0;
    private static final int BUSINESS_CLASS = 1;
    private static final int ECONOMY_CLASS = 2;
    private static final String[] CLASS_LIST = new String[] {"F", "B", "E"};
    private static final String[] CLASS_FULLNAME_LIST = new String[] {
        "First Class", "Business Class", "Economy Class"};

    /* Commands */
    private static final String[] COMMANDS_LIST = new String[] { "book", 
        "cancel", "lookup", "availabletickets", "upgrade", "print","exit"};
    private static final int BOOK_IDX = 0;
    private static final int CANCEL_IDX = 1;
    private static final int LOOKUP_IDX = 2;
    private static final int AVAI_TICKETS_IDX = 3;
    private static final int UPGRADE_IDX = 4;
    private static final int PRINT_IDX = 5;
    private static final int EXIT_IDX = 6;
    private static final int BOOK_UPGRADE_NUM_ARGS = 3;
    private static final int CANCEL_LOOKUP_NUM_ARGS = 2;

    /* Strings for main */
    private static final String USAGE_HELP =
            "Available commands:\n" +
            "- book <travelClass(F/B/E)> <passengerName>\n" +
            "- book <rowNumber> <passengerName>\n" +
            "- cancel <passengerName>\n" +
            "- lookup <passengerName>\n" +
            "- availabletickets\n" +
            "- upgrade <travelClass(F/B)> <passengerName>\n" +
            "- print\n" +
            "- exit";
    private static final String CMD_INDICATOR = "> ";
    private static final String INVALID_COMMAND = "Invalid command.";
    private static final String INVALID_ARGS = "Invalid number of arguments.";
    private static final String INVALID_ROW = 
        "Invalid row number %d, failed to book.\n";
    private static final String DUPLICATE_BOOK =
        "Passenger %s already has a booking and cannot book multiple seats.\n";
    private static final String BOOK_SUCCESS = 
        "Booked passenger %s successfully.\n";
    private static final String BOOK_FAIL = "Could not book passenger %s.\n";
    private static final String CANCEL_SUCCESS = 
        "Canceled passenger %s's booking successfully.\n";
    private static final String CANCEL_FAIL = 
        "Could not cancel passenger %s's booking, do they have a ticket?\n";
    private static final String UPGRADE_SUCCESS = 
        "Upgraded passenger %s to %s successfully.\n";
    private static final String UPGRADE_FAIL = 
        "Could not upgrade passenger %s to %s.\n";
    private static final String LOOKUP_SUCCESS = 
            "Passenger %s is in row %d.\n";
    private static final String LOOKUP_FAIL = "Could not find passenger %s.\n";
    private static final String AVAILABLE_TICKETS_FORMAT = "%s: %d\n";
    //friday lecture code
    
    /* Static variables - DO NOT add any additional static variables */
    static String [] passengers;
    static int planeRows;
    static int firstClassRows;
    static int businessClassRows;

    /**
     * Runs the command-line interface for our Airline Reservation System.
     * Prompts user to enter commands, which correspond to different functions.
     * @param args args[0] contains the filename to the csv input
     * @throws FileNotFoundException if the filename args[0] is not found
     */

    public static void main (String[] args) throws FileNotFoundException {
        // If there are an incorrect num of args, print error message and quit
        if (args.length != 1) {
            System.out.println(INVALID_ARGS);
            return;
        }
        initPassengers(args[0]); // Populate passengers based on csv input file
        System.out.println(USAGE_HELP);
        Scanner scanner = new Scanner(System.in); //
        while (true) {
            System.out.print(CMD_INDICATOR);
            String line = scanner.nextLine().trim();

            // Exit
            if (line.toLowerCase().equals(COMMANDS_LIST[EXIT_IDX])) {
                scanner.close();
                return;
            }

            String[] splitLine = line.split(COMMAND_DELIMITER);
            splitLine[0] = splitLine[0].toLowerCase(); 

            // Check for invalid commands
            boolean validFlag = false;
            for (int i = 0; i < COMMANDS_LIST.length; i++) {
                if (splitLine[0].toLowerCase().equals(COMMANDS_LIST[i])) {
                    validFlag = true;
                }
            }
            if (!validFlag) {
                System.out.println(INVALID_COMMAND);
                continue;
            }

            // Book
            if (splitLine[0].equals(COMMANDS_LIST[BOOK_IDX])) {
                if (splitLine.length < BOOK_UPGRADE_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        BOOK_UPGRADE_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                try {
                    // book row <passengerName>
                    int row = Integer.parseInt(contents[1]);
                    if (row < 0 || row >= passengers.length) {
                        System.out.printf(INVALID_ROW, row);
                        continue;
                    }
                    // Do not allow duplicate booking
                    boolean isDuplicate = false;
                    for (int i = 0; i < passengers.length; i++) {
                        if (passengerName.equals(passengers[i])) {
                            isDuplicate = true;
                        }
                    }
                    if (isDuplicate) {
                        System.out.printf(DUPLICATE_BOOK, passengerName);
                        continue;
                    }
                    if (book(row, passengerName)) {
                        System.out.printf(BOOK_SUCCESS, passengerName);
                    } else {
                        System.out.printf(BOOK_FAIL, passengerName);
                    }
                } catch (NumberFormatException e) {
                    // book <travelClass(F/B/E)> <passengerName>
                    validFlag = false;
                    contents[1] = contents[1].toUpperCase();
                    for (int i = 0; i < CLASS_LIST.length; i++) {
                        if (CLASS_LIST[i].equals(contents[1])) {
                            validFlag = true;
                        }
                    }
                    if (!validFlag) {
                        System.out.println(INVALID_COMMAND);
                        continue;
                    }
                    // Do not allow duplicate booking
                    boolean isDuplicate = false;
                    for (int i = 0; i < passengers.length; i++) {
                        if (passengerName.equals(passengers[i])) {
                            isDuplicate = true;
                        }
                    }
                    if (isDuplicate) {
                        System.out.printf(DUPLICATE_BOOK, passengerName);
                        continue;
                    }
                    int travelClass = FIRST_CLASS;
                    if (contents[1].equals(CLASS_LIST[BUSINESS_CLASS])) {
                        travelClass = BUSINESS_CLASS;
                    } else if (contents[1].equals(
                                CLASS_LIST[ECONOMY_CLASS])) {
                        travelClass = ECONOMY_CLASS;
                    }
                    if (book(passengerName, travelClass)) {
                        System.out.printf(BOOK_SUCCESS, passengerName);
                    } else {
                        System.out.printf(BOOK_FAIL, passengerName);
                    }
                }
            }

            // Upgrade 
            if (splitLine[0].equals(COMMANDS_LIST[UPGRADE_IDX])) {
                if (splitLine.length < BOOK_UPGRADE_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        BOOK_UPGRADE_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                validFlag = false;
                contents[1] = contents[1].toUpperCase();
                for (int i = 0; i < CLASS_LIST.length; i++) {
                    if (CLASS_LIST[i].equals(contents[1])) {
                        validFlag = true;
                    }
                }
                if (!validFlag) {
                    System.out.println(INVALID_COMMAND);
                    continue;
                }
                int travelClass = FIRST_CLASS;
                if (contents[1].equals(CLASS_LIST[BUSINESS_CLASS])) {
                    travelClass = BUSINESS_CLASS;
                } else if (contents[1].equals(CLASS_LIST[ECONOMY_CLASS])) {
                    travelClass = ECONOMY_CLASS;
                }
                if (upgrade(passengerName, travelClass)) {
                    System.out.printf(UPGRADE_SUCCESS, passengerName, 
                            CLASS_FULLNAME_LIST[travelClass]);
                } else {
                    System.out.printf(UPGRADE_FAIL, passengerName, 
                            CLASS_FULLNAME_LIST[travelClass]);
                }
            }

            // Cancel
            if (splitLine[0].equals(COMMANDS_LIST[CANCEL_IDX])) {
                if (splitLine.length < CANCEL_LOOKUP_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        CANCEL_LOOKUP_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                if (cancel(passengerName)) {
                    System.out.printf(CANCEL_SUCCESS, passengerName);
                } else {
                    System.out.printf(CANCEL_FAIL, passengerName);
                }
            }

            // Lookup
            if (splitLine[0].equals(COMMANDS_LIST[LOOKUP_IDX])) {
                if (splitLine.length < CANCEL_LOOKUP_NUM_ARGS) {
                    System.out.println(INVALID_ARGS);
                    continue;
                }
                String[] contents = line.split(COMMAND_DELIMITER, 
                        CANCEL_LOOKUP_NUM_ARGS);
                String passengerName = contents[contents.length - 1];
                if (lookUp(passengerName) == -1) {
                    System.out.printf(LOOKUP_FAIL, passengerName);
                } else {
                    System.out.printf(LOOKUP_SUCCESS, passengerName, 
                            lookUp(passengerName));
                }
            }

            // Available tickets
            if (splitLine[0].equals(COMMANDS_LIST[AVAI_TICKETS_IDX])) {
                int[] numTickets = availableTickets();
                for (int i = 0; i < CLASS_FULLNAME_LIST.length; i++) {
                    System.out.printf(AVAILABLE_TICKETS_FORMAT, 
                            CLASS_FULLNAME_LIST[i], numTickets[i]);
                }
            }

            // Print
            if (splitLine[0].equals(COMMANDS_LIST[PRINT_IDX])) {
                printPlane();
            }
        }
    }

    /**
     * Initializes the static variables passengers, planeRows,
     *  firstClassRows, and businessClassRows
     *  using the contents of the CSV file named fileName.
     *   /**
         * static String [] passengers;
           static int planeRows;
    static int firstClassRows;
    static int businessClassRows;
     * @param fileName
     * @throws FileNotFoundException
     */
    private static void initPassengers(String fileName) throws 
            FileNotFoundException {
        // TODO
        File f = new File(fileName);
        Scanner s = new Scanner( f );    
    //save, javac, up arrow, up arrow 

    String str = s.nextLine();
    String[] init = str.split(",");
    planeRows = Integer.parseInt( init[0] );
    firstClassRows = Integer.parseInt( init[1] ); //parse to integer if need
    businessClassRows = Integer.parseInt( init[2] );
    passengers = new String[ planeRows ]; 

    
    while( s.hasNextLine() )
    {
        String line = s.nextLine();

        int place = Integer.parseInt(line.substring( 0 , line.indexOf(",") ));     

        passengers[ place ] = line.substring( line.indexOf(",") + 1, line.length() - 1 );
    
    }
    s.close();
}
//what order should we work on method. in order. some method call each other
/**
 * Return the travel class corresponding to the given row.
 * @param row
 * @return
 */
    private static int findClass(int row) {
         /**private static final int FIRST_CLASS = 0;
    private static final int BUSINESS_CLASS = 1;
    private static final int ECONOMY_CLASS = 2; */ //fbuisnes class row firs class row. 
        
    if ( row >= 0 && row < firstClassRows  ) //ranges row=1
        {
            return FIRST_CLASS;
        }

    else if( row >= firstClassRows && row < firstClassRows + businessClassRows )
        {
            return BUSINESS_CLASS;
        }

    else if( row >= firstClassRows + businessClassRows && row < planeRows )
        {
            return ECONOMY_CLASS;
        }

    else{
        return -1;
    }

    }

    /**
     * Return the first row of the given travelClass.
Return -1 if travelClass is not FIRST_CLASS, BUSINESS_CLASS, or ECONOMY_CLASS.
     * @param travelClass
     * @return
     */
    private static int findFirstRow( int travelClass ) {
        if( travelClass == FIRST_CLASS )
        {
            return 0;
        }
        else if( travelClass == BUSINESS_CLASS )
        {
            return firstClassRows; //last index of it is the 1st idnex
        }
        else if( travelClass == ECONOMY_CLASS ) //this one not print. economy returns 0
        {
            return firstClassRows + businessClassRows; 
        }
        else
        {
            return -1;
        }

       
    }

    /**
     * Return the last row of a given travelClass.
Return -1 if travelClass is not FIRST_CLASS, BUSINESS_CLASS, or ECONOMY_CLASS.
     * @param travelClass
     * @return
     */
    private static int findLastRow(int travelClass) { //offby1
       if( travelClass == FIRST_CLASS )
       {
        return firstClassRows -1;
       }
        else if( travelClass == BUSINESS_CLASS )
       {
        return firstClassRows + businessClassRows - 1;
       }
        else if( travelClass == ECONOMY_CLASS ) //not work
       {
        return planeRows - 1;
       }
       else
       {
        return -1;
       }

       
    }

    /**
     * BOOK BY CLASS
     * Book a ticket for passengerName in the travelClass section. 
     * Place passengerName in the first available row of
     *  the selected travelClass. A row is available if there is no other passenger that booked that row.
Return false if no seats are available for the selected travelClass, otherwise return true.
If passengerName is null, return false.
You may assume that travelClass is FIRST_CLASS, BUSINESS_CLASS, or ECONOMY_CLASS.
You may assume that passengerName does not already have a booking.
     * @param passengerName
     * @param travelClass
     * @return
     */
    public static boolean book(String passengerName, int travelClass) {

        if( passengerName == null )
        {
            return false;
        }

        int[] openSeats = availableTickets();

        if( travelClass == FIRST_CLASS)
        {
           if( openSeats[0] == 0 )
           {
               return false;
           }
        }
        if( travelClass == BUSINESS_CLASS)
        {
           if( openSeats[1] == 0 )
           {
               return false;
           }
        }
        if( travelClass == ECONOMY_CLASS)
        {
           if( openSeats[2] == 0 )
           {
               return false;
           }
        }

        for( int i = findFirstRow(travelClass); i <= findLastRow(travelClass); i++ )
        {
            if( passengers[i] == null ) //find 1st available row
            {
                passengers[i] = passengerName;
                return true;
            }
        }
       return false;
    }


    /**
     * BOOK BY ROW
     * Book a ticket for passengerName at the seat number row
     * . If the row is available, place passengerName in the row. 
     * If the row is not available, place passengerName in the first 
     * available** row in the same travel class as row.
Return false if no seats are available in the travel class of row, otherwise return true.
Return false if passengerName is null.
You may assume that row is valid.
     * @param row
     * @param passengerName
     * @return
     */
    public static boolean book(int row, String passengerName) 
    {
         int[] openSeats = availableTickets();

         if( passengerName == null )
         {
             return false;
         }

         if( findClass(row) == FIRST_CLASS)
         {
            if( openSeats[0] == 0 )
            {
                return false;
            }
         }
         if( findClass(row) == BUSINESS_CLASS)
         {
            if( openSeats[1] == 0 )
            {
                return false;
            }
         }
         if( findClass(row) == ECONOMY_CLASS)
         {
            if( openSeats[2] == 0 )
            {
                return false;
            }
         }

         System.out.println("passengers at row "+passengers[row]); 
                if( passengers[ row ] == null )
                {
                    passengers[ row ] = passengerName; //book
                    return true;
                }
                else //if cant book by row, book by class. book by class return trueorfalse
                {
                    System.out.println("testingbook by class "+book(passengerName, findClass( row )));
                    book(passengerName, findClass( row )); //returns boolean bookbyclass
                }
            
        return false; 

         }
        
    

    /**
     * Cancel the booking for passengerName. Remove passengerName from the passengers array.
Return true upon successful removal, false otherwise.
If passengerName is null, return false.
     * @param passengerName
     * @return
     */
    public static boolean cancel( String passengerName ){
         if( passengerName == null )
         {
             return false;
         }

         else
         {
         //how to check if the name dont exist???
            for( int i = 0; i < passengers.length; i++)
            {
                 //calling obj cant be null. equals can be null
                if( passengerName.equals( passengers[i] ))
                {
                    passengers[i] = null; //remove them
                    return true;
                }
            }
            return false; 
        }
    }

    /**
     Look up the row number of passengerName. 
     Return the row number of passengerName, or return -1 if not found.
If passengerName is null, return -1.
     * @param passengerName
     * @return
     */
    public static int lookUp(String passengerName) { //fix now
        int row = -1;
         if( passengerName == null)
         {
             return row;
        }
            for( int i = 0; i < passengers.length; i++ ) //infinite loop null and no exists
            {
                if( passengerName.equals( passengers[i] )  )
                {
                    row = i;
                }
            }
        return row; 
        }
    

    /**
     * Find and return the number of available tickets in each travel class.
The return value should be in the format of [first class, business class, economy cl
     * @return
     */
    public static int[] availableTickets() {
//testing only

int[] tickets = new int[3];
int open1st = 0;
int openBus = 0;
int openEcon = 0;

for( int i = 0; i < firstClassRows; i++)
{
    if( passengers[i] == null )
    {
        open1st++;
    }
}

for( int i = firstClassRows; i < firstClassRows + businessClassRows; i++)
{
    if( passengers[i] == null )
    {
        openBus++;
    }
}

for( int i = firstClassRows + businessClassRows; i < planeRows; i++)
{
    if( passengers[i] == null )
    {
        openEcon++;
    }
}

tickets[0] = open1st;
tickets[1] = openBus;
tickets[2] = openEcon;
return tickets;
}

    /**
     * Upgrade passengerName to a seat in the upgradeClass.
If passengerName is null or passengerName is not found in passengers, return false.
If upgradeClass is lower or equal to passengerName's existing class, return false and do not modify passengers
. (i.e. a First Class passenger trying to 'upgrade' to Business Class)
Otherise, upgrade (move) passengerName to the first available row in upgradeClass.
Return true if the upgrade is successful, false otherwise.
You may assume that travelClass is FIRST_CLASS, BUSINESS_CLASS, or ECONOMY_CLASS.
     * @param passengerName
     * @param upgradeClass
     * @return
     */
    public static boolean upgrade(String passengerName, int upgradeClass) {
        
        if( passengerName == null || lookUp(passengerName) == -1 )
        {
        return false; // TODO
        }

        int[] openSeats = availableTickets();

        //if upgradeclass equal passengers current class or is a lower class
        // findclass took in row, lookup returns row
        if(  upgradeClass >= findClass(lookUp(passengerName))) //lookup returns row of passenger
        {
            return false; //cant downgrade class, cant upgrade to same class
        }

        if( upgradeClass == FIRST_CLASS )
        {
            if( openSeats[0] == 0 )
            {
                return false;
            }
        }
        if( upgradeClass == BUSINESS_CLASS )
        {
            if( openSeats[1] == 0 )
            {
                return false;
            }
        }
        if( upgradeClass == ECONOMY_CLASS )
        {
            if( openSeats[2] == 0 )
            {
                return false;
            }
        }
                cancel(passengerName);//forloop manual cancel
                book(passengerName, upgradeClass);
                return true;
    }

    /**
     * Prints out the names of each of the passengers according to their booked
     * seat row. No name is printed for an empty (currently available) seat.
     */
    public static void printPlane() {
        for (int i = 0; i < passengers.length; i++) {
            System.out.printf(PLANE_FORMAT, i, CLASS_LIST[findClass(i)], 
                    passengers[i] == null ? "" : passengers[i]);
        }
    }
}