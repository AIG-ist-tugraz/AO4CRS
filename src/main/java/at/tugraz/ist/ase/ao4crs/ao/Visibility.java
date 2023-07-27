/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.ao;

import at.tugraz.ist.ase.ao4crs.common.Utilities;
import at.tugraz.ist.ase.ao4crs.core.AllRecommendationLists;
import at.tugraz.ist.ase.ao4crs.core.Item;
import at.tugraz.ist.ase.ao4crs.core.ItemsReader;
import at.tugraz.ist.ase.hiconfit.common.LoggerUtils;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Implementation of Visibility of Items
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class Visibility extends AnalysisOperation {

    AllRecommendationLists all = null;

    public Visibility(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile) {
        super(fmFile, filterFile, itemsFile);
    }

    public HashMap<Item, Double> calculate() throws IOException, FeatureModelParserException {
        calculateAllRecommendations();

        val items = ItemsReader.read(itemsFile); // don't need to calculate rf

        // calculate visibility for each item
        HashMap<Item, Double> visibilities = new HashMap<>();
        for (Item item : items) {
            val visibilityValue = calculate(item);
            visibilities.put(item, visibilityValue);
        }

        return visibilities;
    }

    public double calculate(Item item) throws FeatureModelParserException, IOException {
        Utilities.printInfo(printResults, writer, "Item", item.id());

        if (all == null) {
            calculateAllRecommendations();
        }

        // calculate visibility
        LoggerUtils.indent();
        all.setWriter(writer);
        all.setPrintResults(true);
        double visibility = all.visibility(item);

        // print results
        if (printResults) {
            String message = String.format("%sVisibility: %s", LoggerUtils.tab(), visibility);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
        }
        LoggerUtils.outdent();

        return visibility;
    }

    private void calculateAllRecommendations() throws FeatureModelParserException, IOException {
        Recommendation recommendation = new Recommendation(fmFile, filterFile, itemsFile);
        recommendation.setWriter(writer);
        recommendation.setPrintResults(false);
        all = recommendation.calculateAllRecommendations();
    }
}
