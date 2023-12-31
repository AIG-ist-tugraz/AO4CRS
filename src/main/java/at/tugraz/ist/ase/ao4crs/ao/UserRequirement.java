/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.ao;

import at.tugraz.ist.ase.ao4crs.common.Utilities;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Requirement;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static at.tugraz.ist.ase.ao4crs.configurator.ConfiguratorAdapterFactory.createConfigurator;

/**
 * This class identifies all possible user requirements for a given feature model.
 *
 * @author Viet-Man Le (vietman.le@ist.tugraz.at)
 */
@Slf4j
public class UserRequirement extends AnalysisOperation {

    public UserRequirement(File fmFile, File filterFile, File itemsFile) {
        super(fmFile, filterFile, itemsFile);
    }

    public List<Requirement> getRequirements() throws FeatureModelParserException {
        val fm = Utilities.loadFeatureModel(fmFile);
        List<String> leafFeatures = Utilities.getLeafFeatures(fm);

        Integer[] indexesArr = Utilities.createIndexesArray(leafFeatures.size());
        Set<Integer> targetSet = Sets.newHashSet(indexesArr);

        List<Requirement> URs = new ArrayList<>();
        for (int card = 1; card <= leafFeatures.size(); card++) {
            Set<Set<Integer>> combinations = Sets.combinations(targetSet, card);

            for (Set<Integer> combination : combinations) {
                // generate variable_value_combinations
                List<String> var_value_combs = Utilities.generateFMValueCombinations(card, combination, leafFeatures);

                var_value_combs.forEach(var_value_comb -> {
                    Requirement requirement = Utilities.convertToRequirement(var_value_comb);
                    URs.add(requirement);
                });
            }
        }

        return URs;
    }

    public List<Requirement> getGlobalConsistentUserRequirements() throws FeatureModelParserException {
        List<Requirement> URs = getRequirements();

        // filter consistent user requirements
        val configurator = createConfigurator(fmFile);

        List<Requirement> list = new ArrayList<>();
        for (Requirement UR : URs) {
            if (configurator.isConsistent(UR)) {
                list.add(UR);
            }
        }
        return list;
    }

    public List<Requirement> getConsistentUserRequirements() throws FeatureModelParserException, IOException {
        List<Requirement> URs = getRequirements();

        // filter consistent user requirements
        val configurator = createConfigurator(fmFile, filterFile, itemsFile, null);

        List<Requirement> list = new ArrayList<>();
        for (Requirement UR : URs) {
            configurator.findAllSolutions(UR);

            if (configurator.getItems().size() > 0) {
                list.add(UR);
            }
        }
        return list;
    }

    public List<Requirement> getInconsistentUserRequirements() throws FeatureModelParserException, IOException {
        List<Requirement> URs = getRequirements();

        // filter consistent user requirements
        val configurator = createConfigurator(fmFile, filterFile, itemsFile, null);

        List<Requirement> list = new ArrayList<>();
        for (Requirement UR : URs) {
            configurator.findAllSolutions(UR);

            if (configurator.getItems().size() == 0) {
                list.add(UR);
            }
        }
        return list;
    }
}
