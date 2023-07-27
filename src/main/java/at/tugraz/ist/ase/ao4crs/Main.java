/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs;

import at.tugraz.ist.ase.ao4crs.ao.*;
import at.tugraz.ist.ase.ao4crs.cli.CmdLineOptions;
import at.tugraz.ist.ase.ao4crs.cli.ConfigManager;
import at.tugraz.ist.ase.ao4crs.common.Utilities;
import at.tugraz.ist.ase.ao4crs.core.Item;
import at.tugraz.ist.ase.ao4crs.core.rank.SimpleItemRankingStrategy;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Assignment;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Requirement;
import at.tugraz.ist.ase.hiconfit.common.LoggerUtils;
import at.tugraz.ist.ase.hiconfit.fm.parser.FeatureModelParserException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static at.tugraz.ist.ase.ao4crs.cli.ConfigManager.defaultConfigFile;
import static at.tugraz.ist.ase.ao4crs.configurator.ConfiguratorAdapterFactory.createConfigurator;

@Slf4j
public class Main {

    private static final List<String> QUERY_RESTRICTIVENESS_FILES = Arrays.asList("q1_1.csv", "q1_2.csv", "q1_3.csv");

    public static void main(String[] args) throws IOException, FeatureModelParserException {
        String programTitle = "Analysis Operations On The Run";
        String usage = "Usage: java -jar ao4fma.jar [options]]";

        // Parse command line arguments
        CmdLineOptions cmdLineOptions = new CmdLineOptions(null, programTitle, null, usage);
        cmdLineOptions.parseArgument(args);

        if (cmdLineOptions.isHelp()) {
            cmdLineOptions.printUsage();
            System.exit(0);
        }

        cmdLineOptions.printWelcome();

        String confFile = cmdLineOptions.getConfFile() == null ? defaultConfigFile : cmdLineOptions.getConfFile();
        ConfigManager cfg = ConfigManager.getInstance(confFile);

        val fmFile = cfg.getFmFile();
        val filterFile = cfg.getFilterFile();
        val itemsFile = cfg.getItemsFile();
        val queries_folder = cfg.getQueries_folder();
        val resultFile = cfg.getResultFile();

        @Cleanup val writer = new BufferedWriter(new FileWriter(resultFile));
        LoggerUtils.setUseThreadInfo(false);

        // Restrictiveness
        restrictiveness(fmFile, filterFile, itemsFile, writer, queries_folder);

        // Restrictiveness for all features
        restrictivenessAllLeafFeatures(fmFile, filterFile, itemsFile, writer);

        // Check False Optional
        checkFalseOptional(fmFile, filterFile, itemsFile, writer, queries_folder);

        // Accessibility
        accessibility(fmFile, filterFile, itemsFile, writer);

        // Product Catalog Coverage
        itemSetCoverage(fmFile, filterFile, itemsFile, writer);

        // Visibility of Items
        visibilityOfItems(fmFile, filterFile, itemsFile, writer);

        // Controversy of Features
        controversyOfFeatures(fmFile, filterFile, itemsFile, writer);

        // Global controversy
        globalControversy(fmFile, filterFile, itemsFile, writer);
    }

