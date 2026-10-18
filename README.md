# Ski Resort Simulation

A discrete-event simulation of skier traffic around a ski resort — slopes and lifts modelled
as a directed graph, three skier strategies, and LaTeX map rendering of what happened.

Written in Java for the Object-Oriented Programming course at the University of Warsaw (2026).

## What it simulates

A resort is a graph: **nodes** are points on the mountain, **edges** are either a **slope**
(skiable downhill, with a length, difficulty and congestion effect) or a **lift** (fixed
departure interval, capacity and travel time). Skiers enter the resort over the day, choose
where to go, queue for lifts and ski down, and every decision depends on how attractive a
slope looks to that particular skier at that moment.

Three skier types share one engine and differ only in how they choose:

| Type | Strategy |
|---|---|
| **Local** | decides at every node, may change its mind before skiing the slope it picked |
| **Greedy** | picks the most attractive slope in the whole resort, then plans a route to it |
| **Collector** | heads for the slope it has skied the fewest times, breaking ties by distance, then by attractiveness |

Attractiveness combines the slope's own properties with a **boredom** term, so a skier who has
just done a slope finds it less tempting than one they have not seen all day.

## Technical highlights

- **Discrete-event engine.** Time advances event by event, not tick by tick: `SilnikSymulacji`
  pulls the earliest event from a priority queue, applies it and schedules whatever follows
  (`ZdarzeniePojawienieNaStoku`, `ZdarzenieOdjazdWyciagu`, `ZdarzenieKoniecWjazduWyciagiem`,
  `ZdarzenieKoniecZjazduTrasa`).
- **Data structures written from scratch.** A binary heap (`KopiecZdarzen`) backing the event
  queue and a circular buffer (`BuforCykliczny`) for lift queues. The heap sits behind the same
  `KolejkaZdarzen` interface as a `java.util`-based implementation
  (`KolejkaZdarzenBiblioteczna`), so the two can be swapped and compared.
- **BFS path planning** (`WyszukiwarkaSciezek`). Route plans are found by breadth-first search
  over the combined graph of slopes and lifts; the path is reconstructed by walking back along
  the edge through which each node was first reached.
- **Polymorphic strategies.** `Sportowiec` (abstract) → `SportowiecLokalny` and
  `SportowiecZPlanem` → `SportowiecZachlanny` / `SportowiecKolekcjoner`. The engine never asks
  which type it is holding.
- **Visualisation.** The run emits LaTeX (`.tex`) maps of the resort and of individual skiers'
  journeys, plus end-of-day statistics.

## Layout

```
src/
  elementy_symulacji/   Osrodek, Wezel, Krawedz, Trasa, Wyciag
  sportowcy/            skier hierarchy
  przeszukiwanie/       WyszukiwarkaSciezek (BFS), Sciezka
  struktury_symulacji/  KolejkaZdarzen (interface), KopiecZdarzen, KolejkaZdarzenBiblioteczna, BuforCykliczny
  symulacja/            SilnikSymulacji, Zdarzenie and its subtypes
  wejscie/              WczytanieDanych — parsing the input format
  wizualizacja/         GeneratorWizualizacji — LaTeX output
  generator-mapek/      PROVIDED BY THE COURSE — not in this repository, see below
  Main.java
test/                   JUnit 5 tests
tests/                  sample input files
```

Identifiers and comments are in Polish because the course required it.

> **Not my code, and not included here:** the map-drawing library `kadra.mapki`
> (`src/generator-mapek/`) was supplied by the teaching staff and used unmodified. It is
> theirs to distribute, not mine, so it is left out of this repository — drop it into
> `src/generator-mapek/` to build the visualisation. Everything else, roughly 1,400 lines
> across the packages listed above, is my own work.

## Building and running

Requires **JDK 21** and, for the tests, the JUnit 5 standalone console JAR placed in
`lib/junit-platform-console-standalone.jar`
([download](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/)).

```powershell
.\build.ps1                                              # compile to out\
.\build.ps1 -Test                                        # compile and run the JUnit tests
.\build.ps1 -Run -Dane "tests\przykladowe-dane-1.txt"    # compile and run a simulation
```

The simulation reads its scenario from standard input and writes `.tex` maps into the directory
given by `-MapDir` (default `mapki-out`). Assertions are enabled (`-ea`) on every run.

## Input format

Plain text, read from stdin: the list of nodes, then the edges (slopes and lifts with their
parameters), then the skiers with their arrival times and preferences. `tests/` holds two
complete example scenarios.

## Tests

Six JUnit 5 test classes cover the parts where a bug would be invisible in the output: BFS path
finding, event-queue ordering, lift departure logic, attractiveness, boredom decay and lift
statistics.
