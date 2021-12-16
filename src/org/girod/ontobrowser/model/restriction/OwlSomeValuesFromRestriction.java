/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.SomeValuesFromRestriction;
import org.apache.jena.rdf.model.Resource;

/**
 * An owl restriction which binds to a SomeValuesFromRestriction.
 *
 * @since 0.1
 */
public class OwlSomeValuesFromRestriction extends RestrictedOwlRestriction<SomeValuesFromRestriction> {

   public OwlSomeValuesFromRestriction(SomeValuesFromRestriction restriction) {
      super(restriction);
      Resource resource = restriction.getSomeValuesFrom();
      computeKey(resource);
   }
}

