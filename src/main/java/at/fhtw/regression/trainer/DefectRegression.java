package at.fhtw.regression.trainer;

import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import org.tribuo.ImmutableFeatureMap;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.math.la.DenseMatrix;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.regression.RegressionFactory;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.sgd.linear.LinearSGDModel;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;

public class DefectRegression {

  public static void main (String[] args) throws Exception {

    CSVLoader<Regressor> loader = new CSVLoader<>(',', new RegressionFactory());
    var dataSource = loader.loadDataSource(Paths.get("src/main/resources/stories.csv"),
                                           "defectCount");

    // Train/Test Split
    var splitter = new TrainTestSplitter<>(dataSource, 0.8, 42L);
    var train = new MutableDataset<>(splitter.getTrain());
    var test = new MutableDataset<>(splitter.getTest());

    // LinearSGDTrainer konfigurieren
    LinearSGDTrainer trainer = new LinearSGDTrainer(
        new SquaredLoss(),    // Loss
        new AdaGrad(0.1),     // Lernrate
        5000000,                   // Epochen
        train.size(),         // MiniBatch = Dataset-Größe
        43,                  // Seed
        new Random(32).nextInt()        // RNG
    );

    Model<Regressor> model = trainer.train(train);
    LinearSGDModel linearModel = (LinearSGDModel)model;

    DenseMatrix weights = linearModel.getWeightsCopy();
    ImmutableFeatureMap featureMap = linearModel.getFeatureIDMap();

    System.out.println("Lineare Regressionsformel:");

    for (int i = 0; i < weights.getShape()[1] - 1; i++) {
      String featureName = featureMap.get(i).getName();
      double weight = weights.get(0, i);
      System.out.printf("%s * %.4f%n", featureName, weight);
    }

    double bias = weights.get(0, weights.getShape()[1] - 1);
    System.out.printf("Bias (Intercept) = %.4f%n", bias);

    // Modell evaluieren
    System.out.println("\nModellvorhersagen (Testdaten):");
    List<Prediction<Regressor>> predictions = model.predict(test);

    double mseTotal = 0.0; // Mean Squared Error berechnen
    int count = 0;

    for (int i = 0; i < predictions.size(); i++) {
      var prediction = predictions.get(i);
      double observed = test.getExample(i).getOutput().getValues()[0];
      double predicted = prediction.getOutput().getValues()[0];

      System.out.printf("Beispiel %d -> Tatsächlich: %.4f, Vorhergesagt: %.4f%n",
                        i + 1,
                        observed,
                        predicted);

      mseTotal += Math.pow(observed - predicted, 2);
      count++;
    }

    double mse = mseTotal / count; // Mean Squared Error (MSE)
    System.out.printf("\nMittlerer quadratischer Fehler (MSE): %.4f%n", mse);
  }

}

