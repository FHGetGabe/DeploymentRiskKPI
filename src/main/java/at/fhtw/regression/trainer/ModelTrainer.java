package at.fhtw.regression.trainer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.tribuo.DataSource;
import org.tribuo.ImmutableFeatureMap;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.classification.Label;
import org.tribuo.classification.dtree.CARTClassificationTrainer;
import org.tribuo.classification.dtree.impurity.GiniIndex;
import org.tribuo.common.tree.RandomForestTrainer;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.math.la.DenseMatrix;
import org.tribuo.math.optimisers.AdaGradRDA;
import org.tribuo.regression.RegressionFactory;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.baseline.DummyRegressionTrainer;
import org.tribuo.regression.ensemble.AveragingCombiner;
import org.tribuo.regression.rtree.CARTRegressionTrainer;
import org.tribuo.regression.rtree.impurity.MeanSquaredError;
import org.tribuo.regression.sgd.linear.LinearSGDModel;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;
import org.tribuo.regression.xgboost.XGBoostRegressionTrainer;

@Getter
@Setter
public class ModelTrainer {

  protected MutableDataset<Regressor> train;
  protected MutableDataset<Regressor> test;

  public ModelTrainer () throws IOException {
    CSVLoader<Regressor> loader = new CSVLoader<>(',', new RegressionFactory());
    DataSource<Regressor> dataSource = loader.loadDataSource(Paths.get(
                                                                 "src/main/resources/stories.csv"),
                                                             "defectCount");

    TrainTestSplitter<Regressor> splitter = new TrainTestSplitter<>(dataSource, 0.8, 42L);
    train = new MutableDataset<>(splitter.getTrain());
    test = new MutableDataset<>(splitter.getTest());
  }

  private void evaluateModel (Model<Regressor> model) {
    // Vorhersagen machen und evaluieren
    System.out.println("\nModellvorhersagen (Testdaten):");
    List<Prediction<Regressor>> predictions = model.predict(test);

    double mseTotal = 0.0;
    int count = 0;

    // Fehlerbereiche initialisieren
    Map<String, Integer> errorRanges = new HashMap<>();
    errorRanges.put("±5%", 0);
    errorRanges.put("±10%", 0);
    errorRanges.put("±20%", 0);
    errorRanges.put("±25%", 0);
    errorRanges.put("±35%", 0);
    errorRanges.put("±50%", 0);

    for (var prediction : predictions) {
      double observed = test.getExample(count).getOutput().getValues()[0];
      double predicted = prediction.getOutput().getValues()[0];
      double relativeError = Math.abs(observed - predicted) / observed;

      System.out.printf("Beispiel %d -> Tatsächlich: %.4f, Vorhergesagt: %.4f, Fehler: %.2f%%%n",
                        count + 1, observed, predicted, relativeError * 100);

      // Mittleren quadratischen Fehler berechnen
      mseTotal += Math.pow(observed - predicted, 2);

      // Zählen der Vorhersagen in Fehlerbereichen
      if (relativeError <= 0.05) {
        errorRanges.put("±5%", errorRanges.get("±5%") + 1);
      }
      if (relativeError <= 0.10) {
        errorRanges.put("±10%", errorRanges.get("±10%") + 1);
      }
      if (relativeError <= 0.20) {
        errorRanges.put("±20%", errorRanges.get("±20%") + 1);
      }
      if (relativeError <= 0.25) {
        errorRanges.put("±25%", errorRanges.get("±25%") + 1);
      }
      if (relativeError <= 0.35) {
        errorRanges.put("±35%", errorRanges.get("±35%") + 1);
      }
      if (relativeError <= 0.50) {
        errorRanges.put("±50%", errorRanges.get("±50%") + 1);
      }

      count++;
    }

    double mse = mseTotal / count; // Mittlerer quadratischer Fehler (MSE)
    System.out.printf("\nMittlerer quadratischer Fehler (MSE): %.4f%n", mse);

    // Ausgabe der Fehlerbereiche
    System.out.println("\nAnzahl der Vorhersagen in Fehlerbereichen:");
    for (Map.Entry<String, Integer> entry : errorRanges.entrySet()) {
      System.out.printf("%s: %d von %d (%.2f%%)%n",
                        entry.getKey(),
                        entry.getValue(),
                        count,
                        (entry.getValue() * 100.0) / count);
    }
  }

