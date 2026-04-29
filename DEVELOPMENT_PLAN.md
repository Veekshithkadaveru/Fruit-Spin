---
name: Fruit Spin v1.1
overview: High-speed reaction timing game where a wheel of 7 fruits spins continuously. Tap when the target fruit aligns with a bottom pointer. Wrong tap = lose a life, 3 lives lost = game over. Target changes every 5 correct taps. Pure Compose Canvas. Fully offline. API 26+ min.
todos:
  - id: project-setup
    content: Setup Compose + Navigation + Room; configure basic MVVM
    status: done
  - id: fruit-wheel-canvas
    content: Implement FruitWheel.kt Compose Canvas rendering and withFrameNanos rotation loop
    status: pending
    dependencies:
      - project-setup
  - id: tap-detection
    content: Implement TapValidator.kt pointer detection with tolerance window
    status: pending
    dependencies:
      - project-setup
  - id: game-viewmodel
    content: Implement GameViewModel state (tap handling, lives, score, streak multiplier)
    status: pending
    dependencies:
      - fruit-wheel-canvas
      - tap-detection
  - id: game-screen-layout
    content: Implement GameScreen layout with Target display, Multiplier UI, and dynamic backgrounds
    status: pending
    dependencies:
      - game-viewmodel
  - id: haptics-and-feedback
    content: Add Haptic feedback, Score floats, and Screen flashes for correct/wrong taps
    status: pending
    dependencies:
      - game-screen-layout
  - id: game-over-persistence
    content: Implement Game Over screen and Room best score persistence
    status: pending
    dependencies:
      - project-setup
  - id: polish-qa-delivery
    content: Real device QA edge cases (streak resets, speed changes, background crossfades) + client build
    status: pending
    dependencies:
      - haptics-and-feedback
      - game-over-persistence
---

# Fruit Spin — Phase 1 Implementation Plan

> **Overview**: High-speed reaction timing game. A wheel of 7 fruits spins continuously. A target fruit is shown above. The player taps when the target fruit aligns with a bottom pointer. Reaching 10 consecutive correct taps activates a x2 Multiplier. Atmospheric background visually escalates as the player's score increases. Pure Compose Canvas. Fully offline.
>
> **Delivery**: 2 Days (Vibe Coding Assisted)

> **Asset Note**: Agents must use assets from `@Seven+01N_elem/Seven+01N_elem` and `@Seven+01N/Seven+01N` into `app/src/main/res/drawable/`.
>
> Backgrounds: `back_1`, `back_2`, `back_3`
>
> Fruits: Grapes, Strawberry, Orange, Banana, Watermelon, Plum, Lucky 7

## ✅ Project Status & Todos

### 🏗 Phase A: Foundation (Day 1)
- [x] A1: Project Setup & Architecture <!-- id: project-setup -->
- [ ] A2: FruitWheel.kt Canvas Rendering <!-- id: fruit-wheel-canvas -->
- [ ] A3: withFrameNanos Rotation Loop <!-- id: fruit-wheel-canvas -->

### ⚙️ Phase B: Core Mechanics & Layout (Day 1.5)
- [ ] B1: TapValidator.kt Pointer Detection <!-- id: tap-detection -->
- [ ] B2: GameViewModel State & Logic <!-- id: game-viewmodel -->
- [ ] B3: Game Screen Layout & Dynamic Backgrounds <!-- id: game-screen-layout -->

### 🚀 Phase C: Polish, Persistence & Delivery (Day 2)
- [ ] C1: Target Display & Multiplier UI Animations <!-- id: game-screen-layout -->
- [ ] C2: Haptic Feedback & Screen Flashes <!-- id: haptics-and-feedback -->
- [ ] C3: Game Over Screen & Room Persistence <!-- id: game-over-persistence -->
- [ ] C4: QA & Real Device Testing <!-- id: polish-qa-delivery -->

---

## 🏗 System Architecture

