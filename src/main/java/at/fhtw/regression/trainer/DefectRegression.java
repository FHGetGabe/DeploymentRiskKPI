package at.fhtw.regression.trainer;

import org.tribuo.ImmutableFeatureMap;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.math.la.DenseMatrix;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.regression.RegressionFactory;
import org.tribuo.regression.Regressor;
import org.tribuo.regression.sgd.linear.LinearSGDModel;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;

import java.nio.file.Paths;
import java.util.Random;

public class DefectRegression {

    public static void main(String[] args) throws Exception {

        CSVLoader<Regressor> loader = new CSVLoader<>(',', new RegressionFactory());
        var dataSource = loader.loadDataSource(Paths.get("src/main/resources/stories.csv"), "defects");

        // Train/Test Split
        var splitter = new TrainTestSplitter<>(dataSource, 1, 42L);
        var train = new MutableDataset<>(splitter.getTrain());

        // LinearSGDTrainer konfigurieren
        LinearSGDTrainer trainer = new LinearSGDTrainer(
                new SquaredLoss(),    // Loss
                new AdaGrad(0.1),     // Lernrate
                5000,                   // Epochen
                train.size(),         // MiniBatch = Dataset-Größe
                42,                  // Seed
                new Random(42).nextInt()        // RNG
        );

        Model<Regressor> model = trainer.train(train);
        LinearSGDModel linearModel = (LinearSGDModel) model;

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
    }

}

