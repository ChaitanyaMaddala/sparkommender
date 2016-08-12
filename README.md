#Sparkommender

Fast, Scalable and Portable Hotel Recommender Microservice.

##About
Sparkommender is a project demonstrating how Apache Spark can be used to
to fulfill a real business need. In this particular example, it tackles the challenge of
using the existing data set to make the best possible hotel recommendations
in real-time to any other web service or directly to users
visiting the page on computer or mobile devices.

For Devpost-style summary of the project refer [here](DEVPOST.md).

##Summary
This project is split into three phases.

[Phase One](PHASE_ONE.md) of the project is concerned about the initial
exploration and analysis of the data.
Followed by trying different Machine Learning algorithms to come up with
the best model for recommending hotels.

[Phase Two](PHASE_TWO.md) builds the actual Web Service that
can be invoked via a REST interface to get recommendations using
the models built in the previous phase.
A simple front-end example app is also included which can be used on computers
or mobile devices.

[Phase Three](PHASE_THREE.md) looks at the portability and scalability aspects of the app.
As the app leverages Docker, it can be easily run on various platforms
like IBM Bluemix Containers Service or Docker Cloud/AWS.
It also includes performance test metrics generated with Gatling Stress Tool.
As a simulation, one million of recommendations were made for the test dataset.
