/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.MaxCardinalityRestriction;
import org.apache.jena.rdf.model.Resource;

/**
 * An owl restriction which binds to a MaxCardinalityRestriction.
 *
 * @since 0.1
 */
public class OwlMaxCardinalityRestriction extends RestrictedOwlRestriction<MaxCardinalityRestriction> {

   public OwlMaxCardinalityRestriction(MaxCardinalityRestriction restriction) {
      super(restriction);
      Resource resource = restriction.getIsDefinedBy();
      computeKey(resource);
   }

   /**
    * Return the max cardinality.
    *
    * @return the max cardinality
    */
   public int getMaxCardinality() {
      return restriction.getMaxCardinality();
   }
}
