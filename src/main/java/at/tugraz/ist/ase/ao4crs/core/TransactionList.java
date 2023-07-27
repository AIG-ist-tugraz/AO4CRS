/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core;

import lombok.Getter;
import lombok.Synchronized;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A list of transactions
 */
public class TransactionList implements Iterable<Transaction> {

    @Getter
    List<Transaction> transactions = new LinkedList<>();

    public int size() {
        return transactions.size();
    }

    @Synchronized
    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public Iterator<Transaction> iterator() {
        return transactions.listIterator();
    }

    /**
     * Returns the number of times an item was purchased
     * @param i a item
     * @return the number of times the item was purchased
     */
    public long selections(Item i) {
        return transactions.parallelStream().filter(t -> t.item_id().equals(i.id())).count();
    }
}
