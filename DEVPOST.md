#Sparkommender

Fast, Scalable and Portable Hotel Recommender Microservice.

<img src="https://cloud.githubusercontent.com/assets/246085/17652299/b3a2f7b0-6270-11e6-8f3b-8c64168b9b2f.png">

##Inspiration

Recommendation engines are becoming a necessity for every trading company nowadays.
This is particularly important in the travel sector,
where the race is on between the competitors for the market share by keeping old and attracting new customers.
The holy grail is to improve the user experience and at the same time increase the revenue.
Main challenges come both from the shear amount of data like user clicks and bookings and from high
velocity of the incoming users' requests.

First challenge is to be able to gain insights, analyze and build accurate models
when the data is too big to fit into a memory of a single computer.

Second challenge is to actually apply the knowledge we gained (ML models)
in a real business setting like a booking website where the users are not left
waiting for the recommendations to load on the page.


##What it does

**Sparkommender** is a project that tackles hotel recommendation problem at scale.

1. Uses Spark to gain knowledge about the data and build best recommendation models
2. Builds a microservice exposing a REST interface to make recommendations so
any other web service, any computer or mobile application could easily integrate
and make use of the predictions


## How I built it
1. Used portable and sharable Spark notebooks connecting to a Spark Cluster for data analysis and model building.
2. Built Dockerized Play! Web Service with a REST interface
3. Tested portability by running both in IBM Containers Service and Docker Cloud/AWS
4. Tested performance and stability by running Gatling tests (1 Million Recommendations Test URL)
5. Built a simple website integrating the microservice (http://sparkommender.com)

## Challenges I ran into
Random Forest implementation only returns 1 prediction and I didn't find a way to get more

not fast enough -> Spark Job Server

## Accomplishments that I'm proud of


## What I learned
not fast enough -> Spark Job Server

best tool for a given job, and spark is not made for super fast response times

## What's next for Sparkommender

One of the ideas is to use a streaming recommendation engine, where the recommendations
themselves get better over time with the information about the actual new bookings.
However, the nature of the data set used doesn't offer explicit ratings,
but only implicit information like clicks and bookings.
Therefore, in a near future, I could look into adding implicit recommendations to
https://github.com/brkyvz/streaming-matrix-factorization project
and extending the Sparkommender to receive the actual reservations.

Extend to also use top per hotel_market (aka city) instead of top global.

Use some Deep Learning like Tensorflow to come up with a better model

Use Cassandra as a backend with in-memory option for quick serves?
