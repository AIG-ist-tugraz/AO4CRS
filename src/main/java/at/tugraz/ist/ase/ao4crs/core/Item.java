/*
 * Analysis Operations for Constraint-based Recommender Systems
 *
 * Copyright (c) 2023 AIG team, Institute for Software Technology, Graz University of Technology, Austria
 *
 * Contact: http://ase.ist.tugraz.at/ASE/
 */

package at.tugraz.ist.ase.ao4crs.core;

import at.tugraz.ist.ase.hiconfit.cacdr_core.Solution;
import com.google.common.base.Objects;
import lombok.Builder;

/**
 * An item
 * @param id the id of the item
 * @param properties technical properties of the item
 * @param fm_values user requirements/feature values based on that the item was identified
 * @param rf the result of ranking function
 */
@Builder
public record Item(String id, Solution properties, Solution fm_values, int rf, boolean rf_calculated) {
    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", properties=[" + properties + "]" +
                ", fm_values=[" + fm_values + "]" +
                ", rf=" + rf +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;
        return Objects.equal(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
