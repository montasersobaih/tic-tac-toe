# Project Structure Recommendations

## Summary

The project currently follows a basic JavaFX/Maven layout, but the application code mixes UI controllers, game rules,
JavaFX tasks, resource constants, utility classes, and experimental/demo classes inside the same root package:

```text
com.mj.tic.tac.toe.javafx.java
```

The recommended direction is to separate the project by responsibility:

- `app` for JavaFX startup and application bootstrap.
- `ui` for FXML controllers, dialogs, and JavaFX-specific view code.
- `domain` for pure game concepts such as board coordinates, marks, winner data, difficulty, and game state.
- `service` or `engine` for game logic such as winner detection and computer move selection.
- `task` or `application.task` for JavaFX background task wrappers.
- `infrastructure` for resource loading, FXML loading, localization, and technical helpers.

This keeps JavaFX-specific code away from core game rules and makes the project easier to test, maintain, and extend.

## Current Structure

Current main package layout:

```text
src/main/java/com/mj/tic/tac/toe/javafx/java
├── StartupApplication.java
├── GameUI.java
├── TicTacToe.java
├── TestClass.java
├── constant
├── controller
│   ├── BaseController.java
│   ├── ControllersMediator.java
│   ├── layout
│   └── view
├── dialog
├── task
└── util
```

Current resource layout:

```text
src/main/resources
├── assist/css
│   ├── control
│   └── interface
├── interface
│   ├── dialog
│   └── layout
├── controls.properties
├── messages.properties
└── style.css
```

## Main Issues

1. The root package name is too long and contains repeated implementation detail:

```text
com.mj.tic.tac.toe.javafx.java
```

A cleaner root would be:

```text
com.mj.tictactoe
```

or, if you want to keep JavaFX explicit:

```text
com.mj.tictactoe.javafx
```

2. Game model classes are placed under `util`.

Classes such as `Coordinates`, `Difficulty`, `Mark`, and `Winner` are not generic utilities. They represent the game
domain and should move to a domain/model package.

3. JavaFX tasks contain both UI work and game logic.

For example, winner checking and computer move selection can be pure game logic. JavaFX `Task` classes should mostly
wrap background execution, not own the rules themselves.

4. Experimental/demo classes are mixed with production application code.

Classes like `GameUI`, `TicTacToe`, and `TestClass` look like prototypes or experiments. They should not live beside the
production JavaFX application entry point.

5. Resource names are inconsistent.

The resource directory `interface` works, but `view` or `fxml` is more common and clearer in JavaFX projects. The
`assist` directory is also vague; `css`, `images`, and `fonts` are clearer.

6. Controller coordination is still evolving.

`ControllersMediator` is a good direction, but it should expose behavior-oriented methods rather than low-level
infrastructure such as `getExecutor()`.

## Recommended Java Package Structure

Recommended target structure:

```text
src/main/java/com/mj/tictactoe
├── app
│   └── StartupApplication.java
├── domain
│   ├── Board.java
│   ├── Cell.java
│   ├── Coordinates.java
│   ├── Difficulty.java
│   ├── GameResult.java
│   ├── GameState.java
│   ├── Mark.java
│   ├── Player.java
│   └── Winner.java
├── engine
│   ├── ComputerMoveEngine.java
│   ├── GameEngine.java
│   ├── MinimaxStrategy.java
│   ├── MoveStrategy.java
│   ├── RandomMoveStrategy.java
│   └── WinnerDetector.java
├── ui
│   ├── controller
│   │   ├── BaseController.java
│   │   ├── ControllerMediator.java
│   │   ├── ApplicationBarController.java
│   │   ├── LeftPanelController.java
│   │   ├── PlayAreaPanelController.java
│   │   └── ViewController.java
│   ├── dialog
│   │   ├── BaseDialog.java
│   │   ├── ConfirmDialog.java
│   │   └── DifficultyDialog.java
│   └── task
│       ├── CheckWinnerTask.java
│       ├── ComputerMoveTask.java
│       ├── MarkCellTask.java
│       ├── NewGameTask.java
│       └── ResetPlayAreaTask.java
└── infrastructure
    ├── fxml
    │   ├── FxmlLoaderFactory.java
    │   └── FxmlView.java
    ├── i18n
    │   └── ResourceBundleService.java
    └── resource
        ├── CssResource.java
        └── FxmlResource.java
```

