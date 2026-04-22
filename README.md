# ASL Recognition Framework

An interactive learning platform for American Sign Language (ASL) hand gesture recognition. Built with Java, Google MediaPipe, OpenCV, and JavaFX.

## Project Overview

This framework enables real-time ASL letter recognition (A-Z) through an interactive learning application. Users see a target ASL gesture, perform it in front of their webcam, and receive instant accuracy feedback. Designed as an educational tool to help learners master the ASL alphabet through gamified practice sessions.

**Key Features:**
- Real-time hand gesture recognition via MediaPipe
- Practice mode with streak-based progression (A-Z, alphabetical order)
- Wrist-relative landmark normalization for position-invariant recognition
- 7 rotation variants per letter for robust rotation-invariant recognition
- Cosine similarity scoring for accurate gesture matching
- Interactive UI with live camera feed and instruction videos
- 71 comprehensive unit tests with edge case coverage

## Tech Stack

- **Language**: Java 17+
- **Build System**: Maven
- **Computer Vision**: Google MediaPipe (hand pose detection) + OpenCV
- **UI Framework**: JavaFX
- **Testing**: JUnit 5

## Architecture

### Layer 1: Recognition Layer (`recognition/`)
- `GestureRecognizer` - Interface for gesture matching
- `MediaPipeRecognizer` - Cosine similarity-based recognition
- `LandmarkBridge` - Python MediaPipe bridge subprocess
- `GestureLibrary` - Reference gesture loading and variant generation
- `LandmarkUtils` - Normalization and rotation utilities

### Layer 2: Game Logic (`game/`)
- `GameSession` - Unified session interface
- `PracticeSession` - Single-player practice mode (A-Z progression)
- `LetterProgression` - Letter sequence management
- `ScoringStrategy` - Pass/fail and scoring logic
- `GameEventListener` - Event-driven output decoupling

### Layer 3: UI Layer (`ui/`)
- `GameUI` - Main JavaFX window with game state display
- `CameraService` - Live camera frame capture and landmark rendering
- `LoadInstruction` - Video instruction player (per-letter guidance)
- `GestureGate` - Confidence threshold filtering (0.95 minimum)
- `ConfigLoader` - Configuration loading from config.properties

### Layer 4: Persistence Layer (`persistence/`)
- `UserProgress` - Data model for user progress
- `AttemptRecord` - Individual attempt record with accuracy and timestamp
- **Future Work**: `UserProgressDAO` (SQLite implementation deferred)

### Layer 5: Core Layer (`core/`)
- `UserProgress` - In-session wrapper around persistence model
- Provides convenience methods for game logic

### Model Layer (`model/`)
- `GestureDefinition` - Abstract base for gesture definitions
- `StaticGestureDefinition` - Single hand pose (A-I, K-Z)
- `DynamicGestureDefinition` - Sequence of poses (J, Z - future work)
- `HandLandmark` - 3D coordinate of a hand joint (21 landmarks per hand)
- `RecognitionResult` - Recognition output with confidence and match status
- `GestureType` - Enum for STATIC vs DYNAMIC gestures

## Building & Running

### Prerequisites
- Java 17 or later
- Maven 3.6+
- Webcam (USB camera or IP camera via config)
- Python 3.8+ with MediaPipe installed (`pip install mediapipe`)

### Build
```bash
cd asl-recognition-framework
mvn clean compile
```

Expected output: `BUILD SUCCESS`

### Run
```bash
mvn javafx:run
```

Or with exec plugin:
```bash
mvn exec:java -Dexec.mainClass="aslframework.Main"
```

### Run Tests
```bash
mvn clean test
```

Expected output: `BUILD SUCCESS` with **38 tests passed** ✓

### Generate Javadoc
```bash
mvn javadoc:javadoc
open target/site/javadoc/index.html
```

## Configuration

Create `config.properties` in the project root (optional):

```properties
# Custom Python executable path (default: python3 from PATH)
python.path=/usr/bin/python3

# IP camera URL (default: use built-in webcam at index 0)
camera.url=rtsp://192.168.1.100/stream
```

## Game Modes

### Practice Mode (Implemented)
- Single-player progression through ASL letters A-Z in alphabetical order
- Each letter requires 3 consecutive successful recognitions to advance
- Streak counter tracks consecutive successes
- Perfect bonus awarded for clearing letters with minimal failures
- Running score tracks total points accumulated
- Detailed logging of all attempts (letter, accuracy, pass/fail)

### Battle Mode (Future Work)
- Multi-player elimination game with difficulty-ordered letters
- Players compete simultaneously with increasing difficulty
- Dynamic elimination as players fail

## Usage

### Start Application
```bash
mvn javafx:run
```

