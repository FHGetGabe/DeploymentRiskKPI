package at.fhtw.regression.trainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TransformToLabel {

  private String convertDefectCountToCategory(double defectCount) {
    int lowerBound = (int) (defectCount / 50) * 50;
    int upperBound = lowerBound + 49;

    if (defectCount < 50) {
      return "0-49"; // Für Werte unter 50
    }
    return lowerBound + "-" + upperBound;
  }

  // Transformiertes CSV erstellen, das alle Spalten enthält
  public void preprocessToLabelCSV(String inputPath, String outputPath) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(inputPath));
    List<String> newLines = new ArrayList<>();

    // Header anpassen (defectCategory hinzufügen)
    String header = lines.get(0).replace("defectCount", "defectCategory");
    newLines.add(header);

    // Daten transformieren
    for (String line : lines.subList(1, lines.size())) { // Header überspringen
      String[] columns = line.split(",");

      if (columns.length < 12) {
        // Sicherstellen, dass die Zeile genug Spalten hat, sonst überspringen
        System.err.println("Zeile übersprungen: " + line);
        continue;
      }

      try {
        // `defectCount` in die Kategorie umwandeln
        double defectCount = Double.parseDouble(columns[1]);
        String defectCategory = convertDefectCountToCategory(defectCount);

        // Spalten aktualisieren: Ersetzen von `defectCount` durch `defectCategory`
        columns[1] = defectCategory;

        // Zeile wieder zusammensetzen und zur neuen Liste hinzufügen
        String transformedLine = String.join(",", columns);
        newLines.add(transformedLine);

      } catch (NumberFormatException e) {
        // Fehler in der Umwandlung behandeln
        System.err.println("Ungültiger Zahlenwert in Zeile: " + line);
      }
    }

    // In die Ausgabedatei schreiben
    Files.write(Paths.get(outputPath), newLines);

    System.out.println("Die transformierte Datei wurde erfolgreich erstellt: " + outputPath);
  }

  public static void main (String[] args) throws IOException {
    TransformToLabel transformToLabel = new TransformToLabel();
    transformToLabel.preprocessToLabelCSV(
        "src/main/resources/stories.csv", // Eingabedatei
        "src/main/resources/labelled-stories.csv" // Ausgabedatei
    );
  }
}