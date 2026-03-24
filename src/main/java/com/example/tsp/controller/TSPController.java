package com.example.tsp.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class TSPController {

    // DTO class for input
    static class Point {
        public double x;
        public double y;
    }

    @PostMapping("/solveTSP")
    public Map<String, Object> solveTSP(@RequestBody List<Point> points) {

        if (points == null || points.size() < 2) {
            return Map.of(
                    "path", new ArrayList<>(),
                    "distance", 0
            );
        }

        int n = points.size();

        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[n];

        int current = 0;
        path.add(current);
        visited[current] = true;

        double totalDistance = 0;

        for (int i = 1; i < n; i++) {
            int next = -1;
            double minDist = Double.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (!visited[j]) {
                    double dist = distance(points.get(current), points.get(j));
                    if (dist < minDist) {
                        minDist = dist;
                        next = j;
                    }
                }
            }

            path.add(next);
            visited[next] = true;
            totalDistance += minDist;
            current = next;
        }

        // return to start (optional)
        totalDistance += distance(points.get(current), points.get(0));

        return Map.of(
                "path", path,
                "distance", totalDistance
        );
    }

    private double distance(Point a, Point b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}