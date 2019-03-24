/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signalflowgraph.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import javafx.util.Pair;

/**
 *
 * @author arabtech
 */
public class Graph {

    private int size;
    private boolean[] visited;
    private List<List<Pair<Integer, Integer>>> g;
    private ArrayList<ArrayList<Integer>> pathList;
    private ArrayList<Integer> pathWeights;
    private ArrayList<ArrayList<Integer>> cycles;
    private ArrayList<Integer> cycleWeights;
    private Stack<Integer> stack;
    private int[] deltas;
    private int weight;
    private int L;
    private ArrayList<Integer> path;
    private ArrayList<Integer> nonTouchingWeights;
    private ArrayList<ArrayList<Integer>> nonTouchingCycles;

    public Graph(int size) {
        this.size = size;
        visited = new boolean[size];
        g = createGraph(size);
        pathList = new ArrayList<>();
        pathWeights = new ArrayList<>();
        cycles = new ArrayList<>();
        cycleWeights = new ArrayList<>();
        stack = new Stack<>();
        weight = 1;
        L = 1;
        nonTouchingWeights = new ArrayList<>();
        nonTouchingCycles = new ArrayList<>();
    }

    public List<List<Pair<Integer, Integer>>> getGraph() {
        return g;
    }

    public String getTransferFunction(int from, int to) {
        visited = new boolean[size];
        path = new ArrayList<>();
        findPaths(from, to, g);
        deltas = new int[pathList.size()];
        path = new ArrayList<>();
        visited = new boolean[size];
        findCycles(0, g);
        findDeltas();
        findNonTouchingCycles();
        long numerator = 0;
        for (int i = 0; i < pathList.size(); i++) {
            numerator += pathWeights.get(i) * deltas[i];
        }
        long denominator = 0;
        int sumOfCycleWeights = 0;
        for (int i = 0; i < cycleWeights.size(); i++) {
            sumOfCycleWeights += cycleWeights.get(i);
        }
        int sumOfNonTouching = 0;
        for (int i = 0; i < nonTouchingWeights.size(); i++) {
            sumOfNonTouching += nonTouchingWeights.get(i);
        }
        denominator = 1 - sumOfCycleWeights + sumOfNonTouching;
        String result = "C(S)/R(S) = " + numerator / gcd(numerator, denominator) + "/" + denominator / gcd(numerator, denominator);
        System.out.println(result);
        return result;
    }

