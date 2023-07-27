# AO4CRS

Source code to accompany the paper "Analysis Operations for Constraint-based Recommender Systems"
will be presented in [RecSys'23](https://recsys.acm.org/recsys23/).

## Table of Contents

- [Repository structure](#repository-structure)
- [How to reproduce the experiment](#how-to-reproduce-the-experiment)

## Repository structure

| *folder*               | *description*                                                          |
|------------------------|------------------------------------------------------------------------|
| ./data/conf/           | the configuration files                                                |
| ./data/digicam.xml     | testing feature model                                                  |
| ./data/filter.mzn      | filter constraints using MiniZinc syntax                               |
| ./data/items.csv       | item assortment                                                     |
| ./data/query           | stores three user requirements used in calculating the Restrictiveness |
| ./lib                  | a library                                                              |
| ./src                  | source code                                                            |
| Dockerfile             | Dockerfile to build the Docker image                                   |
| results.txt            | results                                                                |

## How to reproduce the experiment

Build a Docker image of the **AO4CRS** with the following command:

```shell
docker build -t ao4crs .
```

> It took around 4-5 minutes to complete this step on an Apple M1 laptop.

Next, create a folder for the experiment results and copy results inside the Docker image to the folder:

```shell
mkdir results
docker run --rm --entrypoint tar ao4crs cC ./results . | tar xvC ./results
```
