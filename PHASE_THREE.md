#Phase Three

## Table of Contents
1. [Deploying to Various Clouds](#Deploying to Various Clouds)
2. [Performance Tests](#Performance Tests)
3. [Summary](#Summary)

Deploy to Bluemix and DockerCloud
take screenshots


Check how to set up a load-balancer and launch multiple instances
check if the app can scale.


Create separate context per model, to hopefully increase the performance



Performance seems slow on the Job server

1M of recommendations made for the test dataset


So I can just cache in memory mixed model,
and for other models call to Job Server to Spark

Run Gatling on mixed model only