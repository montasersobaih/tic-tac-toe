# MinimaxMoveStrategy — Efficiency Recommendations

This document identifies performance bottlenecks and proposes concrete
optimisations for `MinimaxMoveStrategy`. The naive minimax explores O(b^d)
nodes (b = branching factor ≈ board size², d = depth ≈ empty cells), which
becomes prohibitive beyond 4×4.

---

## 1. Actually wire up alpha-beta pruning

**Problem:** The class-level Javadoc claims alpha-beta pruning is used, but the
`minimax` method signature has no `alpha` / `beta` parameters
(`MinimaxMoveStrategy.java:77`). Every leaf is visited.

**Recommendation:** Add `int alpha, int beta` parameters and prune when
`alpha >= beta`. For a well-ordered 3×3 tree this cuts the node count from
roughly 550k to ~20k. On larger boards the gains are even more dramatic.

```java
private int minimax(Board board, Player opponent, Player player,
                    int alpha, int beta) {
    // … base cases unchanged …
    for (int i = 0; i < dimension; i++) {
        for (int j = 0; j < dimension; j++) {
            if (board.get(i, j) == 0) {
                board.mark(i, j, opponent);
                int score = minimax(board, opponent.opponent(), player,
                                    alpha, beta);
                board.unmark(i, j);
                if (isMax) {
                    alpha = Math.max(alpha, score);
                    if (alpha >= beta) return alpha;   // prune
                } else {
                    beta = Math.min(beta, score);
                    if (alpha >= beta) return beta;     // prune
                }
            }
        }
    }
    return isMax ? alpha : beta;
}
```

The top-level `findMove` call would pass `α = -∞, β = +∞`.

---

## 2. Order moves by heuristic value

**Problem:** Alpha-beta pruning is most effective when the best move is
examined first. The current code iterates cells in row-major order, which
often evaluates a poor move first.

**Recommendation:** Score empty cells with a cheap heuristic and sort
descending (max player) or ascending (min player). Good heuristics:

- **Centre proximity** — cells closer to the centre participate in more
  winning lines and are more likely to be good.
- **Line potential** — count how many of the cell's intersecting lines
  already contain friendly marks.
- **Threat detection** — prefer cells that complete a line or block an
  opponent's line.

For a 3×3 board a static priority map suffices; for larger boards compute
dynamically but keep the sort O(n² log n) — negligible next to the search
cost it saves.

```java
List<int[]> scored = new ArrayList<>(emptyCount);
for (each empty cell) {
    scored.add(new int[]{x, y, heuristic(board, x, y, opponent)});
}
if (isMax) scored.sort((a,b) -> b[2] - a[2]);
else       scored.sort((a,b) -> a[2] - b[2]);
```

---

## 3. Cache evaluated states with a transposition table

**Problem:** Different move sequences can lead to identical board
configurations, but the algorithm re-evaluates them from scratch every time.

**Recommendation:** Maintain a `Map<Long, Integer>` (or a fixed-size LRU cache)
keyed by a Zobrist hash of the board. Before recursing, check the table; on
return, store the score.

```java
// Pre‑generate random values for each (cell, player) pair.
private static final long[][][] zobristTable = new long[MAX_DIM][MAX_DIM][3];

long hash = computeZobristHash(board);
if(transpositionTable.

containsKey(hash)){
        return transpositionTable.

get(hash);
}
        // … recurse …
        transpositionTable.

put(hash, score);
```

The hash can be updated incrementally during `mark`/`unmark` to avoid
O(n²) re-computation at every node.

**Memory note:** Only cache nodes where the remaining depth ≥ a threshold
(e.g. 2) to avoid storing millions of shallow entries. On large boards use
a bounded cache (`LinkedHashMap` with `removeEldestEntry`) to prevent
unbounded memory growth.

---

## 4. Precompute the first move

**Problem:** On an empty board the search explores all `n²` symmetrically
equivalent cells, then recurses through the entire game tree — completely
unnecessary work.

**Recommendation:** If all cells are empty, return the optimal opening
move immediately without search:

- **Odd dimensions (3×3, 5×5, …):** the centre cell `(n/2, n/2)` is the
  optimal first move.
- **Corner cells** are also perfectly optimal for 3×3.

