const express = require('express');
const redis = require('redis');

const client = redis.createClient({
    host: 'redis-server', // This is the name of the service in the docker-compose.yml file
    port: 6379
});


const PORT = 8080;

const app = express();
app.listen(PORT);
console.log(`Server running on port ${PORT}`);

// Set a default value for visits
client.set('visits', 0);

app.get('/', (req, res) => {
    // Increment the visits value in redis
    client.get('visits', (err, visits) => {
        client.set('visits', parseInt(visits) + 1);
        res.send(`Number of visits is ${visits}`);
    });
});
