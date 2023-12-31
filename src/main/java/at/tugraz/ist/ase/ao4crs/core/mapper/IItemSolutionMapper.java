/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core.mapper;

import at.tugraz.ist.ase.ao4crs.core.Item;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Solution;

public interface IItemSolutionMapper {
    Item toItem(Solution solution, int num_properties);
}
