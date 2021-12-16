/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.CardinalityRestriction;
import org.apache.jena.rdf.model.Resource;

/**
 * An owl restriction which binds to a CardinalityRestriction.
 *
 * @since 0.1
 */
public class OwlCardinalityRestriction extends RestrictedOwlRestriction<CardinalityRestriction> {

   public OwlCardinalityRestriction(CardinalityRestriction restriction) {
      super(restriction);
      Resource resource = restriction.getIsDefinedBy();
      computeKey(resource);
   }
}

