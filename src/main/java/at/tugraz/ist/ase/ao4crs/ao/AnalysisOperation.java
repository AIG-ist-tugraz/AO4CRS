/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.ao;

import lombok.NonNull;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.File;

/**
 * Base class for all analysis operations
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
public abstract class AnalysisOperation {
    @Setter
    protected BufferedWriter writer = null; // print results to file
    @Setter
    protected boolean printResults = true; // true to print results, false to not print results

    protected final File fmFile;
    protected final File filterFile;
    protected final File itemsFile;

    public AnalysisOperation(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile) {
        this.fmFile = fmFile;
        this.filterFile = filterFile;
        this.itemsFile = itemsFile;
    }
}