    public List<List<Pair<Integer, Integer>>> createGraph(int n) {
        List<List<Pair<Integer, Integer>>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<Pair<Integer, Integer>>());
        }
        return graph;
    }

    public void addEdge(int from, int to, int weight) {
        g.get(from).add(new Pair(to, weight));
    }

    public void findPaths(int v, int end, List<List<Pair<Integer, Integer>>> graph) {
        visited[v] = true;
        path.add(v);
        if (v == end) {
            visited[v] = false;
            /*for (int i = 0; i < path.size(); i++) {
             System.out.print(path.get(i) + " ");
             }
             System.out.println();*/
            pathList.add((ArrayList<Integer>) path.clone());
            pathWeights.add(weight);
            return;
        }
        for (int i = 0; i < graph.get(v).size(); i++) {
            if (!visited[graph.get(v).get(i).getKey()]) {
                weight *= graph.get(v).get(i).getValue();
                findPaths(graph.get(v).get(i).getKey(), end, graph);
                path.remove(graph.get(v).get(i).getKey());
                weight /= graph.get(v).get(i).getValue();
            }
        }
        visited[v] = false;
    }

    public void findCycles(int v, List<List<Pair<Integer, Integer>>> graph) {
        visited[v] = true;
        stack.add(v);
        for (int i = 0; i < graph.get(v).size(); i++) {
            if (!visited[graph.get(v).get(i).getKey()]) {
                findCycles(graph.get(v).get(i).getKey(), graph);
                stack.remove(graph.get(v).get(i).getKey());
            } else if (stack.contains(graph.get(v).get(i).getKey())) {
                int z = 1;
                path.add(graph.get(v).get(i).getKey());
                while (stack.get(stack.size() - z) != graph.get(v).get(i).getKey()) {
                    path.add(stack.get(stack.size() - z));
                    z++;
                }
                path.add(graph.get(v).get(i).getKey());
                Collections.reverse(path);
                for (int r = 0; r < path.size() - 1; r++) {
                    int index = 0;
                    for (int m = 0; m < graph.get(path.get(r)).size(); m++) {
                        if (graph.get(path.get(r)).get(m).getKey().equals(path.get(r + 1))) {
                            index = m;
                        }
                    }
                    L *= graph.get(path.get(r)).get(index).getValue();
                }
                if (cycleWeights.contains(L)) {
                    int index = cycleWeights.indexOf(L);
                    ArrayList<Integer> c = cycles.get(index);
                    int cnt = 0;
                    for (int j = 0; j < c.size(); j++) {
                        if (path.contains(c.get(j))) {
                            cnt++;
                        }
                    }
                    if (cnt != c.size() && cnt != path.size()) {
                        cycleWeights.add(L);
                        cycles.add(path);
                    }
                } else {
                    cycleWeights.add(L);
                    cycles.add(path);
                    /*for(int r=0;r<path.size();r++){
                     System.out.print(path.get(r)+" ");
                     }
                     System.out.println();*/
                }
                path = new ArrayList<>();
                L = 1;
            }
        }
        visited[v] = false;
    }

    public void findDeltas() {
        boolean valid = true;
        for (int i = 0; i < pathList.size(); i++) {
            for (int k = 0; k < cycles.size(); k++) {
                for (int j = 0; j < pathList.get(i).size(); j++) {
                    if (cycles.get(k).contains(pathList.get(i).get(j))) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    deltas[i] += cycleWeights.get(k);
                }
                valid = true;
            }
        }
        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = 1 - deltas[i];
            //System.out.println(deltas[i]);
        }
    }

    public void findNonTouchingCycles() {
        path = new ArrayList<>();
        boolean valid = true;
        boolean[] print = new boolean[size]; //no. of nodes
        nonTouchingCycles = (ArrayList<ArrayList<Integer>>) cycles.clone();
        // nonTouchingWeights=(ArrayList<Integer>) cycleWeights.clone();
        for (int i = 0; i < nonTouchingCycles.size(); i++) {
            for (int j = i + 1; j < nonTouchingCycles.size(); j++) {
                for (int k = 0; k < nonTouchingCycles.get(i).size(); k++) {
                    if (nonTouchingCycles.get(j).contains(nonTouchingCycles.get(i).get(k))) {
                        valid = false;
                        break;
                    } else {
                        path.add(nonTouchingCycles.get(i).get(k));
                    }
                }
                if (valid) {
                    for (int z = 0; z < nonTouchingCycles.get(j).size(); z++) {
                        path.add(nonTouchingCycles.get(j).get(z));
                    }
                    int firstWeight;
                    int secondWeight;
                    if (i < cycleWeights.size()) {
                        firstWeight = cycleWeights.get(i);
                    } else {
                        firstWeight = nonTouchingWeights.get(i - cycleWeights.size());
                    }
                    if (j < cycleWeights.size()) {
                        secondWeight = cycleWeights.get(j);
                    } else {
                        secondWeight = nonTouchingWeights.get(j - cycleWeights.size());
                    }
                    int weight = firstWeight * secondWeight;
                    if (nonTouchingWeights.contains(weight)) {
                        int index = cycleWeights.size() + nonTouchingWeights.indexOf(weight);
                        int cnt = 0;
                        for (int r = 0; r < nonTouchingCycles.get(index).size(); r++) {
                            if (path.contains(nonTouchingCycles.get(index).get(r))) {
                                cnt++;
                            }
                        }
                        if (cnt != nonTouchingCycles.get(index).size()) {
                            nonTouchingWeights.add(weight);
                            nonTouchingCycles.add((ArrayList<Integer>) path.clone());
                            System.out.println("Non-touching loops are: ");
                            for (int f = 0; f < path.size(); f++) {
                                System.out.print(path.get(f)+" ");
                                 if(!print[path.get(f)]){
                                     print[path.get(f)]=true;
                                 }
                                 else{
                                     System.out.println();
                                 }
                            }
                        }
                    } else {
                        nonTouchingWeights.add(weight);
                        nonTouchingCycles.add((ArrayList<Integer>) path.clone());
                        System.out.println("Non-touching loops are: ");
                        for (int f = 0; f < path.size(); f++) {
                                System.out.print(path.get(f)+" ");
                                 if(!print[path.get(f)]){
                                     print[path.get(f)]=true;
                                 }
                                 else{
                                     System.out.println();
                                 }
                            }
                    }
                }
                print=new boolean[size]; //no. of nodes 
                valid = true;
                path = new ArrayList();
            }
        }
    }

    public long gcd(long a, long b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