### 1. High-Level Architecture (MVVM + Compose)
```
┌─────────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI Layer                  │
│   (GameScreen, GameOverScreen, FruitWheel, TapButton)      │
├─────────────────────────────────────────────────────────────┤
│                     ViewModel Layer                         │
│   (GameViewModel) via StateFlow                            │
├─────────────────────────────────────────────────────────────┤
│                      Domain/Data Layer                     │
│   (Room: BestScoreEntity + DAO)                            │
└─────────────────────────────────────────────────────────────┘
```

### 2. State Architecture (StateFlow + MVVM)
```kotlin
data class GameUiState(
    val rotationAngle: Float = 0f,
    val targetFruit: Fruit = Fruit.GRAPES,
    val lives: Int = 3,
    val score: Int = 0,
    val correctStreak: Int = 0,
    val scoreMultiplier: Int = 1,
    val backgroundImage: Int = R.drawable.back_1,
    val currentSpeedDps: Float = 60f,
    val isGameOver: Boolean = false
)
```

### 3. Project File Structure
```
app/krafted/fruitspin/
├── MainActivity.kt                          # Compose NavHost
├── ui/
│   ├── GameScreen.kt                        # main gameplay UI
│   ├── GameOverScreen.kt                    # end game UI
│   └── components/
│       ├── FruitWheel.kt                    # Canvas drawArc segments
│       └── MultiplierBanner.kt              # x2 Multiplier ACTIVE
├── utils/
│   └── TapValidator.kt                      # tolerance window logic
├── viewmodel/
│   └── GameViewModel.kt                     # game state loop and tap handling
└── data/db/
    ├── BestScoreEntity.kt                   # Room best score
    ├── BestScoreDao.kt
    └── AppDatabase.kt                       # Room database singleton
```

---

## 🚀 Detailed Implementation Roadmap
---
## Phase A: Foundation (Day 1)

### A1: Project Setup & Architecture <!-- id: project-setup -->
> **Goal**: Configure project with Compose, Navigation, Room. Set up MVVM skeleton.

**Duration**: 1.5 Hours

**Exit Criteria:**
- [x] App builds and runs.
- [x] Assets from `Seven+01N_elem/Seven+01N_elem` and `Seven+01N/Seven+01N` copied to `res/drawable/`.
- [x] NavHost can navigate between Game -> GameOver.

---

### A2: FruitWheel.kt Canvas Rendering <!-- id: fruit-wheel-canvas -->
> **Goal**: Render a segmented wheel with 7 fruits on a Compose Canvas.

**Duration**: 2 Hours

**Implementation Targets:**
- 7 equal segments (360° ÷ 7 ≈ 51.4° each).
- Draw fruits: Grapes, Strawberry, Orange, Banana, Watermelon, Plum, Lucky 7.

**Exit Criteria:**
- [ ] Wheel renders correctly on screen.
- [ ] Each segment has the correct fruit icon and color.

---

### A3: Game Loop & withFrameNanos <!-- id: fruit-wheel-canvas -->
> **Goal**: Implement continuous wheel rotation linked to score speed.

**Duration**: 1.5 Hours

**Implementation Targets:**
- infiniteTransition animatable or `withFrameNanos` loop in `LaunchedEffect`.
- Speed mapped to score (<150: 60f, <300: 90f, <500: 130f, >500: 180f).

**Exit Criteria:**
- [ ] Wheel spins smoothly.
- [ ] Speed escalates correctly based on score thresholds.

---
## Phase B: Core Mechanics & Layout (Day 1.5)

