#Phase Two

## Table of Contents
1. [Leveraging Spark Job Server](#Leveraging Spark Job Server)
2. [Optimisations](#Exploratory Data Analysis)
3. [REST Interface and Demo App](#REST Interface and Demo App)
5. [Summary](#Summary)

Explain how I levarage the SPark Job Server
how I created jobs for model init and caching
and a job to execute predictions

how it connects to Spark


how simple it is to integrate via REST


script to add and init new models and context

## Spark Job Server

Build Sparkommender jar and upload to Job Server

    sbt assembly
    curl --data-binary @/Users/radek.ostrowski/git/sparkommender/target/scala-2.11/sparkommender.jar localhost:8090/jars/sparkommender

Create the context:(done automatically now)

    POST: localhost:8090/contexts/sparkommender-context?num-cpu-cores=4&memory-per-node=2g&context-factory=spark.jobserver.context.SQLContextFactory&dependent-jar-uris=file:///sparkommender/jars/sparkommender.jar

    POST: localhost:8090/contexts/sparkommender-context?num-cpu-cores=4&memory-per-node=2g&context-factory=spark.jobserver.context.SQLContextFactory

Init the job:

    POST: localhost:8090/jobs?context=sparkommender-context&appName=sparkommender&classPath=sparkommender.jobserver.InitJob&sync=true


add to context: spark.scheduler.mode=FAIR ?