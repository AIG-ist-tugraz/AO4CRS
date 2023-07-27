/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core.rank;

import at.tugraz.ist.ase.ao4crs.core.Item;
import lombok.NonNull;

import java.io.File;
import java.util.List;

public interface IItemRankingStrategy {
    List<Item> rank(List<Item> items);

    IItemRankCalculatable getCalculator(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile);
}
