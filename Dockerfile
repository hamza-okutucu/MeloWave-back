# Utiliser une image de base avec Maven et Java
FROM maven:3.8.4-openjdk-8-slim AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier POM
COPY pom.xml .

# Télécharger les dépendances Maven
RUN mvn dependency:go-offline

# Copier les fichiers de l'application
COPY src ./src

# Compiler l'application
RUN mvn package

# Utiliser une image légère avec JRE
FROM openjdk:8-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le certificat root.crt
COPY root.crt .

# Copier le fichier JAR généré à partir de la phase de construction précédente
COPY --from=build /app/target/MeloWave-0.0.1-SNAPSHOT.jar .

# Exposer le port sur lequel l'application s'exécute
EXPOSE 8080

# Commande pour démarrer l'application
CMD ["java", "-jar", "MeloWave-0.0.1-SNAPSHOT.jar"]