```java
if(board.getMarkedCellsCount() ==0){
int mid = dimension / 2;
    return Optional.

of(new Coordinates(mid, mid));
        }
```

This eliminates the deepest branch of the entire tree.

---

## 5. Limit depth with iterative deepening

**Problem:** On 5×5+ boards even alpha-beta + ordering cannot finish
within a reasonable time — the branching factor is simply too large.

**Recommendation:** Use **iterative deepening** with a **time budget**.
Start with depth 1 and increase until the time limit is reached, then
return the best move from the deepest completed iteration. The previous
iteration's best move also serves as the first candidate for the next
iteration, feeding directly into move ordering (recommendation #2).

```java
long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeLimitSeconds);
        int bestX = dimension / 2, bestY = dimension / 2;
for(
int depth = 1;
depth <=maxDepth;depth++){
        try{
int result = iterativeMinimax(board, player, depth, deadline);
// … update best move …
    }catch(
TimeoutException e){
        break;
        }
        }
```

On 5×5, searching to depth 2–3 within 5 seconds is feasible and still
provides competent play.

---

## 6. Replace WinnerDetector with an inline check

**Problem:** `WinnerDetector.detect(board)` launches three
`CompletableFuture`s every call, adding significant overhead (thread pool
submission, synchronization, `allOf` coordination) even though the minimax
hot path calls it millions of times.

**Recommendation:** Use the same tight inline loop that is already
described in the class Javadoc. Check only the 2n lines (n rows + n
columns + 2 diagonals) directly in the `minimax` method.

```java
private boolean hasWinner(Board board, int x, int y) {
    byte p = board.get(x, y);
    if (p == 0) return false;
    int n = board.getDimension();
    // check row
    for (int j = 0; j < n; j++) if (board.get(x, j) != p) break row;
    // check column
    for (int i = 0; i < n; i++) if (board.get(i, y) != p) break col;
    // check main diagonal
    if (x == y) { for (int i = 0; i < n; i++) if (board.get(i, i) != p) break; }
    // check anti-diagonal
    if (x + y == n - 1) { … }
}
```

Or even simpler, since the inline check in `MinimaxMoveStrategy.java:77` is
described but not actually used, just reuse the inline logic from the
`TicTacToeMinimax` class if it already exists.

---

## 7. Reduce symmetry duplication

**Problem:** Two board states that are rotations or reflections of each
other are functionally identical, yet minimax evaluates both.

**Recommendation:** Normalise each board to a canonical rotation before
querying the transposition table (recommendation #3). This maps 8 symmetric
states to 1 entry, effectively multiplying the cache size by 8.

```java
long canonicalHash = min(
        zobristRotate0, zobristRotate90, zobristRotate180, zobristRotate270,
        zobristReflectH, zobristReflectV, zobristReflectDiag1, zobristReflectDiag2
);
```

---

## 8. Flatten the board to a 1D byte array

**Problem:** `byte[][]` involves two heap allocations per board and an
extra pointer dereference on every access. For the minimax hot path this
adds measurable latency.

**Recommendation:** Store cells in a single `byte[]` of length `n × n`.
Access `cells[x * n + y]` instead of `cells[x][y]`. This improves cache
locality and reduces indirect costs.

```java
private final byte[] cells;  // cells[x * dimension + y]
```

`Board.mark`, `Board.unmark`, and `Board.get` would all need corresponding
changes.

---

## Summary of expected gains

| Optimisation                | 3×3 speedup | 5×5 impact              |
|-----------------------------|-------------|-------------------------|
| Alpha-beta pruning          | 20–50×      | Makes 5×5 tractable     |
| Move ordering               | 2–3×        | Critical for alpha-beta |
| Transposition table         | 2–4×        | Significant             |
| First-move shortcut         | ~20×        | ~20×                    |
| Iterative deepening + timer | —           | Only viable approach    |
| Inline winner check         | 3–5×        | 3–5×                    |
| Symmetry reduction          | 2–8×        | 2–8×                    |
| Flatten to `byte[]`         | 1.1–1.3×    | 1.1–1.3×                |

The first four items (alpha-beta, ordering, transposition table, first-move
shortcut) deliver the largest wins. For 5×5+, iterative deepening with a
time limit (#5) is **essential** — without it the search simply will not
finish.
