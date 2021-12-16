/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.MinCardinalityRestriction;
import org.apache.jena.rdf.model.Resource;

/**
 * An owl restriction which binds to a MinCardinalityRestriction.
 *
 * @since 0.1
 */
public class OwlMinCardinalityRestriction extends RestrictedOwlRestriction<MinCardinalityRestriction> {

   public OwlMinCardinalityRestriction(MinCardinalityRestriction restriction) {
      super(restriction);
      Resource resource = restriction.getIsDefinedBy();
      computeKey(resource);
   }
   
   /**
    * Return the min cardinality.
    *
    * @return the min cardinality
    */
   public int getMaxCardinality() {
      return restriction.getMinCardinality();
   }   
}

