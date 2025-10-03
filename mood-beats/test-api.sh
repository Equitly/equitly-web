#!/bin/bash

# quick test script i wrote

echo "Testing MoodBeats API..."
echo "========================"

# probably should make this configurable but whatever
BASE_URL="http://localhost:8080/api/mood"

# test health first
echo ""
echo "Health check:"
curl -s "$BASE_URL/health"
echo ""
echo ""

# get demo info
echo "Demo endpoint:"
curl -s "$BASE_URL/demo" | jq
echo ""

# test some moods
echo "Testing happy mood:"
curl -s -X POST "$BASE_URL/analyze" -H "Content-Type: application/json" -d '{"moodDescription": "I am feeling energetic and happy after a great morning workout"}' | jq
echo ""

# this one should be interesting
echo "Testing sad mood:"
curl -X POST "$BASE_URL/analyze" -H "Content-Type: application/json" -d '{"moodDescription": "Feeling melancholic and nostalgic, like looking through old photos on a rainy day"}'
echo ""

# work stress test
echo "Work stress:"
curl -s -X POST "$BASE_URL/analyze" \
  -H "Content-Type: application/json" \
  -d '{"moodDescription": "Super stressed about work deadlines but trying to stay focused and productive"}'
echo ""
echo ""

# test with context - this was tricky to get working
echo "With context:"
curl -s -X POST "$BASE_URL/analyze" -H "Content-Type: application/json" -d '{"moodDescription": "Feeling relaxed and peaceful", "context": "studying"}' | jq '.'

# lets try one more random one
echo ""
echo "Random test:"
curl -s -X POST "$BASE_URL/analyze" -H "Content-Type: application/json" -d '{"moodDescription": "kinda tired but excited for the weekend"}'

echo ""
echo "Done testing. Should all work now."
