FROM openjdk:17
WORKDIR /app
COPY build/libs/sprout-market-0.0.1-SNAPSHOT.jar /app/sprout-market.jar
CMD ["java", "-jar", "sprout-market.jar"]