# Maze Runner Game
# TEAM 52
# Josh Adventure

## Overview
Josh Adventure is a 2D adventure game where players navigate through challenging mazes while avoiding monsters, collecting power-ups, and finding keys to progress through levels. The game features dynamic lighting, combat mechanics, and various collectible items.

## Game Features

### Character Controls
- **Movement**: Arrow keys for movement (UP, DOWN, LEFT, RIGHT)
- **Sprint**: Hold SHIFT while moving to run faster
- **Attack**: Press F to attack monsters
- **Camera Zoom**:
    - Plus (+) key to zoom in
    - Minus (-) key to zoom out
    - R key to reset zoom to default
- **Pause**: ESC key to pause the game

### Health System
- Player starts with 3 hearts
- Hearts can be collected to restore health
- Taking damage from monsters or traps reduces health
- Game over occurs when health reaches zero

### Items and Power-ups
1. **Heart**
    - Restores 1 health point
    - Maximum health is 3 hearts
    - Awards 200 points when collected

2. **Speed Boost**
    - Temporarily increases movement speed
    - Lasts for 8 seconds
    - Awards 200 points when collected

3. **Key**
    - Required to open exit doors
    - Only one key needed per level
    - Awards 500 points when collected

4. **Score up**
    - Provides score bonus
    - Awards 150 points when collected

### Scoring System
- Heart Collection: 200 points
- Speed-up Collection: 200 points
- Key Collection: 500 points
- Shield Collection: 150 points
- Time Bonus: 5 points per second survived

### Navigation
- A directional arrow points toward the exit door
- Fog of war mechanic limits visibility to area around player
- Real time updated HUD reflects current state

### Enemies
1. **Monsters**
    - Chase player when in range
    - Once is attacked, is immobilized for a short amount of time
    - Cause damage on contact
    - Have attack cooldown periods

2. **Ghosts**
    - Static enemies
    - Cause damage on contact
    - Cannot be defeated

3. **Traps**
    - Static hazards
    - Cause damage on contact

### Level Progression
- Game consists of 3 levels (1-1, 1-2, 1-3)
- Each level requires finding a key and reaching the exit door
- Progress is saved between levels
- Score carries over between levels

## Game Screens
1. **Main Menu**
    - Start Game
    - Story
    - Credits
    - Exit

2. **Pause Menu**
    - Continue
    - Stage Select
    - Exit

3. **Game Over Screen**
    - Try Again
    - Main Menu
    - Exit

4. **Victory Screen**
    - Shows final score
    - Option to continue or return to menu

## Tips for Success
1. Always keep an eye on your health
2. Use sprint strategically to avoid monsters
3. Collect power-ups whenever possible
4. Use the directional arrow to find the exit
5. Time your attacks carefully when fighting monsters
6. Don't forget to collect the key before reaching the exit door

## Technical Requirements
- Built using LibGDX game development framework
- Java Runtime Environment required
- Minimum resolution: 1280x720
