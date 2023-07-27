/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core;

import at.tugraz.ist.ase.hiconfit.cacdr_core.Requirement;

/**
 * A Transaction
 * @param id just an id
 * @param ur_id user requirement id
 * @param item_id id of the item that customer bought
 * @param req the user requirement
 */
public record Transaction(int id, int ur_id, String item_id, Requirement req) { }