### Practice Mode Workflow
1. Application launches with live camera feed
2. Target letter displays in instruction panel (e.g., "Practice: A")
3. Instruction video auto-plays (a.mp4, b.mp4, ... z.mp4 from assets/guidance/)
4. Sign the letter to the camera
5. Real-time gesture recognition provides confidence feedback
6. Hold gesture confidently (≥0.95 confidence) for 1 second to attempt
7. Recognition compares against 7 rotated variants for robustness
8. Success (≥0.75 accuracy) increments streak; failure resets to 0
9. 3 consecutive successes clears the letter and advances to next
10. Perfect bonus awarded: max(0, 3 - failCount) × 10 × tier
11. Final score displayed when all 26 letters cleared

### Scoring
- **Attempt Score**: floor(accuracy × 100) × letterTier
- **Perfect Bonus**: max(0, 3 - failCount) × 10 × letterTier
- **Letter Total**: sum of passing attempt scores + perfect bonus
- **Session Total**: cumulative across all 26 letters

### Hold Timer
- Gesture must be held confidently (≥0.95 gesture gate) for 1000ms to register
- Hold state is tracked per letter to prevent accidental duplicate submissions
- Auto-clears stale landmark visualization after 200ms of no detection

## Code Quality & Testing

**Test Coverage:**
- 38 unit tests across 9 test classes
- **Scoring**: Base score, pass threshold, bonus calculation
- **Recognition**: Cosine similarity, variant selection, edge cases
- **Models**: Immutability, defensive copying, boundary values
- **Landmarks**: Normalization, rotation, negative coordinates
- **UI**: Configuration loading, gesture gating, camera service

**Key Design Patterns:**
- **Strategy Pattern**: `ScoringStrategy` (StandardScoringStrategy, PerfectBonusScoringStrategy)
- **Factory Pattern**: `GameSessionFactory` (session creation)
- **Observer Pattern**: `GameEventListener` (event-driven output)
- **Template Method**: `AbstractGameSession`, `AbstractLetterProgression`
- **Composition**: Sessions compose recognizers, libraries, strategies, progressions

**OOD Principles:**
- ✓ **Encapsulation**: All fields private; defensive copying for collections
- ✓ **Abstraction**: Interfaces for GestureRecognizer, GameSession, LetterProgression, ScoringStrategy
- ✓ **Inheritance**: Gesture hierarchy (Static/DynamicGestureDefinition); AbstractGameSession base
- ✓ **Polymorphism**: Multiple strategy implementations; interface-based contracts
- ✓ **Separation of Concerns**: 5-layer architecture with clear boundaries and responsibilities

## Debugging

### Enable Debug Output
```bash
DEBUG=true mvn javafx:run
```

### Common Issues

**"Could not open camera"**
- Ensure webcam is not in use by another application
- Check camera permissions on macOS/Linux
- Try setting `camera.url` in config.properties for IP camera

**"Landmark extractor exited unexpectedly"**
- Verify Python 3 is installed: `python3 --version`
- Verify MediaPipe is installed: `pip install mediapipe`
- Check Python path in config.properties if using custom installation

**"Reference data directory not found"**
- Run from project root where `scripts/reference_data/` exists
- Verify JSON files exist for A-Z letters

**Recognition not working**
- Check lighting conditions (MediaPipe needs good visibility)
- Ensure hand is fully visible in camera frame
- Verify gesture confidence is ≥0.95 (gate threshold)

