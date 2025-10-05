package at.fhtw.regression.trainer;

import java.io.IOException;

public class  ModelTraining {

  public static void main (String[] args) throws IOException {
    /*
    ModelTrainer modelTrainer = new ModelTrainer();
    modelTrainer.trainMeanDummy();
    modelTrainer.trainMedianDummy();
    modelTrainer.trainLinearRegression();
    modelTrainer.trainRandomForest();
    modelTrainer.trainXGBoostRegression();

     */

    ModelLabelTrainer modelLabelTrainer = new ModelLabelTrainer();
    modelLabelTrainer.trainCARTClassification();
    modelLabelTrainer.trainLibLinearClassification();
    modelLabelTrainer.trainLibSVMClassification();
    modelLabelTrainer.trainKernelSVMClassification();
  }
}
