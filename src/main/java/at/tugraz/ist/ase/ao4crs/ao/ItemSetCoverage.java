/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.ao;

import at.tugraz.ist.ase.ao4crs.core.Item;
import at.tugraz.ist.ase.hiconfit.common.LoggerUtils;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Implementation of Item Set Coverage
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class ItemSetCoverage extends AnalysisOperation {

    public ItemSetCoverage(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile) {
        super(fmFile, filterFile, itemsFile);
    }

    public double calculate() throws IOException, FeatureModelParserException {
        val accessibility = new Accessibility(fmFile, filterFile, itemsFile);
        accessibility.setWriter(writer);
        accessibility.setPrintResults(false);

        HashMap<Item, Double> results = accessibility.calculate();

        // NUMERATOR
        int countAtLeastOne = 0;
        for (Item i : results.keySet()) {
            if (results.get(i) > 0) {
                countAtLeastOne++;
            }
        }

        double coverage = (double) countAtLeastOne / results.size();

        // print results
        if (printResults) {
            String message = String.format("%sNumber of items recommended at least one: %s", LoggerUtils.tab(), countAtLeastOne);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
            message = String.format("%sNumber of items: %s", LoggerUtils.tab(), results.size());
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
            message = String.format("%sItem Set Coverage: %s", LoggerUtils.tab(), coverage);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
        }

        return coverage;
    }

}
