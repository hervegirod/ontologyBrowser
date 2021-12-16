/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.girod.ontobrowser.model.restriction;

import org.apache.jena.ontology.Restriction;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL2;

/**
 * An owl restriction which binds to a qualified cardinality restriction.
 *
 * @since 0.1
 */
public class OwlQualifiedCardinalityRestriction extends RestrictedOwlRestriction<Restriction> {
   private final int cardinality;

   public OwlQualifiedCardinalityRestriction(Restriction restriction, Resource resource) {
      super(restriction);
      computeKey(resource);
      
      RDFNode node = restriction.getPropertyValue(OWL2.qualifiedCardinality);
      Literal literal = node.asLiteral();
      cardinality = literal.getInt();      
   }
   
   /**
    * Return the cardinality.
    *
    * @return the cardinality
    */
   public int getCardinality() {
      return cardinality;
   }     
}