### B1: TapValidator.kt Pointer Detection <!-- id: tap-detection -->
> **Goal**: Check segment at 270° (6 o'clock) using a ±15° tolerance window.

**Duration**: 1.5 Hours

**Implementation Targets:**
- Calculate the current fruit at the bottom pointer based on `rotationAngle`.
- ±15° valid hit window.

**Exit Criteria:**
- [ ] Correctly identifies the targeted fruit on tap.
- [ ] Rejects hits outside the tolerance window.

---

### B2: GameViewModel State Logic <!-- id: game-viewmodel -->
> **Goal**: Handle taps, lives, score, and multiplier logic.

**Duration**: 2 Hours

**Implementation Targets:**
- Correct Tap: Points * Multiplier (Base points vary by fruit, x3 for Lucky 7). Streak +1. Multiplier = 2 if streak >= 10. Every 5 correct taps = new target.
- Wrong Tap: Lose 1 life, brief slow-down, lose multiplier (reset to 1). 3 lives lost = Game Over.

**Exit Criteria:**
- [ ] Score and multiplier calculate accurately.
- [ ] Lives decrement on miss.
- [ ] Target changes every 5 correct taps.

---

### B3: Game Screen Layout & Dynamic Backgrounds <!-- id: game-screen-layout -->
> **Goal**: Compose the main screen UI and handle background crossfades.

**Duration**: 2.5 Hours

**Implementation Targets:**
- Top bar with Score, Lives (💔).
- Target fruit display.
- Bottom TAP button.
- Dynamic background crossfade: `back_1` -> `back_2` -> `back_3` based on score.

**Exit Criteria:**
- [ ] UI matches PRD layout.
- [ ] Background crossfades smoothly over 1000ms when score threshold is crossed.

---
## Phase C: Polish, Persistence & Delivery (Day 2)

### C1: Target Display & Multiplier UI <!-- id: game-screen-layout -->
> **Goal**: Highlight the active target and streak multiplier.

**Duration**: 1.5 Hours

**Implementation Targets:**
- "Hit 3 more to change target" counter.
- "🔥 x2 MULTIPLIER ACTIVE! 🔥" banner appears at streak 10+.

**Exit Criteria:**
- [ ] Multiplier UI pulses into view correctly.

---

### C2: Haptic Feedback & Screen Flashes <!-- id: haptics-and-feedback -->
> **Goal**: Add physical and visual weight to taps.

**Duration**: 2 Hours

**Implementation Targets:**
- Correct Tap: Light haptic tick, Green flash on pointer, +points float animation.
- Wrong Tap: Heavy haptic buzz, Red flash + screen shake.
- Lucky 7 Correct: Gold screen flash, +50 JACKPOT! float.

**Exit Criteria:**
- [ ] Visual and haptic feedback trigger instantly on interaction.

---

### C3: Game Over Screen & Room Persistence <!-- id: game-over-persistence -->
> **Goal**: End game state and highest score persistence.

**Duration**: 2 Hours

**Implementation Targets:**
- Room Database for storing the best offline score.
- Game Over screen showing final score and "Play Again" button.

**Exit Criteria:**
- [ ] High score persists across sessions.

---

### C4: QA & Delivery <!-- id: polish-qa-delivery -->
> **Goal**: Final device testing.

**Duration**: 1 Hour

**Exit Criteria:**
- [ ] No crashes on tap spam.
- [ ] Client APK builds successfully.

---

## 📊 Timeline Summary
```
Day 1 (AM)  ████████████  Phase A: A1-A3 (Project Setup, Canvas, Spin Loop)
Day 1 (PM)  ████████████  Phase B: B1-B2 (Tap Validation, ViewModel Logic)
Day 2 (AM)  ████████████  Phase B: B3 & Phase C: C1-C2 (Layout, UI, Haptics)
Day 2 (PM)  ████████████  Phase C: C3-C4 (Game Over, Persistence, QA)
```

---

## 🎯 Success Metrics / Acceptance Criteria
- [ ] Wheel spins continuously and smoothly.
- [ ] Tap detection accurately resolves the fruit at the pointer within ±15°.
- [ ] Multiplier activates at 10 streaks and breaks on miss.
- [ ] Background crossfades logically as score hits 150, 300, 500 thresholds.
- [ ] Lives and Score calculated properly.
- [ ] Room persists high score offline.

---

## 🚫 Scope Boundaries
### In Scope (v1.1)
- 7 fruit segment continuous Canvas wheel.
- Score multipliers & streak system.
- Dynamic escalating backgrounds.
- Haptics and visual flashes.

### Out of Scope (v1.1)
- Online leaderboards.
- Power-ups other than Lucky 7 point multipliers.
- User accounts / login.

---

Document Version: 1.1
Last Updated: April 2026
Project: Fruit Spin — Kotlin + Jetpack Compose
