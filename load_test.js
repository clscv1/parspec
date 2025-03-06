const http = require('http');
const https = require('https');
const startTime = Date.now()
// API Configuration
const API_URL = 'http://localhost:4567/order/accept';
const TOTAL_REQUESTS = 10000; // Total number of requests
const CONCURRENT_REQUESTS = 1000; // Requests sent at a time

// Determine whether to use HTTP or HTTPS module
const url = new URL(API_URL);
const httpModule = url.protocol === 'https:' ? https : http;

// Sample JSON Payload
const requestData = JSON.stringify({
    userId: "123",
    itemIds: ["1", "2", "3"],
    totalAmount: 100
});

// Function to send a single request
function sendRequest(iteration) {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: url.hostname,
            port: url.port,
            path: url.pathname,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': Buffer.byteLength(requestData)
            }
        };

        const startTime = Date.now();
        const req = httpModule.request(options, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                const duration = Date.now() - startTime;
                console.log(`Request ${iteration}: Status ${res.statusCode}, Time ${duration} ms`);
                resolve();
            });
        });

        req.on('error', (error) => {
            console.error(`Request ${iteration} failed:`, error.message);
            reject(error);
        });

        req.write(requestData);
        req.end();
    });
}

// Function to run requests in batches
async function runLoadTest() {
    console.log(`Starting load test: ${TOTAL_REQUESTS} requests, ${CONCURRENT_REQUESTS} concurrent...`);
    
    let completedRequests = 0;
    while (completedRequests < TOTAL_REQUESTS) {
        const batchSize = Math.min(CONCURRENT_REQUESTS, TOTAL_REQUESTS - completedRequests);
        const batch = [];

        for (let i = 0; i < batchSize; i++) {
            batch.push(sendRequest(completedRequests + i + 1));
        }

        await Promise.all(batch);  // Wait for batch to complete
        completedRequests += batchSize;
    }

    console.log("Load test completed.");
}

// Run the load test
runLoadTest();
