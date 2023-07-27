/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core;

import com.google.common.base.Objects;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkElementIndex;

/**
 * A list of recommended items for a user requirement
 */
public class RecommendationList implements Iterable<Item> {

    List<Item> items;

    public RecommendationList(List<Item> items) {
        this.items = items;
    }

    public int size() {
        return items.size();
    }

    public boolean empty() {
        return items.isEmpty();
    }

    public Item get(int index) {
        checkElementIndex(index, items.size(), "Item index out of bound!");
        return items.get(index);
    }

    /**
     * Get the rank of the item in the list
     * @param item the item
     * @return the rank of the item in the list
     */
    public int rank(Item item) {
        return items.indexOf(item) + 1;
    }

    /**
     * Check if the recommendation list contains the item
     * @param item the item
     * @return true if the recommendation list contains the item
     */
    public boolean contains(Item item) {
        return items.parallelStream().anyMatch(i -> Objects.equal(i, item));
    }

    /**
     * Check if the feature is part of the user requirements that led to the recommendation list
     * @param feature the feature
     * @return true if the feature is part of the user requirements that led to the recommendation list
     */
    public boolean contains(String feature) {
        return items.stream().anyMatch(i -> i.fm_values().getAssignments().stream().anyMatch(a -> a.getVariable().equals(feature) && a.getValue().equals("true")));
    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

    @Override
    public void forEach(Consumer<? super Item> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Item> spliterator() {
        return Iterable.super.spliterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecommendationList p)) return false;
        if (items.size() != p.items.size()) return false;
        return IntStream.range(0, items.size()).allMatch(i -> items.get(i).equals(p.items.get(i)));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(items);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            sb.append(item.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
