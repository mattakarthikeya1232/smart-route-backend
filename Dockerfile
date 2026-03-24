FROM eclipse-temurin:17-jdk

WORKDIR /app

# copy only backend files properly
COPY . .

# build jar
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# debug (optional but helpful)
RUN ls target

EXPOSE 8080

# run using shell so wildcard works
CMD sh -c "java -jar target/*.jar"