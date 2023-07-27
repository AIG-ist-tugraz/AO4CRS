/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core.rank;

import at.tugraz.ist.ase.ao4crs.core.ItemAssortment;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;

import java.io.IOException;

public interface IItemRankCalculatable {
    ItemAssortment calculate(ItemAssortment itemAssortment) throws FeatureModelParserException, IOException;
}
