# 베이스 이미지 명
FROM node:18

WORKDIR /usr/src/app
COPY package*.json ./

RUN npm install
EXPOSE 8080

CMD [ "node", "server.js" ]
