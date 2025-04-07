# Snake Game 🐍

A classic Snake game implementation in Java with modern features including multiple food types, score tracking, and timekeeping.

## Features ✨
- **Three Food Types:**
  - 🟥 Normal Food (+1 score, +1 length)
  - 🟨 Golden Food (+3 score, +1 length)
  - 🟪 Bad Food (-5 score, -2 length)
- **Dynamic Scoreboard:**
  - Current score tracking
  - Game timer (MM:SS format)
- **Game Mechanics:**
  - Collision detection (walls & self)
  - Responsive controls
  - Auto-restart on game over
- **Customization Options:**
  - Adjustable grid size
  - Configurable speeds
  - Custom color schemes

## Installation ⚙️
1. **Prerequisites:**
   - JDK 17 or later
   - Git (optional)

2. **Clone & Run:**
```bash
git clone https://github.com/dlxky/Snake.git
cd Snake
javac SnakeGame.java
java SnakeGame
