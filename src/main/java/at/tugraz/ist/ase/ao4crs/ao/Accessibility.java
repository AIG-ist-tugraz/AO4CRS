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
 * Implementation of Accessibility of Items
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class Accessibility extends AnalysisOperation {

    AllRecommendationLists all = null;

    public Accessibility(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile) {
        super(fmFile, filterFile, itemsFile);
    }

    public HashMap<Item, Double> calculate() throws IOException, FeatureModelParserException {
        calculateAllRecommendations();

        val items = ItemsReader.read(itemsFile);

        // calculate accessibility for each item
        HashMap<Item, Double> accessibility = new HashMap<>();
        for (Item item : items) {
            val accessibilityValue = calculate(item);
            accessibility.put(item, accessibilityValue);
        }

        return accessibility;
    }

    public double calculate(Item item) throws FeatureModelParserException, IOException {
        Utilities.printInfo(printResults, writer, "Item", item.id());

        if (all == null) {
            calculateAllRecommendations();
        }

        // calculate accessibility
        int occurrences = all.countOccurrence(item);
        double accessibility = (double) occurrences / all.size();

        // print results
        if (printResults) {
            LoggerUtils.indent();
            String message = String.format("%sOccurrences: %s", LoggerUtils.tab(), occurrences);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
            message = String.format("%sTotal recommendations: %s", LoggerUtils.tab(), all.size());
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
            message = String.format("%sAccessibility: %s", LoggerUtils.tab(), accessibility);
            log.info(message);
            if (writer != null) {
                writer.write(message);
                writer.newLine();
            }
            LoggerUtils.outdent();
        }

        return accessibility;
    }

    private void calculateAllRecommendations() throws FeatureModelParserException, IOException {
        Recommendation recommendation = new Recommendation(fmFile, filterFile, itemsFile);
        recommendation.setWriter(writer);
        recommendation.setPrintResults(false);
        all = recommendation.calculateAllRecommendations();
    }
}
