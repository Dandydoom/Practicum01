import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * PersonGenerator.java
 * Prompts user to enter person records and saves them to a comma-delimited text file.
 * Uses Java NIO for thread-safe file writing and SafeInput for bulletproofed console input.
 *
 * @author Kirby Fortney
 */
public class PersonGenerator
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        ArrayList<String> records = new ArrayList<>();
        boolean addMore = true;

        System.out.println("=== Person Data File Generator ===");

        // Keep looping until the user says they are done entering people
        while (addMore)
        {
            // Collect each field using SafeInput so bad input is blocked and repeated
            String id        = SafeInput.getNonZeroLenString(in, "Enter Person ID (e.g. 000001)");
            String firstName = SafeInput.getNonZeroLenString(in, "Enter First Name");
            String lastName  = SafeInput.getNonZeroLenString(in, "Enter Last Name");
            String title     = SafeInput.getNonZeroLenString(in, "Enter Title (Mr., Mrs., Dr., Esq., etc.)");
            int    yob       = SafeInput.getInt(in, "Enter Year of Birth");

            // Build the comma-delimited record string
            String record = id + ", " + firstName + ", " + lastName + ", " + title + ", " + yob;

            // Confirm with the user before adding to the list
            if (SafeInput.getYNConfirm(in, "Save this record? [" + record + "]"))
            {
                records.add(record);
                System.out.println("Record saved to list!");
            }
            else
            {
                System.out.println("Record discarded.");
            }

            // Ask if they want to add another person
            addMore = SafeInput.getYNConfirm(in, "Add another person?");
        }

        // If nothing was entered, exit gracefully
        if (records.isEmpty())
        {
            System.out.println("No records entered. Exiting.");
            return;
        }

        // Ask for the filename to save to (goes into the src folder)
        String fileName = SafeInput.getNonZeroLenString(in, "Enter filename to save (e.g. PersonTestData.txt)");

        // Build the full path pointing to the src folder of this project
        File workingDirectory = new File(System.getProperty("user.dir"));
        Path file = Paths.get(workingDirectory.getPath() + "\\src\\" + fileName);

        try
        {
            // NIO pattern: wrap BufferedWriter around a BufferedOutputStream
            OutputStream out = new BufferedOutputStream(Files.newOutputStream(file, CREATE));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

            // Write each record on its own line
            for (String rec : records)
            {
                writer.write(rec, 0, rec.length());
                writer.newLine();
            }

            writer.close(); // Must close to flush the buffer and seal the file
            System.out.println("\nFile saved successfully: " + file.toString());
        }
        catch (IOException e)
        {
            System.out.println("Error writing file!");
            e.printStackTrace();
        }
    }
}
