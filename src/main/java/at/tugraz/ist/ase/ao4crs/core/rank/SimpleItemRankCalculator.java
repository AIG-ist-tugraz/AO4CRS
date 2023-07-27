/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core.rank;

import at.tugraz.ist.ase.ao4crs.common.Utilities;
import at.tugraz.ist.ase.ao4crs.configurator.ConfiguratorAdapter;
import at.tugraz.ist.ase.ao4crs.core.Item;
import at.tugraz.ist.ase.ao4crs.core.ItemAssortment;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Assignment;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Requirement;
import at.tugraz.ist.ase.hiconfit.fm.core.AbstractRelationship;
import at.tugraz.ist.ase.hiconfit.fm.core.CTConstraint;
import at.tugraz.ist.ase.hiconfit.fm.core.Feature;
import at.tugraz.ist.ase.hiconfit.fm.core.FeatureModel;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import lombok.NonNull;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static at.tugraz.ist.ase.ao4crs.configurator.ConfiguratorAdapterFactory.createConfigurator;

public class SimpleItemRankCalculator implements IItemRankCalculatable {

    File fmFile;
    File filterFile;
    File itemsFile;

    FeatureModel<Feature, AbstractRelationship<Feature>, CTConstraint> fm;
    ConfiguratorAdapter configurator;

    public SimpleItemRankCalculator(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile) {
        this.fmFile = fmFile;
        this.filterFile = filterFile;
        this.itemsFile = itemsFile;
    }

    public ItemAssortment calculate(ItemAssortment items) throws FeatureModelParserException, IOException {
        fm = Utilities.loadFeatureModel(fmFile);
        configurator = createConfigurator(fmFile, filterFile, itemsFile, null);

        // loop over all items
        val new_items = new ItemAssortment();
        for (Item p: items) {
            val rf = calculate(p);
            val item = Item.builder()
                    .id(p.id())
                    .properties(p.properties())
                    .fm_values(p.fm_values())
                    .rf(rf)
                    .rf_calculated(true)
                    .build();
            new_items.add(item);
        }
        return new_items;
    }

    private int calculate(Item item) {
        // loop over all features
        AtomicInteger rf = new AtomicInteger();
        for (Feature f: fm.getBfFeatures()) {
            val req = Requirement.requirementBuilder()
                    .assignments(List.of(Assignment.builder()
                            .variable(f.getName())
                            .value("true")
                            .build()))
                    .build();

            configurator.findAllSolutions(req);

            if (configurator.getItems().parallelStream().anyMatch(i -> i.equals(item))) {
                rf.getAndIncrement();
            }
        }
        return rf.get();
    }
}
