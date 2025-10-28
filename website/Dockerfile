FROM node:18-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install
RUN apk add --no-cache curl

COPY . .

EXPOSE 3000
CMD ["node", "server.js"]