#!/bin/bash

# Script de despliegue automático para FNAFHS Academy
# Uso: ./scripts/deploy.sh

echo "🚀 Iniciando despliegue de FNAFHS Academy..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar que estamos en la raíz del proyecto
if [ ! -d "backend" ] || [ ! -d "frontend" ]; then
    echo -e "${RED}❌ Error: Ejecuta este script desde la raíz del proyecto FNAFHS-ACADEMY${NC}"
    exit 1
fi

# 1. Verificar Java
echo -e "${YELLOW}📦 Verificando Java...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java no está instalado. Instala Java 21 o superior.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Java encontrado: $(java -version 2>&1 | head -n 1)${NC}"

# 2. Verificar Maven
echo -e "${YELLOW}📦 Verificando Maven...${NC}"
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}⚠️ Maven no encontrado, usando Maven Wrapper...${NC}"
    MAVEN_CMD="./backend/mvnw"
else
    MAVEN_CMD="mvn"
fi
echo -e "${GREEN}✅ Listo${NC}"

# 3. Compilar backend
echo -e "${YELLOW}🔨 Compilando backend...${NC}"
cd backend
$MAVEN_CMD clean package -DskipTests
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Error en la compilación del backend${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Backend compilado exitosamente${NC}"
cd ..

# 4. Verificar frontend
echo -e "${YELLOW}🌐 Verificando frontend...${NC}"
if [ ! -f "frontend/css/estilos.css" ]; then
    echo -e "${RED}❌ No se encuentra frontend/css/estilos.css${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Frontend listo${NC}"

# 5. Preguntar si quiere iniciar el servidor
echo ""
echo -e "${YELLOW}¿Deseas iniciar el servidor ahora? (s/n)${NC}"
read -r respuesta

if [ "$respuesta" = "s" ] || [ "$respuesta" = "S" ]; then
    echo -e "${GREEN}🚀 Iniciando servidor Spring Boot...${NC}"
    echo -e "${YELLOW}📝 El servidor estará disponible en: http://localhost:8080${NC}"
    echo -e "${YELLOW}⚠️ Presiona Ctrl+C para detener el servidor${NC}"
    echo ""
    cd backend
    $MAVEN_CMD spring-boot:run
else
    echo -e "${GREEN}✅ Despliegue completado. Ejecuta 'cd backend && ./mvnw spring-boot:run' para iniciar.${NC}"
fi