/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.ao;

import at.tugraz.ist.ase.ao4crs.common.Utilities;
import at.tugraz.ist.ase.ao4crs.configurator.ConfiguratorAdapter;
import at.tugraz.ist.ase.ao4crs.core.AllRecommendationLists;
import at.tugraz.ist.ase.ao4crs.core.Item;
import at.tugraz.ist.ase.ao4crs.core.RecommendationList;
import at.tugraz.ist.ase.ao4crs.core.rank.IItemRankingStrategy;
import at.tugraz.ist.ase.ao4crs.core.rank.SimpleItemRankingStrategy;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Requirement;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static at.tugraz.ist.ase.ao4crs.configurator.ConfiguratorAdapterFactory.createConfigurator;

/**
 * Implementation of a Recommendation task
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class Recommendation extends AnalysisOperation {

    @Setter
    IItemRankingStrategy rankingStrategy = null;
    ConfiguratorAdapter configurator = null;

    @Builder
    public Recommendation(File fmFile, File filterFile, File itemsFile) {
        super(fmFile, filterFile, itemsFile);
    }

    public RecommendationList recommend(Requirement req) throws FeatureModelParserException, IOException {
        if (configurator == null) {
            configurator = createConfigurator(fmFile, filterFile, itemsFile, rankingStrategy.getCalculator(fmFile, filterFile, itemsFile));
        }

        configurator.findAllSolutions(req); // identify all items that satisfy the Requirement

        // reorder the items according to the recommendation strategy
        List<Item> recommendedItems;
        if (rankingStrategy != null) {
            recommendedItems = rankingStrategy.rank(configurator.getItems());
        } else {
            recommendedItems = configurator.getItems();
        }

        if (printResults) {
            Utilities.printList(recommendedItems, writer);
        }

        return new RecommendationList(recommendedItems);
    }

    public AllRecommendationLists calculateAllRecommendations() throws FeatureModelParserException, IOException {
        AllRecommendationLists all = new AllRecommendationLists();
        UserRequirement urOperation = new UserRequirement(fmFile, filterFile, itemsFile);

        // calculate all list of user requirements
        List<Requirement> userRequirements = urOperation.getConsistentUserRequirements();

        Recommendation recommendation = Recommendation.builder()
                .fmFile(fmFile)
                .filterFile(filterFile)
                .itemsFile(itemsFile)
                .build();
        recommendation.setWriter(writer);
        recommendation.setPrintResults(this.printResults);
        recommendation.setRankingStrategy(new SimpleItemRankingStrategy()); // set ranking strategy

        // calculate all list of recommendations
        for (Requirement userRequirement : userRequirements) {
            val recommendationList = recommendation.recommend(userRequirement);

            if (!recommendationList.empty()) {
                all.add(recommendationList);
            }
        }

        return all;
    }
}