## Recommended Resource Structure

Recommended target resource layout:

```text
src/main/resources
├── com/mj/tictactoe
│   ├── fxml
│   │   ├── view.fxml
│   │   ├── dialog
│   │   │   ├── confirm-dialog.fxml
│   │   │   └── difficulty-dialog.fxml
│   │   └── layout
│   │       ├── application-bar.fxml
│   │       ├── left-panel.fxml
│   │       └── play-area-panel.fxml
│   ├── css
│   │   ├── base
│   │   │   ├── colors.css
│   │   │   └── typography.css
│   │   ├── controls
│   │   │   ├── button.css
│   │   │   ├── dialog.css
│   │   │   ├── label.css
│   │   │   ├── pane.css
│   │   │   ├── scroll-bar.css
│   │   │   └── text-field.css
│   │   └── views
│   │       ├── view.css
│   │       ├── application-bar.css
│   │       ├── left-panel.css
│   │       └── play-area-panel.css
│   └── i18n
│       ├── controls.properties
│       └── messages.properties
```

Why this is better:

- Resource paths are grouped under the application namespace.
- FXML, CSS, and i18n files are separated by asset type.
- Kebab-case file names such as `left-panel.fxml` match common resource naming conventions.
- The resource layout mirrors the Java package structure enough to stay discoverable.

## Suggested Class Moves

| Current class                                     | Recommended location                  | Reason                                      |
|---------------------------------------------------|---------------------------------------|---------------------------------------------|
| `StartupApplication`                              | `app`                                 | It is the JavaFX application bootstrap.     |
| `GameUI`                                          | `prototype` or remove                 | It appears to be a standalone prototype UI. |
| `TicTacToe`                                       | `prototype` or remove                 | It appears to be a console prototype.       |
| `TestClass`                                       | `dev` package, test source, or remove | It contains experimental utility classes.   |
| `Coordinates`                                     | `domain`                              | It is a game value object, not a utility.   |
| `Difficulty`                                      | `domain`                              | It is part of the game model.               |
| `GameState`                                       | `domain`                              | It describes game state.                    |
| `Mark`                                            | `domain`                              | It represents a board move.                 |
| `Winner`                                          | `domain`                              | It represents a game result.                |
| `FXMLUtil`                                        | `infrastructure.fxml`                 | It loads FXML resources.                    |
| `ResourceBundleUtil`                              | `infrastructure.i18n`                 | It handles localization lookup.             |
| `DInterface`, `UInterface`                        | `infrastructure.resource`             | They represent FXML resource references.    |
| `StyleSheet`                                      | `infrastructure.resource`             | It represents CSS resource references.      |
| `BaseController`                                  | `ui.controller`                       | It is UI-controller infrastructure.         |
| `ControllersMediator`                             | `ui.controller`                       | It coordinates UI controllers.              |
| `BaseDialog`, `ConfirmDialog`, `DifficultyDialog` | `ui.dialog`                           | They are JavaFX dialog components.          |
| JavaFX task classes                               | `ui.task` or `application.task`       | They are JavaFX-specific async wrappers.    |

## Recommended Responsibility Split

### Domain Layer

The domain layer should not import JavaFX classes.

Good domain classes:

```text
Board
Coordinates
Difficulty
GameState
Mark
Player
Winner
GameResult
```

This layer should be easy to unit test without starting JavaFX.

### Engine Layer

The engine layer should contain game rules:

