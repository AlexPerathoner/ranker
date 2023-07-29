[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AlexPerathoner_ranker&metric=coverage)](https://sonarcloud.io/summary/new_code?id=AlexPerathoner_ranker)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=AlexPerathoner_ranker&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=AlexPerathoner_ranker)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=AlexPerathoner_ranker&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=AlexPerathoner_ranker)

# Ranker

## Problem
Having many hundreds of items you'd like to subjectively grade on a given scale gets exponentially higher the more items you have. Especially if that grading start many years ago, and hasn't started the way you're doing it now.

Let's consider this concrete example:

- You have an Anilist profile, with hundreds of completed series.
- You started rating them from 0.0 to 10.0 following a series of criteria.
- Years pass and your ratings slightly change.
- You now find series that have about the same grade as some others, which you consider way better or worse

## Concept

### User use case

- We import all completed series of one user
- We choose a pair of series
- We ask to the user which of two is better
- We keep doing that over and over

### Implementation solution

- We represent all series as nodes
- Each preference of one series in a pair is represented with an directed edge between two nodes
- We calculate a hidden ranking value using [PageRank](https://en.wikipedia.org/wiki/PageRank)
- We convert the hidden ranking value into a grade, with the wanted distribution of grades (e.g. normal, linear...)

# TODO

## Backend
- login with anilist
- add import of data
	- add graphql query
	- save results to DB
	- use DB if data is present, else load data
		- handle response with frontend

## Frontend
- export ranking
	- as table (csv)
	- as notes, with given distribution
	- -> create script to update rankings in anilist?
- table view to delete existing edges
	- change into a graph view?
- add keyboard shortcuts



## Integration
- all
