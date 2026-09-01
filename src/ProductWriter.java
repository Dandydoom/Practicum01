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
 * ProductWriter.java
 * Prompts user to enter product records and saves them to a comma-delimited text file.
 * Uses Java NIO for thread-safe file writing and SafeInput for bulletproofed console input.
 *
 * @author Kirby Fortney
 */
public class ProductWriter
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        ArrayList<String> records = new ArrayList<>();
        boolean addMore = true;

        System.out.println("=== Product Data File Writer ===");

        // Keep looping until the user says they are done entering products
        while (addMore)
        {
            // Collect each field using SafeInput so bad input is blocked and repeated
            String id          = SafeInput.getNonZeroLenString(in, "Enter Product ID (e.g. 000001)");
            String name        = SafeInput.getNonZeroLenString(in, "Enter Product Name");
            String description = SafeInput.getNonZeroLenString(in, "Enter Description (short sentence)");
            double cost        = SafeInput.getDouble(in, "Enter Cost (e.g. 600.0)");

            // Build the comma-delimited record string
            String record = id + ", " + name + ", " + description + ", " + cost;

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

            // Ask if they want to add another product
            addMore = SafeInput.getYNConfirm(in, "Add another product?");
        }

        // If nothing was entered, exit gracefully
        if (records.isEmpty())
        {
            System.out.println("No records entered. Exiting.");
            return;
        }

        // Ask for the filename to save to (goes into the src folder)
        String fileName = SafeInput.getNonZeroLenString(in, "Enter filename to save (e.g. ProductTestData.txt)");

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
