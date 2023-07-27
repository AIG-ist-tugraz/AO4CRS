/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.configurator;

import at.tugraz.ist.ase.ao4crs.common.Utilities;
import at.tugraz.ist.ase.ao4crs.core.ItemsReader;
import at.tugraz.ist.ase.ao4crs.core.mapper.ItemSolutionMapperImpl;
import at.tugraz.ist.ase.ao4crs.core.rank.IItemRankCalculatable;
import at.tugraz.ist.ase.ao4crs.model.ItemAwareConfigurationModel;
import at.tugraz.ist.ase.ao4crs.model.translator.MZN2ChocoTranslator;
import at.tugraz.ist.ase.hiconfit.cacdr_core.translator.fm.FMSolutionTranslator;
import at.tugraz.ist.ase.hiconfit.configurator.ConfigurationModel;
import at.tugraz.ist.ase.hiconfit.configurator.Configurator;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import at.tugraz.ist.ase.hiconfit.kb.fm.FMKB;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class ConfiguratorAdapterFactory {

    public static ConfiguratorAdapter createConfigurator(@NonNull File fmFile, @NonNull File filterFile, @NonNull File itemsFile, IItemRankCalculatable calculator) throws FeatureModelParserException, IOException {
        // load the feature model
        val fm = Utilities.loadFeatureModel(fmFile);
        // read items
        var items = ItemsReader.read(itemsFile); // need to calculate rf

        if (calculator != null) {
            items = calculator.calculate(items);
        }

        // create knowledge base
        val kb = new FMKB<>(fm, false);
        val translator = new MZN2ChocoTranslator(); // a translator for filter constraints
        // model for the configuration task
        val itemAwareConfigurationModel = ItemAwareConfigurationModel.builder()
                .kb(kb)
                .rootConstraints(true)
                .filterFile(filterFile)
                .translator(translator)
                .build();
        itemAwareConfigurationModel.initialize();

        // create and return configurator
        return ConfiguratorAdapter.configuratorAdapterBuilder()
                .kb(kb)
                .model(itemAwareConfigurationModel)
                .translator(new FMSolutionTranslator())
                .itemAssortment(items)
                .itemSolutionMapper(new ItemSolutionMapperImpl())
                .build();
    }

    public static Configurator createConfigurator(File fmFile) throws FeatureModelParserException {
        // load the feature model
        val fm = Utilities.loadFeatureModel(fmFile);

        // convert the feature model into FMKB
        val kb = new FMKB<>(fm, false);

        val configurationModel = new ConfigurationModel(kb, true);
        configurationModel.initialize();
        return Configurator.builder()
                .kb(kb)
                .configurationModel(configurationModel)
                .translator(new FMSolutionTranslator())
                .build();
    }
}
