FROM vertx/vertx3
WORKDIR /
ADD target/refund-1.0-fat.jar refund-1.0.jar
EXPOSE 8082
CMD java - jar refund-1.0.jar