/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.ao;

import at.tugraz.ist.ase.hiconfit.cacdr_core.Requirement;
import at.tugraz.ist.ase.hiconfit.common.LoggerUtils;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Implementation of Global Controversy
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class GlobalControversy extends AnalysisOperation {

    public GlobalControversy(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile) {
        super(fmFile, filterFile, itemsFile);
    }

    public double calculate() throws IOException, FeatureModelParserException {
        UserRequirement urOperation = new UserRequirement(fmFile, filterFile, itemsFile);

        // all user requirements
        List<Requirement> requirements = urOperation.getRequirements();
        int numberOfRequirements = requirements.size();

        // all inconsistent user requirements
        requirements = urOperation.getInconsistentUserRequirements();
        int numberOfInconsistentRequirements = requirements.size();

        // calculate the global controversy
        double globalControversy = (double) numberOfInconsistentRequirements / numberOfRequirements;

        // print results
        if (printResults) {
            String message = String.format("%sTotal inconsistent URs: %s", LoggerUtils.tab(), numberOfInconsistentRequirements);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
            message = String.format("%sTotal URs: %s", LoggerUtils.tab(), numberOfRequirements);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
            message = String.format("%sGlobal controversy: %s", LoggerUtils.tab(), globalControversy);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
        }

        return globalControversy;
    }

}
