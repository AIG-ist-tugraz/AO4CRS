/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core;

import at.tugraz.ist.ase.hiconfit.cacdr_core.Solution;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Item Assortment
 */
public class ItemAssortment implements Iterable<Item> {
    List<Item> items = new LinkedList<>();

    /**
     * @return the number of items in the assortment
     */
    public int size() {
        return items.size();
    }

    public void add(Item item) {
        items.add(item);
    }

    public Iterator<Item> iterator() {
        return items.listIterator();
    }

    /**
     * Get the item with the given properties
     * @param properties the properties of the item
     * @return  the item with the given properties
     */
    public Optional<Item> get(Solution properties) {
        return items.stream().filter(i -> i.properties().equals(properties)).findFirst();
    }
}
