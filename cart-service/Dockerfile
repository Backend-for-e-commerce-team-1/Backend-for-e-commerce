FROM eclipse-temurin:25-jre
VOLUME /tmp
COPY build/libs/cart-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]