```text
WinnerDetector
ComputerMoveEngine
MoveStrategy
RandomMoveStrategy
MinimaxStrategy
GameEngine
```

The current logic inside `ComputerMoveTask` and `CheckWinnerTask` should eventually move here. The task classes can then
call the engine and return the result.

### UI Layer

The UI layer should contain JavaFX-specific code:

```text
controllers
dialogs
FXML event handlers
JavaFX properties
JavaFX nodes
```

Controllers should coordinate UI behavior, not own all game rules.

### Task Layer

JavaFX task classes should be thin wrappers around work that may take time:

```java
public final class ComputerMoveTask extends Task<Coordinates> {

    private final ComputerMoveEngine engine;

    @Override
    protected Coordinates call() {
        return engine.findMove(board, difficulty, player);
    }
}
```

This makes the logic reusable outside JavaFX and much easier to test.

## Mediator Recommendation

Keep the mediator, but keep it behavior-oriented.

Prefer methods like:

```java
void executeTask(Task<?> task);

void onTaskSucceeded(WorkerStateEvent event);

void startNewGame();

void resetGame();
```

Avoid exposing low-level infrastructure:

```java
ExecutorService getExecutor();
```

Reason: child controllers should not know how tasks are scheduled. They should ask the mediator to perform an
application action.

## Naming Recommendations

Use singular names where the type represents one concept:

```text
ControllerMediator
ResourceBundleService
FxmlLoaderFactory
WinnerDetector
ComputerMoveEngine
```

Avoid vague names:

```text
util
constant
assist
interface
```

Better names:

```text
domain
infrastructure
resource
css
fxml
ui
engine
```

## Migration Plan

1. Remove or isolate prototype classes.

Move `GameUI`, `TicTacToe`, and `TestClass` out of the production package. If they are still useful, place them under a
clearly named package such as:

```text
com.mj.tictactoe.prototype
```

2. Rename the root package.

Move from:

```text
com.mj.tic.tac.toe.javafx.java
```

to:

```text
com.mj.tictactoe
```

This should be done once, using IDE refactoring, because FXML controller references and `module-info.java` must also be
updated.

3. Move domain classes out of `util`.

Move `Coordinates`, `Difficulty`, `GameState`, `Mark`, and `Winner` into `domain`.

4. Extract game logic from JavaFX tasks.

Move minimax, random move selection, winner checking, and board rules into `engine`.

5. Keep JavaFX tasks thin.

Tasks should call engine/service classes and return results. UI updates should remain on the JavaFX Application Thread.

6. Reorganize resources.

Move FXML under `fxml`, CSS under `css`, and properties under `i18n`.

7. Update `module-info.java`.

After package changes, update `opens` statements for JavaFX reflection:

```java
opens com.
mj.tictactoe.app to
javafx.graphics;
opens com.
mj.tictactoe.ui.controller to
javafx.fxml;
opens com.
mj.tictactoe.ui.dialog to
javafx.fxml;
```

## Recommended Final Structure

For this project size, avoid overengineering. This structure is enough:

```text
com.mj.tictactoe
├── app
├── domain
├── engine
├── ui
│   ├── controller
│   ├── dialog
│   └── task
└── infrastructure
    ├── fxml
    ├── i18n
    └── resource
```

This gives the project clear boundaries without creating too many packages.

## Highest Priority Changes

If you only do a few changes now, prioritize these:

1. Move `Coordinates`, `Difficulty`, `GameState`, `Mark`, and `Winner` from `util` to `domain`.
2. Move minimax and winner-checking logic out of JavaFX `Task` classes into `engine`.
3. Remove or isolate `GameUI`, `TicTacToe`, and `TestClass`.
4. Keep `ExecutorService` private and expose mediator methods such as `executeTask(...)`.
5. Rename the root package to `com.mj.tictactoe` when the current refactor stabilizes.

These changes will improve readability, testability, and long-term maintainability without forcing a full architecture
rewrite.
