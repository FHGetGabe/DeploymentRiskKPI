package at.fhtw.regression.trainer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.tribuo.DataSource;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.Prediction;
import org.tribuo.classification.Label;
import org.tribuo.classification.LabelFactory;
import org.tribuo.classification.dtree.CARTClassificationTrainer;
import org.tribuo.classification.dtree.impurity.GiniIndex;
import org.tribuo.classification.liblinear.LibLinearClassificationTrainer;
import org.tribuo.classification.liblinear.LinearClassificationType;
import org.tribuo.classification.libsvm.LibSVMClassificationTrainer;
import org.tribuo.classification.libsvm.SVMClassificationType;
import org.tribuo.common.libsvm.KernelType;
import org.tribuo.common.libsvm.SVMParameters;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.math.kernel.RBF;
import org.tribuo.classification.sgd.kernel.KernelSVMTrainer;

@Getter
@Setter
public class ModelLabelTrainer {

  private MutableDataset<Label> trainClassification;
  private MutableDataset<Label> testClassification;

  public ModelLabelTrainer() throws IOException {
    CSVLoader<Label> loader = new CSVLoader<>(',', new LabelFactory());
    DataSource<Label> dataSource = loader.loadDataSource(Paths.get("src/main/resources/classification-data.csv"), "defectCategory");

    TrainTestSplitter<Label> splitter = new TrainTestSplitter<>(dataSource, 0.8, 42L);
    trainClassification = new MutableDataset<>(splitter.getTrain());
    testClassification = new MutableDataset<>(splitter.getTest());
  }

  private void evaluateClassificationModel (Model<Label> model) {
    // Prognosen für Testdaten erstellen
    System.out.println("\nModellvorhersagen (Testdaten):");
    List<Prediction<Label>> predictions = model.predict(testClassification);

    int correctCount = 0;
    for (Prediction<Label> prediction : predictions) {
      Label actual = prediction.getExample().getOutput();
      Label predicted = prediction.getOutput();

      // Ausgabe der Ergebnisse
      System.out.printf("Tatsächlich: %s, Vorhergesagt: %s%n",
                        actual.getLabel(),
                        predicted.getLabel());

      // Richtigkeit prüfen
      if (actual.equals(predicted)) {
        correctCount++;
      }
    }

    // Genauigkeit berechnen
    double accuracy = (double)correctCount / predictions.size();
    System.out.printf("\nGenauigkeit des Modells: %.2f%%%n", accuracy * 100);
  }

  public void trainCARTClassification () {
    // Initialisierung des CARTClaassificationTrainers mit Hyperparametern
    CARTClassificationTrainer trainer = new CARTClassificationTrainer(
        10,              // Maximale Tiefe des Baums
        1.0f,            // Mindestgewicht eines Blattknotens
        0.001f,          // Minimaler Gewinn bei einem Split
        0.7f,            // Anteil der Merkmale pro Split
        true,            // Verwendung zufälliger Splitpunkte
        new GiniIndex(), // Impurity-Metrik (z. B. Gini)
        42L              // Zufallssamen
    );

    // Modell mit Trainingsdaten trainieren (Label-Datensatz erforderlich)
    Model<Label> model = trainer.train(trainClassification);

    System.out.println("\nDas CART-Klassifikationsmodell wurde erfolgreich trainiert.");

    // Evaluation des Modells
    evaluateClassificationModel(model);
  }

  public void trainLibLinearClassification () {
    // Initialisierung des CARTClaassificationTrainers mit Hyperparametern
    LibLinearClassificationTrainer trainer = new LibLinearClassificationTrainer(
        new LinearClassificationType(LinearClassificationType.LinearType.L2R_LR),// Typ des Modells (Logistische Regression mit L2-Regularisierung)
        1.0,// Regularisierungsparameter C
        1// Anzahl der Threads, die verwendet werden
    );

    // Modell mit Trainingsdaten trainieren (Label-Datensatz erforderlich)
    Model<Label> model = trainer.train(trainClassification);

    System.out.println("\nDas LibLinear-Modell wurde erfolgreich trainiert.");

    // Evaluation des Modells
    evaluateClassificationModel(model);
  }

  public void trainLibSVMClassification () {
    // Erstellen der SVM-Parameter
    SVMParameters<Label> parameters = new SVMParameters<>(
        new SVMClassificationType(SVMClassificationType.SVMMode.C_SVC),// Klassifikationstyp: Support Vector Classification
        KernelType.RBF// Radial Basis Function (RBF)-Kernel
    );

    // Verwenden Sie das interne `svm_parameter`-Objekt zur manuellen Konfiguration
    parameters.getParameters().C = 1.0;       // Regularisierungsparameter (cost)
    parameters.getParameters().gamma = 0.5;  // Gamma für den Kernel
    parameters.getParameters().eps = 0.001;  // Genauigkeit (Convergence Tolerance)
    parameters.getParameters().cache_size = 500.0; // Cache-Größe für den SVM-Algorithmus

    // Trainer mit SVM-Parametern initialisieren
    LibSVMClassificationTrainer trainer = new LibSVMClassificationTrainer(parameters);


    // Modelltraining
    Model<Label> model = trainer.train(trainClassification);

    System.out.println("\nDas LibSVM-Modell wurde erfolgreich trainiert.");

    // Modell evaluieren
    evaluateClassificationModel(model);
  }

  public void trainKernelSVMClassification () {
    // Initialisierung des KernelSVMTrainer mit RBF-Kernel
    KernelSVMTrainer trainer = new KernelSVMTrainer(
        new RBF(1.0),     // Kernel: Radial Basis Function (RBF)
        0.01,             // Regularisationsparameter (lambda)
        100,              // Maximale Iterationen (epochs)
        10,               // Logging-Intervall
        42L               // Zufallssamen für Reproduzierbarkeit
    );

    // Training des Modells
    Model<Label> model = trainer.train(trainClassification);

    System.out.println("Das Kernel-SVM-Klassifikationsmodell wurde erfolgreich trainiert.");

    // Evaluation des Modells
    evaluateClassificationModel(model);

  }
}
