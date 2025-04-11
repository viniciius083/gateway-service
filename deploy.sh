#!/bin/bash

# Pega o nome do .jar mais recente dentro da pasta target/
APP_JAR=$(ls -t target/*.jar | head -n 1)
APP_NAME=$(basename "$APP_JAR")
LOG_PATH="logs/application.log"

echo "🔪 Parando instâncias antigas de $APP_NAME..."
pkill -f "$APP_NAME"

echo "🧱 Buildando o projeto..."
mvn clean install || { echo "❌ Build falhou!"; exit 1; }

echo "🚀 Iniciando aplicação com nohup..."
mkdir -p logs
nohup java -jar "$APP_JAR" > "$LOG_PATH" 2>&1 &

echo "✅ Aplicação [$APP_NAME] iniciada! Log: $LOG_PATH"
