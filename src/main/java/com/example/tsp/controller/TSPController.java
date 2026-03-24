package com.example.tsp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TSPController {

    // ✅ Test API
    @GetMapping("/")
    public String home() {
        return "TSP Backend Running 🚀";
    }

    // ✅ MAIN TSP API (GENETIC ALGORITHM)
    @PostMapping("/tsp")
    public Map<String, Object> solveTSP(@RequestBody List<Map<String, Double>> cities) {

        int n = cities.size();
        int populationSize = 100;
        int generations = 300;

        Random rand = new Random();

        // 🔹 Initial population
        List<List<Integer>> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            List<Integer> path = new ArrayList<>();
            for (int j = 0; j < n; j++) path.add(j);
            Collections.shuffle(path);
            population.add(path);
        }

        List<Integer> bestPath = null;
        double bestDistance = Double.MAX_VALUE;

        // 🔁 Genetic Algorithm loop
        for (int gen = 0; gen < generations; gen++) {

            // Sort by fitness (distance)
            population.sort(Comparator.comparingDouble(p -> totalDistance(p, cities)));

            double currentBest = totalDistance(population.get(0), cities);

            if (currentBest < bestDistance) {
                bestDistance = currentBest;
                bestPath = new ArrayList<>(population.get(0));
            }

            List<List<Integer>> newPopulation = new ArrayList<>();

            // 🏆 Elitism (keep best 20)
            for (int i = 0; i < 20; i++) {
                newPopulation.add(new ArrayList<>(population.get(i)));
            }

            // 🔀 Crossover + mutation
            while (newPopulation.size() < populationSize) {
                List<Integer> parent1 = population.get(rand.nextInt(50));
                List<Integer> parent2 = population.get(rand.nextInt(50));

                List<Integer> child = crossover(parent1, parent2);

                // 🔁 Mutation
                if (rand.nextDouble() < 0.1) {
                    int i = rand.nextInt(n);
                    int j = rand.nextInt(n);
                    Collections.swap(child, i, j);
                }

                newPopulation.add(child);
            }

            population = newPopulation;
        }

        // 🔁 Return to start
        bestPath = normalizePath(bestPath);
        bestPath.add(bestPath.get(0));

        Map<String, Object> response = new HashMap<>();
        response.put("path", bestPath);
        response.put("distance", bestDistance);

        return response;
    }

    private List<Integer> normalizePath(List<Integer> path) {
        int startIndex = path.indexOf(0);

        List<Integer> normalized = new ArrayList<>();

        for (int i = 0; i < path.size(); i++) {
            normalized.add(path.get((startIndex + i) % path.size()));
        }

        return normalized;
    }

    // 📏 TOTAL DISTANCE
    private double totalDistance(List<Integer> path, List<Map<String, Double>> cities) {
        double dist = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            dist += distance(cities.get(path.get(i)), cities.get(path.get(i + 1)));
        }

        dist += distance(cities.get(path.get(path.size() - 1)), cities.get(path.get(0)));

        return dist;
    }

    // 🔀 CROSSOVER (GENETIC)
    private List<Integer> crossover(List<Integer> p1, List<Integer> p2) {
        int n = p1.size();
        Random rand = new Random();

        int start = rand.nextInt(n);
        int end = rand.nextInt(n);

        List<Integer> child = new ArrayList<>(Collections.nCopies(n, -1));

        for (int i = Math.min(start, end); i <= Math.max(start, end); i++) {
            child.set(i, p1.get(i));
        }

        for (int i = 0; i < n; i++) {
            if (!child.contains(p2.get(i))) {
                for (int j = 0; j < n; j++) {
                    if (child.get(j) == -1) {
                        child.set(j, p2.get(i));
                        break;
                    }
                }
            }
        }

        return child;
    }

    // 🌍 REAL DISTANCE (HAVERSINE FORMULA)
    private double distance(Map<String, Double> a, Map<String, Double> b) {

        double lat1 = Math.toRadians(a.get("x"));
        double lon1 = Math.toRadians(a.get("y"));
        double lat2 = Math.toRadians(b.get("x"));
        double lon2 = Math.toRadians(b.get("y"));

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double hav = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(hav), Math.sqrt(1 - hav));

        double R = 6371; // Earth radius (KM)

        return R * c;
    }
}

