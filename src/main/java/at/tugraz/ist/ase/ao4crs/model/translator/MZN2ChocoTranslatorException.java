/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2021-2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.model.translator;

/**
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
public class MZN2ChocoTranslatorException extends RuntimeException {
    public MZN2ChocoTranslatorException(String mess) {
        super(mess);
    }
}
