import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 * ProductReader.java
 * Uses JFileChooser to let the user select a product data file, then reads
 * and displays the records in a formatted table using Java NIO.
 *
 * @author Kirby Fortney
 */
public class ProductReader
{
    public static void main(String[] args)
    {
        JFileChooser chooser = new JFileChooser();
        File selectedFile;
        String rec = "";
        ArrayList<String> lines = new ArrayList<>();

        final int FIELDS_LENGTH = 4;

        String id, name, description;
        double cost;

        try
        {
            // Start the file chooser in the project's working directory
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);

            // Show the dialog — everything runs inside this if-block
            // because the user might close the dialog without picking a file
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();

                // NIO pattern: wrap BufferedReader around a BufferedInputStream
                InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                // Read every line into the ArrayList
                while (reader.ready())
                {
                    rec = reader.readLine();
                    lines.add(rec);
                }
                reader.close(); // Always close after reading

                // Print a formatted table header
                System.out.println("\n");
                System.out.printf("%-10s%-15s%-30s%10s%n", "ID#", "Name", "Description", "Cost");
                System.out.println("==================================================================");

                // Process each line: split by comma, trim whitespace, parse types
                String[] fields;
                for (String line : lines)
                {
                    fields = line.split(",");

                    if (fields.length == FIELDS_LENGTH)
                    {
                        id          = fields[0].trim();
                        name        = fields[1].trim();
                        description = fields[2].trim();
                        cost        = Double.parseDouble(fields[3].trim());

                        // Print each record in neat columns
                        System.out.printf("%-10s%-15s%-30s%10.2f%n",
                                id, name, description, cost);
                    }
                    else
                    {
                        System.out.println("Possibly corrupt record: " + line);
                    }
                }

                System.out.println("\nFile read successfully!");
            }
            else
            {
                // User closed the dialog without picking a file
                System.out.println("No file selected. Please run the program again.");
                System.exit(0);
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
