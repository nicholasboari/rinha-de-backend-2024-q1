sudo docker compose down
mvn clean package -DskipTests
sudo docker buildx build -t java-puro:latest .
sudo docker compose up
