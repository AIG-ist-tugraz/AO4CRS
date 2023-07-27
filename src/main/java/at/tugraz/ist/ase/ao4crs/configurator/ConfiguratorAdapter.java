/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.configurator;

import at.tugraz.ist.ase.ao4crs.core.Item;
import at.tugraz.ist.ase.ao4crs.core.ItemAssortment;
import at.tugraz.ist.ase.ao4crs.core.mapper.IItemSolutionMapper;
import at.tugraz.ist.ase.ao4crs.model.ItemAwareConfigurationModel;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Assignment;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Requirement;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Solution;
import at.tugraz.ist.ase.hiconfit.cacdr_core.translator.ISolutionTranslatable;
import at.tugraz.ist.ase.hiconfit.configurator.Configurator;
import at.tugraz.ist.ase.hiconfit.kb.core.KB;
import lombok.Builder;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter for the Configurator class
 */
public class ConfiguratorAdapter extends Configurator {

    private final ItemAwareConfigurationModel model;
    private final ItemAssortment itemAssortment;

    protected final List<Item> items = new LinkedList<>();

    // return a copy of the list
    public List<Item> getItems() {
        return new LinkedList<>(items);
    }

    IItemSolutionMapper itemSolutionMapper;

    @Builder(builderMethodName = "configuratorAdapterBuilder")
    public ConfiguratorAdapter(@NonNull KB kb,
                               @NonNull ItemAwareConfigurationModel model,
                               ISolutionTranslatable translator,
                               @NonNull ItemAssortment itemAssortment,
                               @NonNull IItemSolutionMapper itemSolutionMapper) {
        super(kb, model, translator, null);
        this.model = model;
        this.itemAssortment = itemAssortment;
        this.itemSolutionMapper = itemSolutionMapper;
    }

    public void findAllSolutions(Requirement requirement) {
        if (requirement != null) {
            setRequirement(requirement);
        }

        // clear items
        items.clear();
        emptySolutions();

        findAllSolutions(false, 0);

        // filter solutions
        super.getSolutions().forEach(solution -> {
            Item translatedItem = itemSolutionMapper.toItem(solution, model.getNumProperties());

            itemAssortment.get(translatedItem.properties()).ifPresent(item -> {
                Item newItem = new Item(item.id(), item.properties(), translatedItem.fm_values(), item.rf(), item.rf_calculated());

                if (!contains(items, newItem)) {
                    items.add(newItem);
                }
            });
        });
    }

    @Override
    protected Solution getCurrentSolution() {
        List<Assignment> assignments = model.getPropertyVars().stream()
                .map(var -> Assignment.builder()
                        .variable(var.getName())
                        .value(var.getValue())
                        .build())
                .collect(Collectors.toCollection(LinkedList::new));

        kb.getVariableList().forEach(var -> assignments.add(Assignment.builder()
                                                .variable(var.getName())
                                                .value(var.getValue())
                                                .build()));

        return Solution.builder().assignments(assignments).build();
    }

    private boolean contains(List<Item> items, Item item) {
        return items.parallelStream().anyMatch(i -> i.id().equals(item.id()));
    }
}
