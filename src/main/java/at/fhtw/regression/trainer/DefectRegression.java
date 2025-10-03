package at.fhtw.regression.trainer;

import java.nio.file.Paths;
import java.util.List;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.regression.RegressionFactory;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.rtree.CARTJointRegressionTrainer;

public class DefectRegression {

  public static void main(String[] args) throws Exception {
    // Daten laden
    CSVLoader<Regressor> loader = new CSVLoader<>(',', new RegressionFactory());
    var dataSource = loader.loadDataSource(Paths.get("src/main/resources/stories.csv"),
                                           "defectCount");

    // Train-Test-Split
    var splitter = new TrainTestSplitter<>(dataSource, 0.8, 42L);
    var train = new MutableDataset<>(splitter.getTrain());
    var test = new MutableDataset<>(splitter.getTest());

    // CART Regression Trainer konfigurieren
    CARTJointRegressionTrainer trainer = new CARTJointRegressionTrainer();

    // Modell trainieren
    Model<Regressor> model = trainer.train(train);

    // Vorhersagen machen und evaluieren
    System.out.println("\nModellvorhersagen (Testdaten):");
    List<Prediction<Regressor>> predictions = model.predict(test);

    double mseTotal = 0.0;
    int count = 0;

    for (var prediction : predictions) {
      double observed = test.getExample(count).getOutput().getValues()[0];
      double predicted = prediction.getOutput().getValues()[0];

      System.out.printf("Beispiel %d -> Tatsächlich: %.4f, Vorhergesagt: %.4f%n",
                        count + 1,
                        observed,
                        predicted);

      mseTotal += Math.pow(observed - predicted, 2);
      count++;
    }

    double mse = mseTotal / count; // Mittlerer quadratischer Fehler
    System.out.printf("\nMittlerer quadratischer Fehler (MSE): %.4f%n", mse);
  }
}