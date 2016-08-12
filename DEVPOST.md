#Sparkommender

Fast, Scalable and Portable Hotel Recommender Microservice.

##Inspiration

Recommendation engines are becoming a necessity for every trading company nowadays.
This is particularly important in the travel sector,
where the race is on between the competitors for the market share by keeping old and attracting new customers.

The holy grail is to improve the user experience and at the same time increase the revenue.
Main challenges come both from the shear amount of data like user clicks and bookings and from high velocity of the incoming requests.

First challenge is to be able to gain insights, analyze and build accurate models
when the data is too big to fit into a memory of a single computer. We already know that
Spark is a great solution here...

Second challenge is to actually apply the knowledge we gained, like ML models,
in a real-time application without leaving the user waiting for the page to load.

Portability and Scalability...


##What it does

**Sparkommender** is a project that tackles hotel recommendation problem at scale.

1) Uses Spark to gain knowledge about the data and build best recommendation models
2) Builds a microservice exposing a REST interface to make recommendations so
any other web service, any computer or mobile application could easily integrate
and make use of the predictions


## How I built it
1) Spark Cluster for data analysis / model building / etc
2) Portable and sharable Spark notebooks
2) Play Web Service App with REST interface
4) Tested portability by running both in IBM Containers Service and Docker Cloud
5) Tested performance by running Gatling tests


## Challenges I ran into
Random Forest implementation only returns 1 prediction and I didn't find a way to get more

not fast enough -> Spark Job Server

## Accomplishments that I'm proud of

I think **Sparkommender** is quite a cool idea and I'm proud I could implement it very quickly blending all those various technologies together. It is a hack nonetheless, with many loose ends, but what's most important, it is working!

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
