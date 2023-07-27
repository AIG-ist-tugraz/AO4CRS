/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core;

import at.tugraz.ist.ase.hiconfit.cacdr_core.Assignment;
import at.tugraz.ist.ase.hiconfit.cacdr_core.Solution;
import at.tugraz.ist.ase.hiconfit.common.LoggerUtils;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Reads the items from a file
 */
@Slf4j
@UtilityClass
public class ItemsReader {

    public ItemAssortment read(@NonNull File file) throws IOException {
        log.debug("{}Reading the item file - {} >>>", LoggerUtils.tab(), file.getName());

        @Cleanup BufferedReader reader = Files.newReader(file, Charsets.UTF_8);

        val items = new ItemAssortment();

        String line = reader.readLine();
        while (line != null) {
            String[] tokens = line.split(",");

            // first token is the item id
            String[] parts = tokens[0].split("=");
            val id = parts[1];

            List<Assignment> assignments = new LinkedList<>();
            // the rest of the tokens are the assignments
            for (int i = 1; i < tokens.length; i++) {
                String token = tokens[i];
                parts = token.split("=");

                val variable = parts[0];
                val value = parts[1];

                val ur = Assignment.builder().variable(variable).value(value).build();

                assignments.add(ur);
            }

            val item = Item.builder()
                    .id(id)
                    .properties(Solution.builder().assignments(assignments).build())
                    .fm_values(null)
                    .rf(0)
                    .rf_calculated(false)
                    .build();
            items.add(item);

            line = reader.readLine();
        }

        return items;
    }
}
