package at.fhtw.regression.trainer.models;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CSVParameters {

  List<CSVParameter> parameters = new ArrayList<>();

  public void addToCSVParameter (CSVParameter csvParameter) {
    parameters.add(csvParameter);
  }

  public void writeToCSV (String filePath) throws IOException {
    if (parameters == null || parameters.isEmpty()) {
      System.out.println("No data to write (releaseValues list is null or empty).");
      return;
    }

    CsvMapper csvMapper = new CsvMapper();
    CsvSchema schema = csvMapper.schemaFor(CSVParameter.class).withHeader().withLineSeparator("\n");
    csvMapper.writer(schema).writeValue(new File(filePath), parameters);
    System.out.println("Daten wurden erfolgreich in '" + filePath + "' geschrieben.");
  }
}
