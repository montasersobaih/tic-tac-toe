# `Executors` Methods — Comparison

All methods below live in `java.util.concurrent.Executors`.

## Summary Table

| Method                               | Returns                    | Core Pool Size | Max Pool Size       | Keep-Alive | Queue                 | Scheduled? | Thread Factory                                    |
|--------------------------------------|----------------------------|----------------|---------------------|------------|-----------------------|------------|---------------------------------------------------|
| `newSingleThreadExecutor()`          | `ExecutorService`          | 1              | 1                   | 0 s        | `LinkedBlockingQueue` | No         | default                                           |
| `newFixedThreadPool(n)`              | `ExecutorService`          | n              | n                   | 0 s        | `LinkedBlockingQueue` | No         | default                                           |
| `newCachedThreadPool()`              | `ExecutorService`          | 0              | `Integer.MAX_VALUE` | 60 s       | `SynchronousQueue`    | No         | default                                           |
| `newSingleThreadScheduledExecutor()` | `ScheduledExecutorService` | 1              | 1                   | 0 s        | `DelayedWorkQueue`    | Yes        | default                                           |
| `newScheduledThreadPool(n)`          | `ScheduledExecutorService` | n              | `Integer.MAX_VALUE` | 0 s        | `DelayedWorkQueue`    | Yes        | default                                           |
| `defaultThreadFactory()`             | `ThreadFactory`            | —              | —                   | —          | —                     | —          | creates daemon=false, normal-priority threads     |
| `privilegedThreadFactory()`          | `ThreadFactory`            | —              | —                   | —          | —                     | —          | same as default + inherits `AccessControlContext` |

## Detailed breakdown

### Factory methods returning `ExecutorService`

#### `newSingleThreadExecutor()`

- **Pool size**: exactly 1 thread.
- **Queue**: unbounded `LinkedBlockingQueue`.
- **Guarantee**: tasks execute sequentially, never concurrently. If the thread dies, a new one is created.
- **Use case**: background serial processing (e.g., write-ahead log, file-writer worker).

#### `newFixedThreadPool(int nThreads)`

- **Pool size**: exactly `n` threads.
- **Queue**: unbounded `LinkedBlockingQueue`.
- **Behavior**: if all threads are busy, new tasks queue up. No thread is ever removed (keep-alive doesn't apply since
  core == max).
- **Use case**: steady stream of CPU-bound work where you want to limit concurrency.

#### `newCachedThreadPool()`

- **Core**: 0 threads. Starts empty.
- **Max**: `Integer.MAX_VALUE` (effectively unbounded).
- **Queue**: `SynchronousQueue` — each `submit()` blocks until a thread picks it up.
- **Keep-alive**: 60 seconds. Idle threads are reclaimed.
- **Behavior**: reuses existing threads when possible; spawns new ones if none are idle. Scales up and down dynamically.
- **Use case**: many short-lived, IO-bound tasks (e.g., serving HTTP requests).

### Factory methods returning `ScheduledExecutorService`

#### `newSingleThreadScheduledExecutor()`

- Same as `newSingleThreadExecutor()` but implements `ScheduledExecutorService`.
- **Use case**: periodic or delayed tasks where ordering matters (e.g., heartbeats, retry timers).

#### `newScheduledThreadPool(int corePoolSize)`

- **Pool size**: `corePoolSize` threads that stay alive; can grow up to `Integer.MAX_VALUE` if needed (though the
  growable path is rarely used in practice).
- **Queue**: `DelayedWorkQueue` — tasks are ordered by their scheduled time.
- **Use case**: parallel periodic/delayed tasks (e.g., monitoring multiple connections).

### Thread Factory methods

#### `defaultThreadFactory()`

- Creates threads in the same `ThreadGroup`, with normal priority, non-daemon, and a name like `pool-N-thread-M`.
- **Use case**: default for all the factory methods above.

#### `privilegedThreadFactory()`

- Identical to `defaultThreadFactory()` except the new thread inherits the calling code's `AccessControlContext` (i.e.,
  security permissions).
- **Use case**: security-managed environments where the thread pool must run with the creator's privileges.

## Visual decision diagram

```
Need scheduled/delayed execution?
  ├── Yes, and single-threaded → newSingleThreadScheduledExecutor()
  ├── Yes, and multi-threaded  → newScheduledThreadPool(n)
  └── No (plain task execution)
       ├── Thread dies? Need bounded resource usage?
       │   ├── No → newCachedThreadPool() (auto-scale up/down)
       │   └── Yes
       │        ├── Single consumer → newSingleThreadExecutor()
       │        └── N consumers     → newFixedThreadPool(n)
```

## Key notes

- `newSingleThreadExecutor()` and `newSingleThreadScheduledExecutor()` wrap the pool so that it **cannot be reconfigured
  ** (e.g., you cannot later call `setCorePoolSize()`). The other methods return a directly configurable
  `ThreadPoolExecutor`.
- `newCachedThreadPool()` is dangerous under sustained high load — it will keep creating threads until memory runs out.
  Use it when you know tasks are short-lived.
- `newFixedThreadPool()` with an unbounded queue can also cause memory issues if producers outpace consumers
  indefinitely.
- `privilegedThreadFactory()` is rarely needed outside of RMI, security managers, or plugin systems.
