/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core;

import at.tugraz.ist.ase.hiconfit.common.LoggerUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

@Slf4j
public class AllRecommendationLists implements Iterable<RecommendationList> {

    @Setter
    BufferedWriter writer = null;
    @Setter
    boolean printResults = true;

    List<RecommendationList> all = new ArrayList<>();

    public void add(RecommendationList list) {
        all.add(list);
//        if (!all.contains(list)) {
//            all.add(list);
//        }
    }

    public int countOccurrence(Item item) {
        return (int) all.parallelStream().filter(list -> list.contains(item)).count();
    }

    public double visibility(Item item) throws IOException {
        int total_rank = 0;
        int total_worst_rank = 0;
        for (RecommendationList list : all) {
            if (list.contains(item)) {
                int rank = list.rank(item);
                int worst_rank = list.size();

                if (printResults) {
                    String message = String.format("%srank: %s - worst rank: %s", LoggerUtils.tab(), rank, worst_rank);
                    log.info(message);
                    if (writer != null) {
                        writer.write(message);
                        writer.newLine();
                    }
                }

                total_rank += rank;
                total_worst_rank += worst_rank;
            }
        }
        return 1 - ((double) total_rank / total_worst_rank);
    }

    public int size() {
        return all.size();
    }

    @Override
    public Iterator<RecommendationList> iterator() {
        return all.iterator();
    }

    @Override
    public void forEach(Consumer<? super RecommendationList> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<RecommendationList> spliterator() {
        return Iterable.super.spliterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (RecommendationList list : all) {
            sb.append(list.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