  /*
    private void evaluateModel (Model<Regressor> model) {
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
   */

  public void trainLinearRegression () {

    LinearSGDTrainer trainer = new LinearSGDTrainer(
        new SquaredLoss(),
        new AdaGradRDA(0.001, 0.01),
        500000,
        train.size(),
        40,
        new Random(32).nextInt()
    );

    Model<Regressor> model = trainer.train(train);

    // Überprüfen, ob das Modell vom Typ LinearSGDModel ist
    LinearSGDModel linearModel = (LinearSGDModel)model;

    // Gewichtungen holen
    DenseMatrix weights = linearModel.getWeightsCopy();

    // Feature-Namen holen
    ImmutableFeatureMap featureMap = linearModel.getFeatureIDMap();

    System.out.println("\nGewichtungen der Features:");
    for (int i = 0; i < weights.getDimension2Size() - 1; i++) {
      String featureName = featureMap.get(i).getName();
      double weight = weights.get(0, i);
      System.out.printf("Feature: %s, Gewichtung: %.4f%n", featureName, weight);
    }

    evaluateModel(model);
  }

  public void trainRandomForest () {
    // Konfiguration des DecisionTreeTrainers für Regressionsaufgaben
    CARTRegressionTrainer treeTrainer = new CARTRegressionTrainer(
        15,       // Reduzierte maximale Tiefe des Baums
        1.0f,     // Minimale Gewichtung pro Blatt
        0.001f,   // Kleinerer minimaler Split-Verlust
        0.7f,     // Weniger Features pro Split
        false,
        new MeanSquaredError(),
        42L
    );

    RandomForestTrainer<Regressor> trainer = new RandomForestTrainer<>(
        treeTrainer,
        new AveragingCombiner(),
        500 // Reduzierte Anzahl der Bäume
    );

    // Modell trainieren
    Model<Regressor> model = trainer.train(train);

    // Modell auswerten
    evaluateModel(model);
  }

  public void trainXGBoostRegression () {
    // Konfiguration der XGBoost-Hyperparameter
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("eta", 0.1);           // Kleinere Lernrate für stabilere Konvergenz
    parameters.put("max_depth", 12);     // Tiefer, um komplexere Beziehungen zu erfassen
    parameters.put("min_child_weight", 3); // Mindestgewicht: Strengere Kriterien für Blätter
    parameters.put("gamma", 0.2);         // Kleinerer Splitverlust
    parameters.put("subsample", 0.8);    // Reduziertes Daten-Sampling für Bäume
    parameters.put("colsample_bytree", 0.5); // Reduzierte Merkmalsanzahl
    parameters.put("lambda", 5.0);       // Regularisierungsparameter
    parameters.put("alpha", 0.5);

    // Initialisierung des XGBoostRegressionTrainers
    XGBoostRegressionTrainer trainer = new XGBoostRegressionTrainer(
        XGBoostRegressionTrainer.RegressionType.TWEEDIE, // Regressionstyp für Squared Loss
        200,                         // Anzahl der Bäume (Iterationsschritte)
        parameters                   // Hyperparameter
    );

    // Training des Modells
    Model<Regressor> model = trainer.train(train);

    System.out.println("\nDas XGBoost-Modell wurde erfolgreich trainiert.");

    // Modell evaluieren
    evaluateModel(model);
  }

  public void trainMeanDummy () {
    DummyRegressionTrainer dummyTrainer = DummyRegressionTrainer.createMeanTrainer();
    Model<Regressor> model = dummyTrainer.train(train);
    evaluateModel(model);
  }

  public void trainMedianDummy () {
    DummyRegressionTrainer dummyTrainer = DummyRegressionTrainer.createMedianTrainer();
    Model<Regressor> model = dummyTrainer.train(train);
    evaluateModel(model);
  }
}
