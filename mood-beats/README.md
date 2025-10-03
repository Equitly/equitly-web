# 🎵 MoodBeats - AI-Powered Mood to Music Translator

**"Translate your feelings into the perfect soundtrack"**

MoodBeats is an innovative Spring Boot application that uses AI to analyze your mood descriptions and translate them into personalized music recommendations. Simply describe how you're feeling, and our AI will analyze your emotions and recommend music genres and characteristics that match your current state of mind.

## ✨ Features

- **🤖 AI-Powered Mood Analysis**: Uses Spring AI with OpenAI to analyze complex emotional descriptions
- **🎭 Emotional Intelligence**: Extracts primary emotions, energy levels, arousal, and valence from text
- **🎼 Genre Recommendations**: Maps emotional states to appropriate music genres
- **📊 Mood Visualization**: Beautiful dashboard showing your emotional analysis
- **🎨 Intuitive Web Interface**: Clean, modern UI for easy interaction
- **🔍 Natural Language Processing**: Handles complex, nuanced mood descriptions

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2, Spring AI, Spring Data JPA
- **Database**: PostgreSQL with JSONB support
- **AI**: OpenAI GPT-3.5-turbo for mood analysis
- **Frontend**: Vanilla HTML, CSS, JavaScript
- **Build Tool**: Maven

## 🚀 Quick Start

### Prerequisites

1. **Java 21** or higher
2. **PostgreSQL** database running locally
3. **OpenAI API Key** (for AI mood analysis)

### Setup Instructions

1. **Clone and navigate to the project**:
   ```bash
   cd mood-beats
   ```

2. **Set up PostgreSQL database**:
   ```sql
   CREATE DATABASE moodbeats;
   CREATE USER moodbeats_user WITH PASSWORD 'password123';
   GRANT ALL PRIVILEGES ON DATABASE moodbeats TO moodbeats_user;
   ```

3. **Configure environment variables**:
   ```bash
   export OPENAI_API_KEY="your-openai-api-key-here"
   export DB_USERNAME="moodbeats_user"
   export DB_PASSWORD="password123"
   ```

4. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**:
   - Web Interface: http://localhost:8080
   - API Health Check: http://localhost:8080/api/mood/health
   - Demo Endpoint: http://localhost:8080/api/mood/demo

## 🎯 Usage Examples

### Web Interface

1. Open http://localhost:8080 in your browser
2. Type your mood description in the text area
3. Optionally select a context (working, exercising, etc.)
4. Click "✨ Analyze My Mood"
5. View your personalized analysis and recommendations!

### Example Mood Descriptions

Try these example mood descriptions to see the AI in action:

- *"I'm feeling nostalgic about my college days but also excited about my future career possibilities"*
- *"Stressed about my upcoming presentation but trying to stay positive and confident"*
- *"It's raining outside and I feel contemplative, cozy, and slightly melancholy"*
- *"Just finished an amazing workout and I feel energized, accomplished, and ready to take on the world"*

### API Endpoints

**Analyze Mood** (POST `/api/mood/analyze`):
```json
{
  "moodDescription": "I'm feeling nostalgic and hopeful at the same time",
  "context": "relaxing",
  "includeExplicitContent": false,
  "maxRecommendations": 10
}
```

**Response**:
```json
{
  "id": null,
  "moodDescription": "I'm feeling nostalgic and hopeful at the same time",
  "primaryEmotions": ["nostalgia", "hope"],
  "energyLevel": 6,
  "arousalLevel": 4,
  "valence": 7,
  "moodSummary": "Energy: 6, Arousal: 4, Valence: 7",
  "recommendedGenres": ["indie-folk", "alternative"],
  "recommendations": [],
  "createdAt": null,
  "analysisInsight": "This mood combines reflective nostalgia with forward-looking optimism..."
}
```

## 🏗️ Architecture

### Core Components

1. **MoodAnalysisService**: Handles AI-powered mood analysis using Spring AI
2. **Entity Models**: JPA entities for Users, MoodAnalyses, Songs, Genres, etc.
3. **REST Controllers**: API endpoints for mood analysis and recommendations
4. **Data Layer**: PostgreSQL with repositories for data persistence

### Database Schema

The application uses a sophisticated database schema with tables for:
- `users` - User accounts
- `mood_analyses` - AI analysis results with JSONB fields
- `genres` - Music genres with mood compatibility ranges
- `songs` - Music tracks with emotional characteristics
- `playlists` - Generated playlists based on mood
- `recommendations` - Recommendation history and feedback

## 🔮 Future Enhancements

This is a foundation that can be extended with:

- **🎵 Spotify Integration**: Fetch real songs and create actual playlists
- **🔐 User Authentication**: Full user management and login system
- **📱 Mobile App**: React Native or Flutter mobile application
- **🤝 Social Features**: Share mood analyses and playlists
- **📈 Analytics Dashboard**: Track mood patterns over time
- **🎨 Advanced UI**: More sophisticated frontend with React/Vue
- **🌟 Machine Learning**: Custom ML models trained on user feedback

## 🧪 Development

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package -DskipTests
java -jar target/mood-beats-0.0.1-SNAPSHOT.jar
```

### Configuration
Key configuration properties in `application.yml`:
- Database connection settings
- OpenAI API configuration
- Logging levels
- Custom mood analysis prompts

## 🤝 Contributing

This project is a demo showcasing AI integration with Spring Boot. Feel free to:
- Add new mood analysis features
- Improve the AI prompting
- Enhance the UI/UX
- Add music service integrations
- Optimize the recommendation algorithms

## 📝 License

This project is for educational and demonstration purposes.

---

**Built with ❤️ using Spring Boot, Spring AI, and PostgreSQL**

*Experience the future of emotion-driven music discovery!* 🎵✨
