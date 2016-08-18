#Sparkommender

This project has been entered into IBM Spark competition. Official project page:
http://devpost.com/software/sparkommender.

For Devpost-style summary of the project please refer [here](DEVPOST.md).

It is currently hosted on IBM Bluemix Containers Service and runs on http://sparkommender.com

It uses Kaggle Expedia hotel recommendation dataset: https://www.kaggle.com/c/expedia-hotel-recommendations/data

    3.8G  train.csv         - the training set
    264M  test.csv          - the test set
    132M  destinations.csv  - hotel search latent attribute

[Sparkommender-ml](sparkommender-ml) holds the machine learning part of the project.

Summary of the machine learning part of the project is shared as Spark-notebooks both in the original and in the pdf format [here](sparkommender-ml/notebooks).

[Sparkommender-service](sparkommender-service) holds the web service part of the project.

Performance and stability simulation code is
[here](sparkommender-ml/src/test/scala/sparkommender/gatling/SparkommenderSimulation.scala).
The results of running one million recommendations are available on: http://sparkommender.com/assets/simulation/index.html
