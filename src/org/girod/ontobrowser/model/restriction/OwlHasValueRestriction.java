/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.rdf.model.Resource;

/**
 * An owl restriction which binds to a HasValueRestriction.
 *
 * @since 0.1
 */
public class OwlHasValueRestriction extends RestrictedOwlRestriction<HasValueRestriction> {

   public OwlHasValueRestriction(HasValueRestriction restriction) {
      super(restriction);
      Resource resource = restriction.getHasValue().asResource();
      computeKey(resource);
   }
}

