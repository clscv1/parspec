#!/usr/bin/bash

# API Endpoint
URL="http://localhost:4567/order/accept"

# JSON Payload
DATA='{
    "userId": "123",
    "itemIds": ["1","2","3"],
    "totalAmount": 100
}'

# Number of requests
TOTAL_REQUESTS=1000
CONCURRENT_REQUESTS=50  # Adjust this based on your system's capacity

# Function to send a single request
send_request() {
    curl --silent --output /dev/null --location "$URL" \
         --header 'Content-Type: application/json' \
         --data "$DATA"
}

# Running requests in parallel
echo "Starting load test: $TOTAL_REQUESTS requests, $CONCURRENT_REQUESTS concurrent."

for ((i=1; i<=TOTAL_REQUESTS; i++)); do
    send_request &  # Run in background
    if (( i % CONCURRENT_REQUESTS == 0 )); then
        wait  # Wait for the batch to complete
    fi
done

wait  # Ensure all requests complete
echo "Load test completed."