    private static void restrictiveness(File fmFile,
                                        File filterFile,
                                        File itemsFile,
                                        BufferedWriter writer,
                                        String queries_folder) throws IOException, FeatureModelParserException {
        String message = String.format("%sI. RESTRICTIVENESS:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val restrictiveness = new Restrictiveness(fmFile, filterFile, itemsFile);
        restrictiveness.setWriter(writer);

        LoggerUtils.indent();
        for (String queryFile : QUERY_RESTRICTIVENESS_FILES) {
            String query = queries_folder + queryFile;
            val req = Utilities.readRequirement(query);

            double restrict_value = restrictiveness.calculate(req);
        }
        LoggerUtils.outdent();
    }

    private static void restrictivenessAllLeafFeatures(File fmFile,
                                                       File filterFile,
                                                       File itemsFile,
                                                       BufferedWriter writer) throws IOException, FeatureModelParserException {
        String message = String.format("%sI.1. RESTRICTIVENESS - ALL LEAF FEATURES:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val restrictiveness = new Restrictiveness(fmFile, filterFile, itemsFile);
        restrictiveness.setWriter(writer);

        LoggerUtils.indent();
        LinkedHashMap<String, Double> results = restrictiveness.calculate();
        LoggerUtils.outdent();
    }

    private static void checkFalseOptional(File fmFile,
                                           File filterFile,
                                           File itemsFile,
                                           BufferedWriter writer,
                                           String queries_folder) throws IOException, FeatureModelParserException {
        String message = String.format("%sI.2. CHECK FALSE OPTIONAL:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val restrictiveness = new Restrictiveness(fmFile, filterFile, itemsFile);
        restrictiveness.setWriter(writer);

        LoggerUtils.indent();
        val fm = Utilities.loadFeatureModel(fmFile);

        for (String f1 : Utilities.getLeafFeatures(fm)) {
            for (String f2 : Utilities.getLeafFeatures(fm)) {
                if (f1.equals(f2)) continue;

                val req = Requirement.requirementBuilder()
                        .assignments(List.of(Assignment.builder()
                                        .variable(f1)
                                        .value("false")
                                        .build(),
                                Assignment.builder()
                                        .variable(f2)
                                        .value("true")
                                        .build()))
                        .build();

                val restrict_value = restrictiveness.calculate(req);
            }
        }
        LoggerUtils.outdent();
    }

    private static void accessibility(File fmFile,
                                      File filterFile,
                                      File itemsFile,
                                      BufferedWriter writer) throws IOException, FeatureModelParserException {
        String message = String.format("%sII. ACCESSIBILITY:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val accessibility = new Accessibility(fmFile, filterFile, itemsFile);
        accessibility.setWriter(writer);

        LoggerUtils.indent();
        HashMap<Item, Double> results = accessibility.calculate();
        LoggerUtils.outdent();
    }

    private static void itemSetCoverage(File fmFile,
                                        File filterFile,
                                        File itemsFile,
                                        BufferedWriter writer) throws IOException, FeatureModelParserException {
        String message = String.format("%sIII. ITEM SET COVERAGE:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val coverage = new ItemSetCoverage(fmFile, filterFile, itemsFile);
        coverage.setWriter(writer);

        LoggerUtils.indent();
        double results = coverage.calculate();
        LoggerUtils.outdent();
    }

    private static void visibilityOfItems(File fmFile,
                                          File filterFile,
                                          File itemsFile,
                                          BufferedWriter writer) throws IOException, FeatureModelParserException {
        String message = String.format("%sIV. VISIBILITY OF ITEMS:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val visibility = new Visibility(fmFile, filterFile, itemsFile);
        visibility.setWriter(writer);

        LoggerUtils.indent();
        HashMap<Item, Double> results = visibility.calculate();
        LoggerUtils.outdent();
    }

    private static void controversyOfFeatures(File fmFile,
                                              File filterFile,
                                              File itemsFile,
                                              BufferedWriter writer) throws IOException, FeatureModelParserException {

        String message = String.format("%sV. CONTROVERSY OF FEATURES:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val controversy = new Controversy(fmFile, filterFile, itemsFile);
        controversy.setWriter(writer);

        LoggerUtils.indent();
        HashMap<String, Double> results = controversy.calculate();
        LoggerUtils.outdent();
    }

    private static void globalControversy(File fmFile,
                                          File filterFile,
                                          File itemsFile,
                                          BufferedWriter writer) throws IOException, FeatureModelParserException {
        String message = String.format("%sVI. GLOBAL CONTROVERSY:", LoggerUtils.tab());
        log.info(message);
        writer.write(message); writer.newLine();

        // create the operation
        val controversy = new GlobalControversy(fmFile, filterFile, itemsFile);
        controversy.setWriter(writer);

        LoggerUtils.indent();
        double results = controversy.calculate();
        LoggerUtils.outdent();
    }

    public static void findProducts(File fmFile,
                                    BufferedWriter writer) throws IOException, FeatureModelParserException {
        // load the feature model
        val configurator = createConfigurator(fmFile);

        configurator.findAllSolutions(false,0);

        // map the solutions to items
        val items = configurator.getSolutions().stream().map(solution -> new Item("0",null, solution, 0, false)).collect(Collectors.toList());

        Utilities.printList(items, writer);
    }

    public static void findProducts(File fmFile,
                                    File filterFile,
                                    File itemsFile,
                                    BufferedWriter writer) throws IOException, FeatureModelParserException {
        Recommendation recommendation = Recommendation.builder()
                .fmFile(fmFile)
                .filterFile(filterFile)
                .itemsFile(itemsFile)
                .build();
        recommendation.setWriter(writer);
        recommendation.setRankingStrategy(new SimpleItemRankingStrategy()); // set ranking strategy
        val recommendationList = recommendation.recommend(null);
    }
}