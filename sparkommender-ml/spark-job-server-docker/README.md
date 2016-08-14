# Sparkommender

To build it run:

    docker build -t radek1st/sparkommender .

To run it with included Spark in local mode:

    docker run -it -p 8090:8090 radek1st/sparkommender

To run it with Spark running on Kubernetes:

    docker run -it --add-host="spark-master:54.165.46.228" -e SPARK_MASTER=spark://spark-master:7077 -p 8090:8090 radek1st/sparkommender

    docker run -it --add-host="spark-master:ip-of-host-on-k8s" -e SPARK_MASTER=spark://spark-master:7077 -p 8090:8090 velvia/spark-jobserver:0.6.1.mesos-0.25.0.spark-1.5.2


docker run -it -e SPARK_MASTER=spark://ec2-52-90-10-64.compute-1.amazonaws.com:6066 -p 8090:8090 radek1st/sparkommender