## Project Structure
```
asl-recognition-framework/
├── src/main/java/aslframework/
│   ├── Main.java                 (Entry point)
│   ├── ConfigLoader.java         (Configuration loading)
│   ├── core/
│   │   └── UserProgress.java     (Session-level progress wrapper)
│   ├── game/
│   │   ├── GameConfig.java       (Constants: thresholds, bonuses)
│   │   ├── GameMode.java         (PRACTICE, BATTLE enum)
│   │   ├── LetterDifficulty.java (Tier assignment for A-Z)
│   │   ├── PlayerState.java      (Generic player state)
│   │   ├── progression/
│   │   │   ├── LetterProgression.java
│   │   │   ├── AbstractLetterProgression.java
│   │   │   ├── SequentialProgression.java (A-Z order)
│   │   │   └── DifficultyProgression.java  (Future: tier order)
│   │   ├── result/
│   │   │   ├── GameResult.java   (Interface)
│   │   │   └── PracticeResult.java
│   │   ├── scoring/
│   │   │   ├── ScoringStrategy.java
│   │   │   ├── StandardScoringStrategy.java
│   │   │   └── PerfectBonusScoringStrategy.java
│   │   └── session/
│   │       ├── GameSession.java  (Interface)
│   │       ├── AbstractGameSession.java
│   │       ├── PracticeSession.java (Implemented)
│   │       ├── GameEventListener.java
│   │       ├── NoOpGameEventListener.java
│   │       └── GameSessionFactory.java
│   ├── model/
│   │   ├── GestureDefinition.java
│   │   ├── StaticGestureDefinition.java
│   │   ├── DynamicGestureDefinition.java
│   │   ├── GestureType.java
│   │   ├── HandLandmark.java
│   │   └── RecognitionResult.java
│   ├── persistence/
│   │   ├── UserProgress.java     (Data model)
│   │   └── AttemptRecord.java
│   ├── recognition/
│   │   ├── GestureRecognizer.java
│   │   ├── MediaPipeRecognizer.java
│   │   ├── LandmarkBridge.java   (Python subprocess bridge)
│   │   ├── GestureLibrary.java
│   │   ├── LandmarkUtils.java    (Normalization + rotation)
│   │   └── MockGestureRecognizer.java
│   └── ui/
│       ├── GameUI.java           (Main JavaFX window)
│       ├── CameraService.java    (Camera + landmark rendering)
│       ├── LoadInstruction.java  (Video player)
│       ├── GestureGate.java      (Confidence filtering)
│       └── ConfigLoader.java
├── src/test/java/aslframework/
│   ├── game/scoring/
│   │   ├── StandardScoringStrategyTest.java
│   │   └── PerfectBonusScoringStrategyTest2.java
│   ├── model/
│   │   ├── GestureDefinitionTest.java
│   │   ├── HandLandMarkTest.java
│   │   └── RecognitionResultTest.java
│   ├── recognition/
│   │   ├── MediaPipeRecognizerTest.java
│   │   └── MockGestureRecognizerTest.java
│   └── ui/
│       ├── CameraServiceTest.java
│       ├── ConfigLoaderTest.java
│       └── GestureGateTest.java
├── scripts/
│   ├── reference_data/        (JSON files: A.json - Z.json)
│   ├── landmark_extractor.py
│   ├── collect_reference_data.py
│   ├── migrate_reference_data.py
│   ├── add_reference_variant.py
│   └── test_rotations.py
├── assets/
│   ├── guidance/              (MP4 videos: a.mp4 - z.mp4)
│   └── animation/
├── lib/
│   ├── opencv-4130.jar        (Added via .gitignore - local system)
│   └── libopencv_java4130.dylib
├── pom.xml
├── README.md
└── config.properties.template
```

## Future Work

The following features are deferred for future releases:

1. **Battle Mode** - Multi-player elimination game with difficulty-ordered letters
2. **Persistence DAO** - SQLite database integration for user progress tracking
3. **Dynamic Gestures** - Motion-based recognition for J and Z letters
4. **OpenCV Integration** - Replace Python subprocess with direct OpenCV Java binding
5. **Networking** - Remote multiplayer battle support

## Team Responsibilities

This project was developed as a CS 5004 final project with the following ownership:

**Tavish** - Gesture Recognition Layer
- MediaPipe integration and hand landmark extraction
- Cosine similarity recognition algorithm
- Landmark normalization and rotation augmentation (7 variants)
- Recognition pipeline testing

**Zachary (Zihao Li)** - Persistence & UI Layer
- JavaFX game interface (GameUI)
- Camera feed integration (CameraService)
- Instruction video player (LoadInstruction)
- Session state management

**Chester (Chengchi Jiang)** - Game Logic & Scoring
- Practice session implementation (PracticeSession)
- Letter progression system (SequentialProgression)
- Scoring strategies and bonus calculation (PerfectBonusScoringStrategy)
- Game event listener pattern

## References & Resources

- [Google MediaPipe Hand Landmarker](https://ai.google.dev/edge/mediapipe/solutions/vision/hand_landmarker)
- [OpenCV 4.13.0 Java Documentation](https://docs.opencv.org/4.5.2/javadoc/)
- [JavaFX 21 Documentation](https://openjfx.io/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

## Building JAR File

To create an executable JAR with all dependencies:

```bash
mvn clean package
java -Djava.library.path=./lib -jar target/asl-recognition-framework.jar
```

## Notes

- This is an educational implementation for CS 5004, not production software
- Recognition accuracy depends on lighting, hand size, and camera quality
- Currently optimized for US English ASL letters A-Z
- J and Z require motion capture (stubbed as DynamicGestureDefinition for future implementation)
- Tested on macOS with built-in webcam and external USB cameras

## Submissions

**Presentation Date**: April 21, 2026 (completed)  
**Final Code Commit**: April 21, 2026 (12:00 PM PT deadline)  
**Status**: Ready for grading

---

**Last Updated**: April 21, 2026  
**Project Status**: Final Submission - All 71 Tests Passing ✓
