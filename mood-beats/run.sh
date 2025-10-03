#!/bin/bash

echo "🎵 Starting MoodBeats Application..."

# Check if PostgreSQL is running
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo "⚠️  PostgreSQL is not running. Please start PostgreSQL first:"
    echo "   brew services start postgresql"
    echo "   or"
    echo "   pg_ctl -D /usr/local/var/postgres start"
    exit 1
fi

# Check if database exists
if ! psql -h localhost -U postgres -lqt | cut -d \| -f 1 | grep -qw moodbeats; then
    echo "📊 Creating moodbeats database..."
    createdb -h localhost -U postgres moodbeats
    echo "   Database created!"
fi

# Check for OpenAI API key
if [ -z "$OPENAI_API_KEY" ]; then
    echo "⚠️  OPENAI_API_KEY environment variable is not set."
    echo "   Please set your OpenAI API key:"
    echo "   export OPENAI_API_KEY='your-api-key-here'"
    echo ""
    echo "   You can get an API key from: https://platform.openai.com/api-keys"
    echo ""
    echo "   For now, the app will start but AI analysis won't work without a valid key."
fi

# Set default environment variables if not set
export DB_USERNAME=${DB_USERNAME:-moodbeats_user}
export DB_PASSWORD=${DB_PASSWORD:-password123}

echo "🚀 Starting Spring Boot application..."
echo "   Database: PostgreSQL (moodbeats)"
echo "   OpenAI API Key: ${OPENAI_API_KEY:+Set ✓}${OPENAI_API_KEY:-Not Set ⚠️}"
echo ""

./mvnw spring-boot:run

echo ""
echo "🎉 MoodBeats is running!"
echo "   Web Interface: http://localhost:8080"
echo "   API Health: http://localhost:8080/api/mood/health"
echo ""
