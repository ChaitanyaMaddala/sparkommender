#Sparkommender-ML
This is the Machine Learning part of the project.

##Model Building Instructions

Use provided Spark-notebooks, link them to the uncompressed **Expedia Kaggle** dataset and connect to any Spark cluster to explore the data
and export the models. Or, copy the uncompressed **Expedia Kaggle** dataset into the `data` folder.
Update `setMaster` in [MixedModel](src/main/scala/sparkommender/ml/MixedModel.scala)
file so it points to your Spark cluster and update where
 you want the resulting models stored.
In either case, make the models available for the second part
of the project.

##Performance Simulation Instructions

Update [SparkommenderSimulation](src/test/scala/sparkommender/gatling/SparkommenderSimulation.scala)
to point to your running instance of the Sparkommender service and invoke:

    sbt gatling:test

Gatling generates test results as HTML page by default.

