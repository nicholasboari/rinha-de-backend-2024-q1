sudo docker compose down
mvn clean package -DskipTests
sudo docker buildx build --platform linux/amd64 -t api-spring:latest .
sudo docker compose up
